package com.gpsocial.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gpsocial.R;

public class MapFragment extends Fragment implements LocationListener {
    private GoogleMap map;
    private static View mMainView;
    private TextView switchStatus;
    List<Marker> markerArray;
    
    //Test Location
    LatLng UW_RCH = new LatLng(43.470241, -80.540792);
    LatLng UW_Mels_Diner = new LatLng(43.472803, -80.535299);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		if (mMainView != null) {
			ViewGroup parent = (ViewGroup) mMainView.getParent();
			if (parent != null) {
				parent.removeView(mMainView);
			}
		}

		try {
			mMainView = inflater.inflate(R.layout.fragment_map, container, false);
		} catch (InflateException e) {
			// map is already there, just return view as it is
		}

		markerArray = new ArrayList<Marker>();

		final SupportMapFragment frag = (SupportMapFragment)
				getFragmentManager().findFragmentById(R.id.map);
		map = frag.getMap();
		map.setMyLocationEnabled(true);
		Marker user1 = map.addMarker(new MarkerOptions()
				.position(UW_RCH)
				.title("David")
				.snippet("David is at RCH")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_launcher)));
		markerArray.add(user1);
		Marker user2 = map.addMarker(new MarkerOptions()
				.position(UW_Mels_Diner)
				.title("Amy")
				.snippet("Amy is at Mel's Diner")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.ic_launcher)));
		markerArray.add(user2);
		LocationManager locManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		// Criteria criteria = new Criteria();
		// String provider = locManager.getBestProvider(criteria, false);

		switchStatus = (TextView) mMainView.findViewById(R.id.switchStatus);
		Button btnGPSConfig = (Button) mMainView.findViewById(R.id.gpsButton);
		btnGPSConfig.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent viewIntent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(viewIntent);
			}
		});

		Location locGPS = locManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (locGPS != null) {
			onLocationChanged(locGPS);
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					20000, 0, this);
		} else {
			Location locNoGPS = locManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (locNoGPS != null) {
				onLocationChanged(locNoGPS);
			}
			locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					20000, 0, this);
		}

		/*
		 * boolean isGPSEnabled =
		 * locManager.isProviderEnabled(LocationManager.GPS_PROVIDER); boolean
		 * isNetworkEnabled =
		 * locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		 * 
		 * if (isGPSEnabled){
		 * locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		 * 20000, 0, this); } else {
		 * locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		 * 20000, 0, this); }
		 */
		return mMainView;
	}

	@Override
	public void onLocationChanged(Location location) {
				
		switchStatus.setText("... wait for updates");
		
		for (Marker marker : markerArray) {
		    if (marker.getTitle().equals("MyLocation")){
		    	marker.remove();
		    }
		}
		
		TextView ln=(TextView)mMainView.findViewById(R.id.lng);
		TextView lt=(TextView)mMainView.findViewById(R.id.lat);
		
		// Getting latitude of the current location
		double latitude = location.getLatitude();
		
		// Getting longitude of the current location
		double longitude = location.getLongitude();		
		
		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);
		
		Marker myLoc = map.addMarker(new MarkerOptions()
						        .position(latLng)
						        .title("MyLocation")
						        .snippet("I am here")
						        .icon(BitmapDescriptorFactory
						        		.fromResource(R.drawable.cur_position)));
		markerArray.add(myLoc);
		// Showing the current location in Google Map
		map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		
		// Zoom in the Google Map
		map.animateCamera(CameraUpdateFactory.zoomTo(14));	
		
		ln.setText("long: " + longitude);
        lt.setText("lat: " + latitude);

        switchStatus.setText("Successfully detected your location");
        
        
        
	}

	@Override
	public void onProviderDisabled(String provider) {
		TextView ln = (TextView)mMainView.findViewById(R.id.lng);
		LocationManager locManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		boolean isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if(!isGPSEnabled && !isNetworkEnabled){
			if(!ln.getText().toString().equals("")){
				switchStatus.setText("Location Service OFF. Last Saved Location Shown");
			}else{
				switchStatus.setText("Location Service OFF. Use the button to turn on");
			}
			
		}
		
		if (!isGPSEnabled){
	        if (isNetworkEnabled){
	        	switchStatus.setText("GPS is OFF, but using LTE/Wi-Fi");
			}
		}
		if (!isNetworkEnabled){
	        if (isGPSEnabled){
	        	switchStatus.setText("LTE/Wi-Fi is OFF, but using GPS");
	        }
		}
		
		/*
		if (provider.equals("network")){			
			switchStatus.setText("GPS is OFF / network is ON");			
		}
		*/		
	}

	@Override
	public void onProviderEnabled(String provider) {
		if (provider.equals("gps")){			
			switchStatus.setText("GPS is ON");			
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub		
	}	
}