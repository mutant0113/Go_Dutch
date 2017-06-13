package com.mutant.godutch.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mutant.godutch.AdapterGroup
import com.mutant.godutch.NewGroupActivity
import com.mutant.godutch.R
import com.mutant.godutch.model.Group
import kotlinx.android.synthetic.main.fragment_groups.*
import java.util.*

/**
 * Created by evanfang102 on 2017/3/30.
 */

class GroupsFragment : Fragment() {

    internal var mAdapterGroup: AdapterGroup? = null
    private var mDatabaseGroup: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFirebase()
        setupGroups(view)
        setupFabNewGroup(view)
    }

    private fun setupFabNewGroup(view: View) {
        view.findViewById(R.id.fab_new_group).setOnClickListener { startActivity(NewGroupActivity.getIntent(activity)) }
    }

    private fun setupGroups(view: View) {
        mAdapterGroup = AdapterGroup(activity, ArrayList<Group>())
        val MyLayoutManager = LinearLayoutManager(activity)
        MyLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recycler_view_groups.adapter = mAdapterGroup
        recycler_view_groups.layoutManager = MyLayoutManager
    }

    private fun setupFirebase() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        mDatabaseGroup = FirebaseDatabase.getInstance().reference.child("groups").child(firebaseUser?.uid)
        mDatabaseGroup?.orderByKey()?.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                mAdapterGroup?.addItem(dataSnapshot.getValue(Group::class.java))
                recycler_view_groups.scrollToPosition(0)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                // TODO
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
