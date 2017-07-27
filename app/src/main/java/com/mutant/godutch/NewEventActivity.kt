package com.mutant.godutch

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import com.mutant.godutch.fragment.NewEventStep1Fragment
import com.mutant.godutch.fragment.NewEventStep2Fragment
import com.mutant.godutch.widget.EventTypeWidget.TYPE
import kotlinx.android.synthetic.main.activity_new_event.*

class NewEventActivity : BaseActivity() {

    internal var mGroupId: String = ""
    internal var mGroupName: String = ""
    internal var mType: TYPE = TYPE.FOOD
    var isTakePhoto: Boolean = false

    val mNewEventStep1Fragment = NewEventStep1Fragment()
    val mNewEventStep2Fragment = NewEventStep2Fragment()

    companion object {
        val VIEWPAGER_STEP_1 = 0
        val VIEWPAGER_STEP_2 = 1

        val BUNDLE_KEY_GROUP_ID = "BUNDLE_KEY_GROUP_ID"
        val BUNDLE_KEY_GROUP_NAME = "BUNDLE_KEY_GROUP_NAME"

        fun getIntent(activity: Activity, groupId: String, groupName: String): Intent {
            val intent = Intent(activity, NewEventActivity::class.java)
            intent.putExtra(BUNDLE_KEY_GROUP_ID, groupId)
            intent.putExtra(BUNDLE_KEY_GROUP_NAME, groupName)
            return intent
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_new_event

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setup() {
        setupBundle()
        setupViewPager()
    }

    fun setupBundle() {
        mGroupId = intent.getStringExtra(BUNDLE_KEY_GROUP_ID)
        mGroupName = intent.getStringExtra(EventsActivity.BUNDLE_KEY_GROUP_NAME)
    }

    fun setupViewPager() {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(mNewEventStep1Fragment)
        viewPagerAdapter.addFragment(mNewEventStep2Fragment)
        viewPager.adapter = viewPagerAdapter
    }
}
