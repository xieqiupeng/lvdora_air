package com.lvdora.aqi.util;

import com.lvdora.aqi.module.ModuleActivitiesManager;

/**
 * activity退出方案
 * 
 * @author xqp
 */
public class ExitTool {

	public static void exit(){
		ModuleActivitiesManager.getActivitiesStack().exit();
	}
}
