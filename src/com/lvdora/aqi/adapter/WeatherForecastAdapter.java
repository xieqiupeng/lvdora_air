package com.lvdora.aqi.adapter;

import java.util.List;
import java.util.Map;

import com.lvdora.aqi.R;
import com.lvdora.aqi.adapter.DeviceAdapter.ViewHolder;
import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.util.DateUtil;
import com.lvdora.aqi.util.GradeTool;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherForecastAdapter extends BaseAdapter {
	private CityAqi cityAqi;
	private Context context;
	private LayoutInflater mInflater = null;

	public WeatherForecastAdapter(CityAqi cityAqi, Context context) {
		this.cityAqi = cityAqi;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return 5;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		// 如果缓存convertView为空，则需要创建View
		if (convertView == null) {
			holder = new ViewHolder();
			// 根据自定义的Item布局加载布局
			convertView = mInflater.inflate(R.layout.weather_detail_item, null);
			holder.dateIndex = (TextView) convertView
					.findViewById(R.id.data_index);
			holder.weatherIcon = (ImageView) convertView
					.findViewById(R.id.weather_img);
			holder.weatherDetail = (TextView) convertView
					.findViewById(R.id.weather_detail);
			holder.temp = (TextView) convertView
					.findViewById(R.id.weather_temp);
			holder.windForce = (TextView) convertView.findViewById(R.id.weather_windforce);

			// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
        if(position == 0){
    		holder.dateIndex.setText("今天");
    		holder.weatherIcon.setBackgroundDrawable(convertView.getResources().getDrawable(GradeTool.getWeatherIcon(cityAqi.getWeather_icon0())));
    		holder.weatherDetail.setText(cityAqi.getWeather0().indexOf('，') > 0 ? cityAqi.getWeather0().substring
    				(0, cityAqi.getWeather0().indexOf('，')) : cityAqi.getWeather0());
    		holder.temp.setText(cityAqi.getLowTemp0() + "~"
				+ cityAqi.getHightTemp0() + "℃");
    		holder.windForce.setText(cityAqi.getWindForce0().indexOf('，') > 0 ? cityAqi.getWindForce0().substring
    				(0,cityAqi.getWindForce0().indexOf('，')): cityAqi.getWindForce0());

        }
        if(position == 1){
    		holder.dateIndex.setText(DateUtil.getTomorrowWeek());
    		holder.weatherIcon.setBackgroundDrawable(convertView.getResources().getDrawable(GradeTool.getWeatherIcon(cityAqi.getWeather_icon1())));
    		holder.weatherDetail.setText(cityAqi.getWeather1().indexOf('，') > 0 ? cityAqi.getWeather1().substring
    				(0, cityAqi.getWeather1().indexOf('，')) : cityAqi.getWeather1());
    		holder.temp.setText(cityAqi.getLowTemp1() + "~"
				+ cityAqi.getHightTemp1() + "℃");
    		holder.windForce.setText(cityAqi.getWindForce1().indexOf('，') > 0 ? cityAqi.getWindForce1().substring
    				(0,cityAqi.getWindForce1().indexOf('，')): cityAqi.getWindForce1());
        }
        if(position == 2){
    		holder.dateIndex.setText(DateUtil.getSecTomorrowWeek());
    		holder.weatherIcon.setBackgroundDrawable(convertView.getResources().getDrawable(GradeTool.getWeatherIcon(cityAqi.getWeather_icon2())));
    		holder.weatherDetail.setText(cityAqi.getWeather2().indexOf('，') > 0 ? cityAqi.getWeather2().substring
    				(0, cityAqi.getWeather2().indexOf('，')) : cityAqi.getWeather2());
    		holder.temp.setText(cityAqi.getLowTemp2() + "~"
				+ cityAqi.getHightTemp2() + "℃");
    		holder.windForce.setText(cityAqi.getWindForce2().indexOf('，') > 0 ? cityAqi.getWindForce2().substring
    				(0,cityAqi.getWindForce2().indexOf('，')): cityAqi.getWindForce2());
        }
        if(position == 3){
    		holder.dateIndex.setText(DateUtil.getThirdTomorrowWeek());
    		holder.weatherIcon.setBackgroundDrawable(convertView.getResources().getDrawable(GradeTool.getWeatherIcon(cityAqi.getWeather_icon3())));
    		holder.weatherDetail.setText(cityAqi.getWeather3().indexOf('，') > 0 ? cityAqi.getWeather3().substring
    				(0, cityAqi.getWeather3().indexOf('，')) : cityAqi.getWeather3());
    		holder.temp.setText(cityAqi.getLowTemp3() + "~"
				+ cityAqi.getHightTemp3() + "℃");
    		holder.windForce.setText(cityAqi.getWindForce3().indexOf('，') > 0 ? cityAqi.getWindForce3().substring
    				(0,cityAqi.getWindForce3().indexOf('，')): cityAqi.getWindForce3());
        }if(position == 4){
    		holder.dateIndex.setText(DateUtil.getFourthTomorrowWeek());
    		holder.weatherIcon.setBackgroundDrawable(convertView.getResources().getDrawable(GradeTool.getWeatherIcon(cityAqi.getWeather_icon4())));
    		holder.weatherDetail.setText(cityAqi.getWeather4().indexOf('，') > 0 ? cityAqi.getWeather4().substring
    				(0, cityAqi.getWeather4().indexOf('，')) : cityAqi.getWeather4());
    		holder.temp.setText(cityAqi.getLowTemp4() + "~"
				+ cityAqi.getHightTemp4() + "℃");
    		holder.windForce.setText(cityAqi.getWindForce4().indexOf('，') > 0 ? cityAqi.getWindForce4().substring
    				(0,cityAqi.getWindForce4().indexOf('，')): cityAqi.getWindForce4());
        }
		return convertView;
	}

	static class ViewHolder {
		public TextView dateIndex;
		public ImageView weatherIcon;
		public TextView weatherDetail;
		public TextView temp;
		public TextView windForce;

	}
}
