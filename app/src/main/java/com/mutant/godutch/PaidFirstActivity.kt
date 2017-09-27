package com.mutant.godutch

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v7.widget.*
import android.view.*
import android.widget.CheckBox
import android.widget.CompoundButton
import com.bumptech.glide.Glide
import com.mutant.godutch.model.ExchangeRate
import com.mutant.godutch.model.Friend
import kotlinx.android.synthetic.main.activity_paid_first.*
import kotlinx.android.synthetic.main.list_item_friend_tick.view.*
import java.util.*


/**
 * Created by evanfang102 on 2017/9/12.
 * 選擇誰先墊錢的頁面
 */
class PaidFirstActivity : BaseActivity() {

    private var mTotal: Double = 0.0
    private lateinit var mExchangeRate: ExchangeRate
    private lateinit var mFriends: ArrayList<Friend>

    companion object {

        private val BUNDLE_KEY_TOTAL = "BUNDLE_KEY_TOTAL"
        private val BUNDLE_KEY_EXCHANGE_RATE = "BUNDLE_KEY_EXCHANGE_RATE"
        private val BUNDLE_KEY_FRIENDS = "BUNDLE_KEY_FRIENDS"

        fun getIntent(activity: Activity, total: Double, exchangeRate: ExchangeRate, friends: ArrayList<Friend>): Intent {
            val intent = Intent(activity, PaidFirstActivity::class.java)
            intent.putExtra(BUNDLE_KEY_TOTAL, total)
            intent.putExtra(BUNDLE_KEY_EXCHANGE_RATE, exchangeRate)
            intent.putParcelableArrayListExtra(BUNDLE_KEY_FRIENDS, friends)
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
        // TODO filter friends who have dept
//        data.putExtra(BUNDLE_KEY_FRIENDS_PAID, mFriendsPaid)
        setResult(Activity.RESULT_OK)
        finish()
    }

    override val layoutId: Int
        get() = R.layout.activity_paid_first

    fun onClickShareLeft(v: View) {
        val numberOfPeoplePaid = (recyclerView_paid.adapter as AdapterFriendsTick).getNumberOfPeoplePaid()
        if (numberOfPeoplePaid == 0) {
            Snackbar.make(constraint_parent, "請勾選至少一個人", Snackbar.LENGTH_SHORT).show()
        } else {
            val sharedLeft = getMoneyLeft() / numberOfPeoplePaid
            (0 until recyclerView_paid.childCount).forEach {
                val editText = recyclerView_paid.getChildAt(it).findViewById(R.id.editText_debt) as AppCompatEditText
                val originDept = if (!editText.text.isNullOrBlank()) editText.text.toString().toDouble() else 0.0
                val sum = sharedLeft + originDept

                val checkBox = recyclerView_paid.getChildAt(it).findViewById(R.id.checkBox) as CheckBox
                if (checkBox.isChecked) {
                    editText.setText("$sum")
                }
            }
            textView_left.text = "$0"
        }
    }

    private fun getMoneyLeft(): Double {
        return textView_left.text.removePrefix("$").toString().toDouble()
    }

    override fun setup() {
        setupBundle()
        setupPaidFirst()
    }

    private fun setupBundle() {
        mTotal = intent.getDoubleExtra(BUNDLE_KEY_TOTAL, 0.0)
        mExchangeRate = intent.getParcelableExtra(BUNDLE_KEY_EXCHANGE_RATE)
        mFriends = intent.getParcelableArrayListExtra(BUNDLE_KEY_FRIENDS)
    }

    private fun setupPaidFirst() {
        recyclerView_paid.layoutManager = LinearLayoutManager(this@PaidFirstActivity)
        recyclerView_paid.adapter = AdapterFriendsTick()
    }

    inner class AdapterFriendsTick : RecyclerView.Adapter<ViewHolder>() {

        private var checkedPos = BooleanArray(mFriends.size, { false })
        private var mLastEditMoney = 0.0

        init {
            mFriends.filter { it.debt != 0.0 }.map { checkedPos[mFriends.indexOf(it)] = true }

            // TODO 自己要拉到第一個
            Collections.sort(mFriends) { o1, o2 -> o1?.uid!!.compareTo(o2?.uid!!) }
            if (mFriends.remove(me)) {
                mFriends.add(0, me)
            }
        }

        override fun getItemCount(): Int = mFriends.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_friend_tick, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val friend = mFriends[position]
            Glide.with(this@PaidFirstActivity).load(friend.photoUrl).error(R.drawable.profile_pic).into(holder.mImageViewPhotoUrl)
            holder.mTextViewName.text = friend.name
            holder.mEditTextDept.visibility = View.VISIBLE
            holder.mEditTextDept.setText(if (friend.debt != 0.0) friend.debt.toString() else "")

            holder.mCheckBox.setOnCheckedChangeListener(null)
            holder.mCheckBox.isChecked = checkedPos[position]

            holder.itemView.setOnClickListener({
                holder.mCheckBox.isChecked = !holder.mCheckBox.isChecked
            })

            holder.mCheckBox.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                checkedPos[position] = isChecked
                evenlyShared()
                notifyDataSetChanged()
            }

            holder.mEditTextDept.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    val editText = v as AppCompatEditText
                    editText.setText("")
                    calculateLeft()
                }
            }
        }

        private fun calculateLeft() {
            var inputTotal = 0.0
            (0 until recyclerView_paid.childCount).forEach {
                val editText = recyclerView_paid.getChildAt(it).findViewById(R.id.editText_debt) as AppCompatEditText
                if (!editText.text.isNullOrBlank()) {
                    inputTotal += editText.text.toString().toDouble()
                }
            }

            setTextLeft(mTotal - inputTotal)
        }

        private fun setTextLeft(left: Double) {
            textView_left.text = "$$left"
        }

        private fun evenlyShared() {
            val numberOfPeoplePaid = getNumberOfPeoplePaid()
            val moneyShared = if (numberOfPeoplePaid != 0) mTotal / numberOfPeoplePaid else 0.0
            mFriends.forEach {
                it.debt = if (checkedPos[mFriends.indexOf(it)]) moneyShared else 0.0
            }
        }

        fun getNumberOfPeoplePaid(): Int {
            var number = 0
            checkedPos.filter { it }.map { number++ }
            return number
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageViewPhotoUrl: AppCompatImageView = itemView.imageView_photo_url
        val mTextViewName: AppCompatTextView = itemView.textView_name
        val mCheckBox: AppCompatCheckBox = itemView.checkBox
        val mEditTextDept: AppCompatEditText = itemView.editText_debt
    }

}
