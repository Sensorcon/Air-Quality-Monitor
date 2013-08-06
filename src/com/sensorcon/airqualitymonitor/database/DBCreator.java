package com.sensorcon.airqualitymonitor.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBCreator extends SQLiteOpenHelper {

	// Database stuff
	private static final String DATABASE_NAME = "aqm.db";
	private static final int DATABASE_VERSION = 1;

	public static final String DATABASE_TABLE = "aqm_data";
	public static final String COLUNM_ID = "_id";
	public static final String COLUMN_DATETIME = "date_time";
	public static final String COLUMN_CO = "co_data";
	public static final String COLUMN_CO2 = "co2_data";
	public static final String COLUMN_HUMIDITY = "humidity_data";
	public static final String COLUMN_TEMPERATURE = "temperature_data";
	public static final String COLUMN_PRESSURE = "pressure_data";

	public static final String[] ALL_COLUMNS = {
		COLUNM_ID,
		COLUMN_DATETIME,
		COLUMN_CO,
		COLUMN_CO2,
		COLUMN_HUMIDITY,
		COLUMN_TEMPERATURE,
		COLUMN_PRESSURE
	};
	
	public static final String[] ALL_COLOUMNS_UNITS = {
		"" ,
		" (yyyyMMdd_HHmmss)",
		" (ppm)",
		" (ppm)",
		" (%)",
		" (C)",
		" (Pa)"
	};
	
	
	// Create the table
	private static final String DATABASE_CREATE_TABLE = "create table "
			+ DATABASE_TABLE + " ( " 
			+ COLUNM_ID + " integer primary key autoincrement, "
			+ COLUMN_DATETIME + " text not null, "
			+ COLUMN_CO + " real not null ,"
			+ COLUMN_CO2 + " real not null ,"
			+ COLUMN_HUMIDITY + " real not null ,"
			+ COLUMN_TEMPERATURE + " real not null ,"
			+ COLUMN_PRESSURE + " real not null) ;";
			
	
	public DBCreator(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DBCreator.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		    db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
		    onCreate(db);		
	}

}
