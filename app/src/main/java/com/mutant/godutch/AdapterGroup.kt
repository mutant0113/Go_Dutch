package com.mutant.godutch

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.mutant.godutch.model.Group
import com.mutant.godutch.utils.Utility

/**
 * Created by evanfang102 on 2017/3/30.
 */

class AdapterGroup(private val activity: Activity, private val groups: MutableList<Group>) : RecyclerView.Adapter<ViewHolderGroup>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderGroup {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_item_group, parent, false)
        val holder = ViewHolderGroup(view)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolderGroup, position: Int) {
        val group = groups[position]
        Glide.with(activity).load(group.photoUrl).placeholder(R.drawable.travel_default_640).into(holder.mImageViewPhoto)
        holder.mTextViewDate.text = Utility.getRelativeTimeSpanDate(group.timestampCreated)
        holder.mTextViewTitle.text = group.title
        holder.mTextViewDebt.text = "TWD $$(group.subtotal.toString())"
        holder.mLinearLayoutFriends.removeAllViews()
        // TODO get user data by uid
//        for (friend in group.friends) {
//            val imageViewFriendPhoto = ImageView(activity)
//            Glide.with(activity).load(friend.photoUrl).placeholder(R.drawable.ic_account_circle_black_48dp)
//                    .fitCenter().animate(R.anim.design_fab_in).into(imageViewFriendPhoto)
//            val proPicSize = activity.resources.getDimension(R.dimen.pro_pic_size).toInt()
//            val params = LinearLayout.LayoutParams(proPicSize, proPicSize)
//            params.rightMargin = activity.resources.getDimension(R.dimen.activity_vertical_margin).toInt()
//            holder.mLinearLayoutFriends.addView(imageViewFriendPhoto, params)
//        }
        holder.itemView.setOnClickListener { activity.startActivity(EventsActivity.getIntent(activity, group.key, group.title)) }
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    fun addItem(group: Group) {
        groups.add(0, group)
        notifyItemInserted(0)
    }
}
