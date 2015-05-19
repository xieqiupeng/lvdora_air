package com.lvdora.aqi.dao;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
//import android.util.Log;

import com.lvdora.aqi.model.CityAqi;
import com.lvdora.aqi.model.Device;
import com.lvdora.guid.sqllite.SqliteHelper;

/**
 * device表的数据操作
 * 
 * @1 DB lvdora.db
 * @2 Table device
 * 
 * @author xqp
 */
public class DeviceDao {
	// 数据库名称
	public static String DB_NAME = "lvdora.db";
	// 数据库版本
	public static int DB_VERSION = 3;
	// 表名称
	public static final String TB_NAME = "device";

	private SQLiteDatabase db;
	private SqliteHelper dbHelper;

	// init
	public DeviceDao(Context context) {
		dbHelper = SqliteHelper.getInstance(context, DB_NAME, null, DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}

	// close
	public void closeAll() {
		db.close();
		dbHelper.close();
	}

	/**
	 * 取得总条数
	 * 
	 * @return
	 */
	public int length() {
		return this.length(TB_NAME);
	}

	/**
	 * 取得数据库总条数
	 * 
	 * @param tabName
	 * @return
	 */
	public int length(String tabName) {
		Cursor cursor = db.query(tabName, null, null, null, null, null, "id ASC");
		cursor.moveToFirst();
		int length = cursor.getCount();
		cursor.close();
		return length;
	}

	/**
	 * 插入单条数据
	 * 
	 * @param cityAqi
	 */
	public void insertAllDevice(List<Device> devices) {
		Log.i("DeviceDao", devices.toString());
		// 将VO存入DB
		db.beginTransaction();
		db.delete(TB_NAME, null, null);
		for (Device device : devices) {
			ContentValues values = deviceBeanToContentValues(device);
			db.insert(TB_NAME, CityAqi.ORDER, values);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	// group数据
	public List<String> selectGroup(String cityId, String style) {
		List<String> groupList = new ArrayList<String>();
		Cursor cursor = selectCounty(cityId, style);
		do {
			String name = cursor.getString(cursor.getColumnIndex("countyName"));
			groupList.add(name);
		} while (cursor.moveToNext());
		Log.i("DeviceDao", groupList.toString());
		return groupList;
	}

	public Cursor selectCounty(String cityId, String style) {
		String sql = "SELECT countyName FROM " + TB_NAME
				+ " WHERE cityId = ? AND style = ? GROUP BY countyName ORDER BY countySort";
		String[] para = new String[] { cityId, style };
		Cursor cursor = db.rawQuery(sql, para);
		cursor.moveToFirst();
		return cursor;
	}

	// child数据
	public List<Device> selectChildsInOneGroup(String countyName, String cityId, String style) {
		List<Device> list = new ArrayList<Device>();
		Cursor cursor = selectDeviceByCountyName(countyName, cityId, style);
		do {
			Device device = new Device();
			device.setCountyName(countyName);
			String address = cursor.getString(cursor.getColumnIndex("address"));
			device.setAddress(address);
			String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
			device.setNickname(nickname);
			String pm25 = cursor.getString(cursor.getColumnIndex("pm25"));
			device.setPm25(pm25);
			String aqi = cursor.getString(cursor.getColumnIndex("aqi"));
			device.setAqi(aqi);
			list.add(device);
		} while (cursor.moveToNext());
		return list;
	}

	public Cursor selectDeviceByCountyName(String countyName, String cityId, String style) {
		String sql = "SELECT countyname,id, devId, nickname, pm25, aqi, address FROM device WHERE countyname = ? and cityId = ? and style = ? ORDER BY nickname";
		String[] para = new String[] { countyName, cityId, style };
		Cursor cursor = db.rawQuery(sql, para);
		cursor.moveToFirst();
		return cursor;
	}

	public Cursor selectByCity(String cityId) {
		String sql = "select countyName,id,devId,nickname,pm25,aqi,cityId"//
				+ " from " + TB_NAME + " where cityId = ? order by countySort";
		String[] para = new String[] { cityId };
		Cursor cursor = db.rawQuery(sql, para);
		cursor.moveToFirst();
		return cursor;
	}

	public String[] selectCityArray() {
		String sql = "SELECT cityId, count(0) FROM " + TB_NAME + " GROUP BY cityId";
		Cursor cursor = db.rawQuery(sql, null);
		String[] cityArray = new String[cursor.getCount()];
		cursor.moveToFirst();
		int i = 0;
		do {
			String cityId = cursor.getString(cursor.getColumnIndex("cityId"));
			Log.i("DeviceDao", cursor.getString(cursor.getColumnIndex("cityId")));
			cityArray[i] = cityId;
			i++;
		} while (cursor.moveToNext());
		Log.i("DeviceDao", cityArray.length + "");
		return cityArray;
	}

	// 改变序号
	public void updateOrderByOrder(int orderOld, int orderNew) {
		db.execSQL("update cityaqi set num = ? where num = ?", new String[] { orderNew + "", orderOld + "" });
	}

	// VO转为名值对
	private ContentValues deviceBeanToContentValues(Device device) {
		ContentValues values = new ContentValues();
		// {num = 4...}
		values.put("nickname", device.getNickname());
		values.put("address", device.getAddress());
		values.put("pm25", device.getPm25());
		values.put("style", device.getStyle());
		values.put("devId", device.getDevId());
		values.put("aqi", device.getAqi());
		values.put("countySort", device.getCountySort());
		values.put("countyName", device.getCountyName());
		values.put("countyId", device.getCountyId());
		values.put("cityId", device.getCityId());
		return values;
	}
}
