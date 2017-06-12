package com.mutant.godutch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mutant.godutch.model.Event;
import com.mutant.godutch.model.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mutant on 2017/6/11.
 */

public class CheckoutActivity extends BaseActivity {

    RecyclerView mRecyclerViewCheckout;
    private static String BUNDLE_KEY_LIST_EVENTS = "BUNDLE_KEY_LIST_EVENTS";

    public static Intent getIntent(Context context, ArrayList<Event> events) {
        Intent intent = new Intent(context, CheckoutActivity.class);
        intent.putParcelableArrayListExtra(BUNDLE_KEY_LIST_EVENTS, events);
        return intent;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_checkout;
    }

    @Override
    public void findViews() {
        mRecyclerViewCheckout = (RecyclerView) findViewById(R.id.recycler_view_checkout);
    }

    @Override
    public void setup() {
        setupCheckout();
    }

    private void setupCheckout() {
        List<Event> events = getIntent().getParcelableArrayListExtra(BUNDLE_KEY_LIST_EVENTS);
        List<Friend> friendsShared = new ArrayList<>();
        for(Event event: events) {
            for(Friend friendInEvent: event.getFriendsShared()) {
                boolean isExists = false;
                for(Friend friendInShared: friendsShared) {
                    if(friendInShared.getUid().equals(friendInEvent.getUid())) {
                        friendInShared.setNeedToPay(friendInShared.getNeedToPay() + friendInEvent.getNeedToPay());
                        isExists = true;
                        break;
                    }
                }

                if(!isExists) {
                    friendsShared.add(friendInEvent);
                }
            }
        }

        mRecyclerViewCheckout.setAdapter(new RecycleViewAdapterFriendsShared(friendsShared));
        mRecyclerViewCheckout.setLayoutManager(new GridLayoutManager(this, 2));
    }

    class RecycleViewAdapterFriendsShared extends RecyclerView.Adapter<ViewHolderFriendShared> {

        List<Friend> friendShared;

        public RecycleViewAdapterFriendsShared(List<Friend> friendsShared) {
            this.friendShared = friendsShared;
        }

        @Override
        public ViewHolderFriendShared onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_item_friend_shared, parent, false);
            ViewHolderFriendShared holder = new ViewHolderFriendShared(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolderFriendShared holder, int position) {
            Friend friendShared = this.friendShared.get(position);
            holder.mTextViewName.setText(friendShared.getName());
            // TODO
//            holder.mImageViewProPic.setImageURI();
            holder.mTextViewNeedToPay.setText(String.valueOf(friendShared.getNeedToPay()));
        }

        @Override
        public int getItemCount() {
            return friendShared.size();
        }
    }

    class ViewHolderFriendShared extends RecyclerView.ViewHolder {

        public AppCompatImageView mImageViewProPic;
        public AppCompatTextView mTextViewName;
        public AppCompatTextView mTextViewNeedToPay;

        public ViewHolderFriendShared(View itemView) {
            super(itemView);
            findViews(itemView);
        }

        private void findViews(View itemView) {
            mImageViewProPic = (AppCompatImageView) itemView.findViewById(R.id.imageView_pro_pic);
            mTextViewName = (AppCompatTextView) itemView.findViewById(R.id.textView_name);
            mTextViewNeedToPay = (AppCompatTextView) itemView.findViewById(R.id.textView_need_to_pay);
        }
    }
}
