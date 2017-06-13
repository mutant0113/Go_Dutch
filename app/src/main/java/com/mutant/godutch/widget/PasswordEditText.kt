package com.mutant.godutch.widget

import android.content.Context
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet

import com.mutant.godutch.utils.Patterns
import com.mutant.godutch.utils.Utility

class PasswordEditText : android.support.v7.widget.AppCompatEditText, CustomEditTextInterface {
    private val maxLength = 20

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
        this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        Utility.setMaxLength(this, maxLength)
    }

    override val isValid: Boolean
        get() = Patterns.PASSWORD.matcher(this.text.toString()).matches()

    val isEmpty: Boolean
        get() = TextUtils.isEmpty(this.text.toString())

    override fun setError(error: CharSequence) {
        this.requestFocus()
        super.setError(error)
    }
}