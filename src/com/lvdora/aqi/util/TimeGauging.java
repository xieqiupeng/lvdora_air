package com.lvdora.aqi.util;

import java.util.TreeMap;

import android.util.Log;

/**
 * 进行时间管理在每个阶段检查运行时间
 * 
 * @author xqp
 */
public class TimeGauging extends TreeMap<String, Long> {

	private static final long serialVersionUID = 5784046975776152997L;

	public static String START_TIME = "START_TIME";
	public static String LOGO_TIME = "LOGO_TIME";
	public static String HOME_TIME = "HOME_TIME";
	public static String MAIN_TIME = "MAIN_TIME";

	private static TimeGauging instance;

	private TimeGauging() {

	}

	/**
	 * 
	 */
	public static void showTime(String name) {
		if (instance == null) {
			instance = new TimeGauging();
		}
		instance.put(name, System.currentTimeMillis());
		Log.w(name, instance.get(name) + "");
	}

	/**
	 * 
	 */
	public static void diffTime(String name1, String name2) {
		showTime(name2);
		Log.w(name2 + "-" + name1, instance.get(name2) - instance.get(name1) + "");
	}
}
