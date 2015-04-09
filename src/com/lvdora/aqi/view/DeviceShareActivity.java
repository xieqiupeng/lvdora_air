package com.lvdora.aqi.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lvdora.aqi.R;
import com.lvdora.aqi.adapter.DeviceAdapter;
import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.module.ModuleActivitiesManager;
import com.lvdora.aqi.util.AsyncHttpClient;
import com.lvdora.aqi.util.AsyncHttpResponseHandler;
import com.lvdora.aqi.util.Constant;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.ExitTool;
import com.lvdora.aqi.util.GradeTool;
import com.lvdora.aqi.util.NetworkTool;
import com.lvdora.aqi.util.ShareTool;
import com.lvdora.aqi.util.UpdateTool;
/**
 * 
 * @author Administrator
 *
 */
public class DeviceShareActivity extends Fragment implements OnClickListener {

	private static final int UPDATE_UI = 1;
	private ProgressDialog pDialog;
	private ListView listDevice;
	private List<Map<String, Object>> devices;
	private DeviceAdapter deviceListAdapter;
	private String deviceJson;
	private TextView setCityAqi;
	private TextView setCityPm25;
	private TextView setCityName;
	private TextView update_time;
	private ImageButton shareImageBtn;// 分享按钮
	private ImageButton updateImageBtn;// 更新按钮
	private ImageView updateImageAnimation;// 更新动画
	private SharedPreferences sp;
	private CityAqi locationCity;
	private int cityId;

	private CityAqiDao cityAqiDB;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_UI: {
				initViewDeviceData();
				break;
			}
			default:
				break;
			}
		}
	};

	/**
	 * 
	 */
	public void initViewDeviceData() {
		devices = DataTool.getDeviceData(deviceJson);
		deviceListAdapter = new DeviceAdapter(devices, getActivity());
		listDevice.setAdapter(deviceListAdapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		
		sp = getActivity().getSharedPreferences("location", 0);
		cityId = sp.getInt("cityId", 0);

		/*
		 * sp = getActivity().getSharedPreferences("jsondata", 0); deviceJson =
		 * sp.getString("devicejson", "");
		 */
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View devicesView = inflater.inflate(R.layout.devices_activity, container, false);

		// 当前页面加入activity管理模块
		ModuleActivitiesManager.getActivitiesStack().push(getActivity());
		
		sp = getActivity().getSharedPreferences("location", 0);
		cityId = sp.getInt("cityId", 0);

		listDevice = (ListView) devicesView.findViewById(R.id.device_details);
		setCityAqi = (TextView) devicesView.findViewById(R.id.show_city_aqi);
		setCityPm25 = (TextView) devicesView.findViewById(R.id.show_city_pm25);
		setCityName = (TextView) devicesView.findViewById(R.id.city_name);
		update_time = (TextView) devicesView.findViewById(R.id.update_minjian);
		shareImageBtn = (ImageButton) devicesView.findViewById(R.id.devices_share_image);
		updateImageBtn = (ImageButton) devicesView.findViewById(R.id.devices_update_image);
		updateImageAnimation = (ImageView) devicesView.findViewById(R.id.devides_update_image_animation);
		shareImageBtn.setOnClickListener(this);
		updateImageBtn.setOnClickListener(this);

		// 初始化数据
		initCityData();

		devices = new ArrayList<Map<String, Object>>();
		// devices = DataTool.getDeviceData(deviceJson);
		if (!NetworkTool.isNetworkConnected(getActivity())) {
			initViewDeviceData();
		} else {
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

	/*
	 * 初始化城市信息
	 */
	public void initCityData() {
		cityAqiDB = new CityAqiDao(getActivity(), "");
		locationCity = cityAqiDB.getItem(cityId);

		setCityName.setText(locationCity.getCityName());
		setCityAqi.setText(locationCity.getAqi());
		setCityAqi.setTextColor(getResources().getColor(GradeTool.getMapColorByIndex(locationCity.getAqi())));
		setCityPm25.setText(locationCity.getPm25());
		update_time.setText("更新" + locationCity.getAqi_pubtime().substring(11, 16));
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
				ShareTool.SharePhoto(path, "绿朵分享", "绿朵民间站点分享。下载地址：http://app.lvdora.com", getActivity());
			} else {
				Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.devices_update_image:
			OnFlush();
			break;
		default:
			break;
		}

	}

	/**
	 * 刷新数据
	 * 
	 * @author admin
	 * 
	 */
	public void OnFlush() {
		if (NetworkTool.isNetworkConnected(getActivity())) {
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(Constant.DEVICE_URL, new AsyncHttpResponseHandler() {
				@Override
				public void onStart() {
					UpdateTool.startLoadingAnim(getActivity(), updateImageBtn, updateImageAnimation);
				}

				@Override
				public void onSuccess(String result) {
					sp = getActivity().getSharedPreferences("jsondata", 0);
					sp.edit().putString("devicejson", result).commit();
					deviceJson = sp.getString("devicejson", "");
					initViewDeviceData();
					updateCityAqi();
				}

				@Override
				public void onFailure(Throwable error) {
				}
			});
		} else {
			Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
		}
	}

	protected void updateCityAqi() {

		cityAqiDB.delByOrder(locationCity.getOrder());
		AsyncHttpClient client = new AsyncHttpClient();

		client.get(Constant.SERVER_URL + locationCity.getCityId(), new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {

			}

			@Override
			public void onSuccess(String result) {
				CityAqi cityAqi = DataTool.getCityAqi(result, locationCity.getCityId(), 0);

				cityAqiDB.delByOrder(0);
				cityAqiDB.saveData(cityAqi);
				// 重新赋值
				initCityData();
				UpdateTool.stopLoadingAnim(updateImageBtn, updateImageAnimation);
			}
			
			@Override
			public void onFailure(Throwable error) {

			}
		});
	}

	/**
	 * 自动更新
	 */
	@Override
	public void onStart() {
		super.onStart();
		long nowTime = System.currentTimeMillis();
		sp = getActivity().getSharedPreferences("autoupdate", 0);
		long loadTime = sp.getLong("deviceloadTime", 0);
		if (nowTime - loadTime > Constant.UPDATE_TIME) {
			OnFlush();
			sp.edit().putLong("deviceloadTime", nowTime).commit();
		}
	}
}