package com.lvdora.aqi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lvdora.aqi.R;
import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.model.SiteAqi;
import com.lvdora.aqi.util.GradeTool;

public class SiteAdapter extends BaseAdapter {

	private Context context;
	private List<SiteAqi> siteAqis;
	private LayoutInflater inflater;

	public SiteAdapter(Context context, List<SiteAqi> siteAqis) {
		this.context = context;
		this.siteAqis = siteAqis;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return this.siteAqis.size() + 1;
	}

	@Override
	public Object getItem(int position) {

		return siteAqis.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		SiteAqi siteAqi = siteAqis.get(position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.site_activity_item, null);
			
			holder.mTvSite = ((TextView) convertView.findViewById(R.id.tv_site));
			holder.mTvAQI = (TextView) convertView.findViewById(R.id.tv_AQI);
			holder.mTvPM = (TextView) convertView.findViewById(R.id.tv_PM25);
			holder.mTvQuality = (TextView) convertView
					.findViewById(R.id.tv_quality);
			
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}

		holder.mTvSite.setText(siteAqi.getName());
		holder.mTvAQI.setText(siteAqi.getAqi());
		holder.mTvPM.setText(siteAqi.getPm25());
		Log.d("lvdora", siteAqi.getName()+siteAqi.getPm25());
		holder.mTvQuality.setText(GradeTool.getStateByIndex(Integer
				.parseInt(siteAqi.getAqi())));
		holder.mTvQuality.setBackgroundDrawable(context.getResources().getDrawable(
				GradeTool.getAqiColorByIndex(siteAqi.getAqi())));
		holder.mTvQuality.setTextColor(context.getResources().getColor(
				GradeTool.getTextColorByAqi(siteAqi.getAqi())));
		
		return convertView;
	}

	static class ViewHolder {
		TextView mTvSite;
		TextView mTvAQI;
		TextView mTvPM;
		TextView mTvQuality;

	}

}
