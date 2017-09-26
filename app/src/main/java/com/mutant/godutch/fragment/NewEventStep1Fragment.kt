package com.mutant.godutch.fragment

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.mutant.godutch.ExchangeRateActivity
import com.mutant.godutch.NewEventActivity
import com.mutant.godutch.R
import com.mutant.godutch.model.ExchangeRate
import kotlinx.android.synthetic.main.fragment_new_event_step_1.*
import java.text.DecimalFormat


/**
 * Created by evanfang102 on 2017/9/20.
 */
class NewEventStep1Fragment : Fragment() {

    lateinit var mActivity: NewEventActivity
    lateinit var rootView: View
    var mExchangeRate: ExchangeRate
    var mTax: Double = 0.0
    var mTotal: Double = 0.0

    init {
        // TODO depend on country
        val jsonKey = "TWD"
        val rate = 1.0
        val lastUpdated = ""
        val country = "新台幣"
        mExchangeRate = ExchangeRate(jsonKey, rate, lastUpdated, country)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_next, menu)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        mActivity = (activity as NewEventActivity)
        rootView = inflater!!.inflate(R.layout.fragment_new_event_step_1, container, false)
//        mActivity.setupToolbar(rootView)
        return rootView
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity.mToolbar?.title = "[${mActivity.mGroup.title}]輸入帳目"

        showKeyboard()
        updateUiBySubTotal()
        selectCurrency()
    }

    private fun selectCurrency() {
        button_currency.setOnClickListener({
            startActivityForResult(Intent(activity, ExchangeRateActivity::class.java),
                    NewEventActivity.REQUEST_CODE_EXCHANGE_RATE)
        })
    }

    private fun updateUiBySubTotal() {
        editText_subtotal.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (mTax == 0.0) run {
                    button_tax_0.isSelected = true
                }
                updateTotal()
            }
        })
    }

    private fun showKeyboard() {
        val imm = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        editText_title.requestFocus()
        imm.showSoftInput(editText_subtotal, InputMethodManager.SHOW_FORCED)
    }

    fun onTaxButtonClicked(view: View) {
        val tax = view.tag.toString().toDouble()
        button_tax_0.isSelected = false
        button_tax_10.isSelected = false
        when (tax) {
            0.0 -> {
                button_tax_0.isSelected = true
                mTax = 0.0
            }
            10.0 -> {
                button_tax_10.isSelected = true
                mTax = 10.0
            }
        }
        updateTotal()
    }

    fun updateTotal() {
        val subtotal = editText_subtotal.text.toString()
        if (subtotal.isEmpty()) {
            textView_total.text = "$0"
        } else {
            mTotal = Math.round(subtotal.toDouble() * ((100 + mTax) / 100)).toDouble()
            val decimalFormat = DecimalFormat("${mExchangeRate.jsonKey} #,###")
            textView_total.text = decimalFormat.format(mTotal.toDouble())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NewEventActivity.REQUEST_CODE_EXCHANGE_RATE && resultCode == Activity.RESULT_OK) {
            mExchangeRate = data.getParcelableExtra(NewEventActivity.BUNDLE_KEY_EXCHANGE_RATE)
            button_currency.text = mExchangeRate.country
            updateTotal()
        }
    }

}