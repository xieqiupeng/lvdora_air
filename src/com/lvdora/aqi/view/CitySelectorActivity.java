package com.lvdora.aqi.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lvdora.aqi.R;
import com.lvdora.aqi.adapter.CitySelectorAdapter;
import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.aqi.dao.CityDao;
import com.lvdora.aqi.model.City;
import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.model.CitysIndexMap;
import com.lvdora.aqi.model.Province;
import com.lvdora.aqi.module.ModuleActivitiesManager;
import com.lvdora.aqi.util.AsyncHttpClient;
import com.lvdora.aqi.util.AsyncHttpResponseHandler;
import com.lvdora.aqi.util.Constant;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.EnAndDecryption;
import com.lvdora.aqi.util.NetworkTool;

/**
 * 城市选择界面
 * 
 * @author xqp
 * 
 */
public class CitySelectorActivity extends Activity implements OnClickListener {

	private ExpandableListView lv_citys;
	private List<Province> provinces;
	private SharedPreferences sp;
	private CityAqiDao cityAqiDao;
	private List<City> citys;
	private int cityId;
	private int order;
	private String cityName;
	private ProgressDialog pDialog;
	private ImageButton backBtn;
	private TextView beijingText;
	private TextView shanghaiText;
	private TextView chengduText;
	private TextView guangzhouText;
	private TextView shenzhenText;
	private String longtitude;
	private String latitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_city_activity);

		// 当前页面加入activity管理模块
		ModuleActivitiesManager.getActivitiesStack().push(this);
		
		findView();

		// 吐槽数据，经纬度
		sp = getSharedPreferences("spitdata", 0);
		longtitude = sp.getString("longtitude", "");
		latitude = sp.getString("latitude", "");

		// 从sp取得所有城市，存入cityList
		sp = getSharedPreferences("citydata", 0);
		String citysString = sp.getString("citys", "");

		citys = new ArrayList<City>();
		try {
			citys = (ArrayList<City>) EnAndDecryption.String2WeatherList(citysString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 从数据库查询所有省份
		this.provinces = new CityDao(this).getAll();
		// 添加省市列表
		this.lv_citys = (ExpandableListView) findViewById(R.id.lv_citys);
		this.lv_citys.setAdapter(new CitySelectorAdapter(this, provinces));
		// 选择城市事件
		this.lv_citys.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				// 网络
				if (!NetworkTool.isNetworkConnected(CitySelectorActivity.this)) {
					Toast.makeText(CitySelectorActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
					return false;
				}

				// 点击得到城市名称和id
				cityName = provinces.get(groupPosition).getCitys().get(childPosition).getName();
				cityId = provinces.get(groupPosition).getCitys().get(childPosition).getId();
				order = citys.size();
				Log.e("ID", cityId + "");

				// 验证城市是否存在
				if (!validateCity()) {
					return false;
				}
				// 获取添加城市的天气信息
				getWeatherJson();
				// 获取添加城市吐槽数据
				DataTool.getCitySpitData(getApplicationContext(), String.valueOf(cityId), "", longtitude, latitude);
				return true;
			}
		});
	}

	// 验证城市是否存在
	public boolean validateCity() {
		for (City city : citys) {
			if (city.getId() == cityId) {
				Toast.makeText(CitySelectorActivity.this, "该城市已添加", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}

	// 将城市添加到sp
	public void saveData() {

		City city = new City();
		city.setId(cityId);
		city.setName(cityName);
		city.setOrder(order);
		citys.add(city);

		// 存映射
		CitysIndexMap.getInstance(this).put(order, cityId);

		// 将城市添加到sp
		sp = getSharedPreferences("citydata", 0);
		String cityString = EnAndDecryption.CityList2String(citys);
		sp.edit().putString("citys", cityString).commit();
	}

	// 获取添加的空气信息
	public void getWeatherJson() {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(Constant.SERVER_URL + cityId, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				pDialog = ProgressDialog.show(CitySelectorActivity.this, "",
						getResources().getString(R.string.getting_data), true, true, new OnCancelListener() {
							@Override
							public void onCancel(DialogInterface dialog) {
								return;
							}
						});
				pDialog.show();
				pDialog.setCancelable(true);
				pDialog.setCanceledOnTouchOutside(true);
			}

			@Override
			public void onSuccess(String content) {
				Log.e("result", content);
				// 获得添加城市的天气信息
				CityAqi cityAqi = DataTool.getCityAqi(content, cityId, order);
				if (cityAqi.getAqi().equals("--")) {
					Toast.makeText(CitySelectorActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
					// 取消进度条
					pDialog.cancel();
					return;
				}
				// 添加到数据库
				cityAqiDao = new CityAqiDao(CitySelectorActivity.this, "");
				cityAqiDao.saveData(cityAqi);
				// 取消进度条
				pDialog.cancel();
				// 将城市添加到缓冲中
				saveData();
				// 跳转到城市管理界面
				Intent intent = new Intent();
				intent.setClass(CitySelectorActivity.this, CitySettingActivity.class);
				startActivity(intent);
				finish();
			}

			@Override
			public void onFailure(Throwable error) {
			}
		});
	}

	@Override
	public void onClick(View v) {
		// 网络
		if (!NetworkTool.isNetworkConnected(CitySelectorActivity.this)) {
			Toast.makeText(CitySelectorActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
			return;
		}

		switch (v.getId()) {
		case R.id.tv_beijing:
			order = citys.size();
			cityId = 18;
			cityName = "北京";
			if (!validateCity()) {
				return;
			}
			getWeatherJson();
			break;
		case R.id.tv_shanghai:
			order = citys.size();
			cityId = 286;
			cityName = "上海";
			if (!validateCity()) {
				return;
			}
			getWeatherJson();
			break;
		case R.id.tv_guangzhou:
			order = citys.size();
			cityId = 45;
			cityName = "广州";
			if (!validateCity()) {
				return;
			}
			getWeatherJson();
			break;
		case R.id.tv_shenzhen:
			order = citys.size();
			cityId = 56;
			cityName = "深圳";
			if (!validateCity()) {
				return;
			}
			getWeatherJson();
			break;
		case R.id.tv_shenyang:
			order = citys.size();
			cityId = 221;
			cityName = "沈阳";
			if (!validateCity()) {
				return;
			}
			getWeatherJson();
			break;
		case R.id.btn_life_back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void findView() {
		this.backBtn = (ImageButton) findViewById(R.id.btn_life_back);
		this.backBtn.setOnClickListener(this);
		this.beijingText = (TextView) findViewById(R.id.tv_beijing);
		this.beijingText.setOnClickListener(this);
		this.shanghaiText = (TextView) findViewById(R.id.tv_shanghai);
		this.shanghaiText.setOnClickListener(this);
		this.chengduText = (TextView) findViewById(R.id.tv_shenzhen);
		this.chengduText.setOnClickListener(this);
		this.guangzhouText = (TextView) findViewById(R.id.tv_guangzhou);
		this.guangzhouText.setOnClickListener(this);
		this.shenzhenText = (TextView) findViewById(R.id.tv_shenyang);
		this.shenzhenText.setOnClickListener(this);
	}
}
