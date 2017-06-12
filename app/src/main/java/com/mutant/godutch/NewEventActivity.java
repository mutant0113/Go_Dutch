package com.mutant.godutch;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
    AppCompatEditText mEditTextSubtotal;
    AppCompatEditText mEditTextTax;
    AppCompatEditText mEditTextTotal;
    AppCompatButton mButtonTax10;
    RecyclerView mRecycleViewFriendsShared;
    RecycleViewAdapterFriendsShared mAdapterFriendsShared;
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
        mEditTextSubtotal = (AppCompatEditText) findViewById(R.id.editText_subtotal);
        mEditTextTotal = (AppCompatEditText) findViewById(R.id.editText_total);
        mEditTextTax = (AppCompatEditText) findViewById(R.id.editText_tax);
        mButtonTax10 = (AppCompatButton) findViewById(R.id.button_tax_10);
    }

    @Override
    public void setup() {
        mGroupId = getIntent().getStringExtra(BUNDLE_KEY_GROUP_ID);
        setupFireBase();
        setupFriendsShard();
        setupButtonTax10Listener();
        setupSubtotalTextChangedListener();
        setupTotalTextChangedListener();
    }

    boolean isButtonTax10Clicked = false;

    private void setupButtonTax10Listener() {
        mButtonTax10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isButtonTax10Clicked) {
                    mEditTextTax.setText("0");
                    mEditTextTotal.setText(mEditTextSubtotal.getText());
                    isButtonTax10Clicked = false;
                    view.setBackgroundColor(0);
                } else {
                    int subtotal = Integer.valueOf(mEditTextSubtotal.getText().toString());
                    mEditTextTax.setText(String.valueOf(Math.round(subtotal * 0.1)));
                    mEditTextTotal.setText(String.valueOf(Math.round(subtotal * 1.1)));
                    isButtonTax10Clicked = true;
                    view.setBackgroundColor(Color.YELLOW);
                }
            }
        });
    }

    public void onClickButtonTaxCustom(View view) {
        // TODO
    }

    private void setupSubtotalTextChangedListener() {
        mEditTextSubtotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isButtonTax10Clicked = true;
                mButtonTax10.callOnClick();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupTotalTextChangedListener() {
        mEditTextTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    mAdapterFriendsShared.modifyTotal(TextUtils.isEmpty(s) ? 0 : Integer.parseInt(s.toString()));
                } catch (NumberFormatException e) {
                    try {
                        Crashlytics.logException(e);
                    } catch (IllegalThreadStateException ie) {
                        ie.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupFriendsShard() {
        mRecycleViewFriendsShared = (RecyclerView) findViewById(R.id.recycler_view_friends_shared);
        mRecycleViewFriendsShared.setLayoutManager(new GridLayoutManager(this, 2));
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
                    // TODO 如果付錢的不是自己的CASE，先假設都是自己付錢
                    friends.add(0, getMeInFriend());
                    mAdapterFriendsShared = new RecycleViewAdapterFriendsShared(NewEventActivity.this, 0, friends);
                    mRecycleViewFriendsShared.setAdapter(mAdapterFriendsShared);
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
        int subtotal = Integer.parseInt(mEditTextSubtotal.getText().toString());
        int tax = Integer.parseInt(mEditTextTax.getText().toString());
        int total = Integer.parseInt(mEditTextTotal.getText().toString());
        List<Friend> friendswhoPaid = ((RecycleViewAdapterFriendsShared) mRecycleViewFriendsShared.getAdapter()).getFriendsFilterBySelected();
        Event event = new Event(title, description, subtotal, tax, total, friendswhoPaid);
        event.setSubtotal(total);
        DatabaseReference databaseReference = mDatabaseEvents.push();
        event.setId(databaseReference.getKey());
        event.setFriendWhoPaidFirst(mAdapterFriendsShared.getFriendWhoPaidFirst());
        databaseReference.setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        });
    }

    private class RecycleViewAdapterFriendsShared extends RecyclerView.Adapter<ViewHolderShared> {

        Context context;
        int total;
        List<Friend> friends;
        boolean[] isSelected;
        Friend friendWhoPaidFirst;

        public RecycleViewAdapterFriendsShared(Context context, int total, List<Friend> friends) {
            this.context = context;
            this.total = total;
            this.friends = friends;
            this.isSelected = new boolean[friends.size()];
        }

        @Override
        public ViewHolderShared onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_item_friend, parent, false);
            // TODO judge isclicked
            ViewHolderShared holder = new ViewHolderShared(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolderShared holder, final int position) {
            final Friend friend = friends.get(position);
            // TODO set image
//            holder.mImageViewProPic.setImageURI();
            holder.mTextViewName.setText(friend.getName());
            final View itemView = holder.itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    if (isSelected[position]) {
                        // TODO setbackground
                        itemView.setBackgroundColor(Color.WHITE);
                        itemView.setSelected(false);
                        isSelected[position] = false;
                    } else {
                        // TODO setbackground
                        itemView.setBackgroundColor(Color.YELLOW);
                        itemView.setSelected(true);
                        isSelected[position] = true;
                    }
                    modifyTotal(total);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // TODO bug if user modifies total.
                    friend.setNeedToPay(friend.getNeedToPay() - total);
                    friendWhoPaidFirst = friend;
                    itemView.setBackgroundColor(Color.RED);
                    return true;
                }
            });

            holder.mTextViewNeedToPay.setText(String.valueOf(friend.getNeedToPay()));
            holder.mTextViewInvitationState.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }

        public void modifyTotal(int total) {
            this.total = total;
            sharedPaid();
        }

        private void sharedPaid() {
            int isSelectedCount = 0;
            for (int i = 0; i < isSelected.length; i++) {
                if (isSelected[i]) {
                    isSelectedCount++;
                }
            }

            int sharedSubtotal = 0;
            int remainder = 0;
            if (isSelectedCount != 0) {
                try {
                    sharedSubtotal = total / isSelectedCount;
                    remainder = total % isSelectedCount;
                } catch (NumberFormatException e) {
                    try {
                        Crashlytics.logException(e);
                    } catch (IllegalThreadStateException ie) {
                        ie.printStackTrace();
                    }
                }

                for (int i = 0; i < friends.size(); i++) {
                    Friend friend = friends.get(i);
                    if (isSelected[i]) {
                        friend.setNeedToPay((remainder-- > 0) ? sharedSubtotal + 1 : sharedSubtotal);
                    } else {
                        friend.setNeedToPay(0);
                    }
                }
                notifyDataSetChanged();
            }
        }

        public List<Friend> getFriendsFilterBySelected() {
            List<Friend> friendsFliterBySelected = new ArrayList<>();
            for (int i = 0; i < friends.size(); i++) {
                if (isSelected[i]) {
                    friendsFliterBySelected.add(friends.get(i));
                }
            }
            return friendsFliterBySelected;
        }

        public Friend getFriendWhoPaidFirst() {
            return friendWhoPaidFirst;
        }
    }

    class ViewHolderShared extends RecyclerView.ViewHolder {

        RelativeLayout mRelativeLayoutCompat;
        AppCompatImageView mImageViewProPic;
        AppCompatTextView mTextViewName;
        AppCompatTextView mTextViewNeedToPay;
        AppCompatTextView mTextViewInvitationState;

        public ViewHolderShared(View itemView) {
            super(itemView);
            findViews(itemView);
        }

        private void findViews(View itemView) {
            mRelativeLayoutCompat = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_friend);
            mImageViewProPic = (AppCompatImageView) itemView.findViewById(R.id.imageView_pro_pic);
            mTextViewName = (AppCompatTextView) itemView.findViewById(R.id.textView_name);
            mTextViewNeedToPay = (AppCompatTextView) itemView.findViewById(R.id.textView_need_to_pay);
            mTextViewInvitationState = (AppCompatTextView) itemView.findViewById(R.id.textView_invitation_state);
        }
    }

}
