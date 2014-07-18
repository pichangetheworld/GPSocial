package com.gpsocial.fragments;

import java.lang.reflect.Type;

import org.apache.http.Header;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gpsocial.MainActivity;
import com.gpsocial.R;
import com.gpsocial.client.GPSocialClient;
import com.gpsocial.data.MapUserData;
import com.loopj.android.http.TextHttpResponseHandler;

public class MapFragment extends Fragment implements LocationListener {
	private static final Type _TYPE = new TypeToken<MapUserData[]>() {}.getType();
	
    private GoogleMap mMap;
    private static View mMainView;
    private TextView switchStatus;
    private SparseArray<Marker> userMarkers;
    private int mUserId;
    
    //Test Location
    LatLng UW_RCH = new LatLng(43.470241, -80.540792);
    LatLng UW_Mels_Diner = new LatLng(43.472803, -80.535299);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		mUserId = ((MainActivity)getActivity()).getId();

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

		userMarkers = new SparseArray<Marker>();

		final SupportMapFragment frag = (SupportMapFragment)
				getFragmentManager().findFragmentById(R.id.map);
		mMap = frag.getMap();
		mMap.setMyLocationEnabled(true);
//		Marker user1 = map.addMarker(new MarkerOptions()
//				.position(UW_RCH)
//				.title("David")
//				.snippet("David is at RCH")
//				.icon(BitmapDescriptorFactory
//						.fromResource(R.drawable.ic_launcher)));
//		markerArray.add(user1);
//		Marker user2 = map.addMarker(new MarkerOptions()
//				.position(UW_Mels_Diner)
//				.title("Amy")
//				.snippet("Amy is at Mel's Diner")
//				.icon(BitmapDescriptorFactory
//						.fromResource(R.drawable.ic_launcher)));
//		markerArray.add(user2);
		LocationManager locManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		// Criteria criteria = new Criteria();
		// String provider = locManager.getBestProvider(criteria, false);

		switchStatus = (TextView) mMainView.findViewById(R.id.switchStatus);
		ToggleButton shareLocation = (ToggleButton) mMainView.findViewById(R.id.share_my_location);
		shareLocation.setChecked(true);
		shareLocation.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Toast.makeText(getActivity(), "Toggled privacy settings!", Toast.LENGTH_SHORT).show();
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
		
		getResultFromServer();
		
		return mMainView;
	}

	@Override
	public void onLocationChanged(Location location) {
				
		Marker m = userMarkers.get(mUserId);
		if (m != null)
			m.remove();
		
		TextView ln = (TextView) mMainView.findViewById(R.id.lng);
		TextView lt = (TextView) mMainView.findViewById(R.id.lat);
		
		// Getting latitude of the current location
		double latitude = location.getLatitude();
		
		// Getting longitude of the current location
		double longitude = location.getLongitude();		
		
		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);
		
		Marker myLoc = mMap.addMarker(new MarkerOptions()
						        .position(latLng)
						        .title("MyLocation")
						        .snippet("I am here")
						        .icon(BitmapDescriptorFactory
						        		.fromResource(R.drawable.cur_position)));
		userMarkers.put(mUserId, myLoc);
		
		// Showing the current location in Google Map
		mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		
		// Zoom in the Google Map
		mMap.animateCamera(CameraUpdateFactory.zoomTo(14));	
		
		ln.setText("long: " + longitude);
        lt.setText("lat: " + latitude);
        ln.setVisibility(View.GONE);
        lt.setVisibility(View.GONE);

//        switchStatus.setText("Successfully detected your location");
        
        if (getActivity() != null && !getActivity().isFinishing())
        	((MainActivity) getActivity()).setLocation(longitude, latitude);
	}

	@Override
	public void onProviderDisabled(String provider) {
		TextView ln = (TextView)mMainView.findViewById(R.id.lng);
		LocationManager locManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		boolean isGPSEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if(!isGPSEnabled && !isNetworkEnabled){
			if (!ln.getText().toString().equals("")) {
				switchStatus.setText("Location Service OFF. Last Saved Location Shown");
			} else {
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
		GPSocialClient.get("users_near_me", act.getRequestParams(),
				new TextHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						MapUserData[] feedFromServer = new Gson().fromJson(response, _TYPE);
						for (MapUserData data : feedFromServer) {
							Marker m = userMarkers.get(data.id);
							if (m == null) {
//								System.out.println("pchan: user doesn't exist yet id(" + data.id + ")");
//								System.out.println("pchan: drawing user at (" + data.lat + "," + data.lng + ")");
								Marker user1 = mMap.addMarker(new MarkerOptions()
										.position(new LatLng(data.lat, data.lng))
										.title(data.user_name)
										.icon(BitmapDescriptorFactory
												.fromResource(R.drawable.ic_launcher)));
								userMarkers.put(data.id, user1);
							} else {
								m.setPosition(new LatLng(data.lat, data.lng));
							}
						}

						act.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ProgressDialog pd = act.getProgressDialog();
								if (pd != null)
									pd.dismiss();
							}
						});
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] errorResponse, Throwable e) {
						// called when response HTTP status is "4XX" (eg. 401,
						// 403, 404)
						System.err.println("pchan: Error on Maps: "
								+ statusCode + " message:"
								+ e.getLocalizedMessage());

						act.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ProgressDialog pd = act.getProgressDialog();
								if (pd != null)
									pd.dismiss();

								new AlertDialog.Builder(act)
										.setTitle("Error")
										.setMessage(
												"An error occurred when finding your friends."
														+ " Please check your connection or try again later.")
										.setPositiveButton(
												android.R.string.ok,
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
													}
												}).show();

							}
						});
					}
				});
	}
}