package com.lvdora.aqi.model;

/**
 * 本class用于操作City的AQI数据
 * 
 * @author Eagle
 * 
 */
public class CityAqi {
	
	public static final String ORDER = "num";
	public static final String CITYNAME = "cityName";
	public static final String CITYID = "cityId";
	public static final String AQI = "aqi";
	public static final String PM25 = "pm25";
	public static final String PM25_AQI = "pm25_aqi";
	public static final String PM10 = "pm10";
	public static final String PM10_AQI = "pm10_aqi";
	public static final String SO2 = "so2";
	public static final String SO2_AQI = "so2_aqi";
	public static final String NO2 = "no2";
	public static final String NO2_AQI = "no2_aqi";
	public static final String CO = "co";
	public static final String CO_AQI = "co_aqi";
	public static final String O3 = "o3";
	public static final String O3_AQI = "o3_aqi";
	public static final String AQI_PUBTIME = "aqi_pubtime";
	public static final String USA_AQI = "usa_aqi";
	public static final String USA_PM25 = "usa_pm25";
	public static final String US_PUBTIME = "us_pubtime";
	public static final String TEMP_NOW = "temp_now";
	public static final String WIND = "wind";
	public static final String WEATHER0 = "weather0";
	public static final String WEATHER_ICON0 = "weather_icon0";
	public static final String WEATHER0_PUBTIME = "weather0_pubtime";
	public static final String HIGHTTEMP0 = "hightTemp0";
	public static final String LOWTEMP0 = "lowTemp0";
	public static final String WINDFORCE0 = "windForce0";
	public static final String WEATHER1 = "weather1";
	public static final String WEATHER_ICON1 = "weather_icon1";
	public static final String WEATHER1_PUBTIME = "weather1_pubtime";
	public static final String HIGHTTEMP1 = "hightTemp1";
	public static final String LOWTEMP1 = "lowTemp1";
	public static final String WINDFORCE1 = "windForce1";
	public static final String WEATHER2 = "weather2";
	public static final String WEATHER_ICON2 = "weather_icon2";
	public static final String WEATHER2_PUBTIME = "weather2_pubtime";
	public static final String HIGHTTEMP2 = "hightTemp2";
	public static final String LOWTEMP2 = "lowTemp2";
	public static final String WINDFORCE2 = "windForce2";
	public static final String WEATHER3 = "weather3";
	public static final String WEATHER_ICON3 = "weather_icon3";
	public static final String WEATHER3_PUBTIME = "weather3_pubtime";
	public static final String HIGHTTEMP3 = "hightTemp3";
	public static final String LOWTEMP3 = "lowTemp3";
	public static final String WINDFORCE3 = "windForce3";
	public static final String WEATHER4 = "weather4";
	public static final String WEATHER_ICON4 = "weather_icon4";
	public static final String WEATHER4_PUBTIME = "weather4_pubtime";
	public static final String HIGHTTEMP4 = "hightTemp4";
	public static final String LOWTEMP4 = "lowTemp4";
	public static final String WINDFORCE4 = "windForce4";
	public static final String TEMPCURRENTPUBTIME = "tempCurrentPubtime";

	private int cityId;
	//界面位置
	private int order;
	private String cityName;

	// aqi字段
	private String aqi; 
	private String pm25;
	private String pm25_aqi;
	private String pm10;
	private String pm10_aqi;
	private String so2;
	private String so2_aqi;
	private String no2;
	private String no2_aqi;
	private String co;
	private String co_aqi;
	private String o3;
	private String o3_aqi;
	private String aqi_pubtime;
	private String usa_aqi;
	private String usa_pm25;
	private String us_pubtime;
	private String temp_now;
	private String wind;
	private String weather0;
	private String weather_icon0;
	private String weather0_pubtime;
	private String hightTemp0;
	private String lowTemp0;
	private String windForce0;
	private String weather1;
	private String weather_icon1;
	private String weather1_pubtime;
	private String hightTemp1;
	private String lowTemp1;
	private String windForce1;
	private String weather2;
	private String weather_icon2;
	private String weather2_pubtime;
	private String hightTemp2;
	private String lowTemp2;
	private String windForce2;
	private String weather3;
	private String weather_icon3;
	private String weather3_pubtime;
	private String hightTemp3;
	private String lowTemp3;
	private String windForce3;
	private String weather4;
	private String weather_icon4;
	private String weather4_pubtime;
	private String hightTemp4;
	private String lowTemp4;
	private String windForce4;
	private String tempCurrentPubtime;


	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * 取得 城市名称
	 * 
	 * @return
	 */
	public String getCityName() {
		return cityName;
	}

	/**
	 * 设置城市名称
	 * 
	 * @param city
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	/**
	 * 取得 城市id
	 * 
	 * @return
	 */
	public int getCityId() {
		return cityId;
	}

	/**
	 * 设置城市id
	 * 
	 * @param cityId
	 */
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getAqi() {
		return aqi;
	}

	public void setAqi(String aqi) {
		this.aqi = aqi;
	}

	/**
	 * 取得pm25
	 * 
	 * @return
	 */
	public String getPm25() {
		return pm25;
	}

	/**
	 * 设置pm25
	 * 
	 * @param pm25
	 */
	public void setPm25(String pm25) {
		this.pm25 = pm25;
	}

	/**
	 * 取得pm25的aqi值
	 * 
	 * @return
	 */
	public String getPm25_aqi() {
		return pm25_aqi;
	}

	/**
	 * 设置pm25的aqi值
	 * 
	 * @param pm25_aqi
	 */
	public void setPm25_aqi(String pm25_aqi) {
		this.pm25_aqi = pm25_aqi;
	}

	/**
	 * 取得pm10的值
	 * 
	 * @return
	 */
	public String getPm10() {
		return pm10;
	}

	/**
	 * 设置pm10的值
	 * 
	 * @param pm10
	 */
	public void setPm10(String pm10) {
		this.pm10 = pm10;
	}

	/**
	 * 取得pm10的aqi值
	 * 
	 * @return
	 */
	public String getPm10_aqi() {
		return pm10_aqi;
	}

	/**
	 * 设置pm10的aqi值
	 * 
	 * @param pm25_aqi
	 */
	public void setPm10_aqi(String pm10_aqi) {
		this.pm10_aqi = pm10_aqi;
	}

	/**
	 * 取得So2的值
	 * 
	 * @return
	 */
	public String getSo2() {
		return so2;
	}

	/**
	 * 设置So2的值
	 * 
	 * @param pm10
	 */
	public void setSo2(String so2) {
		this.so2 = so2;
	}

	/**
	 * 取得So2的aqi值
	 * 
	 * @return
	 */
	public String getSo2_aqi() {
		return so2_aqi;
	}

	/**
	 * 设置So2的aqi值
	 * 
	 * @return
	 */
	public void setSo2_aqi(String so2_aqi) {
		this.so2_aqi = so2_aqi;
	}

	/**
	 * 取得No2的值
	 * 
	 * @return
	 */
	public String getNo2() {
		return no2;
	}

	/**
	 * 设置No2的值
	 * 
	 * @return
	 */
	public void setNo2(String no2) {
		this.no2 = no2;
	}

	/**
	 * 取得No2的aqi值
	 * 
	 * @return
	 */
	public String getNo2_aqi() {
		return no2_aqi;
	}

	/**
	 * 设置No2的aqi值
	 * 
	 * @return
	 */
	public void setNo2_aqi(String no2_aqi) {
		this.no2_aqi = no2_aqi;
	}

	/**
	 * 取得co的值
	 * 
	 * @return
	 */
	public String getCo() {
		return co;
	}

	/**
	 * 设置co的值
	 * 
	 * @return
	 */
	public void setCo(String co) {
		this.co = co;
	}

	/**
	 * 取得co的aqi值
	 * 
	 * @return
	 */
	public String getCo_aqi() {
		return co_aqi;
	}

	/**
	 * 设置co的aqi值
	 * 
	 * @return
	 */
	public void setCo_aqi(String co_aqi) {
		this.co_aqi = co_aqi;
	}

	/**
	 * 取得o3的值
	 * 
	 * @return
	 */
	public String getO3() {
		return o3;
	}

	/**
	 * 设置o3的值
	 * 
	 * @return
	 */
	public void setO3(String o3) {
		this.o3 = o3;
	}

	/**
	 * 取得o3的aqi值
	 * 
	 * @return
	 */
	public String getO3_aqi() {
		return o3_aqi;
	}

	/**
	 * 设置o3的aqi值
	 * 
	 * @return
	 */
	public void setO3_aqi(String o3_aqi) {
		this.o3_aqi = o3_aqi;
	}

	/**
	 * 取得aqi的更新时间
	 * 
	 * @return
	 */
	public String getAqi_pubtime() {
		return aqi_pubtime;
	}

	/**
	 * 设置aqi的更新时间
	 * 
	 * @return
	 */
	public void setAqi_pubtime(String aqi_pubtime) {
		this.aqi_pubtime = aqi_pubtime;
	}

	/**
	 * 取得当前天气的更新时间
	 * 
	 * @return
	 */
	public String getWeather0_pubtime() {
		return weather0_pubtime;
	}

	/**
	 * 设置当前天气的更新时间
	 * 
	 * @return
	 */
	public void setWeather0_pubtime(String weather0_pubtime) {
		this.weather0_pubtime = weather0_pubtime;
	}

	/**
	 * 取得第二天天气的更新时间
	 * 
	 * @return
	 */
	public String getWeather1_pubtime() {
		return weather1_pubtime;
	}

	/**
	 * 设置第二天天气的更新时间
	 * 
	 * @return
	 */
	public void setWeather1_pubtime(String weather1_pubtime) {
		this.weather1_pubtime = weather1_pubtime;
	}

	/**
	 * 取得第三天天气的更新时间
	 * 
	 * @return
	 */
	public String getWeather2_pubtime() {
		return weather2_pubtime;
	}

	/**
	 * 设置第三天天气的更新时间
	 * 
	 * @return
	 */
	public void setWeather2_pubtime(String weather2_pubtime) {
		this.weather2_pubtime = weather2_pubtime;
	}

	/**
	 * 获取美使馆的aqi
	 * 
	 * @return
	 */
	public String getUsa_aqi() {
		return usa_aqi;
	}

	/**
	 * 设置美使馆的aqi
	 * 
	 * @return
	 */
	public void setUsa_aqi(String usa_aqi) {
		this.usa_aqi = usa_aqi;
	}

	/**
	 * 获取美使馆的pm25
	 * 
	 * @return
	 */
	public String getUsa_pm25() {
		return usa_pm25;
	}

	// 设置美使馆的pm25
	public void setUsa_pm25(String usa_pm25) {
		this.usa_pm25 = usa_pm25;
	}

	/**
	 * 获取美领馆的更新时间
	 * 
	 * @return
	 */
	public String getUs_pubtime() {
		return us_pubtime;
	}

	/**
	 * 设置美领馆的更新时间
	 * 
	 * @return
	 */
	public void setUs_pubtime(String us_pubtime) {
		this.us_pubtime = us_pubtime;
	}

	/**
	 * 取得当前的温度
	 * 
	 * @return
	 */
	public String getTemp_now() {
		return temp_now;
	}

	/**
	 * 设置当前的温度
	 * 
	 * @return
	 */
	public void setTemp_now(String temp_now) {
		this.temp_now = temp_now;
	}

	/**
	 * 取得今天的天气情况
	 * 
	 * @return
	 */
	public String getWeather0() {
		return weather0;
	}

	/**
	 * 设置今天的天气情况
	 * 
	 * @return
	 */
	public void setWeather0(String weather0) {
		this.weather0 = weather0;
	}

	/**
	 * 取得今天的风向
	 * 
	 * @return
	 */
	public String getWind() {
		return wind;
	}

	/**
	 * 设置今天的风向
	 * 
	 * @return
	 */
	public void setWind(String wind) {
		this.wind = wind;
	}

	/**
	 * 取得今天的最高气温
	 * 
	 * @return
	 */
	public String getHightTemp0() {
		return hightTemp0;
	}

	/**
	 * 设置今天的最高气温
	 * 
	 * @return
	 */
	public void setHightTemp0(String hightTemp0) {
		this.hightTemp0 = hightTemp0;
	}

	/**
	 * 取得今天的最低气温
	 * 
	 * @return
	 */
	public String getLowTemp0() {
		return lowTemp0;
	}

	/**
	 * 设置今天的最低气温
	 * 
	 * @return
	 */
	public void setLowTemp0(String lowTemp0) {
		this.lowTemp0 = lowTemp0;
	}

	/**
	 * 取得第二天的天气情况
	 * 
	 * @return
	 */
	public String getWeather1() {
		return weather1;
	}

	/**
	 * 设置第二天的天气情况
	 * 
	 * @return
	 */
	public void setWeather1(String weather1) {
		this.weather1 = weather1;
	}

	/**
	 * 取得第二天的最高气温
	 * 
	 * @return
	 */
	public String getHightTemp1() {
		return hightTemp1;
	}

	/**
	 * 设置第二天的最高气温
	 * 
	 * @return
	 */
	public void setHightTemp1(String hightTemp1) {
		this.hightTemp1 = hightTemp1;
	}

	/**
	 * 取得第二天的最低气温
	 * 
	 * @return
	 */
	public String getLowTemp1() {
		return lowTemp1;
	}

	/**
	 * 设置第二天的最低气温
	 * 
	 * @return
	 */
	public void setLowTemp1(String lowTemp1) {
		this.lowTemp1 = lowTemp1;
	}

	/**
	 * 取得第三天的天气情况
	 * 
	 * @return
	 */
	public String getWeather2() {
		return weather2;
	}

	/**
	 * 设置第三天的天气情况
	 * 
	 * @return
	 */
	public void setWeather2(String weather2) {
		this.weather2 = weather2;
	}

	/**
	 * 取得第三天的最高气温
	 * 
	 * @return
	 */
	public String getHightTemp2() {
		return hightTemp2;
	}

	/**
	 * 设置第三天的最高气温
	 * 
	 * @return
	 */
	public void setHightTemp2(String hightTemp2) {
		this.hightTemp2 = hightTemp2;
	}

	/**
	 * 取得第三天的最低气温
	 * 
	 * @return
	 */
	public String getLowTemp2() {
		return lowTemp2;
	}

	/**
	 * 设置第三天的最低气温
	 * 
	 * @return
	 */
	public void setLowTemp2(String lowTemp2) {
		this.lowTemp2 = lowTemp2;
	}

	/**
	 * 取得今天的天气情况图片
	 * 
	 * @return
	 */
	public String getWeather_icon0() {
		return weather_icon0;
	}

	/**
	 * 设置今天的天气情况图片
	 * 
	 * @return
	 */
	public void setWeather_icon0(String weather_icon0) {
		this.weather_icon0 = weather_icon0;
	}

	/**
	 * 取得第二天的天气情况图片
	 * 
	 * @return
	 */
	public String getWeather_icon1() {
		return weather_icon1;
	}

	/**
	 * 设置第二天的天气情况图片
	 * 
	 * @return
	 */
	public void setWeather_icon1(String weather_icon1) {
		this.weather_icon1 = weather_icon1;
	}

	/**
	 * 取得第三天的天气情况图片
	 * 
	 * @return
	 */
	public String getWeather_icon2() {
		return weather_icon2;
	}

	/**
	 * 设置第三天的天气情况图片
	 * 
	 * @return
	 */
	public void setWeather_icon2(String weather_icon2) {
		this.weather_icon2 = weather_icon2;
	}

	public String getWeather3() {
		return weather3;
	}

	public void setWeather3(String weather3) {
		this.weather3 = weather3;
	}

	public String getWeather_icon3() {
		return weather_icon3;
	}

	public void setWeather_icon3(String weather_icon3) {
		this.weather_icon3 = weather_icon3;
	}

	public String getWeather3_pubtime() {
		return weather3_pubtime;
	}

	public void setWeather3_pubtime(String weather3_pubtime) {
		this.weather3_pubtime = weather3_pubtime;
	}

	public String getHightTemp3() {
		return hightTemp3;
	}

	public void setHightTemp3(String hightTemp3) {
		this.hightTemp3 = hightTemp3;
	}

	public String getLowTemp3() {
		return lowTemp3;
	}

	public void setLowTemp3(String lowTemp3) {
		this.lowTemp3 = lowTemp3;
	}

	public String getWeather4() {
		return weather4;
	}

	public void setWeather4(String weather4) {
		this.weather4 = weather4;
	}

	public String getWeather_icon4() {
		return weather_icon4;
	}

	public void setWeather_icon4(String weather_icon4) {
		this.weather_icon4 = weather_icon4;
	}

	public String getWeather4_pubtime() {
		return weather4_pubtime;
	}

	public void setWeather4_pubtime(String weather4_pubtime) {
		this.weather4_pubtime = weather4_pubtime;
	}

	public String getHightTemp4() {
		return hightTemp4;
	}

	public void setHightTemp4(String hightTemp4) {
		this.hightTemp4 = hightTemp4;
	}

	public String getLowTemp4() {
		return lowTemp4;
	}

	public void setLowTemp4(String lowTemp4) {
		this.lowTemp4 = lowTemp4;
	}

	public String getWindForce0() {
		return windForce0;
	}

	public void setWindForce0(String windForce0) {
		this.windForce0 = windForce0;
	}

	public String getWindForce1() {
		return windForce1;
	}

	public void setWindForce1(String windForce1) {
		this.windForce1 = windForce1;
	}

	public String getWindForce2() {
		return windForce2;
	}

	public void setWindForce2(String windForce2) {
		this.windForce2 = windForce2;
	}

	public String getWindForce3() {
		return windForce3;
	}

	public void setWindForce3(String windForce3) {
		this.windForce3 = windForce3;
	}

	public String getWindForce4() {
		return windForce4;
	}

	public void setWindForce4(String windForce4) {
		this.windForce4 = windForce4;
	}

	public String getTempCurrentPubtime() {
		return tempCurrentPubtime;
	}

	public void setTempCurrentPubtime(String tempCurrentPubtime) {
		this.tempCurrentPubtime = tempCurrentPubtime;
	}

	public CityAqi(){
		
	}
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CityAqi [cityName=" + cityName + ", cityId=" + cityId + ", aqi=" + aqi + ", pm25=" + pm25
				+ ", pm25_aqi=" + pm25_aqi + ", pm10=" + pm10 + ", pm10_aqi=" + pm10_aqi + ", so2=" + so2
				+ ", so2_aqi=" + so2_aqi + ", no2=" + no2 + ", no2_aqi=" + no2_aqi + ", co=" + co + ", co_aqi="
				+ co_aqi + ", o3=" + o3 + ", o3_aqi=" + o3_aqi + ", aqi_pubtime=" + aqi_pubtime + ", usa_aqi="
				+ usa_aqi + ", usa_pm25=" + usa_pm25 + ", us_pubtime=" + us_pubtime + ", temp_now=" + temp_now
				+ ", wind=" + wind + ", weather0=" + weather0 + ", weather_icon0=" + weather_icon0
				+ ", weather0_pubtime=" + weather0_pubtime + ", hightTemp0=" + hightTemp0 + ", lowTemp0=" + lowTemp0
				+ ", windForce0=" + windForce0 + ", weather1=" + weather1 + ", weather_icon1=" + weather_icon1
				+ ", weather1_pubtime=" + weather1_pubtime + ", hightTemp1=" + hightTemp1 + ", lowTemp1=" + lowTemp1
				+ ", windForce1=" + windForce1 + ", weather2=" + weather2 + ", weather_icon2=" + weather_icon2
				+ ", weather2_pubtime=" + weather2_pubtime + ", hightTemp2=" + hightTemp2 + ", lowTemp2=" + lowTemp2
				+ ", windForce2=" + windForce2 + ", weather3=" + weather3 + ", weather_icon3=" + weather_icon3
				+ ", weather3_pubtime=" + weather3_pubtime + ", hightTemp3=" + hightTemp3 + ", lowTemp3=" + lowTemp3
				+ ", windForce3=" + windForce3 + ", weather4=" + weather4 + ", weather_icon4=" + weather_icon4
				+ ", weather4_pubtime=" + weather4_pubtime + ", hightTemp4=" + hightTemp4 + ", lowTemp4=" + lowTemp4
				+ ", windForce4=" + windForce4 + ", tempCurrentPubtime=" + tempCurrentPubtime + ", order=" + order
				+ "]";
	}

}
