package com.lvdora.aqi.util;

import com.lvdora.aqi.R;

/**
 * 等級判定工具类
 * 
 * @xqp
 * 
 */
public class GradeTool {

	//
	public static String getStateByIndex(int paramInt) {
		if ((paramInt > -1) && (paramInt <= 0))
			return "";
		if (paramInt < 51)
			return "优";
		if (paramInt < 101)
			return "良";
		if (paramInt < 151)
			return "轻度污染";
		if (paramInt < 201)
			return "中度污染";
		if (paramInt < 301)
			return "重度污染";
		return "严重污染";
	}

	//
	public static int getMapColorByIndex(String index) {
		if ("--".equals(index.trim()) || "".equals(index.trim())) {
			return R.color.green;
		}
		float paramInt = Float.parseFloat(index.trim());
		if ((paramInt > -1) && (paramInt < 0))
			return 0;
		if (paramInt < 51)
			return R.color.green;
		if (paramInt < 101)
			return R.color.yellow;
		if (paramInt < 151)
			return R.color.orange;
		if (paramInt < 201)
			return R.color.red;
		if (paramInt < 301)
			return R.color.purple;
		return R.color.hered;
	}

	// 颜色
	public static int getAqiColorByIndex(String index) {
		if ("--".equals(index.trim()) || "".equals(index.trim())) {
			return R.drawable.aqi_index_shape_green;
		}
		float paramInt = Float.parseFloat(index.trim());
		if ((paramInt > -1) && (paramInt < 0))
			return 0;
		if (paramInt < 51)
			return R.drawable.aqi_index_shape_green;
		if (paramInt < 101)
			return R.drawable.aqi_index_shape_yellow;
		if (paramInt < 151)
			return R.drawable.aqi_index_shape_orange;
		if (paramInt < 201)
			return R.drawable.aqi_index_shape_red;
		if (paramInt < 301)
			return R.drawable.aqi_index_shape_purple;
		return R.drawable.aqi_index_shape_hered;
	}

	public static int getTextColorByAqi(String index) {
		if ("--".equals(index.trim()) || "".equals(index.trim())) {
			return R.color.white;
		}
		float paramInt = Float.parseFloat(index.trim());
		if ((paramInt > -1) && (paramInt < 0))
			return R.color.black;
		if (paramInt < 51)
			return R.color.white;
		if (paramInt < 101)
			return R.color.black;
		return R.color.white;
	}

	/**
	 * 
	 * @param index
	 * @return
	 */
	public static int getColorByIndex(String index) {
		if ("--".equals(index.trim()) || "".equals(index.trim())) {
			return R.drawable.exponent_bg_shape_green;
		}
		float paramInt = Float.parseFloat(index.trim());
		if ((paramInt > -1) && (paramInt < 0))
			return 0;
		if (paramInt < 51)
			return R.drawable.exponent_bg_shape_green;
		if (paramInt < 101)
			return R.drawable.exponent_bg_shape_yellow;
		if (paramInt < 151)
			return R.drawable.exponent_bg_shape_orange;
		if (paramInt < 201)
			return R.drawable.exponent_bg_shape_red;
		if (paramInt < 301)
			return R.drawable.exponent_bg_shape_purple;
		return R.drawable.exponent_bg_shape_hered;
	}

	/**
	 * 
	 * @param grade
	 * @return
	 */
	public static int getWeatherIcon(String grade) {
		if (grade.equals("d00")) {
			return R.drawable.weathericon_graph_01;
		}
		if (grade.equals("d01")) {
			return R.drawable.weathericon_graph_02;
		}
		if (grade.equals("d02")) {
			return R.drawable.weathericon_graph_03;
		}
		if (grade.equals("d03")) {
			return R.drawable.weathericon_graph_04;
		}
		if (grade.equals("d04")) {
			return R.drawable.weathericon_graph_05;
		}
		if (grade.equals("d05")) {
			return R.drawable.weathericon_graph_06;
		}
		if (grade.equals("d06")) {
			return R.drawable.weathericon_graph_07;
		}
		if (grade.equals("d07")) {
			return R.drawable.weathericon_graph_08;
		}
		if (grade.equals("d08")) {
			return R.drawable.weathericon_graph_09;
		}
		if (grade.equals("d09")) {
			return R.drawable.weathericon_graph_10;
		}
		if (grade.equals("d10")) {
			return R.drawable.weathericon_graph_10;
		}
		if (grade.equals("d11")) {
			return R.drawable.weathericon_graph_11;
		}
		if (grade.equals("d12")) {
			return R.drawable.weathericon_graph_11;
		}
		if (grade.equals("d13")) {
			return R.drawable.weathericon_graph_12;
		}
		if (grade.equals("d14")) {
			return R.drawable.weathericon_graph_13;
		}
		if (grade.equals("d15")) {
			return R.drawable.weathericon_graph_14;
		}
		if (grade.equals("d16")) {
			return R.drawable.weathericon_graph_15;
		}
		if (grade.equals("d17")) {
			return R.drawable.weathericon_graph_16;
		}
		if (grade.equals("d18")) {
			return R.drawable.weathericon_graph_17;
		}
		if (grade.equals("d19")) {
			return R.drawable.weathericon_graph_18;
		}
		if (grade.equals("d53")) {
			return R.drawable.weathericon_graph_52;
		}
		return R.drawable.weathericon_graph_01;
	}

	//
	public static int getSuggestion(int paramInt) {
		if ((paramInt > -1) && (paramInt < 51))
			return R.string.you_weather_description;
		if (paramInt < 101)
			return R.string.liang_weather_description;
		if (paramInt < 151)
			return R.string.qingdu_weather_description;
		if (paramInt < 201)
			return R.string.zhongdu_weather_description;
		if (paramInt < 301)
			return R.string.zhongdumore_weather_description;
		return R.string.yanzhong_weather_description;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static String getValue(String value) {
		if (value != null) {
			return value;
		}
		return "--";
	}
}
