package com.mutant.godutch.fragment

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.crashlytics.android.Crashlytics
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mutant.godutch.*
import com.mutant.godutch.NewEventActivity.Companion.REQUEST_CODE_EXCHANGE_RATE
import com.mutant.godutch.R
import com.mutant.godutch.model.Event
import com.mutant.godutch.model.Friend
import com.mutant.godutch.utils.NotificationHelper
import com.mutant.godutch.utils.Utility
import kotlinx.android.synthetic.main.card_view_item_friend.view.*
import kotlinx.android.synthetic.main.fragment_new_event_step_1.*
import kotlinx.android.synthetic.main.fragment_new_event_step_2.*
import java.util.*

/**
 * Created by evanfang102 on 2017/7/27.
 */

class NewEventStep2Fragment : Fragment() {

    lateinit var mActivity: NewEventActivity
    lateinit var mAdapterFriendsShared: RecycleViewAdapterFriendsShared
    private var mFirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var mDatabaseFriends: DatabaseReference = FirebaseDatabase.getInstance().reference.child("friends").child(mFirebaseUser?.uid)

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_done, menu)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        mActivity = (activity as NewEventActivity)
        mAdapterFriendsShared = RecycleViewAdapterFriendsShared(activity, 0, arrayListOf())
        return inflater!!.inflate(R.layout.fragment_new_event_step_2, container, false)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFireBase()
        setupCurrencyListener()
        setupPaidFirst()
        setupFriendsShared()
    }

    private fun setupCurrencyListener() {
        button_currency.setOnClickListener {
            startActivityForResult(Intent(activity, ExchangeRateActivity::class.java), REQUEST_CODE_EXCHANGE_RATE)
        }
    }

    private fun setupPaidFirst() {
        linearLayout_friend_who_paid_first.setOnClickListener({ startActivity(Intent(activity, PaidFirstActivity::class.java)) })
    }

    private fun setupFriendsShared() {
        linearLayout_friends_who_shared.setOnClickListener({ startActivity(Intent(activity, SharedActivity::class.java)) })
        recycler_view_friends_shared.layoutManager = GridLayoutManager(mActivity, 2)
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
                    friends.add(0, mActivity.me)
                    mAdapterFriendsShared = RecycleViewAdapterFriendsShared(mActivity, 0, friends)
                    recycler_view_friends_shared.adapter = mAdapterFriendsShared
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    fun createNewEvent() {
        if (mActivity.isTakePhoto) {
            val bitmap = ((mActivity.mNewEventStep3Fragment.rootView.findViewById(R.id.imageView_photo) as ImageView).drawable as BitmapDrawable).bitmap
            val filePath = mFirebaseUser!!.uid + "/" + System.currentTimeMillis() + ".png"
            Utility.uploadImage(filePath, bitmap, OnFailureListener { exception ->
                exception.printStackTrace()
                Toast.makeText(mActivity, R.string.upload_image_failed, Toast.LENGTH_LONG).show()
            }, OnSuccessListener { taskSnapshot ->
                Toast.makeText(mActivity, R.string.upload_image_successfully, Toast.LENGTH_LONG).show()
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                createNewEvent(taskSnapshot?.downloadUrl)
            })
        } else {
            createNewEvent(null)
        }
    }

    private fun createNewEvent(imageDownloadUrl: Uri?) {
        val title = (mActivity.mNewEventStep3Fragment.rootView.findViewById(R.id.editText_title) as TextView).text.toString()
        val subtotal = Integer.parseInt(editText_subtotal.text.toString())
        val tax = mActivity.mNewEventStep1Fragment.mTax
        val total = mActivity.mNewEventStep1Fragment.mTotal
        val friendsShared = (recycler_view_friends_shared.adapter as RecycleViewAdapterFriendsShared).friendsFilterBySelected
        val friendPaid = mAdapterFriendsShared?.friendPaid
        val event = Event(imageDownloadUrl?.toString() ?: "", mActivity.mType, title, subtotal, tax,
                total, mActivity.mNewEventStep1Fragment.mExchangeRate, friendsShared, friendPaid)
        val databaseReference = FirebaseDatabase.getInstance().reference.child("events").child(mActivity.mGroupId).push()
        databaseReference.setValue(event).addOnSuccessListener {
            val notiyTitle = getString(R.string.notify_new_event_title, mFirebaseUser?.displayName, mActivity.mGroupName)
            val notifyContent = getString(R.string.notify_new_event_content, title)
            sendNewEventNotificationToFriends(friendsShared[1].uid, notiyTitle, notifyContent)
            mActivity.finish()
        }
    }

    private fun sendNewEventNotificationToFriends(userUid: String, title: String, content: String) {
        FirebaseDatabase.getInstance().reference.child("users").child(userUid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val fcmToken = dataSnapshot.child("fcmToken").value as? String
                    // TODO title, content, icon
                    if (!TextUtils.isEmpty(fcmToken)) {
                        NotificationHelper.sendNotificationToUser(fcmToken!!, title, content)
                    }
                } else {
                    Toast.makeText(mActivity, "此ID不存在", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    inner class RecycleViewAdapterFriendsShared(internal var context: Context, internal var total: Int, internal var friends: List<Friend>) : RecyclerView.Adapter<ViewHolderShared>() {
        var isSelected: BooleanArray
        var friendPaid = Friend()
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
                friend.debt = friend.debt - total
                friendPaid = friend
                itemView.setBackgroundColor(Color.RED)
                true
            }

            holder.mTextViewDebt.text = friend.debt.toString()
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
            val isSelectedCount = isSelected.indices.count { isSelected[it] }

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
                        friend.debt = if (remainder-- > 0) sharedSubtotal + 1 else sharedSubtotal
                    } else {
                        friend.debt = 0
                    }
                }
                notifyDataSetChanged()
            }
        }

        val friendsFilterBySelected: List<Friend>
            get() {
                val friendsFliterBySelected = friends.indices.filter { isSelected[it] }.map { friends[it] }
                return friendsFliterBySelected
            }
    }

    inner class ViewHolderShared(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mRelativeLayoutCompat: RelativeLayout = itemView.relativeLayout_friend
        var mImageViewPhotoUrl: AppCompatImageView = itemView.imageView_photo_url
        var mTextViewName: AppCompatTextView = itemView.textView_name
        var mTextViewDebt: AppCompatTextView = itemView.textView_debt
        var mTextViewInvitationState: AppCompatTextView = itemView.textView_invitation_state
    }

}
