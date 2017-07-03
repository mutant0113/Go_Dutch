package com.mutant.godutch

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*
import com.mutant.godutch.model.Event
import com.mutant.godutch.model.Friend
import com.mutant.godutch.utils.Utility
import kotlinx.android.synthetic.main.activity_events.*
import kotlinx.android.synthetic.main.card_view_item_event.view.*
import kotlinx.android.synthetic.main.card_view_item_friend_shared.view.*
import java.util.*

class EventsActivity : BaseActivity() {
    internal var mGroupId: String = ""

    internal var mAdapterEvent: RecycleViewAdapterEvent = RecycleViewAdapterEvent(this@EventsActivity, ArrayList<Event>())
    internal var mDatabaseEvents: DatabaseReference? = null

    companion object {

        val BUNDLE_KEY_GROUP_ID = "BUNDLE_KEY_GROUP_ID"

        fun getIntent(activity: Activity, groupId: String): Intent {
            val intent = Intent(activity, EventsActivity::class.java)
            intent.putExtra(BUNDLE_KEY_GROUP_ID, groupId)
            return intent
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_events

    override fun findViews() {

    }

    fun onClickButtonCheckout(view: View) {
        startActivity(CheckoutActivity.getIntent(this, mAdapterEvent.getEvents))
    }

    override fun setup() {
        mGroupId = intent.getStringExtra(BUNDLE_KEY_GROUP_ID)
        setupEvents()
        setupFireBase()
        setupFabNewEvent()
    }

    private fun setupFireBase() {
        mDatabaseEvents = FirebaseDatabase.getInstance().reference.child("events").child(mGroupId)
        if (mDatabaseEvents != null) {
            mDatabaseEvents?.orderByKey()?.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    mAdapterEvent.addItem(dataSnapshot.getValue(Event::class.java))
                    recycler_view_event.scrollToPosition(0)
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                    // TODO
                }

                override fun onChildRemoved(dataSnapshot: com.google.firebase.database.DataSnapshot) {
                    // TODO
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                    // TODO
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }

    private fun setupFabNewEvent() {
        findViewById(R.id.fab_new_event).setOnClickListener { startActivity(NewEventActivity.getIntent(this@EventsActivity, mGroupId)) }
    }

    private fun setupEvents() {
        // TODO fetch from web;
        //        events.add(new Event("住宿", "六天住宿錢", friendShared));
        //        events.add(new Event("早餐", "好吃的懷石料理", friendShared));
        //        events.add(new Event("門票錢", "直接登上東京鐵塔!!", friendShared));
        //        events.add(new Event("喔米阿給", "三大待喔米阿給YA~~", friendShared));
        val MyLayoutManager = LinearLayoutManager(this)
        MyLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recycler_view_event.adapter = mAdapterEvent
        recycler_view_event.layoutManager = MyLayoutManager
    }

    internal inner class RecycleViewAdapterEvent(var activity: Activity, var events: ArrayList<Event>) :
            RecyclerView.Adapter<ViewHolderEvent>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderEvent {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_item_event, parent, false)
            val holder = ViewHolderEvent(view)
            return holder
        }

        override fun onBindViewHolder(holder: ViewHolderEvent, position: Int) {
            val event = events[position]
            holder.mTextViewTitle.text = event.title
            holder.mTextViewDate.text = Utility.getRelativeTimeSpanDate(event.timestampCreated)
            holder.mTextViewDescription.text = event.description
            holder.mTextViewTotal.text = "$" + event.subtotal
            holder.mRecycleViewFriendsShared.adapter = RecycleViewAdapterFriendsShared(event.friendsShared)
            holder.mRecycleViewFriendsShared.layoutManager = GridLayoutManager(this@EventsActivity, 2)
            holder.itemView.setOnClickListener { activity.startActivity(NewEventActivity.getIntent(activity, mGroupId)) }
            holder.itemView.setOnLongClickListener {
                removeEvent(event)
                false
            }
        }

        private fun removeEvent(event: Event) {
            AlertDialog.Builder(activity).setTitle("系統提示").setMessage("確定要刪除此筆？")
                    .setPositiveButton("確定") { dialog, which -> mDatabaseEvents?.child(event.id)?.removeValue() }
                    .setNeutralButton("取消", null).show()
        }

        override fun getItemCount(): Int {
            return events.size
        }

        fun addItem(event: Event) {
            events.add(0, event)
            notifyItemInserted(0)
        }

        val getEvents: ArrayList<Event>
            get() = events
    }

    internal inner class RecycleViewAdapterFriendsShared(var friendShared: List<Friend>) : RecyclerView.Adapter<ViewHolderFriendShared>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFriendShared {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_item_friend_shared, parent, false)
            val holder = ViewHolderFriendShared(view)
            return holder
        }

        override fun onBindViewHolder(holder: ViewHolderFriendShared, position: Int) {
            val friendShared = this.friendShared[position]
            holder.mTextViewName.text = friendShared.name
            // TODO
            //            holder.mImageViewProPic.setImageURI();
            holder.mTextViewNeedToPay.text = friendShared.needToPay.toString()
        }

        override fun getItemCount(): Int {
            return friendShared.size
        }
    }

    internal inner class ViewHolderEvent(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTextViewTitle: AppCompatTextView = itemView.textView_title
        var mTextViewDate: AppCompatTextView = itemView.textView_date
        var mTextViewDescription: AppCompatTextView = itemView.textView_description
        var mTextViewTotal: AppCompatTextView = itemView.textView_total
        var mRecycleViewFriendsShared: RecyclerView = itemView.recycler_view_friends_with_pay

    }

    internal inner class ViewHolderFriendShared(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImageViewProPic: AppCompatImageView = itemView.imageView_pro_pic
        var mTextViewName: AppCompatTextView = itemView.textView_name
        var mTextViewNeedToPay: AppCompatTextView = itemView.textView_need_to_pay
    }

}
