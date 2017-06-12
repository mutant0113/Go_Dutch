package com.mutant.godutch;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by evanfang102 on 2017/3/30.
 */

public class ViewHolderGroup extends RecyclerView.ViewHolder {

    public AppCompatImageView mImageViewPhoto;
    public AppCompatTextView mTextViewTitle;
    public AppCompatTextView mTextViewDescription;
    public AppCompatTextView mTextViewTotalPay;
    public LinearLayout mLinearLayoutFriends;

    public ViewHolderGroup(View itemView) {
        super(itemView);
        findViews(itemView);
    }

    private void findViews(View itemView) {
        mImageViewPhoto = (AppCompatImageView) itemView.findViewById(R.id.imageView_photo);
        mTextViewTitle = (AppCompatTextView) itemView.findViewById(R.id.textView_title);
        mTextViewDescription = (AppCompatTextView) itemView.findViewById(R.id.textView_description);
        mTextViewTotalPay = (AppCompatTextView) itemView.findViewById(R.id.textView_need_to_pay);
        mLinearLayoutFriends = (LinearLayout) itemView.findViewById(R.id.linearLayout_friends);
    }

}
