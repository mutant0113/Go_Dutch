package com.mutant.godutch.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.InputFilter;
import android.text.format.DateUtils;
import android.widget.EditText;

/**
 * Created by evanfang102 on 2017/5/23.
 */

public class Utility {

    /**
     * 設定editText輸入長度
     *
     * @param editText
     * @param maxLength
     */
    public static void setMaxLength(EditText editText, int maxLength) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    public static CharSequence getRelativeTimeSpanDate(long timestamp) {
        return android.text.format.DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS);
    }

    public static void intentToGoogleMarketToDowloadApp(Context context, String appMarketId) {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appMarketId));
        context.startActivity(marketIntent);
    }
}
