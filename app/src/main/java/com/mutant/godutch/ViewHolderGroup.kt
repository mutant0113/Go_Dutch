package com.mutant.godutch

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.card_view_item_group.view.*

/**
 * Created by evanfang102 on 2017/3/30.
 */

class ViewHolderGroup(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var mImageViewPhoto: AppCompatImageView = itemView.imageView_photo
    var mTextViewDate: AppCompatTextView = itemView.textView_date
    var mTextViewTitle: AppCompatTextView = itemView.textView_title
    var mTextViewDescription: AppCompatTextView = itemView.textView_description
    var mTextViewTotalPay: AppCompatTextView = itemView.textView_need_to_pay
    var mLinearLayoutFriends: LinearLayoutCompat = itemView.linearLayout_friends

}
