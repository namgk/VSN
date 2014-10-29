package com.auto.data;

public class FriendDetails {
	int profile_pic;
	String name;
	String vehicle;
	String desc;
	String time;
	
	public FriendDetails() {
		// Empty Constructor
	}
	
	public FriendDetails(int pic, String name, String vehicle, String desc, String time) {
		this.profile_pic = pic;
		this.name = name;
		this.vehicle = vehicle;
		this.desc = desc;
		this.time = time;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getVehicle() {
		return vehicle;
	}
	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}
	
	public int getProfilePic() {
		return profile_pic;
	}
	public void setIcon(int profile_pic) {
		this.profile_pic = profile_pic;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
