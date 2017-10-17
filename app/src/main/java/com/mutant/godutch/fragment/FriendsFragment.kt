package com.mutant.godutch.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mutant.godutch.AdapterFriend
import com.mutant.godutch.LoginActivity
import com.mutant.godutch.NewFriendActivity
import com.mutant.godutch.R
import com.mutant.godutch.model.Friend
import kotlinx.android.synthetic.main.fragment_friends.*
import java.util.*

/**
 * Created by evanfang102 on 2017/6/7.
 */

class FriendsFragment : Fragment() {

    internal var mAdapterFriend: AdapterFriend? = null
    private var mDatabaseFriend: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFirebase()
        setupFriends()
        setupProPic()
        setupButtonCurrency()
        setupButtonLogout()
//        setupFabNewGroup()
    }

    private fun setupProPic() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if(firebaseUser != null) {
            Glide.with(context).load(firebaseUser.photoUrl).error(R.drawable.profile_pic).into(imageView_photo_me)
        }

        imageView_photo_me.setOnClickListener {
            // TODO
        }
    }

    private fun setupButtonCurrency() {
        button_currency.setOnClickListener {
            startActivity(Intent(NewFriendActivity.getIntent(activity)))
        }
    }

    private fun setupButtonLogout() {
        button_logout.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            auth.addAuthStateListener { firebaseAuth ->
                if (firebaseAuth.currentUser == null && activity != null) {
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity.finish()
                }
            }
            auth.signOut()
        }
    }

//    private fun setupFabNewGroup() {
//        fab_new_friend.setOnClickListener { startActivity(NewFriendActivity.getIntent(activity)) }
//    }

    private fun setupFriends() {
        val MyLayoutManager = LinearLayoutManager(activity)
        MyLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mAdapterFriend = AdapterFriend(activity, ArrayList<Friend>())
        recycler_view_shared.adapter = mAdapterFriend
        recycler_view_shared.layoutManager = MyLayoutManager
    }

    private fun setupFirebase() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        mDatabaseFriend = FirebaseDatabase.getInstance().reference.child("friends").child(firebaseUser!!.uid)
        mDatabaseFriend!!.orderByKey().addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                mAdapterFriend!!.addItem(dataSnapshot.getValue(Friend::class.java))
                recycler_view_shared.scrollToPosition(0)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                mAdapterFriend!!.changeItemState(dataSnapshot.getValue(Friend::class.java))
            }

            override fun onChildRemoved(dataSnapshot: com.google.firebase.database.DataSnapshot) {
                // TODO
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                // TODO
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

}
