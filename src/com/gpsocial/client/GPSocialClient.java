package com.gpsocial.client;

import org.apache.http.HttpEntity;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GPSocialClient {
	private static final String BASE_URL = "http://54.200.74.117:8080/";
	
	private static AsyncHttpClient client = new AsyncHttpClient();
	
	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(Context context, String url, HttpEntity e,
			AsyncHttpResponseHandler responseHandler) {
		System.out.println("pchan: posting entity " + e);
		client.post(context, getAbsoluteUrl(url), e, "application/json", responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
}
