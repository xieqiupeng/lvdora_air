package com.lvdora.aqi.util;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.lvdora.aqi.R;

public class Config {

	private static final String TAG = "Config";
	public static final String UPDATE_SAVENAME = "lvaqi.apk";
	public static final String UPDATE_APKNAME = "lvaqi.apk";

	public static float getVerCode(Context context) {
		float verCode = -1f;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					"com.lvdora.aqi", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verCode;
	}
	
	public static int getVerCode(Context context,String AppClass) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					"com.lvdora.aqi", 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return verCode;
	}

	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.lvdora.aqi", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;

	}

	public static String getAppName(Context context) {
		String verName = context.getResources().getText(R.string.app_name)
				.toString();
		return verName;
	}
}
