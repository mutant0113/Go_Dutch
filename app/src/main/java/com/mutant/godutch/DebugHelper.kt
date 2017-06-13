package com.mutant.godutch

import android.content.Context
import android.util.Log
import android.widget.Toast

object DebugHelper {

    internal var TAG = "DebugHelper"

    private var bDeveloperMode = true
    var bUseGoogleAnalytics = true && !bDeveloperMode // 設true，開GA，若DeveloperMode開啟，不傳送GA
    var bUseCrashlytics = true && !bDeveloperMode
    fun checkOpen(): Boolean {
        return bDeveloperMode
    }

    fun debugLog(isDebug: Boolean, TAG: String, message: String) {
        if (isDebug) {
            debugLog(TAG, message)
        }
    }

    fun debugLog(TAG: String?, message: String?) {
        if (TAG == null || message == null) {
            return
        }

        if (checkOpen()) {
            Log.d(TAG, message)
        }
    }

    fun debugToast(context: Context?, message: String?) {
        if (context == null || message == null) {
            return
        }

        if (checkOpen()) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    fun setDeveloperModeEnabled(isEnabled: Boolean) {
        bDeveloperMode = isEnabled
    }
}