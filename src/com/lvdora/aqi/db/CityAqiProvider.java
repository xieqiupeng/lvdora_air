package com.lvdora.aqi.db;

import com.lvdora.aqi.model.CityAqi;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class CityAqiProvider extends ContentProvider {

	// 数据库存放的包名
	public static final Uri URI = Uri.parse("content://com.lvdora.aqi");
	// 数据库名称
	public static String DB_NAME = "lvdora.db";
	// 数据库版本
	public static int DB_VERSION = 3;
	// 表名称
	public static final String TB_NAME = "cityaqi";
	// 各方法都要使用的数据库
	SQLiteDatabase database;

	@Override
	public boolean onCreate() {
		database = getContext().openOrCreateDatabase("lvdora.db", Context.MODE_PRIVATE, null);
		database.execSQL("CREATE TABLE IF NOT EXISTS cityaqi(id integer primary key autoincrement, num integer unique,cityName varchar(10), cityId varchar(10) unique,aqi varchar(10),pm25 varchar(10),pm25_aqi varchar(10),pm10 varchar(10),pm10_aqi varchar(10),so2 varchar(10),so2_aqi varchar(10),no2 varchar(10),no2_aqi varchar(10),co varchar(10),co_aqi varchar(10),o3 varchar(10),o3_aqi varchar(10),aqi_pubtime varchar(10),usa_aqi varchar(10),usa_pm25 varchar(10),us_pubtime varchar(10),temp_now varchar(10),wind varchar(20),weather0 varchar(20),weather_icon0 varchar(10),weather0_pubtime varchar(20),hightTemp0 varchar(10),lowTemp0 varchar(10),windForce0 varchar(10),weather1 varchar(20),weather_icon1 varchar(10),weather1_pubtime varchar(10),hightTemp1 varchar(10),lowTemp1 varchar(10),windForce1 varchar(10),weather2 varchar(10),weather_icon2 varchar(10),weather2_pubtime varchar(20),hightTemp2 varchar(10),lowTemp2 varchar(10),windForce2 varchar(10),weather3 varchar(10),weather_icon3 varchar(10),weather3_pubtime varchar(20),hightTemp3 varchar(10),lowTemp3 varchar(10),windForce3 varchar(10),weather4 varchar(10),weather_icon4 varchar(10),weather4_pubtime varchar(20),hightTemp4 varchar(10),lowTemp4 varchar(10),windForce4 varchar(10),tempCurrentPubtime varchar(10))");
		return false;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentvalues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues contentvalues, String s, String[] as) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Cursor query(Uri uri, String[] as, String s, String[] as1, String s1) {
		Cursor cursor = database.query(TB_NAME, null, null, null, null, null, CityAqi.ORDER + " ASC");
		return cursor;
	}

	@Override
	public int delete(Uri uri, String s, String[] as) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}
}
