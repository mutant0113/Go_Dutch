package com.mutant.godutch

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.mutant.godutch.model.ExchangeRate
import com.mutant.godutch.model.Friend
import kotlinx.android.synthetic.main.activity_shared.*

/**
 * Created by evanfang102 on 2017/9/12.
 * 選擇誰要拆帳的頁面
 */
class SharedActivity : BaseActivity() {

    var mTotal : Int = 0
    lateinit var mExchangeRate: ExchangeRate
    lateinit var mFriends: ArrayList<Friend>

    companion object {

        private val BUNDLE_KEY_TOTAL = "BUNDLE_KEY_TOTAL"
        private val BUNDLE_KEY_EXCHANGE_RATE = "BUNDLE_KEY_EXCHANGE_RATE"
        private val BUNDLE_KEY_FRIENDS = "BUNDLE_KEY_FRIENDS"

        fun getIntent(activity: Activity, total: Int, exchangeRate: ExchangeRate, friendsShared: ArrayList<Friend>): Intent {
            val intent = Intent(activity, PaidFirstActivity::class.java)
            intent.putExtra(BUNDLE_KEY_TOTAL, total)
            intent.putExtra(BUNDLE_KEY_EXCHANGE_RATE, exchangeRate)
            intent.putParcelableArrayListExtra(BUNDLE_KEY_FRIENDS, friendsShared)
            return intent
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_done, menu)
        return true
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_done -> done()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun done() {
        var data = Intent()
        data.putExtra(SharedActivity.BUNDLE_KEY_FRIENDS, mFriends)
        setResult(Activity.RESULT_OK)
        finish()
    }

    override val layoutId: Int
        get() = R.layout.activity_shared

    override fun setup() {
        setupBundle()
        setupShared()
    }

    private fun setupBundle() {
        mTotal = intent.getIntExtra(BUNDLE_KEY_TOTAL, 0)
        mExchangeRate = intent.getParcelableExtra(BUNDLE_KEY_EXCHANGE_RATE)
        mFriends = intent.getParcelableArrayListExtra(BUNDLE_KEY_FRIENDS)
    }

    private fun setupShared() {
        recyclerView_shared.layoutManager = LinearLayoutManager(this@SharedActivity)
        recyclerView_shared.adapter = AdapterPaidCheck(this@SharedActivity, mFriends, mExchangeRate, null)
    }

}
