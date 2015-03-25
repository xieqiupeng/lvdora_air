package com.lvdora.aqi.view;

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
import org.json.JSONArray;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
//import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.lvdora.aqi.R;
import com.lvdora.aqi.util.Config;
import com.lvdora.aqi.util.Constant;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.ExitTool;
import com.lvdora.aqi.util.ScreenManager;

public class MainActivity extends FragmentActivity {

	private SharedPreferences sp;
	private long mExitTime;
	private FragmentTabHost mTabHost;
	private RadioGroup mTabRg;
	private String newVerName;
	private String updateDetails;
	private float newVerCode;
	public ProgressDialog pBar;
	private Handler handler = new Handler();
	private int isUpdate;
	private String downPath;
	private StringBuffer sb;

	// private OnMainListener mainListener;

	private final Class[] fragments = { HazeForecastActivity.class, //
			MapActivity.class, //
			HomeActivity.class, //
			RankActivity.class, //
			DeviceShareActivity.class //
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		ScreenManager.getScreenManager().pushActivity(this);
		ExitTool.activityList.add(MainActivity.this);

		sp = getSharedPreferences("verdata", 0);
		newVerCode = Float.parseFloat(sp.getString("newVerCode", "1"));
		newVerName = sp.getString("verName", "");
		updateDetails = sp.getString("updatedetails", "");
		// Log.e("aqi", "MAIN"+updateDetails);
		isUpdate = sp.getInt("isUpdate", 0);
		// 更新版本
		Float vercode = (float) Config.getVerCode(this);
		if (newVerCode > vercode) {
			if (isUpdate == 2) {
				showMustUpdateDialog();
			} else if (isUpdate == 1) {
				showUpdateDialog();
			}
		}
		//初始化5个tab页
		initTabs();
	}

	/*
	 * 初始化5个tab页
	 */
	private void initTabs() {

		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		int count = fragments.length;
		for (int i = 0; i < count; i++) {
			TabSpec tabSpec = mTabHost.newTabSpec(i + "").setIndicator(i + "");
			mTabHost.addTab(tabSpec, fragments[i], null);
		}
		mTabHost.setCurrentTab(2);

		// 切换界面
		mTabRg = (RadioGroup) findViewById(R.id.tab_rg_menu);
		mTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				sp = getSharedPreferences("isFlash", 0);
				sp.edit().putBoolean("isFlash", true).commit();
				
				// 切换界面
				switch (checkedId) {
				case R.id.tab_rb_trend:
					mTabHost.setCurrentTab(0);
					break;
				case R.id.tab_rb_map:
					mTabHost.setCurrentTab(1);
					break;
				case R.id.tab_rb_home:
					mTabHost.setCurrentTab(2);
					break;
				case R.id.tab_rb_rank:
					mTabHost.setCurrentTab(3);
					break;
				case R.id.tab_rb_more:
					mTabHost.setCurrentTab(4);
					break;
				default:
					break;
				}
			}
		});
	}

	// 完全退出应用程序
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				// 停止服务
				ExitTool.exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showMustUpdateDialog() {
		updateDetails();
		Dialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("立即更新 ").setMessage(sb.toString())
		// 设置内容
				.setPositiveButton("立即更新",// 设置确定按钮
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								pBar = new ProgressDialog(MainActivity.this);
								pBar.setTitle("正在下载");
								pBar.setMessage("请稍候...");
								pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								// 下载apk文件
								downFile(Constant.DOWNLOAD_URL);
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	/**
	 * 
	 */
	private void showUpdateDialog() {

		updateDetails();

		Dialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("建议立即更新 ?").setMessage(sb.toString())
		// 设置内容
				.setPositiveButton("更新",// 设置确定按钮
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								pBar = new ProgressDialog(MainActivity.this);
								pBar.setTitle("正在下载");
								pBar.setMessage("请稍候...");
								pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								// 下载apk文件
								pBar.show();
								downFile(Constant.DOWNLOAD_URL);
							}

						}).setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 点击"取消"按钮之后退出程序
						// finish();
					}
				}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	/**
	 * 版本更新信息
	 */
	public void updateDetails() {
		String verName = Config.getVerName(this);
		sb = new StringBuffer();
		JSONArray details;
		try {
			sb.append("发现新版本:");
			sb.append(newVerName + "\r\n");
			sb.append("当前版本:");
			sb.append(verName + "\r\n");
			sb.append("新版本增加功能:" + "\r\n");
			details = new JSONArray(updateDetails);
			for (int i = 0; i < details.length(); i++) {
				sb.append(details.get(i) + "\r\n");
			}
			/*
			 * sb.append("如不更新则无法使用新功能！" + "\r\n"); sb.append("建议您立即更新！！！");
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文件下载
	 * 
	 * @param url
	 */
	public void downFile(final String url) {
		downPath = DataTool.createFileDir("Download");
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

						File file = new File(downPath, Config.UPDATE_SAVENAME);
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
					down();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}.start();

	}

	/**
	 * 进度条
	 */
	public void down() {
		handler.post(new Runnable() {
			public void run() {
				pBar.cancel();
				update();
			}
		});

	}

	/**
	 * 更新应用程序包
	 */
	public void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(downPath, Config.UPDATE_SAVENAME)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

}
