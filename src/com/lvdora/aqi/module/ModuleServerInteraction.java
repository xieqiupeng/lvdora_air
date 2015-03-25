package com.lvdora.aqi.module;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Binder;
import android.util.Log;

import com.lvdora.aqi.model.City;
import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.util.AsyncHttpClient;
import com.lvdora.aqi.util.AsyncHttpResponseHandler;
import com.lvdora.aqi.util.Constant;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.view.LogoActivity;

/**
 * 服务器交互类
 * 
 * @1 根据城市id和order得到aqi数据 存到数据库
 */
public class ModuleServerInteraction {

	private Activity activity;
	private List<CityAqi> Aqis;
	// 请求aqi数量
	private int requestNum = 1;

	public ModuleServerInteraction(Activity activity) {
		this.activity = activity;
		Aqis = new ArrayList<CityAqi>();
	}

	/*
	 * 多条请求
	 */
	public void sendRequestForAqis(List<City> citys) {
		this.requestNum = citys.size();
		String citysID = "";
		String citysOrder = "";
		for (City city : citys) {
			citysID += city.getId() + "|";
			citysOrder += city.getOrder() + "|";
		}
		citysID = citysID.substring(0, citysID.length() - 1);
		citysOrder = citysOrder.substring(0, citysOrder.length() - 1);
		Log.v("LD", citysID + " " + citysOrder);
		sendRequestForAqis(citysOrder, citysID);
	}

	/**
	 * 与服务器交互
	 * 
	 * @param param
	 */
	public void sendRequestForAqis(final String citysOrder, final String citysID) {
		Log.d("LD", citysID + citysOrder + " " + Aqis.toString() + " " + Aqis.size() + " " + requestNum);
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
				Log.v("ModuleServerIneteraction", "ServerSendBack "+Aqis.size() + "   " + Aqis.toString() + "   " + result);
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
				// 界面回调，默认单条查询
				if (Aqis.size() == requestNum) {
					pageCallBack();
				}
			}
		});
	}

	public void sendRequestForAqi(City city) {
		sendRequestForAqi(city.getOrder(), city.getId());
	}

	/**
	 * 与服务器交互
	 * 
	 * @param param
	 */
	public void sendRequestForAqi(final int cityOrder, final int cityID) {
		Log.d("ModuleServerIneteraction", cityID + " " + Aqis.toString() + Aqis.size());
		String param = Constant.SERVER_URL + cityID;
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(param, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String result) {
				Log.w("ModuleServerIneteraction", "ServerSendBack "+cityID + result);
				// 取得添加城市及信息
				CityAqi cityAqi = DataTool.getCityAqi(result, cityID, cityOrder);
				Aqis.add(cityAqi);
				// 界面回调，默认单条查询
				if (Aqis.size() == requestNum) {
					Log.i("LD", Aqis.size() + "   " + cityID + Aqis.toString());
					pageCallBack();
				}
			}
		});
	}

	/**
	 * 回调界面函数
	 */
	public void pageCallBack() {
		Log.d("ModuleServerIneteraction", "pageCallBack " + Aqis.toString());
		//String name = activity.toString();
		//name = name.substring(0, name.indexOf("y") + 1);
		((LogoActivity) activity).listToDB(Aqis);
	}
}
