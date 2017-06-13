package com.mutant.godutch.widget

/**
 * 客製化EditText
 */
interface CustomEditTextInterface {

    /**
     * 設定這個edittext所有相關限制，例如maxLength, inputType等等
     */
    fun initializeSetting()

    /**
     * 判斷格式是否符合規定
     * @return 是否符合格式
     */
    val isValid: Boolean
}
