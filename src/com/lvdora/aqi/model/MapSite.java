package com.lvdora.aqi.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MapSite implements Serializable {

	private String name;
	private String aqi;
	private Double spotLongitude;
	private Double spotLatitude;

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

	public Double getSpotLongitude() {
		return spotLongitude;
	}

	public void setSpotLongitude(Double spotLongitude) {
		this.spotLongitude = spotLongitude;
	}

	public Double getSpotLatitude() {
		return spotLatitude;
	}

	public void setSpotLatitude(Double spotLatitude) {
		this.spotLatitude = spotLatitude;
	}

	@Override
	public String toString() {
		return "MapSite [name=" + name + ", aqi=" + aqi + ", spotLongitude="
				+ spotLongitude + ", spotLatitude=" + spotLatitude + "]";
	}

}
