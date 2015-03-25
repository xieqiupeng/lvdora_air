package com.lvdora.aqi.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.lvdora.aqi.baidu.BMapUtil;
import com.lvdora.aqi.baidu.MyApplication;
import com.lvdora.aqi.model.MapPopup;
import com.lvdora.aqi.util.AsyncHttpClient;
import com.lvdora.aqi.util.AsyncHttpResponseHandler;
import com.lvdora.aqi.util.Constant;
import com.lvdora.aqi.util.GradeTool;
import com.lvdora.aqi.util.NetworkTool;
import com.lvdora.aqi.util.UpdateTool;

public class MapActivity extends Fragment implements OnClickListener {

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
	protected ImageButton updateDataBtn;// 更新按钮
	public ImageView updateImageAnimation;// 更新动画
	private Button button;
	private OverlayItem mCurItem;
	private int aqi;

	private SharedPreferences sp;
	private int nowStatus = 0;
	private int myStatus = 0;
	private DisplayMetrics dm;

	/**
	 * overlay 位置坐标
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sp = getActivity().getSharedPreferences("jsondata", 0);
		mapJson = sp.getString("rankjson", "");
		mapSiteJson = sp.getString("mapsitejson", "");
		/**
		 * 使用地图sdk前需先初始化BMapManager. BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
		 * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
		 */
		MyApplication app = (MyApplication) getActivity().getApplication();
		if (app.mBMapManager == null) {

			app.mBMapManager = new BMapManager(getActivity());
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(new MyApplication.MyGeneralListener());
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View mapView = inflater
				.inflate(R.layout.map_activity, container, false);
		// TODO 取得屏幕的分辨率
		dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		mMapView = (MapView) mapView.findViewById(R.id.bmapView);
		updateDataBtn = (ImageButton) mapView.findViewById(R.id.update_image);
		updateDataBtn.setOnClickListener(this);
		updateImageAnimation = (ImageView) mapView
				.findViewById(R.id.update_image_animation);
		/**
		 * 获取地图控制器
		 */
		mMapController = mMapView.getController();
		/**
		 * 设置地图是否响应点击事件 .
		 */
		mMapController.enableClick(true);
		/**
		 * 设置地图缩放级别
		 */
		mMapController.setZoom(5);
		/**
		 * 显示内置缩放控件
		 */
		// mMapView.setBuiltInZoomControls(true);

		/**
		 * 设定类型
		 */
		type = "captial";

		/**
		 * 初始化显示省会城市信息
		 */
		initOverlay();

		/**
		 * 设定地图中心点
		 */
		GeoPoint p = new GeoPoint((int) (34.759998 * 1E6),
				(int) (113.650002 * 1E6));
		mMapController.setCenter(p);
		
		/**
		 * 地图状态监听
		 */
		MapStatusChange();

		return mapView;
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
				if (zoom >= 7 && zoom <= 11) {
					nowStatus = 2;
				}
				if (zoom >= 11 && zoom <= 18) {
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
		mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.popup),
				mMapView);
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
		mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.popup),
				mMapView);
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
		mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.popup),
				mMapView);
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
					spotLongitude = (int) (Float.parseFloat(obj.getString(
							"cityLng").toString()) * 1E6);// obj.getDouble("longtitude");
					spotLatitude = (int) (Float.parseFloat(obj.getString(
							"cityLat").toString()) * 1E6);
					if (latitudeDownLimt < spotLatitude
							&& spotLatitude < latitudeUpLimt
							&& longitudeDownLimt < spotLongitude
							&& spotLongitude < longitudeUpLimt) {
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
			mPopup.setAirQuality(GradeTool.getStateByIndex(Integer
					.parseInt(object.getString("aqi_aqi"))));
			popList.add(mPopup);
			aqi = Integer.parseInt(object.getString("aqi_aqi"));
			GeoPoint p = new GeoPoint(
					(int) (object.getDouble("cityLat") * 1E6),
					(int) (object.getDouble("cityLng") * 1E6));
			OverlayItem item = new OverlayItem(p, "覆盖物", "");
			// 覆盖物
			mOverlay.addItem(item);
			// Android 文字绘制到Bitmap上 OpenGL ES中似乎不能输出文本.
			// 将文本写到Bitmap上,再作为贴图,则可实现文字输出. 文字绘制到Bitmap上的方法为:
			String value = object.getString("aqi_aqi");
			Bitmap bmp = Bitmap.createBitmap(dm.widthPixels/12, dm.widthPixels/20, Bitmap.Config.ARGB_8888); // 图象大小要根据文字大小算下,以和文本长度对应
			Canvas canvasTemp = new Canvas(bmp);
			// 涂上颜色
			canvasTemp.drawColor(getResources().getColor(
					GradeTool.getMapColorByIndex(aqi + "")));
			Paint pa = new Paint();
			String familyName = object.getString("aqi_aqi");
			Typeface font = Typeface.create(familyName, Typeface.BOLD);
			// Typeface font = Typeface.create(value, Typeface.BOLD);
			pa.setColor(getActivity().getResources().getColor(
					GradeTool.getTextColorByAqi(aqi + "")));
			pa.setTypeface(font);
			pa.setTextSize(dm.widthPixels/25);
			canvasTemp.drawText(value,
					(float) (dm.widthPixels/10 - pa.measureText(value)) / 4, dm.widthPixels/25, pa);// 文字居中显示
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
		viewCache = getActivity().getLayoutInflater().inflate(
				R.layout.custom_text_myview, null);
		popupInfo = (View) viewCache.findViewById(R.id.mypopinfo);
		button = new Button(getActivity());
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
					GeoPoint p = new GeoPoint(mCurItem.getPoint()
							.getLatitudeE6() , mCurItem.getPoint()
							.getLongitudeE6());
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
							int aqiValue = Integer.parseInt(obj
									.getString("aqi_aqi"));
							// 改变textview值
							TextView city = (TextView) popupInfo
									.findViewById(R.id.city);
							city.setText(obj.getString("spotName"));
							TextView aqi = (TextView) popupInfo
									.findViewById(R.id.aqi);
							aqi.setText("AQI：" + obj.getString("aqi_aqi"));
							TextView airQuality = (TextView) popupInfo
									.findViewById(R.id.level);
							airQuality.setTextSize(18);
							airQuality.setText("空气质量："
									+ GradeTool.getStateByIndex(aqiValue));
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
							TextView city = (TextView) popupInfo
									.findViewById(R.id.city);
							city.setText(map.getCityCame());
							TextView aqi = (TextView) popupInfo
									.findViewById(R.id.aqi);
							aqi.setText("AQI：" + map.getAqi());
							TextView airQuality = (TextView) popupInfo
									.findViewById(R.id.level);
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
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
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
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.update_image:
			OnFlush();
			break;
		default:
			break;
		}
	}

	/**
	 * 刷新数据
	 */
	public void OnFlush() {
		if (NetworkTool.isNetworkConnected(getActivity())) {

			AsyncHttpClient client = new AsyncHttpClient();

			client.get(Constant.JSON_SERVER, new AsyncHttpResponseHandler() {
				@Override
				public void onStart() {
					// 更新按钮的动画开始旋转
					UpdateTool.startLoadingAnim(getActivity(), updateDataBtn,
							updateImageAnimation);
				}

				@Override
				public void onSuccess(String result) {
					mapJson = result;
					sp = getActivity().getSharedPreferences("jsondata", 0);
					sp.edit().putString("rankjson", mapJson).commit();
					initOverlay();
					// 更新按钮的动画结束旋转
					UpdateTool.stopLoadingAnim(updateDataBtn,
							updateImageAnimation);
				}

				@Override
				public void onFailure(Throwable error) {
				}
			});
		} else {
			Toast.makeText(getActivity(), R.string.network_error, 0).show();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		long nowTime = System.currentTimeMillis();
		sp = getActivity().getSharedPreferences("autoupdate", 0);
		long loadTime = sp.getLong("mapLoadTime", 0);
		if (nowTime - loadTime > Constant.UPDATE_TIME) {
			OnFlush();
			sp.edit().putLong("mapLoadTime", nowTime).commit();
		}
	}
}