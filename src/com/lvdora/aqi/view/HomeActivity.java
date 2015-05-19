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
import com.lvdora.aqi.module.ModuleLocation;
import com.lvdora.aqi.module.ModuleSPIO;
import com.lvdora.aqi.module.ModuleVersionUpdate;
import com.lvdora.aqi.module.MyApplication;
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
import com.lvdora.aqi.util.TimeGauging;
import com.lvdora.aqi.util.TitleActionItem;
import com.lvdora.aqi.util.TitlePopup;
import com.lvdora.aqi.util.TitlePopup.OnItemOnClickListener;
import com.lvdora.aqi.util.UpdateTool;

/**
 * 主界面
 * 
 * @author xqp
 * 
 */
public class HomeActivity extends Fragment implements OnClickListener {

	public static final int UPDATE_UI = 1;
	public static final int SPIT_PUBLISH = 2;
	public static final int SPIT_ON = 3;
	public static final int SAVE_SP = 4;

	// 当前页面城市标识位
	public static int currentIndexOut = 0;

	// 翻页指示器
	private List<ImageView> indicators;
	private LinearLayout indicatorLayout;

	private TitlePopup titlePopup;
	// 是否首次进入
	public boolean isFirstIn = false;
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
	private LinearLayout cityTemperature;
	private LinearLayout moreWetherForecast;
	private RelativeLayout item2;
	private LinearLayout item3;
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

	/**
	 * @1 接受子线程发送的数据
	 * @2 用此数据配合主线程更新UI
	 */
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.w("handler", "handler");
			switch (msg.what) {
			case UPDATE_UI: {
				updateUI();
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
			case SAVE_SP: {
				Log.w("d", "d");
				saveSP();
				break;
			}
			default:
				break;
			}
		}
	};

	/**
	 * 显示进度条线程
	 * 
	 * @1 和服务器通讯获得aqi信息
	 * @2 存库
	 * @3 更新UI
	 */
	class ShowProgressThread implements Runnable {
		@Override
		public void run() {
			int i = 0;
			//
			while (true) {
				int count = DataTool.count_city;
				cityCount = cityAqiDB.getCount();
				Log.i("ShowProgressThread", "count=" + count + " cityCount=" + cityCount);
				// 更新UI
				if ((count == cityCount && count > 0) || i++ > 3) {
					pDialog.dismiss();
					mHandler.sendEmptyMessageDelayed(UPDATE_UI, 0);
					DataTool.count_city = 0;
					break;
				}
				//
				else {
					DataTool.count_city = 0;
					// 服务器请求aqi数据， 并存库
					DataTool.getAqiData(citys, cityAqiDB);
				}
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MyApplication app = (MyApplication) getActivity().getApplication();
		app.setHandler(mHandler);

		// 验证时间
		TimeGauging.diffTime(TimeGauging.MAIN_TIME, TimeGauging.HOME_TIME);
		TimeGauging.diffTime(TimeGauging.LOGO_TIME, TimeGauging.HOME_TIME);
		TimeGauging.diffTime(TimeGauging.START_TIME, TimeGauging.HOME_TIME);

		onstart_flush = false;

		// 初始化数据库
		cityAqiDB = new CityAqiDao(getActivity(), "");
		Log.i("HomeActivity", "onCreate cityAqiDB=" + cityAqiDB.getCount());

		// 下载about信息
		File aboutFile = new File(DataTool.createFileDir("Download") + "/about.html");
		if (!aboutFile.exists() && NetworkTool.isNetworkConnected(getActivity())) {
			DataTool.getAboutData();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// 整个home界面
		View homeView = inflater.inflate(R.layout.forecast_activity, container, false);
		findIndexView(homeView);

		// 初始化界面
		loadSP();
		initViewPager();
		initIndicators();

		// 更新数据
		updateData();
		// 更新UI
		updateUI();
		return homeView;
	}

	@Override
	public void onStart() {
		super.onStart();
		autoUpdate();
	}

	@Override
	public void onResume() {
		Log.i("HomeActivity", "onResume");
		super.onResume();
		updateUI();
	}

	@Override
	public void onPause() {
		// 城市列表缓存
		CitysIndexMap.getInstance(getActivity()).listToSP();
		super.onPause();
	}

	@Override
	public void onStop() {
		// 存一下缓存
		saveSP();
		// 城市列表缓存
		CitysIndexMap.getInstance(getActivity()).listToSP();
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		// 城市列表缓存
		CitysIndexMap.getInstance(getActivity()).listToSP();
		cityAqiDB.closeAll();
		super.onDestroy();
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
					// 点击子项目
					extraMenuItemClick(position);
				}
			});
			break;
		case R.id.city_setting_icon:
			// 城市管理界面
			intent.setClass(getActivity(), CitySettingActivity.class);
			startActivity(intent);
			break;
		case R.id.update_image:
			updateData();
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
		case R.id.item3:
		case R.id.location_site:
			showSite(intent);
			break;
		case R.id.temp_layout:
		case R.id.weather_layout:
			cityId = citys.get(currentIndexOut).getId();
			intent.setClass(getActivity(), WeatherForecast.class);
			intent.putExtra("cityId", cityId);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	// 更新数据
	private void updateData() {
		UpdateTool.startLoadingAnim(getActivity(), updateDataBtn, updateAnimImg);
		if (!NetworkTool.isNetworkConnected(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
			UpdateTool.stopLoadingAnim(updateDataBtn, updateAnimImg);
		}
		// 更新当前城市aqi
		sendRequest4CityAqi();
		// 更新定位城市最近站点
		sendRequest4NearestSite();
	}

	/**
	 * 添加滑动页个数
	 */
	private void initViewPager() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		views = new ArrayList<View>();
		// 添加滑动页个数
		for (int i = 0; i < citys.size(); i++) {
			View v = inflater.inflate(R.layout.forecast_pager, null);
			views.add(v);
		}
		Log.w("HomeActivity", "initView " + views.size());

		// 页面滑动
		pageScroll();
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
		Log.i("HomeActivity", "initIndicators " + currentIndexOut);
		for (int i = 0;; i++) {
			if (i >= citys.size()) {
				((ImageView) this.indicators.get(currentIndexOut)).setEnabled(false);
				return;
			}
			ImageView localImageView = (ImageView) View.inflate(getActivity(), R.layout.dot_imageview, null);
			this.indicators.add(i, localImageView);
			this.indicatorLayout.addView(localImageView, i);
		}
	}

	// 页面滑动
	private void pageScroll() {
		pagerAdapter = new ViewPagerAdapter(views);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(currentIndexOut);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				Log.e("onPageSelected", position + "");
				// 当前页面位置
				currentIndexOut = position;
				// 刷新当前界面
				updateData();
				//
				views.get(position).setSelected(true);
				// 存缓存
				saveSP();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				Log.i("onPageScrolled", arg0 + "  " + arg1 + "   " + arg2);
				if (arg1 == 0) {
					// 存缓存
					saveSP();
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				Log.w("onPageScrollStateChanged", arg0 + "");
				switch (arg0) {
				case 2:
					((ImageView) indicators.get(currentIndexOut)).setEnabled(true);
					break;
				}

			}
		});
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
		titlePopup.addAction(new TitleActionItem(getActivity(), "账号管理", R.drawable.home_login));
		titlePopup.addAction(new TitleActionItem(getActivity(), "设置物联", R.drawable.home_wifi));
		// titlePopup.addAction(new TitleActionItem(getActivity(),
		// "意见反馈",R.drawable.settings_tabcontent_suggest));
	}

	// 点击子项目
	private void extraMenuItemClick(int position) {
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
			updateData();
			break;
		case 5:
			// startActivity(new Intent(getActivity(), LoginActivity.class));
			break;
		case 6:
			startActivity(new Intent(getActivity(), WifiActivity.class));
			break;
		default:
			break;
		}
	}

	// 更新当前城市aqi
	private void sendRequest4CityAqi() {
		cityId = CitysIndexMap.getInstance(getActivity()).get(currentIndexOut);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(Constant.SERVER_URL + cityId, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				UpdateTool.startLoadingAnim(getActivity(), updateDataBtn, updateAnimImg);
			}

			@Override
			public void onSuccess(String result) {
				Log.d("HomeActivity", "sendRequest4CityAqi" + result);
				if (result == null) {
					return;
				}
				CityAqi cityAqi = DataTool.getCityAqi(result, cityId, currentIndexOut);
				// update单条cityaqi
				cityAqiDB.updateSingleById(cityAqi, cityId);
				mHandler.sendEmptyMessage(UPDATE_UI);
			}
		});
	}

	// 定位城市最近站点
	private void sendRequest4NearestSite() {
		if (currentIndexOut != 0) {
			return;
		}
		int cityId = CitysIndexMap.getInstance(getActivity()).get(0);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(Constant.SITE_URL + cityId, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				UpdateTool.startLoadingAnim(getActivity(), updateDataBtn, updateAnimImg);
			}

			@Override
			public void onSuccess(String result) {
				Log.d("HomeActivity", "sendRequest4NearestSite" + result);
				if (result.equals("0")) {
					return;
				}
				siteList = DataTool.siteResultToList(result);
				siteList = DataTool.sortCitySiteList(siteList, ModuleLocation.longitude * 1E6,
						ModuleLocation.latitude * 1E6);
				mHandler.sendEmptyMessage(UPDATE_UI);
			}

			@Override
			public void onFailure(Throwable error) {
				UpdateTool.stopLoadingAnim(updateDataBtn, updateAnimImg);
			}
		});
	}

	/**
	 * 所在地区站点信息
	 * 
	 * @param intent
	 */
	private void showSite(Intent intent) {
		if (NetworkTool.isNetworkConnected(getActivity())) {
			halfHourUpdate();
		}

		cityId = CitysIndexMap.getInstance(getActivity()).get(currentIndexOut);
		intent.setClass(getActivity(), SiteActivity.class);
		intent.putExtra("order", currentIndexOut);
		intent.putExtra("cityId", cityId);
		intent.putExtra("cityName", cityName);
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
				cityContentList = EnAndDecryption.String2SpitContentList(sp.getString("spit_" + nowCityId, ""));
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
			cityContentList = EnAndDecryption.String2SpitContentList(sp.getString("spit_"
					+ citys.get(currentIndexOut).getId(), ""));
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
					cityContentList = EnAndDecryption.String2SpitContentList(sp.getString("spit_" + nowCityId, ""));
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
										cityContentList = EnAndDecryption.String2SpitContentList(sp.getString(
												"spitjson", ""));
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
				cityContentList = EnAndDecryption.String2SpitContentList(sp.getString(
						"spit_" + citys.get(currentIndexOut).getId(), ""));
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
	 * 自动刷新
	 */
	public void autoUpdate() {
		// 当前时间
		long nowTime = System.currentTimeMillis();
		sp = getActivity().getSharedPreferences("autoupdate", 0);
		long loadTime = sp.getLong("homeLoadTime", 0);
		if (nowTime - loadTime > Constant.UPDATE_TIME) {
			updateData();
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
		// 清除download文件
		ModuleDataClean.cleanCustomCache(DataTool.createFileDir("Download"));

		resetApplication();
	}

	private void resetApplication() {
		Intent intent = new Intent("com.lvdora.aqi");
		intent.setClassName("com.lvdora.aqi", "com.lvdora.aqi.view.LvdoraGuide");
		startActivity(intent);
		ExitTool.exit();
	}

	// 定位图标
	public void homepageLocateIcon(int position) {
		ImageView locate = (ImageView) views.get(position).findViewById(R.id.home_locate);
		if (position == 0) {
			locate.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 时间大于30分钟删除缓存
	 */
	private void halfHourUpdate() {
		long nowTime = System.currentTimeMillis();
		sp = getActivity().getSharedPreferences("autoupdate", 0);
		long loadTime = sp.getLong("siteloadTime", 0);
		String site = sp.getString("sites_" + cityId, "");
		//
		if (nowTime - loadTime > Constant.UPDATE_TIME || site.equals("")) {
			// 删除站点数据
			sp = getActivity().getSharedPreferences("sitedata", 0);
			sp.edit().remove("sites_" + cityId).commit();
			// 更新站点数据
			DataTool.getNearestSite(getActivity(), cityId);
			// 存更新时间
			sp = getActivity().getSharedPreferences("autoupdate", 0);
			sp.edit().putLong("siteloadTime", nowTime).commit();
		}
	}

	// 加载缓存
	private void loadSP() {
		// 获取用户选择的城市
		sp = getActivity().getSharedPreferences("citydata", 0);
		citys = EnAndDecryption.String2WeatherList(sp.getString("citys", ""));
		ModuleSPIO.showCityData(getActivity(), "HomeActivity loadSP");
		cityCount = citys.size();

		// sortSiteData是当前城市的站点数据
		sp = getActivity().getSharedPreferences("sortSiteData", 0);
		siteList = EnAndDecryption.String2SiteList(sp.getString("sortSiteList", ""));

		// cur_city是当前所有选择的城市
		sp = getActivity().getSharedPreferences("cur_city", 0);
		// currentID = sp.getInt("city_id", 18);
		// currentIndexOut =
		// CitysIndexMap.getInstance(getActivity()).getKeyByValue(currentID);

		// Log.e("aqi", "siteList:"+siteList.toString());
		// 取得吐槽缓存数据
		// sp = getActivity().getSharedPreferences("spitdata", 0); contentList =
		// DataTool.String2SpitContentList(sp.getString( "spitjson", ""));

		// 保存运行时间到自动更新缓存，精确到毫秒
		long loadTime = System.currentTimeMillis();
		sp = getActivity().getSharedPreferences("autoupdate", 0);
		sp.edit().putLong("siteloadTime", loadTime).commit();

		// 版本信息
		sp = getActivity().getSharedPreferences("verdata", 0);
		newVerCode = Float.parseFloat(sp.getString("newVerCode", "0"));
		newVerName = sp.getString("verName", "");
		updateDetails = sp.getString("updatedetails", "");
		about = Integer.parseInt(sp.getString("about", "0"));

		// json排名数据
		sp = getActivity().getSharedPreferences("jsondata", 0);
		sp.getString("rankjson", "");

		// 吐槽数据
		sp = getActivity().getSharedPreferences("spitdata", 0);
		longtitude = sp.getString("longtitude", "");
		latitude = sp.getString("latitude", "");

		// 是否首次进入homepage
		sp = getActivity().getSharedPreferences("isFirstIn", 0);
		isFirstIn = sp.getBoolean("isFirstIn", false);
	}

	// 存缓存
	private void saveSP() {
		Log.w("HomeActivity", "saveSP");
		SharedPreferences sp;
		// 获取用户选择的城市
		sp = getActivity().getSharedPreferences("citydata", 0);
		sp.edit().putString("citys", EnAndDecryption.CityList2String(citys)).commit();

		// sortSiteData是当前城市的站点数据
		sp = getActivity().getSharedPreferences("sortSiteData", 0);
		sp.edit().putString("sortSiteList", EnAndDecryption.SiteList2String(siteList)).commit();

		// 存缓存
		try {
			sp = getActivity().getSharedPreferences("cur_city", 0);
			sp.edit().putInt("city_id", cityAqis.get(currentIndexOut).getCityId()).commit();
		} catch (Exception e) {
		}

		// 是否首次进入homepage
		sp = getActivity().getSharedPreferences("isFirstIn", 0);
		sp.edit().putBoolean("isFirstIn", false).commit();
	}

	private void findIndexView(View homeView) {
		// 上方导航栏
		this.forecast_index_layout = (RelativeLayout) homeView.findViewById(R.id.forecast_index_in);
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

		// viewPager
		this.viewPager = (ViewPager) homeView.findViewById(R.id.viewpager);
		// 下方翻页指示器
		this.indicatorLayout = ((LinearLayout) homeView.findViewById(R.id.page_indicator));
	}

	// 单个城市
	private void findItemView(View view) {
		// 1 城市
		this.cityLayout = (RelativeLayout) view.findViewById(R.id.aqi_city_layout);
		cityLayout.setOnClickListener(this);
		this.aqiValueText = (TextView) view.findViewById(R.id.tv_aqi_index);
		this.aqiGradeText = (TextView) view.findViewById(R.id.tv_aqi_grade);
		this.pm25_value_text = (TextView) view.findViewById(R.id.pm25_value);
		this.updateTimeText = (TextView) view.findViewById(R.id.update_aqi_time);
		// 1.2 温度
		this.cityTemperature = (LinearLayout) view.findViewById(R.id.temp_layout);
		// cityTemperature.setOnClickListener(this);
		this.curDateText = (TextView) view.findViewById(R.id.tv_date);
		this.curTempText = (TextView) view.findViewById(R.id.tv_temp_index);
		this.weatherText = (TextView) view.findViewById(R.id.tv_temp_sign);
		this.windText = (TextView) view.findViewById(R.id.tv_wind);
		this.suggestionText = (TextView) view.findViewById(R.id.suggestion);

		// item2 最近站点和天气
		item2 = (RelativeLayout) view.findViewById(R.id.item2);
		this.nearestSiteLayout = (LinearLayout) view.findViewById(R.id.location_site);
		this.nearestSiteLayout.setOnClickListener(this);
		this.nearnest_site_name_text = (TextView) view.findViewById(R.id.nearnest_site_name);
		this.nearnest_site_aqiValue_text = (TextView) view.findViewById(R.id.nearnest_site_aqi);
		this.nearnest_site_pm25Value_text = (TextView) view.findViewById(R.id.nearnest_site_pm25);
		this.nearnest_site_aqiGrade_text = (TextView) view.findViewById(R.id.nearnest_site_aqi_grade);
		this.nearnest_site_updateTime_text = (TextView) view.findViewById(R.id.nearnest_site_update_time);
		this.moreWetherForecast = (LinearLayout) view.findViewById(R.id.weather_layout);
		this.moreWetherForecast.setOnClickListener(this);
		this.firstDayText = (TextView) view.findViewById(R.id.tv_firstDay);
		this.secondDayText = (TextView) view.findViewById(R.id.tv_secondDay);
		this.firstWeatherImag = (ImageView) view.findViewById(R.id.iv_firstDay_weather);
		this.secondWeatherImag = (ImageView) view.findViewById(R.id.iv_secondDay_weather);

		// 3 美领馆
		item3 = (LinearLayout) view.findViewById(R.id.item3);
		this.usaLayout = (LinearLayout) view.findViewById(R.id.us_consulate);
		this.usaLayout.setOnClickListener(this);
		// findView
		this.usa_aqiValue_text = (TextView) view.findViewById(R.id.usa_aqi_index);
		this.usa_name = (TextView) view.findViewById(R.id.use_aqi_desc);
		this.usa_aqiGrade_text = (TextView) view.findViewById(R.id.usa_aqi_grade);
		this.usa_pm25Value_text = (TextView) view.findViewById(R.id.usa_pm25_value);
		this.usa_updateTime_text = (TextView) view.findViewById(R.id.usa_update_time);
	}

	// 给单个页面赋值
	private void updateUI() {
		Log.d("HomeActivity", "updateUI");
		UpdateTool.stopLoadingAnim(updateDataBtn, updateAnimImg);
		CityAqi cityAqi = cityAqiDB.selectAqiByOrder(currentIndexOut);
		View view = views.get(currentIndexOut);
		// 界面
		findItemView(view);
		// 页头
		setPageTitle(cityAqi);
		// cityaqi
		setCityAqi(cityAqi, view);
		// 最近站点
		setNearestSite();
		// 天气预报
		setWeatherForecast(cityAqi);
		// 美领馆
		setUSConsulate(view, cityAqi);
		// setAmericanEmbassy(i, view, cityAqi);
	}

	// 设置页头和指示器
	private void setPageTitle(CityAqi cityAqi) {
		((ImageView) indicators.get(currentIndexOut)).setEnabled(false);
		cityName = cityAqi.getCityName();
		// 设置城市名称
		cityNameText.setText(cityName);
	}

	private void setCityAqi(CityAqi cityAqi, View view) {
		Log.d("HomeActivity", "setCityAqi" + cityAqi.toString());
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
		}
	}

	//
	private void setNearestSite() {
		if (currentIndexOut != 0) {
			return;
		}
		if (siteList.isEmpty()) {
			return;
		}
		item2.setVisibility(View.VISIBLE);
		SiteAqi site = siteList.get(0);
		String aqiSiteValue = site.getAqi();
		nearnest_site_name_text.setText(site.getName());
		nearnest_site_aqiValue_text.setText(aqiSiteValue);
		int aqiColor = GradeTool.getAqiColorByIndex(aqiSiteValue);
		nearnest_site_aqiValue_text.setBackgroundDrawable(getResources().getDrawable(aqiColor));
		int textColor = GradeTool.getTextColorByAqi(aqiSiteValue);
		nearnest_site_aqiValue_text.setTextColor(getResources().getColor(textColor));
		nearnest_site_pm25Value_text.setText("PM2.5 : " + site.getPm25());
		nearnest_site_aqiGrade_text.setText(GradeTool.getStateByIndex(Integer.parseInt(aqiSiteValue)));
		// 更新时间
		nearnest_site_updateTime_text.setText("更新 " + site.getUpdateTime().substring(11, 16));
	}

	private void setWeatherForecast(CityAqi cityAqi) {
		this.curDateText.setText(DateUtil.getDate());
		this.curTempText.setText(cityAqi.getTemp_now());
		int weatherLength = cityAqi.getWeather0().length();
		this.weatherText.setText(cityAqi.getWeather0());
		this.windText.setText(cityAqi.getWind());
		if (!cityAqi.getLowTemp0().equals("--") && !cityAqi.getLowTemp1().equals("--")) {
			// 今天\n10°/22°
			String strTmp = "今天\n" + cityAqi.getLowTemp0() + "~" + cityAqi.getHightTemp0() + "℃";
			this.firstDayText.setText(strTmp);
			strTmp = DateUtil.getTomorrowWeek() + "\n" + cityAqi.getLowTemp1() + "~" + cityAqi.getHightTemp1() + "℃";
			this.secondDayText.setText(strTmp);
			// 根据天气等级设置天气图标
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

	private void setUSConsulate(View view, CityAqi cityAqi) {
		Log.d("HomeActivity", "setUSConsulate" + cityAqi.toString());
		// 显示美领馆的空气信息
		if (cityAqi.getUsa_aqi().equals("--")) {
			return;
		}
		Log.d("HomeActivity", "setUSConsulate");
		this.item3.setVisibility(View.VISIBLE);
		// 只有北京叫做美使馆
		if (cityAqi.getCityName().equals("北京")) {
			this.usa_name.setText("美使馆");
			// this.nearnest_site_name_text.setText("美领馆");
		} else {
			this.usa_name.setText("美领馆");
		}
		// setText
		this.usa_aqiValue_text.setText(cityAqi.getUsa_aqi());
		int bColor = GradeTool.getAqiColorByIndex(cityAqi.getUsa_aqi());
		this.usa_aqiValue_text.setBackgroundDrawable(getResources().getDrawable(bColor));
		int tColor = GradeTool.getTextColorByAqi(cityAqi.getUsa_aqi());
		this.usa_aqiValue_text.setTextColor(getResources().getColor(tColor));
		String usaAqiState = GradeTool.getStateByIndex(Integer.parseInt(cityAqi.getUsa_aqi()));
		if (usaAqiState.length() == 4) {
			this.usa_aqiGrade_text.setTextSize(16);
		}
		this.usa_aqiGrade_text.setText(usaAqiState);
		this.usa_pm25Value_text.setText("PM2.5 : " + cityAqi.getUsa_pm25());
		this.usa_updateTime_text.setText("更新 " + cityAqi.getUs_pubtime().substring(11, 16));
		Log.d("HomeActivity", "setUSConsulate");
	}
}
