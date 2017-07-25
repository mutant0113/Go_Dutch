package com.mutant.godutch

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.UploadTask
import com.mutant.godutch.model.Event
import com.mutant.godutch.model.Friend
import com.mutant.godutch.utils.NotificationHelper
import com.mutant.godutch.utils.Utility
import kotlinx.android.synthetic.main.activity_new_event.*
import kotlinx.android.synthetic.main.card_view_item_friend.view.*
import java.util.*

class NewEventActivity : BaseActivity() {

    internal var mGroupId: String = ""
    internal var mGroupName: String = ""
    internal var mType: TYPE = TYPE.FOOD
    internal var mAdapterFriendsShared: RecycleViewAdapterFriendsShared = RecycleViewAdapterFriendsShared(this@NewEventActivity, 0, arrayListOf())
    internal var mFirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    internal var mDatabaseFriends: DatabaseReference = FirebaseDatabase.getInstance().reference.child("friends").child(mFirebaseUser?.uid)

    var isTakePhoto: Boolean = false

    companion object {

        val BUNDLE_KEY_GROUP_ID = "BUNDLE_KEY_GROUP_ID"
        val BUNDLE_KEY_GROUP_NAME = "BUNDLE_KEY_GROUP_NAME"

        enum class TYPE {
            FOOD,
            SHOPPING,
            HOTEL,
            TICKET
        }

        val BUNDLE_KEY_TYPE = "BUNDLE_KEY_TYPE"

        fun getIntent(activity: Activity, groupId: String, groupName: String, type: TYPE): Intent {
            val intent = Intent(activity, NewEventActivity::class.java)
            intent.putExtra(BUNDLE_KEY_GROUP_ID, groupId)
            intent.putExtra(BUNDLE_KEY_GROUP_NAME, groupName)
            intent.putExtra(BUNDLE_KEY_TYPE, type)
            return intent
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_new_event

    fun onClickTakeAPhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, NewGroupActivity.REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == NewGroupActivity.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data?.extras
            val imageBitmap = extras?.get("data") as Bitmap
            imageView_photo.setImageBitmap(imageBitmap)
            isTakePhoto = true
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setup() {
        mGroupId = intent.getStringExtra(BUNDLE_KEY_GROUP_ID)
        mGroupName = intent.getStringExtra(EventsActivity.BUNDLE_KEY_GROUP_NAME)
        mType = intent.getSerializableExtra(BUNDLE_KEY_TYPE) as TYPE
        setupPhotoOnClickListener()
        setupFabType()
        setupFireBase()
        setupFriendsShard()
        setupButtonTax10Listener()
        setupSubtotalTextChangedListener()
        setupTotalTextChangedListener()
    }

    fun setupPhotoOnClickListener() {
        imageView_photo.setOnClickListener { onClickTakeAPhoto() }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupFabType() {
        when (mType) {
            TYPE.FOOD -> {
                fab_event_type.setImageDrawable(getDrawable(R.drawable.food_fork_drink))
                imageView_photo.setImageResource(R.drawable.food_default_640)
            }
            TYPE.SHOPPING -> {
                fab_event_type.setImageDrawable(getDrawable(R.drawable.ic_shopping_cart_white_24dp))
                imageView_photo.setImageResource(R.drawable.shopping_default_640)
            }
            TYPE.HOTEL -> {
                fab_event_type.setImageDrawable(getDrawable(R.drawable.ic_local_hotel_white_24dp))
                imageView_photo.setImageResource(R.drawable.hotel_default_640)
            }
            TYPE.TICKET -> {
                fab_event_type.setImageDrawable(getDrawable(R.drawable.ticket))
                imageView_photo.setImageResource(R.drawable.ticket_default_640)
            }
        }
    }

    private fun setupButtonTax10Listener() {
        slider_tax.setOnValueChangedListener {
            val subtotalText = editText_subtotal.text.toString()
            if (!TextUtils.isEmpty(subtotalText)) {
                val subtotal = Integer.valueOf(subtotalText)!!
                val tax = Math.round(subtotal * (it.toDouble() / 100))
                editText_tax.setText(tax.toString())
                editText_total.setText((subtotal + tax).toString())
            }
        }
    }

    private fun setupSubtotalTextChangedListener() {
        editText_subtotal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                slider_tax.value = 0
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun setupTotalTextChangedListener() {
        editText_total.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    mAdapterFriendsShared?.modifyTotal(if (TextUtils.isEmpty(s)) 0 else Integer.parseInt(s.toString()))
                } catch (e: NumberFormatException) {
                    try {
                        Crashlytics.logException(e)
                    } catch (ie: IllegalThreadStateException) {
                        ie.printStackTrace()
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun setupFriendsShard() {
        recycler_view_friends_shared.layoutManager = GridLayoutManager(this, 2)
    }

    private fun setupFireBase() {
        if (mFirebaseUser != null) {
            mDatabaseFriends.orderByChild("name").addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val friends = ArrayList<Friend>()
                    val iterator = dataSnapshot.children.iterator()
                    while (iterator.hasNext()) {
                        friends.add((iterator.next() as DataSnapshot).getValue(Friend::class.java))
                    }
                    // TODO 如果付錢的不是自己的CASE，先假設都是自己付錢
                    friends.add(0, meInFriend)
                    mAdapterFriendsShared = RecycleViewAdapterFriendsShared(this@NewEventActivity, 0, friends)
                    recycler_view_friends_shared.adapter = mAdapterFriendsShared
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    fun onClickCreateNewEvent(view: View) {
        if (isTakePhoto) {
            val bitmap = (imageView_photo.drawable as BitmapDrawable).bitmap
            val filePath = mFirebaseUser!!.uid + "/" + System.currentTimeMillis() + ".png"
            Utility.uploadImage(filePath, bitmap, OnFailureListener { exception ->
                exception.printStackTrace()
                Snackbar.make(coordinatorLayout_parent, R.string.upload_image_failed, Snackbar.LENGTH_LONG).show()
            }, OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                Snackbar.make(coordinatorLayout_parent, R.string.upload_image_successfully, Snackbar.LENGTH_LONG).show()
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                createNewEvent(taskSnapshot?.downloadUrl)
            })
        } else {
            createNewEvent(null)
        }
    }

    fun createNewEvent(imageDownloadUrl: Uri?) {
        val title = editText_title.text.toString()
        val description = editText_description.text.toString()
        val subtotal = Integer.parseInt(editText_subtotal.text.toString())
        val tax = Integer.parseInt(editText_tax.text.toString())
        val total = Integer.parseInt(editText_total.text.toString())
        val friendsShared = (recycler_view_friends_shared.adapter as RecycleViewAdapterFriendsShared).friendsFilterBySelected
        val friendWhoPaidFirst = mAdapterFriendsShared?.friendWhoPaidFirst
        val event = Event(imageDownloadUrl?.toString() ?: "", mType, title, description, subtotal, tax, total, friendsShared, friendWhoPaidFirst)
        val databaseReference = FirebaseDatabase.getInstance().reference.child("events").child(mGroupId).push()
        databaseReference.setValue(event)
        databaseReference.setValue(ServerValue.TIMESTAMP).addOnSuccessListener {
            val notiyTitle = getString(R.string.notify_new_event_title, mFirebaseUser?.displayName, mGroupName)
            val notifyContent = getString(R.string.notify_new_event_content, title)
            sendNewEventNotificationToFriends(friendsShared[1].uid, notiyTitle, notifyContent)
            finish()
        }
    }

    fun sendNewEventNotificationToFriends(userUid: String, title: String, content: String) {
        FirebaseDatabase.getInstance().reference.child("users").child(userUid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val fcmToken = dataSnapshot.child("fcmToken").value as? String
                    // TODO title, content, icon
                    if (!TextUtils.isEmpty(fcmToken)) {
                        NotificationHelper.sendNotificationToUser(fcmToken!!, title, content)
                    }
                } else {
                    Snackbar.make(coordinatorLayout_parent, "此ID不存在", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    inner class RecycleViewAdapterFriendsShared(internal var context: Context, internal var total: Int, internal var friends: List<Friend>) : RecyclerView.Adapter<ViewHolderShared>() {
        internal var isSelected: BooleanArray
        var friendWhoPaidFirst = Friend()
            internal set

        init {
            this.isSelected = BooleanArray(friends.size)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderShared {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_item_friend, parent, false)
            // TODO judge isclicked
            val holder = ViewHolderShared(view)
            return holder
        }

        override fun onBindViewHolder(holder: ViewHolderShared, position: Int) {
            val friend = friends[position]
            Glide.with(context).load(friend.photoUrl).error(R.drawable.profile_pic).into(holder.mImageViewPhotoUrl)
            holder.mTextViewName.text = friend.name
            val itemView = holder.itemView
            itemView.setOnClickListener {
                if (isSelected[position]) {
                    // TODO setbackground
                    itemView.setBackgroundColor(Color.WHITE)
                    itemView.isSelected = false
                    isSelected[position] = false
                } else {
                    // TODO setbackground
                    itemView.setBackgroundColor(Color.YELLOW)
                    itemView.isSelected = true
                    isSelected[position] = true
                }
                modifyTotal(total)
            }

            itemView.setOnLongClickListener {
                // TODO bug if user modifies total.
                friend.needToPay = friend.needToPay - total
                friendWhoPaidFirst = friend
                itemView.setBackgroundColor(Color.RED)
                true
            }

            holder.mTextViewNeedToPay.text = friend.needToPay.toString()
            holder.mTextViewInvitationState.visibility = View.GONE
        }

        override fun getItemCount(): Int {
            return friends.size
        }

        fun modifyTotal(total: Int) {
            this.total = total
            sharedPaid()
        }

        private fun sharedPaid() {
            var isSelectedCount = 0
            for (i in isSelected.indices) {
                if (isSelected[i]) {
                    isSelectedCount++
                }
            }

            var sharedSubtotal = 0
            var remainder = 0
            if (isSelectedCount != 0) {
                try {
                    sharedSubtotal = total / isSelectedCount
                    remainder = total % isSelectedCount
                } catch (e: NumberFormatException) {
                    try {
                        Crashlytics.logException(e)
                    } catch (ie: IllegalThreadStateException) {
                        ie.printStackTrace()
                    }

                }

                for (i in friends.indices) {
                    val friend = friends[i]
                    if (isSelected[i]) {
                        friend.needToPay = if (remainder-- > 0) sharedSubtotal + 1 else sharedSubtotal
                    } else {
                        friend.needToPay = 0
                    }
                }
                notifyDataSetChanged()
            }
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

    inner class ViewHolderShared(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mRelativeLayoutCompat: RelativeLayout = itemView.relativeLayout_friend
        var mImageViewPhotoUrl: AppCompatImageView = itemView.imageView_photo_url
        var mTextViewName: AppCompatTextView = itemView.textView_name
        var mTextViewNeedToPay: AppCompatTextView = itemView.textView_need_to_pay
        var mTextViewInvitationState: AppCompatTextView = itemView.textView_invitation_state

    }

}
