package com.gpsocial.fragments;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gpsocial.MainActivity;
import com.gpsocial.R;
import com.gpsocial.adapter.FeedListAdapter;
import com.gpsocial.client.GPSocialClient;
import com.gpsocial.data.FeedData;
import com.gpsocial.data.ProfileData;
import com.gpsocial.data.TwitterData;
import com.loopj.android.http.TextHttpResponseHandler;

public class ProfileFragment extends Fragment {
	private FeedListAdapter adapter;
	private ListView listview;
	private List<FeedData> standardFeed;
	
	private TextView username;
	private TextView handle;
	private ImageView avatar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_profile, container,
				false);
		
		standardFeed = new ArrayList<FeedData>();
		
		adapter = new FeedListAdapter(getActivity(), R.layout.list_feed, standardFeed);
		
		username = (TextView) rootView.findViewById(R.id.username);
		handle = (TextView) rootView.findViewById(R.id.handle);
		avatar = (ImageView) rootView.findViewById(R.id.avatar_pic);
		
		// TODO
		// get flags
		// link Facebook
		// link Twitter
		
		listview = (ListView) rootView.findViewById(R.id.feed_list);
		listview.setAdapter(adapter);
		
		// TODO
		// post Tweets
		
		getResultFromServer();
		
		return rootView;
	}

	public void updateFeed() {
		adapter.setListData(standardFeed);
		adapter.notifyDataSetChanged();
	}
	
	public void getResultFromServer() {
		GPSocialClient.get("profile", ((MainActivity) getActivity()).getRequestParams(), new TextHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				ProfileData profileFeed = new Gson().fromJson(response, ProfileData.class);
        		standardFeed.clear();
        		for (TwitterData data : profileFeed.feed) {
        			standardFeed.add(new FeedData(data));
        		}

        		System.out.println("pchan name is " + profileFeed.name + " handle:" + profileFeed.twitter_handle);
        		username.setText(profileFeed.name);
        		handle.setText(profileFeed.twitter_handle);
        		
        		getProfileImg(profileFeed.profile_img_url_tw);
        	    
        		updateFeed();
			}
			
            @Override
			public void onFailure(String responseBody, Throwable error) {
				super.onFailure(responseBody, error);
			}
        });
	}
	
	private void getProfileImg(final String img_url) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					URL url = new URL(img_url);
					final Bitmap bmp = BitmapFactory.decodeStream(url
							.openConnection().getInputStream());
					
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							avatar.setImageBitmap(bmp);
							adapter.notifyDataSetChanged();
						}
					});
					
				} catch (MalformedURLException e) {
					System.err.println("pchan: profile picture url " + img_url + " not working");
					e.printStackTrace();
				} catch (IOException e) {
					System.err.println("pchan: profile picture bitmap not working");
					e.printStackTrace();
				}
			}
		}).start();
	}
}