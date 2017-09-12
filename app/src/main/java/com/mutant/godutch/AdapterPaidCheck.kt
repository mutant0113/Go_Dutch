package com.mutant.godutch

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.mutant.godutch.model.ExchangeRate
import com.mutant.godutch.model.Friend


/**
 * Created by evanfang102 on 2017/7/26.
 */

class AdapterPaidCheck(var activity: Activity, var friends: List<Friend>, val exchangeRate: ExchangeRate?, var database: DatabaseReference?) :
        RecyclerView.Adapter<ViewHolderPaidCheck>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPaidCheck {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_paid_check, parent, false)
        return ViewHolderPaidCheck(view)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolderPaidCheck, position: Int) {
        val friend = friends[position]
        setupProPic(holder, friend.photoUrl)
        holder.mTextViewName.text = friend.name
        holder.mButtonPaidCheck.text = if(friend.settleUp) Math.abs(friend.debt).toString() else {"0"} + " / " + Math.abs(friend.debt)
        var isClickPaidCheck = false
        holder.mButtonPaidCheck.setOnClickListener {
            isClickPaidCheck = !isClickPaidCheck
            if(isClickPaidCheck) {
                val afterExchange = Math.round(Math.abs(friend.debt) * exchangeRate?.rate!!)
                holder.mButtonPaidCheck.text = if (friend.settleUp) afterExchange.toString() else {"0"} + " / " + afterExchange
            } else {
                holder.mButtonPaidCheck.text = if(friend.settleUp) Math.abs(friend.debt).toString() else {"0"} + " / " + Math.abs(friend.debt)
            }
        }

        if(database == null) {
            holder.mButtonRemind.visibility = View.GONE
            holder.mButtonSettleUp.visibility = View.GONE
        }

        // TODO send notification
        holder.mButtonRemind.setOnClickListener { }
        holder.mButtonSettleUp.setOnClickListener {
            holder.mButtonPaidCheck.text = Math.abs(friend.debt).toString() + " / " + Math.abs(friend.debt)
            // TODO database
            // TODO send notification
            // TODO friend paid check add
            friend.settleUp = true
            database?.setValue(friends)
        }
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