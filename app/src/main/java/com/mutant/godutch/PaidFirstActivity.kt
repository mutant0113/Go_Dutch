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
import kotlinx.android.synthetic.main.activity_paid_first.*
import java.util.*


/**
 * Created by evanfang102 on 2017/9/12.
 * 選擇誰先墊錢的頁面
 */
class PaidFirstActivity : BaseActivity() {

    private var mTotal: Double = 0.0
    private lateinit var mExchangeRate: ExchangeRate
    private lateinit var mAllUserPaid: ArrayList<Friend>
    private var mMode = MODE_PAID_EVENLY

    companion object {
        const val MODE_PAID_EVENLY = 0
        const val MODE_PAID_MANUALLY = 1

        const val BUNDLE_KEY_TOTAL = "BUNDLE_KEY_TOTAL"
        const val BUNDLE_KEY_EXCHANGE_RATE = "BUNDLE_KEY_EXCHANGE_RATE"
        const val BUNDLE_KEY_FRIENDS_PAID = "BUNDLE_KEY_FRIENDS_PAID"

        fun getIntent(activity: Activity, total: Double, exchangeRate: ExchangeRate, friendsPaid: ArrayList<Friend>): Intent {
            val intent = Intent(activity, PaidFirstActivity::class.java)
            intent.putExtra(BUNDLE_KEY_TOTAL, total)
            intent.putExtra(BUNDLE_KEY_EXCHANGE_RATE, exchangeRate)
            intent.putParcelableArrayListExtra(BUNDLE_KEY_FRIENDS_PAID, friendsPaid)
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
        if (mMode == MODE_PAID_EVENLY) {
            data.putExtra(BUNDLE_KEY_FRIENDS_PAID, (recyclerView_paid_evenly.adapter as AdapterPaidEvenly).getFriendsPaid())
        } else if (mMode == MODE_PAID_MANUALLY) {
            data.putExtra(BUNDLE_KEY_FRIENDS_PAID, (recyclerView_paid_manually.adapter as AdapterPaidManually).getFriendsPaid())
        }

        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override val layoutId: Int
        get() = R.layout.activity_paid_first

    fun onClickShareLeft(v: View) {
        val numberOfPeoplePaid = (recyclerView_paid_evenly.adapter as AdapterPaidEvenly).getNumberOfPeoplePaid()
        if (numberOfPeoplePaid == 0) {
            Snackbar.make(constraint_parent, "請勾選至少一個人", Snackbar.LENGTH_SHORT).show()
        } else {
            val sharedLeft = getMoneyLeft() / numberOfPeoplePaid
            (0 until recyclerView_paid_evenly.childCount).forEach {
                val editText = recyclerView_paid_evenly.getChildAt(it).findViewById(R.id.editText_debt) as AppCompatEditText
                val originDept = if (!editText.text.isNullOrBlank()) editText.text.toString().toDouble() else 0.0
                val sum = sharedLeft + originDept

                val checkBox = recyclerView_paid_evenly.getChildAt(it).findViewById(R.id.checkBox) as CheckBox
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
        mAllUserPaid = intent.getParcelableArrayListExtra(BUNDLE_KEY_FRIENDS_PAID)

        Collections.sort(mAllUserPaid) { o1, o2 -> o1?.uid!!.compareTo(o2?.uid!!) }
        mAllUserPaid.filter { it.uid == me.uid }.map {
            mAllUserPaid.remove(it)
            mAllUserPaid.add(0, it)
        }

    }

    private fun setupPaidFirst() {
        var allUsersPaidEvenly = arrayListOf<Friend>()
        mAllUserPaid.forEach { allUsersPaidEvenly.add(Friend(it)) }
        recyclerView_paid_evenly.layoutManager = LinearLayoutManager(this@PaidFirstActivity)
        recyclerView_paid_evenly.adapter = AdapterPaidEvenly(allUsersPaidEvenly)

        var allUsersPaidManually = arrayListOf<Friend>()
        mAllUserPaid.forEach { allUsersPaidManually.add(Friend(it)) }
        recyclerView_paid_manually.layoutManager = LinearLayoutManager(this@PaidFirstActivity)
        recyclerView_paid_manually.adapter = AdapterPaidManually(allUsersPaidManually)
    }

    fun onClickSharedMode(v: View) {
        setMode(MODE_PAID_EVENLY)
    }

    fun onClickCustomMode(v: View) {
        setMode(MODE_PAID_MANUALLY)
    }

    fun setMode(mode: Int) {
        mMode = mode
        viewFlipper.displayedChild = mMode

        button_evenly.isSelected = mode == MODE_PAID_EVENLY
        button_manually.isSelected = mode == MODE_PAID_MANUALLY
    }

    inner class AdapterPaidEvenly(var allUserPaid: ArrayList<Friend>) : RecyclerView.Adapter<ViewHolderPaidEvenly>() {

        private var checkedPos = BooleanArray(allUserPaid.size, { false })

        init {
            allUserPaid.filter { it.debt != 0.0 }.map { checkedPos[allUserPaid.indexOf(it)] = true }
        }

        override fun getItemCount(): Int = allUserPaid.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPaidEvenly {
            return ViewHolderPaidEvenly(LayoutInflater.from(parent.context).inflate(R.layout.list_item_paid_evenly, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolderPaidEvenly, position: Int) {
            val friend = allUserPaid[position]
            Glide.with(this@PaidFirstActivity).load(friend.photoUrl).error(R.drawable.profile_pic).into(holder.mImageViewPhotoUrl)
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
            allUserPaid.forEach {
                it.debt = if (checkedPos[allUserPaid.indexOf(it)]) moneyShared else 0.0
            }
        }

        fun getNumberOfPeoplePaid(): Int {
            var number = 0
            checkedPos.filter { it }.map { number++ }
            return number
        }

        fun getFriendsPaid(): ArrayList<Friend> {
            allUserPaid.filter { it.debt == 0.0 }.map { allUserPaid.remove(it) }
            return allUserPaid
        }

    }

    inner class ViewHolderPaidEvenly(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageViewPhotoUrl: AppCompatImageView = itemView.findViewById(R.id.imageView_photo_url) as AppCompatImageView
        val mTextViewName: AppCompatTextView = itemView.findViewById(R.id.textView_name) as AppCompatTextView
        val mCheckBox: AppCompatCheckBox = itemView.findViewById(R.id.checkBox) as AppCompatCheckBox
        val mTextViewDept: AppCompatTextView = itemView.findViewById(R.id.textView_debt) as AppCompatTextView
    }

    inner class AdapterPaidManually(var allUserPaid: ArrayList<Friend>) : RecyclerView.Adapter<ViewHolderPaidManually>() {

        override fun getItemCount(): Int = allUserPaid.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPaidManually {
            return ViewHolderPaidManually(LayoutInflater.from(parent.context).inflate(R.layout.list_item_friend_tick, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolderPaidManually, position: Int) {
            val friend = allUserPaid[position]
            Glide.with(this@PaidFirstActivity).load(friend.photoUrl).error(R.drawable.profile_pic).into(holder.mImageViewPhotoUrl)
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
            (0 until recyclerView_paid_evenly.childCount).forEach {
                val editText = recyclerView_paid_manually.getChildAt(it).findViewById(R.id.editText_debt) as AppCompatEditText
                if (!editText.text.isNullOrBlank()) {
                    val dept = editText.text.toString().toDouble()
                    allUserPaid[it].debt = dept
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
            allUserPaid.filter { it.debt == 0.0 }.map { allUserPaid.remove(it) }
            return allUserPaid
        }

    }

    inner class ViewHolderPaidManually(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageViewPhotoUrl: AppCompatImageView = itemView.findViewById(R.id.imageView_photo_url) as AppCompatImageView
        val mTextViewName: AppCompatTextView = itemView.findViewById(R.id.textView_name) as AppCompatTextView
        val mCheckBox: AppCompatCheckBox = itemView.findViewById(R.id.checkBox) as AppCompatCheckBox
        val mEditTextDept: AppCompatEditText = itemView.findViewById(R.id.editText_debt) as AppCompatEditText
    }
}
