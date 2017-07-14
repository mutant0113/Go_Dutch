package com.mutant.godutch

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mutant.godutch.model.Group

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
        Glide.with(activity).load(group.photoUrl).error(R.drawable.take_a_photo).into(holder.mImageViewPhoto)
        holder.mTextViewTitle.text = group.title
        holder.mTextViewDescription.text = group.description
        holder.mTextViewTotalPay.text = "$" + group.subtotal.toString()
        // TODO add friends' photo
        for (friend in group.friends) {
            val imageViewFriendPhoto = ImageView(activity)
            Glide.with(activity).load(friend.photoUrl).placeholder(R.drawable.ic_account_circle_black_48dp)
                    .fitCenter().animate(R.anim.design_fab_in).into(imageViewFriendPhoto)
            holder.mLinearLayoutFriends.addView(imageViewFriendPhoto)
        }
        holder.itemView.setOnClickListener { activity.startActivity(EventsActivity.getIntent(activity, group.id)) }
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    fun addItem(group: Group) {
        groups.add(0, group)
        notifyItemInserted(0)
    }
}
