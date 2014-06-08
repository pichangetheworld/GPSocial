package com.gpsocial.adapter;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class FeedListAdapter extends ArrayAdapter<String> {

	public FeedListAdapter(Context context, int resource,
			int textViewResourceId, List<String> objects) {
		super(context, resource, textViewResourceId, objects);
	}


}
