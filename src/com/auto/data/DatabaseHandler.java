package com.auto.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.auto.data.TripData;
import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHandler extends SQLiteOpenHelper {
	
	// Database Version
	static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "tripManager";
	Context context;
	
	// Trip table name
	private static String TABLE_TRIP = "Trip";
	private static String TABLE_TRIP_DETAIL = "Details";
	
	// Trip table Column names
	private static final String KEY_TRIP = "trip_no";
	private static final String KEY_SPEED = "speed";
	private static final String KEY_TIME = "time";
	private static final String KEY_RPM = "rpm";
	private static final String KEY_THROTTLE = "throttle";
	private static final String KEY_FUEL = "fuel_level";
	private static final String KEY_ECON = "econ";
	private static final String KEY_LONG = "long";
	private static final String KEY_LAT = "lat";
	private static final String KEY_DIST = "distance";
	
	// Trip table Column names
	private static final String KEY_TITLE = "title";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_VEHICLE = "vehicle_name";

	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_TRIP_TABLE = "CREATE TABLE " + TABLE_TRIP + "(" 
				+ KEY_TRIP + " TEXT, " + KEY_SPEED + " TEXT, " + KEY_TIME + " TEXT, "
				+ KEY_RPM + " TEXT, " + KEY_THROTTLE + " TEXT, " + KEY_FUEL + " TEXT, " 
				+ KEY_ECON + " TEXT, " + KEY_LONG + " TEXT, " + KEY_LAT + " TEXT, " + KEY_DIST + " TEXT)";
		
		String CREATE_TRIP_DETAIL_TABLE = "CREATE TABLE " + TABLE_TRIP_DETAIL + "(" 
				+ KEY_TRIP + " TEXT PRIMARY KEY, " + KEY_TITLE + " TEXT, " + KEY_DESCRIPTION + " TEXT, " + KEY_VEHICLE + " TEXT)";

		System.out.print(CREATE_TRIP_TABLE);
		System.out.print(CREATE_TRIP_DETAIL_TABLE);
		db.execSQL(CREATE_TRIP_TABLE);
		db.execSQL(CREATE_TRIP_DETAIL_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP_DETAIL); 
        // Create tables again
        onCreate(db);	
	}
	
	public void executeSQLScript(SQLiteDatabase db, String dbname ) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = null;
		
		try {
			inputStream = assetManager.open(dbname);
			while((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
			
			String[] createScript = outputStream.toString().split(";");
			for(int i=0; i< createScript.length; i++) {
				String sqlStatement = createScript[i].trim();
				if(sqlStatement.length() > 0) {
					db.execSQL(sqlStatement + ";");
				}
			}
		} catch (IOException e){
	        // TODO Handle Script Failed to Load
	    } catch (SQLException e) {
	        // TODO Handle Script Failed to Execute
	    }
	}
	
	// Adding new trip
	public void addTrip(TripData trip) {
		System.out.println("Trip no: " + trip.getTripNo());
		System.out.println("Trip size: " + trip.getSpeed().size());
		System.out.println("Trip time: " + trip.getTime().size());
		System.out.println("Trip rpm: " + trip.getRpm().size());
		System.out.println("Trip throttle: " + trip.getThrottle().size());
		System.out.println("Trip fuel: " + trip.getFuelLevel().size());
		System.out.println("Trip econ: " + trip.getFuelEcon().size());
		System.out.println("Trip coord: " + trip.getCoordinate().size());
		System.out.println("Trip distance: " + trip.getDistance().size());
		
		SQLiteDatabase db = this.getWritableDatabase();
		System.out.println("Trip entries: " + trip.getFuelLevel().size());
		for(int i=0; i<trip.getFuelLevel().size(); i++) {
			ContentValues values = new ContentValues();
			values.put(KEY_TRIP, trip.getTripNo());
			values.put(KEY_SPEED, trip.getSpeed(i));
			values.put(KEY_TIME, trip.getTime(i));
			values.put(KEY_RPM, trip.getRpm(i));
			values.put(KEY_THROTTLE, trip.getThrottle(i));
			values.put(KEY_FUEL, trip.getFuelLevel(i));
			values.put(KEY_ECON, trip.getFuelEcon(i));
			values.put(KEY_LONG, trip.getCoordinate(i).longitude);
			values.put(KEY_LAT, trip.getCoordinate(i).latitude);
			values.put(KEY_DIST, trip.getDistance(i));
			db.insert(TABLE_TRIP, null, values);
		}
	
		ContentValues desc = new ContentValues();
		desc.put(KEY_TRIP, trip.getTripNo());
		desc.put(KEY_TITLE, "Trip " + trip.getTripNo());
		desc.put(KEY_DESCRIPTION, trip.getDescription());
		db.insert(TABLE_TRIP_DETAIL, null, desc);
		db.close();
	}
	 
	// Getting single trip
	public TripData getTrip(int trip_no) {
		SQLiteDatabase db = this.getWritableDatabase();
		TripData trip = new TripData();
		
		Cursor cursor = db.query(TABLE_TRIP, new String[] { 
				KEY_TRIP, KEY_SPEED, KEY_TIME, KEY_RPM, KEY_THROTTLE, KEY_FUEL, KEY_ECON, KEY_LONG, KEY_LAT, KEY_DIST }, KEY_TRIP + "=?", 
				new String[] { Integer.toString(trip_no) }, null, null, null, null);
		if(cursor != null) {
			// Looping through all rows and adding to list
			if(cursor.moveToFirst()) {
				do {
					System.out.println("Getting Cursor 0: " + cursor.getString(0) + " Cursor 1: " + cursor.getString(1) + " Cursor 2: " + cursor.getString(2) + "Cursor 3: " + cursor.getString(3) + "Cursor 4: " + cursor.getString(4) + "Cursor 5: " + cursor.getString(5) + "Cursor 6: " + cursor.getString(6) + "Cursor 7: " + cursor.getString(7) + "Cursor 8: " + cursor.getString(8));
					trip.setTripNo(cursor.getInt(0));
					trip.addSpeed(cursor.getInt(1));
					trip.addTime(cursor.getString(2));
					trip.addRpm(cursor.getInt(3));
					trip.addThrottle(cursor.getDouble(4));
					trip.addFuelLevel(cursor.getDouble(5));
					trip.addFuelEcon(cursor.getDouble(6));
					trip.addCoordinate(new LatLng(Double.parseDouble(cursor.getString(8)), Double.parseDouble(cursor.getString(7))));
					trip.addDistance(cursor.getDouble(9));
				} while (cursor.moveToNext());
			}
		}
		/*System.out.println("Trip no: " + trip_no);
		System.out.println("Trip size: " + trip.getSpeed().size());
		System.out.println("Trip time: " + trip.getTime().size());
		System.out.println("Trip rpm: " + trip.getRpm().size());
		System.out.println("Trip throttle: " + trip.getThrottle().size());
		System.out.println("Trip fuel: " + trip.getFuelLevel().size());
		System.out.println("Trip econ: " + trip.getFuelEcon().size());
		System.out.println("Trip coord: " + trip.getCoordinate().size());
		System.out.println("Trip dist: " + trip.getDistance().size());*/
		
		Cursor cursor2 = db.query(TABLE_TRIP_DETAIL, new String[] { 
				KEY_TITLE, KEY_DESCRIPTION, KEY_VEHICLE }, KEY_TRIP + "=?", 
				new String[] { Integer.toString(trip_no) }, null, null, null, null);
		if(cursor2 != null) {
			// Looping through all rows and adding to list
			if(cursor2.moveToFirst()) {
				do {
					System.out.println("Getting Cursor 0: " + cursor2.getString(0) + " Cursor 1: " + cursor2.getString(1) + " Cursor 2: " + cursor2.getString(2));
					trip.setTitle(cursor2.getString(0));
					trip.setDescription(cursor2.getString(1));
					trip.setVehicle(cursor2.getString(2));
				} while (cursor2.moveToNext());
			}
		}		
		
		return trip;
	}

	// Getting trip Count
	public int getTripCount() {
		String countQuery = "SELECT COUNT(DISTINCT " + KEY_TRIP + ") FROM " + TABLE_TRIP;
		System.out.println(countQuery);
		int count = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		
		if(cursor != null){
			if(cursor.moveToFirst()) {
				count = cursor.getInt(0);
			}
				
	    }
		cursor.close();
        return count;
	}
	
	public int getDbCount() {
		String countQuery = "SELECT * FROM " + TABLE_TRIP;
		int count = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		count = cursor.getCount();
		cursor.close();
 
        // return count
        return count;
	}
	
	public boolean hasTrip(String time_stamp) {
		int count = getDbCount();
		System.out.println("Db Count: " + count);
		
		String countQuery = "SELECT * FROM " + TABLE_TRIP + " WHERE " + KEY_TIME + " = '" + time_stamp + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		count = cursor.getCount();
		
		if(count > 0) {
			System.out.println("Found");
			cursor.close();
			return true;
		}
			
		System.out.println("Not found");
		return false;
	}

	
	// Get trip time of trip trip_no
	public String getTripTime(int trip_no) {
		String time = "";
		
		String countQuery = "SELECT " + KEY_TIME +" FROM " + TABLE_TRIP + " WHERE " + KEY_SPEED + " = 0 AND " + KEY_TRIP + " = " + trip_no + " LIMIT 1";
		System.out.println(countQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		
		if(cursor != null){
			if(cursor.moveToFirst()) {
				time = cursor.getString(0);
				System.out.println(time);
			}		
	    }
		cursor.close();
		return time;
	}
			
		
	
	// Calculates the average fuel for all available trips
	public double getAverageEcon() {
		double average = 0.0;
		int numTrips = getTripCount();
		
		for(int i=1; i<=numTrips; i++) {
			average += getAverageEcon(i);
		}
		if(numTrips != 0)
			return average/numTrips;
		else
			return 0.0;
	}
	
	// Calculates the average fuel for trip trip_no
	public double getAverageEcon(int trip_no) {
		double econ = -1.0;
		
		String countQuery = "SELECT " + KEY_ECON + ", " + KEY_DIST + " FROM " + TABLE_TRIP + " WHERE " + KEY_TRIP + " = " + trip_no + " ORDER BY " + KEY_DIST + " DESC LIMIT 1";
		System.out.println(countQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		
		if(cursor != null){
			if(cursor.moveToFirst()) {
				econ = cursor.getDouble(0);
				System.out.println(econ);
			}		
	    }
		cursor.close();
		return econ;
	}
	
	// Calculates the total distance for all available trips
	public double getTotalDist() {
		double total = 0.0;
		int numTrips = getTripCount();
		
		for(int i=1; i<=numTrips; i++) {
			total += getTripDist(i);
		}
		if(numTrips != 0)
			return total;
		else
			return 0.0;
	}
	
	
	// Calculates the distance trip trip_no
	public double getTripDist(int trip_no) {
		double final_dist = -1.0;
		double init_dist = -1.0;
		
		String countQuery = "SELECT " + KEY_DIST +" FROM " + TABLE_TRIP + " WHERE " + KEY_TRIP + " = " + trip_no + " ORDER BY " + KEY_DIST + " DESC LIMIT 1";
		System.out.println(countQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		
		if(cursor != null){
			if(cursor.moveToFirst()) {
				final_dist = cursor.getDouble(0);
				System.out.println(final_dist);
			}		
	    }
		
		countQuery = "SELECT " + KEY_DIST +" FROM " + TABLE_TRIP + " WHERE " + KEY_TRIP + " = " + trip_no + " ORDER BY " + KEY_DIST + " LIMIT 1";
		System.out.println(countQuery);
		db = this.getReadableDatabase();
		cursor = db.rawQuery(countQuery, null);
		
		if(cursor != null){
			if(cursor.moveToFirst()) {
				init_dist = cursor.getDouble(0);
				System.out.println(init_dist);
			}		
	    }
		cursor.close();
		return final_dist - init_dist;
	}
	
	// Calculates the average fuel for trip trip_no
	public double getFuelLevel() {
		double econ = -1.0;
		
		String countQuery = "SELECT " + KEY_FUEL + ", " + KEY_DIST + " FROM " + TABLE_TRIP + " WHERE " + KEY_TRIP + " = " + getTripCount() + " ORDER BY " + KEY_DIST + " DESC LIMIT 1";
		System.out.println(countQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		
		if(cursor != null){
			if(cursor.moveToFirst()) {
				econ = cursor.getDouble(0);
				System.out.println(econ);
			}		
	    }
		cursor.close();
		return econ;
	}
	
	public ArrayList<LatLng> getCoords(int trip_no) {
		ArrayList<LatLng> coords = new ArrayList<LatLng>();
		
		String countQuery = "SELECT" + KEY_LAT + ", " + KEY_LONG +  " FROM " + TABLE_TRIP + " WHERE " + KEY_TRIP + " = " + trip_no;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		if(cursor != null) {
			// Looping through all rows and adding to list
			if(cursor.moveToFirst()) {
				do {
					System.out.println("Getting Cursor 0: " + cursor.getString(0) + "Cursor 1: " + cursor.getString(1));
					LatLng temp = new LatLng(cursor.getDouble(0), cursor.getDouble(1));
					coords.add(temp);
				} while (cursor.moveToNext());
			}
		}
		return coords;
	}
	
	// Deleting single contact
	public void deleteTrip(int trip_no) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_TRIP, KEY_TRIP + " = ?",
				new String[] { Integer.toString(trip_no) });
		db.close();
	}
	
	// Calculates the average fuel for trip trip_no
	public void updateDesc(String trip_no, String vehicle, String title, String desc) {
		
		/*String Query = "UPDATE " + TABLE_TRIP_DETAIL + " SET " + KEY_TITLE + " = '" + title + "', " + KEY_DESCRIPTION + " = '" + desc + "', " + KEY_VEHICLE + " = '" + vehicle + "' WHERE " + KEY_TRIP + " = " + trip_no;
		System.out.println(Query);*/
		SQLiteDatabase db = this.getReadableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(KEY_TITLE, title);
		cv.put(KEY_DESCRIPTION, desc);
		cv.put(KEY_VEHICLE, vehicle);
		db.update(TABLE_TRIP_DETAIL, cv, "trip_no = " + trip_no, null);
	}
}
