package com.sensorcon.airqualitymonitor;

public class Constants {

	// Keys for SharedPreferences
	final static String NEEDS_SETUP = "NEEDS_SETUP";
	final static String SD_MAC = "SD_MAC";
	final static String SERVICE_STATUS = "SERVICE_STATUS";
	final static String TEMPERATURE_UNIT = "TEMPERATURE_UNIT";
	final static String PRESSURE_UNIT = "PRESSURE_UNIT";
	final static String TIME_INTERVAL = "TIME_INTERVAL";
	
	// Values
	final static int FAHRENHEIT = 0;
	final static int CELCIUS = 1;
	
	final static int PASCAL = 0;
	final static int HECTOPASCAL = 1;
	final static int KILOPASCAL = 2;
	final static int ATMOSPHERE = 3;
	final static int MMHG = 4;
	final static int INHG = 5;
	
	final static int MINUTES_1 = 1;
	final static int MINUTES_5 = 5;	
	final static int MINUTES_15 = 15;
	final static int MINUTES_30 = 30;
	final static int MINUTES_60 = 60;
	
	public final static int CO_GOOD = 5;
	public final static int CO_MODERATE = 10;
	
	public final static int CO2_GOOD = 1000;
	public final static int CO2_MODERATE = 1500;
	
	public final static int STATUS_GOOD = 0;
	public final static int STATUS_MODERATE = 1;
	public final static int STATUS_BAD = 2;
	
	public final static float LOW_BATTERY_NOTIFY = (float) 3.35;

	public final static int NOTIFY_SERVICE_STATUS = 0;
	public final static int NOTIFY_LOW_BATTERY = 1;
	public final static int NOTIFY_AQ_STATUS = 2;

	final static String SERVICE_ON = "The monitoring service is running";
	final static String SERVICE_OFF = "The monitoring service is stopped";

	
}
