package com.mutant.godutch

import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.card_view_item_event.view.*

/**
 * Created by evanfang102 on 2017/3/30.
 */

class ViewHolderEventCard(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var mImageViewPhoto: AppCompatImageView = itemView.imageView_photo
    var mFabType: FloatingActionButton = itemView.fab_event_type
    var mTextViewTitle: AppCompatTextView = itemView.textView_title
    var mTextViewDate: AppCompatTextView = itemView.textView_date
    var mTextViewTotal: AppCompatTextView = itemView.textView_total
    var mRecycleViewFriendsShared: RecyclerView = itemView.recycler_view_shared
}
