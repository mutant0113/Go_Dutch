package com.mutant.godutch

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.*
import com.bumptech.glide.Glide
import com.mutant.godutch.model.Event
import com.mutant.godutch.model.Friend
import com.mutant.godutch.utils.Utility
import kotlinx.android.synthetic.main.activity_event_detail.*
import kotlinx.android.synthetic.main.fragment_event_detail.view.*

class EventDetailActivity : BaseActivity() {

    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter
    private lateinit var mEvents: ArrayList<Event>
    private var mInitPos: Int = 0

    companion object {

        private val BUNDLE_KEY_EVENTS = "BUNDLE_KEY_EVENTS"
        private val BUNDLE_KEY_INIT_POS = "BUNDLE_KEY_INIT_POS"

        fun getIntent(activity: Activity, events: ArrayList<Event>, initPos: Int): Intent {
            val intent = Intent(activity, EventDetailActivity::class.java)
            intent.putExtra(BUNDLE_KEY_EVENTS, events)
            intent.putExtra(BUNDLE_KEY_INIT_POS, initPos)
            return intent
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_event_detail

    override fun setup() {
        mEvents = intent.getParcelableArrayListExtra<Event>(BUNDLE_KEY_EVENTS)
        mInitPos = intent.getIntExtra(BUNDLE_KEY_INIT_POS, 0)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        // Set up the ViewPager with the sections adapter.
        container!!.adapter = mSectionsPagerAdapter
        container.currentItem = mInitPos
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_event_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        lateinit var mEvent: Event

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            mEvent = arguments.getParcelable(BUNDLE_KEY_EVENT)
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater!!.inflate(R.layout.fragment_event_detail, container, false)
            setupToolbar(rootView)
            setupAppbarOffsetChanged(rootView)
            setupFabType(rootView, mEvent.type)
            if (!TextUtils.isEmpty(mEvent.photoUrl)) {
                Glide.with(context).load(mEvent.photoUrl).error(R.drawable.food_default_640).into(rootView.imageView_photo)
            }
            rootView.textView_date.text = Utility.getRelativeTimeSpanDate(mEvent.timestampCreated)
            rootView.textView_description.text = mEvent.description
            setupFriendsWhoPaidFirst(rootView)
            setupFriendsShared(rootView)
            return rootView
        }

        private fun setupFriendsWhoPaidFirst(rootView: View) {
            val MyLayoutManager = LinearLayoutManager(activity)
            rootView.recycler_view_friends_who_paid_first.adapter = AdapterPaidCheck(activity, arrayListOf(mEvent.friendWhoPaidFirst))
            rootView.recycler_view_friends_who_paid_first.layoutManager = MyLayoutManager
        }

        private fun setupFriendsShared(rootView: View) {
            val MyLayoutManager = LinearLayoutManager(activity)
            var friendsFilter: ArrayList<Friend> = arrayListOf()
            mEvent.friendsShared.filterTo(friendsFilter) { it.needToPay > 0 }
            rootView.recycler_view_friends_shared.adapter = AdapterPaidCheck(activity, friendsFilter)
            rootView.recycler_view_friends_shared.layoutManager = MyLayoutManager
        }

        private var state: CollapsingToolbarLayoutState? = null

        private enum class CollapsingToolbarLayoutState {
            EXPANDED,
            COLLAPSED,
            INTERMEDIATE
        }

        // TODO layout bug
        private fun setupAppbarOffsetChanged(rootView: View) {
            rootView.app_bar_layout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                if (verticalOffset == 0) {
                    if (state != CollapsingToolbarLayoutState.EXPANDED) {
                        state = CollapsingToolbarLayoutState.EXPANDED
//                    collapsing_toolbar.title = "EXPANDED"
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.totalScrollRange) {
                    if (state != CollapsingToolbarLayoutState.COLLAPSED) {
//                    collapsing_toolbar.title = ""
//                    playButton.setVisibility(View.VISIBLE)
                        state = CollapsingToolbarLayoutState.COLLAPSED
                    }
                } else {
                    if (state != CollapsingToolbarLayoutState.INTERMEDIATE) {
//                    if(state == CollapsingToolbarLayoutState.COLLAPSED){
//                        playButton.setVisibility(View.GONE);//由折叠变为中间状态时隐藏播放按钮
//                    }
//                    collapsing_toolbar.title = "INTERMEDIATE"
                        state = CollapsingToolbarLayoutState.INTERMEDIATE
                    }
                }
            }
        }

        private fun setupToolbar(rootView: View) {
            (activity as BaseActivity).setupToolbar(rootView.collapsing_toolbar_layout)
            rootView.collapsing_toolbar_layout.title = mEvent.title
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private fun setupFabType(rootView: View, type: NewEventActivity.Companion.TYPE?) {
            when (type) {
                NewEventActivity.Companion.TYPE.FOOD -> {
                    rootView.fab_event_type.setImageDrawable(activity.getDrawable(R.drawable.food_fork_drink))
                    rootView.imageView_photo.setImageResource(R.drawable.food_default_640)
                }
                NewEventActivity.Companion.TYPE.SHOPPING -> {
                    rootView.fab_event_type.setImageDrawable(activity.getDrawable(R.drawable.ic_shopping_cart_white_24dp))
                    rootView.imageView_photo.setImageResource(R.drawable.shopping_default_640)
                }
                NewEventActivity.Companion.TYPE.HOTEL -> {
                    rootView.fab_event_type.setImageDrawable(activity.getDrawable(R.drawable.ic_local_hotel_white_24dp))
                    rootView.imageView_photo.setImageResource(R.drawable.hotel_default_640)
                }
                NewEventActivity.Companion.TYPE.TICKET -> {
                    rootView.fab_event_type.setImageDrawable(activity.getDrawable(R.drawable.ticket))
                    rootView.imageView_photo.setImageResource(R.drawable.ticket_default_640)
                }
            }
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val BUNDLE_KEY_SECTION_NUMBER = "BUNDLE_KEY_SECTION_NUMBER"
            private val BUNDLE_KEY_EVENT = "BUNDLE_KEY_EVENT"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int, event: Event?): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(BUNDLE_KEY_SECTION_NUMBER, sectionNumber)
                args.putParcelable(BUNDLE_KEY_EVENT, event)
                fragment.arguments = args
                return fragment
            }
        }
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position, mEvents?.get(position))
        }

        override fun getCount(): Int {
            return mEvents.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mEvents[position].title
        }
    }
}
