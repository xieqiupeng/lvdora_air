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
import com.lvdora.aqi.module.ModuleServerInteraction;
import com.lvdora.aqi.util.EnAndDecryption;

/**
 * 城市映射：保存6个定位、收藏城市的映射
 * 
 * @1.View 城市变更，编辑和保存操作
 * @2.SP 和citydata的数据交互
 * @3.DB 和cityaqi的数据交互
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
		// 返回实例
		return instance;
	}

	/*
	 * 重新排序
	 */
	public void reorder() {
		Log.v("CitysIndexMap", "recorder " + instance.size());
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
		return -1;
	}

	// 取缓存
	public void spToMap() {
		instance.clear();
		SharedPreferences spCitys = activity.getSharedPreferences("citydata", 0);
		for (int i = 0; i < 6; i++) {
			int value = spCitys.getInt(i + "", -1);
			instance.put(i, value);
		}
	}

	// 存缓存
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

	// 城市列表存缓存
	public void listToSP() {
		Log.v("CitysIndexMap", "listToSP " + instance.toString());
		List<City> citys = new ArrayList<City>();
		for (Integer key : instance.keySet()) {
			City city = new City();
			city.setOrder(key);
			city.setId(instance.get(key));
			citys.add(city);
		}
		try {
			String cityString = EnAndDecryption.CityList2String(citys);
			// String cityString = citys.toString();
			SharedPreferences sp;
			sp = activity.getSharedPreferences("citydata", 0);
			sp.edit().putString("citys", cityString).commit();
		} catch (Exception e) {
		}
		ModuleSPIO.showCityData(activity, "CitysIndexMap");
	}

	// 存库
	public void sendRequestForAqis() {
		ModuleServerInteraction msi = new ModuleServerInteraction(activity);
		for (int i = 0; i < instance.size(); i++) {
			msi.sendRequestForAqi(i, instance.get(i));
		}
	}

	// 存库
	public void mapToDB(List<CityAqi> cityAqis) {
		CityAqiDao cityAqiDao = new CityAqiDao(activity, "");
		cityAqiDao.insertCityAqiList(cityAqis);
	}

	// 取库
	public void dbToMap() {
		CityAqiDao cityAqiDao = new CityAqiDao(activity, "");
		List<CityAqi> aqis = cityAqiDao.getAll();
	}

	/*-------------------下边是在不改旧有逻辑的情况下的修改方法------------------------------*/
	// 查询特定的值
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