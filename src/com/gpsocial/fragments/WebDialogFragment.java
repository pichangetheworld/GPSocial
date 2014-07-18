package com.gpsocial.fragments;

import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gpsocial.R;
import com.gpsocial.SigninActivity;

public class WebDialogFragment extends DialogFragment {
	private WebView wv = null;

	private String mUrl = "http://www.google.com";

	private static class TwitterWebViewClient extends WebViewClient {
		WebDialogFragment mFragment = null;
		
		public TwitterWebViewClient(WebDialogFragment fragment) {
			mFragment = fragment;
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// what is the value of url
			if (url.startsWith(SigninActivity.TWITTER_CALLBACK_URL)) {
				Uri uri = Uri.parse(url);
				((SigninActivity) mFragment.getActivity()).onSuccessfulTwitterSignin(uri);
				mFragment.dismiss();
				return true;
			}
			return true;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.web_fragment, container, false);

		wv = (WebView) v.findViewById(R.id.webview);
		wv.setWebViewClient(new TwitterWebViewClient(this));
		wv.loadUrl(mUrl);
		
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		return v;
	}

	public void setUrl(String url) {
		mUrl = url;
	}
}
