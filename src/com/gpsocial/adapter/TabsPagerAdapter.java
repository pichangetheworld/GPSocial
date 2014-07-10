package com.gpsocial.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gpsocial.fragments.HomeFragment;
import com.gpsocial.fragments.MapFragment;
import com.gpsocial.fragments.ProfileFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	List<Fragment> mFragments;
	
	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
		
		mFragments = new ArrayList<Fragment>();
		mFragments.add(new MapFragment());
		mFragments.add(new HomeFragment());
		mFragments.add(new ProfileFragment());
	}

	@Override
	public Fragment getItem(int index) {
		return mFragments.get(index % 3);
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return mFragments.size();
	}

}
