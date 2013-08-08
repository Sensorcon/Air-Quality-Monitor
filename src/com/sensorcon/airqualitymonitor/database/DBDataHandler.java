package com.sensorcon.airqualitymonitor.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.sensorcon.airqualitymonitor.HistoryActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Environment;

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
	
	public void loadDummyData() {
		DBDateTime dataTime = new DBDateTime(-1, "20130102_012345");
		DBCO coData = new DBCO(-1, 0);
		DBCO2 co2Data = new DBCO2(-1, 450);
		DBTemperature tempData = new DBTemperature(-1, 25);
		DBHumidity humidityData = new DBHumidity(-1, 50);
		DBPressure pressureData = new DBPressure(-1, 90000);
		ContentValues dataValues = new ContentValues();
		dataValues.put(DBCreator.COLUMN_DATETIME, dataTime.getValue());
		dataValues.put(DBCreator.COLUMN_CO, coData.getValue());
		dataValues.put(DBCreator.COLUMN_CO2, co2Data.getValue());
		dataValues.put(DBCreator.COLUMN_TEMPERATURE, tempData.getValue());
		dataValues.put(DBCreator.COLUMN_HUMIDITY, humidityData.getValue());
		dataValues.put(DBCreator.COLUMN_PRESSURE, pressureData.getValue());
		database.insert(DBCreator.DATABASE_TABLE, null, dataValues);
		
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
	
	public void clearDatabaseProgress(Context context, Activity activity) {
		
		AsyncDelete clearDB = new AsyncDelete();
		clearDB.SetContext(context);
		clearDB.SetActivityToRestart(activity);
		clearDB.execute();
	}
	
	public ArrayList<DBDataBlob> getAllData() {
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

	private class AsyncDelete extends AsyncTask<Void, Void, Void> {

		Activity activityToRestart;
		public void SetActivityToRestart(Activity activity) {
			this.activityToRestart = activity;
		}
		
		DBDataHandler dbHandler;
		ProgressDialog pDialog;
		Context context;
		public void SetContext(Context aContext) {
			this.context = aContext;
		}
		
		@Override
		protected void onPreExecute() {
			open();
			// Find out how many rows we have
			String sql = "SELECT COUNT(*) FROM " + DBCreator.DATABASE_TABLE;
		    SQLiteStatement statement = database.compileStatement(sql);
		    long nRows = statement.simpleQueryForLong();
			
		    // Set up our dialog
			pDialog = new ProgressDialog(context);
			pDialog.setTitle("Clearing Database");
			pDialog.setMessage("Measurements deleted:");
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(false);
			pDialog.setProgress(0);
			pDialog.setMax((int) nRows);
			pDialog.show();
				
		}
		@Override
		protected Void doInBackground(Void... params) {
			// Delete
						Cursor myIDs = database.query(DBCreator.DATABASE_TABLE, DBCreator.ALL_COLUMNS, null, null, null, null, null);
						myIDs.moveToFirst();
						while (!myIDs.isAfterLast()) {
							int columnID = myIDs.getInt(myIDs.getColumnIndex(DBCreator.COLUNM_ID));
							String selection = DBCreator.COLUNM_ID + " LIKE ?";
							String selectionArgs[] = { String.valueOf(columnID) };
							database.delete(DBCreator.DATABASE_TABLE, selection, selectionArgs);
							publishProgress(null);
							myIDs.moveToNext();
						}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {
			pDialog.incrementProgressBy(1);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			pDialog.dismiss();
			close();
			if (activityToRestart.getClass() == HistoryActivity.class) {
				((HistoryActivity)activityToRestart).restart();
			}
		}
	}
}
