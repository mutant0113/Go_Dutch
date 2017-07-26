package com.mutant.godutch

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.mutant.godutch.model.Friend


/**
 * Created by evanfang102 on 2017/7/26.
 */

class AdapterPaidCheck(var activity: Activity, var friends: List<Friend>) :
        RecyclerView.Adapter<ViewHolderPaidCheck>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPaidCheck {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_paid_check, parent, false)
        val holder = ViewHolderPaidCheck(view)
        return holder
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolderPaidCheck, position: Int) {
        val friend = friends[position]
        setupProPic(holder, friend.photoUrl)
        holder.mTextViewName.text = friend.name
        holder.mTextViewPaidCheck.text = "0 / " + friend.needToPay
    }

    private fun setupProPic(holder: ViewHolderPaidCheck, photoUrl: String) {
        if (!TextUtils.isEmpty(photoUrl)) {
            Glide.with(activity).load(photoUrl).error(R.drawable.take_a_photo).into(holder.mImageViewPhoto)
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }

}