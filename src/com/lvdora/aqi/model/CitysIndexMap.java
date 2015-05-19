package com.lvdora.aqi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.aqi.module.ModuleSPIO;
import com.lvdora.aqi.thread.ThreadServerInteraction;
import com.lvdora.aqi.util.EnAndDecryption;
import com.lvdora.aqi.view.HomeActivity;

/**
 * 城市映射：保存6个定位、收藏城市的映射
 * 
 * @1.View 保存：城市的变更和编辑
 * @2.SP 数据交互：和citydata
 * @3.DB 数据交互：和cityaqi
 * 
 * @author xqp
 * 
 */
public class CitysIndexMap extends TreeMap<Integer, Integer> {

	private static final long serialVersionUID = -8222090615251741193L;
	// 所在页面
	private Activity activity;
	// 城市id映射列表的单例
	private static CitysIndexMap instance;

	private CitysIndexMap(Activity activity) {
		this.activity = activity;
	}

	public static CitysIndexMap getInstance(Activity activity) {
		// 初次构造
		if (instance == null) {
			instance = new CitysIndexMap(activity);
		}
		Log.v("CitysIndexMap", "getInstance " + instance.size() + " " + instance.toString());
		Log.v("CitysIndexMap", "currentIndexOut " + HomeActivity.currentIndexOut);
		// 返回实例
		return instance;
	}

	/*
	 * 重新排序
	 */
	public void reorder() {
		Log.v("CitysIndexMap", "reorder " + instance.size());
		// 去掉不合理的数字
		instance.remove(-1);
		int i = 0;
		Map<Integer, Integer> tempMap = new TreeMap<Integer, Integer>();
		// 重排
		for (Integer key : instance.keySet()) {
			tempMap.put(i, instance.get(key));
			i++;
		}
		Log.v("CitysIndexMap", "reorder " + tempMap.toString());
		instance.clear();
		for (Integer key : tempMap.keySet()) {
			instance.put(key, tempMap.get(key));
		}
		tempMap.clear();
	}

	/*
	 * getKeyByValue
	 */
	public int getKeyByValue(int value) {
		for (int key : instance.keySet()) {
			if (instance.get(key).equals(value)) {
				return key;
			}
		}
		return 0;
	}

	/**
	 *  取缓存
	 */
	public void spToMap() {
		instance.clear();
		SharedPreferences sp = activity.getSharedPreferences("citydata", 0);
		List<City> citys = EnAndDecryption.String2WeatherList(sp.getString("citys", ""));
		for (City city : citys) {
			instance.put(city.getOrder(), city.getId());
		}
		ModuleSPIO.showCityData(activity, "HomeActivity loadSP");
	}

	/**
	 *  存缓存
	 */
	public void mapToSP() {
		SharedPreferences spCitys = activity.getSharedPreferences("citydata", 0);
		// 遍历所有key取value存入sp
		for (Integer key : instance.keySet()) {
			spCitys.edit().putInt(key + "", instance.get(key)).commit();
		}

		// 存定位信息
		int cityId = instance.get(0);
		SharedPreferences spLocation = activity.getSharedPreferences("location", 0);
		spLocation.edit().putInt("cityId", cityId).commit();
	}

	/**
	 *  城市列表存缓存
	 */
	public void listToSP() {
		Log.v("CitysIndexMap", "listToSP " + instance.toString());
		List<City> citys = new ArrayList<City>();
		for (Integer key : instance.keySet()) {
			City city = new City();
			city.setOrder(key);
			city.setId(instance.get(key));
			citys.add(city);
		}
		String cityString = EnAndDecryption.CityList2String(citys);
		SharedPreferences sp = activity.getSharedPreferences("citydata", 0);
		sp.edit().putString("citys", cityString).commit();
		ModuleSPIO.showCityData(activity, "CitysIndexMap");
	}

	/**
	 *  存库
	 */
	public void sendRequestForAqis() {
		ThreadServerInteraction msi = new ThreadServerInteraction(activity);
		for (int i = 0; i < instance.size(); i++) {
			msi.sendRequestForAqi(i, instance.get(i));
		}
	}

	// 存库
	public void mapToDB() {
		//
		sendRequestForAqis();
		// 存库
//		CityAqiDao cityAqiDao = new CityAqiDao(activity, "");
		// cityAqiDao.insertCityAqiList();
	}

	/**
	 *  存库
	 * @param cityAqis
	 */
	public void mapToDB(List<CityAqi> cityAqis) {
		CityAqiDao cityAqiDao = new CityAqiDao(activity, "");
		cityAqiDao.insertCityAqiList(cityAqis);
	}

	/**
	 * 取库
	 */
	public void dbToMap() {
		CityAqiDao cityAqiDao = new CityAqiDao(activity, "");
		List<CityAqi> aqis = cityAqiDao.getAll();
	}

	/*-------------------下边是在不改旧有逻辑的情况下的修改方法------------------------------*/
	/**
	 * 查询特定的值
	 */ 
	private boolean test(Integer selectByKey, Integer selectByValue) {
		for (Integer key : instance.keySet()) {
			if (key == selectByKey) {
				return true;
			}
		}
		for (Integer value : instance.values()) {
			if (value == selectByValue) {
				return true;
			}
		}
		return false;
	}
}