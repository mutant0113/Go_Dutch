package com.mutant.godutch

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.activity_dept.*
import java.util.*


/**
 * Created by evanfang102 on 2017/9/12.
 * 選擇誰先墊錢以及誰要拆帳的頁面
 */
class DeptActivity : BaseActivity() {

    private var mTotal: Double = 0.0
    private lateinit var mExchangeRate: ExchangeRate
    private lateinit var mAllUserDept: ArrayList<Friend>
    private var mMode = MODE_EVENLY

    companion object {
        const val MODE_EVENLY = 0
        const val MODE_MANUALLY = 1

        const val BUNDLE_KEY_TOTAL = "BUNDLE_KEY_TOTAL"
        const val BUNDLE_KEY_EXCHANGE_RATE = "BUNDLE_KEY_EXCHANGE_RATE"
        const val BUNDLE_KEY_FRIENDS_DEPT = "BUNDLE_KEY_FRIENDS_DEPT"

        fun getIntent(activity: Activity, total: Double, exchangeRate: ExchangeRate, friendsDept: ArrayList<Friend>): Intent {
            val intent = Intent(activity, DeptActivity::class.java)
            intent.putExtra(BUNDLE_KEY_TOTAL, total)
            intent.putExtra(BUNDLE_KEY_EXCHANGE_RATE, exchangeRate)
            intent.putParcelableArrayListExtra(BUNDLE_KEY_FRIENDS_DEPT, friendsDept)
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
        if (mMode == MODE_EVENLY) {
            data.putExtra(BUNDLE_KEY_FRIENDS_DEPT, (recyclerView_evenly.adapter as AdapterEvenly).getFriendsPaid())
        } else if (mMode == MODE_MANUALLY) {
            data.putExtra(BUNDLE_KEY_FRIENDS_DEPT, (recyclerView_manually.adapter as AdapterManually).getFriendsPaid())
        }

        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override val layoutId: Int
        get() = R.layout.activity_dept

    fun onClickShareLeft(v: View) {
        val numberOfPeoplePaid = (recyclerView_evenly.adapter as AdapterEvenly).getNumberOfPeoplePaid()
        if (numberOfPeoplePaid == 0) {
            Snackbar.make(constraint_parent, "請勾選至少一個人", Snackbar.LENGTH_SHORT).show()
        } else {
            val sharedLeft = getMoneyLeft() / numberOfPeoplePaid
            (0 until recyclerView_evenly.childCount).forEach {
                val editText = recyclerView_evenly.getChildAt(it).findViewById(R.id.editText_debt) as AppCompatEditText
                val originDept = if (!editText.text.isNullOrBlank()) editText.text.toString().toDouble() else 0.0
                val sum = sharedLeft + originDept

                val checkBox = recyclerView_evenly.getChildAt(it).findViewById(R.id.checkBox) as CheckBox
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
        onClickSharedMode(button_evenly)
    }

    private fun setupBundle() {
        mTotal = intent.getDoubleExtra(BUNDLE_KEY_TOTAL, 0.0)
        mExchangeRate = intent.getParcelableExtra(BUNDLE_KEY_EXCHANGE_RATE)
        mAllUserDept = intent.getParcelableArrayListExtra(BUNDLE_KEY_FRIENDS_DEPT)
    }

    private fun setupPaidFirst() {
        var allUsersPaidEvenly = arrayListOf<Friend>()
        mAllUserDept.forEach { allUsersPaidEvenly.add(Friend(it)) }
        recyclerView_evenly.layoutManager = LinearLayoutManager(this@DeptActivity)
        recyclerView_evenly.adapter = AdapterEvenly(allUsersPaidEvenly)

        var allUsersPaidManually = arrayListOf<Friend>()
        mAllUserDept.forEach { allUsersPaidManually.add(Friend(it)) }
        recyclerView_manually.layoutManager = LinearLayoutManager(this@DeptActivity)
        recyclerView_manually.adapter = AdapterManually(allUsersPaidManually)
    }

    fun onClickSharedMode(v: View) {
        setMode(MODE_EVENLY)
    }

    fun onClickCustomMode(v: View) {
        setMode(MODE_MANUALLY)
    }

    fun setMode(mode: Int) {
        mMode = mode
        viewFlipper.displayedChild = mMode

        button_evenly.isSelected = mode == MODE_EVENLY
        button_manually.isSelected = mode == MODE_MANUALLY
    }

    inner class AdapterEvenly(var allUserDept: ArrayList<Friend>) : RecyclerView.Adapter<ViewHolderPaidEvenly>() {

        private var checkedPos = BooleanArray(allUserDept.size, { false })

        init {
            allUserDept.filter { it.debt != 0.0 }.map { checkedPos[allUserDept.indexOf(it)] = true }
        }

        override fun getItemCount(): Int = allUserDept.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPaidEvenly {
            return ViewHolderPaidEvenly(LayoutInflater.from(parent.context).inflate(R.layout.list_item_evenly, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolderPaidEvenly, position: Int) {
            val friend = allUserDept[position]
            Glide.with(this@DeptActivity).load(friend.photoUrl).error(R.drawable.profile_pic).into(holder.mImageViewPhotoUrl)
            holder.mTextViewName.text = friend.name
            holder.mTextViewDept.text = if (friend.debt != 0.0) friend.debt.toString() else ""

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

        }

        private fun evenlyShared() {
            val numberOfPeoplePaid = getNumberOfPeoplePaid()
            val moneyShared = if (numberOfPeoplePaid != 0) mTotal / numberOfPeoplePaid else 0.0
            allUserDept.forEach {
                it.debt = if (checkedPos[allUserDept.indexOf(it)]) moneyShared else 0.0
            }
        }

        fun getNumberOfPeoplePaid(): Int {
            var number = 0
            checkedPos.filter { it }.map { number++ }
            return number
        }

        fun getFriendsPaid(): ArrayList<Friend> {
            allUserDept.filter { it.debt == 0.0 }.map { allUserDept.remove(it) }
            return allUserDept
        }

    }

    inner class ViewHolderPaidEvenly(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageViewPhotoUrl: AppCompatImageView = itemView.findViewById(R.id.imageView_photo_url) as AppCompatImageView
        val mTextViewName: AppCompatTextView = itemView.findViewById(R.id.textView_name) as AppCompatTextView
        val mCheckBox: AppCompatCheckBox = itemView.findViewById(R.id.checkBox) as AppCompatCheckBox
        val mTextViewDept: AppCompatTextView = itemView.findViewById(R.id.textView_debt) as AppCompatTextView
    }

    inner class AdapterManually(var allUserDept: ArrayList<Friend>) : RecyclerView.Adapter<ViewHolderPaidManually>() {

        override fun getItemCount(): Int = allUserDept.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPaidManually {
            return ViewHolderPaidManually(LayoutInflater.from(parent.context).inflate(R.layout.list_item_friend_tick, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolderPaidManually, position: Int) {
            val friend = allUserDept[position]
            Glide.with(this@DeptActivity).load(friend.photoUrl).error(R.drawable.profile_pic).into(holder.mImageViewPhotoUrl)
            holder.mTextViewName.text = friend.name
            holder.mEditTextDept.visibility = View.VISIBLE
            holder.mEditTextDept.setText(if (friend.debt != 0.0) friend.debt.toString() else "")

            holder.mCheckBox.visibility = View.INVISIBLE

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
            (0 until recyclerView_evenly.childCount).forEach {
                val editText = recyclerView_manually.getChildAt(it).findViewById(R.id.editText_debt) as AppCompatEditText
                if (!editText.text.isNullOrBlank()) {
                    val dept = editText.text.toString().toDouble()
                    allUserDept[it].debt = dept
                    inputTotal += dept
                }
            }

            setTextLeft(mTotal - inputTotal)
        }

        @SuppressLint("SetTextI18n")
        private fun setTextLeft(left: Double) {
            textView_left.text = "$$left"
        }

        fun getFriendsPaid(): ArrayList<Friend> {
            calculateLeft()
            allUserDept.filter { it.debt == 0.0 }.map { allUserDept.remove(it) }
            return allUserDept
        }

    }

    inner class ViewHolderPaidManually(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageViewPhotoUrl: AppCompatImageView = itemView.findViewById(R.id.imageView_photo_url) as AppCompatImageView
        val mTextViewName: AppCompatTextView = itemView.findViewById(R.id.textView_name) as AppCompatTextView
        val mCheckBox: AppCompatCheckBox = itemView.findViewById(R.id.checkBox) as AppCompatCheckBox
        val mEditTextDept: AppCompatEditText = itemView.findViewById(R.id.editText_debt) as AppCompatEditText
    }
}
