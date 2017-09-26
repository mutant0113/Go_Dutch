package com.mutant.godutch

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.*
import com.mutant.godutch.model.Event
import com.mutant.godutch.model.Group
import kotlinx.android.synthetic.main.activity_events.*

class EventsActivity : BaseActivity() {

    private lateinit var mGroup: Group

    internal var mAdapterEventCardView: AdapterEventCard? = null
    internal var mAdapterEventList: AdapterEventList? = null
    private var mDatabaseEvents: DatabaseReference? = null

    companion object {

        private val BUNDLE_KEY_GROUP = "BUNDLE_KEY_GROUP"

        fun getIntent(activity: Activity, group: Group): Intent {
            val intent = Intent(activity, EventsActivity::class.java)
            intent.putExtra(BUNDLE_KEY_GROUP, group)
            return intent
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_events, menu)
        return true
    }

    private val VIEW_FLIPPER_LIST = 0
    private val VIEW_FLIPPER_CARD_VIEW = 1

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_list -> {
                if (viewFlipper.displayedChild == VIEW_FLIPPER_CARD_VIEW) {
                    viewFlipper.displayedChild = VIEW_FLIPPER_LIST
                    item.icon = resources.getDrawable(R.drawable.ic_view_agenda_white_24dp, null)
                } else {
                    viewFlipper.displayedChild = VIEW_FLIPPER_CARD_VIEW
                    item.icon = resources.getDrawable(R.drawable.ic_format_list_bulleted_white_24dp, null)
                }
            }
            R.id.action_list_check -> check()
        }

        return super.onOptionsItemSelected(item)
    }

    override val layoutId: Int
        get() = R.layout.activity_events

    private fun check() {
        startActivity(CheckActivity.getIntent(this, mAdapterEventCardView?.getEvents))
    }

    override fun setup() {
        mGroup = intent.getParcelableExtra(BUNDLE_KEY_GROUP)
        title = mGroup.title
        setupFireBase()
        setupEventsCard()
        setupEventsList()
        setupFabNewEvent()
    }

    private fun setupFireBase() {
        mDatabaseEvents = FirebaseDatabase.getInstance().reference.child("events").child(mGroup.key)
        if (mDatabaseEvents != null) {
            mDatabaseEvents?.orderByKey()?.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    var event = dataSnapshot.getValue(Event::class.java)
                    event.key = dataSnapshot.key
                    mAdapterEventCardView?.addItem(event)
                    mAdapterEventList?.addItem(event)
                    recycler_view_event_card.scrollToPosition(0)
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

    private fun setupEventsCard() {
        mAdapterEventCardView = AdapterEventCard(this@EventsActivity, ArrayList<Event>(), mGroup, mDatabaseEvents)
        val myLayoutManager = LinearLayoutManager(this)
        myLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recycler_view_event_card.adapter = mAdapterEventCardView
        recycler_view_event_card.layoutManager = myLayoutManager
    }

    private fun setupEventsList() {
        mAdapterEventList = AdapterEventList(this@EventsActivity, ArrayList<Event>(), mGroup, mDatabaseEvents)
        val myLayoutManager = LinearLayoutManager(this)
        myLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recycler_view_event_list.layoutManager = myLayoutManager
        recycler_view_event_list.adapter = mAdapterEventList
        val dividerItemDecoration = DividerItemDecoration(this@EventsActivity, myLayoutManager.orientation)
        recycler_view_event_list.addItemDecoration(dividerItemDecoration)
    }

    private fun setupFabNewEvent() {
        fab_new_event.setOnClickListener { startActivity(NewEventActivity.getIntent(this@EventsActivity, mGroup)) }
    }

}
