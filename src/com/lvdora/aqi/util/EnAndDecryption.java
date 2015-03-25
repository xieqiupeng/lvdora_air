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
import com.lvdora.aqi.model.SiteAqi;

/**
 * 
 * 加城市列表的加解密
 * 
 * @author xqp
 * 
 */
public class EnAndDecryption {

	// cityList加密
	public static String CityList2String(List<City> cityList) throws IOException {
		// 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件。
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		// 然后将得到的字符数据装载到ObjectOutputStream
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		// writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
		objectOutputStream.writeObject(cityList);
		// 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
		String cityListString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
		// 关闭objectOutputStream
		objectOutputStream.close();
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

	// 解密
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
}
