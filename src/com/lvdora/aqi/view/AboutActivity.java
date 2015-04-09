package com.lvdora.aqi.view;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;

import com.lvdora.aqi.R;
import com.lvdora.aqi.module.ModuleActivitiesManager;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.NetworkTool;
import com.lvdora.aqi.util.WebViewContentClient;

/**
 * 关于我们
 * 
 * @author Administrator
 * 
 */
public class AboutActivity extends Activity {

	private static final int UPDATE_UI = 1;
	private ImageView backView;
	private WebView aboutWebview;
	private String rootPath;
	private ProgressDialog pDialog;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_UI: {
				pDialog.dismiss();
				initView();
				break;
			}
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_more_about);
		
		// 当前页面加入activity管理模块
		ModuleActivitiesManager.getActivitiesStack().push(this);
		
		rootPath = DataTool.createFileDir("Download");
		Log.i("lvdora", "path" + rootPath);
		
		backView = (ImageView) findViewById(R.id.btn_life_back);
		backView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		if (!NetworkTool.isNetworkConnected(AboutActivity.this)) {
			initView();
		} else {
			// 判断获取数据结束
			String message = getResources().getString(R.string.getting_data);
			pDialog = ProgressDialog.show(AboutActivity.this, "", message);
			pDialog.show();
			final File file1 = new File(rootPath + "/ic_launcher.png");
			final File file2 = new File(rootPath + "/code.jpg");
			final File file3 = new File(rootPath + "/about.html");
			// initView();
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						if (file1.exists() && file2.exists() && file3.exists()) {
							mHandler.sendEmptyMessageDelayed(UPDATE_UI, 0);
							break;
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}

	private void initView() {
		aboutWebview = (WebView) findViewById(R.id.about_webview);
		// 设置WebView属性，能够执行Javascript脚本
		aboutWebview.getSettings().setJavaScriptEnabled(true);
		// 加载本地文件
		aboutWebview.loadUrl("file:///" + rootPath + "/about.html");
		// 设置Web视图
		aboutWebview.setWebViewClient(new WebViewContentClient());
	}

	// @Override
	// 设置回退
	// 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	/*
	 * public boolean onKeyDown(int keyCode, KeyEvent event) { if ((keyCode ==
	 * KeyEvent.KEYCODE_BACK) && aboutWebview.canGoBack()) {
	 * aboutWebview.goBack(); //goBack()表示返回WebView的上一页面 return true; } return
	 * false; }
	 */
}