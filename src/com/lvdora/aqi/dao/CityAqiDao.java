package com.lvdora.aqi.dao;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lvdora.aqi.model.CityAqi;
import com.lvdora.guid.sqllite.SqliteHelper;

/**
 * cityaqi表的数据操作 DB lvdora.db Table cityaqi
 * 
 * @author Eagle,XQP
 * 
 */
public class CityAqiDao {
	// 数据库名称
	public static String DB_NAME = "lvdora.db";
	// 数据库版本
	public static int DB_VERSION = 3;
	// 表名称
	public static final String TB_NAME = "cityaqi";

	private SQLiteDatabase db;
	private SqliteHelper dbHelper;

	// init
	public CityAqiDao(Context context, String filePath) {
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
	 * 删除比这个小的所有数据
	 * 
	 * @param oldMaxID
	 */
	public void delOldData(int oldMaxID) {
		db.delete(TB_NAME, "id <= ?", new String[] { String.valueOf(oldMaxID) });
	}

	/**
	 * 通过order删除某一条数据
	 * 
	 * @param order
	 */
	public void delByOrder(int order) {
		db.delete(TB_NAME, CityAqi.ORDER + " = ?", new String[] { String.valueOf(order) });
	}

	/**
	 * 通过order删除某一条数据
	 * 
	 * @param order
	 */
	public void deleteAll() {
		db.delete(TB_NAME, null, null);
	}

	/**
	 * 通过cityid删除某一条数据
	 * 
	 * @param order
	 */
	public void delByCityID(int cityid) {
		db.delete(TB_NAME, CityAqi.CITYID + " = ?", new String[] { String.valueOf(cityid) });
	}

	/**
	 * 取得最大的id号
	 * 
	 * @return
	 */
	public int getLastUid() {
		final String str_QUERY = "SELECT MAX(id) FROM " + TB_NAME;
		Cursor cur = db.rawQuery(str_QUERY, null);
		cur.moveToFirst();
		int ID = cur.getInt(0);
		cur.close();
		return ID;
	}

	/**
	 * 
	 * @param cityId
	 * @return
	 */
	public CityAqi getItem(int cityId) {
		String sql = " select * from " + TB_NAME + " where " + CityAqi.CITYID + " = ?; ";
		String[] para = { String.valueOf(cityId) };
		Cursor cursor = this.selectBySql(sql, para);
		cursor.moveToFirst();
		CityAqi cityAqi = new CityAqi();
		cityAqi.setOrder(cursor.getInt(cursor.getColumnIndex("num")));
		cityAqi.setCityId(cursor.getInt(2));
		cityAqi.setCityName(cursor.getString(3));
		cityAqi.setAqi(cursor.getString(4));
		cityAqi.setPm25(cursor.getString(5));
		cityAqi.setPm25_aqi(cursor.getString(6));
		cityAqi.setPm10(cursor.getString(7));
		cityAqi.setPm10_aqi(cursor.getString(8));
		cityAqi.setSo2(cursor.getString(9));
		cityAqi.setSo2_aqi(cursor.getString(10));
		cityAqi.setNo2(cursor.getString(11));
		cityAqi.setNo2_aqi(cursor.getString(12));
		cityAqi.setCo(cursor.getString(13));
		cityAqi.setCo_aqi(cursor.getString(14));
		cityAqi.setO3(cursor.getString(15));
		cityAqi.setO3_aqi(cursor.getString(16));
		cityAqi.setAqi_pubtime(cursor.getString(17));
		cityAqi.setUsa_aqi(cursor.getString(18));
		cityAqi.setUsa_pm25(cursor.getString(19));
		cityAqi.setUs_pubtime(cursor.getString(20));
		cityAqi.setTemp_now(cursor.getString(21));
		cityAqi.setWind(cursor.getString(22));
		cityAqi.setWeather0(cursor.getString(23));
		cityAqi.setWeather_icon0(cursor.getString(24));
		cityAqi.setWeather0_pubtime(cursor.getString(25));
		cityAqi.setHightTemp0(cursor.getString(26));
		cityAqi.setLowTemp0(cursor.getString(27));
		cityAqi.setWindForce0(cursor.getString(28));
		cityAqi.setWeather1(cursor.getString(29));
		cityAqi.setWeather_icon1(cursor.getString(30));
		cityAqi.setWeather1_pubtime(cursor.getString(31));
		cityAqi.setHightTemp1(cursor.getString(32));
		cityAqi.setLowTemp1(cursor.getString(33));
		cityAqi.setWindForce1(cursor.getString(34));
		cityAqi.setWeather2(cursor.getString(35));
		cityAqi.setWeather_icon2(cursor.getString(36));
		cityAqi.setWeather2_pubtime(cursor.getString(37));
		cityAqi.setHightTemp2(cursor.getString(38));
		cityAqi.setLowTemp2(cursor.getString(39));
		cityAqi.setWindForce2(cursor.getString(40));

		// 添加第四第五天
		cityAqi.setWeather3(cursor.getString(41));
		cityAqi.setWeather_icon3(cursor.getString(42));
		cityAqi.setWeather3_pubtime(cursor.getString(43));
		cityAqi.setHightTemp3(cursor.getString(44));
		cityAqi.setLowTemp3(cursor.getString(45));
		cityAqi.setWindForce3(cursor.getString(46));
		cityAqi.setWeather4(cursor.getString(47));
		cityAqi.setWeather_icon4(cursor.getString(48));
		cityAqi.setWeather4_pubtime(cursor.getString(49));
		cityAqi.setHightTemp4(cursor.getString(50));
		cityAqi.setLowTemp4(cursor.getString(51));
		cityAqi.setWindForce4(cursor.getString(52));

		cityAqi.setTempCurrentPubtime(cursor.getString(53));
		cursor.close();
		return cityAqi;
	}

	public CityAqi selectAqiByOrder(int cityOrder) {
		String sql = " select * from " + TB_NAME + " where " + CityAqi.ORDER + " = ?; ";
		String[] para = { String.valueOf(cityOrder) };
		Cursor cursor = this.selectBySql(sql, para);
		cursor.moveToFirst();
		CityAqi cityAqi = new CityAqi();
		cityAqi.setOrder(cursor.getInt(cursor.getColumnIndex("num")));
		cityAqi.setCityId(cursor.getInt(2));
		cityAqi.setCityName(cursor.getString(3));
		cityAqi.setAqi(cursor.getString(4));
		cityAqi.setPm25(cursor.getString(5));
		cityAqi.setPm25_aqi(cursor.getString(6));
		cityAqi.setPm10(cursor.getString(7));
		cityAqi.setPm10_aqi(cursor.getString(8));
		cityAqi.setSo2(cursor.getString(9));
		cityAqi.setSo2_aqi(cursor.getString(10));
		cityAqi.setNo2(cursor.getString(11));
		cityAqi.setNo2_aqi(cursor.getString(12));
		cityAqi.setCo(cursor.getString(13));
		cityAqi.setCo_aqi(cursor.getString(14));
		cityAqi.setO3(cursor.getString(15));
		cityAqi.setO3_aqi(cursor.getString(16));
		cityAqi.setAqi_pubtime(cursor.getString(17));
		cityAqi.setUsa_aqi(cursor.getString(18));
		cityAqi.setUsa_pm25(cursor.getString(19));
		cityAqi.setUs_pubtime(cursor.getString(20));
		cityAqi.setTemp_now(cursor.getString(21));
		cityAqi.setWind(cursor.getString(22));
		cityAqi.setWeather0(cursor.getString(23));
		cityAqi.setWeather_icon0(cursor.getString(24));
		cityAqi.setWeather0_pubtime(cursor.getString(25));
		cityAqi.setHightTemp0(cursor.getString(26));
		cityAqi.setLowTemp0(cursor.getString(27));
		cityAqi.setWindForce0(cursor.getString(28));
		cityAqi.setWeather1(cursor.getString(29));
		cityAqi.setWeather_icon1(cursor.getString(30));
		cityAqi.setWeather1_pubtime(cursor.getString(31));
		cityAqi.setHightTemp1(cursor.getString(32));
		cityAqi.setLowTemp1(cursor.getString(33));
		cityAqi.setWindForce1(cursor.getString(34));
		cityAqi.setWeather2(cursor.getString(35));
		cityAqi.setWeather_icon2(cursor.getString(36));
		cityAqi.setWeather2_pubtime(cursor.getString(37));
		cityAqi.setHightTemp2(cursor.getString(38));
		cityAqi.setLowTemp2(cursor.getString(39));
		cityAqi.setWindForce2(cursor.getString(40));

		// 添加第四第五天
		cityAqi.setWeather3(cursor.getString(41));
		cityAqi.setWeather_icon3(cursor.getString(42));
		cityAqi.setWeather3_pubtime(cursor.getString(43));
		cityAqi.setHightTemp3(cursor.getString(44));
		cityAqi.setLowTemp3(cursor.getString(45));
		cityAqi.setWindForce3(cursor.getString(46));
		cityAqi.setWeather4(cursor.getString(47));
		cityAqi.setWeather_icon4(cursor.getString(48));
		cityAqi.setWeather4_pubtime(cursor.getString(49));
		cityAqi.setHightTemp4(cursor.getString(50));
		cityAqi.setLowTemp4(cursor.getString(51));
		cityAqi.setWindForce4(cursor.getString(52));

		cityAqi.setTempCurrentPubtime(cursor.getString(53));
		cursor.close();
		return cityAqi;
	}

	/**
	 * 取得全部数据
	 * 
	 * @return
	 */
	public List<CityAqi> getAll() {
		List<CityAqi> list = new ArrayList<CityAqi>();
		Cursor cursor = db.query(TB_NAME, null, null, null, null, null, CityAqi.ORDER + " ASC");
		cursor.moveToFirst();
		while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
			CityAqi cityAqi = new CityAqi();
			cityAqi.setOrder(cursor.getInt(cursor.getColumnIndex("num")));
			cityAqi.setCityId(cursor.getInt(2));
			cityAqi.setCityName(cursor.getString(3));
			cityAqi.setAqi(cursor.getString(4));
			cityAqi.setPm25(cursor.getString(5));
			cityAqi.setPm25_aqi(cursor.getString(6));
			cityAqi.setPm10(cursor.getString(7));
			cityAqi.setPm10_aqi(cursor.getString(8));
			cityAqi.setSo2(cursor.getString(9));
			cityAqi.setSo2_aqi(cursor.getString(10));
			cityAqi.setNo2(cursor.getString(11));
			cityAqi.setNo2_aqi(cursor.getString(12));
			cityAqi.setCo(cursor.getString(13));
			cityAqi.setCo_aqi(cursor.getString(14));
			cityAqi.setO3(cursor.getString(15));
			cityAqi.setO3_aqi(cursor.getString(16));
			cityAqi.setAqi_pubtime(cursor.getString(17));
			cityAqi.setUsa_aqi(cursor.getString(18));
			cityAqi.setUsa_pm25(cursor.getString(19));
			cityAqi.setUs_pubtime(cursor.getString(20));
			cityAqi.setTemp_now(cursor.getString(21));
			cityAqi.setWind(cursor.getString(22));
			cityAqi.setWeather0(cursor.getString(23));
			cityAqi.setWeather_icon0(cursor.getString(24));
			cityAqi.setWeather0_pubtime(cursor.getString(25));
			cityAqi.setHightTemp0(cursor.getString(26));
			cityAqi.setLowTemp0(cursor.getString(27));
			cityAqi.setWindForce0(cursor.getString(28));

			cityAqi.setWeather1(cursor.getString(29));
			cityAqi.setWeather_icon1(cursor.getString(30));
			cityAqi.setWeather1_pubtime(cursor.getString(31));
			cityAqi.setHightTemp1(cursor.getString(32));
			cityAqi.setLowTemp1(cursor.getString(33));
			cityAqi.setWindForce1(cursor.getString(34));

			cityAqi.setWeather2(cursor.getString(35));
			cityAqi.setWeather_icon2(cursor.getString(36));
			cityAqi.setWeather2_pubtime(cursor.getString(37));
			cityAqi.setHightTemp2(cursor.getString(38));
			cityAqi.setLowTemp2(cursor.getString(39));
			cityAqi.setWindForce2(cursor.getString(40));

			// 添加第四第五天
			cityAqi.setWeather3(cursor.getString(41));
			cityAqi.setWeather_icon3(cursor.getString(42));
			cityAqi.setWeather3_pubtime(cursor.getString(43));
			cityAqi.setHightTemp3(cursor.getString(44));
			cityAqi.setLowTemp3(cursor.getString(45));
			cityAqi.setWindForce3(cursor.getString(46));

			cityAqi.setWeather4(cursor.getString(47));
			cityAqi.setWeather_icon4(cursor.getString(48));
			cityAqi.setWeather4_pubtime(cursor.getString(49));
			cityAqi.setHightTemp4(cursor.getString(50));
			cityAqi.setLowTemp4(cursor.getString(51));
			cityAqi.setWindForce4(cursor.getString(52));

			cityAqi.setTempCurrentPubtime(cursor.getString(53));
			list.add(cityAqi);
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}

	/**
	 * 保存多条数据
	 * 
	 * @param cityAqis
	 */
	public void insertCityAqiList(List<CityAqi> cityAqis) {
		db.beginTransaction();
		this.deleteAll();
		for (CityAqi cityAqi : cityAqis) {
			this.saveData(cityAqi);
		}
		Log.e("CityAqiDao", "" + this.getCount());
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 插入单条数据
	 * 
	 * @param cityAqi
	 */
	public void saveData(CityAqi cityAqi) {
		// 将VO存入DB
		ContentValues values = cityAqiBeanToContentValues(cityAqi);
		db.insert(TB_NAME, CityAqi.ORDER, values);
	}

	// 总数
	public int getCount() {
		final String str_QUERY = "SELECT count(*) FROM " + TB_NAME;
		Cursor cursor = db.rawQuery(str_QUERY, null);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count;
	}

	// 自定义sql查询
	public Cursor selectBySql(String sql, String[] para) {
		if (db.isOpen()) {
			return db.rawQuery(sql, para);
		}
		return null;
	}

	// 取得最大的order编号
	public int selectMaxNum() {
		final String str_QUERY = "SELECT MAX(num) FROM " + TB_NAME;
		Cursor cur = db.rawQuery(str_QUERY, null);
		cur.moveToFirst();
		int num = cur.getInt(0);
		cur.close();
		return num;
	}

	// update指定id的城市数据
	public void updateSingleById(CityAqi cityAqi, int id) {
		db.beginTransaction();
		try {
			delByCityID(id);
			saveData(cityAqi);
			db.setTransactionSuccessful();
		} catch (Exception e) {
		} finally {
			db.endTransaction();
		}
	}

	// 改变序号
	public void updateOrderByOrder(int orderOld, int orderNew) {
		db.execSQL("update cityaqi set num = ? where num = ?", new String[] { orderNew + "", orderOld + "" });
	}

	// VO转为名值对
	private ContentValues cityAqiBeanToContentValues(CityAqi cityAqi) {

		ContentValues values;
		values = new ContentValues();
		// {num = 4...}
		values.put(CityAqi.ORDER, cityAqi.getOrder());
		values.put(CityAqi.CITYNAME, cityAqi.getCityName());
		values.put(CityAqi.CITYID, cityAqi.getCityId());
		values.put(CityAqi.AQI, cityAqi.getAqi());
		values.put(CityAqi.PM25, cityAqi.getPm25());
		values.put(CityAqi.PM25_AQI, cityAqi.getPm25_aqi());
		values.put(CityAqi.PM10, cityAqi.getPm10());
		values.put(CityAqi.PM10_AQI, cityAqi.getPm10_aqi());
		values.put(CityAqi.SO2, cityAqi.getSo2());
		values.put(CityAqi.SO2_AQI, cityAqi.getSo2_aqi());
		values.put(CityAqi.NO2, cityAqi.getNo2());
		values.put(CityAqi.NO2_AQI, cityAqi.getNo2_aqi());
		values.put(CityAqi.CO, cityAqi.getCo());
		values.put(CityAqi.CO_AQI, cityAqi.getCo_aqi());
		values.put(CityAqi.O3, cityAqi.getO3());
		values.put(CityAqi.O3_AQI, cityAqi.getO3_aqi());
		values.put(CityAqi.AQI_PUBTIME, cityAqi.getAqi_pubtime());
		values.put(CityAqi.USA_AQI, cityAqi.getUsa_aqi());
		values.put(CityAqi.USA_PM25, cityAqi.getUsa_pm25());
		values.put(CityAqi.US_PUBTIME, cityAqi.getUs_pubtime());
		values.put(CityAqi.TEMP_NOW, cityAqi.getTemp_now());
		values.put(CityAqi.WIND, cityAqi.getWind());
		values.put(CityAqi.WEATHER0, cityAqi.getWeather0());
		values.put(CityAqi.WEATHER_ICON0, cityAqi.getWeather_icon0());
		values.put(CityAqi.WEATHER0_PUBTIME, cityAqi.getWeather0_pubtime());
		values.put(CityAqi.HIGHTTEMP0, cityAqi.getHightTemp0());
		values.put(CityAqi.LOWTEMP0, cityAqi.getLowTemp0());
		values.put(CityAqi.WINDFORCE0, cityAqi.getWindForce0());
		values.put(CityAqi.WEATHER1, cityAqi.getWeather1());
		values.put(CityAqi.WEATHER_ICON1, cityAqi.getWeather_icon1());
		values.put(CityAqi.WEATHER1_PUBTIME, cityAqi.getWeather1_pubtime());
		values.put(CityAqi.HIGHTTEMP1, cityAqi.getHightTemp1());
		values.put(CityAqi.LOWTEMP1, cityAqi.getLowTemp1());
		values.put(CityAqi.WINDFORCE1, cityAqi.getWindForce1());
		values.put(CityAqi.WEATHER2, cityAqi.getWeather2());
		values.put(CityAqi.WEATHER_ICON2, cityAqi.getWeather_icon2());
		values.put(CityAqi.WEATHER2_PUBTIME, cityAqi.getWeather2_pubtime());
		values.put(CityAqi.HIGHTTEMP2, cityAqi.getHightTemp2());
		values.put(CityAqi.LOWTEMP2, cityAqi.getLowTemp2());
		values.put(CityAqi.WINDFORCE2, cityAqi.getWindForce2());
		values.put(CityAqi.WEATHER3, cityAqi.getWeather3());
		values.put(CityAqi.WEATHER_ICON3, cityAqi.getWeather_icon3());
		values.put(CityAqi.WEATHER3_PUBTIME, cityAqi.getWeather3_pubtime());
		values.put(CityAqi.HIGHTTEMP3, cityAqi.getHightTemp3());
		values.put(CityAqi.LOWTEMP3, cityAqi.getLowTemp3());
		values.put(CityAqi.WINDFORCE3, cityAqi.getWindForce3());
		values.put(CityAqi.WEATHER4, cityAqi.getWeather4());
		values.put(CityAqi.WEATHER_ICON4, cityAqi.getWeather_icon4());
		values.put(CityAqi.WEATHER4_PUBTIME, cityAqi.getWeather4_pubtime());
		values.put(CityAqi.HIGHTTEMP4, cityAqi.getHightTemp4());
		values.put(CityAqi.LOWTEMP4, cityAqi.getLowTemp4());
		values.put(CityAqi.WINDFORCE4, cityAqi.getWindForce4());
		values.put(CityAqi.TEMPCURRENTPUBTIME, cityAqi.getTempCurrentPubtime());
		return values;
	}
}
