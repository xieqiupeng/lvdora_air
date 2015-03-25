package com.lvdora.guid.sqllite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * DBOperation DB lvdora.db Table guid
 * 
 * @author Eagle
 * 
 */
public class DataHelper {
	// 数据库名称
	public static String DB_NAME = "lvdora.db";
	// 数据库版本
	public static int DB_VERSION = 3;
	// 表名称
	public static final String TB_NAME = "guid";

	private SQLiteDatabase db;
	private SqliteHelper dbHelper = null;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public DataHelper(Context context, String filePath) {
		if (dbHelper == null) {
			dbHelper = SqliteHelper.getInstance(context, DB_NAME, null, DB_VERSION);
		}
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * selectAll
	 * 
	 * @return
	 */
	public List<guidUsedInfo> getUserList() {
		List<guidUsedInfo> menuList = new ArrayList<guidUsedInfo>();
		Cursor cursor = db.query(TB_NAME, null, null, null, null, null, "id ASC");
		cursor.moveToFirst();

		while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
			guidUsedInfo guidUsed = new guidUsedInfo();
			guidUsed.setId(cursor.getInt(0));
			guidUsed.setIssue(cursor.getString(1));
			guidUsed.setUsed(cursor.getInt(2));
			menuList.add(guidUsed);
			cursor.moveToNext();
		}
		cursor.close();
		return menuList;
	}

	/**
	 * 获得总条数
	 * 
	 * @return
	 */
	public int length() {
		Cursor cursor = db.query(TB_NAME, null, null, null, null, null, "issue ASC");
		cursor.moveToFirst();
		return cursor.getCount();
	}

	/**
	 * 判断menu表中的是否包含某个list的记录
	 * 
	 * @param issue
	 * @return
	 */
	public Boolean haveMenuList(String issue) {
		Boolean b = false;
		Cursor cursor = db.query(TB_NAME, null, guidUsedInfo.ISSUE + "='" + issue + "' AND used=1", null, null, null,
				null);
		b = cursor.moveToFirst();
		cursor.close();
		return b;
	}

	/**
	 * 添加menu表的记录
	 * 
	 * @param menu
	 * @return
	 */
	public Long saveMenuInfo(guidUsedInfo menu) {
		ContentValues values = new ContentValues();
		values.put(guidUsedInfo.ISSUE, menu.getIssue());
		values.put(guidUsedInfo.USED, menu.getUsed());
		Long uid = db.insert(TB_NAME, guidUsedInfo.ID, values);
		return uid;
	}

	/**
	 * 指定表统计总数
	 * 
	 * @param tabName
	 * @return
	 */
	public int length(String tabName) {
		Cursor cursor = db.query(tabName, null, null, null, null, null, "issue ASC");
		cursor.moveToFirst();
		return cursor.getCount();
	}

	/**
	 * 关闭
	 */
	public void closeDB() {
		db.close();
		dbHelper.close();
	}
}
