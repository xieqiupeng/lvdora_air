package com.lvdora.aqi.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.lvdora.aqi.util.ScreenManager;
import com.lvdora.aqi.R;
import com.lvdora.aqi.adapter.CitySettingAdaper;
import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.aqi.dao.CityDao;
import com.lvdora.aqi.model.City;
import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.model.CitysIndexMap;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.EnAndDecryption;
import com.lvdora.aqi.util.ExitTool;
import com.lvdora.aqi.util.NetworkTool;
import com.lvdora.aqi.util.UpdateTool;

/**
 * 城市管理界面
 * 
 * @author xqp
 * 
 */
public class CitySettingActivity extends Activity implements OnClickListener {

	public static final int UPDATE_UI = 1;

	private ImageButton backBtn;
	// 地图刷新
	private ImageView updateImg;

	//
	private CheckBox editCheck;
	private GridView gv_citys;
	private ImageButton updateBtn;

	private CitySettingAdaper cityAdapter;
	private ArrayList<City> citys;
	private SharedPreferences sp;
	private boolean editModel = false;
	private CityAqiDao cityAqiDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_city_activity);

		ScreenManager.getScreenManager().pushActivity(this);
		ExitTool.activityList.add(CitySettingActivity.this);
		findView();

		// 测试页面加载时数据
		// testData();

		// 数据库初始化
		cityAqiDao = new CityAqiDao(CitySettingActivity.this, "");
		// 从sp中取得城市
		getCitysBySP();
		// 点击城市列表
		cityGridClick();
		// 编辑城市列表
		cityEditClick();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_life_back:
			// 返回
			Intent intent = new Intent(CitySettingActivity.this, MainActivity.class);
			sp = getSharedPreferences("isFlash", 0);
			sp.edit().putBoolean("isFlash", true).commit();
			startActivity(intent);
			this.finish();
			break;
		case R.id.update_image:
			if (!NetworkTool.isNetworkConnected(CitySettingActivity.this)) {
				Toast.makeText(CitySettingActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
			} else {
				// 更新数据
				updateData();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 返回首页
			Intent intent = new Intent(CitySettingActivity.this, MainActivity.class);
			sp = getSharedPreferences("isFlash", 0);
			sp.edit().putBoolean("isFlash", true).commit();
			startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 老方法是从sp中取得城市
	private void getCitysBySP() {
		// 获取管理城市数量
		sp = getSharedPreferences("citydata", 0);
		String cityListString = sp.getString("citys", "");
		// 得到城市
		citys = new ArrayList<City>();
		try {
			citys = (ArrayList<City>) EnAndDecryption.String2WeatherList(cityListString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d("LD", citys.toString());
	}

	/**
	 * 点击城市列表
	 */
	private void cityGridClick() {
		// 设置Adapter
		cityAdapter = new CitySettingAdaper(citys, this);
		this.gv_citys.setAdapter(cityAdapter);

		// 长按grid进入编辑
		this.gv_citys.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				cityAdapter.setFlag(1);
				cityAdapter.notifyDataSetChanged();
				editModel = true;
				CitySettingActivity.this.editCheck.setChecked(true);
				return true;
			}
		});

		// 点击grid添加城市
		this.gv_citys.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 不处于编辑状态
				if (editModel == false) {
					int addButton = CitySettingActivity.this.citys.size();
					//int addButton = CitysIndexMap.getInstance(CitySettingActivity.this).size();
					// 点击添加城市
					if (position == addButton) {
						// 最多只能添加六个城市
						if (addButton == 6) {
							Toast.makeText(CitySettingActivity.this, "最多只能添加六个城市", Toast.LENGTH_SHORT).show();
							return;
						}
						// 进入添加城市页面
						Intent intent = new Intent();
						intent.setClass(CitySettingActivity.this, CitySelectorActivity.class);
						startActivity(intent);
					}
					// 进入城市信息详情
					else {
						Intent intent = new Intent(CitySettingActivity.this, MainActivity.class);
						HomeActivity.currentIndexOut = position;
						sp = getSharedPreferences("isFlash", 0);
						sp.edit().putBoolean("isFlash", true).commit();
						startActivity(intent);
						finish();

						// 存缓存
						sp = getSharedPreferences("cur_city", 0);
						//CitysIndexMap.getInstance(CitySettingActivity.this).spToMap();
						CityAqiDao cityAqiDao = new CityAqiDao(CitySettingActivity.this,"");
						CityAqi cityAqi = cityAqiDao.selectAqiByOrder(position);
						int cityID = cityAqi.getCityId();
						sp.edit().putInt("city_id", cityID).commit();
					}
				}
			}
		});

	}

	// 给编辑按钮添加事件
	private void cityEditClick() {
		// 编辑按钮的两个形态
		this.editCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cityAdapter.setFlag(1);
					cityAdapter.notifyDataSetChanged();
					editModel = true;
				} else {
					cityAdapter.setFlag(0);
					cityAdapter.notifyDataSetChanged();
					editModel = false;
				}
			}
		});
	}

	/**
	 * 更新数据
	 */
	private void updateData() {
		// 刷新动画
		UpdateTool.startLoadingAnim(CitySettingActivity.this, updateBtn, updateImg);
		// 删除最大id号对应的数据
		int maxId = cityAqiDao.getLastUid();
		cityAqiDao.delOldData(maxId);
		// 插入新数据
		DataTool.getAqiData(citys, cityAqiDao);
		// 更新ui
		new Thread(new UpdateThread()).start();
	}

	// 发送消息
	class UpdateThread implements Runnable {
		@Override
		public void run() {
			while (true) {
				int count = cityAqiDao.getCount();
				if (count == citys.size()) {
					mHandler.sendEmptyMessageDelayed(UPDATE_UI, 0);
					break;
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_UI: {
				// 点击城市列表
				cityGridClick();
				// 点击编辑城市
				cityEditClick();
				// 刷新动画停止
				UpdateTool.stopLoadingAnim(updateBtn, updateImg);
				break;
			}
			default:
				break;
			}
		}
	};

	private void findView() {
		// 按钮的获取：返回，刷新，编辑
		this.backBtn = (ImageButton) findViewById(R.id.btn_life_back);
		this.backBtn.setOnClickListener(this);
		this.updateBtn = (ImageButton) findViewById(R.id.update_image);
		this.updateBtn.setOnClickListener(this);
		// gridView
		this.gv_citys = (GridView) findViewById(R.id.gv_life_citys);
		// checkbox
		this.editCheck = (CheckBox) findViewById(R.id.btn_life_edit);
		// 地图刷新
		this.updateImg = (ImageView) findViewById(R.id.update_image_animation);
	}
}