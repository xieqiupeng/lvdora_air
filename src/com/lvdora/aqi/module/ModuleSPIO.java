package com.lvdora.aqi.module;

import java.util.ArrayList;
import java.util.List;

import com.lvdora.aqi.model.City;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.EnAndDecryption;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 解决问题：缓存IO的统一管理
 * 
 * @1 所有sp的名字和用途
 * @author xqp
 */
public class ModuleSPIO {
	
	// SPIO含义
	public static final String FULLNAME = "SharedPreferences InputStream and OutputStream";
	
	// private Activity activity;
	private SharedPreferences sp;

	// 所有sp的名字和用途
	public final static String[] spName = {
			// 0 城市编辑界面中添加的城市
			"citydata",
			// 1 保存当前城市
			"cur_city",
			// 2 服务器返回数据
			"jsondata",
			// 3
			"sitedata",
			// 4
			"location",
			// 5
			"spitdata",
			// 6
			"sortSiteData",
			// 7 记录界面加载时间
			"autoupdate",
			// 8 进度条标志位，存GPS
			"isFirstIn"
	//
	};

	public ModuleSPIO() {
	}

	public static void showCityData(Activity activity, String FromWhere) {
		SharedPreferences sp = activity.getSharedPreferences("citydata", 0);
		List<City> citys = new ArrayList<City>();
		citys = EnAndDecryption.String2WeatherList(sp.getString("citys", ""));
		int cityCount = citys.size();
		Log.d("ModuleSPIO", "showCityData from" + FromWhere + " citys:" + cityCount + " " + citys.toString());
	}

	public void dataIsFirstInSave(Activity activity, boolean trueOrFalse) {
		SharedPreferences sp;
		sp = activity.getSharedPreferences("isFirstIn", 0);
		sp.edit().putBoolean("isFirstIn", trueOrFalse).commit();
	}

}
