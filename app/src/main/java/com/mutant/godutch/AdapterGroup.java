package com.mutant.godutch;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.mutant.godutch.model.Group;

import java.util.List;

/**
 * Created by evanfang102 on 2017/3/30.
 */

public class AdapterGroup extends RecyclerView.Adapter<ViewHolderGroup> {

    private Activity activity;
    private List<Group> groups;

    public AdapterGroup(Activity activity, List<Group> groups) {
        this.activity = activity;
        this.groups = groups;
    }

    @Override
    public ViewHolderGroup onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item_group, parent, false);
        ViewHolderGroup holder = new ViewHolderGroup(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolderGroup holder, int position) {
        final Group group = groups.get(position);
        // TODO fetch image from web
        Glide.with(activity).load(group.getPhotoUrl()).error(R.drawable.take_a_photo).into(holder.mImageViewPhoto);
        holder.mTextViewTitle.setText(group.getTitle());
        holder.mTextViewDescription.setText(group.getDescription());
        holder.mTextViewTotalPay.setText("$" + String.valueOf(group.getTotalPaid()));
        // TODO add friends' photo
//        holder.mLinearLayoutFriends.addView(new View());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(EventsActivity.getIntent(activity, group.getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void addItem(Group group) {
        groups.add(0, group);
        notifyItemInserted(0);
    }
}
