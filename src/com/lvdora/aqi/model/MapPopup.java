package com.lvdora.aqi.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MapPopup implements Serializable {
   
	private String cityCame;
	private int aqi;
	private String airQuality;
	public String getCityCame() {
		return cityCame;
	}
	public void setCityCame(String cityCame) {
		this.cityCame = cityCame;
	}
	public int getAqi() {
		return aqi;
	}
	public void setAqi(int aqi) {
		this.aqi = aqi;
	}
	public String getAirQuality() {
		return airQuality;
	}
	public void setAirQuality(String airQuality) {
		this.airQuality = airQuality;
	}
	@Override
	public String toString() {
		return "MapPopup [cityCame=" + cityCame + ", aqi=" + aqi
				+ ", airQuality=" + airQuality + "]";
	}

}
