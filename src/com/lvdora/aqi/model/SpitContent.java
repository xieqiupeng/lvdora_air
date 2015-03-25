package com.lvdora.aqi.model;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class SpitContent implements Serializable {

	private String spotId;
	private String content;
	private String pubTime;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPubTime() {
		return pubTime;
	}

	public void setPubTime(String pubTime) {
		this.pubTime = pubTime;
	}

	public String getSpotId() {
		return spotId;
	}

	public void setSpotId(String spotId) {
		this.spotId = spotId;
	}

	@Override
	public String toString() {
		return "SpitContent [spotId=" + spotId + ", content=" + content
				+ ", pubTime=" + pubTime + "]";
	}

	//log测试spitContent数据
	public static String spitContentListToString(List<SpitContent> list) {
		SpitContent aVenting = new SpitContent();
		String str = "";
		for (int i = 0; i < list.size(); i++) {
			aVenting = list.get(i);
			str += aVenting.toString() + "\n";
		}
		return str;
	}
}
