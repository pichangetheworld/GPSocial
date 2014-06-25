package com.gpsocial.fragments;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gpsocial.R;
import com.gpsocial.adapter.FeedListAdapter;
import com.gpsocial.client.GPSocialClient;
import com.gpsocial.data.FeedData;
import com.gpsocial.data.TwitterData;
import com.loopj.android.http.TextHttpResponseHandler;

public class HomeFragment extends Fragment {
	private static final String _DUMMYDATA = "[{\"created_at\":\"Wed Jun 18 10:12:14 +0000 2014\",\"id\":20,\"id_str\":\"20\",\"text\":\"just setting up my twttr\",\"source\":\"web\",\"truncated\":false,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"user\":{\"id\":12,\"id_str\":\"12\",\"name\":\"Jack Dorsey\",\"screen_name\":\"jack\",\"location\":\"California\",\"description\":\"\",\"url\":null,\"entities\":{\"description\":{\"urls\":[]}},\"protected\":false,\"followers_count\":2577282,\"friends_count\":1085,\"listed_count\":23163,\"created_at\":\"Tue Mar 21 20:50:14 +0000 2006\",\"favourites_count\":2449,\"utc_offset\":-25200,\"time_zone\":\"Pacific Time (US & Canada)\",\"geo_enabled\":true,\"verified\":true,\"statuses_count\":14447,\"lang\":\"en\",\"contributors_enabled\":false,\"is_translator\":false,\"is_translation_enabled\":false,\"profile_background_color\":\"EBEBEB\",\"profile_background_image_url\":\"http://abs.twimg.com/images/themes/theme7/bg.gif\",\"profile_background_image_url_https\":\"https://abs.twimg.com/images/themes/theme7/bg.gif\",\"profile_background_tile\":false,\"profile_image_url\":\"http://pbs.twimg.com/profile_images/448483168580947968/pL4ejHy4_normal.jpeg\",\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/448483168580947968/pL4ejHy4_normal.jpeg\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/12/1347981542\",\"profile_link_color\":\"990000\",\"profile_sidebar_border_color\":\"DFDFDF\",\"profile_sidebar_fill_color\":\"F3F3F3\",\"profile_text_color\":\"333333\",\"profile_use_background_image\":true,\"default_profile\":false,\"default_profile_image\":false,\"following\":true,\"follow_request_sent\":false,\"notifications\":false},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"retweet_count\":23936,\"favorite_count\":21879,\"entities\":{\"hashtags\":[],\"symbols\":[],\"urls\":[],\"user_mentions\":[]},\"favorited\":false,\"retweeted\":false,\"lang\":\"en\"},"
			+ "{\"created_at\":\"Tue Jun 17 23:25:34 +0000 2014\",\"id\":432656548536401900,\"id_str\":\"432656548536401920\",\"text\":\"POST statuses/update. Great way to start. https://t.co/9S8YO69xzf (disclaimer, this was not posted via the API).\",\"source\":\"web\",\"truncated\":false,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"user\":{\"id\":2244994945,\"id_str\":\"2244994945\",\"name\":\"TwitterDev\",\"screen_name\":\"TwitterDev\",\"location\":\"Internet\",\"description\":\"Developers and Platform Relations @Twitter. We are developers advocates. We can't answer all your questions, but we listen to all of them!\",\"url\":\"https://t.co/66w26cua1O\",\"entities\":{\"url\":{\"urls\":[{\"url\":\"https://t.co/66w26cua1O\",\"expanded_url\":\"https://dev.twitter.com/\",\"display_url\":\"dev.twitter.com\",\"indices\":[0,23]}]},\"description\":{\"urls\":[]}},\"protected\":false,\"followers_count\":3147,\"friends_count\":909,\"listed_count\":53,\"created_at\":\"Sat Dec 14 04:35:55 +0000 2013\",\"favourites_count\":61,\"utc_offset\":-25200,\"time_zone\":\"Pacific Time (US & Canada)\",\"geo_enabled\":false,\"verified\":true,\"statuses_count\":217,\"lang\":\"en\",\"contributors_enabled\":false,\"is_translator\":false,\"is_translation_enabled\":false,\"profile_background_color\":\"FFFFFF\",\"profile_background_image_url\":\"http://abs.twimg.com/images/themes/theme1/bg.png\",\"profile_background_image_url_https\":\"https://abs.twimg.com/images/themes/theme1/bg.png\",\"profile_background_tile\":false,\"profile_image_url\":\"http://pbs.twimg.com/profile_images/431949550836662272/A6Ck-0Gx_normal.png\",\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/431949550836662272/A6Ck-0Gx_normal.png\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/2244994945/1391977747\",\"profile_link_color\":\"0084B4\",\"profile_sidebar_border_color\":\"FFFFFF\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"profile_text_color\":\"333333\",\"profile_use_background_image\":false,\"default_profile\":false,\"default_profile_image\":false,\"following\":true,\"follow_request_sent\":false,\"notifications\":false},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"retweet_count\":1,\"favorite_count\":5,\"entities\":{\"hashtags\":[],\"symbols\":[],\"urls\":[{\"url\":\"https://t.co/9S8YO69xzf\",\"expanded_url\":\"https://dev.twitter.com/docs/api/1.1/post/statuses/update\",\"indices\":[42,65]}],\"user_mentions\":[]},\"favorited\":false,\"retweeted\":false,\"possibly_sensitive\":false,\"lang\":\"en\"}]";

	private static final Type _TYPE = new TypeToken<TwitterData[]>(){}.getType();
	
	private FeedListAdapter adapter;
	private ListView listview;
	
	private String resultFromServer = _DUMMYDATA;
	private List<FeedData> standardFeed;
	
	Thread timer = new Thread() {
	    public void run () {
	        for (;;) {
	            // do stuff in a separate thread
	    		getResultFromServer();
	            uiCallback.sendEmptyMessage(0);
	            try {
				    // sleep for 3 seconds
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		    }
		}
	};
	
	private static Handler uiCallback = new Handler () {
	    public void handleMessage (Message msg) {
	        // do stuff with UI
	    }
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// get JSON string from server 
		Gson gson = new Gson();
		TwitterData[] feedFromServer = gson.fromJson(resultFromServer, _TYPE);
		
		standardFeed = new ArrayList<FeedData>();
		for (TwitterData data : feedFromServer) {
			standardFeed.add(new FeedData(data));
		}
		
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
	
	public void getResultFromServer() {
		System.out.println("pchan: should be trying to hit server");
		GPSocialClient.get("twitterTest", null, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
        		TwitterData[] feedFromServer = new Gson().fromJson(response, _TYPE);
        		standardFeed.clear();
        		for (TwitterData data : feedFromServer) {
        			standardFeed.add(new FeedData(data));
        		}
        		
        		System.out.println("pchan: RESULT FROM SERVER " + response);
        		
        		getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateFeed();
					}
				});
			}


		    @Override
		    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
		        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
		    	System.err.println("pchan: Error: " + statusCode + " message:" + e.getLocalizedMessage());
		    }
        });
	}
}

