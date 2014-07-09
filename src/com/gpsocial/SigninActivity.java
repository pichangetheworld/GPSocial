package com.gpsocial;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.gpsocial.client.GPSocialClient;
import com.gpsocial.data.AuthData;
import com.gpsocial.fragments.WebDialogFragment;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

public class SigninActivity extends Activity {
	private UiLifecycleHelper uiHelper;

	// callback for twitter
	private static final String TWITTER_CALLBACK_URL = "oauth://gpsocial";
	private static final String TWITTER_CONSUMER_KEY = "RDfBstGQ5U8zMxP5dLcF6ugI4";
	private static final String TWITTER_CONSUMER_SECRET = "qJRiOLJDP2QqoWpv0rt7aAoCKBGmdQLd4J5FUeM7OVlx7qYyfO";

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
		public void call(final Session session, SessionState state,
				Exception exception) {
			if (state.isOpened()) {
				// make request to the /me API
				Request.newMeRequest(session, new Request.GraphUserCallback() {
					// callback after Graph API response with user object
					@Override
					public void onCompleted(GraphUser user, Response response) {
						RequestParams request = new RequestParams();
						if (user != null) {
							request.put("userId", user.getId());
						}

						request.put("token", session.getAccessToken());
						loginToGPSocialFacebook(request);
					}
				}).executeAsync();
			}
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
		String mUserId = mSharedPreferences.getString(PREF_KEY_LOGIN, "");
		if (!mUserId.isEmpty()) {
			Intent i = new Intent(SigninActivity.this, MainActivity.class);
			Bundle b = new Bundle();
			b.putString("userId", mUserId);
			i.putExtras(b);
			startActivity(i);
		} else {
			Uri uri = getIntent().getData();
			if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
				// oAuth verifier
				String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

				new AsyncTask<String, Void, AccessToken>() {
					@Override
					protected AccessToken doInBackground(String... params) {
						AccessToken accessToken = null;
						try {
							// Get the access token
							accessToken = twitter.getOAuthAccessToken(requestToken, params[0]);

							// Shared Preferences
							Editor e = mSharedPreferences.edit();

							// After getting access token, access token secret
							// store them in application preferences
							e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
							e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
							
							e.commit(); // save changes

							Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
						} catch (TwitterException e) {
							// Check log for login errors
							Log.e("Twitter Login Error", "> " + e.getMessage());
						}
						return accessToken;
					}

					@Override
					protected void onPostExecute(AccessToken token) {
						RequestParams request = new RequestParams();
						request.put("userId", Long.toString(token.getUserId()));
						request.put("screenName", token.getScreenName());
						request.put("token", token.getToken());
						request.put("tokenSecret", token.getTokenSecret());
//						System.out.println("pchan: userId:" + Long.toString(token.getUserId())
//								+ " screenName:" + token.getScreenName()
//								+ " token: " + token.getToken()
//								+ " tokenSecret:" + token.getTokenSecret());
						loginToGPSocialTwitter(request);
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

	public void loginToGPSocialFacebook(RequestParams request) {
		loginToGPSocial("authenticate_facebook", request);
	}

	public void loginToGPSocialTwitter(RequestParams request) {
		loginToGPSocial("authenticate_twitter", request);
	}

	public void loginToGPSocial(String endpoint, RequestParams request) {
		GPSocialClient.post(endpoint, request,
				new TextHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						AuthData auth = new Gson().fromJson(response, AuthData.class);
						
						if (auth.success) {
							// Store login userId
							mSharedPreferences.edit().putString(PREF_KEY_LOGIN, auth.userId).commit();
							
							System.out.println("pchan: SUCCESS! user id is: " + auth.userId);
							
							// open the main activity
							Intent i = new Intent(SigninActivity.this, MainActivity.class);
							Bundle b = new Bundle();
							b.putString("userId", auth.userId);
							i.putExtras(b);
							startActivity(i);
						} else {
							System.err.println("pchan: Signin failed with auth success " + auth.success);
							
							// show error
							onSigninFailure();
						}
					}

					@Override
					public void onFailure(String responseBody, Throwable error) {
						super.onFailure(responseBody, error);
						
						System.err.println("pchan: Signin error... " + error.getLocalizedMessage());
						onSigninFailure();
					}
				});
	}
	
	private void onSigninFailure() {
		new AlertDialog.Builder(this)
				.setTitle("Error")
				.setMessage("An error happened on sign in. Please try again later.")
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						})
				.show();
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
//			WebDialogFragment newFragment = new WebDialogFragment();
//			newFragment.setUrl(requestToken.getAuthenticationURL());
//		    newFragment.show(getFragmentManager(), "twitter_login");
			SigninActivity.this.startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse(requestToken.getAuthenticationURL())));
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
}
