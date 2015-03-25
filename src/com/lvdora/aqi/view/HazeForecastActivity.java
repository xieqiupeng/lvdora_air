package com.lvdora.aqi.view;

import java.io.File;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebSettings;
import com.lvdora.aqi.R;
import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.aqi.graph.MyHorizontalScrollView;
import com.lvdora.aqi.graph.MyHorizontalScrollView1;
import com.lvdora.aqi.graph.StudyGraphBean;
import com.lvdora.aqi.graph.StudyGraphItem;
import com.lvdora.aqi.graph.StudyGraphView;
import com.lvdora.aqi.graph.StudyGraphView1;
import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.util.AsyncHttpClient;
import com.lvdora.aqi.util.AsyncHttpResponseHandler;
import com.lvdora.aqi.util.Constant;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.NetworkTool;
import com.lvdora.aqi.util.ShareTool;
import com.lvdora.aqi.util.UpdateTool;

public class HazeForecastActivity extends Fragment implements OnClickListener {
	private static final int UPDATE_UI = 1;
	private static MyHorizontalScrollView studyGraphLayout;
	private static StudyGraphView studyGraph;
	private MyHorizontalScrollView1 studyGraphLayout1;
	private StudyGraphView1 studyGraph1;
	private ArrayList<StudyGraphItem> studyGraphItems_day;
	private ArrayList<StudyGraphItem> studyGraphItems_month;

	// 标题
	private int cityId;
	private String cityString = "空气质量趋势图-";
	private String cityName;

	private SharedPreferences sp;
	// private ProgressDialog dialog;
	public Boolean isOver = false;
	private String htmlPath;
	private String rootPath;

	private boolean isMeasured = false;
	private TextView max;
	private TextView min;
	private TextView m1;
	private TextView m2;
	private TextView max1;
	private TextView min1;
	private TextView m_1;
	private TextView m_2;
	private ImageButton shareImageBtn;// 分享按钮
	protected ImageButton updateDataBtn;// 更新按钮
	public ImageView updateImageAnimation;// 更新动画
	private static View hazeForecastView;
	private WebView hazeSpreadForecast;
	private String json_day;
	private String json_month;
	static StudyGraphItem energy_max = new StudyGraphItem();
	static StudyGraphItem energy_min = new StudyGraphItem();

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_UI: {
				initWebView();
				// UpdateTool
				// .stopLoadingAnim(updateImageBtn, updateImageAnimation);
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		hazeForecastView = inflater.inflate(R.layout.haze_forecast_activity, container, false);
		findView();

		// 设置标题
		setTitle();

		rootPath = DataTool.createFileDir("Download");
		// initWebView();
		OnFlush();
		//
		initView();

		ViewTreeObserver observer = studyGraph1.getViewTreeObserver();
		observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (!isMeasured) {
					studyGraphLayout.scrollTo(studyGraph.getWidth(), 0);
					studyGraphLayout1.scrollTo(studyGraph.getWidth(), 0);
					isMeasured = true;
				}
				return true;
			}
		});
		return hazeForecastView;
	}

	/**
	 * 初始化界面、数据
	 * 
	 * @author sml
	 * 
	 */
	private void initView() {

		studyGraphItems_day = new ArrayList<StudyGraphItem>();
		studyGraphItems_month = new ArrayList<StudyGraphItem>();

		json_analysis("时pubtime", json_day, 21);
		json_analysis("日day", json_month, 14);

		ArrayList<StudyGraphItem> studyGraphItems_24 = new ArrayList<StudyGraphItem>();

		if (studyGraphItems_day.size() - 24 > 0) {
			for (int i = 0; i < 24; i++) {
				studyGraphItems_24.add(studyGraphItems_day.get(i + studyGraphItems_day.size() - 24));
			}
		} else {
			studyGraphItems_24 = studyGraphItems_day;
		}

		studyGraph1.setData(studyGraphItems_month);
		studyGraph.setData(studyGraphItems_24);

		// 设置纵坐标
		energy_max = findMaxPowers(studyGraphItems_24);
		energy_min = findMinPowers(studyGraphItems_24);
		max.setText(energy_max.getAqi() + "");
		min.setText(energy_min.getAqi() + "");
		m1.setText((energy_max.getAqi() / 3 + 2 * energy_min.getAqi() / 3) + "");
		m2.setText((energy_min.getAqi() / 3 + 2 * energy_max.getAqi() / 3) + "");

		energy_max = findMaxPowers(studyGraphItems_month);
		energy_min = findMinPowers(studyGraphItems_month);
		max1.setText(energy_max.getAqi() + "");
		min1.setText(energy_min.getAqi() + "");
		m_1.setText((energy_max.getAqi() / 3 + 2 * energy_min.getAqi() / 3) + "");
		m_2.setText((energy_min.getAqi() / 3 + 2 * energy_max.getAqi() / 3) + "");

	}

	/**
	 * 取得最大值
	 * 
	 * @author sml
	 * 
	 */
	private StudyGraphItem findMaxPowers(ArrayList<StudyGraphItem> energys) {
		StudyGraphItem energy = new StudyGraphItem();
		energy.setAqi(0);
		for (int i = 0; i < energys.size(); i++) {
			if (energys.get(i).getAqi() > energy.getAqi()) {
				energy = energys.get(i);
			}
		}
		return energy;
	}

	/**
	 * 取得最小值
	 * 
	 * @param energys
	 * @return
	 */
	private StudyGraphItem findMinPowers(ArrayList<StudyGraphItem> energys) {
		StudyGraphItem energy = new StudyGraphItem();
		energy.setAqi(1000);
		for (int i = 0; i < energys.size(); i++) {
			if (energys.get(i).getAqi() < energy.getAqi()) {
				energy = energys.get(i);
			}
		}
		return energy;
	}

	/**
	 * 解析数据
	 * 
	 * @author sml
	 */
	private void json_analysis(String string, String jString, int order) {
		int aqi = 0, pub = 0, aqi_value = 0;
		if (jString != null) {
			for (int i = 0; i < 31; i++) {
				aqi = jString.indexOf("aqi", aqi);
				pub = jString.indexOf(string.substring(1), pub);
				if (aqi > 0) {
					if (jString.charAt(aqi + 7) == '"') {
						aqi_value = Integer.parseInt(jString.substring(aqi + 6, aqi + 7));
					} else if (jString.charAt(aqi + 8) == '"') {
						aqi_value = Integer.parseInt(jString.substring(aqi + 6, aqi + 8));
					} else {
						aqi_value = Integer.parseInt(jString.substring(aqi + 6, aqi + 9));
					}
					if (string.charAt(0) == '时') {
						studyGraphItems_day.add(new StudyGraphBean(jString.substring(pub + order, pub + order + 2),
								aqi_value));
						// studyGraphItems_day.add(new StudyGraphBean
						// (jString.substring(pub+order,pub+order+2)+string.charAt(0),
						// aqi_value));
					} else {
						studyGraphItems_month.add(new StudyGraphBean(jString.substring(pub + order, pub + order + 2)
								+ string.charAt(0), aqi_value));
					}

				} else {
					break;
				}

				aqi = aqi + 1;
				pub = pub + 1;
			}
		}
	}

	/**
	 * 初始化WebView
	 */
	public void initWebView() {

		// dialog = ProgressDialog.show(getActivity(), "", getResources()
		// .getString(R.string.getting_data));
		// // dialog.show();
		// dialog.setCancelable(true);

		htmlPath = "file:///" + rootPath + "/spread.html";
		setWebViewContent(hazeSpreadForecast, htmlPath);

	}

	/**
	 * 加载WebView
	 * 
	 * @param webView
	 * @param url
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	public void setWebViewContent(WebView webView, String url) {

		webView.setVisibility(WebView.VISIBLE);

		// 设置WebView属性，能够执行Javascript脚本
		webView.getSettings().setJavaScriptEnabled(true);
		// 设置Web视图
		webView.setWebViewClient(new WebViewContentClient());
		// webView.loadUrl(url);

		// 设置可以支持缩放
		webView.getSettings().setSupportZoom(true);

		// 设置默认缩放方式尺寸是far
		webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

		// 设置出现缩放工具,支持两点放大缩小
		webView.getSettings().setBuiltInZoomControls(true);

		// 可以点击放大缩小
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		// webView.setInitialScale(180);//缩放等级为75%
		webView.loadUrl(url);
	}

	/**
	 * Web视图
	 * 
	 * @author admin
	 * 
	 */
	class WebViewContentClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// dialog.dismiss();
			isOver = true;
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
			client.get(Constant.JSON_DAY + cityId, new AsyncHttpResponseHandler() {
				@Override
				public void onStart() {
					UpdateTool.startLoadingAnim(getActivity(), updateDataBtn, updateImageAnimation);
				}

				@Override
				public void onSuccess(String result) {
					json_day = result;
					sp.edit().putString("Json_day", json_day).commit();
					initView();
					studyGraph.postInvalidate();
				}

				@Override
				public void onFailure(Throwable error) {
				}
			});

			client.get(Constant.JSON_MONTH + cityId, new AsyncHttpResponseHandler() {
				@Override
				public void onStart() {

				}

				@Override
				public void onSuccess(String result) {
					// 更新按钮的动画结束旋转
					UpdateTool.stopLoadingAnim(updateDataBtn, updateImageAnimation);
					
					json_month = result;
					sp.edit().putString("Json_month", json_month).commit();
					
					initView();
					
					studyGraph1.postInvalidate();
				}

				@Override
				public void onFailure(Throwable error) {
				}
			});

			UpdateTool.stopLoadingAnim(updateDataBtn, updateImageAnimation);

			final File file = new File(rootPath + "/spread.html");
			file.delete();
			DataTool.getForcastData();

			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						if (file.exists()) {
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
		} else {
			json_day = sp.getString("Json_day", "");
			initView();
			studyGraph.postInvalidate();
			json_month = sp.getString("Json_month", "");
			initView();
			studyGraph1.postInvalidate();
			Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 自动更新
	 */
	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.share_image:

			if (NetworkTool.isNetworkConnected(getActivity())) {
				String filePath = DataTool.createFileDir("Share_Imgs");
				String tmpTime = String.valueOf(System.currentTimeMillis());
				String path = filePath + "/" + getResources().getString(R.string.app_name) + "_空气质量趋势_" + tmpTime
						+ ".png";
				ShareTool.shoot(path, getActivity());
				ShareTool.SharePhoto(path, "绿朵分享", cityName + "空气质量趋势图。下载地址：http://app.lvdora.com", getActivity());
			} else {
				Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.update_graph:
			OnFlush();
			initView();
			break;
		default:
			break;
		}
	}

	/**
	 * 设置标题 根据SharedPreference中的cityId
	 * 
	 * @return
	 */
	private void setTitle() {
		// 取出城市id
		sp = getActivity().getSharedPreferences("cur_city", 0);
		cityId = sp.getInt("city_id", 18);
		// 查询城市名称
		CityAqiDao cityAqiDB = new CityAqiDao(this.getActivity(), "");
		CityAqi locationCity = cityAqiDB.getItem(cityId);
		cityName = locationCity.getCityName();

		TextView city = (TextView) hazeForecastView.findViewById(R.id.devices_title);
		city.setText(cityString + cityName);
	}

	private void findView() {

		hazeSpreadForecast = (WebView) hazeForecastView.findViewById(R.id.haze_spread_forcast);
		shareImageBtn = (ImageButton) hazeForecastView.findViewById(R.id.share_image);
		shareImageBtn.setOnClickListener(this);
		updateDataBtn = (ImageButton) hazeForecastView.findViewById(R.id.update_graph);
		updateDataBtn.setOnClickListener(this);
		updateImageAnimation = (ImageView) hazeForecastView.findViewById(R.id.update_graph_animation);

		max = (TextView) hazeForecastView.findViewById(R.id.max);
		min = (TextView) hazeForecastView.findViewById(R.id.min);
		m1 = (TextView) hazeForecastView.findViewById(R.id.m1);
		m2 = (TextView) hazeForecastView.findViewById(R.id.m2);
		max1 = (TextView) hazeForecastView.findViewById(R.id.max1);
		min1 = (TextView) hazeForecastView.findViewById(R.id.min1);
		m_1 = (TextView) hazeForecastView.findViewById(R.id.m_1);
		m_2 = (TextView) hazeForecastView.findViewById(R.id.m_2);

		studyGraphLayout = (MyHorizontalScrollView) hazeForecastView.findViewById(R.id.study_graph_layout);
		studyGraph = (StudyGraphView) hazeForecastView.findViewById(R.id.study_graph);
		studyGraphLayout1 = (MyHorizontalScrollView1) hazeForecastView.findViewById(R.id.study_graph_layout1);
		studyGraph1 = (StudyGraphView1) hazeForecastView.findViewById(R.id.study_graph1);
	}
}
