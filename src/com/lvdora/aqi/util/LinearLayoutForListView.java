package com.lvdora.aqi.util;

import com.lvdora.aqi.adapter.SiteAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class LinearLayoutForListView extends LinearLayout {

	private SiteAdapter adapter;
	private OnClickListener onClickListener = null;

	/**
	 * 绑定布局
	 */
	public void bindLinearLayout() {
		int count = adapter.getCount();
		
		for (int i = 0; i < count; i++) {
			View view = adapter.getView(i, null, null);
			view.setOnClickListener(this.onClickListener);
			if (i == count - 1) {
				LinearLayout ly = (LinearLayout) view;
				ly.removeViewAt(2);
			}
			addView(view, i);
		}
		Log.v("LLFListView", "" + count);
	}

	public LinearLayoutForListView(Context context) {
		super(context);

	}

	public LinearLayoutForListView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	/**
	 * 获取点击事件
	 * 
	 * @return
	 */
	public OnClickListener getOnclickListener() {
		return onClickListener;
	}

	/**
	 * 设置点击事件
	 * 
	 * @param onClickListener
	 */
	public void setOnclickLinstener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	/**
	 * 获取Adapter
	 * 
	 * @return adapter
	 */
	public SiteAdapter getAdpater() {
		return adapter;
	}

	/**
	 * 设置数据
	 * 
	 * @param adpater
	 */
	public void setAdapter(SiteAdapter adpater) {
		this.adapter = adpater;
		bindLinearLayout();
	}

	
}
