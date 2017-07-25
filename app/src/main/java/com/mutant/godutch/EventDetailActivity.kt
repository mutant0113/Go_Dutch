package com.mutant.godutch

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.text.TextUtils
import android.view.*
import com.bumptech.glide.Glide
import com.mutant.godutch.model.Event
import com.mutant.godutch.utils.Utility
import kotlinx.android.synthetic.main.activity_event_detail.*
import kotlinx.android.synthetic.main.fragment_event_detail.view.*

class EventDetailActivity : BaseActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = SectionsPagerAdapter(supportFragmentManager)
    private var mEvents: ArrayList<Event>? = null

    companion object {

        private val BUNDLE_KEY_EVENTS = "BUNDLE_KEY_EVENTS"

        fun getIntent(activity: Activity, events: ArrayList<Event>): Intent {
            val intent = Intent(activity, EventDetailActivity::class.java)
            intent.putExtra(BUNDLE_KEY_EVENTS, events)
            return intent
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_event_detail

    override fun setup() {
        mEvents = intent.getParcelableArrayListExtra<Event>(BUNDLE_KEY_EVENTS)

        // Set up the ViewPager with the sections adapter.
        container!!.adapter = mSectionsPagerAdapter
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
            setupFabType(rootView, mEvent.type)
            if(!TextUtils.isEmpty(mEvent.photoUrl)) {
                Glide.with(context).load(mEvent.photoUrl).error(R.drawable.food_default_640).into(rootView.imageView_photo)
            }
            rootView.textView_title.text = mEvent.title
            rootView.textView_date.text = Utility.getRelativeTimeSpanDate(mEvent.timestampCreated)
            rootView.textView_description.text = mEvent.description
            rootView.recycler_view_friends_who_paid_first
            rootView.recycler_view_friends_shared
            return rootView
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
            return PlaceholderFragment.newInstance(position + 1, mEvents?.get(position))
        }

        override fun getCount(): Int {
            return mEvents?.size ?: 0
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mEvents?.get(position)?.title ?: ""
        }
    }
}
