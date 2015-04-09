package com.lvdora.aqi.thread;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.lvdora.aqi.R;
import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.aqi.model.City;
import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.util.AsyncHttpClient;
import com.lvdora.aqi.util.AsyncHttpResponseHandler;
import com.lvdora.aqi.util.Constant;
import com.lvdora.aqi.util.DataTool;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ThreadServerAqisToDB extends AsyncTask<List<City>, Integer, List<CityAqi>> {

	private ProgressDialog pDialog;
	private Context context;
	// 请求aqi数量
	private int requestNum = 1;
	private List<CityAqi> Aqis;

	public ThreadServerAqisToDB(Context context) {
	}

	@Override
	protected void onPreExecute() {
		pDialog = ProgressDialog.show(context, "", context.getResources().getString(R.string.getting_data));
		pDialog.show();
	}

	@Override
	protected void onPostExecute(List<CityAqi> Aqis) {
		pDialog.dismiss();
		CityAqiDao cityAqiDao = new CityAqiDao(context, "");
		cityAqiDao.insertCityAqiList(Aqis);
	}

	@Override
	protected List<CityAqi> doInBackground(List<City>... citys) {
		sendRequestForAqis(citys[0]);
		return Aqis;
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
		Log.v("ThreadServerAqisToDB", citysID + " " + citysOrder);
		sendRequestForAqis(citysOrder, citysID);
	}

	/**
	 * 多条请求
	 */
	public void sendRequestForAqis(final String citysOrder, final String citysID) {
		Log.d("ThreadServerAqisToDB", citysID + citysOrder + " " + Aqis.toString() + " " + Aqis.size() + " "
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
			}
		});
	}
}
