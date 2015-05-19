package com.lvdora.aqi.util;


public class Constant {
	//http://www.greendora.com
	public static final String MAIN_URL="http://www.lvdora.com";

	//public static final String SERVER_URL = "http://www.greendora.com/index.php/Mobile/getCityDetail/cityId/";
	public static final String SERVER_URL = MAIN_URL+"/index.php/Mobile/getCityDetail/cityId/";
	public static final String AQIS_URL = MAIN_URL+"/index.php/Mobile/getCitiesDetail/cityId/";
	
	public static final String JSON_SERVER = MAIN_URL+"/index.php/Mobile/computedAqi";
	public static final String JSON_DAY = MAIN_URL+"/index.php?s=/Mobile/getAqiKlineData/type/hours/amount/30/cityid/";
	public static final String JSON_MONTH = MAIN_URL+"/index.php?s=/Mobile/getAqiKlineData/type/day/amount/30/citys/";
	//public static final String JSON_SERVER = "http://www.greendora.com/index.php/Mobile/computedAqi";

	//public static final String JSON_SERVER = "http://www.greendora.com/a.txt";

	public static final String ABOUT_URL = MAIN_URL+"/Download/About/about.html";
	public static final String ABOUT_CODE = MAIN_URL+"/Download/About/code.jpg";
	public static final String ABOUT_LOGO = MAIN_URL+"/Download/About/ic_launcher.png";
	public static final String DEVICE_URL = MAIN_URL+"/index.php/Mobile/deviceJson";
	public static final String DEVICE_JAVA_URL = MAIN_URL+"/index.php?s=/Interface/getStationDataByCityId/user/java/cityid/";
	
	public static final String VERSION_URL = MAIN_URL+"/index.php/Mobile/varJson";
	//public static final String VERSION_URL = "http://www.greendora.com/index.php/Mobile/varJson";
	//public static final String SITE_URL = MAIN_URL+"/index.php/Mobile/getSpot/cityid/";
	//public static final String SITE_URL = MAIN_URL+"/index.php/Mobile/getSpot/cityid/";
	public static final String SITE_URL = MAIN_URL+"/index.php/Mobile/getSpot/cityid/";
	public static final String MAP_SITE_URL = MAIN_URL+"/index.php/Mobile/getSpotAll";

	//public static final String BAIDU_KEY = "EE9133d8bb193271193e67655e36fab3"; // 邓
	//public static final String BAIDU_KEY = "kXvjtsxssVZGVA37Td8a4uaf"; //孙
	//public static final String BAIDU_KEY = "kXvjtsxssVZGVA37Td8a4uaf"; //曹
	//public static final String DOWNLOAD_URL = "http://www.greendora.com/Download/Apk/aqi_android.apk";
    public static final String DOWNLOAD_URL = "http://app.lvdora.com/download/lvaqi.apk";
    public static final String HAZESPREADFORECAST_URL = MAIN_URL+"/index.php/Mobile/forewarning";
	//public static final String HAZEHOURSFORECAT_URL = MAIN_URL+"/index.php/Mobile/haze";
	//public static final String HAZESPREADFORECAST_URL = "http://www.greendora.com/index.php/wechat/forewarning";
    public static final long UPDATE_TIME = 30*60*1000;
    public static final String SPIT_URL = MAIN_URL+"/index.php/mobile/commentList";


}
