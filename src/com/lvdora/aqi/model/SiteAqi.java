package com.lvdora.aqi.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SiteAqi implements Serializable{

	private int cityId;
	private String name;
	private String aqi;
	private String pm25;
	private String updateTime;
	private String spotLongitude;
	private String spotLatitude;
	//private float calculate;


	
	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAqi() {
		return aqi;
	}

	public void setAqi(String aqi) {
		this.aqi = aqi;
	}

	public String getPm25() {
		return pm25;
	}

	public void setPm25(String pm25) {
		this.pm25 = pm25;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getSpotLongitude() {
		return spotLongitude;
	}

	public void setSpotLongitude(String spotLongitude) {
		this.spotLongitude = spotLongitude;
	}

	public String getSpotLatitude() {
		return spotLatitude;
	}

	public void setSpotLatitude(String spotLatitude) {
		this.spotLatitude = spotLatitude;
	}

	@Override
	public String toString() {
		return "SiteAqi [cityId=" + cityId + ", name=" + name + ", aqi=" + aqi
				+ ", pm25=" + pm25 + ", updateTime=" + updateTime
				+ ", spotLongitude=" + spotLongitude + ", spotLatitude="
				+ spotLatitude + "]";
	}
   
}
