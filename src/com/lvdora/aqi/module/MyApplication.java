package com.lvdora.aqi.module;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

/**
 * 自己实现Application，
 * 
 * @1 百度地图mapActivity的初始化
 * @2 实现数据共享
 * 
 * @author xqp
 */
public class MyApplication extends Application {

	private static MyApplication mInstance = null;
	public boolean m_bKeyRight = true;
	public BMapManager mBMapManager = null;

	// 共享变量
	private Handler handler = null;

	public static MyApplication getInstance() {
		return mInstance;
	}

	// set方法
	public void setHandler(Handler handler) {
		this.handler = handler;
		Log.w("MyApplication", handler.toString());
	}

	// get方法
	public Handler getHandler() {
		return handler;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = (MyApplication) this.getApplicationContext();
		initEngineManager(this);
	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}
		if (!mBMapManager.init(new MyGeneralListener())) {
			Toast.makeText(MyApplication.getInstance().getApplicationContext(), "BMapManager初始化错误", 0).show();
		}
	}

	// 常用事件监听，用来处理异常的网络错误，授权验证错误等
	public static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(MyApplication.getInstance().getApplicationContext(), "您的网络出错啦！", 0).show();
			}
			//
			else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(MyApplication.getInstance().getApplicationContext(), "输入正确的检索条件！", 0).show();
			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError != 0) {
				Toast.makeText(MyApplication.getInstance().getApplicationContext(), "请检查您的网络连接是否正常！", 0).show();
				// MyApplication.getInstance().m_bKeyRight = false;
			}
			MyApplication.getInstance().m_bKeyRight = true;
		}
	}
}