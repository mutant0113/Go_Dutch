package com.mutant.godutch.model

import android.annotation.SuppressLint
import io.mironov.smuggler.AutoParcelable

/**
 * Created by evanfang102 on 2017/5/26.
 */

@SuppressLint("ParcelCreator")
data class ExchangeRate(
        val jsonKey: String = "",
        val rate: Double = 0.0,
        val lastUpdated: String = "",
        val country: String = "") : AutoParcelable
