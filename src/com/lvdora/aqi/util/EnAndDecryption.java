package com.lvdora.aqi.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import com.lvdora.aqi.model.City;
import com.lvdora.aqi.model.CityRank;
import com.lvdora.aqi.model.MapSite;
import com.lvdora.aqi.model.SiteAqi;
import com.lvdora.aqi.model.SpitContent;

/**
 * 
 * 城市列表的加解密
 * 
 * @author xqp
 * 
 */
public class EnAndDecryption {

	/**
	 * 加密
	 */
	public static String CityList2String(List<City> cityList) {
		// 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream;
		String cityListString = "";
		try {
			// 然后将得到的字符数据装载到ObjectOutputStream
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			// writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
			objectOutputStream.writeObject(cityList);
			// 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
			cityListString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
			// 关闭objectOutputStream
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cityListString;
	}

	// aqiList加密
	public static String SiteList2String(List<SiteAqi> cityList) {
		String cityListString = "";
		// 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// 然后将得到的字符数据装载到ObjectOutputStream
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			// writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
			objectOutputStream.writeObject(cityList);
			// 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
			cityListString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
			// 关闭objectOutputStream
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cityListString;

	}

	public static String mapSiteList2String(List<MapSite> mapSiteList) throws Exception {
		// 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// 然后将得到的字符数据装载到ObjectOutputStream
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		// writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
		objectOutputStream.writeObject(mapSiteList);
		// 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
		String mapSiteListString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
		// 关闭objectOutputStream
		objectOutputStream.close();
		return mapSiteListString;

	}

	public static String spitContentList2String(List<SpitContent> spitContentList) throws Exception {
		// 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// 然后将得到的字符数据装载到ObjectOutputStream
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		// writeObject方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
		objectOutputStream.writeObject(spitContentList);
		// 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
		String spitContentListString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
		// 关闭objectOutputStream objectOutputStream.close();
		return spitContentListString;
	}

	public static String CityRankList2String(List<CityRank> cityRankList) throws IOException {
		// 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// 然后将得到的字符数据装载到ObjectOutputStream
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		// writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
		objectOutputStream.writeObject(cityRankList);
		// 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
		String cityListString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
		// 关闭objectOutputStream
		objectOutputStream.close();
		return cityListString;

	}

	public static String SpitList2String(List<SpitContent> spitList) throws Exception {
		// 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// 然后将得到的字符数据装载到ObjectOutputStream
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		// writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
		objectOutputStream.writeObject(spitList);
		// 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
		String spitListString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
		// 关闭objectOutputStream
		objectOutputStream.close();
		return spitListString;

	}

	// 解密
	public static List<City> String2WeatherList(String cityListString) {
		List<City> cityList = new ArrayList<City>();
		byte[] mobileBytes = Base64.decode(cityListString.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			cityList = (List<City>) objectInputStream.readObject();
			objectInputStream.close();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return cityList;
	}

	/**
	 * 解密
	 */
	public static List<SiteAqi> String2SiteList(String cityListString) {
		byte[] mobileBytes = Base64.decode(cityListString.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
		List<SiteAqi> siteList = new ArrayList<SiteAqi>();
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			siteList = (List<SiteAqi>) objectInputStream.readObject();
			objectInputStream.close();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return siteList;
	}

	public static List<MapSite> String2MapSiteList(String mapSiteListString) throws Exception {
		byte[] mobileBytes = Base64.decode(mapSiteListString.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
		ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
		List<MapSite> mapSiteList = (List<MapSite>) objectInputStream.readObject();
		objectInputStream.close();
		return mapSiteList;
	}

	public static List<SpitContent> String2SpitContentList(String spitContentListString) {
		byte[] mobileBytes = Base64.decode(spitContentListString.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
		ObjectInputStream objectInputStream;
		List<SpitContent> spitContentList = null;
		try {
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			spitContentList = (List<SpitContent>) objectInputStream.readObject();
			objectInputStream.close();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return spitContentList;
	}

	public static List<CityRank> String2CityRankList(String cityListString) {
		byte[] mobileBytes = Base64.decode(cityListString.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
		ObjectInputStream objectInputStream;
		List<CityRank> cityList = null;
		try {
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			cityList = (List<CityRank>) objectInputStream.readObject();
			objectInputStream.close();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return cityList;
	}
}
