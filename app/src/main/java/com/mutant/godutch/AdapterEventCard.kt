package com.mutant.godutch

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.mutant.godutch.model.Event
import com.mutant.godutch.model.Friend
import com.mutant.godutch.model.Group
import com.mutant.godutch.utils.Utility
import com.mutant.godutch.widget.EventTypeWidget.TYPE
import java.util.*

/**
 * Created by evanfang102 on 2017/7/24.
 */

class AdapterEventCard(var activity: Activity, private var group: Group) : RecyclerView.Adapter<ViewHolderEventCard>() {

    private var events = arrayListOf<Event>()
    private val databaseEvents = FirebaseDatabase.getInstance().reference.child("events").child(group.key)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderEventCard {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_item_event, parent, false)
        return ViewHolderEventCard(view)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolderEventCard, position: Int) {
        val event = events[position]
        setupPhoto(holder, event)
        setupFabType(holder, event)
        holder.mTextViewTitle.text = event.title
        holder.mTextViewDate.text = Utility.getRelativeTimeSpanDate(event.timestampCreated)
        // TODO 轉換幣別
        holder.mTextViewTotal.text = "TWD $" + event.subtotal
        holder.mRecycleViewFriendsShared.adapter = RecycleViewAdapterFriendsShared(activity, event.friendsShared)
        holder.mRecycleViewFriendsShared.layoutManager = GridLayoutManager(activity, 2)
        // TODO fetch from database
        holder.itemView.setOnClickListener {
            activity.startActivity(NewEventActivity.getIntent(activity, group))
        }
        holder.itemView.setOnLongClickListener {
            removeEvent(event)
            false
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupFabType(holder: ViewHolderEventCard, event: Event) {
        when (event.type) {
            TYPE.FOOD -> {
                holder.mFabType.setImageDrawable(activity.getDrawable(R.drawable.food_fork_drink))
            }
            TYPE.SHOPPING -> {
                holder.mFabType.setImageDrawable(activity.getDrawable(R.drawable.ic_shopping_cart_white_24dp))
            }
            TYPE.HOTEL -> {
                holder.mFabType.setImageDrawable(activity.getDrawable(R.drawable.ic_local_hotel_white_24dp))
            }
            TYPE.TICKET -> {
                holder.mFabType.setImageDrawable(activity.getDrawable(R.drawable.ticket))
            }
        }
    }

    private fun setupPhoto(holder: ViewHolderEventCard, event: Event) {
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
                .setPositiveButton("確定") { _, _ -> databaseEvents?.child(event.key)?.removeValue() }
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
            holder.mTextViewDebt.text = friendShared.debt.toString()
        }

        override fun getItemCount(): Int {
            return friendShared.size
        }
    }

}