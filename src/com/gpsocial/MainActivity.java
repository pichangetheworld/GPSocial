package com.gpsocial;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.gpsocial.adapter.TabsPagerAdapter;
import com.gpsocial.client.GPSocialClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {
	
	private final int TWITTER_LINKED_FLAG = 1;
	private final int FACEBOOK_LINKED_FLAG = 1 << 1;
	
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	
	private String[] tabs = {"Map", "Home", "Profile"};
	
	private String mUserId;
	private double mLong = 0, mLat = -100; // valid latitude is between [-90, 90]
	private int mSocialNetworkFlags = 0;
	private RequestParams mRequestParams = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mUserId = getIntent().getStringExtra("userId");
		mSocialNetworkFlags = getIntent().getIntExtra("socialNetworkFlags", 0);
		
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
	        .cacheInMemory(true)
	        .cacheOnDisk(true)
	        .bitmapConfig(Bitmap.Config.RGB_565)
	        .imageScaleType(ImageScaleType.EXACTLY)
	        .build();
        ImageLoaderConfiguration config = 
        		new ImageLoaderConfiguration.Builder(getApplicationContext())
		    		.defaultDisplayImageOptions(defaultOptions)
		    		.threadPoolSize(4)
		        	.build();
        ImageLoader.getInstance().init(config);
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		
		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		for (String tab_name : tabs){
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}


		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
	
			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}
	
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
	
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
		
		viewPager.setCurrentItem(1);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.sign_out:
	        	System.out.println("pchan: sign out button pressed");
	        	Intent i = new Intent(MainActivity.this, SigninActivity.class);
	        	i.putExtra("SIGNOUT", true);
	        	finish();
	        	startActivity(i);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public RequestParams getRequestParams() {
		if (mRequestParams == null) {
			mRequestParams = new RequestParams();
			mRequestParams.add("id", mUserId);
		}
		if (mLat > -100) {
			mRequestParams.put("lng", Double.toString(mLong));
			mRequestParams.put("lat", Double.toString(mLat));
		}
		return mRequestParams;
	}
	
	public void setLocation(double lng, double lat) {
		mLong = lng; mLat = lat;
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
	
	public int isTwitterLinked() { return mSocialNetworkFlags & TWITTER_LINKED_FLAG; }
	public int isFacebookLinked() { return mSocialNetworkFlags & FACEBOOK_LINKED_FLAG; }
	
	public void updateFlags(int flags) { mSocialNetworkFlags = flags; }
	
	public void showPopup(View v) {
	    PopupMenu popup = new PopupMenu(this, v);
	    MenuInflater inflater = popup.getMenuInflater();
	    inflater.inflate(R.menu.post_status, popup.getMenu());
	    popup.show();
	}
	
	public void postTweet(View v) {
		final EditText e = (EditText) findViewById(R.id.post_message);
		String message = e.getText().toString();
		
		if (!message.isEmpty()) {
			// TODO post the string to post_tweet endpoint
			System.out.println("pchan: posting a tweet " + message);
			GPSocialClient.post("post_message", getRequestParams(),
					new TextHttpResponseHandler() {
						@Override
						public void onSuccess(String response) {
							e.setText("");
							Toast.makeText(MainActivity.this, 
									"Successfully posted!", Toast.LENGTH_LONG)
									.show();
						}

						@Override
						public void onFailure(String responseBody, Throwable error) {
							super.onFailure(responseBody, error);
							System.err.println("pchan: Error while posting a tweet... " + 
									error.getLocalizedMessage());
						}
					});
		}
	}
}
