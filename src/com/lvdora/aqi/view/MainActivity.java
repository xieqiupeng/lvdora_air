package com.lvdora.aqi.view;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.lvdora.aqi.R;
import com.lvdora.aqi.module.ModuleVersionUpdate;
import com.lvdora.aqi.util.ExitTool;
import com.lvdora.aqi.util.ScreenManager;

/**
 * 
 * 主activity 加载frament
 * 
 * @author xqp
 * 
 */
public class MainActivity extends FragmentActivity {

	private long mExitTime;
	private FragmentTabHost mTabHost;
	private RadioGroup mTabRg;

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

		// 界面管理
		ScreenManager.getScreenManager().pushActivity(this);
		ExitTool.activityList.add(MainActivity.this);

		// 检查，升级版本
		ModuleVersionUpdate mvu = new ModuleVersionUpdate(MainActivity.this);
		mvu.updateChoose();

		// 初始化5个tab页
		initTabs();
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

				SharedPreferences sp = getSharedPreferences("isFlash", 0);
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
}
