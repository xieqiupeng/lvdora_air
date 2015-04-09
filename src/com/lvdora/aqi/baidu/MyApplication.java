package com.lvdora.aqi.baidu;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

public class MyApplication extends Application {

	private static MyApplication mInstance = null;
	public boolean m_bKeyRight = true;
	public BMapManager mBMapManager = null;

	public static MyApplication getInstance() {
		return mInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;

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