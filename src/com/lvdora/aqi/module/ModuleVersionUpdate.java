package com.lvdora.aqi.module;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.lvdora.aqi.util.Config;
import com.lvdora.aqi.util.Constant;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.NetworkTool;

/**
 * apk 版本检测与升级模块
 * 
 * @author xqp
 * 
 */
public class ModuleVersionUpdate {

	Activity activity;

	// 版本检测
	private float newVerCode;
	private String newVerName;
	private String updateDetails;
	public ProgressDialog pBar;
	private StringBuffer sb;
	private String downPath;
	
	
	private Handler handler = new Handler();

	public ModuleVersionUpdate(Activity activity) {
		this.activity = activity;
	}

	/**
	 * 检测版本
	 */
	public void updateVersion() {
		Log.e("ModuleVersionUpdate", "版本检测与升级");
		// 判断是否存在网络
		if (NetworkTool.isNetworkConnected(getActivity())) {
			Float vercode = (float) Config.getVerCode(getActivity());
			if (newVerCode > vercode) {
				showUpdateDialog();
			} else {
				Toast.makeText(getActivity(), "当前已是最新版本！", Toast.LENGTH_SHORT).show();
			}
		} else {
			// --------判断存不存在数据库 开始--------
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("网络提示");
			builder.setMessage("更新版本读取需要链接网络。\n请先连接Internet！");
			// builder.setIcon(R.drawable.about);
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int i) {
					getActivity().finish();
				}
			});
			builder.show();
		}
	}

	/**
	 * 打开文件
	 */
	public void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(downPath, Config.UPDATE_SAVENAME)),
				"application/vnd.android.package-archive");
		activity.startActivity(intent);
	}

	/**
	 * 进度条
	 */
	public void down() {
		handler.post(new Runnable() {
			public void run() {
				pBar.cancel();
				update();
			}
		});
	
	}

	/**
	 * 展示更新对话框
	 */
	private void showUpdateDialog() {
		//
		updateDetails();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// 设置内容
		builder.setTitle("是否更新 ?");
		builder.setMessage(sb.toString());
		// 设置确定按钮
		builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				pBar = new ProgressDialog(getActivity());
				pBar.setTitle("正在下载");
				pBar.setMessage("请稍候...");
				pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				// 下载apk文件
				downFile(Constant.DOWNLOAD_URL);
			}
		});
		builder.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// 点击"取消"按钮之后退出程序
				// getActivity().finish();
			}
		});
		// 创建
		Dialog dialog = builder.create();
		// 显示对话框
		dialog.show();
	}

	/**
	 * 文件下载
	 * 
	 * @param url
	 */
	private void downFile(final String url) {
		downPath = DataTool.createFileDir("Download");
		pBar.show();
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File file = new File(downPath, Config.UPDATE_SAVENAME);
						fileOutputStream = new FileOutputStream(file);
						byte[] buf = new byte[1024];
						int ch = -1;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * 版本更新信息
	 */
	private void updateDetails() {
		String verName = Config.getVerName(getActivity());
		sb = new StringBuffer();
		JSONArray details;
		try {
			sb.append("发现新版本:");
			sb.append(newVerName + "\r\n");
			sb.append("当前版本:");
			sb.append(verName + "\r\n");
			sb.append("新版本增加功能:" + "\r\n");
			details = new JSONArray(updateDetails);
			for (int i = 0; i < details.length(); i++) {
				sb.append(details.get(i) + "\r\n");
			}
			/*
			 * sb.append("如不更新则无法使用新功能！" + "\r\n"); sb.append("建议您立即更新！！！");
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Activity getActivity() {
		return this.activity;
	}
}
