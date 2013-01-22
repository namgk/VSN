package com.auto.vsn;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;

public class MapActivity extends Activity {
	private GoogleMap mMap = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		if(mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if(mMap != null) {
				// Instantiates a new Polyline object and adds points to define a rectangle
				PolylineOptions path = new PolylineOptions()
						.width(5)
				        .add(new LatLng(49.16878568, -123.1325545))
				        .add(new LatLng(49.1476532, -123.1417192)); // Closes the polyline.

				// Set the rectangle's color to red
				path.color(Color.RED);

				// Get back the mutable Polyline
				Polyline polyline = mMap.addPolyline(path);
				
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				mMap.addMarker(new MarkerOptions()
					.position(new LatLng(49.16878568, -123.1325545))
					.title("Fuel Economy: 11.4L/100km")
					.snippet("Throttle Position: 20%, Engine RPM: 1433 RPM"));
				
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_map, menu);
		return true;
	}

}
