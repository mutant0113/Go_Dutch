package com.mutant.godutch;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by evanfang102 on 2017/3/30.
 */

public class ViewHolderFriend extends RecyclerView.ViewHolder {

    public AppCompatImageView mImageViewProPic;
    public AppCompatTextView mTextViewName;
    public AppCompatTextView mTextViewInvitationState;

    public ViewHolderFriend(View itemView) {
        super(itemView);
        findViews(itemView);
    }

    private void findViews(View itemView) {
        mImageViewProPic = (AppCompatImageView) itemView.findViewById(R.id.imageView_pro_pic);
        mTextViewName = (AppCompatTextView) itemView.findViewById(R.id.textView_name);
        mTextViewInvitationState = (AppCompatTextView) itemView.findViewById(R.id.textView_invitation_state);
    }

}
