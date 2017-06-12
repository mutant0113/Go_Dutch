package com.mutant.godutch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mutant.godutch.model.Event
import com.mutant.godutch.model.Friend
import kotlinx.android.synthetic.main.activity_new_event.*
import kotlinx.android.synthetic.main.card_view_item_friend.view.*
import java.util.*

class NewEventActivity : BaseActivity() {

    internal var mGroupId: String = ""
    internal var mAdapterFriendsShared: RecycleViewAdapterFriendsShared = RecycleViewAdapterFriendsShared(this@NewEventActivity, 0, arrayListOf())
    internal var mFirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    internal var mDatabaseEvents: DatabaseReference = FirebaseDatabase.getInstance().reference.child("events").child(mGroupId)
    internal var mDatabaseFriends: DatabaseReference = FirebaseDatabase.getInstance().reference.child("friends").child(mFirebaseUser?.uid)
    internal var mStorage: StorageReference? = null

    override val layoutId: Int
        get() = R.layout.activity_new_event

    override fun findViews() {
    }

    override fun setup() {
        mGroupId = intent.getStringExtra(BUNDLE_KEY_GROUP_ID)
        setupFireBase()
        setupFriendsShard()
        setupButtonTax10Listener()
        setupSubtotalTextChangedListener()
        setupTotalTextChangedListener()
    }

    internal var isButtonTax10Clicked = false

    private fun setupButtonTax10Listener() {
        button_tax_10.setOnClickListener { view ->
            if (isButtonTax10Clicked) {
                editText_tax.setText("0")
                editText_total.text = editText_subtotal.text
                isButtonTax10Clicked = false
                view.setBackgroundColor(0)
            } else {
                val subtotal = Integer.valueOf(editText_subtotal.text.toString())!!
                editText_tax.setText(Math.round(subtotal * 0.1).toString())
                editText_total.setText(Math.round(subtotal * 1.1).toString())
                isButtonTax10Clicked = true
                view.setBackgroundColor(Color.YELLOW)
            }
        }
    }

    fun onClickButtonTaxCustom(view: View) {
        // TODO
    }

    private fun setupSubtotalTextChangedListener() {
        editText_subtotal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isButtonTax10Clicked = true
                button_tax_10.callOnClick()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun setupTotalTextChangedListener() {
        editText_total.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    mAdapterFriendsShared?.modifyTotal(if (TextUtils.isEmpty(s)) 0 else Integer.parseInt(s.toString()))
                } catch (e: NumberFormatException) {
                    try {
                        Crashlytics.logException(e)
                    } catch (ie: IllegalThreadStateException) {
                        ie.printStackTrace()
                    }

                }

            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun setupFriendsShard() {
        recycler_view_friends_shared.layoutManager = GridLayoutManager(this, 2)
    }

    private fun setupFireBase() {
        if (mFirebaseUser != null) {
            val filePath = mFirebaseUser?.uid + "/" + System.currentTimeMillis() + ".png"
            mStorage = FirebaseStorage.getInstance().reference.child(filePath)
            mDatabaseFriends.orderByChild("name").addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val friends = ArrayList<Friend>()
                    val iterator = dataSnapshot.children.iterator()
                    while (iterator.hasNext()) {
                        friends.add((iterator.next() as DataSnapshot).getValue(Friend::class.java))
                    }
                    // TODO 如果付錢的不是自己的CASE，先假設都是自己付錢
                    friends.add(0, meInFriend)
                    mAdapterFriendsShared = RecycleViewAdapterFriendsShared(this@NewEventActivity, 0, friends)
                    recycler_view_friends_shared.adapter = mAdapterFriendsShared
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    fun onClickCreateNewEvent(view: View) {
        val title = editText_title.text.toString()
        val description = editText_description.text.toString()
        val subtotal = Integer.parseInt(editText_subtotal.text.toString())
        val tax = Integer.parseInt(editText_tax.text.toString())
        val total = Integer.parseInt(editText_total.text.toString())
        val friendswhoPaid = (recycler_view_friends_shared.adapter as RecycleViewAdapterFriendsShared).friendsFilterBySelected
        val event = Event(title, description, subtotal, tax, total, friendswhoPaid)
        event.subtotal = total
        val databaseReference = mDatabaseEvents.push()
        event.id = databaseReference.key
        event.friendWhoPaidFirst = mAdapterFriendsShared?.friendWhoPaidFirst as Friend
        databaseReference.setValue(event).addOnSuccessListener { finish() }
    }

    inner class RecycleViewAdapterFriendsShared(internal var context: Context, internal var total: Int, internal var friends: List<Friend>) : RecyclerView.Adapter<ViewHolderShared>() {
        internal var isSelected: BooleanArray
        var friendWhoPaidFirst = Friend()
            internal set

        init {
            this.isSelected = BooleanArray(friends.size)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderShared {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_item_friend, parent, false)
            // TODO judge isclicked
            val holder = ViewHolderShared(view)
            return holder
        }

        override fun onBindViewHolder(holder: ViewHolderShared, position: Int) {
            val friend = friends[position]
            // TODO set image
            //            holder.mImageViewProPic.setImageURI();
            holder.mTextViewName.text = friend.name
            val itemView = holder.itemView
            itemView.setOnClickListener {
                if (isSelected[position]) {
                    // TODO setbackground
                    itemView.setBackgroundColor(Color.WHITE)
                    itemView.isSelected = false
                    isSelected[position] = false
                } else {
                    // TODO setbackground
                    itemView.setBackgroundColor(Color.YELLOW)
                    itemView.isSelected = true
                    isSelected[position] = true
                }
                modifyTotal(total)
            }

            itemView.setOnLongClickListener {
                // TODO bug if user modifies total.
                friend.needToPay = friend.needToPay - total
                friendWhoPaidFirst = friend
                itemView.setBackgroundColor(Color.RED)
                true
            }

            holder.mTextViewNeedToPay.text = friend.needToPay.toString()
            holder.mTextViewInvitationState.visibility = View.GONE
        }

        override fun getItemCount(): Int {
            return friends.size
        }

        fun modifyTotal(total: Int) {
            this.total = total
            sharedPaid()
        }

        private fun sharedPaid() {
            var isSelectedCount = 0
            for (i in isSelected.indices) {
                if (isSelected[i]) {
                    isSelectedCount++
                }
            }

            var sharedSubtotal = 0
            var remainder = 0
            if (isSelectedCount != 0) {
                try {
                    sharedSubtotal = total / isSelectedCount
                    remainder = total % isSelectedCount
                } catch (e: NumberFormatException) {
                    try {
                        Crashlytics.logException(e)
                    } catch (ie: IllegalThreadStateException) {
                        ie.printStackTrace()
                    }

                }

                for (i in friends.indices) {
                    val friend = friends[i]
                    if (isSelected[i]) {
                        friend.needToPay = if (remainder-- > 0) sharedSubtotal + 1 else sharedSubtotal
                    } else {
                        friend.needToPay = 0
                    }
                }
                notifyDataSetChanged()
            }
        }

        val friendsFilterBySelected: List<Friend>
            get() {
                val friendsFliterBySelected = ArrayList<Friend>()
                for (i in friends.indices) {
                    if (isSelected[i]) {
                        friendsFliterBySelected.add(friends[i])
                    }
                }
                return friendsFliterBySelected
            }
    }

    inner class ViewHolderShared(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mRelativeLayoutCompat: RelativeLayout = itemView.relativeLayout_friend
        var mImageViewProPic: AppCompatImageView = itemView.imageView_pro_pic
        var mTextViewName: AppCompatTextView = itemView.textView_name
        var mTextViewNeedToPay: AppCompatTextView = itemView.textView_need_to_pay
        var mTextViewInvitationState: AppCompatTextView = itemView.textView_invitation_state

    }

    companion object {

        val BUNDLE_KEY_GROUP_ID = "BUNDLE_KEY_GROUP_ID"

        fun getIntent(activity: Activity, groupId: String): Intent {
            val intent = Intent(activity, NewEventActivity::class.java)
            intent.putExtra(BUNDLE_KEY_GROUP_ID, groupId)
            return intent
        }
    }

}
