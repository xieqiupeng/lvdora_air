package com.lvdora.aqi.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lvdora.aqi.R;
import com.lvdora.aqi.model.CityRank;
import com.lvdora.aqi.util.AsyncHttpClient;
import com.lvdora.aqi.util.AsyncHttpResponseHandler;
import com.lvdora.aqi.util.Constant;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.EnAndDecryption;
import com.lvdora.aqi.util.NetworkTool;
import com.lvdora.aqi.util.ShareTool;
import com.lvdora.aqi.util.UpdateTool;

/**
 * 排行榜
 * 
 * @author xqp
 */
public class RankActivity extends Fragment {

	private ListView listCity;
	private TextView themeRank;
	private TextView viewAqi;
	private TextView aqiText;
	private TextView pm25Text;
	private TextView pm10Text;
	private TextView o3Text;
	private TextView so2Text;
	private TextView no2Text;
	private TextView rank;
	private ImageButton rankNext;

	private ImageButton shareImageBtn;// 分享按钮
	protected ImageButton updateImageBtn;// 更新按钮
	public ImageView updateImageAnimation;// 更新动画
	private ImageButton rankSelect;// 排名顺序按钮
	private List<CityRank> cityRankList;
	//
	private String[] indexTypes;
	private String indexType;
	// indexTypes 的位数
	private int indexFlag;

	private String cityRankJson;
	private boolean rankUpOrDown;
	private SharedPreferences sp;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sp = getActivity().getSharedPreferences("jsondata", 0);
		cityRankJson = sp.getString("cityrankjson", "");
		sp.getString("pubtime", "");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rankView = inflater.inflate(R.layout.rank_activity, container, false);
		//
		themeRank = (TextView) rankView.findViewById(R.id.theme_rank);
		//
		viewAqi = (TextView) rankView.findViewById(R.id.lv_air_aqi);

		//
		rankNext = (ImageButton) rankView.findViewById(R.id.rank_next);
		rankNext.setOnClickListener(new changeListener());
		aqiText = (TextView) rankView.findViewById(R.id.air_aqi);
		aqiText.setOnTouchListener(new touchListener());
		pm25Text = (TextView) rankView.findViewById(R.id.air_pm25);
		pm25Text.setOnTouchListener(new touchListener());
		pm10Text = (TextView) rankView.findViewById(R.id.air_pm10);
		pm10Text.setOnTouchListener(new touchListener());
		so2Text = (TextView) rankView.findViewById(R.id.air_so2);
		so2Text.setOnTouchListener(new touchListener());
		no2Text = (TextView) rankView.findViewById(R.id.air_no2);
		no2Text.setOnTouchListener(new touchListener());
		o3Text = (TextView) rankView.findViewById(R.id.air_o3);
		o3Text.setOnTouchListener(new touchListener());
		//
		rank = (TextView) rankView.findViewById(R.id.rank_update_time);
		listCity = (ListView) rankView.findViewById(R.id.air_rank_city);
		shareImageBtn = (ImageButton) rankView.findViewById(R.id.share_image);
		shareImageBtn.setOnClickListener(new changeListener());
		updateImageBtn = (ImageButton) rankView.findViewById(R.id.update_image);
		rankSelect = (ImageButton) rankView.findViewById(R.id.rank_up_or_down);
		rankSelect.setOnClickListener(new changeListener());
		updateImageBtn = (ImageButton) rankView.findViewById(R.id.update_image);
		updateImageBtn.setOnClickListener(new changeListener());
		updateImageAnimation = (ImageView) rankView.findViewById(R.id.update_image_animation);
		// 设置默认排序为最差城市排名
		rankUpOrDown = true;
		initView();
		return rankView;
	}

	@Override
	public void onStart() {
		super.onStart();
		long nowTime = System.currentTimeMillis();
		sp = getActivity().getSharedPreferences("autoupdate", 0);
		long loadTime = sp.getLong("rankLoadTime", 0);
		if (nowTime - loadTime > Constant.UPDATE_TIME) {
			OnFlush();
			sp.edit().putLong("rankLoadTime", nowTime).commit();
		}
	}

	/**
	 * 
	 */
	public void initView() {
		clearAllTextColors();
		// 默认显示AQI
		aqiText.setBackgroundResource(R.drawable.aqi_index_shape_green);
		aqiText.setTextColor(getResources().getColor(R.color.white));
		indexType = "aqi_aqi";
		DataTool.showRankData(cityRankJson, indexType, rankUpOrDown, getActivity(), listCity);
		OnFlush();
	}

	// 可点击
	public void changeAllState() {
		aqiText.setClickable(true);
		pm25Text.setClickable(true);
		pm10Text.setClickable(true);
		so2Text.setClickable(true);
		o3Text.setClickable(true);
		no2Text.setClickable(true);
	}

	/**
	 * 清空颜色
	 */
	public void clearAllTextColors() {
		aqiText.setBackgroundColor(0x00ffffff);
		pm25Text.setBackgroundColor(0x00ffffff);
		pm10Text.setBackgroundColor(0x00ffffff);
		so2Text.setBackgroundColor(0x00ffffff);
		o3Text.setBackgroundColor(0x00ffffff);
		no2Text.setBackgroundColor(0x00ffffff);

		aqiText.setTextColor(getResources().getColor(R.color.white));
		pm25Text.setTextColor(getResources().getColor(R.color.white));
		pm10Text.setTextColor(getResources().getColor(R.color.white));
		so2Text.setTextColor(getResources().getColor(R.color.white));
		o3Text.setTextColor(getResources().getColor(R.color.white));
		no2Text.setTextColor(getResources().getColor(R.color.white));
	}

	// 刷新数据
	public void OnFlush() {
		if (NetworkTool.isNetworkConnected(getActivity())) {
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(Constant.JSON_SERVER, new AsyncHttpResponseHandler() {
				@Override
				public void onStart() {
					UpdateTool.startLoadingAnim(getActivity(), updateImageBtn, updateImageAnimation);
				}

				@Override
				public void onSuccess(String result) {
					try {
						cityRankList = DataTool.getCityRank(getActivity(), result);
						cityRankJson = EnAndDecryption.CityRankList2String(cityRankList);
						sp = getActivity().getSharedPreferences("jsondata", 0);
						sp.edit().putString("cityrankjson", cityRankJson).commit();
						DataTool.showRankData(cityRankJson, indexType, rankUpOrDown, getActivity(), listCity);
						String subPubtime = result.substring(result.indexOf("pubtime") + 10,
								result.indexOf("pubtime") + 26);
						rank.setText("更新：" + subPubtime);
						Log.i("lvdora", "sml:" + subPubtime);
						UpdateTool.stopLoadingAnim(updateImageBtn, updateImageAnimation);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure(Throwable error) {
				}
			});
		} else {
			Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * click监听
	 * 
	 */
	class changeListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.update_image:
				// 判断网络存在则刷新数据，无网络则提示连接网络
				OnFlush();
				break;
			case R.id.share_image:
				if (NetworkTool.isNetworkConnected(getActivity())) {
					String filePath = DataTool.createFileDir("Share_Imgs");
					String tmpTime = String.valueOf(System.currentTimeMillis());
					String path = filePath + "/" + getResources().getString(R.string.app_name) + "_城市排名_" + tmpTime
							+ ".png";
					ShareTool.shoot(path, getActivity());
					ShareTool.SharePhoto(path, "绿朵分享", "全国主要城市空气质量排名。下载地址：http://app.lvdora.com", getActivity());
				} else {
					Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.rank_up_or_down:
				rankUpOrDown = !rankUpOrDown;
				if (rankUpOrDown) {
					rankSelect.setBackgroundDrawable(getResources().getDrawable(R.drawable.rank_down));
				} else {
					rankSelect.setBackgroundDrawable(getResources().getDrawable(R.drawable.rank_up));
				}
				DataTool.showRankData(cityRankJson, indexType, rankUpOrDown, getActivity(), listCity);
				break;
			case R.id.rank_next:
				changeExponent(++indexFlag);
			default:
				break;
			}
		}
	}

	/**
	 * touch监听
	 * 
	 */
	class touchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.air_aqi:
				// TODO
				indexFlag = 0;
				changeExponent(indexFlag);
				break;
			case R.id.air_pm25:
				indexFlag = 1;
				changeExponent(indexFlag);
				break;
			case R.id.air_pm10:
				indexFlag = 2;
				changeExponent(indexFlag);
				break;
			case R.id.air_so2:
				indexFlag = 3;
				changeExponent(indexFlag);
				break;
			case R.id.air_no2:
				indexFlag = 4;
				changeExponent(indexFlag);
				break;
			case R.id.air_o3:
				indexFlag = 5;
				changeExponent(indexFlag);
				break;
			default:
				break;
			}
			return true;
		}
	}

	// 改变展示项目
	private void changeExponent(int indexFlag) {
		indexFlag = indexFlag % 6;
		Log.v("RankActivity", indexFlag+"");
		// get view
		Map<Integer, TextView> mapView = new HashMap<Integer, TextView>();
		mapView.put(0, aqiText);
		mapView.put(1, pm25Text);
		mapView.put(2, pm10Text);
		mapView.put(3, so2Text);
		mapView.put(4, no2Text);
		mapView.put(5, o3Text);
		TextView view = mapView.get(indexFlag);
		// get indexType
		indexTypes = new String[] { "aqi_aqi", "pm25", "pm10", "so2", "no2", "o3" };
		indexType = indexTypes[indexFlag];
		Map<String, String> map = new HashMap<String, String>();
		map.put(indexTypes[0], "AQI");
		map.put(indexTypes[1], "PM2.5");
		map.put(indexTypes[2], "PM10");
		map.put(indexTypes[3], "SO₂");
		map.put(indexTypes[4], "NO₂");
		map.put(indexTypes[5], "O₃");

		clearAllTextColors();
		view.setBackgroundResource(R.drawable.aqi_index_shape_green);
		view.setTextColor(getResources().getColor(R.color.white));
		DataTool.showRankData(cityRankJson, indexType, rankUpOrDown, getActivity(), listCity);
		changeAllState();
		themeRank.setText(map.get(indexType));
		viewAqi.setText(map.get(indexType));
		view.setClickable(false);
	}

}