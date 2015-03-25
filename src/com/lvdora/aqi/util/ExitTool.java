package com.lvdora.aqi.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

/**
 * activity退出方案
 * 
 * @author xqp
 */
public class ExitTool {
	
	public static List<Activity> activityList = new ArrayList<Activity>();
	
	public static void addActivity(Activity activity){
		activityList.add(activity);
	}
	
	public static void exit(){
		
		for(Activity activity:activityList){
			activity.finish();
		}
		ScreenManager.getScreenManager().popAllActivity();
		System.exit(0);
	}
}
