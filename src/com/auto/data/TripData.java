package com.auto.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class TripData {
	private String date, trip_name, description, vehicle_name;
	private int trip_no;
	private ArrayList<Integer> speed = new ArrayList<Integer>();
	private ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
	private ArrayList<String> time = new ArrayList<String>();
	private ArrayList<Double> throttle = new ArrayList<Double>(),
							  fuel_level = new ArrayList<Double>(), 
							  fuel_economy = new ArrayList<Double>(), 
							  distance = new ArrayList<Double>(), 
							  engine_rpm = new ArrayList<Double>();
	private double current_fuel, trip_econ, trip_distance;
	
	public TripData() {
		trip_name = description = vehicle_name = date =  "";	
	}
	public TripData(String d) {
		date = d;
		trip_name = description = vehicle_name = "";
	}
	
	public int getTripNo() {
		return this.trip_no;
	}
	public void setTripNo(int t) {
		this.trip_no = t;
	}
	
	public String getDate() {
		return this.date;
	}
	public void setDate(String d) {
		this.date = d;
	}
	
	public double getCurrentFuel() {
		return this.current_fuel;
	}
	public void setCurrentFuel(int c) {
		this.current_fuel = c;
	}
	
	public double getTripEcon() {
		return this.trip_econ;
	}
	public void setTripEcon(int t) {
		this.trip_econ = t;
	}
	

	public double getTripDistance() {
		return this.trip_distance;
	}
	public void setTripDistance(int d) {
		this.trip_distance = d;
	}
	
	// Adding individual entries to the ArrayList data
	public void addRpm(double rpm) {
		this.engine_rpm.add(rpm);
	}
	public void addCoordinate(LatLng c) {
		this.coordinates.add(c);
	}
	public void addThrottle(double t) {
		this.throttle.add(t);
	}
	public void addFuelLevel(double f) {
		this.fuel_level.add(f);
	}
	public void addFuelEcon(double e) {
		this.fuel_economy.add(e);
	}
	public void addTime(String t) {
		this.time.add(t);
	}
	public void addSpeed(int s) {
		this.speed.add(s);
	}
	public void addDistance(double d) {
		this.distance.add(d);
	}
	
	// Setters for the Detail table
	public void setTitle(String t) {
		this.trip_name = t;
	}
	public void setDescription(String d) {
		this.description = d;
	}
	public void setVehicle(String v) {
		this.vehicle_name = v;
	}
	
	
	// Returns ArrayList data
	public ArrayList<Double> getRpm() {
		return this.engine_rpm;
	}
	public ArrayList<LatLng> getCoordinate() {
		return this.coordinates;
	}
	public ArrayList<Double> getThrottle() {
		return this.throttle;
	}
	public ArrayList<Double> getFuelLevel() {
		return this.fuel_level;
	}
	public ArrayList<Double> getFuelEcon() {
		return this.fuel_economy;
	}
	public ArrayList<String> getTime() {
		return this.time;
	}
	public ArrayList<Integer> getSpeed() {
		return this.speed;
	}
	public ArrayList<Double> getDistance() {
		return this.distance;
	}
	
	// Getters for the Detail table
	public String getTitle() {
		return this.trip_name;
	}
	public String getDescription() {
		return this.description;
	}
	public String getVehicle() {
		return this.vehicle_name;
	}
	
	// Returns individual data entry
	public double getRpm(int i) {
		return this.engine_rpm.get(i);
	}
	public LatLng getCoordinate(int i) {
		return this.coordinates.get(i);
	}
	public double getThrottle(int i) {
		return this.throttle.get(i);
	}
	public double getFuelLevel(int i) {
		return this.fuel_level.get(i);
	}
	public double getFuelEcon(int i) {
		return this.fuel_economy.get(i);
	}
	public String getTime(int i) {
		return this.time.get(i);
	}
	public int getSpeed(int s) {
		return this.speed.get(s);
	}
	public double getDistance(int d) {
		return this.distance.get(d);
	}
	
	public String printTrip() {
		String output = "";
		/*System.out.println("time in printTrip(): " + time.size());
		System.out.println("speed in printTrip(): " + speed.size());
		System.out.println("coord in printTrip(): " + coordinates.size());
		System.out.println("throttle in printTrip(): " + throttle.size());
		System.out.println("fuel in printTrip(): " + fuel_level.size());
		System.out.println("econ in printTrip(): " + fuel_economy.size());
		System.out.println("distance in printTrip(): " + distance.size());
		System.out.println("rpm in printTrip(): " + engine_rpm.size());
		
		for(int i=0; i<time.size(); i++) {
			//speed, coordinates, time, throttle, fuel_level, fuel_economy, distance, engine_rpm
			output += ("Time: " + time.get(i) + ", Speed: " + speed.get(i) + ", Long: " + coordinates.get(i).longitude
					 + ", Lat: " + coordinates.get(i).latitude + ", Throttle: " + throttle.get(i) + ", Fuel Level: " + fuel_level.get(i)
					 + ", Fuel Econ: " + fuel_economy.get(i) + ", Distance: " + distance.get(i) + ", RPM: " + engine_rpm.get(i) + "\n");
		}*/
		return output;
	}
	
	public void clearTrip() {
		time.clear();
		speed.clear();
		coordinates.clear();
		throttle.clear();
		fuel_level.clear();
		fuel_economy.clear();
		distance.clear();
		engine_rpm.clear();
	}
	
	public int size() {
		return time.size();
	}
	
}


