package com.mutant.godutch;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by evanfang102 on 2017/3/30.
 */

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewViewHolder> {

    private List<GroupModel> groupModels;

    public RecycleViewAdapter(List<GroupModel> groupModels) {
        this.groupModels = groupModels;
    }

    @Override
    public RecycleViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item_group, parent, false);
        RecycleViewViewHolder holder = new RecycleViewViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecycleViewViewHolder holder, int position) {
        GroupModel groupModel = groupModels.get(position);
        holder.mTextViewTitle.setText(groupModel.getTitle());
        holder.mTextViewDescription.setText(groupModel.getDescription());
        holder.mTextViewTotalPay.setText(groupModel.getTotalPay());
        // TODO add friends' photo
//        holder.mLinearLayoutFriends.addView(new View());
    }

    @Override
    public int getItemCount() {
        return groupModels.size();
    }
}
