package com.mutant.godutch.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.InputFilter
import android.text.format.DateUtils
import android.widget.EditText

/**
 * Created by evanfang102 on 2017/5/23.
 */

object Utility {

    /**
     * 設定editText輸入長度

     * @param editText
     * *
     * @param maxLength
     */
    fun setMaxLength(editText: EditText, maxLength: Int) {
        editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
    }

    fun getRelativeTimeSpanDate(timestamp: Long): CharSequence {
        return android.text.format.DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS)
    }

    fun intentToGoogleMarketToDowloadApp(context: Context, appMarketId: String) {
        val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appMarketId))
        context.startActivity(marketIntent)
    }
}
