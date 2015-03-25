package com.lvdora.aqi.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;

import com.lvdora.aqi.R;
import com.lvdora.aqi.util.ExitTool;
import com.lvdora.aqi.util.WebViewContentClient;

public class AirQualityStandardsActivity extends Activity {
	private WebView airQualityStandards;
	private ImageView backView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_more_air_quality_standards);
		ExitTool.activityList.add(AirQualityStandardsActivity.this);
		backView = (ImageView) findViewById(R.id.btn_back);
		backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		airQualityStandards = (WebView) findViewById(R.id.air_quality_standards_webview);
		airQualityStandards.getSettings().setJavaScriptEnabled(true);
		airQualityStandards
				.loadUrl("file:///android_asset/airqualitystandards.html");
		airQualityStandards.setWebViewClient(new WebViewContentClient());
	}

}
