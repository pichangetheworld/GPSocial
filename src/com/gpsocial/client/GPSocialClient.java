package com.gpsocial.client;

import com.loopj.android.http.*;

public class GPSocialClient {
  private static final String BASE_URL = "http://54.200.74.117:8080/";

  private static AsyncHttpClient client = new AsyncHttpClient();

  public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//	  System.out.println("GPSocialClient: Getting request from " + getAbsoluteUrl(url));
      client.get(getAbsoluteUrl(url), params, responseHandler);
  }

  public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
      client.post(getAbsoluteUrl(url), params, responseHandler);
  }

  private static String getAbsoluteUrl(String relativeUrl) {
      return BASE_URL + relativeUrl;
  }
}

