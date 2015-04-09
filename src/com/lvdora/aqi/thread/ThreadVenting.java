package com.lvdora.aqi.thread;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lvdora.aqi.R;
import com.lvdora.aqi.model.City;
import com.lvdora.aqi.model.SpitContent;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.DateUtil;
import com.lvdora.aqi.util.EnAndDecryption;
import com.lvdora.aqi.view.HomeActivity;

public class ThreadVenting extends HomeActivity {

	private FragmentActivity activity;
	// TODO 吐槽信息
	private LinearLayout spitLayout;
	private ImageView spitPublish;
	private ImageView spitOff;
	private SpitContent mySpit;
	private TextView spitFirst;
	private TextView spitSecond;
	private TextView spitThird;
	private int nowCityId;
	private List<SpitContent> cityContentList = new ArrayList<SpitContent>();
	private SharedPreferences sp;

	private long publishTime;
	private int pubCount = 0;
	private boolean onOrOff;
	//
	private List<City> citys = new ArrayList<City>();

	public ThreadVenting(FragmentActivity activity, List<City> citys) {
		Log.v("ThreadVenting", "ThreadVenting");
		this.activity = activity;
		this.citys = citys;
	}

	// 吐槽更新数据线程
	public class PublishSpitThread implements Runnable {
		@Override
		public void run() {
			sp = activity.getSharedPreferences("spitdata", 0);
			cityContentList.clear();
			String id = sp.getString("spit_" + citys.get(currentIndexOut).getId(), "");
			cityContentList = EnAndDecryption.String2SpitContentList(id);
			while (true) {
				long newTime = DateUtil.getTime(cityContentList.get(0).getPubTime());
				if (newTime - publishTime > 0) {
					//TODO
					//mHandler.sendEmptyMessageDelayed(SPIT_PUBLISH, 0);
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void findView(View view) {
		this.spitLayout = (LinearLayout) view.findViewById(R.id.spit_layout);
		this.spitPublish = (ImageView) view.findViewById(R.id.spit_publish);
		this.spitOff = (ImageView) view.findViewById(R.id.spit_off);
		this.spitFirst = (TextView) view.findViewById(R.id.spit1);
		this.spitSecond = (TextView) view.findViewById(R.id.spit2);
		this.spitThird = (TextView) view.findViewById(R.id.spit3);
	}

	/*
	 * 初始化吐槽：打开或者关闭
	 */
	public void initCitySpit(List<View> views, List<City> citys) {
		this.citys = citys;
		for (int i = 0; i < views.size(); i++) {
			try {
				nowCityId = citys.get(i).getId();
				cityContentList.clear();
				cityContentList = EnAndDecryption.String2SpitContentList(sp.getString("spit_" + nowCityId, ""));
			} catch (Exception e) {
				e.printStackTrace();
			}
			View view = views.get(i);

			this.spitLayout = (LinearLayout) view.findViewById(R.id.spit_layout);
			this.spitPublish = (ImageView) view.findViewById(R.id.spit_publish);
			this.spitOff = (ImageView) view.findViewById(R.id.spit_off);
			this.spitFirst = (TextView) view.findViewById(R.id.spit1);
			this.spitSecond = (TextView) view.findViewById(R.id.spit2);
			this.spitThird = (TextView) view.findViewById(R.id.spit3);
			// this.line = (View)views.get(i).findViewById(R.id.spit_line);

			if (onOrOff) {
				spitLayout.setVisibility(View.VISIBLE);
				spitPublish.setVisibility(View.VISIBLE);
				spitPublish.setOnClickListener(this);
				spitOff.setOnClickListener(this);
				spitOff.setVisibility(View.VISIBLE);
				spitFirst.setVisibility(View.VISIBLE);
				spitSecond.setVisibility(View.VISIBLE);
				spitThird.setVisibility(View.VISIBLE);
				// line.setVisibility(View.VISIBLE);
				spitFirst.setText("                                                      ");
				spitSecond.setText("                                                                ");
				spitThird.setText("                                                 ");
				if (cityContentList != null) {
					for (int j = 0; j < cityContentList.size(); j++) {
						if (j % 3 == 0) {
							spitFirst.append(cityContentList.get(j).getContent() + "             ");
						}
						if (j % 3 == 1) {
							spitSecond.append(cityContentList.get(j).getContent() + "             ");
						}
						if (j % 3 == 2) {
							spitThird.append(cityContentList.get(j).getContent() + "             ");
						}
					}
					spitFirst.setTextSize(23);
					spitSecond.setTextSize(20);
					spitThird.setTextSize(18);
				}
			} else {
				closeSpit();
			}
		}
		recordSpitTime();
	}

	/**
	 * 显示吐槽内容
	 */
	public void showSpitContent(List<View> views) {
		// 获取缓存数据
		for (int i = 0; i < views.size(); i++) {
			if (i == currentIndexOut) {
				//
				nowCityId = citys.get(i).getId();
				sp = activity.getSharedPreferences("spitdata", 0);
				cityContentList.clear();
				cityContentList = EnAndDecryption.String2SpitContentList(sp.getString("spit_" + nowCityId, ""));

				// 初始化组件
				View view = views.get(currentIndexOut);
				this.spitLayout = (LinearLayout) view.findViewById(R.id.spit_layout);
				this.spitPublish = (ImageView) view.findViewById(R.id.spit_publish);
				this.spitOff = (ImageView) view.findViewById(R.id.spit_off);
				this.spitFirst = (TextView) view.findViewById(R.id.spit1);
				this.spitSecond = (TextView) view.findViewById(R.id.spit2);
				this.spitThird = (TextView) view.findViewById(R.id.spit3);
				// this.line = (View) view.findViewById(R.id.spit_line);

				if (onOrOff) {
					spitLayout.setVisibility(View.VISIBLE);
					spitPublish.setVisibility(View.VISIBLE);
					spitPublish.setOnClickListener(this);
					spitOff.setVisibility(View.VISIBLE);
					spitOff.setOnClickListener(this);
					spitFirst.setVisibility(View.VISIBLE);
					spitSecond.setVisibility(View.VISIBLE);
					spitThird.setVisibility(View.VISIBLE);
					// line.setVisibility(View.VISIBLE);

					spitFirst.setText("                                                      ");
					spitSecond.setText("                                                                ");
					spitThird.setText("                                                 ");

					if (cityContentList != null) {
						int length = cityContentList.size();
						for (int j = 0; j < length; j++) {
							if (mySpit != null) {
								if (length > 8) {
									// if (j == 0 && mySpit != null) {
									if (pubCount % 3 == 0) {
										spitFirst.setText(cityContentList.get(length - 1).getContent() + "           "
												+ cityContentList.get(length - 2).getContent() + "           "
												+ cityContentList.get(length - 3).getContent() + "         "
												+ mySpit.getContent() + "             ");
										spitSecond.setText(cityContentList.get(length - 4).getContent() + "           "
												+ cityContentList.get(length - 5).getContent() + "           "
												+ cityContentList.get(length - 6).getContent() + "         ");
										spitThird.setText(cityContentList.get(length - 7).getContent() + "           "
												+ cityContentList.get(length - 8).getContent() + "           "
												+ cityContentList.get(length - 9).getContent() + "         ");
									}

									if (pubCount % 3 == 1) {
										spitFirst.setText(cityContentList.get(length - 1).getContent() + "           "
												+ cityContentList.get(length - 2).getContent() + "           "
												+ cityContentList.get(length - 3).getContent() + "         ");
										spitSecond.setText(cityContentList.get(length - 4).getContent() + "           "
												+ cityContentList.get(length - 5).getContent() + "           "
												+ cityContentList.get(length - 6).getContent() + "         "
												+ mySpit.getContent() + "             ");
										spitThird.setText(cityContentList.get(length - 7).getContent() + "           "
												+ cityContentList.get(length - 8).getContent() + "           "
												+ cityContentList.get(length - 9).getContent() + "         ");
									}

									if (pubCount % 3 == 2) {
										spitFirst.setText(cityContentList.get(length - 1).getContent() + "           "
												+ cityContentList.get(length - 2).getContent() + "           "
												+ cityContentList.get(length - 3).getContent() + "         ");
										spitSecond.setText(cityContentList.get(length - 4).getContent() + "           "
												+ cityContentList.get(length - 5).getContent() + "           "
												+ cityContentList.get(length - 6).getContent() + "         ");
										spitThird.setText(cityContentList.get(length - 7).getContent() + "           "
												+ cityContentList.get(length - 8).getContent() + "           "
												+ cityContentList.get(length - 9).getContent() + "         "
												+ mySpit.getContent() + "             ");
									}
								}
								//
								else if (length > 2 && length <= 8) {
									if (pubCount % 3 == 0) {
										spitFirst.setText("           " + cityContentList.get(length - 1).getContent()
												+ "           " + cityContentList.get(length - 2).getContent()
												+ "         " + mySpit.getContent() + "             ");
									}
									if (pubCount % 3 == 1) {
										spitSecond.setText("           " + cityContentList.get(length - 1).getContent()
												+ "           " + cityContentList.get(length - 2).getContent()
												+ "         " + mySpit.getContent() + "             ");
									}
									if (pubCount % 3 == 2) {
										spitThird.setText("           " + cityContentList.get(length - 1).getContent()
												+ "           " + cityContentList.get(length - 2).getContent()
												+ "         " + mySpit.getContent() + "             ");
									}
								} else {
									// 取全国城市数据
									sp = activity.getSharedPreferences("spitdata", 0);
									cityContentList.clear();
									try {
										cityContentList = EnAndDecryption.String2SpitContentList(sp.getString(
												"spitjson", ""));
										if (cityContentList != null) {
											if (j % 3 == 0) {
												spitFirst.append(cityContentList.get(j).getContent() + "             ");
												// spitSecond.setText(" ");
												// spitThird.setText(" ");
											}
											if (j % 3 == 1) {
												spitSecond
														.append(cityContentList.get(j).getContent() + "             ");
												// spitFirst.setText(" ");
												// spitThird.setText(" ");
											}
											if (j % 3 == 2) {
												spitThird.append(cityContentList.get(j).getContent() + "             ");
												// spitFirst.setText(" ");
												// spitSecond.setText(" ");
											}
											// spitSecond.setText("           "
											// + mySpit.getContent() +
											// "             ");
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							//
							else {
								// 没有发表内容的情况
								if (j % 3 == 0) {
									spitFirst.append(cityContentList.get(j).getContent() + "             ");
									spitSecond.setText(" ");
									spitThird.setText(" ");
								}
								if (j % 3 == 1) {
									spitSecond.append(cityContentList.get(j).getContent() + "             ");
									spitFirst.setText(" ");
									spitThird.setText(" ");
								}
								if (j % 3 == 2) {
									spitThird.append(cityContentList.get(j).getContent() + "             ");
									spitFirst.setText(" ");
									spitSecond.setText(" ");
								}
							}
						}
						/*
						 * spitFirst.setTextSize(23);
						 * spitSecond.setTextSize(20);
						 * spitThird.setTextSize(24);
						 */
					}
				} else {
					closeSpit();
				}
			}
		}
		recordSpitTime();
	}

	/**
	 * 发布吐槽信息
	 */
	public void publishSpit(final List<View> views, final String longtitude, final String latitude) {
		final EditText publish = new EditText(activity);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("吐槽天气").setIcon(R.drawable.publish).setView(publish);
		builder.setPositiveButton("发布", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String content = publish.getText().toString();
				if (content.equals("")) {
					// 吐槽内容不能为空
					Toast.makeText(activity, R.string.spit, 200).show();
				}
				//
				else {
					if (0 < content.length() && content.length() <= 30) {
						pubCount = pubCount + 1;
						Date nowDate = new Date();
						String myTime = DateUtil.Date2String(nowDate);
						mySpit = new SpitContent();
						mySpit.setPubTime(myTime);
						mySpit.setContent(publish.getText().toString());
						if (pubCount == 1) {
							for (int k = 0; k < views.size(); k++) {
								// 判断到这里，和initSpit 相同
								if (k == currentIndexOut) {
									Toast.makeText(activity, "吐槽成功", Toast.LENGTH_SHORT).show();
									// 重新初始化组件
									spitLayout = (LinearLayout) views.get(currentIndexOut).findViewById(
											R.id.spit_layout);
									spitPublish = (ImageView) views.get(currentIndexOut)
											.findViewById(R.id.spit_publish);
									spitOff = (ImageView) views.get(currentIndexOut).findViewById(R.id.spit_off);
									spitFirst = (TextView) views.get(currentIndexOut).findViewById(R.id.spit1);
									spitSecond = (TextView) views.get(currentIndexOut).findViewById(R.id.spit2);
									spitThird = (TextView) views.get(currentIndexOut).findViewById(R.id.spit3);
									// line =
									// (View)views.get(currentIndexOut).findViewById(R.id.spit_line);
									// 初始化后赋值，组合数据
									spitFirst.setText("                " + mySpit.getContent() + "           ");
									spitSecond.setText("                " + cityContentList.get(0).getContent()
											+ "         ");
									spitThird.setText("                " + cityContentList.get(1).getContent()
											+ "         ");
									for (int i = 0; i < cityContentList.size(); i++) {
										if (i % 3 == 0) {
											spitFirst.append(cityContentList.get(i).getContent() + "             ");
										}
										if (i % 3 == 1) {
											spitSecond.append(cityContentList.get(i).getContent() + "             ");
										}
										if (i % 3 == 2) {
											spitThird.append(cityContentList.get(i).getContent() + "             ");
										}
									}
								}
							}
						}
						DataTool.getCitySpitData(activity, String.valueOf(citys.get(currentIndexOut).getId()), content,
								longtitude, latitude);
						new Thread(new PublishSpitThread()).start();
					} else {
						Toast.makeText(activity, R.string.spitlimit, 200).show();
					}
				}
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	/**
	 * 关闭吐槽
	 */
	public void closeSpit() {
		Toast.makeText(activity, R.string.spitOff, 200).show();
		spitLayout.setVisibility(View.INVISIBLE);
		spitPublish.setVisibility(View.INVISIBLE);
		spitOff.setVisibility(View.INVISIBLE);
		spitFirst.setVisibility(View.INVISIBLE);
		spitSecond.setVisibility(View.INVISIBLE);
		spitThird.setVisibility(View.INVISIBLE);
		// line.setVisibility(View.INVISIBLE);
	}

	/**
	 * 记录吐槽内容发布的最新时间，用于时间比较，判断是否有新内容需要更新
	 */
	private void recordSpitTime() {
		sp = activity.getSharedPreferences("spitdata", 0);
		cityContentList.clear();
		Log.v("ThreadVenting", currentIndexOut+"");
		String spit = sp.getString("spit_" + citys.get(currentIndexOut).getId(), "");
		cityContentList = EnAndDecryption.String2SpitContentList(spit);
		if (cityContentList != null) {
			publishTime = DateUtil.getTime(cityContentList.get(0).getPubTime());
			Log.e("aqi", "pubtime：" + publishTime);
		} else {
			publishTime = System.currentTimeMillis() + 1000;
		}
	}
}