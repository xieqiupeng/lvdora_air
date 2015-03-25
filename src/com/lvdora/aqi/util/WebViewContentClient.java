package com.lvdora.aqi.util;

import android.webkit.WebView;
import android.webkit.WebViewClient;

// Web视图
public class WebViewContentClient extends WebViewClient {
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		view.loadUrl(url);
		return true;
	}
}