package com.lvdora.aqi.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lvdora.aqi.R;

public class FeedBackActivity extends Activity {

	private Button submitBtn;
	private Button cancelBtn;
	private EditText contentEdit;
	private EditText contactEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_more_suggest);

		initView();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, InputMethodManager.HIDE_NOT_ALWAYS);

	}

	private void initView() {
		this.cancelBtn = (Button) findViewById(R.id.cancel_button);
		this.submitBtn = (Button) findViewById(R.id.submit_button);
		this.contentEdit = (EditText) findViewById(R.id.feedback_content_edit);
		this.contactEdit = (EditText) findViewById(R.id.feedback_contact_edit);
		this.contentEdit.requestFocus();

		this.submitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String content = contentEdit.getText().toString();
				if (content.trim().equals("")) {
					Toast.makeText(FeedBackActivity.this, "反馈信息不能为空", Toast.LENGTH_SHORT).show();

				} else {
					new Thread(new sendThread()).start();
				}

			}
		});

		this.cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

	}

	class sendThread implements Runnable {

		@Override
		public void run() {

			new Thread(new Runnable() {
				public void run() {
					String content = contentEdit.getText().toString();
					String contact = contactEdit.getText().toString();

					Message msg = new Message();
					HttpPost httpRequest = new HttpPost("http://ftssoft.duapp.com/feedback_api.php");
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("code", "5405268"));
					params.add(new BasicNameValuePair("kfz", "deng"));
					params.add(new BasicNameValuePair("xmmc", "AQI"));
					params.add(new BasicNameValuePair("fkr", contact));
					params.add(new BasicNameValuePair("fknr", content));
					try {
						httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
						HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
						if (httpResponse.getStatusLine().getStatusCode() == 200) {
							String strResult = EntityUtils.toString(httpResponse.getEntity());
							msg.obj = strResult;
						} else {
							msg.obj = "Error";
						}
					} catch (ClientProtocolException e) {
						msg.obj = e.getMessage().toString();
						e.printStackTrace();
					} catch (IOException e) {
						msg.obj = e.getMessage().toString();
						e.printStackTrace();
					} catch (Exception e) {
						msg.obj = e.getMessage().toString();
						e.printStackTrace();
					}

					// Log.e("msg", msg.obj.toString());
					feedHandler.sendMessage(msg);
				}
			}).start();

		}

	}

	final Handler feedHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (!msg.obj.equals("Error")) {
				contentEdit.setText("");
				Toast.makeText(FeedBackActivity.this, "发送成功，我们会尽快给您反馈", Toast.LENGTH_SHORT).show();
			}
		}

	};

}
