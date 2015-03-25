package com.lvdora.aqi.adapter;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import com.lvdora.aqi.R;
import com.lvdora.aqi.model.Province;

public class CitySelectorAdapter implements ExpandableListAdapter {

	private Context context;
	private List<Province> provinces;

	public CitySelectorAdapter(Context context, List<Province> provinces) {
		this.context = context;
		this.provinces = provinces;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {

	}

	@Override
	public int getGroupCount() {
		return this.provinces.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return ((Province) this.provinces.get(groupPosition)).getCitys().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.provinces.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return ((Province) this.provinces.get(groupPosition)).getCitys().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		//设置城市
		if (convertView == null) {
			convertView = View.inflate(this.context, R.layout.choose_city_item, null);
		}
		((TextView) convertView.findViewById(R.id.tv_city_name)).setText(((Province) this.provinces.get(groupPosition))
				.getName());
		return convertView;

	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(this.context, R.layout.choose_city_item, null);
		}
		TextView localTextView = (TextView) convertView.findViewById(R.id.tv_city_name);
		//设置名称
		String str = ((Province) this.provinces.get(groupPosition)).getCitys().get(childPosition).getName();
		localTextView.setText(str);
		localTextView.setTextColor(-1);
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean areAllItemsEnabled() {

		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void onGroupExpanded(int groupPosition) {

	}

	@Override
	public void onGroupCollapsed(int groupPosition) {

	}

	@Override
	public long getCombinedChildId(long groupId, long childId) {
		return 0;
	}

	@Override
	public long getCombinedGroupId(long groupId) {
		return 0;
	}

}
