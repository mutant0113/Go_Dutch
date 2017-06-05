package com.mutant.godutch;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mutant.godutch.model.Friend;
import com.mutant.godutch.model.Group;

import java.util.ArrayList;
import java.util.List;

public class NewGroupActivity extends BaseActivity {

    AppCompatEditText mEditTextTitle;
    AppCompatEditText mEditTextDescription;
    RecyclerView mRecycleViewFriends;
    DatabaseReference mDatabase;

    public static Intent getIntent(Activity activity) {
        Intent intent = new Intent(activity, NewGroupActivity.class);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_group;
    }

    @Override
    public void findViews() {
        mEditTextTitle = (AppCompatEditText) findViewById(R.id.editText_title);
        mEditTextDescription = (AppCompatEditText) findViewById(R.id.editText_description);
    }

    @Override
    public void setup() {
        setupFireBase();
        setupFriends();
    }

    private void setupFireBase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void onClickCreateNewGroup(View view) {
        String title = mEditTextTitle.getText().toString();
        String description = mEditTextDescription.getText().toString();
        List<Friend> friendsFilterBySelected = ((RecycleViewAdapterFriends) mRecycleViewFriends.getAdapter()).getFriendsFilterBySelected();
        Group group = new Group(title, description, 0, friendsFilterBySelected);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null) {
            String userUid = firebaseUser.getUid();
            DatabaseReference databaseReference = mDatabase.child("group").child(userUid).push();
            group.setId(databaseReference.getKey());
            databaseReference.setValue(group).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finish();
                }
            });
        } else {
            Crashlytics.logException(new NullPointerException());
        }
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
