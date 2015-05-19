package com.lvdora.aqi.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lvdora.aqi.model.City;
import com.lvdora.aqi.model.Province;
import com.lvdora.aqi.model.Longlati;

/**
 * city.db
 * 
 * @author xqp
 * 
 */
public class CityDao {

	private static final String DB_PATH = "/data/data/com.lvdora.aqi/city.db";
	private Context context;

	private String TABLE_NAME = "aqi_cities";

	public CityDao(Context paramContext) {
		this.setContext(paramContext);
	}

	private SQLiteDatabase getCityDB() {
		return SQLiteDatabase.openDatabase(getDBPath(), null, 1);
	}

	private String getDBPath() {
		return new File(DB_PATH).getAbsolutePath();
	}

	/**
	 * 查询所有的省市
	 * 
	 * @return
	 */
	public List<Province> getAll() {
		SQLiteDatabase localSQLiteDatabase = getCityDB();
		List<Province> provinces = new ArrayList<Province>();
		if (localSQLiteDatabase.isOpen()) {
			Cursor cursor = localSQLiteDatabase.rawQuery("select id, name from aqi_provinces", null);
			while (cursor.moveToNext()) {
				String proName = cursor.getString(cursor.getColumnIndex("name"));
				String proId = cursor.getString(cursor.getColumnIndex("id"));
				ArrayList<City> citys = new ArrayList<City>();
				Cursor cursor2 = localSQLiteDatabase.rawQuery(
						"select id,province_id,name from aqi_cities where is_have_aqi=1 and province_id =?",
						new String[] { proId });
				while (cursor2.moveToNext()) {
					City city = new City();

					city.setId(Integer.parseInt(cursor2.getString(cursor2.getColumnIndex("id"))));
					city.setProvinceId(Integer.parseInt(cursor2.getString(cursor2.getColumnIndex("province_id"))));
					String tmp = cursor2.getString(cursor2.getColumnIndex("name"));
					city.setName(tmp);
					citys.add(city);

				}
				cursor2.close();

				Province province = new Province();
				province.setId(Integer.parseInt(proId));
				province.setName(proName);
				province.setCitys(citys);
				provinces.add(province);

			}

			localSQLiteDatabase.close();
			cursor.close();
			return provinces;
		}
		return null;

	}

	/**
	 * 根据定位查询城市ID,如果不在数据库表里面，返回北京的城市ID
	 * 
	 * @param cityName
	 * @return cityID
	 */
	public int getCityId(String cityName) {
		SQLiteDatabase localSQLiteDatabase = getCityDB();
		if (localSQLiteDatabase.isOpen()) {

			String sql = "select id from aqi_cities where is_have_aqi=1 and name = ? ";
			Cursor cursor = localSQLiteDatabase.rawQuery(sql, new String[] { cityName });
			cursor.moveToFirst();
			int cityId = -1;
			if (cursor.getCount() > 0) {
				cityId = cursor.getInt(0);
			} else {
				cityId = 18;
			}
			cursor.close();
			localSQLiteDatabase.close();
			return cityId;
		} else {
			return 18;
		}

		// return 0;
	}

	/**
	 * 判断城市是否有AQI信息
	 * 
	 * @param cityName
	 * @return
	 */
	public boolean isHaveAqi(String cityName) {
		SQLiteDatabase localSQLiteDatabase = getCityDB();
		if (localSQLiteDatabase.isOpen()) {

			String sql = "select id from aqi_cities where name = ? and is_have_aqi=1";
			Cursor cursor = localSQLiteDatabase.rawQuery(sql, new String[] { cityName });
			int count = cursor.getCount();

			cursor.close();
			localSQLiteDatabase.close();

			if (count > 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 更具所属省的ID查询所属省市名称
	 * 
	 * @param provinceId
	 * @return
	 */
	public String getProvinceNameById(int provinceId) {
		SQLiteDatabase localSQLiteDatabase = getCityDB();
		if (localSQLiteDatabase.isOpen()) {
			String sql = "select name from aqi_provinces where id = ?";
			Cursor cursor = localSQLiteDatabase.rawQuery(sql, new String[] { String.valueOf(provinceId) });
			cursor.moveToFirst();

			String name = cursor.getString(0);
			cursor.close();
			localSQLiteDatabase.close();
			return name;
		}
		return null;

	}

	/**
	 * 
	 * @param city_longitude_lati
	 * @return
	 */
	public Longlati getJWById(int provinceId) {
		SQLiteDatabase localSQLiteDatabase = getCityDB();
		if (localSQLiteDatabase.isOpen()) {
			String sql = "select jingdu,weidu from aqi_cities where id = ?";
			Cursor cursor = localSQLiteDatabase.rawQuery(sql, new String[] { String.valueOf(provinceId) });
			cursor.moveToFirst();
			Longlati longlati = new Longlati();
			longlati.setLongi(cursor.getDouble(0));
			longlati.setLati(cursor.getDouble(1));
			cursor.close();

			localSQLiteDatabase.close();
			return longlati;
		}
		return null;

	}

	public String selectNameById(String cityId) {
		SQLiteDatabase db = getCityDB();
		String sql = "SELECT name FROM aqi_cities where id =" + cityId;
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		return cursor.getString(0);
	}

	/**
	 * 
	 * @return
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * 
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = context;
	}

}
