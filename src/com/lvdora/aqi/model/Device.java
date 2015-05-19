package com.lvdora.aqi.model;

/**
 * 
 * 民间站点
 * 
 * @author xqp
 */
public class Device {

	private String id = "3";
	// 站点
	private String nickname = "\u548c\u5e73\u91cc";
	// pm25
	private String pm25 = "39";
	// 指数
	private String aqi = "--";
	// 分类
	private String style = "private";
	private String devId = "LV000006";
	private String countyName = "\u4e1c\u57ce\u533a";
	private String countySort = "0";
	private String cityId = "18";
	private String countyId = "426";
	private String address = "后沙峪";

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPm25() {
		return pm25;
	}

	public void setPm25(String pm25) {
		this.pm25 = pm25;
	}

	public String getAqi() {
		return aqi;
	}

	public void setAqi(String aqi) {
		this.aqi = aqi;
	}

	public String getDevId() {
		return devId;
	}

	public void setDevId(String devId) {
		this.devId = devId;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public String getCountySort() {
		return countySort;
	}

	public void setCountySort(String countySort) {
		this.countySort = countySort;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCountyId() {
		return countyId;
	}

	public void setCountyId(String countyId) {
		this.countyId = countyId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@Override
	public String toString() {
		return "Device [nickname=" + nickname + ", pm25=" + pm25 + ", aqi=" + aqi + ", devId=" + devId
				+ ", countyName=" + countyName + ", address=" + address + "]";
	}
}
