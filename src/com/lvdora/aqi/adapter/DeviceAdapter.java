package com.lvdora.aqi.adapter;

import java.util.List;
import java.util.Map;

import com.lvdora.aqi.R;
import com.lvdora.aqi.util.GradeTool;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter {
	private List<Map<String, Object>> devices;
	private Context context;
	private LayoutInflater mInflater = null;

	public DeviceAdapter(List<Map<String, Object>> devices, Context context) {
		this.devices = devices;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return devices.size();
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
			convertView = mInflater.inflate(R.layout.mydevice_share_item, null);
			holder.nickname = (TextView) convertView
					.findViewById(R.id.device_name);
			holder.showAqiValue = (TextView) convertView
					.findViewById(R.id.device_aqi_value);
			holder.showPm25Value = (TextView) convertView
					.findViewById(R.id.device_pm25_value);
			holder.address = (TextView) convertView
					.findViewById(R.id.device_location);

			// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		//隔行变色
		if(position % 2 == 0){
			convertView.setBackgroundColor(Color.WHITE);
		}else{
			convertView.setBackgroundColor(0xfff3f3f3);
		}

		holder.nickname.setText(devices.get(position).get("nickname")
				.toString());
		holder.address.setText((String) devices.get(position).get("address"));
		holder.showAqiValue.setText((String) devices.get(position).get("aqi"));
		holder.showPm25Value.setText(devices.get(position).get("pm2.5")
				.toString());

		holder.showAqiValue.setBackgroundDrawable(context.getResources()
				.getDrawable(
						GradeTool.getAqiColorByIndex((String) devices.get(
								position).get("aqi"))));
		holder.showAqiValue.setTextColor(context.getResources().getColor(
				GradeTool.getTextColorByAqi((String) devices.get(
						position).get("aqi"))));
		return convertView;
	}

	static class ViewHolder {
		public TextView nickname;
		public TextView address;
		public TextView showAqiValue;
		public TextView showPm25Value;

	}
}
