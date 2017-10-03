package com.mutant.godutch.fragment

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mutant.godutch.AdapterPaidCheck
import com.mutant.godutch.DeptActivity
import com.mutant.godutch.NewEventActivity
import com.mutant.godutch.R
import com.mutant.godutch.model.Event
import com.mutant.godutch.model.ExchangeRate
import com.mutant.godutch.model.Friend
import com.mutant.godutch.utils.NotificationHelper
import com.mutant.godutch.utils.Utility
import kotlinx.android.synthetic.main.fragment_new_event_step_2.*

/**
 * Created by evanfang102 on 2017/7/27.
 */

class NewEventStep2Fragment : Fragment() {

    lateinit var mActivity: NewEventActivity
    private var mFirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private var mDatabaseFriends: DatabaseReference = FirebaseDatabase.getInstance().reference.child("friends").child(mFirebaseUser?.uid)
    var mFriends = ArrayList<Friend>()
    var mAllUsers = ArrayList<Friend>()
    var mTotal: Double = 0.0
    lateinit var mExchangeRate: ExchangeRate

    companion object {
        val REQUEST_PAID_FIRST = 1
        val REQUEST_SHARED = 2
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_done, menu)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        mActivity = (activity as NewEventActivity)
        mTotal = mActivity.mNewEventStep1Fragment.mTotal
        mExchangeRate = mActivity.mNewEventStep1Fragment.mExchangeRate
        return inflater!!.inflate(R.layout.fragment_new_event_step_2, container, false)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity.mToolbar?.title = "[${mActivity.mGroup.title}]分攤花費"

        setupFireBase()
    }

    // 先假設都是自己付錢
    private fun setupPaidFirst() {
        val friendsPaid = arrayListOf<Friend>()
        mAllUsers.forEach { friendsPaid.add(Friend(it)) }

        friendsPaid.filter { it.uid == mActivity.me.uid }.map { it.debt = mTotal }
        recycler_view_paid.layoutManager = LinearLayoutManager(mActivity)
        recycler_view_paid.adapter = AdapterPaidCheck(mActivity, friendsPaid, mExchangeRate, null)

        linearLayout_paid.setOnClickListener {
            val intent = DeptActivity.getIntent(activity, mTotal, mExchangeRate, friendsPaid)
            startActivityForResult(intent, REQUEST_PAID_FIRST)
        }
    }

    private fun setupShared() {
        val friendsShared = ArrayList<Friend>()
        mAllUsers.forEach { friendsShared.add(Friend(it)) }

        val debtAverage = mTotal / friendsShared.size
        friendsShared.forEach { it.debt = debtAverage }

        recycler_view_shared.layoutManager = LinearLayoutManager(mActivity)
        recycler_view_shared.adapter = AdapterPaidCheck(mActivity, friendsShared, mExchangeRate, null)

        // TODO not good design, should consider no network situation
        linearLayout_friends_who_shared.setOnClickListener {
            val intent = DeptActivity.getIntent(activity, mTotal, mExchangeRate, friendsShared)
            startActivityForResult(intent, REQUEST_SHARED)
        }
    }

    private fun setupFireBase() {
        if (mFirebaseUser == null) return

        mDatabaseFriends.orderByChild("name").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val iterator = dataSnapshot.children.iterator()
                while (iterator.hasNext()) {
                    mFriends.add((iterator.next() as DataSnapshot).getValue(Friend::class.java))
                }

                mFriends.forEach { mAllUsers.add(Friend(it)) }
                mAllUsers.add(0, Friend(mActivity.me))

                setupPaidFirst()
                setupShared()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
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
        val title = (mActivity.mNewEventStep1Fragment.rootView.findViewById(R.id.editText_title) as EditText).text.toString()
        val subtotal = (mActivity.mNewEventStep1Fragment.rootView.findViewById(R.id.editText_subtotal) as EditText).text.toString().toDouble()
        val tax = mActivity.mNewEventStep1Fragment.mTax
        val friendsShared = (recycler_view_shared.adapter as AdapterPaidCheck).getFriendsPaid()
        val friendPaid = (recycler_view_paid.adapter as AdapterPaidCheck).getFriendsPaid()
        val event = Event(imageDownloadUrl?.toString() ?: "", mActivity.mType, title, subtotal, tax,
                mTotal, mExchangeRate, friendsShared, friendPaid)
        val databaseReference = FirebaseDatabase.getInstance().reference.child("events").child(mActivity.mGroup.key).push()
        databaseReference.setValue(event).addOnSuccessListener {
            val notifyTitle = getString(R.string.notify_new_event_title, mFirebaseUser?.displayName, mActivity.mGroup.key)
            val notifyContent = getString(R.string.notify_new_event_content, title)
            sendNewEventNotificationToFriends(friendsShared[1].uid, notifyTitle, notifyContent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val friendsDept: ArrayList<Friend> = data!!.getParcelableArrayListExtra(DeptActivity.BUNDLE_KEY_FRIENDS_DEPT)
            if (requestCode == REQUEST_PAID_FIRST) {
                (recycler_view_paid.adapter as AdapterPaidCheck).update(friendsDept)
            } else if (requestCode == REQUEST_SHARED) {
                (recycler_view_shared.adapter as AdapterPaidCheck).update(friendsDept)
            }
        }
    }

}
