package com.lvdora.aqi.model;

import java.io.Serializable;

public class City implements Serializable{
	
	/**
	 * 序列化编号
	 */
	private static final long serialVersionUID = 6739328839130383035L;
	
	private int id;
	private int order;
	private String name;
	private int provinceId;
	
	/**
	 * 取得顺序
	 * @return
	 */
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	@Override
	public String toString() {
		return "City [id=" + id + ", name=" + name + ", order=" + order
				+ ", provinceId=" + provinceId + "]";
	}

}
