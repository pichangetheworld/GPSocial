package com.gpsocial;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gpsocial.adapter.FeedListAdapter;

public class HomeFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		String[] values = { "Facebook", "GooglePlus1", "Facebook2", "Twitter1", "Twitter2", "Facebook3", "Twitter3" };
		List<String> list = new ArrayList<String>();
		for (String str : values) {
			list.add(str);
		}
		
		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		
		final ListView listview = (ListView) rootView.findViewById(R.id.listview);
		listview.setAdapter(new FeedListAdapter(getActivity(), 
				R.layout.list_feed, R.id.feed_content, list));
	    
		return rootView;
	}
}

