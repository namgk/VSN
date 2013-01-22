package com.auto.vsn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.TextView;

public class UserStatActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_stat);
		
		String temp = readStorage();
		TextView display = (TextView) findViewById(R.id.user_stat);
		display.setText(temp);
		display.setMovementMethod(new ScrollingMovementMethod());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_user_stat, menu);
		return true;
	}
	
	public String readStorage() {
		File sdDir = Environment.getExternalStorageDirectory();
		File log = null;
		String text = "";
		
		if(sdDir != null) {
			log = new File(sdDir, "torqueTrackLog.csv");
		}
		if(log != null) {
			System.out.println("log not null");
			try {
				text = IOUtils.toString(new FileInputStream(log));
				System.out.println(text);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return text;
	}
}
