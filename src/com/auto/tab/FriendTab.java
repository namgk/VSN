package com.auto.tab;

import java.util.ArrayList;

import com.auto.data.FriendDetails;
import com.auto.vsn.R;
import com.auto.vsn.R.drawable;
import com.auto.vsn.R.id;
import com.auto.vsn.R.layout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class FriendTab extends Fragment {
	ListView friendList;
	ArrayList<FriendDetails> details = new ArrayList<FriendDetails>();
	AdapterView.AdapterContextMenuInfo info;

	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	 }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_friend_tab, container, false);
		friendList = (ListView) view.findViewById(R.id.MessageList);
		  
		details.add(new FriendDetails(R.drawable.ic_launcher, "Billy Bob", "2001 Toyota Avalon", "Economy: 10L/100km", "12/12/2012 12:12"));
		details.add(new FriendDetails(R.drawable.ic_launcher, "Mitt Romney", "2013 Chrysler 300c", "Economy: 12L/100km", "13/12/2012 10:12"));
		details.add(new FriendDetails(R.drawable.ic_launcher, "Andy Li", "1998 Acura Integra", "Economy: 9.2L/100km", "11/12/2012 04:12"));
		details.add(new FriendDetails(R.drawable.ic_launcher, "Michael Chiang", "2012 Mercedes-Benz CLS550", "Economy: 13.5L/100km", "13/12/2012 02:12"));
		details.add(new FriendDetails(R.drawable.ic_launcher, "Calvin Ng", "2012 Subaru Impreza", "Economy: 9.8L/100km", "13/12/2012 06:12"));
		details.add(new FriendDetails(R.drawable.ic_launcher, "Ronald Ko", "2007 Acura CSX", "Economy: 10.5L/100km", "13/12/2012 11:12"));
		 
		friendList.setAdapter(new FriendAdapter(details, getActivity()));
		 
		return view;
	}
}
