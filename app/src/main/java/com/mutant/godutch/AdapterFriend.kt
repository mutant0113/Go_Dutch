package com.mutant.godutch

import android.app.Activity
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mutant.godutch.model.Friend

/**
 * Created by evanfang102 on 2017/3/30.
 */

class AdapterFriend(private val activity: Activity, private val friends: MutableList<Friend>) : RecyclerView.Adapter<ViewHolderFriend>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFriend {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_item_friend, parent, false)
        val holder = ViewHolderFriend(view)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolderFriend, position: Int) {}

    override fun onBindViewHolder(holder: ViewHolderFriend, position: Int, payloads: List<Any>?) {
        super.onBindViewHolder(holder, position, payloads)
        val friend = friends[position]
        if (payloads!!.isEmpty()) {
            Glide.with(activity).load(friend.photoUrl).placeholder(R.drawable.profile_pic)
                    .error(R.drawable.profile_pic).into(holder.mImageViewPhotoUrl)
            holder.mTextViewName.text = friend.name
            setupFriendState(holder, friend)
            holder.mTextViewNeedToPay.visibility = View.INVISIBLE
            //        holder.itemView.setOnClickListener(new View.OnClickListener() {
            //            @Override
            //            public void onClick(View v) {
            //                activity.startActivity(EventsActivity.getIntent(activity, friend.getId()));
            //            }
            //        });
        } else {
            val type = payloads[0] as Int
            if (type == 0) {
                setupFriendState(holder, friend)
            }
        }
    }

    private fun setupFriendState(holder: ViewHolderFriend, friend: Friend) {
        val textView = holder.mTextViewInvitationState
        val state = friend.state
        when (state) {
            Friend.STATE_ACCEPTED -> {
                textView.setTextColor(Color.BLACK)
                textView.text = "朋友"
            }
            Friend.STATE_BE_INVITED -> {
                textView.setTextColor(Color.RED)
                textView.text = "等待接受"
            }
            Friend.STATE_NOT_BE_ACCEPTED -> {
                textView.setTextColor(Color.GRAY)
                textView.text = "接受"
            }
        }

        textView.setOnClickListener {
            if (state == Friend.STATE_NOT_BE_ACCEPTED) {
                textView.setTextColor(Color.BLACK)
                textView.text = "朋友"
                sendAcceptMsgToServer(holder, friend)
            }
        }
    }

    private fun sendAcceptMsgToServer(holder: ViewHolderFriend, friend: Friend) {
        val databaseFriends = FirebaseDatabase.getInstance().reference.child("friends")
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            // TODO check failed
            databaseFriends.child(firebaseUser.uid).child(friend.uid).child("state").setValue(Friend.STATE_ACCEPTED)
            databaseFriends.child(friend.uid).child(firebaseUser.uid).child("state").setValue(Friend.STATE_ACCEPTED)
        } else {
            holder.mTextViewInvitationState.setTextColor(Color.GRAY)
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    fun addItem(friend: Friend) {
        friends.add(0, friend)
        notifyItemInserted(0)
    }

    fun changeItemState(friend: Friend) {
        var position = -1
        for (i in friends.indices) {
            val child = friends[i]
            if (child.name == friend.name) {
                position = i
                break
            }
        }

        friends[position] = friend
        notifyItemChanged(position, 0)
    }
}
