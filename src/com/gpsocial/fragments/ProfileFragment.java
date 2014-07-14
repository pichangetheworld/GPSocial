package com.gpsocial.fragments;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.gson.Gson;
import com.gpsocial.MainActivity;
import com.gpsocial.R;
import com.gpsocial.adapter.FeedListAdapter;
import com.gpsocial.client.GPSocialClient;
import com.gpsocial.data.AuthData;
import com.gpsocial.data.FeedData;
import com.gpsocial.data.ProfileData;
import com.gpsocial.data.TwitterData;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileFragment extends Fragment {
	private UiLifecycleHelper uiHelper = null;
	
	private FeedListAdapter adapter;
	private ListView listview;
	private List<FeedData> standardFeed;
	
	private TextView username;
	private TextView handle;
	private ImageView avatar;
	
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
						JSONObject request = new JSONObject();
						try {
							if (user != null) {
								request.put("userId", user.getId());
							}
							request.put("token", session.getAccessToken());
						} catch (JSONException e) {
						}
						signInToGPSocialFacebook(request);
					}

					private void signInToGPSocialFacebook(JSONObject request) {
						try {
							GPSocialClient.post(getActivity(), "authenticate_facebook",
									new StringEntity(request.toString()),
									new TextHttpResponseHandler() {
										@Override
										public void onSuccess(String response) {
											AuthData auth = new Gson().fromJson(response, AuthData.class);
											
											if (auth.success) {
												((MainActivity) getActivity()).updateFlags(auth.connectedFlag);
											} else {
												// show error
												onSigninFailure();
											}
										}

										@Override
										public void onFailure(String responseBody, Throwable error) {
											super.onFailure(responseBody, error);
											
											System.err.println("pchan: Signin error... " + 
													error.getLocalizedMessage());
											onSigninFailure();
										}
									});
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
							System.err.println("pchan: encoding exception... " + 
									e.getLocalizedMessage());
							onSigninFailure();
						}						
					}

					private void onSigninFailure() {
						new AlertDialog.Builder(getActivity())
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
				}).executeAsync();
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

		if (((MainActivity) getActivity()).isFacebookLinked() == 0) {
			uiHelper = new UiLifecycleHelper(getActivity(), callback);
			uiHelper.onCreate(savedInstanceState);
		}
		
		standardFeed = new ArrayList<FeedData>();
		
		adapter = new FeedListAdapter(getActivity(), R.layout.list_feed, standardFeed);
		
		username = (TextView) rootView.findViewById(R.id.username);
		handle = (TextView) rootView.findViewById(R.id.handle);
		avatar = (ImageView) rootView.findViewById(R.id.avatar_pic);

		// get flags
		// link Facebook
		// link Twitter
		final LoginButton linkFacebook = (LoginButton) rootView.findViewById(R.id.link_facebook);
		linkFacebook.setBackgroundResource(R.drawable.link_facebook_selector);
		if (((MainActivity) getActivity()).isFacebookLinked() != 0) {
			linkFacebook.setVisibility(View.GONE);
		}
		if (((MainActivity) getActivity()).isTwitterLinked() != 0) {
			((Button) rootView.findViewById(R.id.link_twitter)).setVisibility(View.GONE);
		}
		
		listview = (ListView) rootView.findViewById(R.id.feed_list);
		listview.setAdapter(adapter);
		
		getResultFromServer();
		
		return rootView;
	}

	public void updateFeed() {
		adapter.setListData(standardFeed);
		adapter.notifyDataSetChanged();
	}
	
	public void getResultFromServer() {
		GPSocialClient.get("profile", ((MainActivity) getActivity()).getRequestParams(),
				new TextHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						System.out.println("pchan: response was successful "
								+ response);
						ProfileData profileFeed = new Gson().fromJson(response,
								ProfileData.class);
						standardFeed.clear();
						for (TwitterData data : profileFeed.feed) {
							standardFeed.add(new FeedData(data));
						}

						username.setText(profileFeed.name);
						handle.setText(profileFeed.twitter_handle);

						ImageLoader.getInstance().displayImage(
								profileFeed.profile_img_url_tw, avatar);

						updateFeed();
					}

					@Override
					public void onFailure(String responseBody, Throwable error) {
						super.onFailure(responseBody, error);

						System.err.println("pchan: Error on Profile message:"
								+ error.getLocalizedMessage());
					}
				});
	}


	@Override
	public void onResume() {
		super.onResume();
		if (uiHelper != null)
			uiHelper.onResume();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (uiHelper != null)
			uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (uiHelper != null)
			uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (uiHelper != null)
			uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (uiHelper != null)
			uiHelper.onSaveInstanceState(outState);
	}

}