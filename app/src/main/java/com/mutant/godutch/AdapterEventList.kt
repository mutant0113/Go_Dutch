package com.mutant.godutch

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.mutant.godutch.model.Event
import com.mutant.godutch.utils.Utility
import com.mutant.godutch.widget.EventTypeWidget.TYPE
import java.util.*

/**
 * Created by evanfang102 on 2017/7/24.
 */

class AdapterEventList(var activity: Activity, var events: ArrayList<Event>, var groupId: String,
                       var groupName: String, var databaseEvents: DatabaseReference?) : RecyclerView.Adapter<ViewHolderEventList>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderEventList {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_event, parent, false)
        val holder = ViewHolderEventList(view)
        return holder
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolderEventList, position: Int) {
        val event = events[position]
        setupPhoto(holder, event)
        setupFabType(holder, event)
        setupFriendsSharedProPic(holder, event)
        holder.mTextViewTitle.text = event.title
        holder.mTextViewDate.text = Utility.getRelativeTimeSpanDate(event.timestampCreated)
        holder.mTextViewDescription.text = event.description
        // TODO 轉換幣別
        holder.mTextViewTotal.text = "TWD $" + event.subtotal
        // TODO fetch from firebase
        holder.itemView.setOnClickListener {
            // TODO makeSceneTransitionAnimation bug
//            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, it,
//                    activity.resources.getString(R.string.events_image_photo))
//            ActivityCompat.startActivity(activity, EventDetailActivity.getIntent(activity, event, position), options.toBundle())
            activity.startActivity(EventDetailActivity.getIntent(activity, events, position))
        }
        holder.itemView.setOnLongClickListener {
            removeEvent(event)
            false
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupFabType(holder: ViewHolderEventList, event: Event) {
        when (event.type) {
            TYPE.FOOD -> {
                holder.mTextViewType.text = activity.getString(R.string.event_type_food)
            }
            TYPE.SHOPPING -> {
                holder.mTextViewType.text = activity.getString(R.string.event_type_shopping)
            }
            TYPE.HOTEL -> {
                holder.mTextViewType.text = activity.getString(R.string.event_type_hotel)
            }
            TYPE.TICKET -> {
                holder.mTextViewType.text = activity.getString(R.string.event_type_ticket)
            }
        }
    }

    private fun setupPhoto(holder: ViewHolderEventList, event: Event) {
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

    private fun setupFriendsSharedProPic(holder: ViewHolderEventList, event: Event) {
        for (friend in event.friendsShared) {
            val imageViewFriendPhoto = ImageView(activity)
            val proPicSize = activity.resources.getDimension(R.dimen.pro_pic_size).toInt()
            val layoutParams = LinearLayout.LayoutParams(proPicSize, proPicSize)
            // TODO bug, no left margin
            layoutParams.setMargins(activity.resources.getDimension(R.dimen.list_screen_edge_left_and_right_margins).toInt(), 0, 0, 0)
            Glide.with(activity).load(friend.photoUrl).placeholder(R.drawable.ic_account_circle_black_48dp)
                    .fitCenter().animate(R.anim.design_fab_in).into(imageViewFriendPhoto)
            holder.mLinearLayoutFriendsShared.addView(imageViewFriendPhoto, layoutParams)
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

}