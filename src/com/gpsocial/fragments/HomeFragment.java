package com.gpsocial.fragments;

import java.lang.reflect.Type;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gpsocial.R;
import com.gpsocial.R.id;
import com.gpsocial.R.layout;
import com.gpsocial.adapter.FeedListAdapter;
import com.gpsocial.data.FeedData;

public class HomeFragment extends Fragment {
//	private static final String[] SAMPLEVALUES = { "Facebook", "GooglePlus1", "Facebook2", "Twitter1", "Twitter2", "Facebook3", "Twitter3" };
	private String resultFromServer;
	
	private static final Type _type = new TypeToken<FeedData[]>(){}.getType();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// get JSON string from server 
		Gson gson = new Gson();
		FeedData[] feedFromServer = gson.fromJson(resultFromServer, _type);
		
		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		
		final ListView listview = (ListView) rootView.findViewById(R.id.listview);
		listview.setAdapter(new FeedListAdapter(getActivity(), 
				R.layout.list_feed, feedFromServer));
	    
		return rootView;
	}
}

