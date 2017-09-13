package com.mutant.godutch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.mutant.godutch.model.Friend
import com.mutant.godutch.model.Group
import com.mutant.godutch.utils.Utility.Companion.uploadImage
import kotlinx.android.synthetic.main.activity_new_group.*
import kotlinx.android.synthetic.main.list_view_item_friend_tick.view.*
import java.util.*


class NewGroupActivity : BaseActivity() {

    private var mFirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    internal var mStorage: StorageReference? = null

    private var isTakePhoto: Boolean = false

    override val layoutId: Int
        get() = R.layout.activity_new_group

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_new_group, menu)//将toolbar中的菜单添加上来
        return true
    }

    override fun setup() {
        title = ""
        setupMenu()
        setupFireBase()
    }

    private fun setupMenu() {
        mToolbar?.inflateMenu(R.menu.menu_new_group)
        mToolbar?.setOnMenuItemClickListener { p0 ->
            when (p0?.itemId) {
                R.id.action_take_a_photo -> takeAPhoto()
                R.id.action_done -> uploadPhotoBeforeCreateNewGroup()
            }
            true
        }

    }

    private fun setupFireBase() {
        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        if (mFirebaseUser != null) {
            mDatabase.child("friends").child(mFirebaseUser!!.uid).orderByChild("uid").addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val friends = ArrayList<Friend>()
                    val iterator = dataSnapshot.children.iterator()
                    while (iterator.hasNext()) {
                        friends.add((iterator.next() as DataSnapshot).getValue(Friend::class.java))
                    }
                    friends.add(0, me)
                    setupRecycleView(friends)
                }

                private fun setupRecycleView(friends: List<Friend>) {
                    recycler_view_friends_shared.layoutManager = LinearLayoutManager(this@NewGroupActivity)
                    recycler_view_friends_shared.adapter = RecycleViewAdapterFriends(this@NewGroupActivity, friends)
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }

    private fun takeAPhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data?.extras
            val imageBitmap = extras?.get("data") as Bitmap
            imageView_photo.setImageBitmap(imageBitmap)
            isTakePhoto = true
        }
    }

    @Synchronized fun uploadPhotoBeforeCreateNewGroup() {
        if (isTakePhoto) {
            val bitmap = (imageView_photo.drawable as BitmapDrawable).bitmap
            val filePath = mFirebaseUser!!.uid + "/" + System.currentTimeMillis() + ".png"
            uploadImage(filePath, bitmap, OnFailureListener { exception ->
                exception.printStackTrace()
                Snackbar.make(coordinatorLayout_parent_new_group, R.string.upload_image_failed, Snackbar.LENGTH_LONG).show()
            }, OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                Snackbar.make(coordinatorLayout_parent_new_group, R.string.upload_image_successfully, Snackbar.LENGTH_LONG).show()
                createNewGroup(taskSnapshot?.downloadUrl)
            })
        } else {
            createNewGroup(null)
        }
    }

    private fun createNewGroup(imageDownloadUrl: Uri?) {
        val title = editText_title.text.toString()
        val friendsChecked = (recycler_view_friends_shared.adapter as RecycleViewAdapterFriends).mFriendsChecked
        val group = Group(title, imageDownloadUrl?.toString() ?: "", 0, friendsChecked)
        if (mFirebaseUser != null) {
            val databaseReference = mDatabase.child("groups").push()
            databaseReference.setValue(group).addOnSuccessListener {
                saveUserUidMappingToGroups(friendsChecked, databaseReference.key)
                finish()
            }
        } else {
            try {
                Crashlytics.logException(NullPointerException())
            } catch (e: IllegalThreadStateException) {
                e.printStackTrace()
            }
        }
    }

    private fun saveUserUidMappingToGroups(friendsUid: List<String>, groupKey: String) {
        for (uid in friendsUid) {
            mDatabase.child("groups_mapping").child(uid).push().setValue(groupKey)
        }
    }

    inner class RecycleViewAdapterFriends(internal var context: Context, private var friends: List<Friend>) : RecyclerView.Adapter<ViewHolder>() {

        var mFriendsChecked: MutableList<String> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_view_item_friend_tick, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val friend = friends[position]
            Glide.with(context).load(friend.photoUrl).error(R.drawable.profile_pic).into(holder.mImageViewPhotoUrl)
            holder.mTextViewName.text = friend.name

            holder.itemView.setOnClickListener({
                holder.mCheckBox.isChecked = !holder.mCheckBox.isChecked
                if (holder.mCheckBox.isChecked) {
                    mFriendsChecked.add(friend.uid)
                } else {
                    mFriendsChecked.remove(friend.uid)
                }
            })
        }

        override fun getItemCount(): Int {
            return friends.size
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageViewPhotoUrl: AppCompatImageView = itemView.imageView_photo_url
        val mTextViewName: AppCompatTextView = itemView.textView_name
        val mTextViewDebt: AppCompatTextView = itemView.textView_debt
        val mCheckBox: AppCompatCheckBox = itemView.checkBox
    }

    companion object {

        fun getIntent(activity: Activity): Intent {
            val intent = Intent(activity, NewGroupActivity::class.java)
            return intent
        }

        internal val REQUEST_IMAGE_CAPTURE = 1
    }
}
