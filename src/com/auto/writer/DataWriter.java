package com.auto.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Environment;

public class DataWriter {
	
	private String time;
	private int speed;
	private double lat;
	private double lng;
	private double rpm;
	private double throttle;
	private double fuel_level;
	private double dist;
	private double fuel_econ;
	
	File currentFile;
	
	public DataWriter() {
		
		this.speed = 0;
		this.lat = 0;
		this.lng = 0;
		this.rpm = 0;
		this.throttle = 0;
		this.fuel_level = 0;
		this.dist = 0;
		this.fuel_econ = 0;
		setTime();
		initialize();
		
	}
	
	@SuppressLint("SimpleDateFormat")
	public void initialize() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
		Date date = new Date();
		String today = sdf.format(date);
		
		File sdDir = Environment.getExternalStorageDirectory();
		File file = new File(sdDir + "/sdLogs/", "sdLog_" + today + ".csv");	
		
		currentFile = file;
		
		try {
			
			FileWriter fw = new FileWriter(file);
	        BufferedWriter writer = new BufferedWriter( fw );
			
			file.createNewFile();
			
			writer.write("System Time");
            writer.write("Speed (km/h)");
            writer.write("Latitude");
            writer.write("Longitude");
            writer.write("RPM");
            writer.write("Throttle %");
            writer.write("Fuel Level %");
            writer.write("Distance Travelled (km)");
            writer.write("Fuel Economy (L/100km)");
            writer.newLine();
            
    		writer.flush();
    		writer.close();
    		fw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void setTime() {
		Calendar calendar = Calendar.getInstance();
		this.time = new Timestamp(calendar.getTime().getTime()).toString();
	}
	
	public void setSpeed(int spd) {
		this.speed = spd;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public void setLng(double lng) {
		this.lng = lng;
	}
	
	public void setRPM(double rpm) {
		this.rpm = rpm;
	}
	
	public void setThrottle(double throttle) {
		this.throttle = throttle;
	}
	
	public void setFuelLevel(double fuel_level) {
		this.fuel_level = fuel_level;
	}
	
	public void setDistance(double dist) {
		this.dist = dist;
	}
	
	public void setFuelEcon(double fuel_econ) {
		this.fuel_econ = fuel_econ;
	}
	
	public void write() throws IOException {
		
		File file = currentFile;  
        
       	FileWriter fw = new FileWriter(file, true);
       	BufferedWriter writer = new BufferedWriter( fw );

       	writer.write(this.time);
       	writer.write(Integer.toString(this.speed));
       	writer.write(Double.toString(this.lat));
       	writer.write(Double.toString(this.lng));
       	writer.write(Double.toString(this.rpm));
       	writer.write(Double.toString(this.throttle));
       	writer.write(Double.toString(this.fuel_level));
       	writer.write(Double.toString(this.dist));
       	writer.write(Double.toString(this.fuel_econ));
       	writer.newLine();
        
       	writer.flush();
       	writer.close();
       	fw.close();
        
	}

}
