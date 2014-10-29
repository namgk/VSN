package com.auto.tab;

import java.util.ArrayList;

import com.auto.data.FriendDetails;
import com.auto.vsn.R;
import com.auto.vsn.R.id;
import com.auto.vsn.R.layout;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendAdapter extends BaseAdapter {
	
	private ArrayList<FriendDetails> mData;
	Context mContext;
	
	FriendAdapter (ArrayList data, Context c) {
		mData = data;
		mContext = c;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View v = arg1;
		if(v == null) {
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_row, null);
		}
		ImageView image = (ImageView) v.findViewById(R.id.icon);
		TextView fromView = (TextView)v.findViewById(R.id.From);
        TextView subView = (TextView)v.findViewById(R.id.subject);
        TextView descView = (TextView)v.findViewById(R.id.description);
        TextView timeView = (TextView)v.findViewById(R.id.time);
		
        FriendDetails details = mData.get(arg0); // get position
        image.setImageResource(details.getProfilePic());
        fromView.setText(details.getName());
        subView.setText("Trip Stats: " + details.getVehicle());
        descView.setText(details.getDesc());
        timeView.setText(details.getTime());
        
        return v;
        
	}

}
