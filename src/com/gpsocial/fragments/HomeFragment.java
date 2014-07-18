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
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gpsocial.MainActivity;
import com.gpsocial.R;
import com.gpsocial.adapter.FeedListAdapter;
import com.gpsocial.client.GPSocialClient;
import com.gpsocial.data.FeedData;
import com.loopj.android.http.TextHttpResponseHandler;

public class HomeFragment extends Fragment {
	private static final Type _TYPE = new TypeToken<FeedData[]>() {}.getType();

	private FeedListAdapter adapter;
	private ListView listview;
	private SwipeRefreshLayout swipeView;

	private List<FeedData> standardFeed;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		standardFeed = new ArrayList<FeedData>();

		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		adapter = new FeedListAdapter(getActivity(), R.layout.list_feed, standardFeed);

		swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
	    swipeView.setEnabled(false);
	    swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
	        @Override
	        public void onRefresh() {
	        	swipeView.setRefreshing(true);
	            getResultFromServer();
	        }
	    });
		
		listview = (ListView) rootView.findViewById(R.id.listview);
		listview.setAdapter(adapter);
		listview.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0)
                    swipeView.setEnabled(true);
                else
                    swipeView.setEnabled(false);
			}
		});

		getResultFromServer();

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
				if (!act.isFinishing() && !pd.isShowing())
					pd.show();
			}
		});
		GPSocialClient.get("news_feed", act.getRequestParams(),
				new TextHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
//				System.out.println("pchan: response from server " + response);
				FeedData[] feedFromServer = new Gson().fromJson(response, _TYPE);
				
				standardFeed.clear();
				for (FeedData data : feedFromServer) {
					standardFeed.add(data);
				}

				act.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ProgressDialog pd = act.getProgressDialog();
						if (pd != null)
							pd.dismiss();
						swipeView.setRefreshing(false);
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
						swipeView.setRefreshing(false);
						
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
