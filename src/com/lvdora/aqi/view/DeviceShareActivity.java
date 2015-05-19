package com.lvdora.aqi.view;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lvdora.aqi.R;
import com.lvdora.aqi.adapter.ExAdapter;
import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.aqi.dao.CityDao;
import com.lvdora.aqi.dao.DeviceDao;
import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.model.Device;
import com.lvdora.aqi.module.ModuleActivitiesManager;
import com.lvdora.aqi.util.AsyncHttpClient;
import com.lvdora.aqi.util.AsyncHttpResponseHandler;
import com.lvdora.aqi.util.Constant;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.GradeTool;
import com.lvdora.aqi.util.NetworkTool;
import com.lvdora.aqi.util.ShareTool;
import com.lvdora.aqi.util.UpdateTool;

/**
 * 民间站点
 * 
 * @author xqp
 */
public class DeviceShareActivity extends Fragment implements OnClickListener {

	private static final int UPDATE_UI = 1;
	private static int CITY_INDEX = 0;

	private ProgressDialog pDialog;
	private ExpandableListView listDevice;
	private List<Device> devices;

	private String deviceJson;
	private TextView setCityAqi;
	private TextView setCityPm25;
	private TextView setCityName;
	private TextView update_time;
	private TextView cityName;
	private ImageButton shareImageBtn;// 分享按钮
	private ImageButton updateImageBtn;// 更新按钮
	private ImageView updateImageAnimation;// 更新动画
	private TextView publicPlace;
	private TextView privateShare;

	private SharedPreferences sp;
	private CityAqi locationCity;
	private int cityId;
	private CityAqiDao cityAqiDB;
	private DeviceDao deviceDao;
	private String[] cityIdArray = new String[] { "18", "286" };
	//
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_UI: {
				getDeviceFromServer();
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

		sp = getActivity().getSharedPreferences("location", 0);
		cityId = sp.getInt("cityId", 0);
		//
		getDeviceFromServer();
		//
		deviceDao = new DeviceDao(getActivity());
		//
		devices = new ArrayList<Device>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// 当前页面加入activity管理模块
		ModuleActivitiesManager.getActivitiesStack().push(getActivity());

		//
		sp = getActivity().getSharedPreferences("location", 0);
		cityId = sp.getInt("cityId", 0);

		//
		View devicesView = inflater.inflate(R.layout.devices_activity, container, false);
		findView(devicesView);

		// 初始化数据
		initCityData();

		if (!NetworkTool.isNetworkConnected(getActivity())) {
			showPublicPlace();
		}
		//
		else {
			// 判断获取数据结束
			pDialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.getting_data));
			pDialog.show();
			pDialog.setCancelable(true);

			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						sp = getActivity().getSharedPreferences("jsondata", 0);
						deviceJson = sp.getString("devicejson", "");
						if (!deviceJson.trim().equals("")) {
							pDialog.dismiss();
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
		return devicesView;
	}

	/**
	 * 自动更新
	 */
	@Override
	public void onStart() {
		super.onStart();
		halfHourUpdate();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.devices_share_image:
			if (NetworkTool.isNetworkConnected(getActivity())) {
				String filePath = DataTool.createFileDir("Share_Imgs");
				String tmpTime = String.valueOf(System.currentTimeMillis());
				String path = filePath + "/" + getResources().getString(R.string.app_name) + "_民间站点_" + tmpTime
						+ ".png";
				ShareTool.shoot(path, getActivity());
				String share = "绿朵民间站点分享。下载地址：http://app.lvdora.com";
				ShareTool.SharePhoto(path, "绿朵分享", share, getActivity());
			} else {
				Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.devices_update_image:
			onFlush();
			break;
		// 城市列表的接口
		case R.id.city_name:
			changeCity();
			break;
		case R.id.public_place:

			showPublicPlace();
			break;
		case R.id.private_share:

			showPrivateShare();
			break;
		default:
			break;
		}

	}

	// 刷新数据
	public void onFlush() {
		Log.d("DeviceActivity", "Flush");
		if (NetworkTool.isNetworkConnected(getActivity())) {
			getDeviceFromServer();
		} else {
			Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
		}
	}

	// 添加站点列表
	private void initDeviceData(List<String> groupList, String cityId, String style) {
		ExAdapter deviceListAdapter = new ExAdapter(getActivity(), groupList, cityId, style);
		listDevice.setAdapter(deviceListAdapter);
		listDevice.setGroupIndicator(null);
		listDevice.setDivider(null);
		// 默认展开
		int groupCount = listDevice.getCount();
		for (int i = 0; i < groupCount; i++) {
			listDevice.expandGroup(i);
		}
	}

	/*
	 * 初始化城市信息
	 */
	private void initCityData() {
		cityAqiDB = new CityAqiDao(getActivity(), "");
		locationCity = cityAqiDB.getItem(cityId);

		setCityName.setText(locationCity.getCityName());

		setCityAqi.setText(locationCity.getAqi());
		int tColor = GradeTool.getTextColorByAqi(locationCity.getAqi());
		setCityAqi.setTextColor(getResources().getColor(tColor));
		int bColor = GradeTool.getAqiColorByIndex(locationCity.getAqi());
		setCityAqi.setBackgroundDrawable(getResources().getDrawable(bColor));

		setCityPm25.setText(locationCity.getPm25());
		update_time.setText("更新" + locationCity.getAqi_pubtime().substring(11, 16));
	}

	@SuppressWarnings("unused")
	private void updateCityAqi() {
		//
		cityAqiDB.delByOrder(locationCity.getOrder());
		//
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(Constant.SERVER_URL + locationCity.getCityId(), new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				CityAqi cityAqi = DataTool.getCityAqi(result, locationCity.getCityId(), 0);
				cityAqiDB.delByOrder(0);
				cityAqiDB.saveData(cityAqi);
				// 重新赋值
				initCityData();
				UpdateTool.stopLoadingAnim(updateImageBtn, updateImageAnimation);
			}
		});
	}

	// 北京民间站点数据
	private void getDeviceFromServer() {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(Constant.DEVICE_JAVA_URL + cityIdArray[CITY_INDEX], new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				UpdateTool.startLoadingAnim(getActivity(), updateImageBtn, updateImageAnimation);
			}

			@Override
			public void onSuccess(String result) {
				Log.v("DeviceActivity", result);
				sp = getActivity().getSharedPreferences("jsondata", 0);
				sp.edit().putString("devicejson", result).commit();
				deviceJson = sp.getString("devicejson", "");

				devices = new ArrayList<Device>();
				devices = DataTool.deviceJson2List(deviceJson);

				deviceDao.insertAllDevice(devices);

				UpdateTool.stopLoadingAnim(updateImageBtn, updateImageAnimation);

				showCity();
			}
		});
	}

	private void findView(View devicesView) {
		listDevice = (ExpandableListView) devicesView.findViewById(R.id.ExpandableListView);
		setCityAqi = (TextView) devicesView.findViewById(R.id.show_city_aqi);
		setCityPm25 = (TextView) devicesView.findViewById(R.id.show_city_pm25);
		update_time = (TextView) devicesView.findViewById(R.id.update_minjian);
		setCityName = (TextView) devicesView.findViewById(R.id.city_name);
		setCityName.setOnClickListener(this);
		shareImageBtn = (ImageButton) devicesView.findViewById(R.id.devices_share_image);
		shareImageBtn.setOnClickListener(this);
		updateImageBtn = (ImageButton) devicesView.findViewById(R.id.devices_update_image);
		updateImageBtn.setOnClickListener(this);
		updateImageAnimation = (ImageView) devicesView.findViewById(R.id.devides_update_image_animation);
		publicPlace = (TextView) devicesView.findViewById(R.id.public_place);
		publicPlace.setOnClickListener(this);
		privateShare = (TextView) devicesView.findViewById(R.id.private_share);
		privateShare.setOnClickListener(this);
	}

	private void halfHourUpdate() {
		long nowTime = System.currentTimeMillis();
		sp = getActivity().getSharedPreferences("autoupdate", 0);
		long loadTime = sp.getLong("deviceloadTime", 0);
		if (nowTime - loadTime > Constant.UPDATE_TIME) {
			onFlush();
			sp.edit().putLong("deviceloadTime", nowTime).commit();
		}
	}

	@SuppressWarnings("unused")
	private void addListView() {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View listView = inflater.inflate(R.layout.devices_single_list, null);
		listView.setVisibility(View.VISIBLE);
	}

	private void changeCity() {
		CITY_INDEX = (CITY_INDEX + 1) % cityIdArray.length;
		getDeviceFromServer();
	}

	private void showCity() {
		// 1 cityName
		String[] cityNameArray = new String[cityIdArray.length];
		CityDao cityDao = new CityDao(getActivity());
		for (int i = 0; i < cityIdArray.length; i++) {
			cityNameArray[i] = cityDao.selectNameById(cityIdArray[i]);
		}
		setCityName.setText(cityNameArray[CITY_INDEX]);
		// 2 site
		showPublicPlace();
	}

	private void showPublicPlace() {
		publicPlace.setBackgroundColor(Color.parseColor("#A9DBFD"));
		publicPlace.setTextColor(Color.parseColor("#0a65c8"));
		privateShare.setBackgroundColor(Color.parseColor("#3F85CA"));
		privateShare.setTextColor(Color.parseColor("#8FB9E1"));
		devideCounty("public");
	}

	private void showPrivateShare() {
		privateShare.setBackgroundColor(Color.parseColor("#A9DBFD"));
		privateShare.setTextColor(Color.parseColor("#0a65c8"));
		publicPlace.setBackgroundColor(Color.parseColor("#3F85CA"));
		publicPlace.setTextColor(Color.parseColor("#8FB9E1"));
		devideCounty("private");
	}

	private void devideCounty(String publicOrPrivate) {
		List<String> groupList = new ArrayList<String>();
		DeviceDao deviceDao = new DeviceDao(getActivity());
		try {
			groupList = deviceDao.selectGroup(cityIdArray[CITY_INDEX], publicOrPrivate);
		} catch (Exception e) {
			
		}
		initDeviceData(groupList, cityIdArray[CITY_INDEX], publicOrPrivate);
	}
}