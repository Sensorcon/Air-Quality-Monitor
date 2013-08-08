package com.sensorcon.airqualitymonitor;

import java.util.ArrayList;

import com.sensorcon.airqualitymonitor.database.DBDataBlob;
import com.sensorcon.airqualitymonitor.database.DBDataHandler;



import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class AirQualityMonitor extends Activity {

	public Button btnStart;
	public Button btnStop;
	public Button btnStatus;

	private DBDataHandler dbHandler;

	NotificationManager notifier;
	Builder notifyTest;

	SharedPreferences myPreferences;
	Editor prefEditor;

	TextSwitcher aqStatus;

	TextSwitcher tsTemperature;
	TextSwitcher tsHumidity;
	TextSwitcher tsPressure;

	TextSwitcher tsTimeStamp;

	TextSwitcher tsGasData;
	
	ViewFactory ctvGasFactory;
	ViewFactory tvDataFactory;
	ViewFactory tvTimeFactory;

	Animation in;
	Animation out;
	
	ViewFactory faceFactory;
	ImageSwitcher faceSwitcher;
	
	Activity myActivity;
	Context myContext;
	
	ImageView ivInfo;
	
	boolean faceAnimateToggle;
	boolean isMeasuring;
	
	public void setIsMeasuring(boolean status) {
		this.isMeasuring = status;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_air_quality_monitor);

		RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
		mainLayout.setBackgroundResource(R.drawable.bg_gradient);

		myActivity = this;
		myContext = this;
				
		myPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		prefEditor = myPreferences.edit();

		faceFactory = new ViewFactory() {
			
			@Override
			public View makeView() {
				ImageView fView = new ImageView(getApplicationContext());
				fView.setScaleType(ScaleType.CENTER);
				fView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// Don't allow multiple measurements at once
						// here only. Too easy for people to click the face
						// too man times.
						if (!isMeasuring) {
							takeMeasurement();
						}
					}
				});
				return fView;
			}
		};
		

		in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
		out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

		tvDataFactory = new ViewFactory() {

			@Override
			public View makeView() {
				TextView tv = new TextView(getApplicationContext());
				tv.setGravity(Gravity.RIGHT);
				tv.setTextColor(Color.BLACK);
				tv.setTextSize(20);
				return tv;
			}
		};
		
		aqStatus = (TextSwitcher)findViewById(R.id.tsAQStatus);
		aqStatus.setInAnimation(in);
		aqStatus.setOutAnimation(out);
		aqStatus.setFactory(tvDataFactory);
		
		faceSwitcher = (ImageSwitcher)findViewById(R.id.isFace);
		faceSwitcher.setFactory(faceFactory);
		faceSwitcher.setInAnimation(this, android.R.anim.fade_in);
		faceSwitcher.setOutAnimation(this, android.R.anim.fade_out);

		tvTimeFactory = new ViewFactory() {

			@Override
			public View makeView() {
				TextView tv = new TextView(getApplicationContext());
				tv.setGravity(Gravity.LEFT);
				tv.setTextColor(Color.BLACK);
				tv.setTextAppearance(getApplicationContext(), android.R.attr.textAppearanceSmall);
				return tv;
			}
		};

		tsTemperature = (TextSwitcher)findViewById(R.id.textSwitcher1);
		tsHumidity = (TextSwitcher)findViewById(R.id.textSwitcher2);
		tsPressure = (TextSwitcher)findViewById(R.id.textSwitcher3);
		
		tsTimeStamp = (TextSwitcher)findViewById(R.id.tsLastUpdate);

		tsTemperature.setInAnimation(in);
		tsHumidity.setInAnimation(in);
		tsPressure.setInAnimation(in);

		tsTimeStamp.setInAnimation(in);

		tsTemperature.setOutAnimation(out);
		tsHumidity.setOutAnimation(out);
		tsPressure.setOutAnimation(out);

		tsTimeStamp.setOutAnimation(out);

		tsTemperature.setFactory(tvDataFactory);
		tsHumidity.setFactory(tvDataFactory);
		tsPressure.setFactory(tvDataFactory);

		
		tsTimeStamp.setFactory(tvTimeFactory);

		ctvGasFactory = new ViewFactory() {
			
			@Override
			public View makeView() {
				CircularTextView ctv = new CircularTextView(getApplicationContext());
				ctv.setTextSize(20);
				ctv.setTextColor(Color.BLACK);
				return ctv;
			}
		};
		tsGasData = (TextSwitcher)findViewById(R.id.tsGasData);
		tsGasData.setInAnimation(this, android.R.anim.fade_in);
		tsGasData.setOutAnimation(this, android.R.anim.fade_out);
		tsGasData.setFactory(ctvGasFactory);
		
		
		dbHandler = new DBDataHandler(getApplicationContext());

		ivInfo = (ImageView)findViewById(R.id.ivInfo);
		ivInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showAQInfo();
			}
		});


	}

	public void updateDisplay() {
		dbHandler.open();
		ArrayList<DBDataBlob> dbData = dbHandler.getAllData();
		dbHandler.close();
		
		// How many rows are there? 0 indicates an empty database!
		int nEntries = dbData.size();
		int lastItem = nEntries -1;
		
		
		if (nEntries == 0) {
			tsTemperature.setText("N/A");
			tsHumidity.setText("N/A");
			tsPressure.setText("N/A");

			tsGasData.setText("N/A");
			((CircularTextView)tsGasData.getChildAt(0)).setStatusNull();
			((CircularTextView)tsGasData.getChildAt(1)).setStatusNull();

			tsTimeStamp.setText("Last measured: N/A");
			
			aqStatus.setText("N/A");
			
			AlertDialog alert;
			AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
			builder.setTitle("No Data Found");
			builder.setMessage("No stored data was found! Select \"Take a Measurement\" from the menu, " +
			"or click the face to get a reading!");
			builder.setPositiveButton("Measure Now!", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					takeMeasurement();
				}
			});
			builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//
				}
			});
			
			alert = builder.create();
			alert.show();
			faceSwitcher.setImageResource(R.drawable.face_unknown);
			return;
		}


		// Parse units here
		int tempPref = myPreferences.getInt(Constants.TEMPERATURE_UNIT, Constants.CELCIUS);
		if (tempPref != Constants.CELCIUS) {
			long celcius = dbData.get(lastItem).getDbTemperature().getValue();
			double fahrenheit = celcius * (9.0 / 5.0) + 32;
			tsTemperature.setText(String.format("%.0f", fahrenheit) + " F");
		} else {
			tsTemperature.setText(dbData.get(lastItem).getDbTemperature().toString() + " C");
		}
		
		float pValue = dbData.get(lastItem).getDbPressure().getValue();
		int pPref = myPreferences.getInt(Constants.PRESSURE_UNIT, Constants.PASCAL);
		if (pPref == Constants.HECTOPASCAL) {
			tsPressure.setText(String.format("%.2f", pValue/100) + " hPa");
		} else if (pPref == Constants.KILOPASCAL) {
			tsPressure.setText(String.format("%.3f", pValue/1000) + " kPa");
		} else if (pPref == Constants.ATMOSPHERE) {
			tsPressure.setText(String.format("%.3f", pValue * 0.00000986923267) + " atm");
		} else if (pPref == Constants.MMHG) {
			tsPressure.setText(String.format("%.0f", pValue * 0.00750061683) + " mmHg");
		} else if (pPref == Constants.INHG) {
			tsPressure.setText(String.format("%.2f", pValue * 0.000295299830714) + " inHg");
		} else {
			tsPressure.setText(dbData.get(lastItem).getDbPressure().toString() + " Pa");
		}
		
		tsHumidity.setText(dbData.get(lastItem).getDbHumidity().toString() + " %");
		
		String gasData = "";
		// Don't show CO2 if -1
		if (dbData.get(lastItem).getDbCO2().getValue() != -1) {
			gasData = dbData.get(lastItem).getDbCO2().toString() + " ppm CO2\n\n";
		} 
		gasData += dbData.get(lastItem).getDbCO().toString()+ " ppm CO";
		tsGasData.setText(gasData);

		tsTimeStamp.setText("Last measured: " + dbData.get(lastItem).getDbDateTime().getMMDDYYYY() + 
				" " + dbData.get(lastItem).getDbDateTime().getTimeStamp());

		assessQuality(dbData.get(lastItem).getStatus());
	}

	public void assessQuality(int level) {
		// Update good/bad display here

		if (level == Constants.STATUS_GOOD) {
			aqStatus.setText("Good");
			((CircularTextView)tsGasData.getChildAt(0)).setStatusGood();
			((CircularTextView)tsGasData.getChildAt(1)).setStatusGood();
			faceSwitcher.setImageResource(R.drawable.face_good);
		} else if (level == Constants.STATUS_MODERATE) {
			aqStatus.setText("Moderate");
			((CircularTextView)tsGasData.getChildAt(0)).setStatusModerate();
			((CircularTextView)tsGasData.getChildAt(1)).setStatusModerate();
			faceSwitcher.setImageResource(R.drawable.face_moderate);
		} else if (level == Constants.STATUS_BAD) {
			aqStatus.setText("Bad");
			((CircularTextView)tsGasData.getChildAt(0)).setStatusBad();
			((CircularTextView)tsGasData.getChildAt(1)).setStatusBad();
			faceSwitcher.setImageResource(R.drawable.face_bad);
		} else {
			
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.air_quality_advanced, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.history:
			Intent showHistory = new Intent(this, HistoryActivity.class);
			startActivity(showHistory);
			break;
		case R.id.action_settings:
			Intent showPrefs = new Intent(this, PreferenceActivity.class);
			startActivity(showPrefs);
			break;
		case R.id.refresh:
			updateDisplay();
			break;
		case R.id.measure:
			takeMeasurement();
			break;
		case R.id.mainHelp:
			TxtReader help = new TxtReader(myContext);
			help.displayTxtAlert("About", R.raw.main_help);
			break;
		case R.id.aqInfo:
			showAQInfo();
			break;
		}
		
			
		return true;
	}

	public void showAQInfo() {
		AlertDialog alert;
		AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
		builder.setTitle("Air Quality Information");
		builder.setMessage("Which gas would like you like to know more information about?");
		builder.setPositiveButton("Carbon Monoxide", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent coIntent = new Intent(myContext, COInfo.class);
				startActivity(coIntent);
			}
		});
		builder.setNegativeButton("Carbon Dioxide", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent co2Intent = new Intent(myContext, CO2Info.class);
				startActivity(co2Intent);
			}
		});
		alert = builder.create();
		alert.show();
	}
	
	public void takeMeasurement() {
		String MAC = myPreferences.getString(Constants.SD_MAC, "");
		if (MAC.equals("")) {
			// Launch settings activity
			Intent setupDrone = new Intent(getApplicationContext(), PreferenceActivity.class);
			setupDrone.putExtra(Constants.NEEDS_SETUP, true);
			startActivity(setupDrone);
			return;
		}
		faceSwitcher.setImageResource(R.drawable.face_unknown);
		DataSync getData = new DataSync(getApplicationContext(), AirQualityMonitor.this);
		getData.setSdMC(MAC);
		getData.setContext(myContext);
		getData.execute();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateDisplay();
	}

	public void animateFace() {
		if (faceAnimateToggle) {
			faceSwitcher.setImageResource(R.drawable.face_unknown_1);
		} else {
			faceSwitcher.setImageResource(R.drawable.face_unknown_2);
		}
		faceAnimateToggle = !faceAnimateToggle;
	}

	
}
