package com.mutant.godutch

import android.app.Activity
import android.os.Build
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mutant.godutch.model.Friend
import com.mutant.godutch.model.Group
import com.mutant.godutch.utils.Utility

/**
 * Created by evanfang102 on 2017/3/30.
 */

class AdapterGroup(private val activity: Activity, private val groups: ArrayList<Group>) : RecyclerView.Adapter<ViewHolderGroup>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderGroup {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_item_group, parent, false)
        return ViewHolderGroup(view)
    }

    override fun onBindViewHolder(holder: ViewHolderGroup, position: Int) {
        val group = groups[position]
        Glide.with(activity).load(group.photoUrl).placeholder(R.drawable.travel_default_640).into(holder.mImageViewPhoto)
        holder.mTextViewDate.text = Utility.getRelativeTimeSpanDate(group.timestampCreated)
        holder.mTextViewTitle.text = group.title
        holder.mTextViewDebt.text = "TWD $${group.subtotal}"
        holder.mLinearLayoutFriends.removeAllViews()
        group.friendsUid.forEach {
            fetchUserInfo(it, object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }

                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    val friend = dataSnapshot?.getValue(Friend::class.java)
                    val imageViewFriendPhoto = ImageView(activity)
                    Glide.with(activity).load(friend?.photoUrl).placeholder(R.drawable.ic_account_circle_black_48dp)
                            .fitCenter().animate(R.anim.design_fab_in).into(imageViewFriendPhoto)
                    val proPicSize = activity.resources.getDimension(R.dimen.pro_pic_size).toInt()
                    val params = LinearLayoutCompat.LayoutParams(proPicSize, proPicSize)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        params.marginEnd = activity.resources.getDimension(R.dimen.activity_vertical_margin).toInt()
                    }
                    params.rightMargin = activity.resources.getDimension(R.dimen.activity_vertical_margin).toInt()
                    holder.mLinearLayoutFriends.addView(imageViewFriendPhoto, params)
                }
            })
        }

        holder.itemView.setOnClickListener { activity.startActivity(EventsActivity.getIntent(activity, group)) }
        holder.itemView.setOnLongClickListener {
            removeGroup(group)
            false
        }
    }

    private fun removeGroup(group: Group) {
        // TODO all events should be removed
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val databaseGroupsMapping: DatabaseReference? = FirebaseDatabase.getInstance().reference.
                child("groups_mapping").child(firebaseUser?.uid)
        val databaseGroups: DatabaseReference? = FirebaseDatabase.getInstance().reference.child("groups")

        AlertDialog.Builder(activity).setTitle("系統提示").setMessage("確定要刪除此筆？")
                .setPositiveButton("確定") { _, _ ->
                    databaseGroupsMapping?.child(group.key)?.removeValue()
                    databaseGroups?.child(group.key)?.removeValue()
                }.setNeutralButton("取消", null).show()
    }

    private fun fetchUserInfo(uid: String, listener: ValueEventListener) {
        FirebaseDatabase.getInstance().reference.child("users").child(uid).addListenerForSingleValueEvent(listener)
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    fun addItem(group: Group) {
        groups.add(0, group)
        notifyItemInserted(0)
    }
}
