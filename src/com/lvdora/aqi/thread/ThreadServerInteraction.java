package com.lvdora.aqi.thread;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.aqi.model.City;
import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.util.AsyncHttpClient;
import com.lvdora.aqi.util.AsyncHttpResponseHandler;
import com.lvdora.aqi.util.Constant;
import com.lvdora.aqi.util.DataTool;

/**
 * 服务器交互类
 * 
 * @1 根据城市id和order得到aqi数据
 * @2 存到数据库
 */
public class ThreadServerInteraction {

	private Context context;
	private List<CityAqi> Aqis;
	// 请求aqi数量
	private int requestNum = 1;

	public ThreadServerInteraction(Context context) {
		this.context = context;
		Aqis = new ArrayList<CityAqi>();
	}

	/**
	 * 多条请求
	 * 
	 * @param citys
	 */
	public void sendRequestForAqis(List<City> citys) {
		requestNum = citys.size();
		String citysID = "";
		String citysOrder = "";
		for (City city : citys) {
			citysID += city.getId() + "|";
			citysOrder += city.getOrder() + "|";
		}
		citysID = citysID.substring(0, citysID.length() - 1);
		citysOrder = citysOrder.substring(0, citysOrder.length() - 1);
		Log.v("ModuleServerInteraction", citysID + " " + citysOrder);
		sendRequestForAqis(citysOrder, citysID);
	}

	/**
	 * 多条请求
	 */
	public void sendRequestForAqis(final String citysOrder, final String citysID) {
		Log.d("ModuleServerInteraction", citysID + citysOrder + " " + Aqis.toString() + " " + Aqis.size() + " "
				+ requestNum);
		String param = Constant.AQIS_URL + citysID;
		// java转义
		final String[] cityIDArr = citysID.split("\\|");
		final String[] cityOrderArr = citysOrder.split("\\|");
		AsyncHttpClient client = new AsyncHttpClient();
		// url转义
		param = param.replace("|", "%7C");
		client.get(param, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				Log.v("ModuleServerIneteraction", "ServerSendBack " + Aqis.size() + " " + Aqis.toString() + " "
						+ result);
				JSONObject obj;
				try {
					obj = new JSONObject(result);
					for (int i = 0; i < cityIDArr.length; i++) {
						String singleResult = obj.getJSONObject("num" + i).toString();
						// 取得添加城市及信息
						CityAqi cityAqi = DataTool.getCityAqi(singleResult, Integer.parseInt(cityIDArr[i]),
								Integer.parseInt(cityOrderArr[i]));
						Aqis.add(cityAqi);
					}
				} catch (JSONException e) {

				}
				Log.d("ModuleServerIneteraction", "pageCallBack " + Aqis.toString());
				callBackLogo();
			}
		});
	}

	/**
	 * 单条请求
	 * 
	 * @param city
	 */
	public void sendRequestForAqi(City city) {
		sendRequestForAqi(city.getOrder(), city.getId());
	}

	/**
	 * 单条请求
	 */
	public void sendRequestForAqi(final int cityOrder, final int cityID) {
		Log.d("ModuleServerIneteraction", cityID + " " + Aqis.toString() + Aqis.size());
		String param = Constant.SERVER_URL + cityID;
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(param, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				Log.w("ModuleServerIneteraction", "ServerSendBack " + result);
				// 得到单条城市天气信息
				CityAqi cityAqi = DataTool.getCityAqi(result, cityID, cityOrder);
				callBackHome();
			}
		});
	}

	// 回调界面函数
	public void callBackLogo() {
		CityAqiDao cityAqiDao = new CityAqiDao(context, "");
		cityAqiDao.insertCityAqiList(Aqis);
		Log.d("ModuleServerIneteraction", "" + cityAqiDao.getCount());
	}

	// 回调界面函数
	public void callBackHome() {
		Log.d("ModuleServerIneteraction", "pageCallBack " + Aqis.toString());

	}
}
