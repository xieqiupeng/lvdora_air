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
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.lvdora.aqi.util.Config;
import com.lvdora.aqi.util.Constant;
import com.lvdora.aqi.util.DataTool;
import com.lvdora.aqi.util.NetworkTool;

/**
 * 解决问题：版本检测与apk升级
 * 
 * @1 版本检测
 * @2 apk升级
 * @author xqp
 * 
 */
public class ModuleVersionUpdate {

	private Activity activity;
	private ProgressDialog pBar;
	private String downPath;
	private Handler handler = new Handler();

	// 版本检测参数
	public float newVerCode;
	public String newVerName;
	public String updateDetails;
	public int isUpdate;
	public int about;

	public ModuleVersionUpdate(Activity activity) {
		this.activity = activity;
		getCacheFromSP();
		// 更新版本
		Log.v("ModuleVersionUpdate", "ModuleVersionUpdate");
	}

	/**
	 * 版本检测与升级
	 */
	public void updateVersion() {
		Log.e("ModuleVersionUpdate", "updateVersion");
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
	 * 两种升级方式
	 */
	public void updateChoose() {
		Log.e("ModuleVersionUpdate", "updateChoose");
		// 当前apk版本
		Float vercode = (float) Config.getVerCode(activity);
		// 两种升级方式
		if (newVerCode > vercode) {
			if (isUpdate == 2) {
				showMustUpdateDialog();
			}
			//
			else if (isUpdate == 1) {
				showUpdateDialog();
			}
		}
	}

	/**
	 * 必须升级
	 */
	public void showMustUpdateDialog() {
		Log.e("ModuleVersionUpdate", "showMustUpdateDialog");
		// 版本更新信息
		StringBuffer sb = updateDetails();
		// 提示更新对话框
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("立即更新 ");
		builder.setMessage(sb.toString());
		// 设置确定按钮
		builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
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
		// 创建,显示对话框
		Dialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * 可选升级
	 */
	public void showUpdateDialog() {
		Log.e("ModuleVersionUpdate", "showUpdateDialog");
		// 版本更新信息
		StringBuffer sb = updateDetails();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
		// 创建，显示对话框
		Dialog dialog = builder.create();
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
	 * 进度条
	 */
	private void down() {
		handler.post(new Runnable() {
			public void run() {
				pBar.cancel();
				updateApplication();
			}
		});
	}

	/**
	 * 版本升级
	 */
	private void updateApplication() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		File apk = new File(downPath, Config.UPDATE_SAVENAME);
		String type = "application/vnd.android.package-archive";
		intent.setDataAndType(Uri.fromFile(apk), type);
		activity.startActivity(intent);
	}

	// getCacheFromSP
	private void getCacheFromSP() {
		SharedPreferences sp;
		sp = activity.getSharedPreferences("verdata", 0);
		updateDetails = sp.getString("updatedetails", "");
		newVerName = sp.getString("verName", "");
		newVerCode = Float.parseFloat(sp.getString("newVerCode", "1"));
		isUpdate = sp.getInt("isUpdate", 0);
		about = Integer.parseInt(sp.getString("about", "0"));
	}

	/**
	 * 版本更新信息
	 */
	private StringBuffer updateDetails() {
		String verName = Config.getVerName(getActivity());
		StringBuffer sb = new StringBuffer();
		sb.append("发现新版本:");
		sb.append(newVerName + "\r\n");
		sb.append("当前版本:");
		sb.append(verName + "\r\n");
		sb.append("新版本增加功能:" + "\r\n");
		//
		JSONArray details;
		try {
			details = new JSONArray(updateDetails);
			for (int i = 0; i < details.length(); i++) {
				sb.append(details.get(i) + "\r\n");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// sb.append("如不更新则无法使用新功能！" + "\r\n");
		// sb.append("建议您立即更新！！！");
		return sb;
	}

	private Activity getActivity() {
		return this.activity;
	}
}