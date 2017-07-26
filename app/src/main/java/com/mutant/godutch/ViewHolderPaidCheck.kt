package com.mutant.godutch

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.list_item_paid_check.view.*

/**
 * Created by evanfang102 on 2017/3/30.
 */

class ViewHolderPaidCheck(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var mImageViewPhoto: AppCompatImageView = itemView.imageView_photo_url
    var mTextViewName: AppCompatTextView = itemView.textView_name
    var mTextViewPaidCheck: AppCompatTextView = itemView.textView_paid_check
}
