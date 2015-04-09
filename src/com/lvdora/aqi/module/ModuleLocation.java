package com.lvdora.aqi.module;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.BMapManager;
import com.lvdora.aqi.view.LogoActivity;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * 解决问题：定位模块
 * 
 * @author xqp
 */
public class ModuleLocation {

	private Context context;
	private LocationClient mLocationClient;
	// 类变量，对外接口，常用定位属性
	public static String city;
	public static String address;
	public static double longitude;
	public static double latitude;

	// 旧版定位
	private BMapManager mBMapMan = null;

	public ModuleLocation(Context context) {
		this.context = context;
	}

	/**
	 * 1 旧版定位
	 */
	public void locateCity() {
		mBMapMan = new BMapManager(((Activity) context).getApplication());
		mBMapMan.init(null);

		// 第一次定位
		mLocationClient = new LocationClient(context.getApplicationContext());
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		mLocationClient.setLocOption(option);

		// 注册位置监听
		mLocationClient.registerLocationListener(new MyLocationListener());
		mLocationClient.requestLocation();
		mLocationClient.start();
	}

	/**
	 * 开始定位，设置定位参数
	 */
	public void locateStart() {
		// 1 打开定位客户端
		mLocationClient = new LocationClient(context.getApplicationContext());
		// 2 注册定位监听器
		mLocationClient.registerLocationListener(new MyLocationListener());
		// 3 设置定位选项
		LocationClientOption option = new LocationClientOption();
		// 设置定位模式
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setAddrType("all");
		// 返回的定位结果是百度经纬度
		option.setCoorType("bd09ll");
		option.setOpenGps(false);
		// 设置发起定位请求的间隔时间为5000ms
		option.setScanSpan(999);
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		// 4 开启定位
		mLocationClient.start();
	}

	/**
	 * 2 定位回调监听结果
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// 没有定位到
			if (location == null) {
				return;
			}
			// 类变量赋值，常用结果
			city = location.getCity();
			address = location.getAddrStr();
			longitude = location.getLongitude();
			latitude = location.getLatitude();
			Log.v("ModuleLocation", city + address + longitude + latitude + "");
			// 处理返回结果
			resultDispose();
		}
	}

	/**
	 * 3 处理返回结果
	 */
	private void resultDispose() {
		// logo界面里进行处理
		((LogoActivity) context).dispose(city, longitude, latitude);
	}
}