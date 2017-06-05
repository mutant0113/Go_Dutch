package com.mutant.godutch;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mutant.godutch.model.Event;
import com.mutant.godutch.model.Friend;

import java.util.ArrayList;
import java.util.List;

public class NewEventActivity extends BaseActivity {

    AppCompatEditText mEditTextTitle;
    AppCompatEditText mEditTextDescription;
    AppCompatEditText mEditTextTotalPaid;
    RecyclerView mRecycleViewFriends;
    DatabaseReference mDatabase;

    public static Intent getIntent(Activity activity) {
        Intent intent = new Intent(activity, NewEventActivity.class);
        return intent;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_event;
    }

    @Override
    public void findViews() {
        mEditTextTitle = (AppCompatEditText) findViewById(R.id.editText_title);
        mEditTextDescription = (AppCompatEditText) findViewById(R.id.editText_description);
        mEditTextTotalPaid = (AppCompatEditText) findViewById(R.id.editText_total_paid);
    }

    @Override
    public void setup() {
        setupFireBase();
        setupFriends();
    }

    private void setupFireBase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void onClickCreateNewEvent(View view) {
        String title = mEditTextTitle.getText().toString();
        String description = mEditTextDescription.getText().toString();
        int totalPaid = 0;
        try {
            totalPaid = Integer.parseInt(mEditTextTotalPaid.getText().toString());
        } catch (NumberFormatException e) {
            Crashlytics.logException(e);
        }
        List<Friend> friendsFilterBySelected = ((RecycleViewAdapterFriends) mRecycleViewFriends.getAdapter()).getFriendsFilterBySelected();
        Event event = new Event(title, description, friendsFilterBySelected);
        event.setTotalPaid(totalPaid);
        DatabaseReference databaseReference = mDatabase.child("event").push();
        databaseReference.setValue(event);
    }

    private void setupFriends() {
        mRecycleViewFriends = (RecyclerView) findViewById(R.id.recycler_view_friends);
        // TODO fetch from web;
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend(123, "小美", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg"));
        friends.add(new Friend(123, "小明", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg"));
        friends.add(new Friend(123, "小剛", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg"));
        friends.add(new Friend(123, "小智", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg"));
        RecycleViewAdapterFriends adapter = new RecycleViewAdapterFriends(friends);
        mRecycleViewFriends.setAdapter(adapter);
        mRecycleViewFriends.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private class RecycleViewAdapterFriends extends RecyclerView.Adapter<ViewHolder> {

        List<Friend> friends;
        boolean[] isSelected;

        public RecycleViewAdapterFriends(List<Friend> friends) {
            this.friends = friends;
            this.isSelected = new boolean[friends.size()];
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_item_friend, parent, false);
            // TODO judge isclicked
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Friend friend = friends.get(position);
            // TODO set image
//            holder.mImageViewProPic.setImageURI();
            holder.mTextViewName.setText(friend.getName());
            final View itemView = holder.itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    if(isSelected[position]) {
                        // TODO setbackground
                        itemView.setBackgroundColor(Color.WHITE);
                        isSelected[position] = false;
                    } else {
                        // TODO setbackground
                        itemView.setBackgroundColor(Color.YELLOW);
                        isSelected[position] = true;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }

        public List<Friend> getFriendsFilterBySelected() {
            List<Friend> friendsFliterBySelected = new ArrayList<>();
            for(int i = 0; i < friends.size(); i++) {
                if(isSelected[i]) {
                    friendsFliterBySelected.add(friends.get(i));
                }
            }
            return friendsFliterBySelected;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayoutCompat mLinearLayoutCompat;
        AppCompatImageView mImageViewProPic;
        AppCompatTextView mTextViewName;

        public ViewHolder(View itemView) {
            super(itemView);
            findViews(itemView);
        }

        private void findViews(View itemView) {
            mLinearLayoutCompat = (LinearLayoutCompat) itemView.findViewById(R.id.linearLayoutCompat_friend);
            mImageViewProPic = (AppCompatImageView) itemView.findViewById(R.id.appCompat_imageView_pro_pic);
            mTextViewName = (AppCompatTextView) itemView.findViewById(R.id.appCompat_textView_name);
        }
    }

}
