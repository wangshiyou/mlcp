package org.daoke.mlcp.util;

import android.util.Log;

public class MLCPLogUtil {
	private static boolean logFlag = false;

	private final static String TAG = "MLCPLogUtil";

	private static  boolean isLogFlag() {
		return logFlag;
	}

	public static void setLogFlag(boolean logFlag) {
		MLCPLogUtil.logFlag = logFlag;
	}

	public static void i(String str) {
		if (isLogFlag()) {
			Log.i(TAG, str);
		}

	}
	public static void d(String str) {
		if (isLogFlag()) {
			Log.d(TAG, str);
		}

	}
	public static void w(String str) {
		if (isLogFlag()) {
			Log.w(TAG, str);
		}

	}
	public static void e(String str) {
		if (isLogFlag()) {
			Log.e(TAG, str);
		}

	}
}
