package com.mutant.godutch;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mutant.godutch.model.Event;
import com.mutant.godutch.model.Friend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewEventActivity extends BaseActivity {

    public static final String BUNDLE_KEY_GROUP_ID = "BUNDLE_KEY_GROUP_ID";
    String mGroupId;

    AppCompatEditText mEditTextTitle;
    AppCompatEditText mEditTextDescription;
    AppCompatEditText mEditTextTotalPaid;
    RecyclerView mRecycleViewFriends;
    RecycleViewAdapterFriends mAdapterFriends;
    FirebaseUser mFirebaseUser;
    DatabaseReference mDatabaseEvents;
    DatabaseReference mDatabaseFriends;
    StorageReference mStorage;

    public static Intent getIntent(Activity activity, String groupId) {
        Intent intent = new Intent(activity, NewEventActivity.class);
        intent.putExtra(BUNDLE_KEY_GROUP_ID, groupId);
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
        mGroupId = getIntent().getStringExtra(BUNDLE_KEY_GROUP_ID);
        setupFireBase();
        setupFriends();
    }

    private void setupFriends() {
        mRecycleViewFriends = (RecyclerView) findViewById(R.id.recycler_view_friends);
        mRecycleViewFriends.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void setupFireBase() {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseEvents = FirebaseDatabase.getInstance().getReference().child("events").child(mGroupId);
        if (mFirebaseUser != null) {
            String filePath = mFirebaseUser.getUid() + "/" + System.currentTimeMillis() + ".png";
            mStorage = FirebaseStorage.getInstance().getReference().child(filePath);
            mDatabaseFriends = FirebaseDatabase.getInstance().getReference().child("friends").child(mFirebaseUser.getUid());
            mDatabaseFriends.orderByChild("name").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Friend> friends = new ArrayList<>();
                    Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        friends.add(((DataSnapshot) iterator.next()).getValue(Friend.class));
                    }
                    mAdapterFriends = new RecycleViewAdapterFriends(NewEventActivity.this, friends);
                    mRecycleViewFriends.setAdapter(mAdapterFriends);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void onClickCreateNewEvent(View view) {
        String title = mEditTextTitle.getText().toString();
        String description = mEditTextDescription.getText().toString();
        int totalPaid = 0;
        try {
            totalPaid = Integer.parseInt(mEditTextTotalPaid.getText().toString());
        } catch (NumberFormatException e) {
            try {
                Crashlytics.logException(e);
            } catch (IllegalThreadStateException ie) {
                ie.printStackTrace();
            }
        }
        List<Friend> friendswhoPaid = ((RecycleViewAdapterFriends) mRecycleViewFriends.getAdapter()).getFriendsFilterBySelected();
        Event event = new Event(title, description, friendswhoPaid);
        event.setTotalPaid(totalPaid);
        DatabaseReference databaseReference = mDatabaseEvents.push();
        event.setId(databaseReference.getKey());
        databaseReference.setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        });
    }

    private class RecycleViewAdapterFriends extends RecyclerView.Adapter<ViewHolder> {

        Context context;
        List<Friend> friends;
        boolean[] isSelected;

        public RecycleViewAdapterFriends(Context context, List<Friend> friends) {
            this.context = context;
            this.friends = friends;
            this.isSelected = new boolean[friends.size()];
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_item_friend, parent, false);
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
            holder.mTextViewInvitationState.setVisibility(View.GONE);
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

        RelativeLayout mRelativeLayoutCompat;
        AppCompatImageView mImageViewProPic;
        AppCompatTextView mTextViewName;
        AppCompatTextView mTextViewInvitationState;

        public ViewHolder(View itemView) {
            super(itemView);
            findViews(itemView);
        }

        private void findViews(View itemView) {
            mRelativeLayoutCompat = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_friend);
            mImageViewProPic = (AppCompatImageView) itemView.findViewById(R.id.imageView_pro_pic);
            mTextViewName = (AppCompatTextView) itemView.findViewById(R.id.textView_name);
            mTextViewInvitationState = (AppCompatTextView) itemView.findViewById(R.id.textView_invitation_state);
        }
    }

}
