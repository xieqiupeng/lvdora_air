package com.lvdora.aqi.view;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.ViewFlipper;

import com.lvdora.aqi.R;
import com.lvdora.aqi.db.DBManager;
import com.lvdora.aqi.util.Config;
import com.lvdora.guid.sqllite.DataHelper;
import com.lvdora.aqi.dao.CityAqiDao;
import com.lvdora.guid.sqllite.guidUsedInfo;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * 首页
 * 
 * @author
 * 
 */
public class LvdoraGuide extends Activity implements OnGestureListener {

	private CityAqiDao cityAqiDB;
	private DBManager dbManager;
	private ViewFlipper viewFlipper;
	private GestureDetector mGestureDetector;
	private int counter = 0;
	private PointImage currentPoint;
	private LinearLayout layout_point;
	private DataHelper db;
	private DisplayMetrics dm;
	// 图片资源id
	private Integer[] guideId = new Integer[] {
			//
			R.drawable.app_tips_1, //
			R.drawable.app_tips_2,//
			R.drawable.app_tips_3, //
			R.drawable.app_tips_4 //
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 去掉标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 去掉信息栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 绘制界面
		setContentView(R.layout.app_guide);

		// 初始化腾讯bugly
		Context appContext = this.getApplicationContext();
		String appId = "900001956"; // 上Bugly(bugly.qq.com)注册产品获取的AppId
		boolean isDebug = true; // true代表App处于调试阶段，false代表App发布阶段
		CrashReport.initCrashReport(appContext, appId, isDebug); // 初始化SDK
		CrashReport.setUserId("xieqiupeng");

		// 模拟Java Crash方法：
		// CrashReport.testJavaCrash ();
		// 模拟Native Crash方法：
		// CrashReport.testNativeCrash();

		// 此处取得屏幕的分辨率
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// 数据库初始化
		File file = new File(DBManager.DB_PATH + "/" + DBManager.DB_NAME);
		if (!file.exists()) {
			dbManager = new DBManager(LvdoraGuide.this);
			dbManager.openDatabase();
		}

		// 关闭所有数据库
		cityAqiDB = new CityAqiDao(LvdoraGuide.this, "");
		cityAqiDB.closeAll();

		initView();
	}

	/**
	 * 
	 */
	private void initView() {
		// 初次启动标识符
		boolean bNotShowPic = false;

		// 判断是否初次启动
		db = new DataHelper(getApplicationContext(), "");
		int ver = Config.getVerCode(getApplicationContext(), "com.lvdora.app.aqi");
		if (db.haveMenuList(Integer.toString(ver))) {
			bNotShowPic = true;
		}

		// 初次启动进入logoActivity
		if (bNotShowPic) {
			startActivity(new Intent(getApplication(), LogoActivity.class));
			this.finish();
		}

		// 手势
		mGestureDetector = new GestureDetector(this);
		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		layout_point = (LinearLayout) findViewById(R.id.layout_point);
		for (int i = 0; i < guideId.length; i++) {
			GuideImage guideImage = new GuideImage(getApplicationContext(), guideId[i]);
			viewFlipper.addView(guideImage);
		}
		for (int i = 0; i < guideId.length + 1; i++) {
			PointImage pointImage = new PointImage(getApplicationContext());
			if (i == 0) {
				pointImage.choose();
			}
			layout_point.addView(pointImage);
		}
	}

	/**
	 * 
	 */
	private void setPointChoose() {
		for (int i = 0; i < layout_point.getChildCount(); i++) {
			currentPoint = (PointImage) layout_point.getChildAt(i);
			currentPoint.normal();
		}
		currentPoint = (PointImage) layout_point.getChildAt(counter);
		currentPoint.choose();
	}

	// 按下事件
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	// 滑动手势事件
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// 向下滑动
		if (e1.getX() - e2.getX() > 80) {
			viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
			viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
			// 显示下个图片
			if (counter < guideId.length - 1) {
				viewFlipper.showNext();
				counter++;
				setPointChoose();
			}
			// 进入主界面
			else {
				// 存guid
				guidUsedInfo menu = new guidUsedInfo();
				int ver = Config.getVerCode(getApplicationContext(), "com.lvdora.app.aqi");
				menu.setIssue(Integer.toString(ver));
				menu.setUsed(1);
				db.saveMenuInfo(menu);
				// 进logo
				startActivity(new Intent(getApplication(), LogoActivity.class));
				this.finish();
			}
			return true;
		}
		// 向上滑动
		else if (e1.getX() - e2.getX() < -80) {
			viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
			viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
			if (counter > 0) {
				viewFlipper.showPrevious();
				counter--;
			}
			setPointChoose();
			return true;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.mGestureDetector.onTouchEvent(event);
	}

	// 长按事件
	@Override
	public void onLongPress(MotionEvent e) {
	}

	// 在屏幕上拖动事件
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	// down事件发生而move或者up还没发生前触发该事件
	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	/**
	 * 翻页指示器
	 */
	class PointImage extends ImageView {
		LinearLayout.LayoutParams margins = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		public PointImage(Context context) {
			super(context);
			margins.setMargins(20, 0, 0, 0);
			setLayoutParams(margins);
			normal();
		}

		public void choose() {
			setBackgroundResource(R.drawable.app_tips_point_choose);
		}

		public void normal() {
			setBackgroundResource(R.drawable.app_tips_point_normal);
		}
	}

	// 自定义居中imageView控件
	class GuideImage extends ImageView {
		public GuideImage(Context context, Integer resId) {
			// 调用父类的构造方法
			super(context);
			setImageResource(resId);
			setScaleType(ScaleType.FIT_CENTER);
		}
	}
}