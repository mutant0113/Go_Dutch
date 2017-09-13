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

    var mAdapterGroup: AdapterGroup? = null
    var firebaseUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGroups()
        setupFirebase()
        setupFabNewGroup(view)
    }

    private fun setupFabNewGroup(view: View) {
        view.findViewById(R.id.fab_new_group).setOnClickListener { startActivity(NewGroupActivity.getIntent(activity)) }
    }

    private fun setupGroups() {
        mAdapterGroup = AdapterGroup(activity, ArrayList<Group>())
        val MyLayoutManager = LinearLayoutManager(activity)
        MyLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recycler_view_groups.adapter = mAdapterGroup
        recycler_view_groups.layoutManager = MyLayoutManager
    }

    private fun setupFirebase() {
        val databaseGroupsMapping: DatabaseReference? = FirebaseDatabase.getInstance().reference.
                child("groups_mapping").child(firebaseUser?.uid)
        databaseGroupsMapping?.addChildEventListener(object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, p1: String?) {
                // TODO
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot?, p1: String?) {
                fetchGroupsData((dataSnapshot?.value as String))
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
            }

            override fun onCancelled(dataSnapshot: DatabaseError?) {
            }

        })
    }

    private fun fetchGroupsData(groupsKey: String) {
        val databaseGroups: DatabaseReference? = FirebaseDatabase.getInstance().reference.
                child("groups").child(groupsKey)
        databaseGroups?.orderByKey()?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var group = dataSnapshot.getValue(Group::class.java)
                group.key = groupsKey
                mAdapterGroup?.addItem(group)
                recycler_view_groups.scrollToPosition(0)
            }

            override fun onCancelled(p0: DatabaseError?) {
            }
        })
    }
}
