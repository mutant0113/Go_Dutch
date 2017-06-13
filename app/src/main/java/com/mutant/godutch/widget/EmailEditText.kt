package com.mutant.godutch.widget

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.inputmethod.EditorInfo

class EmailEditText : android.support.v7.widget.AppCompatEditText, CustomEditTextInterface {

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
        this.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        this.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
        addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                error = null
            }
        })
    }

    override val isValid: Boolean
        get() = Patterns.EMAIL_ADDRESS.matcher(this.text.toString().trim { it <= ' ' }).matches()

    val isEmpty: Boolean
        get() = TextUtils.isEmpty(this.text.toString().trim { it <= ' ' })

    override fun setError(error: CharSequence?) {
        this.requestFocus()
        super.setError(error)
    }
}