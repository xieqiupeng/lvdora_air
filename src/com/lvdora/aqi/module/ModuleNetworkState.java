package com.lvdora.aqi.module;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

/**
 * 测试ConnectivityManager
 * 
 * @1 ConnectivityManager主要管理和网络连接相关的操作
 * @2 相关的TelephonyManager则管理和手机、运营商等的相关信息；
 * @3 WifiManager则管理和wifi相关的信息。
 * @4 想访问网络状态，首先得添加权限<uses-permissionandroid:name=
 *    "android.permission.ACCESS_NETWORK_STATE"/>
 * @5 NetworkInfo类包含了对wifi和mobile两种网络模式连接的详细描述
 * @6 通过其getState()方法获取的State对象,则代表着连接成功与否等状态。
 * 
 */
public class ModuleNetworkState {

	private Context context;
	private NetworkInfo.State state;

	
	/*
	 * TODO　网络的使用方法
	 */
	public void testConnectivityManager() {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取代表联网状态的NetWorkInfo对象
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		// 获取当前的网络连接是否可用
		boolean available = networkInfo.isAvailable();
		if (available) {
			Log.i("通知", "当前的网络连接可用");
		} else {
			Log.i("通知", "当前的网络连接不可用");
		}

		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if (State.CONNECTED == state) {
			Log.i("通知", "GPRS网络已连接");
		}

		state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (State.CONNECTED == state) {
			Log.i("通知", "WIFI网络已连接");
		}

		// 跳转到无线网络设置界面
		context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		// 跳转到无限wifi网络设置界面
		context.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
	}
}