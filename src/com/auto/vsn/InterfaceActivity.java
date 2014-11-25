package com.auto.vsn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.auto.bt.DeviceListActivity;
import com.auto.data.DatabaseHandler;
import com.auto.data.TripData;
import com.auto.tab.FriendTab;
import com.auto.tab.PersonalTab;
import com.auto.tab.TripTab;
import com.google.android.gms.maps.model.LatLng;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class InterfaceActivity extends FragmentActivity {
	FragmentTransaction transaction;
	static ViewPager mViewPager;
	int selectedPosition;
	static int curr_tab = 0;
	
	static final int PAIR_DEVICE_REQUEST = 100;

	DatabaseHandler db;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(this);
        setContentView(R.layout.activity_interface);

        
        Fragment friendFragment = new FriendTab();
        Fragment personalFragment = new PersonalTab();
        Fragment tripFragment = new TripTab();
        
        PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(friendFragment);
        mPagerAdapter.addFragment(personalFragment);
        mPagerAdapter.addFragment(tripFragment);
        
        mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOffscreenPageLimit(3);
	    mViewPager.setCurrentItem(0);
		
		mViewPager.setOnPageChangeListener(
	        new ViewPager.SimpleOnPageChangeListener() {
	            @Override
	            public void onPageSelected(int position) {
	                getActionBar().setSelectedNavigationItem(position);
	            }
	        });

        ActionBar ab = getActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab tab1 = ab.newTab().setText("Friend Activities")
				.setTabListener(new TabListener<FriendTab>(
                        this, "tabf", FriendTab.class));
		Tab tab2 = ab.newTab().setText("Personal Stats")
				.setTabListener(new TabListener<PersonalTab>(
                        this, "tabp", PersonalTab.class));
		Tab tab3 = ab.newTab().setText("Saved Trips")
				.setTabListener(new TabListener<TripTab>(
                        this, "tabt", TripTab.class));
		
		ab.addTab(tab1);
		ab.addTab(tab2);
		ab.addTab(tab3);
		
    }
    
    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment mFragment;
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;

        public TabListener(Activity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
        }

        /* The following are each of the ActionBar.TabListener callbacks */

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            // Check if the fragment is already initialized
            if (mFragment == null) {
                // If not, instantiate and add it to the activity
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.add(android.R.id.content, mFragment, mTag);
                curr_tab = tab.getPosition();
            } else {
                // If it exists, simply attach it in order to show it
                ft.attach(mFragment);
                curr_tab = tab.getPosition();
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                // Detach the fragment, because another one is being attached
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // User selected the already selected tab. Usually do nothing.
        }

		public void onTabReselected(Tab arg0,
				android.app.FragmentTransaction arg1)
		{
			// TODO Auto-generated method stub
			
		}

		public void onTabSelected(Tab arg0, android.app.FragmentTransaction arg1)
		{
			// TODO Auto-generated method stub
			curr_tab = arg0.getPosition();
			mViewPager.setCurrentItem(arg0.getPosition());
		}

		public void onTabUnselected(Tab arg0,
				android.app.FragmentTransaction arg1)
		{
			// TODO Auto-generated method stub
			
		}
    }
    
    public class PagerAdapter extends FragmentPagerAdapter {

        private final ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

        public PagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
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
    	   intent = new Intent(this, DeviceListActivity.class);
   	       startActivityForResult(intent, PAIR_DEVICE_REQUEST);
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
 	 
 	@Override
 	public void onActivityResult(int requestCode, int resultCode, Intent data) {
 		
 		// Check for the if the result is the "right" one
 		if(requestCode == PAIR_DEVICE_REQUEST) {
 					 
 			// There are only 2 possible results, OK and CANCELED
 			// If the result is OK, it means Bluetooth is successfully paired and we can start UserStat
 			if(resultCode == RESULT_OK) {
 				Intent intent = new Intent(this, UserStatActivity.class);
 				startActivity(intent);
 				Toast.makeText(InterfaceActivity.this,
 						 "Bluetooth device paired", Toast.LENGTH_SHORT).show();
 			}
 					 
 			// If the result is CANCELED, it means either the user backs off from DeviceList
 			// or the user failed to find a Bluetooth Device to pair. Give them a Bluetooth
 			// environment not found reminder message.
 			if(resultCode == RESULT_CANCELED) {
 				Intent intent = new Intent(this, UserStatActivity.class);
 				startActivity(intent);
 				Toast.makeText(InterfaceActivity.this, 
 						"Bluetooth not supported or paired", Toast.LENGTH_SHORT).show();				 
 			}
 					 			 
 		}
 		
 	}
 	 
 	 public static int getTabPos() {
 		 return curr_tab;
 	 }
    
}

