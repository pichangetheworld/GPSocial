package com.gpsocial.adapter;

import com.gpsocial.HomeFragment;
import com.gpsocial.MapFragment;
import com.gpsocial.ProfileFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new MapFragment();
		case 1:
			// Games fragment activity
			return new HomeFragment();
		case 2:
			// Movies fragment activity
			return new ProfileFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}
