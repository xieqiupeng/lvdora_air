package com.lvdora.aqi.module;

import java.util.Stack;
import android.app.Activity;
import android.util.Log;

/**
 * 解决问题：退出所有activity
 * 
 * @1 在每个界面create的时候加载到栈
 * @2 退出时循环退栈，实现完美退出
 * @author xqp
 * 
 */
public class ModuleActivitiesManager extends Stack<Activity> {

	private static final long serialVersionUID = 3853691319036517915L;

	private static ModuleActivitiesManager instance;

	private ModuleActivitiesManager() {

	}

	/*
	 * Singleton获取实例方法
	 */
	public static ModuleActivitiesManager getActivitiesStack() {
		if (instance == null) {
			instance = new ModuleActivitiesManager();
		}
		Log.v("ActivitiesManager", instance.toString());
		return instance;
	}

	/*
	 * 回收堆栈中指定的activity
	 */
	public void popSingleActivity(Activity activity) {
		if (activity != null) {
			// 关闭
			activity.finish();
			// 去除
			instance.remove(activity);
			activity = null;
		}
	}

	/*
	 * 退出调用方法
	 */
	public void exit() {
		if (instance == null) {
			return;
		}
		for (Activity activity : instance) {
			activity.finish();
		}
		System.exit(0);
	}

	/*
	 * 获取堆栈的栈顶activity
	 */
	public Activity currentActivity() {
		Activity activity = null;
		try {
			if (!instance.isEmpty()) {
				activity = instance.pop();
			}
			return activity;
		} catch (Exception ex) {
			Log.v("ModuleActivitiesManager", "currentActivity " + ex.getMessage());
			return activity;
		} finally {
			activity = null;
		}
	}
}