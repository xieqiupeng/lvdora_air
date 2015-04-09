package com.lvdora.aqi.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.lvdora.aqi.R;
import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.aqi.model.City;
import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.model.CitysIndexMap;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.EnAndDecryption;
import com.lvdora.aqi.util.GradeTool;
import com.lvdora.aqi.view.HomeActivity;

/**
 * 城市管理界面修改
 * 
 * @author xqp
 */
public class CitySettingAdaper extends BaseAdapter {

	// 所有选中城市list
	private ArrayList<City> citys;

	// cityaqi全表数据
	private List<CityAqi> cityAqis;
	private Context context;
	private int flag = 0;
	private SharedPreferences sp;
	private CityAqiDao cityAqiDao;

	public CitySettingAdaper(ArrayList<City> citys, Context context) {
		this.citys = citys;
		this.context = context;
		sp = context.getSharedPreferences("citydata", 0);
	}

	@Override
	public int getCount() {
		if (this.flag == 0)
			return 1 + this.citys.size();
		return citys.size();
	}

	@Override
	public Object getItem(int position) {
		return Integer.valueOf(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// 在初始化的时候，这个方法会加载(citys.size+2)遍。
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Log.d("lvdora", "position:" + position);
		// 增加按钮
		if (position == this.citys.size()) {
			View localView = View.inflate(this.context, R.layout.setting_city_item_add, null);
			return localView;
		}
		// 取得list：全部城市AQI数据
		cityAqiDao = new CityAqiDao(context, "");
		cityAqis = cityAqiDao.getAll();
		Log.i("AdaperInit", cityAqis.size() + "  " + cityAqis.toString());
		// 取得城市单体的view
		View localView = View.inflate(this.context, R.layout.setting_city_item, null);
		// 显示天气信息
		showWeather(localView, position);
		// 点击编辑按钮，删除城市，重绘界面
		deleteCity(localView, position);
		return localView;
	}

	/**
	 * 显示天气信息
	 * 
	 * @param localView
	 * @param position
	 */
	public void showWeather(View localView, int position) {
		TextView cityNameText = (TextView) localView.findViewById(R.id.tv_city_name);
		TextView tempText = (TextView) localView.findViewById(R.id.tv_temp);
		TextView stateText = (TextView) localView.findViewById(R.id.tv_state);
		ImageView weatherIconImg = (ImageView) localView.findViewById(R.id.iv_weather_icon);
		ImageView locate = (ImageView) localView.findViewById(R.id.locate);

		CityAqi cityAqi = cityAqis.get(position);
		// 名称
		cityNameText.setText(cityAqi.getCityName());
		// 温度
		tempText.setText(cityAqi.getTemp_now() + "℃");
		// 状态
		stateText.setText(GradeTool.getStateByIndex(Integer.parseInt(cityAqi.getAqi())));
		stateText.setBackgroundDrawable(context.getResources().getDrawable(
				GradeTool.getAqiColorByIndex(cityAqi.getAqi())));
		stateText.setTextColor(context.getResources().getColor(GradeTool.getTextColorByAqi(cityAqi.getAqi())));
		// 图标
		weatherIconImg.setBackgroundDrawable(context.getResources().getDrawable(
				GradeTool.getWeatherIcon(cityAqi.getWeather_icon0())));
		// 定位图标
		if (position == 0) {
			locate.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 删除城市功能
	 * 
	 * @param localView
	 * @param position
	 */
	public void deleteCity(View localView, final int position) {
		// 取得当前城市与当前城市aqi
		final City curCity = citys.get(position);
		final CityAqi curCityAqi = cityAqis.get(position);
		// 可编辑时
		if (flag == 1) {
			// 删除图标动作
			ImageButton deleteImage = (ImageButton) localView.findViewById(R.id.ib_city_delete);
			if (position != 0) {
				deleteImage.setVisibility(View.VISIBLE);
			}
			//
			deleteImage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 删除当前城市与aqi信息
					citys.remove(curCity);
					cityAqis.remove(curCityAqi);
					CitysIndexMap.getInstance((Activity) context).remove(position);
					// 删除
					cityAqiDao.delByCityID(curCity.getId());

					// 重排
					CitysIndexMap.getInstance((Activity) context).reorder();
					// 序号重排
					List<CityAqi> cityAqis = cityAqiDao.getAll();
					for (CityAqi cityAqi : cityAqis) {
						int orderOld = cityAqi.getOrder();
						int orderNew = CitysIndexMap.getInstance((Activity) context).getKeyByValue(cityAqi.getCityId());
						cityAqiDao.updateOrderByOrder(orderOld, orderNew);
					}
					// 去掉不合理的数字
					cityAqiDao.delByOrder(-1);

					// 存sp
					CitysIndexMap.getInstance((Activity) context).listToSP();

					// 界面当前页置为0
					HomeActivity.currentIndexOut = 0;
					try {
						// 等待0.1秒
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					// TODO sp中添加城市
					String cityString = EnAndDecryption.CityList2String(citys);
					sp.edit().putString("citys", cityString).commit();
					notifyDataSetChanged();
				}
			});
		}
	}

	public void setCitys(ArrayList<City> citys) {
		this.citys = citys;
	}

	public void setFlag(int position) {
		this.flag = position;
	}
}