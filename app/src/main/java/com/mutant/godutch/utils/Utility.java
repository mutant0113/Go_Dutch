package com.mutant.godutch.utils;

import android.text.InputFilter;
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
}
