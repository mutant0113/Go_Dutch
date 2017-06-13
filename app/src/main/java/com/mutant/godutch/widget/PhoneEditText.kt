package com.mutant.godutch.widget

import android.content.Context
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Patterns

import com.mutant.godutch.utils.Utility

class PhoneEditText : android.support.v7.widget.AppCompatEditText, CustomEditTextInterface {
    private val maxLength = 10

    constructor(context: Context) : super(context) {
        initializeSetting()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initializeSetting()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initializeSetting()
    }

    override fun initializeSetting() {
        this.inputType = InputType.TYPE_CLASS_PHONE
        Utility.setMaxLength(this, maxLength)
    }

    override val isValid: Boolean
        get() = Patterns.PHONE.matcher(this.text.toString().trim { it <= ' ' }).matches()

    val isEmpty: Boolean
        get() = TextUtils.isEmpty(this.text.toString().trim { it <= ' ' })

    override fun setError(error: CharSequence) {
        this.requestFocus()
        super.setError(error)
    }
}