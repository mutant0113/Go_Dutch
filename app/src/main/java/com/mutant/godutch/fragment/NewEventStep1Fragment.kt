package com.mutant.godutch.fragment

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
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
    var mTax: Int = 0
    var mTotal: Int = 0

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
        return rootView
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun onTaxButtonClicked(view: View) {
        val tax = view.tag.toString().toDouble()
        mTotal = Math.round(editText_subtotal.text.toString().toDouble() * ((100 + tax) / 100)).toInt()
        val decimalFormat = DecimalFormat("$#,###")
        textView_total.text = decimalFormat.format(mTotal.toDouble())

        button_tax_0.isSelected = false
        button_tax_5.isSelected = false
        button_tax_10.isSelected = false
        button_tax_15.isSelected = false
        when (tax) {
            0.0 -> {
                button_tax_0.isSelected = true
                mTax = 0
            }
            5.0 -> {
                button_tax_5.isSelected = true
                mTax = 5
            }
            10.0 -> {
                button_tax_10.isSelected = true
                mTax = 10
            }
            15.0 -> {
                button_tax_15.isSelected = true
                mTax = 15
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NewEventActivity.REQUEST_CODE_EXCHANGE_RATE && resultCode == Activity.RESULT_OK) {
            mExchangeRate = data.getParcelableExtra(NewEventActivity.BUNDLE_KEY_EXCHANGE_RATE)
            button_currency.text = mExchangeRate?.country
        }
    }

}