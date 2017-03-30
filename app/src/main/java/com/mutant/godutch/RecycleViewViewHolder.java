package com.mutant.godutch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by evanfang102 on 2017/3/30.
 */

public class RecycleViewViewHolder extends RecyclerView.ViewHolder {

    public TextView mTextViewTitle;
    public TextView mTextViewDescription;
    public TextView mTextViewTotalPay;
    public LinearLayout mLinearLayoutFriends;

    public RecycleViewViewHolder(View itemView) {
        super(itemView);
        findViews(itemView);
    }

    private void findViews(View itemView) {
        mTextViewTitle = (TextView) itemView.findViewById(R.id.textView_title);
        mTextViewDescription = (TextView) itemView.findViewById(R.id.textView_description);
        mTextViewTotalPay = (TextView) itemView.findViewById(R.id.textView_total_pay);
        mLinearLayoutFriends = (LinearLayout) itemView.findViewById(R.id.linearLayout_friends);
    }

}
