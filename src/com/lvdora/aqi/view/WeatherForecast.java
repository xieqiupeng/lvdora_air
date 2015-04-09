package com.lvdora.aqi.view;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lvdora.aqi.R;
import com.lvdora.aqi.adapter.WeatherForecastAdapter;
import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.module.ModuleActivitiesManager;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.NetworkTool;
import com.lvdora.aqi.util.ShareTool;

public class WeatherForecast extends Activity implements OnClickListener {
	private int cityId;
	private String cityName;
	private TextView dateText;
	private TextView weatherText;
	private TextView winForceText;
	private TextView cityNameText;
	private TextView tempText;

	private ImageView backImageBtn;
	private ImageView updateAnimImg;
	private ImageButton shareBtn;
	private ImageButton updateDataBtn;
	
	private ProgressDialog pDialog;
	private CityAqiDao cityAqiDao;
	private CityAqi cityAqi;
	private ListView listWeather;


	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_detail);
		
		// 当前页面加入activity管理模块
		ModuleActivitiesManager.getActivitiesStack().push(this);
		
		cityId = getIntent().getIntExtra("cityId", 0);
		
		backImageBtn =(ImageView) findViewById(R.id.devices_back_btn);
		backImageBtn.setOnClickListener(this);
		updateDataBtn = (ImageButton) findViewById(R.id.weather_update_image);
		updateDataBtn.setOnClickListener(this);
		shareBtn = (ImageButton) findViewById(R.id.weather_share_image);
		shareBtn.setOnClickListener(this);
		updateAnimImg = (ImageView) findViewById(R.id.weather_update_image_animation);
		initView();
		showViewData();
	}

	private void initView() {
		tempText = (TextView) findViewById(R.id.tempText);
		dateText = (TextView) findViewById(R.id.dateText);
		winForceText = (TextView) findViewById(R.id.winForceText);
		weatherText = (TextView) findViewById(R.id.weather);
		cityNameText = (TextView) findViewById(R.id.cityNameText);
		listWeather =(ListView) findViewById(R.id.weather_list);
	}

	private void showViewData() {
		cityAqiDao = new CityAqiDao(WeatherForecast.this, "");
		cityAqi = cityAqiDao.getItem(cityId);
		tempText.setText(cityAqi.getTemp_now());
		dateText.setText(cityAqi.getTempCurrentPubtime());
		winForceText.setText(cityAqi.getWind());
		cityNameText.setText(cityAqi.getCityName());
		weatherText.setText(cityAqi.getWeather0());
		
	
		WeatherForecastAdapter weatherAdapter = new WeatherForecastAdapter(cityAqi, WeatherForecast.this);
		listWeather.setAdapter(weatherAdapter);
		listWeather.setClickable(false);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.devices_back_btn:
			finish();
			break;
		case R.id.weather_share_image:
			if (NetworkTool.isNetworkConnected(WeatherForecast.this)) {
				String filePath = DataTool.createFileDir("Share_Imgs");
				String tmpTime = String.valueOf(System.currentTimeMillis());
				String path = filePath + "/"+getResources().getString(R.string.app_name)+ "_五天天气预报_" + tmpTime + ".png";
				ShareTool.shoot(path, WeatherForecast.this);
				ShareTool.SharePhoto(path, "分享", "天气信息", WeatherForecast.this);
			} else {
				Toast.makeText(WeatherForecast.this, R.string.network_error,
						Toast.LENGTH_SHORT).show();
			}
		    break;
		case R.id.weather_update_image:
		
	        break;
		default:
			break;
		}
	}

}
