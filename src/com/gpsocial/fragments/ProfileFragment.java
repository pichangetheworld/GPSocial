package com.gpsocial.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gpsocial.R;
import com.gpsocial.data.ProfileData;

public class ProfileFragment extends Fragment {
	private static final String _DUMMYDATA = "{\"username\":\"Joe Smith\",\"twitter_handle\":\"@joesmith\",\"profile_img_url_tw\":\"http://png-3.findicons.com/files/icons/1580/devine_icons_part_2/128/account_and_control.png\","
			+ "\"feed\":[{\"feed_source\":1,\"author\":\"GPSocial\", \"message\":\"first Twitter post\", \"created_at\":1403531574529, \"profile_img_url\":\"http://lautechstudents.com/images/profile.png\"},"
			+ "{\"feed_source\":1,\"author\":\"jack\",\"message\":\"Hi I'm Jack\",\"created_at\":1403531412241,\"profile_img_url\":\"http://lautechstudents.com/images/profile.png\"}]}";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_profile, container,
				false);

		// get JSON string from server
		Gson gson = new Gson();
		ProfileData profileFeed = gson.fromJson(_DUMMYDATA, ProfileData.class);

		TextView username = (TextView) rootView.findViewById(R.id.username);
		username.setText(profileFeed.username);
		
		TextView handle = (TextView) rootView.findViewById(R.id.handle);
		handle.setText(profileFeed.twitter_handle);
		
		View feeds = rootView.findViewById(R.id.status1);
		TextView author1 = (TextView) feeds.findViewById(R.id.author);
		author1.setText(profileFeed.feed[0].author);
		
		TextView message1 = (TextView) feeds.findViewById(R.id.message);
		message1.setText(profileFeed.feed[0].message);

		return rootView;
	}
}