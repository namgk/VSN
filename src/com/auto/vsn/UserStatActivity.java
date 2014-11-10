package com.auto.vsn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.auto.bt.BluetoothChat;
import com.auto.data.TripData;
import com.auto.data.DatabaseHandler;
import com.auto.data.Accelerometer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserStatActivity extends Activity {
	
	ArrayList<TripData> storedTrips = new ArrayList<TripData>();
	TripData trip = new TripData();
	TripData sample = new TripData();
	DatabaseHandler db = new DatabaseHandler(this);
	TextView display;
	
	TextView econ;
	TextView distance;
	TextView throttle;
	TextView fuel;
	
	
	int num_trips = 0;
	int prev_length = 0;
	int curr_length = 0;
	double distance_offset = 0;
	String acc = "";
	boolean recordData = false;
	boolean firstLog = true;
	
	Timer timer, timer2;
	boolean updateFb = false;
	int updatePeriod = 0;

	private GoogleMap mMap = null;
	LatLng prev_coord = new LatLng(0.0, 0.0);
	static double xAxis = 0;
	static double yAxis = 0;
	static double zAxis = 0;
	
	DecimalFormat df = new DecimalFormat("#.##");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Holo);
		setContentView(R.layout.activity_user_stat);
		myView v = new myView(this);
		//Accelerometer accelerometer = new Accelerometer(UserStatActivity.this); // Observation: Upon acceleration, Z goes < 0
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		StringBuilder builder = new StringBuilder();

		updateFb = sharedPrefs.getBoolean("perform_updates", false);
		updatePeriod = Integer.parseInt(sharedPrefs.getString("updates_interval", "-1"));
		
		econ = (TextView) findViewById(R.id.econ);
		distance = (TextView) findViewById(R.id.distance);
		throttle = (TextView) findViewById(R.id.throttle);
		fuel = (TextView) findViewById(R.id.level);
		
		if(mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if(mMap != null) {
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			}
		}

	}
	
	@Override 
	public void onResume() {
		super.onResume();
		//display = (TextView) findViewById(R.id.user_stat);
		econ = (TextView) findViewById(R.id.econ);
		distance = (TextView) findViewById(R.id.distance);
		throttle = (TextView) findViewById(R.id.throttle);
		fuel = (TextView) findViewById(R.id.level);
	}
	
	public void readLine(int row) {
		// Parsing Code
		File sdDir = Environment.getExternalStorageDirectory();
		File log = null;
		String line = "";
		
		if(sdDir != null) {
			log = new File(sdDir + "/sdLogs/", "TripDataLog.csv");
			//log = new File(sdDir, "Test.csv");
		}
		if(log != null) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(log)));
		        try {    	
					for(int i=0; i<row; i++) {
					     line = reader.readLine();
					}
				     String[] RowData = line.split(",");
					 
				     // Time
			    	 trip.addTime(RowData[0]);
			    	 
			    	 // Speed
			    	 if(!RowData[1].equals("-")) {
				    	 trip.addSpeed(Integer.parseInt(RowData[1]));
				     } else {
				    	 trip.addSpeed(0);
				     }
			    	 
			    	 // LatLng
				     trip.addCoordinate(new LatLng(Double.parseDouble(RowData[2]), Double.parseDouble(RowData[3])));
				     
				     // RPM
				     if(!RowData[4].equals("-")) {
				    	 trip.addRpm(Double.parseDouble(RowData[4]));
				     } else {
				    	 trip.addRpm(0.0);
				     } 
				     
				     // Throttle %
				     if(!RowData[5].equals("-")) {
				    	 trip.addThrottle(Double.parseDouble(RowData[5]));
				     } else {
				    	 trip.addThrottle(0.0);
				     } 
				     
				     // Fuel Level
				     if(RowData[6].equals("-")) {
				    	 trip.addFuelLevel(0.0);
				     } else {
				    	 trip.addFuelLevel(Double.parseDouble(RowData[6]));
				     } 
				     
				     // Trip Distance
				     if(RowData[7].equals("-")) {
				    	 trip.addDistance(0.0);
				     } else {
				    	 trip.addDistance(Double.parseDouble(RowData[7]));
				     }
				  		
				     // Fuel Economy
				     if(RowData[8].equals("-") || !RowData[8].contains(".")) {
				    	 trip.addFuelEcon(0.0);
				     } else {
				    	 trip.addFuelEcon(Double.parseDouble(RowData[8]));
				     }
				     
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getLogLength() {
		//num_trips = db.getTripCount();
		// Parsing Code
		File sdDir = Environment.getExternalStorageDirectory();
		File log = null;
		String line = "";
		int length = 0;
		
		if(sdDir != null) {
			log = new File(sdDir + "/sdLogs/", "TripDataLog.csv");
			//log = new File(sdDir, "Test.csv");
		}
		if(log != null) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(log)));
		        try {
					while ((line = reader.readLine()) != null) {
						length++;
					}
					return length;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return length;
	}

	public void logData(View view) {
		recordData = !recordData;
		
		if(recordData) {
			
			Toast.makeText(UserStatActivity.this, "Data Logging Enabled", Toast.LENGTH_SHORT).show();
			trip.clearTrip();
			trip.setTripNo(db.getTripCount() + 1);
			
			timer = new Timer();
			timer2 = new Timer();
			
			timer.scheduleAtFixedRate(new LogTask() {
				@Override
			    public void run() {
			        UserStatActivity.this.runOnUiThread(new Runnable() {
			            @Override
			            public void run() {
			            	
			            	if(recordData) {
			        			curr_length = getLogLength();
			        			readLine(curr_length - 1);
			        			//display.setText("Current Length: " + curr_length + "\nPrevious Length: " + prev_length);
			        			
			        			if(trip.size() > 0) {
			        				if(firstLog) {
			        					trip.setDate(trip.getTime(0));
			        					distance_offset = trip.getDistance(0);
			        					trip.setDescription("Trip made with Social Drive.");
			        					firstLog = false;
			        				}
			        				
				        			acc = warnDriver();
        			
				    				econ.setText("Econ: " + trip.getFuelEcon(trip.size()-1) + "L/100km");
				    				distance.setText("Distance: " + df.format(trip.getDistance(trip.size()-1) - distance_offset) + "km");
				    				throttle.setText("Throttle: " + trip.getThrottle(trip.size()-1) + "%");
				    				fuel.setText("Fuel Remaining: " + trip.getFuelLevel(trip.size()-1) + "%");
			        
				        			if(mMap != null) {
					        			PolylineOptions path = new PolylineOptions().width(5).addAll(trip.getCoordinate()).color(Color.RED);
					    				Polyline polyline = mMap.addPolyline(path);
					    				
					    				if(prev_coord.latitude != trip.getCoordinate(trip.size()-1).latitude && prev_coord.longitude != trip.getCoordinate(trip.size()-1).longitude)
					    					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(trip.getCoordinate(trip.size()-1), 14));
				        			}
				        			
				        			prev_length = curr_length;
			        			}
			        		}
			            }
			        });
			    }
			}, 1000, 1000);
			
			if(updateFb) {
				timer2.scheduleAtFixedRate(new LogTask() {
					@Override
				    public void run() {
				        UserStatActivity.this.runOnUiThread(new Runnable() {
				            @Override
				            public void run() {
				            	// Add Facebook auto posting code
				            }
				        });
					}
				}, 1000, updatePeriod);
			}
			
		}
		else {
			Toast.makeText(UserStatActivity.this, "Data Logging Disabled", Toast.LENGTH_SHORT).show();
			timer.cancel();
			timer2.cancel();
			
			// Ask the user if they want to save the trip if recorded
			
		    // 1. Instantiate an AlertDialog.Builder with its constructor
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    
		    // 2. Chain together various setter methods to set the dialog characteristics
		    builder.setMessage(R.string.confirm_message).setTitle(R.string.confirm_title);
		    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		            // User cancelled the dialog
		        	trip.clearTrip();
		        	firstLog = true;
		        }
		    });
		    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		        	db.addTrip(trip);
		        	trip.clearTrip();
		        	Toast.makeText(UserStatActivity.this, "Trip Saved", Toast.LENGTH_SHORT).show();
		        	firstLog = true;
		        }
		    });
		    // 3. Get the AlertDialog from create()
		    AlertDialog dialog = builder.create();
		    dialog.show();
		  }		
	}
	
	class LogTask extends TimerTask {

		@Override
		public void run() {
			System.out.println("LogTask is completed by Java timer");
		}
		
	}

	public static void updateAccelerometer(double x, double y, double z) {
		System.out.println("This shit is being called.");
		xAxis = x;
		yAxis = y;
		zAxis = z;
		//System.out.println("X: " + x + ", Y: " + y + ", Z: " + z);
    }
	
	 // Functions for initializing the action menu
	 @Override
     public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.action, menu);
       return true;
     }
	
	 @Override
     public boolean onOptionsItemSelected(MenuItem item) {
	   Intent intent;	 
       switch (item.getItemId()) {
       case R.id.menuitem1:
    	   intent = new Intent(this, UserStatActivity.class);
   	       startActivity(intent);
    	   break;
       case R.id.menuitem2:
    	   intent = new Intent(this, SettingsActivity.class);
   	       startActivity(intent);
    	   break;
       default:
         break;
       }
       return true;
     }
	 
	 private class myView extends ImageView{

		 public myView(Context context) {
		  super(context);
		 }

		 @Override
		 protected void onDraw(Canvas canvas) {
		  Bitmap gauge = BitmapFactory.decodeResource(getResources(), R.drawable.gauge);
		  Bitmap dial = BitmapFactory.decodeResource(getResources(), R.drawable.needle);
		          canvas.drawBitmap(gauge, 0, 0, null);
		          canvas.drawBitmap(dial, 0, 0, null);
		 }
	 }
	 
	 public String warnDriver() {
		if(trip.getThrottle().size() > 2) {
			if(trip.getThrottle(trip.size()-1) - trip.getThrottle(trip.size()-2) > 10 && trip.getRpm(trip.size()-1) - trip.getRpm(trip.size()-2) > 500 && trip.getSpeed(trip.size()-2) > 5) {
				return "Plz Stahp!";
			}
	 	}
	 	return "You're Alright";
	 }
	 
}
