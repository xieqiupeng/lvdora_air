package com.lvdora.aqi.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.MKMapStatusChangeListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.lvdora.aqi.R;
import com.lvdora.aqi.adapter.SiteAdapter;
import com.lvdora.aqi.baidu.BMapUtil;
import com.lvdora.aqi.baidu.MyApplication;
import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.aqi.dao.CityDao;
import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.model.Longlati;
import com.lvdora.aqi.model.MapPopup;
import com.lvdora.aqi.model.SiteAqi;
import com.lvdora.aqi.module.ModuleActivitiesManager;
import com.lvdora.aqi.util.AsyncHttpClient;
import com.lvdora.aqi.util.AsyncHttpResponseHandler;
import com.lvdora.aqi.util.Constant;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.EnAndDecryption;
import com.lvdora.aqi.util.GradeTool;
import com.lvdora.aqi.util.LinearLayoutForListView;
import com.lvdora.aqi.util.NetworkTool;
import com.lvdora.aqi.util.ShareTool;
import com.lvdora.aqi.util.TrendView;
import com.lvdora.aqi.util.UpdateTool;

public class SiteActivity extends Activity implements OnClickListener {

	private static final int UPDATE_UI = 1;

	private SharedPreferences sp;
	private List<SiteAqi> siteAqis;
	private int cityId;
	private int order;
	private List<CityAqi> cityAqis;
	private CityAqiDao cityAqiDB;
	private CityDao cityDB;
	private String cityName;
	private SiteAdapter siteAdapter;
	private LinearLayoutForListView siteView;
	private TextView cityNameText;
	private TextView pm10_value_text;
	private TextView O3_value_text;
	private TextView SO2_value_text;
	private TextView NO2_value_text;
	private TextView CO_value_text;
	private DisplayMetrics dm;
	private ScrollView scrollView;
	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView;
	private int latitude;
	private int longitude;
	public String type;
	public List<MapPopup> popList;
	private String mapJson;
	private String mapSiteJson;
	/**
	 * 用MapController完成地图控制
	 */
	private MapController mMapController;
	private MyOverlay mOverlay;
	private PopupOverlay pop;
	private View viewCache;
	private View popupInfo;
	private Button button;

	private OverlayItem mCurItem;
	private int aqi;

	private int nowStatus = 0;
	private int myStatus = 0;

	private LinearLayout site_layout;
	// private LinearLayout noSite_layout;

	private ImageButton updateDataBtn;
	private ImageView backBtn;
	private ImageView shareBtn;
	private ImageView updateAnimImg;

	private TrendView trendView;

	private ProgressDialog pDialog;
	private String siteStr;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_UI: {
				initViewData();
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
		setContentView(R.layout.site_activity);

		// 当前页面加入activity管理模块
		ModuleActivitiesManager.getActivitiesStack().push(this);

		// TODO 取得屏幕的分辨率
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// 初始化界面
		findView();
		// 接受参数
		cityId = getIntent().getIntExtra("cityId", 0);
		cityName = getIntent().getStringExtra("cityName");
		order = getIntent().getIntExtra("order", 0);
		// 数据库
		cityAqis = new ArrayList<CityAqi>();
		cityAqiDB = new CityAqiDao(SiteActivity.this, "");
		cityDB = new CityDao(SiteActivity.this);
		// 查询aqis
		cityAqis = cityAqiDB.getAll();
		// sp缓存
		sp = getSharedPreferences("jsondata", 0);
		mapJson = sp.getString("rankjson", "");
		mapSiteJson = sp.getString("mapsitejson", "");

		/*
		 * 使用地图sdk前需先初始化BMapManager. BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
		 * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
		 */
		MyApplication app = (MyApplication) getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(this);
			// 如果BMapManager没有初始化则初始化BMapManager
			app.mBMapManager.init(new MyApplication.MyGeneralListener());
		}

		// 获取地图控制器
		mMapController = mMapView.getController();
		// 设置地图是否响应点击事件
		mMapController.enableClick(true);
		// 设置地图缩放级别
		mMapController.setZoom(11);
		// 显示内置缩放控件
		// mMapView.setBuiltInZoomControls(true);
		// 设定类型
		type = "captial";
		// 初始化显示省会城市信息
		initOverlay();
		// 设定地图中心点
		Longlati ll = cityDB.getJWById(cityId);
		GeoPoint p;
		if (ll != null) {
			p = new GeoPoint((int) (ll.getLati() * 1E6), (int) (ll.getLongi() * 1E6));
		} else {
			p = new GeoPoint((int) (116.45999 * 1E6), (int) (39.919998 * 1E6));
		}
		mMapController.setCenter(p);
		// 地图状态监听
		MapStatusChange();
		mMapView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_UP) {
					scrollView.requestDisallowInterceptTouchEvent(false);
				} else {
					scrollView.requestDisallowInterceptTouchEvent(true);
				}
				return false;
			}
		});

		if (!NetworkTool.isNetworkConnected(SiteActivity.this)) {
			initViewData();
		} else {
			// 判断获取数据结束
			pDialog = ProgressDialog.show(SiteActivity.this, "", getResources().getString(R.string.getting_data));
			pDialog.show();
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						sp = getSharedPreferences("sitedata", 0);
						siteStr = sp.getString("sites_" + cityId, "");
						if (!siteStr.equals("")) {
							pDialog.dismiss();
							mHandler.sendEmptyMessageDelayed(UPDATE_UI, 0);
							break;
						}
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	// 初始化数据
	private void initViewData() {
		//
		sp = getSharedPreferences("sitedata", 0);
		siteStr = sp.getString("sites_" + cityId, "");
		//
		if (!siteStr.equals("0")) {
			siteAqis = new ArrayList<SiteAqi>();
			try {
				siteAqis = EnAndDecryption.String2SiteList(siteStr);
				siteAdapter = new SiteAdapter(SiteActivity.this, siteAqis);
				siteView.removeAllViews();
				siteView.setAdapter(siteAdapter);

				int screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
				int screenHeight = getWindowManager().getDefaultDisplay().getHeight();

				// this.trendView = (TrendView) findViewById(R.id.trendView);
				trendView.setWidthHeight(screenWidth, screenHeight);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			site_layout.setVisibility(View.GONE);
			// noSite_layout.setVisibility(View.VISIBLE);
		}
		// 刷新指数
		updateExponent();
	}

	/**
	 * 实现对地图状态改变的处理
	 */
	private void MapStatusChange() {
		MKMapStatusChangeListener listener = new MKMapStatusChangeListener() {
			@Override
			public void onMapStatusChange(MKMapStatus mapStatus) {

				int zoom = (int) mapStatus.zoom; // 地图缩放等级

				// int overlooking = mapStatus.overlooking; //地图俯视角度
				// int rotate = mapStatus.rotate; //地图旋转角度
				// Point targetScreen = mapStatus.targetScreen; //中心点的屏幕坐标
				GeoPoint targetGeo = mapStatus.targetGeo; // 中心点的地理坐标
				// rank = (int) mMapView.getZoomLevel();
				if (pop != null) {
					pop.hidePop();
					mMapView.removeView(button);
				}
				/**
				 * 根据地图地图缩放等级自定义地图显示状态
				 */
				if (zoom <= 6) {
					nowStatus = 1;
				}
				if (zoom >= 7 && zoom <= 8) {
					nowStatus = 2;
				}
				if (zoom >= 8 && zoom <= 18) {
					nowStatus = 3;
				}
				/**
				 * 判断地图显示状态与当前状态是否一致，不一致时根据当前状态级别显示 myStatus = 1只显示省会城市，myStatus
				 * = 2显示全国城市，myStatus = 3 显示站点信息
				 */
				if (myStatus != nowStatus) {
					if (nowStatus == 1) {
						type = "capitals";
						mMapView.getOverlays().clear();

						initOverlay();
						myStatus = nowStatus;

					}
					if (nowStatus == 2) {
						type = "cities";
						mMapView.getOverlays().clear();
						initAllOverlay();
						myStatus = nowStatus;
					}
					if (nowStatus == 3) {
						type = "spots";
						latitude = targetGeo.getLatitudeE6();
						longitude = targetGeo.getLongitudeE6();
						mMapView.getOverlays().clear();
						initCitySpotsOverlay();
						myStatus = nowStatus;
					}
				}
			}
		};
		mMapView.regMapStatusChangeListener(listener);
	}

	private void initOverlay() {

		/**
		 * 创建自定义overlay
		 */
		mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.popup), mMapView);
		/**
		 * 准备overlay 数据
		 */
		try {
			JSONArray array = new JSONArray(mapJson);// 全国城市地图数据
			popList = new ArrayList<MapPopup>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				if (Boolean.parseBoolean(obj.getString("isCapital"))) {
					addOverly(obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		showMap();

	}

	private void initAllOverlay() {

		/**
		 * 创建自定义overlay
		 */
		mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.popup), mMapView);
		/**
		 * 准备overlay 数据
		 */
		try {
			JSONArray array = new JSONArray(mapJson);// 全国城市地图数据

			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				addOverly(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		showMap();
	}

	private void initCitySpotsOverlay() {

		/**
		 * 创建自定义overlay
		 */
		mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.popup), mMapView);
		/**
		 * 准备overlay 数据
		 */

		try {
			String spotLng;
			String spotLat;
			int spotLatitude;
			int spotLongitude;
			int latitudeDownLimt = latitude - 1000000;
			int latitudeUpLimt = latitude + 1000000;
			int longitudeDownLimt = longitude - 600000;
			int longitudeUpLimt = longitude + 600000;

			JSONArray array = new JSONArray(mapSiteJson);// 全国站点地图数据
			popList = new ArrayList<MapPopup>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				spotLng = obj.getString("cityLng");
				spotLat = obj.getString("cityLat");

				if (!spotLng.equals("--") && !spotLat.equals("--")) {
					spotLongitude = (int) (Float.parseFloat(obj.getString("cityLng").toString()) * 1E6);// obj.getDouble("longtitude");
					spotLatitude = (int) (Float.parseFloat(obj.getString("cityLat").toString()) * 1E6);
					if (latitudeDownLimt < spotLatitude && spotLatitude < latitudeUpLimt
							&& longitudeDownLimt < spotLongitude && spotLongitude < longitudeUpLimt) {
						addOverly(obj);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		showMap();
	}

	/**
	 * 添加覆盖物
	 * 
	 * @param object
	 */
	public void addOverly(JSONObject object) {

		try {

			MapPopup mPopup = new MapPopup();
			mPopup.setCityCame(object.getString("spotName"));
			mPopup.setAqi(object.getInt("aqi_aqi"));
			mPopup.setAirQuality(GradeTool.getStateByIndex(Integer.parseInt(object.getString("aqi_aqi"))));
			popList.add(mPopup);
			aqi = Integer.parseInt(object.getString("aqi_aqi"));
			GeoPoint p = new GeoPoint((int) (object.getDouble("cityLat") * 1E6),
					(int) (object.getDouble("cityLng") * 1E6));
			OverlayItem item = new OverlayItem(p, "覆盖物", "");
			// 覆盖物
			mOverlay.addItem(item);
			// Android 文字绘制到Bitmap上 OpenGL ES中似乎不能输出文本.
			// 将文本写到Bitmap上,再作为贴图,则可实现文字输出. 文字绘制到Bitmap上的方法为:
			String value = object.getString("aqi_aqi");
			Bitmap bmp = Bitmap.createBitmap(dm.widthPixels / 10, dm.widthPixels / 16, Bitmap.Config.ARGB_8888); // 图象大小要根据文字大小算下,以和文本长度对应
			Canvas canvasTemp = new Canvas(bmp);
			// 涂上颜色
			canvasTemp.drawColor(getResources().getColor(GradeTool.getMapColorByIndex(aqi + "")));
			Paint pa = new Paint();
			String familyName = object.getString("aqi_aqi");
			Typeface font = Typeface.create(familyName, Typeface.BOLD);
			// Typeface font = Typeface.create(value, Typeface.BOLD);
			pa.setColor(getResources().getColor(GradeTool.getTextColorByAqi(aqi + "")));
			pa.setTypeface(font);
			pa.setTextSize(dm.widthPixels / 25);
			canvasTemp.drawText(value, (float) (dm.widthPixels / 10 - pa.measureText(value)) / 2, dm.widthPixels / 20,
					pa);// 文字居中显示
			@SuppressWarnings("deprecation")
			Drawable drawable = new BitmapDrawable(bmp);
			item.setMarker(drawable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示地图
	 */
	public void showMap() {
		/**
		 * 将overlay 添加至MapView中
		 */
		mMapView.getOverlays().add(mOverlay);

		/**
		 * 刷新地图
		 */
		mMapView.refresh();

		/**
		 * 自定义view
		 */
		createView();
		/**
		 * 创建popupoverlay
		 */
		PopupClick();
	}

	/**
	 * 自定义view
	 */
	public void createView() {
		viewCache = getLayoutInflater().inflate(R.layout.custom_text_myview, null);
		popupInfo = (View) viewCache.findViewById(R.id.mypopinfo);
		button = new Button(this);
	}

	/**
	 * 创建popupoverlay
	 */
	public void PopupClick() {
		PopupClickListener popListener = new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {
				if (index == 0) {
					// 更新item位置
					pop.hidePop();
					GeoPoint p = new GeoPoint(mCurItem.getPoint().getLatitudeE6(), mCurItem.getPoint().getLongitudeE6());
					mCurItem.setGeoPoint(p);
					mOverlay.updateItem(mCurItem);
					mMapView.refresh();
				} else if (index == 2) {
					mOverlay.updateItem(mCurItem);
					mMapView.refresh();
				}
			}
		};
		pop = new PopupOverlay(mMapView, popListener);
	}

	/**
	 * 自定义Overlay
	 * 
	 * @author admin
	 * 
	 */
	public class MyOverlay extends ItemizedOverlay {

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}

		@Override
		public boolean onTap(int index) {

			if (type.equals("cities")) {
				try {
					JSONArray array = new JSONArray(mapJson);
					for (int i = 0; i < array.length(); i++) {
						if (i == index) {
							JSONObject obj = array.getJSONObject(i);
							int aqiValue = Integer.parseInt(obj.getString("aqi_aqi"));
							// 改变textview值
							TextView city = (TextView) popupInfo.findViewById(R.id.city);
							city.setText(obj.getString("spotName"));
							TextView aqi = (TextView) popupInfo.findViewById(R.id.aqi);
							aqi.setText("AQI：" + obj.getString("aqi_aqi"));
							TextView airQuality = (TextView) popupInfo.findViewById(R.id.level);
							airQuality.setTextSize(18);
							airQuality.setText("空气质量：" + GradeTool.getStateByIndex(aqiValue));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					for (int i = 0; i < popList.size(); i++) {
						if (i == index) {
							MapPopup map = popList.get(i);
							TextView city = (TextView) popupInfo.findViewById(R.id.city);
							city.setText(map.getCityCame());
							TextView aqi = (TextView) popupInfo.findViewById(R.id.aqi);
							aqi.setText("AQI：" + map.getAqi());
							TextView airQuality = (TextView) popupInfo.findViewById(R.id.level);
							airQuality.setTextSize(18);
							airQuality.setText("空气质量：" + map.getAirQuality());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			OverlayItem item = getItem(index);
			mCurItem = item;
			Bitmap[] bitMaps = { BMapUtil.getBitmapFromView(popupInfo), };
			pop.showPopup(bitMaps, item.getPoint(), 16);
			return true;
		}

		@Override
		public boolean onTap(GeoPoint pt, MapView mMapView) {
			if (pop != null) {
				pop.hidePop();
				mMapView.removeView(button);
			}
			return false;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.update_image:
			if (!NetworkTool.isNetworkConnected(SiteActivity.this)) {
				Toast.makeText(SiteActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
			} else {
				// 更新数据
				updateData();
			}
			break;
		// 返回
		case R.id.back_btn:
			finish();
			break;
		// 分享
		case R.id.share_image:
			if (NetworkTool.isNetworkConnected(SiteActivity.this)) {
				String filePath = DataTool.createFileDir("Share_Imgs");
				String tmpTime = String.valueOf(System.currentTimeMillis());
				String path = filePath + "/" + getResources().getString(R.string.app_name) + "_站点详情_" + tmpTime
						+ ".png";
				ShareTool.shoot(path, SiteActivity.this);
				ShareTool.SharePhoto(path, "绿朵分享", "站点信息", SiteActivity.this);
			} else {
				Toast.makeText(SiteActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	private void updateData() {
		if (NetworkTool.isNetworkConnected(this)) {
			AsyncHttpClient client = new AsyncHttpClient();

			client.get(Constant.SITE_URL + cityId, new AsyncHttpResponseHandler() {
				@Override
				public void onStart() {
					UpdateTool.startLoadingAnim(SiteActivity.this, updateDataBtn, updateAnimImg);
				}

				@Override
				public void onSuccess(String result) {
					siteAqis = DataTool.getSiteAqi(result, cityId);
					sp = getSharedPreferences("sitedata", 0);
					try {
						sp.edit().putString("sites_" + cityId, EnAndDecryption.SiteList2String(siteAqis)).commit();
						initViewData();
						UpdateTool.stopLoadingAnim(updateDataBtn, updateAnimImg);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(Throwable error) {
				}
			});

			client.get(Constant.SERVER_URL + cityId, new AsyncHttpResponseHandler() {
				@Override
				public void onStart() {
				}

				@Override
				public void onSuccess(String result) {

					// 取得添加城市及信息
					CityAqi cityAqi = DataTool.getCityAqi(result, cityId, 0);
					cityAqiDB.delByOrder(0);
					cityAqiDB.saveData(cityAqi);

				}

				@Override
				public void onFailure(Throwable error) {

				}
			});

			client.get(Constant.JSON_SERVER, new AsyncHttpResponseHandler() {
				@Override
				public void onStart() {

				}

				@Override
				public void onSuccess(String result) {
					mapJson = result;
					sp = getSharedPreferences("jsondata", 0);
					sp.edit().putString("rankjson", mapJson).commit();
					initOverlay();
					// 更新按钮的动画结束旋转
					UpdateTool.stopLoadingAnim(updateDataBtn, updateAnimImg);
				}

				@Override
				public void onFailure(Throwable error) {
				}
			});
		} else {
			Toast.makeText(this, R.string.network_error, 0).show();
		}

	}

	@Override
	public void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	public void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		mMapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		autoFlush();
	}

	private void autoFlush() {
		long nowTime = System.currentTimeMillis();

		sp = getSharedPreferences("autoupdate", 0);
		long loadTime = sp.getLong("siteloadTime", 0);
		if (nowTime - loadTime > Constant.UPDATE_TIME) {
			updateData();
			sp.edit().putLong("siteloadTime", nowTime).commit();
		}
	}

	private void updateExponent() {
		CityAqi cityAqi = cityAqis.get(HomeActivity.currentIndexOut);

		this.pm10_value_text.setText(cityAqi.getPm10());
		this.pm10_value_text.setTextColor(getResources().getColor(GradeTool.getTextColorByAqi(cityAqi.getPm10_aqi())));
		this.pm10_value_text.setBackgroundDrawable(getResources().getDrawable(
				GradeTool.getColorByIndex(cityAqi.getPm10_aqi())));
		this.O3_value_text.setText(cityAqi.getO3());
		this.O3_value_text.setTextColor(getResources().getColor(GradeTool.getTextColorByAqi(cityAqi.getO3_aqi())));
		this.O3_value_text.setBackgroundDrawable(getResources().getDrawable(
				GradeTool.getColorByIndex(cityAqi.getO3_aqi())));
		this.SO2_value_text.setText(cityAqi.getSo2());
		this.SO2_value_text.setTextColor(getResources().getColor(GradeTool.getTextColorByAqi(cityAqi.getSo2_aqi())));
		this.SO2_value_text.setBackgroundDrawable(getResources().getDrawable(
				GradeTool.getColorByIndex(cityAqi.getSo2_aqi())));
		this.NO2_value_text.setText(cityAqi.getNo2());
		this.NO2_value_text.setTextColor(getResources().getColor(GradeTool.getTextColorByAqi(cityAqi.getNo2_aqi())));
		this.NO2_value_text.setBackgroundDrawable(getResources().getDrawable(
				GradeTool.getColorByIndex(cityAqi.getNo2_aqi())));
		this.CO_value_text.setText(cityAqi.getCo());
		this.CO_value_text.setTextColor(getResources().getColor(GradeTool.getTextColorByAqi(cityAqi.getCo_aqi())));
		this.CO_value_text.setBackgroundDrawable(getResources().getDrawable(
				GradeTool.getColorByIndex(cityAqi.getCo_aqi())));
	}

	private void findView() {
		//
		this.site_layout = (LinearLayout) findViewById(R.id.site_layout);
		/* this.noSite_layout = (LinearLayout) findViewById(R.id.ll_pm); */
		this.backBtn = (ImageView) findViewById(R.id.back_btn);
		this.backBtn.setOnClickListener(this);
		this.cityNameText = (TextView) findViewById(R.id.city_name_text);
		this.cityNameText.setText(cityName);
		this.updateDataBtn = (ImageButton) findViewById(R.id.update_image);
		this.updateDataBtn.setOnClickListener(this);
		this.shareBtn = (ImageView) findViewById(R.id.share_image);
		this.shareBtn.setOnClickListener(this);
		this.siteView = (LinearLayoutForListView) findViewById(R.id.lv_site_data);
		this.updateAnimImg = (ImageView) findViewById(R.id.update_image_animation);

		// 下方pm信息
		this.pm10_value_text = (TextView) findViewById(R.id.tv_pm10_value);
		this.O3_value_text = (TextView) findViewById(R.id.tv_O3_value);
		this.SO2_value_text = (TextView) findViewById(R.id.tv_SO2_value);
		this.NO2_value_text = (TextView) findViewById(R.id.tv_NO2_value);
		this.CO_value_text = (TextView) findViewById(R.id.tv_CO_value);

		// map信息
		this.mMapView = (MapView) findViewById(R.id.bmapView1);
		this.scrollView = (ScrollView) findViewById(R.id.scroll_view);

	}

}
