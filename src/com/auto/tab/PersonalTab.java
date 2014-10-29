package com.auto.tab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.auto.data.DatabaseHandler;
import com.auto.data.TripData;
import com.auto.vsn.InterfaceActivity;
import com.auto.vsn.R;
import com.auto.vsn.R.layout;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PersonalTab extends ListFragment {
	private static final int SERVICE_TODAY = 0;
	private static final int SERVICE_SOON = 1;
	private static final int SERVICE_OVERDUE = 2;
	
	ListView friendList;
	SimpleAdapter adapter;
	ArrayList<String> top = new ArrayList<String>();
	ArrayList<String> bottom = new ArrayList<String>();
	List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	
	private UiLifecycleHelper uiHelper;
	private ProfilePictureView profilePictureView;
	private TextView userNameView;
	private static final int REAUTH_ACTIVITY_CODE = 100;
	
	int service_status = 0;
		
	ArrayList<TripData> storedTrips = new ArrayList<TripData>();
	TripData trip = new TripData();
	TripData sample = new TripData();
	DatabaseHandler db;
	int num_trips = 0;
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
	Date currDate = new Date();
	Date lastDate = new Date();
	String nextService = "Unavailable";
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	/*@Override
	public
	void onAttach(Activity activity) {
		super.onAttach(activity);
	    
	    if(getActivity() == null)
	    	System.out.println("onAttach Activity null");
	    else
	    	System.out.println("onAttach Activity not null");
		
	}*/
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		db = new DatabaseHandler(getActivity());
	 }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.activity_personal_tab, container, false);
		friendList = (ListView) view.findViewById(android.R.id.list);
		friendList.setScrollContainer(false);
		
	    DecimalFormat df = new DecimalFormat("#.##");
	    
	    readStorage();
		storeData();
		
		profilePictureView = (ProfilePictureView) view.findViewById(R.id.personal_profile_pic);
	    profilePictureView.setCropped(true);
	    userNameView = (TextView) view.findViewById(R.id.personal_user_name);
		
	    top.add("Overall Rating");
		top.add("Total Distance");
		top.add("Average Fuel Economy");
		top.add("Current Fuel Level");
		top.add("Service Date");
		bottom.add(getRating());
		bottom.add(df.format(db.getTotalDist()) + "km");
		bottom.add(df.format(db.getAverageEcon()) + "L/100km");
		bottom.add(df.format(db.getFuelLevel()) + "%");
		//bottom.add("Service recommended in 165 days.");
		bottom.add(nextService);
		
		for(int i=0; i<5; i++) {
			Map<String, String> datum = new HashMap<String, String>(2);
			System.out.println("Main: " + top.get(i));
			System.out.println("Main: " + top.get(i));
	        datum.put("main", top.get(i));
	        datum.put("sub", bottom.get(i));
	        data.add(datum);
		} 
	    
		adapter = new SimpleAdapter(getActivity(), data, android.R.layout.simple_list_item_2, new String[] {"main", "sub"}, new int[] {android.R.id.text1, android.R.id.text2});
	    
		
		registerForContextMenu(friendList);
        friendList.setAdapter(adapter);
        
        // Check for an open session
	    Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	        // Get the user's data
	        makeMeRequest(session);
	    }
        
		return view;
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == REAUTH_ACTIVITY_CODE) {
	        uiHelper.onActivityResult(requestCode, resultCode, data);
	    }
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
	    super.onSaveInstanceState(bundle);
	    uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
	    if (session != null && session.isOpened()) {
	        // Get the user's data.
	        makeMeRequest(session);
	    }
	}
	
	private void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a new callback to handle the response.
		Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {

			@Override
			public void onCompleted(GraphUser user, Response response) {
				// TODO Auto-generated method stub
				if(session == Session.getActiveSession()) {
					if(user != null) {
						profilePictureView.setProfileId(user.getId());
						userNameView.setText(user.getName());
					}
				}
				if(response.getError() != null) {
					// Handle errors, will do later
				}
			}
			
		});
		request.executeAsync();
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(final Session session, final SessionState state, final Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
    
    public void storeData() {
		
		System.out.println("Trip Count: " + db.getTripCount());
		System.out.println("DB Count: " + db.getDbCount());
		System.out.println("storedTrips size: " + storedTrips.size());
		
		for(int i=0; i<storedTrips.size(); i++) {
			System.out.println("Search Param: " + storedTrips.get(i).getTime(0));
			if(!db.hasTrip(storedTrips.get(i).getTime(0))) {
				System.out.println("Trip does not exist");
				db.addTrip(storedTrips.get(i));
			}
			System.out.println("Trip does exist");
		}
	}
	
	public void readStorage() {
		// Set a count value to count the number of lines. Set that as num_lines.
		// Check curr_num_lines every 2 seconds. If curr_num_lines > num_lines, read curr_num_line + 1.
		// Increment curr_num_line
		
		num_trips = db.getTripCount();
		// Parsing Code
		File sdDir = Environment.getExternalStorageDirectory();
		File log = null;
		String line = "";
		boolean first = true;
		
		if(sdDir != null) {
			//log = new File(sdDir, "torqueTrackLog.csv");
			log = new File(sdDir, "Test.csv");
		}
		if(log != null) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(log)));
		        try {
					while ((line = reader.readLine()) != null) {
					     String[] RowData = line.split(",");
					     
					     if(RowData[0].equals("GPS Time")) {
					    	 System.out.println("New Trip");
					    	 num_trips++;
					    	 if(!first)
						    	 storedTrips.add(trip);
					    	 trip = new TripData();
					    	 trip.setTripNo(num_trips);
					    	 first = false;
					     } 
					     else {
					    	 trip.addTime(RowData[0]); // Time Stamp
						     trip.addCoordinate(new LatLng(Double.parseDouble(RowData[3]), Double.parseDouble(RowData[2])));
						     trip.addThrottle(Double.parseDouble(RowData[12])); // Throttle %
						     trip.addRpm(Double.parseDouble(RowData[13])); // Engine RPM
						     trip.addSpeed(Integer.parseInt(RowData[14])); // Speed
						     if(RowData[16].equals("-"))
						    	 trip.addDistance(0.0);
						     else {
						    	 trip.addDistance(Double.parseDouble(RowData[16])); // Trip Distance
						    	 //System.out.println(Double.parseDouble(RowData[16]));
						     }
						     trip.addFuelLevel(Double.parseDouble(RowData[17])); // Fuel Level
						     if(RowData[19].equals("-"))
						    	 trip.addFuelEcon(0.0);
						     else
						    	 trip.addFuelEcon(Double.parseDouble(RowData[19])); // L/100km (Average)*/
					     }
					}
					trip.setDate(trip.getTime(0));
					storedTrips.add(trip);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void updateService() {
		try {
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			StringBuilder builder = new StringBuilder();
			String date = sharedPrefs.getString("service_date", null);
			
			if(date != null && !date.equals("mm/dd/yyyy")) {
				lastDate = dateFormat.parse(date);
				System.out.println("Last service date: " + date);
				System.out.println("Year: " + lastDate.getYear());
				System.out.println("Month: " + lastDate.getMonth());
				System.out.println("Day: " + lastDate.getDay());
				
				System.out.println("Current date: " + date);
				System.out.println("Year: " + currDate.getYear());
				System.out.println("Month: " + currDate.getMonth());
				System.out.println("Day: " + currDate.getDay());
				
				System.out.println("Year Difference: " + (currDate.getYear()-lastDate.getYear())*365);
				System.out.println("Month Difference: " + (currDate.getMonth()-lastDate.getMonth())*30);
				System.out.println("Day Difference: " + (currDate.getDay()-lastDate.getDay()));
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Exception");
		}
		
		System.out.println("Date Elapsed: " + (currDate.getYear() - lastDate.getYear())*365 + (currDate.getMonth() - lastDate.getMonth())*30 + (currDate.getDay() - lastDate.getDay()));
		
		if(lastDate.equals(currDate)) {
			nextService = "Next service recommended today";
			service_status = SERVICE_TODAY;
		}
		else if((currDate.getYear() - lastDate.getYear())*365 + (currDate.getMonth() - lastDate.getMonth())*30 + (currDate.getDay() - lastDate.getDay()) < 180) {
			nextService = "Next service is recommended in " + Integer.toString(180-(currDate.getYear() - lastDate.getYear())*365 + (currDate.getMonth() - lastDate.getMonth())*30 + (currDate.getDay() - lastDate.getDay())) + " days";
			service_status = SERVICE_SOON;
		}
		else if((currDate.getYear() - lastDate.getYear())*365 + (currDate.getMonth() - lastDate.getMonth())*30 + (currDate.getDay() - lastDate.getDay()) > 180) {
			nextService = "Service has been overdue for " + Integer.toString((currDate.getYear() - lastDate.getYear())*365 + (currDate.getMonth() - lastDate.getMonth())*30 + (currDate.getDay() - lastDate.getDay())-180) + " days";
			service_status = SERVICE_OVERDUE;
		}

	}
	
	// This function will be used to give a rating based on the user's driving characteristics
	public String getRating() {
		/*
		score += econ_criteria/db.getAverageEcon();
		
		if(score > 1.0 && score < 1.025)
			rating = "B+";
		else if(score > 1.025 && score < 1.05)
			rating = "A-";
		else if(score > 1.05 && score < 1.15)
			rating = "A";

		return rating;*/
		TripData temp_trip;
		int marker_count = 0;
		Double score = 0.0;
		String rating = "";
		Double econ_criteria = 10.0;
		
		// This loops finds the total number of hard accelerations made
		
		for(int i=1; i<=db.getTripCount(); i++) {
			temp_trip = db.getTrip(i);
			System.out.println("Loop " + i);
			
			for(int j=0; j<temp_trip.getTime().size()-2; j++) {
				if(temp_trip.getThrottle(j+1) - temp_trip.getThrottle(j) > 10 && temp_trip.getRpm(j+1) - temp_trip.getRpm(j) > 500) {
					marker_count++;
				}	
			}
		}
		
		System.out.println("marker count: " + marker_count);
		System.out.println("total distance: " + db.getTotalDist());
		
		// Calculating the score
		score += db.getTotalDist()/marker_count;
		System.out.println("marker density: " + score);
		
		System.out.println("econ ratio: " +  econ_criteria/db.getAverageEcon());
		score += econ_criteria/db.getAverageEcon();
		
		if(service_status == SERVICE_OVERDUE)
			score -= 1;
		else if(service_status == SERVICE_SOON)
			score += 0.5;
		
		// Totalling up the score
		if(score <= 0)
			rating = "F";
		else if(score > 0 && score <= 0.5)
			rating = "D";
		else if(score > 0.5 && score <= 1)
			rating = "C";
		else if(score > 1 && score <= 2)
			rating = "B";
		else if(score > 2 && score <= 3)
			rating = "A-";
		else if(score > 3)
			rating = "A";
		
		System.out.println("Final Score: " + score);
		return rating;
	}

}
