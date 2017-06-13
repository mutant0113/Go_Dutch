package com.mutant.godutch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.mutant.godutch.model.Friend
import com.mutant.godutch.model.Group
import kotlinx.android.synthetic.main.activity_new_group.*
import kotlinx.android.synthetic.main.card_view_item_friend.*
import java.io.ByteArrayOutputStream
import java.util.*

class NewGroupActivity : BaseActivity() {

    internal var mAdapterFriends: RecycleViewAdapterFriends? = null
    internal var mFirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    internal var mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    internal var mStorage: StorageReference? = null

    override val layoutId: Int
        get() = R.layout.activity_new_group

    override fun findViews() {
    }

    override fun setup() {
        setupFriends()
        setupFireBase()
    }

    private fun setupFriends() {
        recycler_view_friends_shared.layoutManager = GridLayoutManager(this, 2)
    }

    private fun setupFireBase() {
        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        if (mFirebaseUser != null) {
            val filePath = mFirebaseUser!!.uid + "/" + System.currentTimeMillis() + ".png"
            mStorage = FirebaseStorage.getInstance().reference.child(filePath)
            mDatabase.child("friends").child(mFirebaseUser!!.uid).orderByChild("name").addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val friends = ArrayList<Friend>()
                    val iterator = dataSnapshot.children.iterator()
                    while (iterator.hasNext()) {
                        friends.add((iterator.next() as DataSnapshot).getValue(Friend::class.java))
                    }
                    mAdapterFriends = RecycleViewAdapterFriends(this@NewGroupActivity, friends)
                    recycler_view_friends_shared.adapter = mAdapterFriends
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }

    fun onClickTakeAPhoto(view: View) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data.extras
            val imageBitmap = extras.get("data") as Bitmap
            imageView_photo.setImageBitmap(imageBitmap)
        }
    }

    private fun uploadImage(bitmap: Bitmap, onFailureListener: OnFailureListener, onSuccessListener: OnSuccessListener<UploadTask.TaskSnapshot>) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = mStorage?.putBytes(data)
        if(uploadTask != null) {
            uploadTask.addOnFailureListener(onFailureListener).addOnSuccessListener(onSuccessListener)
        }
    }

    fun onClickCreateNewGroup(view: View) {
        val bitmap = (imageView_photo.drawable as BitmapDrawable).bitmap
        uploadImage(bitmap, OnFailureListener { exception ->
            exception.printStackTrace()
            Snackbar.make(coordinatorLayout_parent, R.string.upload_image_failed, Snackbar.LENGTH_LONG).show()
        }, OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
            Snackbar.make(coordinatorLayout_parent, R.string.upload_image_successfully, Snackbar.LENGTH_LONG).show()
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            createNewGroup(taskSnapshot?.downloadUrl)
        })
    }

    private fun createNewGroup(imageDownloadUrl: Uri?) {
        val title = editText_title.text.toString()
        val description = editText_description.text.toString()
        val friendsFilterBySelected = (recycler_view_friends_shared.adapter as RecycleViewAdapterFriends).friendsFilterBySelected
        val group = Group(title, description, imageDownloadUrl?.toString() ?: "", 0, friendsFilterBySelected)
        if (mFirebaseUser != null) {
            val userUid = mFirebaseUser!!.uid
            val databaseReference = mDatabase.child("groups").child(userUid).push()
            group.id = databaseReference.key
            databaseReference.setValue(group).addOnSuccessListener { finish() }
        } else {
            try {
                Crashlytics.logException(NullPointerException())
            } catch (e: IllegalThreadStateException) {
                e.printStackTrace()
            }

        }
    }

    inner class RecycleViewAdapterFriends(internal var context: Context, internal var friends: List<Friend>) : RecyclerView.Adapter<ViewHolder>() {
        internal var isSelected: BooleanArray

        init {
            this.isSelected = BooleanArray(friends.size)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_item_friend, parent, false)
            // TODO judge isclicked
            val holder = ViewHolder(view)
            return holder
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val friend = friends[position]
            Glide.with(context).load(friend.proPicUrl).error(R.drawable.profile_pic).into(holder.mImageViewProPic)
            holder.mTextViewName.text = friend.name
            val itemView = holder.itemView
            itemView.setOnClickListener {
                if (isSelected[position]) {
                    // TODO setbackground
                    itemView.setBackgroundColor(Color.WHITE)
                    isSelected[position] = false
                } else {
                    // TODO setbackground
                    itemView.setBackgroundColor(Color.YELLOW)
                    isSelected[position] = true
                }
            }
            holder.mTextViewInvitationState.visibility = View.GONE
        }

        override fun getItemCount(): Int {
            return friends.size
        }

        val friendsFilterBySelected: List<Friend>
            get() {
                val friendsFliterBySelected = ArrayList<Friend>()
                for (i in friends.indices) {
                    if (isSelected[i]) {
                        friendsFliterBySelected.add(friends[i])
                    }
                }
                return friendsFliterBySelected
            }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mImageViewProPic: AppCompatImageView = imageView_pro_pic
        var mTextViewName: AppCompatTextView = textView_name
        var mTextViewInvitationState: AppCompatTextView = textView_invitation_state

    }

    companion object {

        fun getIntent(activity: Activity): Intent {
            val intent = Intent(activity, NewGroupActivity::class.java)
            return intent
        }

        internal val REQUEST_IMAGE_CAPTURE = 1
    }
}