/**************************************************************************
 *              AIRM2M Copyright (c) ATWINCom Ltd.
 * AIRM2M公司介绍:
 *
 *      技术支持群:201848376
 *
 *      淘宝店:http://shop114465275.taobao.com/?spm=a230r.7195193.1997079397.2.PGc7tg
 *
 *      官方网站: www.airm2m.com
 *
 *      BBS: bbs.airm2m.com
 *
 * Author: 李炜镪
 * Verison: V0.1
 * Date: 2014.11.5
 *
 * File Description:
 *
 *  airm2m smartlink 接口定义 FOR ANDROID
 *  
 **************************************************************************/
package com.airm2m.sdk;

public class smartlink {
	
	public final int AIRM2M_SMARTLINKJNI_ERR_NONE = 0;
	public final int AIRM2M_SMARTLINKJNI_ERR_PARAM = -1; // 参数错误 比如SSID是空的
	public final int AIRM2M_SMARTLINKJNI_ERR_BUSY = -2;// 当前已在进行SMARTLINK 请先停止
	public final int AIRM2M_SMARTLINKJNI_ERR_SYS = -3;// 系统错误,比如创建线程失败
	
	/*******************************************************************************
	 *
	 * Function:    smartlink.open
	 *
	 * Parameters:  
	 *    1.ssid       需要配置的ssid
	 *    2.password   需要配置的password
	 *
	 * Returns:     错误信息,见上方错误值定义
	 *
	 * Description: 启动smartlink配置功能
	 *
	 ******************************************************************************/
	public native int open(String ssid, String password);

	/*******************************************************************************
	 *
	 * Function:    smartlink.close
	 *
	 * Parameters:  
	 *
	 * Returns:     
	 *
	 * Description: 停止smartlink配置功能
	 *
	 ******************************************************************************/
	public native void close();
	
    static {
        System.loadLibrary("airm2m-smartlink-jni");
    }
}
