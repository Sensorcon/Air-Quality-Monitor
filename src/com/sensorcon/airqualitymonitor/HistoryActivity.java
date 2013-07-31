package com.sensorcon.airqualitymonitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.sensorcon.airqualitymonitor.database.DBDataBlob;
import com.sensorcon.airqualitymonitor.database.DBDataHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.net.Uri;

public class HistoryActivity extends Activity {



	private DBDataHandler dbHandler;
	ArrayList<DBDataBlob> dbData;
	ListView historyList;
	HistoryAdapter lvAdapter;
	
	Activity myActivity;
	Context myContext;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_activity);
		
		myActivity = this;
		myContext = this;

		historyList = (ListView)findViewById(R.id.lvHistory);

		// Get the data
		dbHandler = new DBDataHandler(getApplicationContext());
		dbHandler.open();
		dbData = dbHandler.getAllData();
		dbHandler.close();

		// Load for display
		DBDataBlob[] blobs = new DBDataBlob[dbData.size()];
		for (int i=0; i < dbData.size(); i++) {
			blobs[i] = dbData.get(i);
		}
		lvAdapter = new HistoryAdapter(getApplicationContext(), blobs);
		historyList.setBackgroundColor(Color.BLACK);
		historyList.setAdapter(lvAdapter);

		historyList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				DBDataBlob selectedBlob = (DBDataBlob)arg0.getItemAtPosition(arg2);
				String timeStamp = selectedBlob.getDbDateTime().toString();
				String year = timeStamp.substring(0, 4);
				String month = timeStamp.substring(4, 6);
				String day = timeStamp.substring(6, 8);
				String hour = timeStamp.substring(9,11);
				String minute = timeStamp.substring(11,13);
				String second = timeStamp.substring(13,15);

				// Will need to separate for unit selection
				Intent reportIntent = new Intent(getApplicationContext(), ReportActivity.class);
				reportIntent.putExtra("date", month +"/" + day + "/" + year);
				reportIntent.putExtra("time", hour + ":" + minute + ":" + second);
				
				
				// Send value
				reportIntent.putExtra("temp", selectedBlob.getDbTemperature().getValue());
				reportIntent.putExtra("pressure", selectedBlob.getDbPressure().getValue());
				// Send String
				reportIntent.putExtra("humidity", selectedBlob.getDbHumidity().toString());
				reportIntent.putExtra("co", selectedBlob.getDbCO().toString());
				reportIntent.putExtra("co2", selectedBlob.getDbCO2().toString());
				reportIntent.putExtra("co_status", selectedBlob.getCoStatus());
				reportIntent.putExtra("co2_status", selectedBlob.getCo2Status());


				startActivity(reportIntent);


			}
		});


	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.history_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case R.id.email:
			File csv = null;
			try {
				dbHandler.open();
				csv = dbHandler.makeCSV();
				dbHandler.close();
			} catch (IOException e) {
				break;
			}

			Uri fileUri = Uri.fromFile(csv);
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Sensordrone AQIA Data");
			sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
			sendIntent.setType("text/html");
			startActivity(sendIntent);
			break;

		case R.id.keep_none:
			AlertDialog alert;
			AlertDialog.Builder builder = new Builder(myContext);
			builder.setTitle("Clear All Stored Data");
			builder.setMessage("Are you sure you want to clear the database of all logged data?");
			builder.setPositiveButton("Clear", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
//					dbHandler.open();
					dbHandler.clearDatabaseProgress(myContext, HistoryActivity.this);
//					dbHandler.close();
				}
			});
			builder.setNegativeButton("Cancel", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			
			alert = builder.create();
			alert.show();
			break;

		case R.id.historyHelp:
			TxtReader help = new TxtReader(myContext);
			help.displayTxtAlert("History", R.raw.history_help);
			break;
		}

		return true;
	}


	public void restart() {
		HistoryActivity.this.onCreate(null);
	}

}

