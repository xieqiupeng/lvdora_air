package com.lvdora.guid.sqllite;

/**
 * DBConnection
 * create table guid and cityaqi
 * @author Eagle
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteHelper extends SQLiteOpenHelper {

	private static SqliteHelper mInstance = null;
	private Context context;
	private String sql = "CREATE TABLE IF NOT EXISTS cityaqi("
			+ " id integer primary key autoincrement,"
			+ " num integer unique,"
			+ " cityId varchar(10) unique,"
			+ " cityName varchar(10),"
			+ " aqi varchar(10),pm25 varchar(10),pm25_aqi varchar(10),pm10 varchar(10),pm10_aqi varchar(10),so2 varchar(10),so2_aqi varchar(10),no2 varchar(10),no2_aqi varchar(10),co varchar(10),co_aqi varchar(10),o3 varchar(10),o3_aqi varchar(10),aqi_pubtime varchar(10),usa_aqi varchar(10),usa_pm25 varchar(10),us_pubtime varchar(10),temp_now varchar(10),wind varchar(20),weather0 varchar(20),weather_icon0 varchar(10),weather0_pubtime varchar(20),hightTemp0 varchar(10),lowTemp0 varchar(10),windForce0 varchar(10),weather1 varchar(20),weather_icon1 varchar(10),weather1_pubtime varchar(10),hightTemp1 varchar(10),lowTemp1 varchar(10),windForce1 varchar(10),weather2 varchar(10),weather_icon2 varchar(10),weather2_pubtime varchar(20),hightTemp2 varchar(10),lowTemp2 varchar(10),windForce2 varchar(10),weather3 varchar(10),weather_icon3 varchar(10),weather3_pubtime varchar(20),hightTemp3 varchar(10),lowTemp3 varchar(10),windForce3 varchar(10),weather4 varchar(10),weather_icon4 varchar(10),weather4_pubtime varchar(20),hightTemp4 varchar(10),lowTemp4 varchar(10),windForce4 varchar(10),tempCurrentPubtime varchar(10))";

	public static SqliteHelper getInstance(Context context, String dbName, CursorFactory factory, int version) {
		/**
		 * use the application context as suggested by CommonsWare. this will
		 * ensure that you dont accidentally leak an Activitys context (see this
		 * article for more information:
		 * http://android-developers.blogspot.nl/2009
		 * /01/avoiding-memory-leaks.html)
		 */
		if (mInstance == null) {
			mInstance = new SqliteHelper(context.getApplicationContext(), dbName, factory, version);
		}
		return mInstance;
	}

	private SqliteHelper(Context context, String dbName, CursorFactory factory, int version) {
		super(context, dbName, factory, version);
		this.context = context;
	}

	// 创建表
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS guid(id integer primary key autoincrement, issue varchar(10), used integer)");
		db.execSQL(sql);
	}

	// 更新表
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 删除表
		db.execSQL("DROP TABLE IF EXISTS guid");
		db.execSQL("DROP TABLE IF EXISTS cityaqi");
		// 重建表
		db.execSQL("CREATE TABLE IF NOT EXISTS guid(id integer primary key autoincrement, issue varchar(10), used integer)");
		db.execSQL(sql);
	}
}
