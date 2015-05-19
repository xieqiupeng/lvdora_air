package com.lvdora.aqi.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.lvdora.aqi.R;
import com.lvdora.aqi.module.ModuleActivitiesManager;
import com.lvdora.aqi.module.ModuleVersionUpdate;
import com.lvdora.aqi.module.MyApplication;
import com.lvdora.aqi.util.ExitTool;
import com.lvdora.aqi.util.TimeGauging;

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

	@SuppressWarnings("rawtypes")
	public final Class[] fragments = { HazeForecastActivity.class, //
			MapActivity.class, //
			HomeActivity.class, //
			RankActivity.class, //
			DeviceShareActivity.class //
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		TimeGauging.diffTime(TimeGauging.LOGO_TIME, TimeGauging.MAIN_TIME);

		// 当前页面加入activity管理模块
		ModuleActivitiesManager.getActivitiesStack().push(this);

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
				// 存缓存
				MyApplication app = (MyApplication) getApplication();
				Log.w("a", app.getHandler() + "");
				app.getHandler().sendEmptyMessage(HomeActivity.SAVE_SP);
				Log.w("b", app.getHandler() + "");
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

		// 添加tabs
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		int count = fragments.length;
		for (int i = 0; i < count; i++) {
			TabSpec tabSpec = mTabHost.newTabSpec(i + "").setIndicator(i + "");
			mTabHost.addTab(tabSpec, fragments[i], null);
		}

		// 进入HomeActivity
		mTabHost.setCurrentTab(2);

		// 切换界面
		mTabRg = (RadioGroup) findViewById(R.id.tab_rg_menu);
		mTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				SharedPreferences sp = getSharedPreferences("isFirstIn", 0);
				sp.edit().putBoolean("isFirstIn", true).commit();

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
