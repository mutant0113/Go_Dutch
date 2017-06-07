package com.mutant.godutch;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by evanfang102 on 2017/3/30.
 */

public class ViewHolderFriend extends RecyclerView.ViewHolder {

    public AppCompatImageView mImageViewPhoto;
    public AppCompatTextView mTextViewName;

    public ViewHolderFriend(View itemView) {
        super(itemView);
        findViews(itemView);
    }

    private void findViews(View itemView) {
        mImageViewPhoto = (AppCompatImageView) itemView.findViewById(R.id.imageView_pro_pic);
        mTextViewName = (AppCompatTextView) itemView.findViewById(R.id.textView_name);
    }

}
