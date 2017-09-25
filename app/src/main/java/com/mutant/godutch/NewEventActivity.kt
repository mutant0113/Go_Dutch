package com.mutant.godutch

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.mutant.godutch.fragment.NewEventStep1Fragment
import com.mutant.godutch.fragment.NewEventStep2Fragment
import com.mutant.godutch.fragment.NewEventStep3Fragment
import com.mutant.godutch.widget.EventTypeWidget.TYPE


class NewEventActivity : BaseActivity() {

    internal var mGroupId: String = ""
    internal var mGroupName: String = ""
    internal var mType: TYPE = TYPE.FOOD
    var isTakePhoto: Boolean = false

    val mNewEventStep1Fragment = NewEventStep1Fragment()
    val mNewEventStep2Fragment = NewEventStep2Fragment()
    val mNewEventStep3Fragment = NewEventStep3Fragment()

    companion object {

        val BUNDLE_KEY_GROUP_ID = "BUNDLE_KEY_GROUP_ID"
        val BUNDLE_KEY_GROUP_NAME = "BUNDLE_KEY_GROUP_NAME"
        val BUNDLE_KEY_EXCHANGE_RATE = "BUNDLE_KEY_EXCHANGE_RATE"
        val REQUEST_CODE_EXCHANGE_RATE = 1

        fun getIntent(activity: Activity, groupId: String, groupName: String): Intent {
            val intent = Intent(activity, NewEventActivity::class.java)
            intent.putExtra(BUNDLE_KEY_GROUP_ID, groupId)
            intent.putExtra(BUNDLE_KEY_GROUP_NAME, groupName)
            return intent
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_new_event

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_next -> nextStep()
            R.id.action_done -> mNewEventStep2Fragment.createNewEvent()
        }

        return super.onOptionsItemSelected(item)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setup() {
        setupBundle()
        setupFragments()
        setupMenu()
    }

    private fun setupMenu() {
        setupToolbar()
        mToolbar?.inflateMenu(R.menu.menu_next)
        mToolbar?.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.action_next -> nextStep()
                R.id.action_done -> mNewEventStep2Fragment.createNewEvent()
            }
            true
        }
    }

    private fun setupBundle() {
        mGroupId = intent.getStringExtra(BUNDLE_KEY_GROUP_ID)
        mGroupName = intent.getStringExtra(EventsActivity.BUNDLE_KEY_GROUP_NAME)
    }

    private fun setupFragments() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.linearLayout_container, mNewEventStep1Fragment)
        ft.commit()
    }

//    private fun setupFragments() {
//        val ft = supportFragmentManager.beginTransaction()
//        ft.replace(R.id.coordinatorLayout_container, mNewEventStep3Fragment)
//        ft.commit()
//    }

    private fun nextStep() {
        // hide keyboard
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)

        // replace view by fragment step 2
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        ft.replace(R.id.linearLayout_container, mNewEventStep2Fragment)
        ft.commit()
    }

    fun preStep() {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
        ft.replace(R.id.linearLayout_container, mNewEventStep3Fragment)
        ft.commit()
    }

    fun onTaxButtonClicked(view: View) {
        mNewEventStep1Fragment.onTaxButtonClicked(view)
    }

}
