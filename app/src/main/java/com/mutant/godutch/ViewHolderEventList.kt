package com.mutant.godutch

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.list_item_event.view.*

/**
 * Created by evanfang102 on 2017/3/30.
 */

class ViewHolderEventList(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var mImageViewPhoto: AppCompatImageView = itemView.imageView_photo
    var mTextViewType: AppCompatTextView = itemView.textView_type
    var mTextViewTitle: AppCompatTextView = itemView.textView_title
    var mTextViewDate: AppCompatTextView = itemView.textView_date
    var mTextViewDescription: AppCompatTextView = itemView.textView_description
    var mTextViewTotal: AppCompatTextView = itemView.textView_total
    var mLinearLayoutFriendsShared: LinearLayoutCompat = itemView.linearLayout_friends_shared
}
