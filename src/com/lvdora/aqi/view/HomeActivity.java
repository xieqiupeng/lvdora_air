package com.lvdora.aqi.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lvdora.aqi.R;
import com.lvdora.aqi.adapter.ViewPagerAdapter;
import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.aqi.model.City;
import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.model.CitysIndexMap;
import com.lvdora.aqi.model.SiteAqi;
import com.lvdora.aqi.model.SpitContent;
import com.lvdora.aqi.module.ModuleDataClean;
import com.lvdora.aqi.module.ModuleSPIO;
import com.lvdora.aqi.module.ModuleVersionUpdate;
import com.lvdora.aqi.util.AsyncHttpClient;
import com.lvdora.aqi.util.AsyncHttpResponseHandler;
import com.lvdora.aqi.util.Constant;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.DateUtil;
import com.lvdora.aqi.util.EnAndDecryption;
import com.lvdora.aqi.util.ExitTool;
import com.lvdora.aqi.util.GradeTool;
import com.lvdora.aqi.util.NetworkTool;
import com.lvdora.aqi.util.ShareTool;
import com.lvdora.aqi.util.TitleActionItem;
import com.lvdora.aqi.util.TitlePopup;
import com.lvdora.aqi.util.TitlePopup.OnItemOnClickListener;
import com.lvdora.aqi.util.UpdateTool;

public class HomeActivity extends Fragment implements OnClickListener {

	public static final int UPDATE_UI = 1;
	public static final int SPIT_PUBLISH = 2;
	public static final int SPIT_ON = 3;

	// 当前页面城市标识符
	public static int currentIndexOut = 0;
	private int currentIndex;
	private int curViewId;
	private int currentID = 0;

	// 翻页指示器
	private List<ImageView> indicators;
	private LinearLayout indicatorLayout;
	private int aboutVersion = 0;

	private TitlePopup titlePopup;
	public boolean isFlash = false;
	private View homeView;
	private List<View> views;
	private ViewPager viewPager;
	private ViewPagerAdapter pagerAdapter;
	// 城市数量
	private int cityCount;
	// 城市列表
	private List<City> citys;
	private List<CityAqi> cityAqis;
	private CityAqiDao cityAqiDB;

	private int cityId;
	private String cityName;
	private LinearLayout usaLayout;
	private LinearLayout nearestSiteLayout;

	private Boolean onOrOff = false;
	private int about;
	// 吐槽信息
	private LinearLayout spitLayout;
	private ImageView spitPublish;
	private ImageView spitOff;
	private SpitContent mySpit;
	private TextView spitFirst;
	private TextView spitSecond;
	private TextView spitThird;

	private String longtitude;
	private String latitude;
	// private View line;
	// private AutoScrollTextView autoScrollTextView;

	private TextView cityNameText;

	private ImageView citySettingBtn;
	private ImageButton updateDataBtn;
	private ImageView shareBtn;
	private ImageView updateAnimImg;
	private ImageView menuImg;

	private ImageView siteIntoBtn;
	private int nowCityId;
	private long publishTime;
	//
	private int pubCount = 0;
	private List<SiteAqi> siteList = new ArrayList<SiteAqi>();

	private List<SpitContent> cityContentList = new ArrayList<SpitContent>();
	// private List<SpitContent> contentList = new ArrayList<SpitContent>();

	// 版本检测
	private float newVerCode;
	private String newVerName;
	private String updateDetails;
	public ProgressDialog pBar;
	private StringBuffer sb;
	private String downPath;
	private Handler handler = new Handler();

	// FlushFlag=true刷新
	public Boolean FlushFlag = false;
	private Boolean first_update = true;
	private Boolean onstart_flush = false;
	private RelativeLayout forecast_index_layout;

	// aqi信息
	private RelativeLayout cityLayout;
	private TextView aqiValueText;
	private TextView aqiGradeText;
	private TextView updateTimeText;
	private TextView suggestionText;

	// 天气信息
	private RelativeLayout cityTemperature;
	private LinearLayout moreWetherForecast;
	private TextView curDateText;
	private TextView curTempText;
	private TextView weatherText;
	private TextView windText;

	private TextView firstDayText;
	private TextView secondDayText;
	private ImageView firstWeatherImag;
	private ImageView secondWeatherImag;

	// 美领馆信息
	private TextView usa_name;
	private TextView usa_aqiValue_text;
	private TextView usa_aqiGrade_text;
	private TextView usa_pm25Value_text;
	private TextView usa_updateTime_text;

	// 最近站点
	private TextView nearnest_site_name_text;
	private TextView nearnest_site_aqiValue_text;
	private TextView nearnest_site_aqiGrade_text;
	private TextView nearnest_site_pm25Value_text;
	private TextView nearnest_site_updateTime_text;

	// 污染物指标
	private TextView pm25_value_text;

	public SharedPreferences sp;

	private ProgressDialog pDialog;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_UI: {
				initView();
				viewDataInit();
				initIndicators();
				break;
			}
			case SPIT_PUBLISH: {
				Toast.makeText(getActivity(), "吐槽成功", Toast.LENGTH_SHORT).show();
				// initCitySpit();
				showSpitContent();
				break;
			}
			case SPIT_ON: {
				// showSpitView(tag);
				break;
			}
			default:
				break;
			}
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		onstart_flush = false;

		// sortSiteData是当前城市的站点数据
		sp = getActivity().getSharedPreferences("sortSiteData", 0);
		siteList = EnAndDecryption.String2SiteList(sp.getString("sortSiteList", ""));

		// cur_city是当前所有选择的城市
		sp = getActivity().getSharedPreferences("cur_city", 0);
		currentID = sp.getInt("city_id", 18);

		// Log.e("aqi", "siteList:"+siteList.toString());
		// 取得吐槽缓存数据
		/*
		 * sp = getActivity().getSharedPreferences("spitdata", 0); contentList =
		 * DataTool.String2SpitContentList(sp.getString( "spitjson", ""));
		 */

		// 保存运行时间到自动更新缓存，精确到毫秒
		long loadTime = System.currentTimeMillis();
		sp = getActivity().getSharedPreferences("autoupdate", 0);
		sp.edit().putLong("siteloadTime", loadTime).commit();

		cityAqiDB = new CityAqiDao(getActivity(), "");
		Log.d("HomeActivity", "onCreate cityAqiDB=" + cityAqiDB.getCount());

		// 获取用户选择的城市
		sp = getActivity().getSharedPreferences("citydata", 0);
		try {
			citys = EnAndDecryption.String2WeatherList(sp.getString("citys", ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
		cityCount = citys.size();
		ModuleSPIO.showCityData(getActivity(), "HomeActivity");

		// 版本信息
		sp = getActivity().getSharedPreferences("verdata", 0);
		newVerCode = Float.parseFloat(sp.getString("newVerCode", ""));
		newVerName = sp.getString("verName", "");
		updateDetails = sp.getString("updatedetails", "");
		about = Integer.parseInt(sp.getString("about", ""));

		// json排名数据
		sp = getActivity().getSharedPreferences("jsondata", 0);
		sp.getString("rankjson", "");

		// about信息
		File aboutFile = new File(DataTool.createFileDir("Download") + "/about.html");
		if (!aboutFile.exists() && NetworkTool.isNetworkConnected(getActivity())) {
			DataTool.getAboutData();
		}

		// 吐槽数据
		sp = getActivity().getSharedPreferences("spitdata", 0);
		longtitude = sp.getString("longtitude", "");
		latitude = sp.getString("latitude", "");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// 整个home界面
		homeView = inflater.inflate(R.layout.forecast_activity, container, false);
		findView();

		// if (contentList != null) {
		// publishTime = DateUtil.getTime(contentList.get(0).getPubTime());
		// Log.e("aqi", "pubtime：" + publishTime);
		// } else {
		// publishTime = System.currentTimeMillis() + 10000;
		// }
		// pastCount = contentList.size();

		sp = getActivity().getSharedPreferences("isFlash", 0);
		isFlash = sp.getBoolean("isFlash", false);
		// 这什么判断
		if (NetworkTool.isNetworkConnected(getActivity()) && !isFlash) {
			// 进度条
			pDialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.getting_data));
			pDialog.show();
			pDialog.setCancelable(true);
			// 下拉菜单按钮
			new Thread(new ShowProgressThread()).start();
		} else {
			initView();
			// 填充数据
			viewDataInit();
		}
		// 初始化翻页指示器
		initIndicators();
		//
		if (about > aboutVersion) {
			aboutVersion += 1;
			DataTool.getAboutData();
		}
		return homeView;
	}

	/**
	 * 监听事件
	 */
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		// 附加功能
		case R.id.title_bar_menu_btn:
			// 初始化菜单按钮
			extraMenuInit();
			// 点击下拉菜单
			titlePopup.show(v);
			titlePopup.setItemOnClickListener(new OnItemOnClickListener() {
				@Override
				public void onItemClick(TitleActionItem item, int position) {
					// 关闭下拉菜单
					titlePopup.dismiss();
					switch (position) {
					case 0:
						Toast.makeText(getActivity(), R.string.spitOn, Toast.LENGTH_SHORT).show();
						cityId = citys.get(currentIndexOut).getId();
						onOrOff = !onOrOff;
						titlePopup.cleanAction();
						// 初始化下拉菜单
						extraMenuInit();
						// 打开吐槽
						initCitySpit(views, citys);
						break;
					case 1:
						// 检测版本
						ModuleVersionUpdate test = new ModuleVersionUpdate(getActivity());
						test.updateVersion();
						break;
					case 2:
						startActivity(new Intent(getActivity(), AirQualityStandardsActivity.class));
						break;
					case 3:
						startActivity(new Intent(getActivity(), AboutActivity.class));
						break;
					case 4:
						// 清除缓存，刷新
						clearCache();
						OnFlush();
						break;
					default:
						break;
					}
				}

			});
			break;
		case R.id.city_setting_icon:
			// 城市管理界面
			intent.setClass(getActivity(), CitySettingActivity.class);
			startActivity(intent);
			break;
		case R.id.update_image:
			// 刷新按钮实现刷新
			FlushFlag = true;
			OnFlush();
			UpdateTool.startLoadingAnim(getActivity(), updateDataBtn, updateAnimImg);
			FlushFlag = false;
			break;
		case R.id.share_image:
			// 分享
			if (NetworkTool.isNetworkConnected(getActivity())) {
				String filePath = DataTool.createFileDir("Share_Imgs");
				String tmpTime = String.valueOf(System.currentTimeMillis());
				String path = filePath + "/" + getResources().getString(R.string.app_name) + "_城市指数详情_" + tmpTime
						+ ".png";
				ShareTool.shoot(path, getActivity());
				ShareTool.SharePhoto(path, "绿朵分享", cityName + "空气质量现状。下载地址：http://app.lvdora.com", getActivity());
			} else {
				Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.spit_publish:
			publishSpit();
			break;
		case R.id.spit_off:
			onOrOff = !onOrOff;
			for (int i = 0; i < views.size(); i++) {
				this.spitLayout = (LinearLayout) views.get(i).findViewById(R.id.spit_layout);
				this.spitPublish = (ImageView) views.get(i).findViewById(R.id.spit_publish);
				this.spitOff = (ImageView) views.get(i).findViewById(R.id.spit_off);
				this.spitFirst = (TextView) views.get(i).findViewById(R.id.spit1);
				this.spitSecond = (TextView) views.get(i).findViewById(R.id.spit2);
				this.spitThird = (TextView) views.get(i).findViewById(R.id.spit3);
				if (!onOrOff) {
					titlePopup.cleanAction();
					extraMenuInit();
					closeSpit();
				}
			}
			break;
		// 所在地区站点信息
		case R.id.site_into_icon:
		case R.id.aqi_city_layout:
		case R.id.usa_layout:
		case R.id.location_site:
			showSite(intent);
			break;
		case R.id.temp:
			cityId = citys.get(currentIndexOut).getId();
			intent.setClass(getActivity(), WeatherForecast.class);
			intent.putExtra("cityId", cityId);
			// intent.putExtra("cityName", cityName);
			startActivity(intent);
			// startActivity(new Intent(getActivity(), WeatherForecast.class));
			// Toast.makeText(getActivity(),
			// "五天天气预报",Toast.LENGTH_SHORT).show();
			break;
		case R.id.weather_more_forecast:
			cityId = citys.get(currentIndexOut).getId();
			intent.setClass(getActivity(), WeatherForecast.class);
			intent.putExtra("cityId", cityId);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		autoUpdate();
		if (onstart_flush) {
			OnFlush();
		}
		onstart_flush = true;
	}

	@Override
	public void onPause() {
		CitysIndexMap.getInstance(getActivity()).listToSP();
		super.onPause();
	}

	@Override
	public void onStop() {
		CitysIndexMap.getInstance(getActivity()).listToSP();
		super.onStop();
	}

	@Override
	public void onDestroy() {
		CitysIndexMap.getInstance(getActivity()).listToSP();
		super.onDestroy();
	}

	/**
	 * 附加功能菜单
	 */
	private void extraMenuInit() {
		titlePopup = new TitlePopup(getActivity(), LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		if (!onOrOff) {
			titlePopup.addAction(new TitleActionItem(getActivity(), "打开吐槽", R.drawable.mm_title_btn_receiver_normal));
		} else {
			titlePopup.addAction(new TitleActionItem(getActivity(), "关闭吐槽", R.drawable.mm_title_btn_receiver_normal));
		}
		titlePopup.addAction(new TitleActionItem(getActivity(), "检测版本", R.drawable.settings_tabcontent_new_version));
		titlePopup.addAction(new TitleActionItem(getActivity(), "空气标准", R.drawable.settings_tabcontent_standard));
		titlePopup.addAction(new TitleActionItem(getActivity(), "关于我们", R.drawable.settings_tabcontent_about));
		titlePopup.addAction(new TitleActionItem(getActivity(), "清除缓存", R.drawable.home_clean));
		// titlePopup.addAction(new TitleActionItem(getActivity(),
		// "意见反馈",R.drawable.settings_tabcontent_suggest));
	}

	/**
	 * 界面滑动
	 */
	private void initView() {

		viewPager = (ViewPager) homeView.findViewById(R.id.viewpager);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		views = new ArrayList<View>();

		// 添加滑动页个数
		for (int i = 0; i < cityCount; i++) {
			View v = inflater.inflate(R.layout.forecast_pager, null);
			views.add(v);
		}
		pagerAdapter = new ViewPagerAdapter(views);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(currentIndexOut);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// Log.e("aqi", "position:"+position);
				pubCount = 0;
				// 设置当前页面
				setCurPage(position);
				views.get(position).setSelected(true);

				// 当前页面位置
				currentIndexOut = position;
				curViewId = position;
				currentID = position;

				// 存缓存
				sp = getActivity().getSharedPreferences("cur_city", 0);
				sp.edit().putInt("city_id", cityAqis.get(curViewId).getCityId()).commit();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int position) {

			}
		});
	}

	/**
	 * 添加界面数据
	 */
	public void viewDataInit() {
		// 获取添加过的城市的list
		cityAqis = new ArrayList<CityAqi>();
		cityAqis = cityAqiDB.getAll();
		Log.v("HomeActivity", "viewDataInit " + cityAqis.toString());
		// 处理数组越界

		cityName = cityAqis.get(currentIndexOut).getCityName();

		Log.v("HomeActivity", "currentIndexOut" + currentIndexOut + cityName);
		this.cityNameText.setText(cityName);
		// 给首页几个城市界面赋值
		int i = 0;
		for (CityAqi cityAqi : cityAqis) {
			setPageView(i, cityAqi);
			i++;
		}
	}

	/**
	 * 给单个页面赋值
	 * 
	 * @param i
	 * @param cityAqi
	 */
	private void setPageView(int i, CityAqi cityAqi) {

		findView(views, i);

		if (cityAqi != null) {
			// 显示aqi信息
			this.aqiValueText.setText(cityAqi.getAqi());
			this.aqiValueText.setBackgroundDrawable(getResources().getDrawable(
					GradeTool.getAqiColorByIndex(cityAqi.getAqi())));
			this.aqiValueText.setTextColor(getResources().getColor(GradeTool.getTextColorByAqi(cityAqi.getAqi())));

			String aqiState = GradeTool.getStateByIndex(Integer.parseInt(cityAqi.getAqi()));
			if (aqiState.length() == 4) {
				this.aqiGradeText.setTextSize(18);
			}
			this.aqiGradeText.setText(aqiState);
			this.pm25_value_text.setText("PM2.5 : " + cityAqi.getPm25());
			this.updateTimeText.setText("更新 " + cityAqi.getAqi_pubtime().substring(11, 16));

			// 显示美领馆的空气信息
			if (i == 0 && !cityAqi.getUsa_aqi().equals("--")) {
				this.usa_aqiValue_text = (TextView) views.get(i).findViewById(R.id.usa_aqi_index);
				this.usa_name = (TextView) views.get(i).findViewById(R.id.use_aqi_desc);
				this.usa_aqiGrade_text = (TextView) views.get(i).findViewById(R.id.usa_aqi_grade);
				this.usa_pm25Value_text = (TextView) views.get(i).findViewById(R.id.usa_pm25_value);
				this.usa_updateTime_text = (TextView) views.get(i).findViewById(R.id.usa_update_time);
				this.usa_aqiValue_text.setText(cityAqi.getUsa_aqi());
				this.usa_aqiValue_text.setBackgroundDrawable(getResources().getDrawable(
						GradeTool.getAqiColorByIndex(cityAqi.getUsa_aqi())));
				this.usa_aqiValue_text.setTextColor(getResources().getColor(
						GradeTool.getTextColorByAqi(cityAqi.getUsa_aqi())));
				String usaAqiState = GradeTool.getStateByIndex(Integer.parseInt(cityAqi.getUsa_aqi()));
				if (usaAqiState.length() == 4) {
					this.usa_aqiGrade_text.setTextSize(16);
				}
				this.usa_aqiGrade_text.setText(usaAqiState);
				this.usa_pm25Value_text.setText("PM2.5 : " + cityAqi.getUsa_pm25());
				this.usa_updateTime_text.setText("更新 " + cityAqi.getUs_pubtime().substring(11, 16));
				if (cityAqi.getCityName().equals("北京")) {
					this.usa_name.setText("美使馆");
					// this.nearnest_site_name_text.setText("美领馆");
				} else {
					this.usa_name.setText("美领馆");
				}
			} else {
				// 隐藏美使馆数据
				this.usaLayout.setVisibility(View.GONE);
			}
		}

		// 定位城市最近站点
		if (i == 0 && siteList != null) {
			this.nearestSiteLayout = (LinearLayout) views.get(i).findViewById(R.id.location_site);
			this.nearestSiteLayout.setVisibility(View.VISIBLE);
			nearestSiteLayout.setOnClickListener(this);

			this.nearnest_site_name_text = (TextView) views.get(i).findViewById(R.id.nearnest_site_name);
			this.nearnest_site_aqiValue_text = (TextView) views.get(i).findViewById(R.id.nearnest_site_aqi);
			this.nearnest_site_pm25Value_text = (TextView) views.get(i).findViewById(R.id.nearnest_site_pm25);
			this.nearnest_site_aqiGrade_text = (TextView) views.get(i).findViewById(R.id.nearnest_site_aqi_grade);
			this.nearnest_site_updateTime_text = (TextView) views.get(i).findViewById(R.id.nearnest_site_update_time);

			SiteAqi site;
			if (siteList.get(0).getName().equals("美国大使馆") || siteList.get(0).getName().equals("美国领事馆")) {
				site = siteList.get(1);
			} else {
				site = siteList.get(0);
			}
			String aqiSiteValue = site.getAqi();
			nearnest_site_name_text.setText(site.getName());
			nearnest_site_aqiValue_text.setText(aqiSiteValue);
			nearnest_site_aqiValue_text.setBackgroundDrawable(getResources().getDrawable(
					GradeTool.getAqiColorByIndex(aqiSiteValue)));
			;
			nearnest_site_aqiValue_text
					.setTextColor(getResources().getColor(GradeTool.getTextColorByAqi(aqiSiteValue)));
			nearnest_site_pm25Value_text.setText("PM2.5 : " + site.getPm25());
			nearnest_site_aqiGrade_text.setText(GradeTool.getStateByIndex(Integer.parseInt(aqiSiteValue)));
			nearnest_site_aqiGrade_text.setTextSize(16);
			nearnest_site_updateTime_text.setText("更新 " + site.getUpdateTime().substring(11, 16));
		}

		if (cityAqi != null) {
			if (i != 0 && !cityAqi.getUsa_aqi().equals("--")) {
				// 美领馆和天气合并
				this.nearestSiteLayout = (LinearLayout) views.get(i).findViewById(R.id.location_site);
				this.nearestSiteLayout.setVisibility(View.VISIBLE);
				nearestSiteLayout.setOnClickListener(this);

				this.nearnest_site_name_text = (TextView) views.get(i).findViewById(R.id.nearnest_site_name);
				this.nearnest_site_aqiValue_text = (TextView) views.get(i).findViewById(R.id.nearnest_site_aqi);
				this.nearnest_site_pm25Value_text = (TextView) views.get(i).findViewById(R.id.nearnest_site_pm25);
				this.nearnest_site_aqiGrade_text = (TextView) views.get(i).findViewById(R.id.nearnest_site_aqi_grade);
				this.nearnest_site_updateTime_text = (TextView) views.get(i).findViewById(
						R.id.nearnest_site_update_time);
				if (cityAqi.getCityName().equals("北京")) {
					this.nearnest_site_name_text.setText("美使馆");
					// this.nearnest_site_name_text.setText("美领馆");
				} else {
					this.nearnest_site_name_text.setText("美领馆");
				}
				this.nearnest_site_aqiValue_text.setText(cityAqi.getUsa_aqi());
				this.nearnest_site_aqiValue_text.setBackgroundDrawable(getResources().getDrawable(
						GradeTool.getAqiColorByIndex(cityAqi.getUsa_aqi())));
				this.nearnest_site_aqiValue_text.setTextColor(getResources().getColor(
						GradeTool.getTextColorByAqi(cityAqi.getUsa_aqi())));
				String usaAqiState = GradeTool.getStateByIndex(Integer.parseInt(cityAqi.getUsa_aqi()));
				if (usaAqiState.length() == 4) {
					this.nearnest_site_aqiGrade_text.setTextSize(16);
				}
				this.nearnest_site_aqiGrade_text.setText(usaAqiState);
				this.nearnest_site_pm25Value_text.setText("PM2.5 : " + cityAqi.getUsa_pm25());
				this.nearnest_site_updateTime_text.setText("更新 " + cityAqi.getUs_pubtime().substring(11, 16));
			}

			// 显示天气信息
			this.curDateText.setText(DateUtil.getDate());
			this.curTempText.setText(cityAqi.getTemp_now());
			int weatherLength = cityAqi.getWeather0().length();
			this.weatherText.setText(cityAqi.getWeather0());
			this.windText.setText(cityAqi.getWind());
			if (!cityAqi.getLowTemp0().equals("--") && !cityAqi.getLowTemp1().equals("--")) {
				// 今天\n10°/22°
				String strTmp = "今天\n" + cityAqi.getLowTemp0() + "~" + cityAqi.getHightTemp0() + "℃";
				this.firstDayText.setText(strTmp);

				strTmp = DateUtil.getTomorrowWeek() + "\n" + cityAqi.getLowTemp1() + "~" + cityAqi.getHightTemp1()
						+ "℃";
				this.secondDayText.setText(strTmp);

				// 根据天气等级设置天气图标，未完成
				this.firstWeatherImag.setBackgroundDrawable(getActivity().getResources().getDrawable(
						GradeTool.getWeatherIcon(cityAqi.getWeather_icon0())));
				this.secondWeatherImag.setBackgroundDrawable(getActivity().getResources().getDrawable(
						GradeTool.getWeatherIcon(cityAqi.getWeather_icon1())));
			} else {
				moreWetherForecast.setVisibility(View.INVISIBLE);
			}
			this.suggestionText.setText(GradeTool.getSuggestion(Integer.parseInt(cityAqi.getAqi())));
			this.suggestionText.setSelected(true);
		}
	}

	/**
	 * 初始化翻页指示器dot
	 */
	private void initIndicators() {
		this.indicators = new ArrayList<ImageView>();
		//
		if (this.indicatorLayout.getChildCount() > 0) {
			this.indicatorLayout.removeAllViews();
		}
		for (int i = 0;; i++) {
			if (i >= cityCount) {
				this.currentIndex = currentIndexOut;
				((ImageView) this.indicators.get(this.currentIndex)).setEnabled(false);
				return;
			}
			ImageView localImageView = (ImageView) View.inflate(getActivity(), R.layout.dot_imageview, null);
			this.indicators.add(i, localImageView);
			this.indicatorLayout.addView(localImageView, i);
		}
	}

	/**
	 * 设置当前页面
	 * 
	 * @param position
	 */
	private void setCurPage(int position) {
		if ((position < 0) || (position > -1 + this.cityCount) || this.currentIndex == position) {
			return;
		}
		((ImageView) this.indicators.get(position)).setEnabled(false);
		((ImageView) this.indicators.get(currentIndex)).setEnabled(true);
		this.currentIndex = position;
		try {
			cityName = cityAqis.get(position).getCityName();
		} catch (Exception e) {
		}

		new Handler().post(new Runnable() {
			@Override
			public void run() {
				// 自动更新
				autoUpdate();
				// 设置城市名称
				cityNameText.setText(cityName);
				// 吐槽内容
			}
		});
	}

	/**
	 * 刷新数据分以下三步 1）取得当前的城市,还有order 2）读取数据 3）删库、存库并显示
	 */
	private void updateViewData() {
		// 刷新按钮实现刷新
		if (FlushFlag) {
			final int currentCityID = cityAqis.get(curViewId).getCityId();
			final int currentOrder = curViewId;
			updateViewDataById(currentCityID, currentOrder);
		}
		// 由start刷新和auto刷新触发
		else {
			for (int i = 0; i < cityCount; i++) {
				final int currentCityID = cityAqis.get(i).getCityId();
				final int currentOrder = i;
				updateViewDataById(currentCityID, currentOrder);
			}
		}
	}

	/**
	 * 从server端取数据
	 */
	private void updateViewDataById(final int currentCityID, final int currentOrder) {
		//
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(Constant.SERVER_URL + currentCityID, new AsyncHttpResponseHandler() {

			// 启动刷新动画
			@Override
			public void onStart() {
				UpdateTool.startLoadingAnim(getActivity(), updateDataBtn, updateAnimImg);
			}

			// 取得添加城市及信息
			@Override
			public void onSuccess(String result) {
				if (result != null) {
					CityAqi cityAqi = DataTool.getCityAqi(result, currentCityID, currentOrder);
					// update单条cityaqi
					cityAqiDB.updateSingleById(cityAqi, currentCityID);
					setPageView(currentOrder, cityAqi);
				}
				if (currentOrder != 0) {
					UpdateTool.stopLoadingAnim(updateDataBtn, updateAnimImg);
				}
			}
		});

		if (currentOrder == 0) {
			client.get(Constant.SITE_URL + currentCityID, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String result) {
					sp = getActivity().getSharedPreferences("sortSiteData", 0);
					siteList = EnAndDecryption.String2SiteList(sp.getString("sortSiteList", ""));
					siteList = DataTool.getsortsite(getActivity(), result, sp.getFloat("locationLong", 0),
							sp.getFloat("locationLat", 0));
					setPageView(currentOrder, null);
					UpdateTool.stopLoadingAnim(updateDataBtn, updateAnimImg);
				}

				@Override
				public void onFailure(Throwable error) {
					UpdateTool.stopLoadingAnim(updateDataBtn, updateAnimImg);
				}
			});
		}
	}

	/**
	 * 所在地区站点信息
	 * 
	 * @param intent
	 */
	private void showSite(Intent intent) {
		cityId = citys.get(currentIndexOut).getId();
		if (NetworkTool.isNetworkConnected(getActivity())) {
			// 判断是不是删除缓存
			// 时间大于30分钟删除缓存
			long nowTime = System.currentTimeMillis();
			sp = getActivity().getSharedPreferences("autoupdate", 0);
			long loadTime = sp.getLong("siteloadTime", 0);
			if (nowTime - loadTime > Constant.UPDATE_TIME || sp.getString("sites_" + cityId, "").equals("")) {
				sp = getActivity().getSharedPreferences("sitedata", 0);
				sp.edit().remove("sites_" + cityId).commit();
				DataTool.getSiteJson(getActivity(), cityId);
				sp = getActivity().getSharedPreferences("autoupdate", 0);
				sp.edit().putLong("siteloadTime", nowTime).commit();
			}
		}
		intent.setClass(getActivity(), SiteActivity.class);
		intent.putExtra("cityId", cityId);
		intent.putExtra("cityName", cityName);
		intent.putExtra("order", curViewId);
		startActivity(intent);
	}

	/*
	 * 初始化吐槽：打开或者关闭
	 */
	private void initCitySpit(List<View> views, List<City> citys) {
		for (int i = 0; i < views.size(); i++) {
			try {
				nowCityId = citys.get(i).getId();
				cityContentList.clear();
				cityContentList = DataTool.String2SpitContentList(sp.getString("spit_" + nowCityId, ""));
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.spitLayout = (LinearLayout) views.get(i).findViewById(R.id.spit_layout);
			this.spitPublish = (ImageView) views.get(i).findViewById(R.id.spit_publish);
			this.spitOff = (ImageView) views.get(i).findViewById(R.id.spit_off);
			this.spitFirst = (TextView) views.get(i).findViewById(R.id.spit1);
			this.spitSecond = (TextView) views.get(i).findViewById(R.id.spit2);
			this.spitThird = (TextView) views.get(i).findViewById(R.id.spit3);
			// this.line = (View)views.get(i).findViewById(R.id.spit_line);

			if (onOrOff) {
				spitLayout.setVisibility(View.VISIBLE);
				spitPublish.setVisibility(View.VISIBLE);
				spitPublish.setOnClickListener(this);
				spitOff.setOnClickListener(this);
				spitOff.setVisibility(View.VISIBLE);
				spitFirst.setVisibility(View.VISIBLE);
				spitSecond.setVisibility(View.VISIBLE);
				spitThird.setVisibility(View.VISIBLE);
				// line.setVisibility(View.VISIBLE);
				spitFirst.setText("                                                      ");
				spitSecond.setText("                                                                ");
				spitThird.setText("                                                 ");
				if (cityContentList != null) {
					for (int j = 0; j < cityContentList.size(); j++) {
						if (j % 3 == 0) {
							spitFirst.append(cityContentList.get(j).getContent() + "             ");
						}
						if (j % 3 == 1) {
							spitSecond.append(cityContentList.get(j).getContent() + "             ");
						}
						if (j % 3 == 2) {
							spitThird.append(cityContentList.get(j).getContent() + "             ");
						}
					}
					spitFirst.setTextSize(23);
					spitSecond.setTextSize(20);
					spitThird.setTextSize(18);
				}
			} else {
				closeSpit();
			}
		}
		recordSpitTime();
	}

	/**
	 * 记录吐槽内容发布的最新时间，用于时间比较，判断是否有新内容需要更新
	 */
	private void recordSpitTime() {
		try {
			sp = getActivity().getSharedPreferences("spitdata", 0);
			cityContentList.clear();
			cityContentList = DataTool.String2SpitContentList(sp.getString(
					"spit_" + citys.get(currentIndexOut).getId(), ""));
			if (cityContentList != null) {
				publishTime = DateUtil.getTime(cityContentList.get(0).getPubTime());
				Log.e("aqi", "pubtime：" + publishTime);
			} else {
				publishTime = System.currentTimeMillis() + 1000;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示吐槽内容
	 */
	private void showSpitContent() {
		// 获取缓存数据
		for (int i = 0; i < views.size(); i++) {
			if (i == currentIndexOut) {
				try {
					nowCityId = citys.get(i).getId();
					sp = getActivity().getSharedPreferences("spitdata", 0);
					cityContentList.clear();
					cityContentList = DataTool.String2SpitContentList(sp.getString("spit_" + nowCityId, ""));
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 初始化组件
				this.spitLayout = (LinearLayout) views.get(currentIndexOut).findViewById(R.id.spit_layout);
				this.spitPublish = (ImageView) views.get(currentIndexOut).findViewById(R.id.spit_publish);
				this.spitOff = (ImageView) views.get(currentIndexOut).findViewById(R.id.spit_off);
				this.spitFirst = (TextView) views.get(currentIndexOut).findViewById(R.id.spit1);
				this.spitSecond = (TextView) views.get(currentIndexOut).findViewById(R.id.spit2);
				this.spitThird = (TextView) views.get(currentIndexOut).findViewById(R.id.spit3);
				// this.line =
				// (View)views.get(currentIndexOut).findViewById(R.id.spit_line);

				if (onOrOff) {
					spitLayout.setVisibility(View.VISIBLE);
					spitPublish.setVisibility(View.VISIBLE);
					spitPublish.setOnClickListener(this);
					spitOff.setOnClickListener(this);
					spitOff.setVisibility(View.VISIBLE);
					spitFirst.setVisibility(View.VISIBLE);
					spitSecond.setVisibility(View.VISIBLE);
					spitThird.setVisibility(View.VISIBLE);
					// line.setVisibility(View.VISIBLE);

					spitFirst.setText("                                                      ");
					spitSecond.setText("                                                                ");
					spitThird.setText("                                                 ");
					if (cityContentList != null) {
						int length = cityContentList.size();
						for (int j = 0; j < length; j++) {
							// pubCount != 0 &&
							if (mySpit != null) {
								if (length > 8) {
									// if (j == 0 && mySpit != null) {
									if (pubCount % 3 == 0) {
										spitFirst.setText(cityContentList.get(length - 1).getContent() + "           "
												+ cityContentList.get(length - 2).getContent() + "           "
												+ cityContentList.get(length - 3).getContent() + "         "
												+ mySpit.getContent() + "             ");
										spitSecond.setText(cityContentList.get(length - 4).getContent() + "           "
												+ cityContentList.get(length - 5).getContent() + "           "
												+ cityContentList.get(length - 6).getContent() + "         ");
										spitThird.setText(cityContentList.get(length - 7).getContent() + "           "
												+ cityContentList.get(length - 8).getContent() + "           "
												+ cityContentList.get(length - 9).getContent() + "         ");
									}

									if (pubCount % 3 == 1) {
										spitFirst.setText(cityContentList.get(length - 1).getContent() + "           "
												+ cityContentList.get(length - 2).getContent() + "           "
												+ cityContentList.get(length - 3).getContent() + "         ");
										spitSecond.setText(cityContentList.get(length - 4).getContent() + "           "
												+ cityContentList.get(length - 5).getContent() + "           "
												+ cityContentList.get(length - 6).getContent() + "         "
												+ mySpit.getContent() + "             ");
										spitThird.setText(cityContentList.get(length - 7).getContent() + "           "
												+ cityContentList.get(length - 8).getContent() + "           "
												+ cityContentList.get(length - 9).getContent() + "         ");
									}

									if (pubCount % 3 == 2) {
										spitFirst.setText(cityContentList.get(length - 1).getContent() + "           "
												+ cityContentList.get(length - 2).getContent() + "           "
												+ cityContentList.get(length - 3).getContent() + "         ");
										spitSecond.setText(cityContentList.get(length - 4).getContent() + "           "
												+ cityContentList.get(length - 5).getContent() + "           "
												+ cityContentList.get(length - 6).getContent() + "         ");
										spitThird.setText(cityContentList.get(length - 7).getContent() + "           "
												+ cityContentList.get(length - 8).getContent() + "           "
												+ cityContentList.get(length - 9).getContent() + "         "
												+ mySpit.getContent() + "             ");
									}
								} else if (length > 2 && length <= 8) {
									if (pubCount % 3 == 0) {
										spitFirst.setText("           " + cityContentList.get(length - 1).getContent()
												+ "           " + cityContentList.get(length - 2).getContent()
												+ "         " + mySpit.getContent() + "             ");
									}
									if (pubCount % 3 == 1) {
										spitSecond.setText("           " + cityContentList.get(length - 1).getContent()
												+ "           " + cityContentList.get(length - 2).getContent()
												+ "         " + mySpit.getContent() + "             ");
									}
									if (pubCount % 3 == 2) {
										spitThird.setText("           " + cityContentList.get(length - 1).getContent()
												+ "           " + cityContentList.get(length - 2).getContent()
												+ "         " + mySpit.getContent() + "             ");
									}
								} else {
									// 取全国城市数据
									sp = getActivity().getSharedPreferences("spitdata", 0);
									cityContentList.clear();
									try {
										cityContentList = DataTool.String2SpitContentList(sp.getString("spitjson", ""));
										if (cityContentList != null) {
											if (j % 3 == 0) {
												spitFirst.append(cityContentList.get(j).getContent() + "             ");
												// spitSecond.setText(" ");
												// spitThird.setText(" ");
											}
											if (j % 3 == 1) {
												spitSecond
														.append(cityContentList.get(j).getContent() + "             ");
												// spitFirst.setText(" ");
												// spitThird.setText(" ");
											}
											if (j % 3 == 2) {
												spitThird.append(cityContentList.get(j).getContent() + "             ");
												// spitFirst.setText(" ");
												// spitSecond.setText(" ");
											}
											// spitSecond.setText("           "
											// + mySpit.getContent() +
											// "             ");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								// 没有发表内容的情况
								if (j % 3 == 0) {
									spitFirst.append(cityContentList.get(j).getContent() + "             ");
									spitSecond.setText(" ");
									spitThird.setText(" ");
								}
								if (j % 3 == 1) {
									spitSecond.append(cityContentList.get(j).getContent() + "             ");
									spitFirst.setText(" ");
									spitThird.setText(" ");
								}
								if (j % 3 == 2) {
									spitThird.append(cityContentList.get(j).getContent() + "             ");
									spitFirst.setText(" ");
									spitSecond.setText(" ");
								}
							}
						}
						/*
						 * spitFirst.setTextSize(23);
						 * spitSecond.setTextSize(20);
						 * spitThird.setTextSize(24);
						 */
					}
				} else {
					closeSpit();
				}
			}
		}
		recordSpitTime();
	}

	/**
	 * 发布吐槽信息
	 */
	private void publishSpit() {
		final EditText publish = new EditText(getActivity());
		new AlertDialog.Builder(getActivity()).setTitle("吐槽天气").setIcon(R.drawable.publish).setView(publish)
				.setPositiveButton("发布", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String content = publish.getText().toString();
						if (content.equals("")) {
							// 吐槽内容不能为空
							Toast.makeText(getActivity(), R.string.spit, 200).show();
						} else {
							if (0 < content.length() && content.length() <= 30) {
								pubCount = pubCount + 1;

								Date nowDate = new Date();
								String myTime = DateUtil.Date2String(nowDate);
								mySpit = new SpitContent();
								mySpit.setPubTime(myTime);
								mySpit.setContent(publish.getText().toString());

								if (pubCount == 1) {

									for (int k = 0; k < views.size(); k++) {
										// 判断到这里，和initSpit 相同
										if (k == currentIndexOut) {
											Toast.makeText(getActivity(), "吐槽成功", Toast.LENGTH_SHORT).show();
											// 重新初始化组件
											spitLayout = (LinearLayout) views.get(currentIndexOut).findViewById(
													R.id.spit_layout);
											spitPublish = (ImageView) views.get(currentIndexOut).findViewById(
													R.id.spit_publish);
											spitOff = (ImageView) views.get(currentIndexOut)
													.findViewById(R.id.spit_off);
											spitFirst = (TextView) views.get(currentIndexOut).findViewById(R.id.spit1);
											spitSecond = (TextView) views.get(currentIndexOut).findViewById(R.id.spit2);
											spitThird = (TextView) views.get(currentIndexOut).findViewById(R.id.spit3);
											// line =
											// (View)views.get(currentIndexOut).findViewById(R.id.spit_line);
											// 初始化后赋值，组合数据
											spitFirst.setText("                " + mySpit.getContent() + "           ");
											spitSecond.setText("                " + cityContentList.get(0).getContent()
													+ "         ");
											spitThird.setText("                " + cityContentList.get(1).getContent()
													+ "         ");
											for (int i = 0; i < cityContentList.size(); i++) {
												if (i % 3 == 0) {
													spitFirst.append(cityContentList.get(i).getContent()
															+ "             ");
												}
												if (i % 3 == 1) {
													spitSecond.append(cityContentList.get(i).getContent()
															+ "             ");
												}
												if (i % 3 == 2) {
													spitThird.append(cityContentList.get(i).getContent()
															+ "             ");
												}
											}
										}
									}
								}
								DataTool.getCitySpitData(getActivity(),
										String.valueOf(citys.get(currentIndexOut).getId()), content, longtitude,
										latitude);
								new Thread(new PublishSpitThread()).start();
							} else {
								Toast.makeText(getActivity(), R.string.spitlimit, 200).show();
							}
						}

					}
				}).setNegativeButton("取消", null).show();
	}

	/**
	 * 吐槽更新数据线程
	 * 
	 * @author xqp
	 * 
	 */
	public class PublishSpitThread implements Runnable {
		@Override
		public void run() {
			sp = getActivity().getSharedPreferences("spitdata", 0);
			try {
				cityContentList.clear();
				cityContentList = DataTool.String2SpitContentList(sp.getString("spit_"
						+ citys.get(currentIndexOut).getId(), ""));
				while (true) {
					long newTime = DateUtil.getTime(cityContentList.get(0).getPubTime());
					if (newTime - publishTime > 0) {
						mHandler.sendEmptyMessageDelayed(SPIT_PUBLISH, 0);
						break;
					}
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 关闭吐槽
	 */
	private void closeSpit() {
		Toast.makeText(getActivity(), R.string.spitOff, 200).show();
		spitLayout.setVisibility(View.INVISIBLE);
		spitPublish.setVisibility(View.INVISIBLE);
		spitOff.setVisibility(View.INVISIBLE);
		spitFirst.setVisibility(View.INVISIBLE);
		spitSecond.setVisibility(View.INVISIBLE);
		spitThird.setVisibility(View.INVISIBLE);
		// line.setVisibility(View.INVISIBLE);
	}

	/**
	 * 刷新数据
	 */
	private void OnFlush() {
		if (NetworkTool.isNetworkConnected(getActivity())) {
			// 更新页面数据
			updateViewData();
		} else {
			Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
			UpdateTool.stopLoadingAnim(updateDataBtn, updateAnimImg);
		}
	}

	/**
	 * 显示进度条线程
	 */
	class ShowProgressThread implements Runnable {
		@Override
		public void run() {
			int i = 0;
			while (true) {
				int count = DataTool.count_city;
				cityCount = cityAqiDB.getCount();
				Log.i("DataTool", "ShowProgressThread count=" + count + "cityCount=" + cityCount);
				if ((count == cityCount && count > 0) || i++ > 3) {
					pDialog.dismiss();
					mHandler.sendEmptyMessageDelayed(UPDATE_UI, 0);
					DataTool.count_city = 0;
					break;
				} else {
					DataTool.count_city = 0;
					DataTool.getAqiData(citys, cityAqiDB);
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 自动刷新
	 */
	public void autoUpdate() {
		// 当前时间
		long nowTime = System.currentTimeMillis();
		sp = getActivity().getSharedPreferences("autoupdate", 0);
		long loadTime = sp.getLong("homeLoadTime", 0);

		if (nowTime - loadTime > Constant.UPDATE_TIME) {
			UpdateTool.startLoadingAnim(getActivity(), updateDataBtn, updateAnimImg);
			OnFlush();
			sp.edit().putLong("homeLoadTime", nowTime).commit();
			Log.i("ShowProgressThread", "autoUpdate:" + first_update);
		}
	}

	/**
	 * 清除缓存
	 */
	private void clearCache() {
		ModuleDataClean.cleanDatabaseByName(getActivity(), "lvdora.db");
		ModuleDataClean.cleanSharedPreference(getActivity());

		resetApplication();
	}

	private void resetApplication() {
		Intent intent = new Intent("com.lvdora.aqi");
		intent.setClassName("com.lvdora.aqi", "com.lvdora.aqi.view.LvdoraGuide");
		startActivity(intent);
		ExitTool.exit();
	}

	// 定位图标
	public void homepageLocate(int position) {
		ImageView locate = (ImageView) views.get(position).findViewById(R.id.home_locate);
		if (position == 0) {
			locate.setVisibility(View.VISIBLE);
		}
	}

	private void findView() {
		// 上方导航栏
		this.forecast_index_layout = (RelativeLayout) homeView.findViewById(R.id.forecast_index_in);
		// 下方翻页指示器
		this.indicatorLayout = ((LinearLayout) homeView.findViewById(R.id.page_indicator));
		// 按钮
		this.cityNameText = (TextView) forecast_index_layout.findViewById(R.id.city_name_text);
		this.updateAnimImg = (ImageView) forecast_index_layout.findViewById(R.id.iv_weather_update);
		this.citySettingBtn = (ImageView) forecast_index_layout.findViewById(R.id.city_setting_icon);
		this.citySettingBtn.setOnClickListener(this);
		this.updateDataBtn = (ImageButton) forecast_index_layout.findViewById(R.id.update_image);
		this.updateDataBtn.setOnClickListener(this);
		this.shareBtn = (ImageView) forecast_index_layout.findViewById(R.id.share_image);
		this.shareBtn.setOnClickListener(this);
		this.siteIntoBtn = (ImageView) forecast_index_layout.findViewById(R.id.site_into_icon);
		this.siteIntoBtn.setOnClickListener(this);
		this.menuImg = (ImageView) forecast_index_layout.findViewById(R.id.title_bar_menu_btn);
		this.menuImg.setOnClickListener(this);
	}

	/**
	 * 不同城市数据
	 * 
	 * @param i
	 */
	private void findView(List<View> views, int i) {
		Log.v("HomeActivity", "findView数组越界" + i + views.size());
		// 美领馆
		usaLayout = (LinearLayout) views.get(i).findViewById(R.id.usa_layout);
		usaLayout.setOnClickListener(this);

		// 城市
		this.cityLayout = (RelativeLayout) views.get(i).findViewById(R.id.aqi_city_layout);
		cityLayout.setOnClickListener(this);
		this.aqiValueText = (TextView) views.get(i).findViewById(R.id.tv_aqi_index);
		this.aqiGradeText = (TextView) views.get(i).findViewById(R.id.tv_aqi_grade);
		this.pm25_value_text = (TextView) views.get(i).findViewById(R.id.pm25_value);
		this.updateTimeText = (TextView) views.get(i).findViewById(R.id.update_aqi_time);

		// 温度
		this.cityTemperature = (RelativeLayout) views.get(i).findViewById(R.id.temp);
		cityTemperature.setOnClickListener(this);
		this.curDateText = (TextView) views.get(i).findViewById(R.id.tv_date);
		this.curTempText = (TextView) views.get(i).findViewById(R.id.tv_temp_index);
		this.weatherText = (TextView) views.get(i).findViewById(R.id.tv_temp_sign);
		this.windText = (TextView) views.get(i).findViewById(R.id.tv_wind);
		this.suggestionText = (TextView) views.get(i).findViewById(R.id.suggestion);

		// 更多天气
		this.moreWetherForecast = (LinearLayout) views.get(i).findViewById(R.id.weather_more_forecast);
		moreWetherForecast.setOnClickListener(this);
		this.firstDayText = (TextView) views.get(i).findViewById(R.id.tv_firstDay);
		this.secondDayText = (TextView) views.get(i).findViewById(R.id.tv_secondDay);
		this.firstWeatherImag = (ImageView) views.get(i).findViewById(R.id.iv_firstDay_weather);
		this.secondWeatherImag = (ImageView) views.get(i).findViewById(R.id.iv_secondDay_weather);
	}
}
