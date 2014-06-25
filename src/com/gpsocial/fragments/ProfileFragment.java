package com.gpsocial.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gpsocial.R;
import com.gpsocial.adapter.FeedListAdapter;
import com.gpsocial.client.GPSocialClient;
import com.gpsocial.data.FeedData;
import com.gpsocial.data.ProfileData;
import com.loopj.android.http.TextHttpResponseHandler;

public class ProfileFragment extends Fragment {
	private static final String _DUMMYDATA = "{\"username\":\"bob marley\",\"twitter_handle\":\"@joesmith\",\"profile_img_url_tw\":\"http://png-3.findicons.com/files/icons/1580/devine_icons_part_2/128/account_and_control.png\","
			+ "\"feed\":[{\"feed_source\":1,\"author\":\"GPSocial\", \"message\":\"first Twitter post\", \"created_at\":1403531574529, \"profile_img_url\":\"http://lautechstudents.com/images/profile.png\"},"
			+ "{\"feed_source\":1,\"author\":\"jack\",\"message\":\"Hi I'm Jack\",\"created_at\":1403531412241,\"profile_img_url\":\"http://lautechstudents.com/images/profile.png\"}]}";

	private FeedListAdapter adapter;
	private ListView listview;
	private List<FeedData> standardFeed;
	
	private TextView username;
	private TextView handle;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// get JSON string from server
		ProfileData profileFeed = new Gson().fromJson(_DUMMYDATA, ProfileData.class);

		View rootView = inflater.inflate(R.layout.fragment_profile, container,
				false);
		
		standardFeed = new ArrayList<FeedData>();
		for (FeedData data : profileFeed.feed) {
			standardFeed.add(data);
		}
		
		adapter = new FeedListAdapter(getActivity(), R.layout.list_feed, standardFeed);
		
		username = (TextView) rootView.findViewById(R.id.username);
		handle = (TextView) rootView.findViewById(R.id.handle);
		
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
		GPSocialClient.get("profileTest", null, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				ProfileData profileFeed = new Gson().fromJson(response, ProfileData.class);
        		standardFeed.clear();
        		for (FeedData data : profileFeed.feed) {
        			standardFeed.add(data);
        		}

        		username.setText(profileFeed.username);
        		handle.setText(profileFeed.twitter_handle);
        		
        		updateFeed();
			}
			
            @Override
			public void onFailure(String responseBody, Throwable error) {
				super.onFailure(responseBody, error);
			}
        });
	}
}