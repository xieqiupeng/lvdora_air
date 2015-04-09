package com.lvdora.aqi.module;

import java.util.ArrayList;
import java.util.List;

import com.lvdora.aqi.R;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 解决问题：给任意一个activity添加页面白点指示器
 * 
 * @1
 * @2
 * @author xqp
 * 
 */
public class ModuleDotIndicator {

	/**
	 * @1  当前白点的位置
	 * @2  当前选中的page页
	 * @3  当前城市的order
	 */
	public static int CURRENT_DOT_POSITION;
	// 所在activity
	private Activity activity;
	// 指示器布局
	private LinearLayout indicatorLayout;
	// 指示器白点列表
	private List<ImageView> indicators;
	// 单例页面指示器
	private static ModuleDotIndicator instance;

	// 单例
	private ModuleDotIndicator(Activity activity, LinearLayout indicatorLayout) {
		this.activity = activity;
		this.indicatorLayout = indicatorLayout;
		this.indicators = new ArrayList<ImageView>();
		CURRENT_DOT_POSITION = 0;
	}

	/**
	 * 
	 * @param activity
	 * @param indicatorLayout
	 *            所在布局
	 * @return
	 */
	public static ModuleDotIndicator getInstance(Activity activity, LinearLayout indicatorLayout) {
		// 初次构造
		if (instance == null) {
			instance = new ModuleDotIndicator(activity, indicatorLayout);
		}
		Log.v("ModuleDotIndicator", "getInstance " + instance.toString());
		// 返回实例
		return instance;
	}

	// 添加指示器
	public void addIndicator(int citySize) {
		// 清空
		if (indicatorLayout.getChildCount() > 0) {
			indicatorLayout.removeAllViews();
		}
		// 添加指示器
		for (int i = 0; i < citySize; i++) {
			ImageView imageView = (ImageView) View.inflate(activity, R.layout.dot_imageview, null);
			indicators.add(i, imageView);
			// 默认为false，加不加都行
			((ImageView) indicators.get(i)).setEnabled(false);
			indicatorLayout.addView(imageView, i);
		}
		((ImageView) indicators.get(0)).setEnabled(true);
	}

	//
	public void updateIndicator() {
		for (ImageView dot : indicators) {
			dot.setEnabled(false);
		}
		((ImageView) indicators.get(CURRENT_DOT_POSITION)).setEnabled(true);
	}
}