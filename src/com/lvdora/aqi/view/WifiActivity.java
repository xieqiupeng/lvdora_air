package com.lvdora.aqi.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.lvdora.aqi.R;
import com.airm2m.sdk.smartlink;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiActivity extends Activity {
	private EditText txt_ssid;
	private EditText txt_password;
	private Button btn_smartlink;
	private Button btn_get_wifi_name;
	private smartlink smartlink;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi);
		smartlink = new smartlink();
		btn_get_wifi_name = (Button) findViewById(R.id.btn_get_wifi_name);
		btn_smartlink = (Button) findViewById(R.id.btn_smartlink);
		txt_ssid = (EditText) findViewById(R.id.txt_ssid);
		txt_ssid.setText(this.getCurrentWifiName());
		txt_password = (EditText) findViewById(R.id.txt_password);
		btn_smartlink.setText("连接");
		btn_get_wifi_name.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				txt_ssid.setText(WifiActivity.this.getCurrentWifiName());
			}
		});

		btn_smartlink.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int result;

				if (btn_smartlink.getText().toString() == "连接") {
					if (txt_ssid.getText().toString().equals("")) {
						new AlertDialog.Builder(WifiActivity.this).setTitle("错误").setMessage("请输入SSID").show();
						return;
					}

					btn_smartlink.setText("STOP");
					result = smartlink.open(txt_ssid.getText().toString(), txt_password.getText().toString());
					System.out.println("send result " + result);
				} else {
					btn_smartlink.setText("连接");
					smartlink.close();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();

		// if (id == R.id.action_settings) {
		// return true;
		// }

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 取得本机现在使用的wifi名称 如果没有开wifi，显示为空。
	 * 
	 * @return string result 返回wifi名称
	 */
	public String getCurrentWifiName() {
		String result;
		WifiManager wifiMgr = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		int wifiState = wifiMgr.getWifiState();
		if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
			WifiInfo info = wifiMgr.getConnectionInfo();
			String wifiId = info != null ? info.getSSID() : null;
			result = wifiId;
			result = result.substring(1, result.length() - 1);
			if (result.equals("unknown ssid")) {
				result = "";
			}
		} else {
			result = "您的wifi未打开";
		}

		return result;
	}
}
