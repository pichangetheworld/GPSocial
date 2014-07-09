package com.gpsocial;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.gpsocial.adapter.TabsPagerAdapter;
import com.loopj.android.http.RequestParams;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {
	
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	
	private String[] tabs = {"Map", "Home", "Profile"};
	
	private String mUserId;
	private RequestParams mRequestParams = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mUserId = getIntent().getStringExtra("userId");
		
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
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
	
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		viewPager.setCurrentItem(1);
	}
	
	public RequestParams getRequestParams() {
		if (mRequestParams == null) {
			mRequestParams = new RequestParams();
			mRequestParams.add("id", mUserId);
		}
		return mRequestParams;
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
	
}
