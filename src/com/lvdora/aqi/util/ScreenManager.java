package com.lvdora.aqi.util;
import java.util.Stack;
import android.app.Activity;
public class ScreenManager {
	 private static Stack<Activity> activityStack;
	 private static ScreenManager instance;
    /**
     * 视图管理器，用于完全退出
     * Singleton方式的实例
     * @return
     */
    public static ScreenManager getScreenManager() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }
 
    /**
     * 回收堆栈中指定的activity
     * 
     * @param activity
     */
    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }
 
    /**
     * 获取堆栈的栈顶activity
     * 
     * @return 栈顶activity
     */
    private Activity currentActivity() {
        Activity activity = null;
        try {
            if (!activityStack.isEmpty()) {
                activity = activityStack.pop();
            }
            return activity;
        } catch (Exception ex) {
            System.out.println("ScreenManager:currentActivity---->"
                    + ex.getMessage());
            return activity;
        } finally {
            activity = null;
        }
    }
 
    /**
     * 将activity压入堆栈
     * 
     * @param activity 需要压入堆栈的activity
     */
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.push(activity);
    }
 
    /**
     * 回收堆栈中所有Activity
     */
    public void popAllActivity() {
        Activity activity = null;
        try {
            while (!activityStack.isEmpty()) {
                activity = currentActivity();
                if (activity != null) {
                    popActivity(activity);
                }
            }
        } finally {
            activity = null;
        }
    }
    /**
     * 构造函数
     */
    private ScreenManager() {
 
    }
}