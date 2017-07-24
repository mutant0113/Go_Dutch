package com.mutant.godutch.server

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by evanfang102 on 2017/7/12.
 */
class WebAgent {

    companion object {

        fun fetchExchangeRate(callback: Callback) {
            val okHttpClient = OkHttpClient()
            val request = Request.Builder().url("https://tw.rter.info/capi.php").build()
            var call = okHttpClient.newCall(request)
            call.enqueue(callback)
        }
    }
}