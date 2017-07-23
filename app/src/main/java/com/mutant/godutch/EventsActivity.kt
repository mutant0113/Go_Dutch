package com.mutant.godutch

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.widget.*
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.mutant.godutch.NewEventActivity.Companion.TYPE
import com.mutant.godutch.model.Event
import com.mutant.godutch.model.Friend
import com.mutant.godutch.utils.Utility
import kotlinx.android.synthetic.main.activity_events.*
import kotlinx.android.synthetic.main.card_view_item_event.view.*
import kotlinx.android.synthetic.main.card_view_item_friend_shared.view.*
import java.util.*

class EventsActivity : BaseActivity() {

    internal var mGroupId: String = ""
    internal var mGroupName: String = ""

    internal var mAdapterEvent: RecycleViewAdapterEvent = RecycleViewAdapterEvent(this@EventsActivity, ArrayList<Event>())
    internal var mDatabaseEvents: DatabaseReference? = null

    companion object {

        val BUNDLE_KEY_GROUP_ID = "BUNDLE_KEY_GROUP_ID"
        val BUNDLE_KEY_GROUP_NAME = "BUNDLE_KEY_GROUP_NAME"

        fun getIntent(activity: Activity, groupId: String, groupName: String): Intent {
            val intent = Intent(activity, EventsActivity::class.java)
            intent.putExtra(BUNDLE_KEY_GROUP_ID, groupId)
            intent.putExtra(BUNDLE_KEY_GROUP_NAME, groupName)
            return intent
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_events, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_list -> Toast.makeText(this@EventsActivity, "action_list", Toast.LENGTH_LONG).show()
            R.id.action_list_check -> check()
        }

        return super.onOptionsItemSelected(item)
    }

    override val layoutId: Int
        get() = R.layout.activity_events

    fun check() {
        startActivity(CheckActivity.getIntent(this, mAdapterEvent.getEvents))
    }

    override fun setup() {
        mGroupId = intent.getStringExtra(BUNDLE_KEY_GROUP_ID)
        mGroupName = intent.getStringExtra(BUNDLE_KEY_GROUP_NAME)
        collapseFabMenuWhenClickingOutside()
        setupEvents()
        setupFireBase()
        setupFabNewEvent()
    }

    fun collapseFabMenuWhenClickingOutside() {
        coordinatorLayout_parent.setOnTouchListener { _, _ ->
            if (fab_new_event.isExpanded) {
                fab_new_event.collapse()
                true
            }
            false
        }
    }

    private fun setupFireBase() {
        mDatabaseEvents = FirebaseDatabase.getInstance().reference.child("events").child(mGroupId)
        if (mDatabaseEvents != null) {
            mDatabaseEvents?.orderByKey()?.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    var event = dataSnapshot.getValue(Event::class.java)
                    event.key = dataSnapshot.key
                    mAdapterEvent.addItem(event)
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
        fab_food.setOnClickListener {
            intentToNewEvent(NewEventActivity.Companion.TYPE.FOOD)
        }
        fab_shopping.setOnClickListener {
            intentToNewEvent(NewEventActivity.Companion.TYPE.SHOPPING)
        }
        fab_hotel.setOnClickListener {
            intentToNewEvent(NewEventActivity.Companion.TYPE.HOTEL)
        }
        fab_ticket.setOnClickListener {
            intentToNewEvent(NewEventActivity.Companion.TYPE.TICKET)
        }
    }

    fun intentToNewEvent(type: TYPE) {
        startActivity(NewEventActivity.getIntent(this@EventsActivity, mGroupId, mGroupName, type))
        fab_new_event.collapse()
    }

    private fun setupEvents() {
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

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onBindViewHolder(holder: ViewHolderEvent, position: Int) {
            val event = events[position]
            setupPhoto(holder, event)
            setupFabType(holder, event)
            holder.mTextViewTitle.text = event.title
            holder.mTextViewDate.text = Utility.getRelativeTimeSpanDate(event.timestampCreated)
            holder.mTextViewDescription.text = event.description
            holder.mTextViewTotal.text = "$" + event.subtotal
            holder.mRecycleViewFriendsShared.adapter = RecycleViewAdapterFriendsShared(activity, event.friendsShared)
            holder.mRecycleViewFriendsShared.layoutManager = GridLayoutManager(this@EventsActivity, 2)
            // TODO fetch from firebase
            holder.itemView.setOnClickListener {
                activity.startActivity(NewEventActivity.getIntent(
                        activity, mGroupId, mGroupName, NewEventActivity.Companion.TYPE.FOOD))
            }
            holder.itemView.setOnLongClickListener {
                removeEvent(event)
                false
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private fun setupFabType(holder: ViewHolderEvent, event: Event) {
            when (event.type) {
                TYPE.FOOD -> {
                    holder.mFabType.setImageDrawable(getDrawable(R.drawable.food_fork_drink))
                }
                TYPE.SHOPPING -> {
                    holder.mFabType.setImageDrawable(getDrawable(R.drawable.ic_shopping_cart_white_24dp))
                }
                TYPE.HOTEL -> {
                    holder.mFabType.setImageDrawable(getDrawable(R.drawable.ic_local_hotel_white_24dp))
                }
                TYPE.TICKET -> {
                    holder.mFabType.setImageDrawable(getDrawable(R.drawable.ticket))
                }
            }
        }

        private fun setupPhoto(holder: ViewHolderEvent, event: Event) {
            if (TextUtils.isEmpty(event.photoUrl)) {
                when (event.type) {
                    TYPE.FOOD -> holder.mImageViewPhoto.setImageResource(R.drawable.food_default_640)
                    TYPE.SHOPPING -> holder.mImageViewPhoto.setImageResource(R.drawable.shopping_default_640)
                    TYPE.HOTEL -> holder.mImageViewPhoto.setImageResource(R.drawable.hotel_default_640)
                    TYPE.TICKET -> holder.mImageViewPhoto.setImageResource(R.drawable.ticket_default_640)
                }
            } else {
                Glide.with(activity).load(event.photoUrl).error(R.drawable.take_a_photo).into(holder.mImageViewPhoto)
            }
        }

        private fun removeEvent(event: Event) {
            AlertDialog.Builder(activity).setTitle("系統提示").setMessage("確定要刪除此筆？")
                    .setPositiveButton("確定") { dialog, which -> mDatabaseEvents?.child(event.key)?.removeValue() }
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

    internal inner class RecycleViewAdapterFriendsShared(var activity: Activity, var friendShared: List<Friend>) : RecyclerView.Adapter<ViewHolderFriendShared>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFriendShared {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_item_friend_shared, parent, false)
            val holder = ViewHolderFriendShared(view)
            return holder
        }

        override fun onBindViewHolder(holder: ViewHolderFriendShared, position: Int) {
            val friendShared = this.friendShared[position]
            holder.mTextViewName.text = friendShared.name
            Glide.with(activity).load(friendShared.photoUrl).placeholder(R.drawable.ic_account_circle_black_48dp)
                    .fitCenter().animate(R.anim.design_fab_in).into(holder.mImageViewPhotoUrl)
            holder.mTextViewNeedToPay.text = friendShared.needToPay.toString()
        }

        override fun getItemCount(): Int {
            return friendShared.size
        }
    }

    internal inner class ViewHolderEvent(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImageViewPhoto: AppCompatImageView = itemView.imageView_photo
        var mFabType: FloatingActionButton = itemView.fab_event_type
        var mTextViewTitle: AppCompatTextView = itemView.textView_title
        var mTextViewDate: AppCompatTextView = itemView.textView_date
        var mTextViewDescription: AppCompatTextView = itemView.textView_description
        var mTextViewTotal: AppCompatTextView = itemView.textView_total
        var mRecycleViewFriendsShared: RecyclerView = itemView.recycler_view_friends_with_pay

    }

    internal inner class ViewHolderFriendShared(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImageViewPhotoUrl: AppCompatImageView = itemView.imageView_photo_url
        var mTextViewName: AppCompatTextView = itemView.textView_name
        var mTextViewNeedToPay: AppCompatTextView = itemView.textView_need_to_pay
    }

}
