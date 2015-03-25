package com.lvdora.aqi.util;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

	private static String mYear;
	private static String mMonth;
	private static String mDay;
	private static String mWeek;

	public static String getDate() {

		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
		mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		int iWeek = c.get(Calendar.DAY_OF_WEEK);
		mWeek = String.valueOf(iWeek);
		switch (iWeek) {
		case 1:
			mWeek = "日";
			break;
		case 2:
			mWeek = "一";
			break;
		case 3:
			mWeek = "二";
			break;
		case 4:
			mWeek = "三";
			break;
		case 5:
			mWeek = "四";
			break;
		case 6:
			mWeek = "五";
			break;
		case 7:
			mWeek = "六";
			break;
		default:
			break;
		}
		return mYear + "-" + mMonth + "-" + mDay + " 周" + mWeek;
	}

	// 获取第二天星期
	public static String getTomorrowWeek() {

		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		int iWeek = c.get(Calendar.DAY_OF_WEEK);
		mWeek = String.valueOf(iWeek);

		switch (iWeek) {
		case 1:
			mWeek = "一";
			break;
		case 2:
			mWeek = "二";
			break;
		case 3:
			mWeek = "三";
			break;
		case 4:
			mWeek = "四";
			break;
		case 5:
			mWeek = "五";
			break;
		case 6:
			mWeek = "六";
			break;
		case 7:
			mWeek = "日";
			break;

		default:
			break;
		}

		return "周" + mWeek;
	}

	// 获取第三天星期
	public static String getSecTomorrowWeek() {

		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		int iWeek = c.get(Calendar.DAY_OF_WEEK);
		mWeek = String.valueOf(iWeek);

		switch (iWeek) {
		case 1:
			mWeek = "二";
			break;
		case 2:
			mWeek = "三";
			break;
		case 3:
			mWeek = "四";
			break;
		case 4:
			mWeek = "五";
			break;
		case 5:
			mWeek = "六";
			break;
		case 6:
			mWeek = "日";
			break;
		case 7:
			mWeek = "一";
			break;

		default:
			break;
		}

		return "周" + mWeek;
	}
	
	// 获取第四天星期
		public static String getThirdTomorrowWeek() {

			final Calendar c = Calendar.getInstance();
			c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			int iWeek = c.get(Calendar.DAY_OF_WEEK);
			mWeek = String.valueOf(iWeek);

			switch (iWeek) {

			case 1:
				mWeek = "三";
				break;
			case 2:
				mWeek = "四";
				break;
			case 3:
				mWeek = "五";
				break;
			case 4:
				mWeek = "六";
				break;
			case 5:
				mWeek = "日";
				break;
			case 6:
				mWeek = "一";
				break;
			case 7:
				mWeek = "二";
				break;
			default:
				break;
			}

			return "周" + mWeek;
		}

		// 获取第五天星期
		public static String getFourthTomorrowWeek() {

			final Calendar c = Calendar.getInstance();
			c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			int iWeek = c.get(Calendar.DAY_OF_WEEK);
			mWeek = String.valueOf(iWeek);

			switch (iWeek) {

			case 1:
				mWeek = "四";
				break;
			case 2:
				mWeek = "五";
				break;
			case 3:
				mWeek = "六";
				break;
			case 4:
				mWeek = "日";
				break;
			case 5:
				mWeek = "一";
				break;
			case 6:
				mWeek = "二";
				break;
			case 7:
				mWeek = "三";
				break;
			default:
				break;
			}

			return "周" + mWeek;
		}


	/**
	 *  将字符串转为时间戳
	 * @param timeString
	 * @return
	 */
	public static Long getTime(String timeString) {
		// String millisecond = null;
		long time = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date;
		try {
			date = sdf.parse(timeString);
			time = date.getTime();
			Log.e("aqi", String.valueOf(time));
			// millisecond = String.valueOf(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	/**
	 *  将时间戳转为字符串
	 * @author admin
	 *
	 */
	public static String Date2String(Date today) {
		//Date today = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyy年MM月dd日hh:mm:ss");
		String time = f.format(today);
		return time;
	}
}
