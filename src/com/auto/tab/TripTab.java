package com.auto.tab;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.auto.data.DatabaseHandler;
import com.auto.data.TripData;
import com.auto.vsn.InterfaceActivity;
import com.auto.vsn.MapActivity;
import com.auto.vsn.R;
import com.auto.vsn.R.layout;
import com.auto.vsn.R.menu;
import com.auto.vsn.TripEditActivity;
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

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TripTab extends ListFragment {
	public static final String EXTRA_MESSAGE = "com.auto.vsn.MESSAGE";
	ListView tripList;
	SimpleAdapter adapter;
	ArrayList<String> top = new ArrayList<String>();
	ArrayList<String> bottom = new ArrayList<String>();
	List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	
	DatabaseHandler db;
	int curr_tab = 0;
	int curr_row = 0;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 db = new DatabaseHandler(getActivity());
	 }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_trip_tab, container, false);
		tripList = (ListView) view.findViewById(android.R.id.list);
		
		int numEntries = db.getTripCount();
		DecimalFormat df = new DecimalFormat("#.##");
		
		for(int i=1; i<=numEntries; i++) {
			TripData trip = db.getTrip(i);
			Map<String, String> datum = new HashMap<String, String>(2);
			top.add(trip.getTitle());
			//bottom.add("Distance: " + df.format(db.getTripDist(i)) + "km\nDate: "+ db.getTripTime(i) +"\nAverage Economy: " + df.format(db.getAverageEcon(i)) + "L/100km\n");
			bottom.add("Date: "+ db.getTripTime(i) + "\nDescription: "+ trip.getDescription() + "\n");
			System.out.println("Main: " + top.get(i-1));
			System.out.println("Bottom: " + bottom.get(i-1));
	        datum.put("main", top.get(i-1));
	        datum.put("sub", bottom.get(i-1));
	        data.add(datum);
		}
		
		adapter = new SimpleAdapter(getActivity(), data, android.R.layout.simple_list_item_2, new String[] {"main", "sub"}, new int[] {android.R.id.text1, android.R.id.text2});
		registerForContextMenu(tripList);
        tripList.setAdapter(adapter);
        
		return view;
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        System.out.println("Item with id ["+id+"] - Position ["+position+"]");
        Intent intent = new Intent(getActivity(), MapActivity.class);
        String pos = Integer.toString(position + 1);
        intent.putExtra(EXTRA_MESSAGE, pos);
        startActivity(intent);
    }
	
	// We want to create a context Menu when the user long click on an item
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	 	    super.onCreateContextMenu(menu, v, menuInfo);
	 	    curr_tab = InterfaceActivity.getTabPos();
	 	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
	        curr_row = info.position;
	 	    System.out.println("Current Tab: " + curr_tab);
	 	    System.out.println("Current Row: " + curr_row);
	 	      
	 	    if(curr_tab == 2) {
		    	menu.setHeaderTitle("Trip Options");
		        menu.add(1, 1, 1, "Edit Trip Details"); 
		        menu.add(1, 2, 2, "Delete");
	 	    }
	 	}
	 	
	 	// This method is called when user selects an Item in the Context menu
	 	@Override
	 	public boolean onContextItemSelected(MenuItem item) {
	 	      int itemId = item.getItemId();
	 	      // Implements our logic
	 	      System.out.println("Item id ["+itemId+"]");
	       
	 	      if(curr_tab == 2) {
	 	    	  if(itemId == 1) {
	 	    		 // Edit selected trip
	 	    		 Intent intent = new Intent(getActivity(), TripEditActivity.class);
	 	    		 String pos = Integer.toString(curr_row + 1);
	 	             intent.putExtra(EXTRA_MESSAGE, pos);
	 	    		 startActivity(intent);
	 	  	      }
	 	    	  else if(itemId == 2) {
	 	    		 //Toast.makeText(getActivity(), "Delete Trip", Toast.LENGTH_SHORT).show();
	 			      // 1. Instantiate an AlertDialog.Builder with its constructor
	 		    	  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	 		    	  
	 		    	  // 2. Chain together various setter methods to set the dialog characteristics
	 		    	  builder.setMessage(R.string.confirm_del_message).setTitle(R.string.confirm_del_title);
	 		    	  builder.setNegativeButton(R.string.confirm_del_no, new DialogInterface.OnClickListener() {
	 		              public void onClick(DialogInterface dialog, int id) {
	 		                  // User cancelled the dialog
	 		              }
	 		          });
	 		    	  builder.setPositiveButton(R.string.confirm_del_yes, new DialogInterface.OnClickListener() {
	 		              public void onClick(DialogInterface dialog, int id) {
	 		    	    	  db.deleteTrip(curr_row + 1);
	 		    	    	  data.remove(curr_row);
			    	    	  top.remove(curr_row);
			    	    	  bottom.remove(curr_row);
	 		    	    	  adapter.notifyDataSetChanged();
	 		    	    	  Toast.makeText(getActivity(), "Trip Removed", Toast.LENGTH_SHORT).show();
	 		              }
	 		          });
	 		    	  // 3. Get the AlertDialog from create()
	 		    	  AlertDialog dialog = builder.create();
	 		    	  dialog.show();
	 		    	  
	 		      }
	 		  }
			return true;
	 	  }
	 	
	 	@Override
		public void onResume() {
	 		super.onResume();
	 		int numEntries = db.getTripCount();
			DecimalFormat df = new DecimalFormat("#.##");
			top.clear();
			bottom.clear();
			data.clear();
			
			for(int i=1; i<=numEntries; i++) {
				TripData trip = db.getTrip(i);
				Map<String, String> datum = new HashMap<String, String>(2);
				top.add(trip.getTitle());
				//bottom.add("Distance: " + df.format(db.getTripDist(i)) + "km\nDate: "+ db.getTripTime(i) +"\nAverage Economy: " + df.format(db.getAverageEcon(i)) + "L/100km\n");
				bottom.add("Date: "+ db.getTripTime(i) + "\nDescription: "+ trip.getDescription() + "\n");
				System.out.println("Main: " + top.get(i-1));
				System.out.println("Bottom: " + bottom.get(i-1));
		        datum.put("main", top.get(i-1));
		        datum.put("sub", bottom.get(i-1));
		        data.add(datum);
			}
			
			adapter = new SimpleAdapter(getActivity(), data, android.R.layout.simple_list_item_2, new String[] {"main", "sub"}, new int[] {android.R.id.text1, android.R.id.text2});
			registerForContextMenu(tripList);
	        tripList.setAdapter(adapter);
	 	}
	
}

