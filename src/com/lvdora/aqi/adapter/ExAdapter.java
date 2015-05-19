package com.lvdora.aqi.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.lvdora.aqi.R;
import com.lvdora.aqi.dao.DeviceDao;
import com.lvdora.aqi.model.Device;
import com.lvdora.aqi.util.GradeTool;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExAdapter extends BaseExpandableListAdapter {

	private Activity activity;

	// {countyId:countyname}
	private List<String> groupList = new ArrayList<String>();
	// {{devid:Device}}
	private Map<Integer, List<Device>> childMap = new TreeMap<Integer, List<Device>>();

	static class ViewHolder {
		public TextView countyName;
		public TextView nickname;
		public TextView showAqiValue;
		public TextView showPm25Value;
	}

	public ExAdapter(Activity activity, List<String> groupList, String cityId, String style) {
		this.activity = activity;
		this.groupList = groupList;
		for (int i = 0; i < groupList.size(); i++) {
			// 添加单个子菜单数据
			DeviceDao deviceDao = new DeviceDao(activity);
			List<Device> list = deviceDao.selectChildsInOneGroup(groupList.get(i), cityId, style);
			childMap.put(i, list);
		}
		Log.w("Exadaper", childMap.toString());
	}

	//
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.member_listview, null);
		}

		TextView title = (TextView) convertView.findViewById(R.id.content_001);
		String countyName = getGroup(groupPosition);
		title.setText(countyName);

		// 图标变化
		ImageView image = (ImageView) convertView.findViewById(R.id.tubiao);
		if (isExpanded) {
			image.setBackgroundResource(R.drawable.btn_browser2);
		} else {
			image.setBackgroundResource(R.drawable.btn_browser);
		}
		return convertView;
	}

	//
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.member_childitem, null);
		}
		Log.w("ExAdapter", convertView.toString());
		// 隔行变色
		if (childPosition % 2 == 0) {
			// 0xfff3f3f3
			convertView.setBackgroundColor(Color.parseColor("#F0F6EC"));
		} else {
			convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
		}

		// 设置文字和样式
		Device device = getChild(groupPosition, childPosition);

		TextView address = (TextView) convertView.findViewById(R.id.address);
		TextView nickname = (TextView) convertView.findViewById(R.id.device_name);
		TextView showAqiValue = (TextView) convertView.findViewById(R.id.device_aqi_value);
		TextView showPm25Value = (TextView) convertView.findViewById(R.id.device_pm25_value);
		TextView level = (TextView) convertView.findViewById(R.id.device_level);

		address.setText(device.getAddress());
		nickname.setText(device.getNickname());
		showPm25Value.setText(device.getPm25());
		showPm25Value.setTextColor(Color.BLACK);

		//
		if (device.getAddress() != "") {
			address.setVisibility(View.VISIBLE);
		} else if (device.getAddress() == "") {
			address.setVisibility(View.GONE);
		}

		try {
			Integer.parseInt(device.getAqi());
		} catch (Exception e) {
			device.setAqi("499");
		}
		String aqiLevel = GradeTool.getStateByIndex(Integer.parseInt(device.getAqi()));
		level.setTextColor(Color.BLACK);
		level.setText(aqiLevel);
		showAqiValue.setText(device.getAqi());
		int bColor = GradeTool.getAqiColorByIndex(device.getAqi());
		showAqiValue.setBackgroundDrawable(activity.getResources().getDrawable(bColor));
		int tColor = GradeTool.getTextColorByAqi(device.getAqi());
		showAqiValue.setTextColor(activity.getResources().getColor(tColor));

		return convertView;
	}

	@Override
	public String getGroup(int groupPosition) {
		return groupList.get(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public int getGroupCount() {
		return groupList.size();
	}

	@Override
	public Device getChild(int groupPosition, int childPosition) {
		return (Device) childMap.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childMap.get(groupPosition).size();
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}


}