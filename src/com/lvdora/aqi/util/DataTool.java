package com.lvdora.aqi.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;

import com.lvdora.aqi.adapter.RankAdapter;
import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.aqi.dao.CityDao;
import com.lvdora.aqi.model.City;
import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.model.CityRank;
import com.lvdora.aqi.model.MapPopup;
import com.lvdora.aqi.model.MapSite;
import com.lvdora.aqi.model.SiteAqi;
import com.lvdora.aqi.model.SpitContent;

/**
 * 
 * @author xqp
 * 
 */
public class DataTool {
	// 城市总数
	public static int count_city = 0;

	/**
	 * 在SD卡下创建根文件夹
	 */
	public static void createSDCardDir() {

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

			// 创建一个文件夹对象，赋值为外部存储器的目录
			File sdcardDir = Environment.getExternalStorageDirectory();

			// 得到一个路径，内容是sdcard的文件夹路径和名字
			String path = sdcardDir.getPath() + "/lvdora";

			File path1 = new File(path);

			if (!path1.exists()) {
				// 若不存在，创建目录，可以在应用启动的时候创建
				path1.mkdirs();
			}
		}

		else {
			return;
		}
	}

	/**
	 * 相对路径转为绝对路径
	 * 
	 * @param filePath
	 * @return
	 */
	public static String createFileDir(String filePath) {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			// 创建一个文件夹对象，赋值为外部存储器的目录
			File sdcardDir = Environment.getExternalStorageDirectory();
			// 得到一个路径，内容是sdcard的文件夹路径和名字
			String appPath = sdcardDir.getPath() + "/lvdora";
			// 绝对路径
			File jdpath = new File(appPath + "/" + filePath);
			if (!jdpath.exists()) {
				// 若不存在，创建目录，可以在应用启动的时候创建
				jdpath.mkdirs();
			}
			return jdpath.getPath();
		} else {
			return "";
		}
	}

	// getCityAqi
	public static CityAqi getCityAqi(String jsonData, int cityId, int order) {

		CityAqi cityAqi = new CityAqi();
		cityAqi.setCityId(cityId);
		cityAqi.setOrder(order);

		try {
			JSONObject obj = new JSONObject(jsonData);
			cityAqi.setCityName(obj.getString("city").trim());
			// 提取aqi信息
			JSONObject airObj = obj.getJSONObject("aqi");
			obj.keys();
			Log.e("DataTool", "jsonToCityAqi " + airObj + ">>>>");
			if (airObj != null) {
				cityAqi.setAqi(GradeTool.getValue(airObj.getString("aqi_aqi")));
				cityAqi.setPm25(GradeTool.getValue(airObj.getString("pm25")));
				cityAqi.setPm25_aqi(GradeTool.getValue(airObj.getString("pm25_aqi")));
				cityAqi.setPm10(GradeTool.getValue(airObj.getString("pm10")));
				cityAqi.setPm10_aqi(GradeTool.getValue(airObj.getString("pm10_aqi")));
				cityAqi.setSo2(GradeTool.getValue(airObj.getString("so2")));
				cityAqi.setSo2_aqi(GradeTool.getValue(airObj.getString("so2_aqi")));
				cityAqi.setNo2(GradeTool.getValue(airObj.getString("no2")));
				cityAqi.setNo2_aqi(GradeTool.getValue(airObj.getString("no2_aqi")));
				cityAqi.setCo(GradeTool.getValue(airObj.getString("co")));
				cityAqi.setCo_aqi(GradeTool.getValue(airObj.getString("co_aqi")));
				cityAqi.setAqi_pubtime(GradeTool.getValue(airObj.getString("pubtime")));
				cityAqi.setO3(GradeTool.getValue(airObj.getString("o3")));
				cityAqi.setO3_aqi(GradeTool.getValue(airObj.getString("o3_aqi")));
			}

			// Log.e("cityaqi2", cityAqi.toString());

			// 提取美领馆信息
			if (cityId == 18 || cityId == 45 || cityId == 221 || cityId == 286 || cityId == 289) {
				JSONObject usaObj = obj.getJSONObject("usa");
				cityAqi.setUsa_aqi(usaObj.getString("aqi_aqi"));
				cityAqi.setUsa_pm25(usaObj.getString("pm25"));
				cityAqi.setUs_pubtime(usaObj.getString("pubtime"));
			} else {
				// 此处是怕数据库插入失败
				cityAqi.setUsa_aqi("--");
				cityAqi.setUsa_pm25("--");
				cityAqi.setUs_pubtime("--");
			}

			// 提取天气信息
			JSONArray weatherObj = obj.getJSONArray("weather");

			// 今天天气
			if (weatherObj != null) {

				// Log.e("weaher",
				// weatherObj.getJSONObject(1).getString("weather_icon"));

				cityAqi.setTemp_now(GradeTool.getValue(weatherObj.getJSONObject(0).getString("temp_now")));
				cityAqi.setWind(GradeTool.getValue(weatherObj.getJSONObject(0).getString("wind")
						+ weatherObj.getJSONObject(0).getString("windDirection")));
				/*
				 * if(weatherObj.getJSONObject(1)!=null &&
				 * weatherObj.getJSONObject(2)!=null &&
				 * weatherObj.getJSONObject(3)!=null &&
				 * weatherObj.getJSONObject(4)!=null &&
				 * weatherObj.getJSONObject(5)!=null ){
				 */

				cityAqi.setWeather0(GradeTool.getValue(weatherObj.getJSONObject(1).getString("weather")));
				cityAqi.setWeather_icon0(GradeTool.getValue(weatherObj.getJSONObject(1).getString("weather_icon")));
				cityAqi.setHightTemp0(GradeTool.getValue(weatherObj.getJSONObject(1).getString("hightTemp")));
				cityAqi.setLowTemp0(GradeTool.getValue(weatherObj.getJSONObject(1).getString("lowTemp")));
				cityAqi.setWindForce0(GradeTool.getValue(weatherObj.getJSONObject(1).getString("windforce")));

				// 第二天天气
				cityAqi.setWeather1(GradeTool.getValue(weatherObj.getJSONObject(2).getString("weather")));
				cityAqi.setWeather_icon1(GradeTool.getValue(weatherObj.getJSONObject(2).getString("weather_icon")));
				cityAqi.setHightTemp1(GradeTool.getValue(weatherObj.getJSONObject(2).getString("hightTemp")));
				cityAqi.setLowTemp1(GradeTool.getValue(weatherObj.getJSONObject(2).getString("lowTemp")));
				cityAqi.setWindForce1(GradeTool.getValue(weatherObj.getJSONObject(2).getString("windforce")));

				// 第三天天气
				cityAqi.setWeather2(GradeTool.getValue(weatherObj.getJSONObject(3).getString("weather")));
				cityAqi.setWeather_icon2(GradeTool.getValue(weatherObj.getJSONObject(3).getString("weather_icon")));
				cityAqi.setHightTemp2(GradeTool.getValue(weatherObj.getJSONObject(3).getString("hightTemp")));
				cityAqi.setLowTemp2(GradeTool.getValue(weatherObj.getJSONObject(3).getString("lowTemp")));
				cityAqi.setWindForce2(GradeTool.getValue(weatherObj.getJSONObject(3).getString("windforce")));

				// 第四天天气
				cityAqi.setWeather3(GradeTool.getValue(weatherObj.getJSONObject(4).getString("weather")));
				cityAqi.setWeather_icon3(GradeTool.getValue(weatherObj.getJSONObject(4).getString("weather_icon")));
				cityAqi.setHightTemp3(GradeTool.getValue(weatherObj.getJSONObject(4).getString("hightTemp")));
				cityAqi.setLowTemp3(GradeTool.getValue(weatherObj.getJSONObject(4).getString("lowTemp")));
				cityAqi.setWindForce3(GradeTool.getValue(weatherObj.getJSONObject(4).getString("windforce")));

				// 第五天天气
				cityAqi.setWeather4(GradeTool.getValue(weatherObj.getJSONObject(5).getString("weather")));
				cityAqi.setWeather_icon4(GradeTool.getValue(weatherObj.getJSONObject(5).getString("weather_icon")));
				cityAqi.setHightTemp4(GradeTool.getValue(weatherObj.getJSONObject(5).getString("hightTemp")));
				cityAqi.setLowTemp4(GradeTool.getValue(weatherObj.getJSONObject(5).getString("lowTemp")));
				cityAqi.setWindForce4(GradeTool.getValue(weatherObj.getJSONObject(5).getString("windforce")));
				// }
			}
			return cityAqi;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param jsonData
	 *            取得的站点信息
	 * @param cityId
	 *            城市id
	 */
	public static List<SiteAqi> getSiteAqi(String jsonData, int cityId) {

		if (jsonData.toString().equals("0")) {

			return null;
		}

		List<SiteAqi> siteAqis = new ArrayList<SiteAqi>();
		try {
			JSONArray array = new JSONArray(jsonData);
			for (int i = 0; i < array.length(); i++) {
				SiteAqi siteAqi = new SiteAqi();
				siteAqi.setCityId(cityId);
				JSONObject obj = array.getJSONObject(i);
				siteAqi.setName(obj.getString("spotName"));
				siteAqi.setAqi(obj.getString("aqi"));
				siteAqi.setPm25(obj.getString("pm25"));

				siteAqis.add(siteAqi);
			}

			return siteAqis;

		} catch (JSONException e) {

			e.printStackTrace();

		}

		return null;

	}

	/**
	 * 地图站点信息
	 * 
	 * @param jsonData
	 * @return
	 */
	public static List<MapSite> getMapSite(String jsonData) {

		if (jsonData.toString().equals("0")) {

			return null;
		}

		List<MapSite> mapSiteList = new ArrayList<MapSite>();
		try {
			JSONArray array = new JSONArray(jsonData);
			for (int i = 0; i < array.length(); i++) {
				MapSite mapSite = new MapSite();
				JSONObject obj = array.getJSONObject(i);
				mapSite.setName(obj.getString("spotName"));
				mapSite.setAqi(obj.getString("aqi"));
				mapSite.setSpotLongitude(obj.getDouble("spotLongitude"));
				mapSite.setSpotLatitude(obj.getDouble("spotLatitude"));
				mapSiteList.add(mapSite);
			}

			return mapSiteList;

		} catch (JSONException e) {

			e.printStackTrace();

		}

		return null;

	}

	public static List<CityRank> getCityRank(Context context, String jsonData) {

		List<CityRank> listRank = new ArrayList<CityRank>();
		CityRank cityRank;
		CityDao cityDao = new CityDao(context);
		try {
			JSONArray array = new JSONArray(jsonData);
			for (int i = 0; i < array.length(); i++) {
				cityRank = new CityRank();
				JSONObject obj = array.getJSONObject(i);
				if (obj != null) {
					// 查询本地数据库获得省份名称
					cityRank.setProvinceName(cityDao.getProvinceNameById(Integer.parseInt((String) obj
							.getString("provinceId"))));
					cityRank.setCityName(obj.getString("spotName"));
					cityRank.setAqi(obj.getString("aqi"));
					cityRank.setAqiCalculated(obj.getString("aqi_aqi"));
					cityRank.setPm25(obj.getString("pm25"));
					cityRank.setPm25Calculated(obj.getString("pm25_aqi"));
					cityRank.setPm10(obj.getString("pm10"));
					cityRank.setPm10Calculated(obj.getString("pm10_aqi"));
					cityRank.setSo2(obj.getString("so2"));
					cityRank.setSo2Calculated(obj.getString("so2_aqi"));
					cityRank.setO3(obj.getString("o3"));
					cityRank.setO3Calculated(obj.getString("o3_aqi"));
					cityRank.setNo2(obj.getString("no2"));
					cityRank.setNo2Calculated(obj.getString("no2_aqi"));
				}
				listRank.add(cityRank);

			}
			return listRank;
		} catch (JSONException e) {

			e.printStackTrace();

		}
		return null;

	}

	public static List<Map<String, Object>> getRankData(String jsonStr, String index, boolean flag) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<CityRank> cityRankList = new ArrayList<CityRank>();
		Map<String, Object> map;

		try {

			cityRankList = sortCityRankList(EnAndDecryption.String2CityRankList(jsonStr), index, flag);
			// 根据参数type排序
			int ranking = 0;// 相同数据的次数
			CityRank cityPro = null;
			for (int i = 0; i < cityRankList.size(); i++) {
				map = new HashMap<String, Object>();
				CityRank city = cityRankList.get(i);
				if (i > 0) {
					cityPro = cityRankList.get(i - 1);
				}
				if (index.equals("aqi_aqi")) {
					if (i > 0) {
						if (city.getAqiCalculated().equals(cityPro.getAqiCalculated())) {
							ranking = ranking + 1;
							// 排名相同只第一个显示排名
							map.put("rank", "");
						} else {
							ranking = 0;
							map.put("rank", i + 1);
						}
					} else {
						map.put("rank", i + 1);
					}
					map.put("aqi", city.getAqiCalculated());
					// 各种数据均计算为AQI的值，用于背景颜色的显示
					map.put("aqiCal", city.getAqiCalculated());

				} else if (index.equals("pm25")) {
					if (i > 0) {
						if (city.getPm25().equals(cityPro.getPm25())) {
							ranking = ranking + 1;
							// 排名相同只第一个显示排名
							map.put("rank", "");

						} else {
							ranking = 0;
							map.put("rank", i + 1);
						}
					} else {
						map.put("rank", i + 1);
					}
					map.put("aqi", city.getPm25());
					map.put("aqiCal", city.getPm25Calculated());

				} else if (index.equals("pm10")) {
					if (i > 0) {
						if (city.getPm10().equals(cityPro.getPm10())) {
							ranking = ranking + 1;
							// 排名相同只第一个显示排名
							map.put("rank", "");

						} else {
							ranking = 0;
							map.put("rank", i + 1);
						}
					} else {
						map.put("rank", i + 1);
					}
					map.put("aqi", city.getPm10());
					map.put("aqiCal", city.getPm10Calculated());

				} else if (index.equals("so2")) {
					if (i > 0) {
						if (city.getSo2().equals(cityPro.getSo2())) {
							ranking = ranking + 1;
							// 排名相同只第一个显示排名
							map.put("rank", "");

						} else {
							ranking = 0;
							map.put("rank", i + 1);
						}
					} else {
						map.put("rank", i + 1);
					}
					map.put("aqi", city.getSo2());
					map.put("aqiCal", city.getSo2Calculated());

				} else if (index.equals("o3")) {
					if (i > 0) {
						if (city.getO3Calculated().equals(cityPro.getO3Calculated())) {
							ranking = ranking + 1;
							// 排名相同只第一个显示排名
							map.put("rank", "");

						} else {
							ranking = 0;
							map.put("rank", i + 1);
						}
					} else {
						map.put("rank", i + 1);
					}
					map.put("aqi", city.getO3());
					map.put("aqiCal", city.getO3Calculated());

				} else {
					if (i > 0) {
						if (city.getNo2().equals(cityPro.getNo2())) {
							ranking = ranking + 1;
							// 排名相同只第一个显示排名
							map.put("rank", "");

						} else {
							ranking = 0;
							map.put("rank", i + 1);
						}
					} else {
						map.put("rank", i + 1);
					}
					map.put("aqi", city.getNo2());
					map.put("aqiCal", city.getNo2Calculated());

				}

				map.put("provinceName", city.getProvinceName());
				map.put("cityName", city.getCityName());
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}

	/**
	 * 实现比较器排序
	 * 
	 * @param array
	 * @param type
	 * @return
	 * @throws JSONException
	 */
	public static List<CityRank> sortCityRankList(List<CityRank> cityList, final String category, final Boolean ranktag)
			throws JSONException {
		List<CityRank> temp = new ArrayList<CityRank>();
		String tag = null;
		for (int i = 0; i < cityList.size(); i++) {
			CityRank city = cityList.get(i);
			if (category.equals("aqi_aqi")) {
				tag = city.getAqiCalculated();
			} else if (category.equals("pm25")) {
				tag = city.getPm25();
			} else if (category.equals("pm10")) {
				tag = city.getPm10();
			} else if (category.equals("so2")) {
				tag = city.getSo2();
			} else if (category.equals("o3")) {
				tag = city.getO3();
			} else if (category.equals("no2")) {
				tag = city.getNo2();
			}
			if (!tag.trim().equals("--")) {

				temp.add(cityList.get(i));

			}
		}

		Collections.sort(temp, new Comparator<CityRank>() {
			@Override
			public int compare(CityRank lhs, CityRank rhs) {
				float lid = 0;
				float rid = 0;
				try {
					if (category.equals("aqi_aqi")) {
						lid = Float.parseFloat((lhs.getAqiCalculated()));
						rid = Float.parseFloat((rhs.getAqiCalculated()));
					} else if (category.equals("pm25")) {
						lid = Float.parseFloat((lhs.getPm25()));
						rid = Float.parseFloat((rhs.getPm25()));
					} else if (category.equals("pm10")) {
						lid = Float.parseFloat((lhs.getPm10()));
						rid = Float.parseFloat((rhs.getPm10()));
					} else if (category.equals("so2")) {
						lid = Float.parseFloat((lhs.getSo2()));
						rid = Float.parseFloat((rhs.getSo2()));
					} else if (category.equals("o3")) {
						lid = Float.parseFloat((lhs.getO3()));
						rid = Float.parseFloat((rhs.getO3()));
					} else if (category.equals("no2")) {
						lid = Float.parseFloat((lhs.getNo2()));
						rid = Float.parseFloat((rhs.getNo2()));
					}
				} catch (Exception e) {
					e.printStackTrace();

				}
				// 降序
				if (ranktag) {
					if (lid >= rid) {
						return -1;
					} else {
						return 1;
					}
				} else {// 升序
					if (lid >= rid) {
						return 1;
					} else {
						return -1;
					}
				}
			}
		});
		return new ArrayList<CityRank>(temp);
	}

	/**
	 * 站点根据经纬度排序
	 * 
	 * @param deviceJson
	 * @return
	 */
	public static List<SiteAqi> sortCitySiteList(List<SiteAqi> citySiteList, final double longitude,
			final double latitude) throws Exception {

		Collections.sort(citySiteList, new Comparator<SiteAqi>() {
			@Override
			public int compare(SiteAqi lhs, SiteAqi rhs) {
				long lid = 0;
				long rid = 0;
				long longitude_lhs = (long) (Math.abs(Double.parseDouble(lhs.getSpotLongitude()) * 1E6 - longitude));
				long latitude_lhs = (long) (Math.abs(Double.parseDouble(lhs.getSpotLatitude()) * 1E6 - latitude));
				long longitude_rhs = (long) (Math.abs(Double.parseDouble(rhs.getSpotLongitude()) * 1E6 - longitude));
				long latitude_rhs = (long) (Math.abs(Double.parseDouble(rhs.getSpotLatitude()) * 1E6 - latitude));

				lid = longitude_lhs * longitude_lhs + latitude_lhs * latitude_lhs;
				rid = longitude_rhs * longitude_rhs + latitude_rhs * latitude_rhs;
				// Log.e("aqi", lhs.getName() +":"+lid);
				// Log.e("aqi", rhs.getName() +":"+rid);
				if (lid - rid >= 0) {
					return 1;
				} else {
					return -1;
				}

			}
		});
		return new ArrayList<SiteAqi>(citySiteList);
	}

	/**
	 * 民间设备数据
	 * 
	 * @param deviceJson
	 * @return
	 */
	public static List<Map<String, Object>> getDeviceData(String deviceJson) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		try {
			JSONArray deviceJsonArray = new JSONArray(deviceJson);
			int kk = 0;
			for (int i = 0; i < deviceJsonArray.length(); i++) {
				JSONObject obj = deviceJsonArray.getJSONObject(i);
				map = new HashMap<String, Object>();

				map.put("nickname", obj.getString("nickname"));
				map.put("address", obj.getString("address"));
				map.put("aqi", obj.getString("aqi"));
				map.put("pm2.5", obj.getString("pm2.5"));
				if (obj.getString("address").indexOf("北京") != -1) {
					list.add(kk, map);
					kk++;
				} else {
					list.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 显示排名信息
	 * 
	 * @param cityRankJson
	 * @param indexType
	 * @param rankUpOrDown
	 * @param context
	 * @param listCity
	 */
	public static void showRankData(String cityRankJson, String indexType, Boolean rankUpOrDown, Context context,
			ListView listCity) {
		List<Map<String, Object>> ranks = DataTool.getRankData(cityRankJson, indexType, rankUpOrDown);
		RankAdapter rankAdapter = new RankAdapter(ranks, context);
		listCity.setAdapter(rankAdapter);
	}

	/**
	 * 获取aqi信息
	 * 
	 * @param citys
	 * @param cityAqiDB
	 */
	public static void getAqiData(List<City> citys, final CityAqiDao cityAqiDB) {
		Log.w("DataTool", "getAqiData " + cityAqiDB.getCount());
		cityAqiDB.deleteAll();
		for (final City city : citys) {
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(Constant.SERVER_URL + city.getId(), new AsyncHttpResponseHandler() {
				@Override
				public void onStart() {
				}

				@Override
				public void onSuccess(String result) {
					// 取得添加城市及信息
					CityAqi cityAqi = DataTool.getCityAqi(result, city.getId(), city.getOrder());
					cityAqiDB.delByCityID(city.getId());
					cityAqiDB.saveData(cityAqi);
					count_city++;
				}

				@Override
				public void onFailure(Throwable error) {
				}
			});
		}
	}

	/**
	 * 获取实时天气
	 */
	/*
	 * public static void getCurrentTemp(final Context context,final int cityId)
	 * {
	 * 
	 * 
	 * AsyncHttpClient client = new AsyncHttpClient();
	 * client.get(Constant.SERVER_URL + cityId, new AsyncHttpResponseHandler() {
	 * 
	 * @Override public void onStart() {
	 * 
	 * }
	 * 
	 * @Override public void onSuccess(String result) { // 取得添加城市及信息
	 * 
	 * // Log.e("aqi", result);
	 * 
	 * CityAqi cityAqi = DataTool.getCityAqi(result, city.getId(),
	 * city.getOrder()); cityAqiDB.saveData(cityAqi); }
	 * 
	 * @Override public void onFailure(Throwable error) {
	 * 
	 * }
	 * 
	 * });
	 * 
	 * }
	 */
	/**
	 * 版本信息
	 * 
	 * @param context
	 */
	public static void getVersionJsonData(final Context context) {
		// 获取网络数据
		AsyncHttpClient client = new AsyncHttpClient();

		client.get(Constant.VERSION_URL, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
			}

			@Override
			public void onSuccess(String result) {
				SharedPreferences sp;
				try {
					JSONArray versionArray = new JSONArray(result);
					if (versionArray.length() > 0) {

						JSONObject obj = versionArray.getJSONObject(0);

						sp = context.getSharedPreferences("verdata", 0);
						sp.edit().putString("verName", obj.getString("verName")).commit();
						// Log.e("aqi", "verName"+obj.getString("verName"));
						sp.edit().putString("newVerCode", obj.getString("verCode")).commit();
						sp.edit().putString("about", obj.getString("about")).commit();
						// Log.e("aqi", obj.getString("info").toString());
						sp.edit().putString("updatedetails", obj.getString("info").toString()).commit();

						String isForce = obj.getString("isForce");

						if (isForce.equals("2")) {
							// 必须更新
							sp.edit().putInt("isUpdate", 2).commit();
						} else if (isForce.equals("1")) {
							// 选择更新
							sp.edit().putInt("isUpdate", 1).commit();
						} else {
							// 不提示
							sp.edit().putInt("isUpdate", 0).commit();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable error) {

			}
		});
	}

	/**
	 * 排名数据
	 * 
	 * @param context
	 */
	public static void getRankJsonData(final Context context) {
		// 获取网络数据
		AsyncHttpClient client = new AsyncHttpClient();

		client.get(Constant.JSON_SERVER, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {

			}

			@Override
			public void onSuccess(String result) {
				List<CityRank> cityRankList;
				List<MapPopup> mPopupList;
				SharedPreferences sp;
				String cityRankJson;
				String mapPopupJson;
				try {
					cityRankList = DataTool.getCityRank(context.getApplicationContext(), result);
					cityRankJson = EnAndDecryption.CityRankList2String(cityRankList);
					/*
					 * mPopupList =
					 * DataTool.getMapPopup(context.getApplicationContext(),
					 * result); mapPopupJson =
					 * DataTool.MapPopupList2String(mPopupList);
					 */
					sp = context.getSharedPreferences("jsondata", 0);
					sp.edit().putString("cityrankjson", cityRankJson).commit();
					sp.edit().putString("rankjson", result).commit();
					// sp.edit().putString("popupjson", mapPopupJson).commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable error) {

			}
		});
	}

	/**
	 * 获取设备数据
	 */
	public static void getDeviceJsonData(final Context context) {
		// 获取网络数据
		AsyncHttpClient client = new AsyncHttpClient();

		client.get(Constant.DEVICE_URL, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {

			}

			@Override
			public void onSuccess(String result) {
				SharedPreferences sp;
				sp = context.getSharedPreferences("jsondata", 0);
				sp.edit().putString("devicejson", result).commit();
			}

			@Override
			public void onFailure(Throwable error) {

			}
		});
	}

	/**
	 * 地图站点数据
	 * 
	 * @param context
	 */
	public static void getMapSiteData(final Context context) {

		AsyncHttpClient client = new AsyncHttpClient();

		client.get(Constant.MAP_SITE_URL, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {

			}

			@Override
			public void onSuccess(String result) {
				// List<MapSite> mapSiteList;
				SharedPreferences sp;
				// String mapSiteJson;

				try {
					// mapSiteList = DataTool.getMapSite(result);
					// mapSiteJson = DataTool.mapSiteList2String(mapSiteList);
					sp = context.getSharedPreferences("jsondata", 0);
					sp.edit().putString("mapsitejson", result).commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable error) {

			}
		});
	}

	public static void getForcastData() {
		String rootPath = DataTool.createFileDir("Download");
		NetworkTool.downloadFile(Constant.HAZESPREADFORECAST_URL, rootPath, "spread.html");
	}

	// 下载文件
	public static void getAboutData() {
		String rootPath = DataTool.createFileDir("Download");
		NetworkTool.downloadFile(Constant.ABOUT_LOGO, rootPath, "ic_launcher.png");
		NetworkTool.downloadFile(Constant.ABOUT_CODE, rootPath, "code.jpg");
		NetworkTool.downloadFile(Constant.ABOUT_URL, rootPath, "about.html");
	}

	/**
	 * 吐槽数据(全国城市)
	 */
	public static void getSpitData(final Context context, final String cityId, String content) {
		// 获取网络数据
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("cityId", cityId);
		params.put("content", content);
		params.put("isAll", "all");
		client.post(Constant.SPIT_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
			}

			@Override
			public void onSuccess(String result) {
				try {
					// 全部吐槽信息
					List<SpitContent> spitList = new ArrayList<SpitContent>();
					JSONArray array = new JSONArray(result);
					SpitContent content;
					String spitContentString;
					SharedPreferences sp;
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						content = new SpitContent();
						content.setContent(obj.getString("content"));
						content.setPubTime(obj.getString("pubtime"));
						spitList.add(content);
					}
					// Log.e("aqi", "spitList:"+spitList);
					spitContentString = EnAndDecryption.SpitList2String(spitList);
					// Log.e("aqi", "spitContentString:"+spitContentString);

					sp = context.getSharedPreferences("spitdata", 0);
					sp.edit().putString("spitjson", spitContentString).commit();
					// Log.e("aqi", "spitContentString:"+spitContentString);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable error) {

			}
		});
	}

	/**
	 * 分城市吐槽
	 * 
	 * @param context
	 * @param cityId
	 * @param content
	 */
	public static void getCitySpitData(final Context context, final String cityId, String content, String longtitude,
			String latitude) {
		// 获取网络数据
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("cityId", cityId);
		params.put("content", content);
		params.put("isAll", "");
		params.put("longtitude", longtitude);
		params.put("latitude", latitude);
		client.post(Constant.SPIT_URL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
			}

			@Override
			public void onSuccess(String result) {
				try {
					// 分城市吐槽
					List<SpitContent> spitList = new ArrayList<SpitContent>();
					JSONArray array = new JSONArray(result);
					SpitContent content;
					String spitContentString;
					SharedPreferences sp;
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						content = new SpitContent();
						content.setSpotId(cityId);
						content.setContent(obj.getString("content"));
						content.setPubTime(obj.getString("pubtime"));
						spitList.add(content);
					}
					// Log.e("aqi", "spitList:"+spitList);
					spitContentString = EnAndDecryption.SpitList2String(spitList);
					// Log.e("aqi", "spitContentString:"+spitContentString);

					sp = context.getSharedPreferences("spitdata", 0);
					sp.edit().putString("spit_" + cityId, spitContentString).commit();
					// Log.e("aqi", "spitContentString:"+spitContentString);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable error) {

			}
		});
	}

	public static void getSiteJson(final Context context, final int cityId) {

		AsyncHttpClient client = new AsyncHttpClient();
		client.get(Constant.SITE_URL + cityId, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {

			}

			@Override
			public void onSuccess(String result) {
				List<SiteAqi> siteAqis;
				SharedPreferences sp;
				// 获取siteaqis
				if (!result.toString().equals("0")) {
					siteAqis = new ArrayList<SiteAqi>();
					siteAqis = DataTool.getSiteAqi(result, cityId);
					// 存入到缓存中
					sp = context.getSharedPreferences("sitedata", 0);
					sp.edit().putString("sites_" + cityId, EnAndDecryption.SiteList2String(siteAqis)).commit();
				} else {
					sp = context.getSharedPreferences("sitedata", 0);
					sp.edit().putString("sites_" + cityId, "0").commit();
				}

			}

			@Override
			public void onFailure(Throwable error) {

			}

		});

	}

	public static List<SiteAqi> getsortsite(final Context context, final String result, final double locationLong,
			final double locationLat) {
		final List<SiteAqi> siteList = new ArrayList<SiteAqi>();
		try {

			List<SiteAqi> sortSiteList;
			SiteAqi site;

			JSONArray jsonArray = new JSONArray(result);

			for (int i = 0; i < jsonArray.length(); i++) {
				site = new SiteAqi();
				JSONObject obj = jsonArray.getJSONObject(i);
				site.setCityId(Integer.valueOf(obj.getString("cityId").toString()));
				site.setName(obj.getString("spotName"));
				site.setAqi(obj.getString("aqi"));
				site.setPm25(obj.getString("pm25"));
				site.setUpdateTime(obj.getString("pubtime"));
				site.setSpotLongitude(obj.getString("longtitude"));
				site.setSpotLatitude(obj.getString("latitude"));
				siteList.add(site);
			}

			sortSiteList = DataTool.sortCitySiteList(siteList, locationLong, locationLat);
			return sortSiteList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void getNearestSite(final Context context, final int cityId, final double locationLong,
			final double locationLat) {
		final List<SiteAqi> siteList = new ArrayList<SiteAqi>();
		Log.e("DataTool", "requestNearestSite " + locationLong + "|" + locationLat);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(Constant.SITE_URL + cityId, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				Log.i("DataTool", "getNearestSite " + Constant.SITE_URL + cityId);
				try {

					List<SiteAqi> sortSiteList;
					SiteAqi site;
					SharedPreferences sp;
					String sortListString;
					Log.i("DataTool", "ok:" + result);
					JSONArray jsonArray = new JSONArray(result);

					for (int i = 0; i < jsonArray.length(); i++) {
						site = new SiteAqi();
						JSONObject obj = jsonArray.getJSONObject(i);
						site.setCityId(Integer.valueOf(obj.getString("cityId").toString()));
						site.setName(obj.getString("spotName"));
						site.setAqi(obj.getString("aqi"));
						site.setPm25(obj.getString("pm25"));
						site.setUpdateTime(obj.getString("pubtime"));
						site.setSpotLongitude(obj.getString("longtitude"));
						site.setSpotLatitude(obj.getString("latitude"));
						siteList.add(site);
					}

					sortSiteList = DataTool.sortCitySiteList(siteList, locationLong, locationLat);
					sortListString = EnAndDecryption.SiteList2String(sortSiteList);
					sp = context.getSharedPreferences("sortSiteData", 0);
					sp.edit().putString("sortSiteList", sortListString).commit();
					sp.edit().putFloat("locationLong", (float) locationLong).commit();// 经度
					sp.edit().putFloat("locationLat", (float) locationLat).commit();// 纬度
					Log.e("DataTool", "sortSiteList:" + (float) locationLong);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}