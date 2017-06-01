package com.mutant.godutch;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class DebugHelper {

	static String TAG = "DebugHelper";

	private static boolean bDeveloperMode = true;
	public static boolean bUseGoogleAnalytics = true && !bDeveloperMode; // 設true，開GA，若DeveloperMode開啟，不傳送GA
	public static boolean bUseCrashlytics = true && !bDeveloperMode;
	public static boolean checkOpen() {
		return bDeveloperMode;
	}

	public static void debugLog(boolean isDebug, String TAG, String message) {
		if (isDebug) {
			debugLog(TAG, message);
		}
	}

	public static void debugLog(String TAG, String message) {
		if (TAG == null || message == null) {
			return;
		}

		if (checkOpen()) {
			Log.d(TAG, message);
		}
	}

	public static void debugToast(Context context, String message) {
		if (context == null || message == null) {
			return;
		}

		if (checkOpen()) {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		}
	}

	public static void setDeveloperModeEnabled(boolean isEnabled) {
		bDeveloperMode = isEnabled;
	}
}