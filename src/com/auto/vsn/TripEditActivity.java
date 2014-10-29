package com.auto.vsn;

import com.auto.data.DatabaseHandler;
import com.auto.data.TripData;
import com.auto.tab.TripTab;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TripEditActivity extends Activity {
	DatabaseHandler db;
	TripData trip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_edit);
		db = new DatabaseHandler(TripEditActivity.this);
		
		// Get the message from the intent
	    Intent intent = getIntent();
	    final String pos = intent.getStringExtra(TripTab.EXTRA_MESSAGE);
	    System.out.println("Position: " + pos);
		
	    // Get vehicle name from preferences
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		final String vehicle = sharedPrefs.getString("vehicle_name", null);
		System.out.println("Vehicle Name: " + vehicle);
		
		// Get Button ID
		Button postTripButton = (Button) this.findViewById(R.id.save);
		postTripButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View view) {
		    	TextView title = (TextView) findViewById(R.id.title);
	    		TextView desc = (TextView) findViewById(R.id.desc);
	    		Intent intent = new Intent(TripEditActivity.this, InterfaceActivity.class);
	    		
		    	if(vehicle != null) {
	    			db.updateDesc(pos, vehicle, title.getText().toString(), desc.getText().toString());
	    			finish();
		    	}
		    	else {
		    		Toast.makeText(TripEditActivity.this, "Please enter vehicle name in settings", Toast.LENGTH_SHORT).show();
		    		db.updateDesc(pos, "", title.getText().toString(), desc.getText().toString());
		    	}
		    }
		});
			
	}

}
