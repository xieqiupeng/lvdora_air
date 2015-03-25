package com.lvdora.aqi.view;

import java.util.List;
import android.app.Activity;
import android.content.Context;
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
import com.lvdora.aqi.util.NetworkTool;
import com.lvdora.aqi.util.ShareTool;
import com.lvdora.aqi.util.UpdateTool;

public class RankActivity extends Fragment {

	private ListView listCity;
	private TextView aqiText;
	private TextView pm25Text;
	private TextView pm10Text;
	private TextView o3Text;
	private TextView so2Text;
	private TextView no2Text;
	private TextView rank;

	private ImageButton shareImageBtn;// 分享按钮
	protected ImageButton updateImageBtn;// 更新按钮
	public ImageView updateImageAnimation;// 更新动画
	private ImageButton rankSelect;// 排名顺序按钮
	private List<CityRank> cityRankList;
	private String indexType;
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
		aqiText = (TextView) rankView.findViewById(R.id.air_aqi);
		aqiText.setOnTouchListener(new touchListener());
		pm25Text = (TextView) rankView.findViewById(R.id.air_pm25);
		pm25Text.setOnTouchListener(new touchListener());
		pm10Text = (TextView) rankView.findViewById(R.id.air_pm10);
		pm10Text.setOnTouchListener(new touchListener());
		so2Text = (TextView) rankView.findViewById(R.id.air_so2);
		so2Text.setOnTouchListener(new touchListener());
		o3Text = (TextView) rankView.findViewById(R.id.air_o3);
		o3Text.setOnTouchListener(new touchListener());
		no2Text = (TextView) rankView.findViewById(R.id.air_no2);
		no2Text.setOnTouchListener(new touchListener());
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

		aqiText.setTextColor(getResources().getColor(R.color.black));
		pm25Text.setTextColor(getResources().getColor(R.color.black));
		pm10Text.setTextColor(getResources().getColor(R.color.black));
		so2Text.setTextColor(getResources().getColor(R.color.black));
		o3Text.setTextColor(getResources().getColor(R.color.black));
		no2Text.setTextColor(getResources().getColor(R.color.black));
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
			client.get(Constant.JSON_SERVER, new AsyncHttpResponseHandler() {
				@Override
				public void onStart() {
					UpdateTool.startLoadingAnim(getActivity(), updateImageBtn, updateImageAnimation);
				}

				@Override
				public void onSuccess(String result) {
					try {
						cityRankList = DataTool.getCityRank(getActivity(), result);
						cityRankJson = DataTool.CityRankList2String(cityRankList);
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
				clearAllTextColors();
				aqiText.setBackgroundResource(R.drawable.aqi_index_shape_green);
				aqiText.setTextColor(getResources().getColor(R.color.white));
				indexType = "aqi_aqi";
				DataTool.showRankData(cityRankJson, indexType, rankUpOrDown, getActivity(), listCity);
				changeAllState();
				aqiText.setClickable(false);
				break;
			case R.id.air_pm25:
				clearAllTextColors();
				pm25Text.setBackgroundResource(R.drawable.aqi_index_shape_green);
				pm25Text.setTextColor(getResources().getColor(R.color.white));
				indexType = "pm25";
				DataTool.showRankData(cityRankJson, indexType, rankUpOrDown, getActivity(), listCity);
				changeAllState();
				pm25Text.setClickable(false);
				break;
			case R.id.air_pm10:
				clearAllTextColors();
				pm10Text.setBackgroundResource(R.drawable.aqi_index_shape_green);
				pm10Text.setTextColor(getResources().getColor(R.color.white));
				indexType = "pm10";
				DataTool.showRankData(cityRankJson, indexType, rankUpOrDown, getActivity(), listCity);
				changeAllState();
				pm10Text.setClickable(false);
				break;
			case R.id.air_so2:
				clearAllTextColors();
				so2Text.setBackgroundResource(R.drawable.aqi_index_shape_green);
				so2Text.setTextColor(getResources().getColor(R.color.white));
				indexType = "so2";
				DataTool.showRankData(cityRankJson, indexType, rankUpOrDown, getActivity(), listCity);
				changeAllState();
				so2Text.setClickable(false);
				break;
			case R.id.air_no2:
				clearAllTextColors();
				no2Text.setBackgroundResource(R.drawable.aqi_index_shape_green);
				no2Text.setTextColor(getResources().getColor(R.color.white));
				indexType = "no2";
				DataTool.showRankData(cityRankJson, indexType, rankUpOrDown, getActivity(), listCity);
				changeAllState();
				no2Text.setClickable(false);
				break;
			case R.id.air_o3:
				clearAllTextColors();
				o3Text.setBackgroundResource(R.drawable.aqi_index_shape_green);
				o3Text.setTextColor(getResources().getColor(R.color.white));
				indexType = "o3";
				DataTool.showRankData(cityRankJson, indexType, rankUpOrDown, getActivity(), listCity);
				changeAllState();
				o3Text.setClickable(false);
				break;
			default:
				break;
			}
			return true;
		}
	}

}