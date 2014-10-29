package com.auto.data;

import com.auto.vsn.UserStatActivity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.lang.Math;


public class Accelerometer implements SensorEventListener {
	private float xAxis;
	private float yAxis;
	private float zAxis;
	
	private float prev_xAxis = 0;
	private float prev_yAxis = 0;
	private float prev_zAxis = 0;
	
	private Activity activity;
	SensorManager manager;
	Sensor accelerometer;
	
	
	public Accelerometer(Activity activity) {
		this.activity = activity;
		manager = (SensorManager) this.activity.getSystemService(Context.SENSOR_SERVICE);
		accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
		manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
	}
	
	public float getX() {
		return this.xAxis;
	}
	
	public float getY() {
		return this.yAxis;
	}
	
	public float getZ() {
		return this.zAxis;
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		if(Math.abs(arg0.values[0] - prev_xAxis) > 0.02 && Math.abs(arg0.values[1] - prev_yAxis) > 0.02 && Math.abs(arg0.values[2] - prev_zAxis) > 0.02) {
			xAxis = arg0.values[0];
			yAxis = arg0.values[1];
			zAxis = arg0.values[2];
			UserStatActivity.updateAccelerometer(xAxis, yAxis, zAxis);
			prev_xAxis = xAxis;
			prev_yAxis = yAxis;
			prev_zAxis = zAxis;
		}
	}
}
