package com.lvdora.aqi.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.lvdora.aqi.R;
import com.lvdora.aqi.view.CitySelectorActivity;
import com.lvdora.aqi.view.LogoActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkTool {

	/**
	 * 是否连接网络
	 * 
	 * @true 已连接
	 * @false 未连接
	 */
	public static boolean NETWORK_CONNECTION = false;

	private static Context context;

	public NetworkTool(Context context) {
		this.context = context;
	}

	/*
	 * 判断网络是否连接
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager cm;
			cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (networkInfo != null) {
				NETWORK_CONNECTION = true;
				return networkInfo.isAvailable();
			}
		}
		NETWORK_CONNECTION = false;
		return false;
	}

	/*
	 * 判断网络是否连接
	 */
	public void isNetworkConnected() {
		ConnectivityManager cm;
		cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null) {
			NETWORK_CONNECTION = true;
		}
		NETWORK_CONNECTION = false;
	}

	/*
	 * 提示网络错误
	 */
	public void toastNetworkDisconnection(Context context) {
		isNetworkConnected();
		if (!NETWORK_CONNECTION) {
			Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * 网络设置提示
	 */
	public void alertWiFiConnection(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("网络设置提示");
		builder.setMessage("网络连接不可用,是否进行设置?");
		// 点击确定
		builder.setPositiveButton("设置", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 进入wifi设置
				Intent intent = new Intent();
				// 判断手机系统的版本 即API大于10 就是3.0或以上版本
				if (android.os.Build.VERSION.SDK_INT > 10) {
					intent.setAction(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
				}
				//
				else {
					String arg0 = "com.android.settings";
					String arg1 = "com.android.settings.WirelessSettings";
					ComponentName name = new ComponentName(arg0, arg1);
					intent.setComponent(name);
					intent.setAction("android.intent.action.VIEW");
				}
				activity.startActivity(intent);
				activity.finish();
			}
		});
		// 点击取消
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ExitTool.exit();
			}
		});
		builder.show();
	}

	/**
	 * 下载
	 * 
	 * @param url
	 * @param filePath
	 * @param fileName
	 */
	public static void downloadFile(final String url, final String filePath, final String fileName) {
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File file = new File(filePath, fileName);
						fileOutputStream = new FileOutputStream(file);
						byte[] buf = new byte[1024];
						int ch = -1;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}