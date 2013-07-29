package com.sensorcon.airqualitymonitor.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;

public class DBDataHandler {
	
	private SQLiteDatabase database;
	private DBCreator dbCreator;
	
	public DBDataHandler(Context context) {
		dbCreator = new DBCreator(context);
	}
	
	public void open() throws SQLException {
		database = dbCreator.getWritableDatabase();
	}
	
	
	public void close() {
		dbCreator.close();
	}
	
	public long addData(DBDateTime dataTime, DBCO coData, DBCO2 co2Data, DBTemperature tempData, DBHumidity humidityData, DBPressure pressureData) {
		ContentValues dataValues = new ContentValues();
		dataValues.put(DBCreator.COLUMN_DATETIME, dataTime.getValue());
		dataValues.put(DBCreator.COLUMN_CO, coData.getValue());
		dataValues.put(DBCreator.COLUMN_CO2, co2Data.getValue());
		dataValues.put(DBCreator.COLUMN_TEMPERATURE, tempData.getValue());
		dataValues.put(DBCreator.COLUMN_HUMIDITY, humidityData.getValue());
		dataValues.put(DBCreator.COLUMN_PRESSURE, pressureData.getValue());
		
		long insertID = database.insert(DBCreator.DATABASE_TABLE, null, dataValues);
		return insertID;
	}
	
	public void clearDatabase() {
		Cursor myIDs = database.query(DBCreator.DATABASE_TABLE, DBCreator.ALL_COLUMNS, null, null, null, null, null);
		myIDs.moveToFirst();
		while (!myIDs.isAfterLast()) {
			int columnID = myIDs.getInt(myIDs.getColumnIndex(DBCreator.COLUNM_ID));
			String selection = DBCreator.COLUNM_ID + " LIKE ?";
			String selectionArgs[] = { String.valueOf(columnID) };
			database.delete(DBCreator.DATABASE_TABLE, selection, selectionArgs);
			myIDs.moveToNext();
		}
	}
	public ArrayList<DBDataBlob> getAllData() {
//		// How many data entries do we have?
//		String sql = "SELECT COUNT(*) FROM " + DBCreator.DATABASE_TABLE;
//	    SQLiteStatement statement = database.compileStatement(sql);
//	    long nRows = statement.simpleQueryForLong();
//		DBDataBlob[] myBlobs = new DBDataBlob[(int) nRows];
		ArrayList<DBDataBlob> myBlobs = new ArrayList<DBDataBlob>();
		
		// Get all of our Data
		Cursor myCursor = database.query(DBCreator.DATABASE_TABLE, DBCreator.ALL_COLUMNS, null, null, null, null, null);
		
		myCursor.moveToFirst();
		while (!myCursor.isAfterLast()) {
			int columnID = myCursor.getInt(myCursor.getColumnIndex(DBCreator.COLUNM_ID));
			String dateTime = myCursor.getString(myCursor.getColumnIndex(DBCreator.COLUMN_DATETIME));
			long coValue = myCursor.getLong(myCursor.getColumnIndex(DBCreator.COLUMN_CO));
			long co2Value = myCursor.getLong(myCursor.getColumnIndex(DBCreator.COLUMN_CO2));
			long tempValue =  myCursor.getLong(myCursor.getColumnIndex(DBCreator.COLUMN_TEMPERATURE));
			long humidityValue = myCursor.getLong(myCursor.getColumnIndex(DBCreator.COLUMN_HUMIDITY));
			long pressureValue = myCursor.getLong(myCursor.getColumnIndex(DBCreator.COLUMN_PRESSURE));
			DBDataBlob thisBlob = new DBDataBlob(columnID, dateTime, coValue, co2Value, tempValue, humidityValue, pressureValue);
			myBlobs.add(thisBlob);
			myCursor.moveToNext();
		}
		return myBlobs;
	}
	
	public void assignQualityLevel() {
		
	}
	
	public File makeCSV() throws IOException {
		File csv = null;
		File root = Environment.getExternalStorageDirectory();
		
		if (root.canWrite()) {
			File dir = new File (root.getAbsolutePath() + "/Sensordrone_AQM");
			dir.mkdirs();
		    csv   =   new File(dir, "Sensordrone_AQM.csv");
			FileOutputStream out = new FileOutputStream(csv);
			byte[] comma = ",".getBytes();
			byte[] newLine = "\n".getBytes();
			
			// Write the header
			for (int i=0; i < DBCreator.ALL_COLUMNS.length; i++) {
				out.write(DBCreator.ALL_COLUMNS[i].getBytes());
				out.write(DBCreator.ALL_COLOUMNS_UNITS[i].getBytes());
				out.write(comma);
			}
			out.write(newLine);
			
			
			// Get all of our Data
			Cursor myCursor = database.query(DBCreator.DATABASE_TABLE, DBCreator.ALL_COLUMNS, null, null, null, null, null);
			
			myCursor.moveToFirst();
			while (!myCursor.isAfterLast()) {
				int columnID = myCursor.getInt(myCursor.getColumnIndex(DBCreator.COLUNM_ID));
				String dateTime = myCursor.getString(myCursor.getColumnIndex(DBCreator.COLUMN_DATETIME));
				long coValue = myCursor.getLong(myCursor.getColumnIndex(DBCreator.COLUMN_CO));
				long co2Value = myCursor.getLong(myCursor.getColumnIndex(DBCreator.COLUMN_CO2));
				long tempValue =  myCursor.getLong(myCursor.getColumnIndex(DBCreator.COLUMN_TEMPERATURE));
				long humidityValue = myCursor.getLong(myCursor.getColumnIndex(DBCreator.COLUMN_HUMIDITY));
				long pressureValue = myCursor.getLong(myCursor.getColumnIndex(DBCreator.COLUMN_PRESSURE));
				DBDataBlob thisBlob = new DBDataBlob(columnID, dateTime, coValue, co2Value, tempValue, humidityValue, pressureValue);
				out.write(thisBlob.csvString().getBytes());
				myCursor.moveToNext();
			}
			
			
			// Close the stream
			out.close();
		}
		
		return csv;
	}

}
