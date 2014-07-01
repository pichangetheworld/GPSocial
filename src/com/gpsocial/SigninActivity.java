package com.gpsocial;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.gson.Gson;
import com.gpsocial.client.GPSocialClient;
import com.loopj.android.http.TextHttpResponseHandler;

public class SigninActivity extends Activity {
	private UiLifecycleHelper uiHelper;

	// callback for twitter
	private static final String TWITTER_CALLBACK_URL = "oauth://gpsocial";
	private static final String TWITTER_CONSUMER_KEY = "NG6J4g9BuMRy9Wxovp9y2t56s";
	private static final String TWITTER_CONSUMER_SECRET = "dynXse9yobluRM2y2TFV61conx6r7d5kiw3iOQZUS6hUDqv6WM";

	// Twitter oauth urls
	private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";

	// Twitter
	private static Twitter twitter;
	private static RequestToken requestToken;

	// Shared Preferences
	private static SharedPreferences mSharedPreferences;

	// Preference Constants
	private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	private static final String PREF_KEY_LOGIN = "gpsocialSignedIn";

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			// Session state was changed
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		// Shared Preferences
        mSharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
		
		setContentView(R.layout.activity_signin);

		// If this was redirected from the Twitter page
		// Parse the uri for the OAuth Verifier
		if (mSharedPreferences.getBoolean(PREF_KEY_LOGIN, false)) {
			startActivity(new Intent(SigninActivity.this, MainActivity.class));
		} else {
			Uri uri = getIntent().getData();
			if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
				// oAuth verifier
				String verifier = uri
						.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

				new AsyncTask<String, Void, AccessToken>() {
					@Override
					protected AccessToken doInBackground(String... params) {
						AccessToken accessToken = null;
						try {
							// Get the access token
							accessToken = twitter.getOAuthAccessToken(
									requestToken, params[0]);

							// Shared Preferences
							Editor e = mSharedPreferences.edit();

							// After getting access token, access token secret
							// store them in application preferences
							e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
							e.putString(PREF_KEY_OAUTH_SECRET,
									accessToken.getTokenSecret());
							// Store login status - true
							e.putBoolean(PREF_KEY_LOGIN, true);
							e.commit(); // save changes

							Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
						} catch (TwitterException e) {
							// Check log for login errors
							Log.e("Twitter Login Error", "> " + e.getMessage());
						}
						return accessToken;
					}
					
					@Override
			        protected void onPostExecute(AccessToken result) {
						String token = new Gson().toJson(result);
						Log.e("Twitter Login", "Token:" + token);
						
						loginToGPSocial(token);
			        }
				}.execute(verifier);
			}
		}

	}

	// Sign in with Twitter
	public void TwitterLogin(View view) {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				getTwitterRequestToken();
				return null;
			}
		}.execute();
	}
	
	public void loginToGPSocial(String request) {
		GPSocialClient.post("authenticate", null, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				// open the main app!!!!
				startActivity(new Intent(SigninActivity.this, MainActivity.class));
			}
			
            @Override
			public void onFailure(String responseBody, Throwable error) {
				super.onFailure(responseBody, error);
				// XXX for now, since the endpoint is not there yet
				startActivity(new Intent(SigninActivity.this, MainActivity.class));
			}
        });
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	// For Twitter Signin
	// Step 1: Get request token
	private void getTwitterRequestToken() {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
		builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
		Configuration configuration = builder.build();

		TwitterFactory factory = new TwitterFactory(configuration);
		twitter = factory.getInstance();

		try {
			requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
			SigninActivity.this.startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse(requestToken.getAuthenticationURL())));
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
}
