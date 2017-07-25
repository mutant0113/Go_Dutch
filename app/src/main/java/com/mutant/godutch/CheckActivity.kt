package com.mutant.godutch

import android.content.Context
import android.content.Intent
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mutant.godutch.model.Event
import com.mutant.godutch.model.Friend
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.card_view_item_friend.view.*
import java.util.*

/**
 * Created by Mutant on 2017/6/11.
 */

class CheckActivity : BaseActivity() {

    companion object {
        private val BUNDLE_KEY_LIST_EVENTS = "BUNDLE_KEY_LIST_EVENTS"

        fun getIntent(context: Context, events: ArrayList<Event>?): Intent {
            val intent = Intent(context, CheckActivity::class.java)
            intent.putParcelableArrayListExtra(BUNDLE_KEY_LIST_EVENTS, events)
            return intent
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_checkout

    override fun setup() {
        setupCheckout()
    }

    private fun setupCheckout() {
        val events = intent.getParcelableArrayListExtra<Event>(BUNDLE_KEY_LIST_EVENTS)
        val friendsShared = ArrayList<Friend>()
        for (event in events) {
            for (friendInEvent in event.friendsShared) {
                var isExists = false
                for (friendInShared in friendsShared) {
                    if (friendInShared.uid == friendInEvent.uid) {
                        friendInShared.needToPay = friendInShared.needToPay + friendInEvent.needToPay
                        isExists = true
                        break
                    }
                }

                if (!isExists) {
                    friendsShared.add(friendInEvent)
                }
            }
        }

        recycler_view_checkout.adapter = RecycleViewAdapterFriendsShared(friendsShared)
        recycler_view_checkout.layoutManager = GridLayoutManager(this, 2)
    }

    internal inner class RecycleViewAdapterFriendsShared(var friendShared: List<Friend>) : RecyclerView.Adapter<ViewHolderFriendShared>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFriendShared {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_item_friend_shared, parent, false)
            val holder = ViewHolderFriendShared(view)
            return holder
        }

        override fun onBindViewHolder(holder: ViewHolderFriendShared, position: Int) {
            val friendShared = this.friendShared[position]
            holder.mTextViewName.text = friendShared.name
            // TODO
            // holder.mImageViewPhotoUrl.setImageURI();
            holder.mTextViewNeedToPay.text = friendShared.needToPay.toString()
        }

        override fun getItemCount(): Int {
            return friendShared.size
        }

    }

    internal inner class ViewHolderFriendShared(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mImageViewPhotoUrl: AppCompatImageView = itemView.imageView_photo_url
        var mTextViewName: AppCompatTextView = itemView.textView_name
        var mTextViewNeedToPay: AppCompatTextView = itemView.textView_need_to_pay
    }

}