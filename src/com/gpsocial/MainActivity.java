package com.gpsocial;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;

import com.gpsocial.adapter.TabsPagerAdapter;
import com.gpsocial.client.GPSocialClient;
import com.gpsocial.data.FeedData;
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
	private JSONObject mHeader = null;
	
	private ImageButton mPostFeed = null;
	private EditText mPostMessage = null;
	
	private ProgressDialog mProgressDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mUserId = getIntent().getStringExtra("userId");
		System.out.println("pchan user id is " + mUserId);
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
		
		mPostFeed = (ImageButton) findViewById(R.id.post_message_source);
		mPostMessage = (EditText) findViewById(R.id.post_message);

		// If Twitter is not linked show FB resources
	    if (isTwitterLinked() == 0) {
        	mPostFeed.setBackground(getResources().getDrawable(R.drawable.facebook_logo_blue));
        	mPostMessage.setHint(R.string.post_status_prompt);
	    }
		
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
	
	public JSONObject getHeader() {
		if (mHeader == null) {
			mHeader = new JSONObject();
			try {
				mHeader.put("id", mUserId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (mLat > -100) {
			try {
				mHeader.put("lng", Double.toString(mLong));
				mHeader.put("lat", Double.toString(mLat));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return mHeader;
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
	
	public int getId() {
		return Integer.parseInt(mUserId);
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
	    Menu popupMenu = popup.getMenu();
	    if (isTwitterLinked() == 0) {
	        popupMenu.findItem(R.id.twitter).setVisible(false);
	    }
	    if (isFacebookLinked() == 0)
	    	popupMenu.findItem(R.id.facebook).setVisible(false);
	    popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
            	mPostFeed.setBackground(item.getIcon());
                if (item.getTitle() == getString(R.string.post_facebook)) {
            		mPostMessage.setHint(R.string.post_status_prompt);
                } else if (item.getTitle() == getString(R.string.post_twitter)) {
            		mPostMessage.setHint(R.string.post_tweet_prompt);
                }
//            	Toast.makeText(getBaseContext(), "Selected the action : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
	    popup.show();
	}
	
	public void postTweet(View v) {
		final EditText e = (EditText) findViewById(R.id.post_message);
		String message = e.getText().toString();
		
		if (!message.isEmpty()) {
			System.out.println("pchan: posting a message " + message);
	        JSONObject p = getHeader();
	        try {
				p.put("message", message);
				p.put("source", FeedData.Source.TWITTER.ordinal());
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
	        ProgressDialog pd = getProgressDialog();
	        pd.setMessage("Posting...");
	        pd.show();
			try {
				GPSocialClient.post(this, "post_message",
						new StringEntity(p.toString()),
						new TextHttpResponseHandler() {
							@Override
							public void onSuccess(String response) {
								new Thread() {
									@Override
									public void run() {
										if (mProgressDialog != null)
											mProgressDialog.dismiss();
									}
								}.start();
								
								e.setText("");
								e.clearFocus();
								InputMethodManager imm = (InputMethodManager)getSystemService(
									      Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(e.getWindowToken(), 0);
								Toast.makeText(MainActivity.this, 
										"Successfully posted!", Toast.LENGTH_LONG)
										.show();
							}

							@Override
							public void onFailure(String responseBody, Throwable error) {
								super.onFailure(responseBody, error);
								System.err.println("pchan: Error while posting a tweet... " + 
										error.getLocalizedMessage());
								onPostMessageFailure();
							}
						});
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
				System.err.println("pchan: error encoding tweet " + e1.getLocalizedMessage());
			}
		}
	}
	
	private void onPostMessageFailure() {
		new Thread() {
			@Override
			public void run() {
				if (mProgressDialog != null)
					mProgressDialog.dismiss();
			}
		}.start();
		new AlertDialog.Builder(this)
				.setTitle("Error")
				.setMessage("An error happened when trying to post your message. Please try again later.")
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						})
				.show();
	}
	
	public ProgressDialog getProgressDialog() {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setIndeterminate(true);
		}
		mProgressDialog.setMessage("Loading...");
		return mProgressDialog;
	}
}
