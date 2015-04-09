package com.lvdora.aqi.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lvdora.aqi.R;
import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.aqi.dao.CityDao;
import com.lvdora.aqi.db.DBManager;
import com.lvdora.aqi.model.City;
import com.lvdora.aqi.model.CitysIndexMap;
import com.lvdora.aqi.module.ModuleActivitiesManager;
import com.lvdora.aqi.module.ModuleLocation;
import com.lvdora.aqi.thread.ThreadServerInteraction;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.EnAndDecryption;
import com.lvdora.aqi.util.ExitTool;
import com.lvdora.aqi.util.NetworkTool;

/**
 * 加载logo界面
 * 
 * @author xqp
 * 
 */
public class LogoActivity extends Activity {

	private DBManager dbManager;

	private List<City> citys = new ArrayList<City>();
	private SharedPreferences sp;
	public boolean isGPS = false;
	public boolean isGPS_2 = false;

	// private List<SiteAqi> citySiteList;
	// private String locationCitySiteJson;
	// 地图管理器
	private LinearLayout logo_layout;
	private int cityID;

	boolean isSetToBeijing = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo_activity);
		Log.d("LogoActivity", "onCreate");

		// 当前页面加入activity管理模块
		ModuleActivitiesManager.getActivitiesStack().push(this);

		// 保存运行时间的毫秒数到缓存
		final long loadTime = System.currentTimeMillis();
		sp = getSharedPreferences("autoupdate", 0);
		sp.edit().putLong("mapLoadTime", loadTime).commit();
		sp.edit().putLong("homeLoadTime", loadTime).commit();
		sp.edit().putLong("rankLoadTime", loadTime).commit();
		sp.edit().putLong("deviceloadTime", loadTime).commit();

		// 创建根目录
		DataTool.createSDCardDir();

		// 如果第一次没有网络连接，强制退出
		NetworkTool network = new NetworkTool(LogoActivity.this);
		if (!NetworkTool.isNetworkConnected(LogoActivity.this)) {
			// 提示设置网络
			network.alertWiFiConnection(LogoActivity.this);
		}
		// 有网络连接
		else {
			pageAnimation();
		}
	}

	@Override
	protected void onDestroy() {
		Log.d("LogoActivity", "onDestroy");
		super.onDestroy();
	}

	/*
	 * 设置透明度变化
	 */
	private void pageAnimation() {
		this.logo_layout = (LinearLayout) findViewById(R.id.ll_logo);
		// 透明度变化动画
		AlphaAnimation localAnimation = new AlphaAnimation(0.1f, 1.0f);
		localAnimation.setDuration(2000);
		this.logo_layout.startAnimation(localAnimation);
		// 监听器
		localAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				// 导入省市数据库
				File file = new File(DBManager.DB_TOTAL_PATH);
				if (!file.exists()) {
					dbManager = new DBManager(LogoActivity.this);
					dbManager.openDatabase();
				}

				// TODO 第一次定位城市
				ModuleLocation locate = new ModuleLocation(LogoActivity.this);
				locate.locateCity();

				// 获取吐槽数据
				DataTool.getSpitData(getApplicationContext(), "", "");
				// 获取排名信息及全国城市地图数据
				DataTool.getRankJsonData(getApplicationContext());
				// 获取全国地图站点数据
				DataTool.getMapSiteData(getApplicationContext());
				// 获取版本信息
				DataTool.getVersionJsonData(getApplicationContext());
				// 获取预警信息
				DataTool.getForcastData();
				// 获取民间设备数据
				DataTool.getDeviceJsonData(getApplicationContext());
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
		});
	}

	/**
	 * 重新定位城市
	 * 
	 * @cityListAutoArrange 定位后城市列表的自动变化
	 * 
	 * @param location
	 */
	public void dispose(String locateCity, double jingdu, double weidu) {

		// 0 TODO 手动设置定位城市，进行测试
		double[][] lonLat = {
				// 0 保定市 游泳馆
				{ 115.472963, 38.878139 },
				// 1 深圳市 荔园
				{ 114.064781, 22.560473 },
				// 2 杭州市 千岛湖
				{ 119.051449, 29.61001 },
				// 3 天津 西站
				{ 117.172509, 39.164913 } };
		locateCity = "天津市";
		jingdu = lonLat[3][0];
		weidu = lonLat[3][1];

		// 1 加载缓存城市
		loadCityListFromSP();

		// 2 加载定位城市，与db和缓存定位城市对比，将定位城市id存入类变量
		cityID = loadLocateCity(locateCity);
		Log.v("LogoActivity", "dispose " + cityID + ":" + citys.toString());

		// 3 解决定位城市和缓存城市的冲突
		resolveConflict();

		// 4 序号重编
		listReorder();

		// 如果定位北京则将经纬度设为天工大厦
		if (isSetToBeijing) {
			jingdu = 116.361555;
			weidu = 39.993906;
		}
		// 5 存sp
		listToSP(jingdu, weidu);

		// 6 请求详细aqi数据，回调存db
		sendRequestForAqis();

		// 争取缓存时间
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 9 跳转到主界面
		Intent intent = new Intent();
		intent.setClass(LogoActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 加载缓存城市
	 * 
	 * @return ture为空，false不为空
	 */
	private boolean loadCityListFromSP() {
		// 取收藏城市列表，from sp citydata
		sp = getSharedPreferences("citydata", 0);
		String citysStr = sp.getString("citys", "");
		if (citysStr.equals("")) {
			return true;
		}
		// 加载收藏城市list
		citys = EnAndDecryption.String2WeatherList(citysStr);
		return false;
	}

	/*
	 * 加载定位城市，与db和缓存定位城市对比
	 */
	private int loadLocateCity(String locateCity) {
		Log.d("LogoActivity", "loadLocateCity " + citys.toString());
		// 0 预处理
		locateCity = locateCity.substring(0, locateCity.length() - 1);
		// 得cityID和isHaveAqi
		CityDao cityDao = new CityDao(LogoActivity.this);
		int cityID = cityDao.getCityId(locateCity); // 定位城市id
		boolean isHaveAqi = false;// 定位城市是否有aqi数据
		if (cityID != -1) {
			isHaveAqi = cityDao.isHaveAqi(locateCity);
		}

		// 1 缓存城市为空，添加定位城市
		if (citys == null || citys.size() == 0) {
			// 新逻辑
			CitysIndexMap.getInstance(this).put(0, cityID);
			// 旧逻辑兼容
			citys = new ArrayList<City>();
			City city = new City();
			city.setId(cityID);
			city.setOrder(0);
			city.setName(locateCity);
			citys.add(city);
			return cityID;
		}
		// 2 定位城市不在中国，或者小城镇没有Aqi数据
		if (cityID == -1 || isHaveAqi == false) {
			// 定位城市设为北京
			locateBeijing();
			return 18;
		}
		// 3 定位城市更新，取代缓存数据
		if (citys.get(0).getName() != locateCity) {
			citys.remove(0);
			City city = new City();
			city.setId(cityID);
			city.setOrder(0);
			city.setName(locateCity);
			citys.add(0, city);
			return cityID;
		}
		return -1;
	}

	// 3 解决定位城市和缓存城市的冲突
	private void resolveConflict() {
		// 冒泡排序，去掉后边的
		for (int i = 0; i < citys.size() - 1; i++) {
			// 从后往前
			for (int j = citys.size() - 1; j > i; j--) {
				if (citys.get(j).getId() == citys.get(i).getId()) {
					citys.remove(j);
				}
			}
		}
		Log.v("LogoActivity", citys.toString());
	}

	// 4 list序号重排，无奈，结构不好
	private void listReorder() {
		// list存map
		for (City city : citys) {
			int order = city.getOrder();
			int id = city.getId();
			CitysIndexMap.getInstance(this).put(order, id);
		}
		// map重排
		CitysIndexMap.getInstance(this).reorder();
		// 重编号
		for (City city : citys) {
			int id = city.getId();
			int order = CitysIndexMap.getInstance(this).getKeyByValue(id);
			city.setOrder(order);
		}
	}

	// 5 存sp
	private void listToSP(double jingdu, double weidu) {
		// 1 存入sp,citydata
		String cityString = EnAndDecryption.CityList2String(citys);
		sp = getSharedPreferences("citydata", 0);
		sp.edit().putString("citys", cityString).commit();

		// 2 存定位城市
		sp = getSharedPreferences("location", 0);
		sp.edit().putInt("cityId", cityID).commit();

		// 3 存spitdata 经纬度
		sp = getSharedPreferences("spitdata", 0);
		sp.edit().putString("longtitude", jingdu + "").commit();
		sp.edit().putString("latitude", weidu + "").commit();

		// 4 存最近站点
		DataTool.getNearestSite(getApplicationContext(), cityID, jingdu * 1E6, weidu * 1E6);

		// 5 存吐槽城市
		for (int i = 0; i < citys.size(); i++) {
			DataTool.getCitySpitData(getApplicationContext(), String.valueOf(citys.get(i).getId()), "", jingdu + "",
					weidu + "");
		}

		// 6 存GPS
		isGPS = true;
		if (isGPS) {
			sp = getSharedPreferences("isFirstIn", 0);
			sp.edit().putBoolean("isFirstIn", false).commit();
			isGPS_2 = false;
		}
	}

	// 6 从服务器获取aqi数据
	private void sendRequestForAqis() {
		Log.w("LogoActivity", "sendRequestForAqis");
		ThreadServerInteraction msi = new ThreadServerInteraction(LogoActivity.this);
		msi.sendRequestForAqis(citys);
	}

	/**
	 * 定位城市设为北京
	 */
	private void locateBeijing() {
		City city = new City();
		city.setId(18);
		city.setOrder(0);
		city.setName("北京");
		// 定位城市设为北京
		citys.add(0, city);
		CitysIndexMap.getInstance(this).put(0, 18);
		isSetToBeijing = true;
	}

	/**
	 * 打开GPS
	 */
	private void turnGPSOn() {
		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", true);
		this.sendBroadcast(intent);

		String provider = Settings.Secure.getString(this.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!provider.contains("gps")) { // if gps is disabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			this.sendBroadcast(poke);
		}
	}

	// GPS定位
	private void locateByGPS() {
		// 有网络的情况,排除不是第一次没有网络的情况
		// 有网络时使用新数据，删除旧数据，无网络时使用旧数据
		// if (NetworkTool.isNetworkConnected(LogoActivity.this)) {
		// //全部删除
		// //cityAqiDB.delOldData(iDbItems);
		// //sp.edit().putInt("iDbItems", iDbItems).commit();
		// }
		// try {
		// Thread.sleep(1000);
		// } catch (Exception e) {
		// }

		// if没有定位到，返回去重新定位
		long NowTime = System.currentTimeMillis();
		if (isGPS && isGPS_2) {
			sp = getSharedPreferences("isFirstIn", 0);
			sp.edit().putBoolean("isFirstIn", false).commit();
			Intent intent = new Intent();
			intent.setClass(LogoActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		} else if (System.currentTimeMillis() - NowTime > 5000) {
			sp = getSharedPreferences("isFirstIn", 0);
			sp.edit().putBoolean("isFirstIn", false).commit();
			Intent intent = new Intent();
			intent.setClass(LogoActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
			// locateCity();
		}
	}

	/**
	 * 没看懂什么意思，好像是传递了一个缩略名
	 */
	private void ShortcutIcon() {
		Intent shortcutIntent = new Intent();
		shortcutIntent.setComponent(new ComponentName("com.lvdora.aqi", "com.lvdora.aqi.view.LvdoraGuide"));
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		final Intent putShortCutIntent = new Intent();
		putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

		// Sets the custom shortcut's title
		putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Title");
		putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.icon));
		putShortCutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		sendBroadcast(putShortCutIntent);
	}

	/**
	 * 关闭GPS
	 */
	private void closeGPS() {
		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", false);
		this.sendBroadcast(intent);

		String provider = Settings.Secure.getString(this.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!provider.contains("gps")) { // if gps is disabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			this.sendBroadcast(poke);
		}
	}
}