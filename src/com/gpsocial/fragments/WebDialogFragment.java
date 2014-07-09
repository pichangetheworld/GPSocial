package com.gpsocial.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.gpsocial.R;

public class WebDialogFragment extends DialogFragment {
	private WebView wv = null;

	private String mUrl = "http://www.google.com";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.web_fragment, container, false);

		wv = (WebView) v.findViewById(R.id.webview);
		wv.loadUrl(mUrl);
		System.out.println("pchan: web dialogfragment view created");

		return v;
	}

	public void setUrl(String url) {
		mUrl = url;
	}
}
