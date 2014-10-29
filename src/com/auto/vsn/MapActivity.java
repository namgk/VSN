package com.auto.vsn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.auto.data.DatabaseHandler;
import com.auto.data.TripData;
import com.auto.tab.TripTab;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.OpenGraphAction;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.model.OpenGraphAction;
import com.facebook.widget.ProfilePictureView;

public class MapActivity extends Activity {
	private GoogleMap mMap = null;
	TripData trip = new TripData();
	List<LatLng> coord = new ArrayList<LatLng>();
	int position = 0;
	int marker_count = 0;
	double marker_density = 0.0;
	
	DecimalFormat df = new DecimalFormat("#.##");
	DatabaseHandler db = new DatabaseHandler(this);
	
	private Button postTripButton;
	private static final Uri M_FACEBOOK_URL = Uri.parse("http://m.facebook.com");
	private static final String POST_ACTION_PATH = "me/vesna-project:plan";
	private boolean pendingPost;
	private ProfilePictureView profilePictureView;
	private TextView userNameView;
	private static final String PENDING_POST_KEY = "pendingPost";
	private ProgressDialog progressDialog;
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private String tripChoice = null;
	private String tripChoiceURL = null;
	private String tripDescription = null;
	private String tripDistance = null;
	private String fuel_consumption = null;
	private String gps_location = null;
	private static final String TRIP_KEY = "trip";
	private static final String TRIP_URL_KEY = "trip_url";

	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(final Session session, final SessionState state, final Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
                tokenUpdated();
            } else {
                makeMeRequest(session);
            }
        }
    }
    /**
     * Notifies that the session token has been updated.
     */
    private void tokenUpdated() {
        if (pendingPost) {
            handlePost();
        }
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		TextView t_title = (TextView) findViewById(R.id.t_title);
		TextView t_desc = (TextView) findViewById(R.id.t_desc);
		TextView t_dist = (TextView) findViewById(R.id.t_dist);
		TextView t_econ = (TextView) findViewById(R.id.t_econ);
		
		//Setup Post Button
		postTripButton = (Button) this.findViewById(R.id.post_trip_button);
		postTripButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View view) {
		        handlePost();
		    }
		});
		
		// Get the message from the intent
	    Intent intent = getIntent();
	    String pos = intent.getStringExtra(TripTab.EXTRA_MESSAGE);
	    System.out.println("Position: " + pos);
	    
	    // Get Trip data from db
		trip = db.getTrip(Integer.parseInt(pos));
		
		// Drawing trip path onto the map
		if(mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if(mMap != null) {
				PolylineOptions path = new PolylineOptions().width(5).addAll(trip.getCoordinate()).color(Color.RED);

				// Get back the mutable Polyline
				Polyline polyline = mMap.addPolyline(path);
				
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				placeMarkers(trip, pos);
			}
		}
		 
	    // Setup posting data
	    String title = "Trip " + pos + " (" + trip.getTitle() + ")";
        tripChoice = trip.getTitle();
        tripDistance = df.format(db.getTripDist(Integer.parseInt(pos)));
        System.out.println("Distance: " + df.format(db.getTripDist(Integer.parseInt(pos))));
        fuel_consumption = df.format(db.getAverageEcon(Integer.parseInt(pos)));
        System.out.println("Econ: " + df.format(db.getAverageEcon(Integer.parseInt(pos))));
        gps_location = "-123.133, 50.16879";
        tripDescription = trip.getDescription();
        tripChoiceURL = "https://vesna-project-496.herokuapp.com/object.php?type=trip&title=" + title + "&description=" + tripDescription + "&distance=" + tripDistance + "&fuel_consumption=" + fuel_consumption + "&gps_location=" + gps_location;
        System.out.println("https://eece496-vesna-project.herokuapp.com/object.php?type=trip&title=" + title + "&description=" + tripDescription + "&distance=" + tripDistance + "&fuel_consumption=" + fuel_consumption + "&gps_location=" + gps_location);
        
		// Displaying trip data to interface
        String assessment = "";
		t_title.setText("Trip " + pos + " (" + trip.getTitle() + ")\n");
		
		if(marker_count == 0)
			assessment = "Congragulations! You are an economical driver. Please continue to drive safely and economically. Overall grade: A+.\n";
		else if(marker_density > 0 && marker_density < 1.5)
			assessment = "Tip: Moderate acceleration was detected, but it wasn't too severe overall. For more detail, please click on the markers on the map. Overall grade: A-.\n";
		else if(marker_density > 1.5 && marker_density < 3)
			assessment = "Tip: Several instances of hard acceleration was detected. Throttle input can be reduced to increase your fuel economy. For more detail, please click on the markers on the map. Overall grade: B+.\n";
		else if(marker_density > 3)
			assessment = "Tip: Many instances of hard acceleration was detected. Throttle input must be reduced to increase your fuel economy. For more detail, please click on the markers on the map. Overall grade: C.\n";
		
		t_desc.setText(assessment);
		t_econ.setText("Average Economy: " + df.format(db.getAverageEcon(Integer.parseInt(pos))) + "L/100km");
		t_dist.setText("Distance Travelled: " + df.format(db.getTripDist(Integer.parseInt(pos))) + "km\n"
						+ "Description: " + trip.getDescription() + "\n");
		
	}
	
	private void makeMeRequest(final Session session) {
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                        profilePictureView.setProfileId(user.getId());
                        userNameView.setText(user.getName());
                    }
                }
                if (response.getError() != null) {
                    handleError(response.getError());
                }
            }
        });
        request.executeAsync();

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_map, menu);
		return true;
	}
	
	public void readStorage() {
		// Parsing Code
		File sdDir = Environment.getExternalStorageDirectory();
		File log = null;
		String line = "";
		
		if(sdDir != null) {
			log = new File(sdDir, "torqueTrackLog.csv");
		}
		if(log != null) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(log)));
		        try {
					while ((line = reader.readLine()) != null) {
					     String[] RowData = line.split(",");
					     
					     if(!RowData[0].equals("GPS Time")) {
					    	 trip.addTime(RowData[0]); // Time Stamp
						     trip.addCoordinate(new LatLng(Double.parseDouble(RowData[3]), Double.parseDouble(RowData[2])));
						     trip.addThrottle(Double.parseDouble(RowData[12])); // Throttle %
						     trip.addRpm(Double.parseDouble(RowData[13])); // Engine RPM
						     trip.addSpeed(Integer.parseInt(RowData[14])); // Speed
						     if(RowData[16].equals("-"))
						    	 trip.addDistance(0.0);
						     else
						    	 trip.addDistance(Double.parseDouble(RowData[16])); // Trip Distance
						     trip.addFuelLevel(Double.parseDouble(RowData[17])); // Fuel Level
						     if(RowData[19].equals("-"))
						    	 trip.addFuelEcon(0.0);
						     else
						    	 trip.addFuelEcon(Double.parseDouble(RowData[19])); // L/100 km (Average)*/
					     }
					}
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
	
	public void placeMarkers(TripData trip, String pos) {
		for(int i=0; i<trip.getTime().size()-2; i++) {
			if(trip.getThrottle(i+1) - trip.getThrottle(i) > 10 && trip.getRpm(i+1) - trip.getRpm(i) > 500) {
				mMap.addMarker(new MarkerOptions()
				.position(trip.getCoordinate(i+1))
				.title("Your engine was at " + df.format(trip.getRpm(i+1)) + "RPM with " + df.format(trip.getThrottle(i+1)) + "% throttle input.")
				.snippet("Tip: Excessive throttle greatly reduces fuel economy."));
				marker_count++;
			}	
		}
		marker_density = marker_count/db.getTripDist(Integer.parseInt(pos));
	}
	
	private void handlePost() {
        pendingPost= false;
        Session session = Session.getActiveSession();

        if (session == null || !session.isOpened()) {
        	System.out.println("Session is null or opened");
            return;
        }

        List<String> permissions = session.getPermissions();
        if (!permissions.containsAll(PERMISSIONS)) {
        	System.out.println("Pending post");
            pendingPost = true;
            requestPublishPermissions(session);
            return;
        }

        // Show a progress dialog because sometimes the requests can take a while.
        progressDialog = ProgressDialog.show(this, "",
                this.getResources().getString(R.string.progress_dialog_text), true);

        // Run this in a background thread since some of the populate methods may take
        // a non-trivial amount of time.
        AsyncTask<Void, Void, Response> task = new AsyncTask<Void, Void, Response>() {

            @Override
            protected Response doInBackground(Void... voids) {
                PlanAction planAction = GraphObject.Factory.create(PlanAction.class);
                publishAction(planAction);
                Bundle params = new Bundle();
                params.putString("description", tripDescription);
                params.putString("distance", tripDistance);
                params.putString("fuel_consumption", fuel_consumption);
                params.putString("gps_location", gps_location);
                params.putString("trip", tripChoiceURL);
                Request request = new Request(Session.getActiveSession(),
                        POST_ACTION_PATH, params, HttpMethod.POST);
                request.setGraphObject(planAction);
                System.out.println("Returning response");
                return request.executeAndWait();
            }

            @Override
            protected void onPostExecute(Response response) {
            	System.out.println("Post Executing");
            	onPostActionResponse(response);
             }
        };

        task.execute();
    }
    
    private void onPostActionResponse(Response response) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if (this == null) {
            // if the user removes the app from the website, then a request will
            // have caused the session to close (since the token is no longer valid),
            // which means the splash fragment will be shown rather than this one,
            // causing activity to be null. If the activity is null, then we cannot
            // show any dialogs, so we return.
            return;
        }
        PostResponse postResponse = response.getGraphObjectAs(PostResponse.class);

        if (postResponse != null && postResponse.getId() != null) {
            String dialogBody = String.format(getString(R.string.result_dialog_text), postResponse.getId());
            new AlertDialog.Builder(this)
                    .setPositiveButton(R.string.result_dialog_button_text, null)
                    .setTitle(R.string.result_dialog_title)
                    .setMessage(dialogBody)
                    .show();
            //init(null);
        } else {
            handleError(response.getError());
        }
    }
    public interface PostResponse extends GraphObject {
        String getId();
    }
    private void handleError(FacebookRequestError error) {
        DialogInterface.OnClickListener listener = null;
        String dialogBody = null;

        if (error == null) {
            dialogBody = getString(R.string.error_dialog_default_text);
        } else {
            switch (error.getCategory()) {
                case AUTHENTICATION_RETRY:
                    // tell the user what happened by getting the message id, and
                    // retry the operation later
                    String userAction = (error.shouldNotifyUser()) ? "" :
                            getString(error.getUserActionMessageId());
                    dialogBody = getString(R.string.error_authentication_retry, userAction);
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, M_FACEBOOK_URL);
                            startActivity(intent);
                        }
                    };
                    break;

                case AUTHENTICATION_REOPEN_SESSION:
                    // close the session and reopen it.
                    dialogBody = getString(R.string.error_authentication_reopen);
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Session session = Session.getActiveSession();
                            if (session != null && !session.isClosed()) {
                                session.closeAndClearTokenInformation();
                            }
                        }
                    };
                    break;

                case PERMISSION:
                    // request the publish permission
                    dialogBody = getString(R.string.error_permission);
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            pendingPost = true;
                            requestPublishPermissions(Session.getActiveSession());
                        }
                    };
                    break;

                case SERVER:
                case THROTTLING:
                    // this is usually temporary, don't clear the fields, and
                    // ask the user to try again
                    dialogBody = getString(R.string.error_server);
                    break;

                case BAD_REQUEST:
                    // this is likely a coding error, ask the user to file a bug
                    dialogBody = getString(R.string.error_bad_request, error.getErrorMessage());
                    break;

                case OTHER:
                case CLIENT:
                default:
                    // an unknown issue occurred, this could be a code error, or
                    // a server side issue, log the issue, and either ask the
                    // user to retry, or file a bug
                    dialogBody = getString(R.string.error_unknown, error.getErrorMessage());
                    break;
            }
        }

        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.error_dialog_button_text, listener)
                .setTitle(R.string.error_dialog_title)
                .setMessage(dialogBody)
                .show();
    }
    private void requestPublishPermissions(Session session) {
        if (session != null) {
        	System.out.println("Session not null, request");
            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS)
                    // demonstrate how to set an audience for the publish permissions,
                    // if none are set, this defaults to FRIENDS
                    .setDefaultAudience(SessionDefaultAudience.FRIENDS)
                    .setRequestCode(REAUTH_ACTIVITY_CODE);
            session.requestNewPublishPermissions(newPermissionsRequest);
        }
    }
    /**
     * Interface representing the Trip Open Graph object.
     */
    private interface TripGraphObject extends GraphObject {
        public String getUrl();
        public void setUrl(String url);
        public void setDescription(String description);
        public String getId();
        public void setId(String id);
    }

    /**
     * Interface representing the Plan action.
     */
    private interface PlanAction extends OpenGraphAction {
        public TripGraphObject getTrip();
        public void setTrip(TripGraphObject trip);
    }
    
    protected void publishAction(OpenGraphAction action) {
        if (tripChoiceURL != null) {
            PlanAction postAction = action.cast(PlanAction.class);
            TripGraphObject trip = GraphObject.Factory.create(TripGraphObject.class);
           // trip.setDescription(tripDescription);
            trip.setUrl(tripChoiceURL);
            postAction.setTrip(trip);
           
        }
    }
   public void onSaveInstanceState(Bundle bundle) {
        if (tripChoice != null && tripChoiceURL != null) {
            bundle.putString(TRIP_KEY, tripChoice);
            bundle.putString(TRIP_URL_KEY, tripChoiceURL);
        }
    }
   
   protected boolean restoreState(Bundle savedState) {
       String trip = savedState.getString(TRIP_KEY);
       String tripURL = savedState.getString(TRIP_URL_KEY);
       if (trip != null && tripURL != null) {
           tripChoice = trip;
           tripChoiceURL = tripURL;
           setTripButton();
           return true;
       }
       return false;
   }
   private void setTripButton() {
       if (tripChoice != null && tripChoiceURL != null) {
          
           postTripButton.setEnabled(true);
       } else {
           postTripButton.setEnabled(false);
       }
   }

}
