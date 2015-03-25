package com.lvdora.aqi.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CityRank implements Comparable<CityRank>, Serializable {

	// city信息
	private String provinceName;
	private String cityName;
	// aqi信息
	private String aqi;
	private String aqiCalculated;
	private String pm25;
	private String pm25Calculated;
	private String pm10;
	private String pm10Calculated;
	private String so2;
	private String so2Calculated;
	private String o3;
	private String o3Calculated;
	private String no2;
	private String no2Calculated;

	/*
	 * public String getRank() { return rank; }
	 * 
	 * public void setRank(String rank) { this.rank = rank; }
	 */

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getAqi() {
		return aqi;
	}

	public void setAqi(String aqi) {
		this.aqi = aqi;
	}

	public String getAqiCalculated() {
		return aqiCalculated;
	}

	public void setAqiCalculated(String aqiCalculated) {
		this.aqiCalculated = aqiCalculated;
	}

	public String getPm25() {
		return pm25;
	}

	public void setPm25(String pm25) {
		this.pm25 = pm25;
	}

	public String getPm25Calculated() {
		return pm25Calculated;
	}

	public void setPm25Calculated(String pm25Calculated) {
		this.pm25Calculated = pm25Calculated;
	}

	public String getPm10() {
		return pm10;
	}

	public void setPm10(String pm10) {
		this.pm10 = pm10;
	}

	public String getPm10Calculated() {
		return pm10Calculated;
	}

	public void setPm10Calculated(String pm10Calculated) {
		this.pm10Calculated = pm10Calculated;
	}

	public String getSo2() {
		return so2;
	}

	public void setSo2(String so2) {
		this.so2 = so2;
	}

	public String getSo2Calculated() {
		return so2Calculated;
	}

	public void setSo2Calculated(String so2Calculated) {
		this.so2Calculated = so2Calculated;
	}

	public String getO3() {
		return o3;
	}

	public void setO3(String o3) {
		this.o3 = o3;
	}

	public String getO3Calculated() {
		return o3Calculated;
	}

	public void setO3Calculated(String o3Calculated) {
		this.o3Calculated = o3Calculated;
	}

	public String getNo2() {
		return no2;
	}

	public void setNo2(String no2) {
		this.no2 = no2;
	}

	public String getNo2Calculated() {
		return no2Calculated;
	}

	public void setNo2Calculated(String no2Calculated) {
		this.no2Calculated = no2Calculated;
	}

	@Override
	public String toString() {
		return "CityRank [provinceName=" + provinceName + ", cityName=" + cityName + ", aqi=" + aqi
				+ ", aqiCalculated=" + aqiCalculated + ", pm25=" + pm25 + ", pm25Calculated=" + pm25Calculated
				+ ", pm10=" + pm10 + ", pm10Calculated=" + pm10Calculated + ", so2=" + so2 + ", so2Calculated="
				+ so2Calculated + ", o3=" + o3 + ", o3Calculated=" + o3Calculated + ", no2=" + no2 + ", no2Calculated="
				+ no2Calculated + "]";
	}

	@Override
	public int compareTo(CityRank city) {
		return this.getAqiCalculated().compareTo(city.getAqiCalculated());
	}

}