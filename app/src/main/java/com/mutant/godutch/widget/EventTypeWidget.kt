package com.mutant.godutch.widget

import android.content.Context
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.View
import com.mutant.godutch.R
import kotlinx.android.synthetic.main.widget_event_type.view.*

/**
 * Created by evanfang102 on 2017/7/27.
 */
class EventTypeWidget : LinearLayoutCompat {

    enum class TYPE {
        FOOD,
        SHOPPING,
        HOTEL,
        TICKET
    }

    var mType = TYPE.FOOD
    companion object {
        interface OnSelectionChanged {
            fun onChanged(type: TYPE)
        }
    }

    var onSelectionChanged: OnSelectionChanged? = null

    constructor(context: Context) : super(context) {
        initializeSetting(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initializeSetting(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initializeSetting(context)
    }

    private fun initializeSetting(context: Context) {
        inflate(context, R.layout.widget_event_type, this)
        setLayoutSelected(this.linearLayout_type_food, TYPE.FOOD)
        this.linearLayout_type_food.setOnClickListener {
            setLayoutSelected(it, TYPE.FOOD)
        }
        this.linearLayout_type_shopping.setOnClickListener {
            setLayoutSelected(it, TYPE.SHOPPING)
        }
        this.linearLayout_type_hotel.setOnClickListener {
            setLayoutSelected(it, TYPE.HOTEL)
        }
        this.linearLayout_type_ticket.setOnClickListener {
            setLayoutSelected(it, TYPE.TICKET)
        }
    }

    private fun setLayoutSelected(view: View, type: TYPE) {
        this.linearLayout_type_food.isSelected = false
        this.linearLayout_type_shopping.isSelected = false
        this.linearLayout_type_hotel.isSelected = false
        this.linearLayout_type_ticket.isSelected = false
        (view as LinearLayoutCompat).isSelected = true
        mType = type
        onSelectionChanged?.onChanged(mType)
    }

    fun getType(): TYPE {
        return mType
    }
}