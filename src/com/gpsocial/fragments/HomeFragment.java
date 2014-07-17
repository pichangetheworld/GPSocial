package com.gpsocial.fragments;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gpsocial.MainActivity;
import com.gpsocial.R;
import com.gpsocial.adapter.FeedListAdapter;
import com.gpsocial.client.GPSocialClient;
import com.gpsocial.data.FeedData;
import com.gpsocial.data.TwitterData;
import com.loopj.android.http.TextHttpResponseHandler;

public class HomeFragment extends Fragment {
	private static final Type _TYPE = new TypeToken<TwitterData[]>() {}.getType();

	private FeedListAdapter adapter;
	private ListView listview;

	private List<FeedData> standardFeed;

	Thread timer = new Thread() {
		public void run() {
			// do stuff in a separate thread
			getResultFromServer();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		standardFeed = new ArrayList<FeedData>();

		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		adapter = new FeedListAdapter(getActivity(), R.layout.list_feed, standardFeed);

		listview = (ListView) rootView.findViewById(R.id.listview);
		listview.setAdapter(adapter);

		timer.start();

		return rootView;
	}

	public void updateFeed() {
		adapter.setListData(standardFeed);
		adapter.notifyDataSetChanged();
	}

	// get JSON string from server
	public void getResultFromServer() {
		final MainActivity act = (MainActivity) getActivity();
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ProgressDialog pd = act.getProgressDialog();
				pd.setMessage("Loading...");
				if (!act.isFinishing())
					pd.show();
			}
		});
		GPSocialClient.get("news_feed", act.getRequestParams(),
				new TextHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				System.out.println("pchan: response from server " + response);
				TwitterData[] feedFromServer = new Gson().fromJson(response, _TYPE);
				
				standardFeed.clear();
				for (TwitterData data : feedFromServer) {
					standardFeed.add(new FeedData(data));
				}

				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ProgressDialog pd = act.getProgressDialog();
						if (pd != null)
							pd.dismiss();
						updateFeed();
					}
				});
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				System.err.println("pchan: Error on News Feed: " + statusCode + " message:"
						+ e.getLocalizedMessage());

				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ProgressDialog pd = act.getProgressDialog();
						if (pd != null)
							pd.dismiss();
						
						new AlertDialog.Builder(act)
						.setTitle("Error")
						.setMessage("An error occurred when loading your news feed." + 
								" Please check your connection or try again later.")
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
									}
								})
						.show();
						
					}
				});
			}
		});
	}
}
