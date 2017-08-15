package com.mutant.godutch

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.card_view_item_friend.view.*

/**
 * Created by evanfang102 on 2017/3/30.
 */

class ViewHolderFriend(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var mImageViewPhotoUrl: AppCompatImageView = itemView.imageView_photo_url
    var mTextViewName: AppCompatTextView = itemView.textView_name
    var mTextViewInvitationState: AppCompatTextView = itemView.textView_invitation_state
    var mTextViewDebt: TextView = itemView.textView_debt

}
