package org.daoke.mlcp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class MLCPNetUtil {
	public static final int MLCP_NET_NO = 0;
	public static final int MLCP_NET_2G = 1;
	public static final int MLCP_NET_3G = 2;
	public static final int MLCP_NET_4G = 3;
	public static final int MLCP_NET_WIFI = 4;
	public static final int MLCP_NET_UNKNOWN = -1;

	private static int getNetWorkStatus(Context context) {

		int netWorkStatus = MLCP_NET_UNKNOWN;

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {

			switch (networkInfo.getType()) {

			case ConnectivityManager.TYPE_MOBILE:
				switch (networkInfo.getSubtype()) {
				case TelephonyManager.NETWORK_TYPE_GPRS: // 锟斤拷通2g
				case TelephonyManager.NETWORK_TYPE_CDMA: // 锟斤拷锟斤拷2g
				case TelephonyManager.NETWORK_TYPE_EDGE: // 锟狡讹拷2g
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_IDEN:
					netWorkStatus = MLCP_NET_2G;
					break;
				case TelephonyManager.NETWORK_TYPE_EVDO_A: // 锟斤拷锟斤拷3g
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_EVDO_B:
				case TelephonyManager.NETWORK_TYPE_EHRPD:
				case TelephonyManager.NETWORK_TYPE_HSPAP:
					netWorkStatus = MLCP_NET_3G;
					break;
				case TelephonyManager.NETWORK_TYPE_LTE:
					netWorkStatus = MLCP_NET_4G;
					break;
				default:
					netWorkStatus = MLCP_NET_UNKNOWN;
				}
				break;
			case ConnectivityManager.TYPE_WIFI:
				netWorkStatus = MLCP_NET_WIFI;
				break;
			default:
				netWorkStatus = MLCP_NET_UNKNOWN;
				break;
			}

		}

		return netWorkStatus;

	}
	
	public static boolean isNetworkAvailable(Context context) {
		boolean isAvailable = true;
		if (MLCP_NET_UNKNOWN == getNetWorkStatus(context)) {

			isAvailable = false;
		}
		return isAvailable;
	}
}
