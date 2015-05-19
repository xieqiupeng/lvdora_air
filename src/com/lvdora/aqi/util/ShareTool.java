package com.lvdora.aqi.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

public class ShareTool {
	// 获取指定Activity的截屏，保存到png文件
	private static Bitmap takeScreenShot(Activity activity) {
		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();

		// 获取状态栏高度
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		// 获取屏幕长和高
		WindowManager manager = (WindowManager) activity
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		// 去掉标题栏
		// Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
				- statusBarHeight - 100);
		view.destroyDrawingCache();
		return b;
	}

	// 保存到sdcard
	private static void savePic(Bitmap b, String strFileName) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(strFileName);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 程序入口
	public static void shoot(String path, Activity a) {
		
		ShareTool.savePic(ShareTool.takeScreenShot(a), path);
	}
	/**
	 * 截图分享
	 * 
	 * @param photoUri
	 * @param activity
	 */
	public static void SharePhoto(String photoUri, String strSubject, String strShare,
			Activity activity) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		File file = new File(photoUri);
		shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//		shareIntent.putExtra(Intent.EXTRA_SUBJECT, strSubject);
//		shareIntent.putExtra(Intent.EXTRA_TEXT, strShare);
//		shareIntent.putExtra("Kdescription", strShare);
		shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shareIntent.setType("image/png");
		activity.startActivity(Intent.createChooser(shareIntent, "分享"));
	}
}
