package com.lvdora.aqi.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

/**
 * 外部数据库导入功能
 * 
 * @city.db
 * 
 * @author xqp
 * 
 */
public class DBManager {
	private final int BUFFER_SIZE = 10000;
	// 保存的数据库文件名
	public static final String DB_NAME = "city.db";
	public static final String PACKAGE_NAME = "com.lvdora.aqi";
	// 在手机里存放数据库的位置
	public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME;
	public static final String DB_TOTAL_PATH = DB_PATH + "/" + DB_NAME;

	private SQLiteDatabase database;
	private Context context;

	public DBManager(Context context) {
		this.context = context;
	}

	public void openDatabase() {
		this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
	}

	// 将数据库从asset考入内部存储
	private SQLiteDatabase openDatabase(String dbfile) {
		try {
			if (!(new File(dbfile).exists())) {
				InputStream is = this.context.getAssets().open("city.db"); // 欲导入的数据库
				FileOutputStream fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
			return db;
		} catch (FileNotFoundException e) {
			Log.e("Database", "File not found");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("Database", "IO exception");
			e.printStackTrace();
		}
		return null;
	}

	public void closeDatabase() {
		this.database.close();
	}
}
