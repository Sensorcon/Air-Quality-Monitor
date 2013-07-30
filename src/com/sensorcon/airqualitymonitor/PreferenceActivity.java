package com.sensorcon.airqualitymonitor;

import com.sensorcon.sensordrone.DroneEventHandler;
import com.sensorcon.sensordrone.DroneEventObject;
import com.sensorcon.sensordrone.DroneEventObject.droneEventType;
import com.sensorcon.sensordrone.android.Drone;
import com.sensorcon.sensordrone.android.tools.DroneConnectionHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.rtp.RtpStream;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class PreferenceActivity extends Activity{

	Drone myDrone;
	DroneConnectionHelper myDroneHelper;
	DroneEventHandler myHandler;
	String storedDrone;
	
	SharedPreferences myPreferences;
	Editor prefEditor;
	Intent aqmService;
	boolean serviceStatus;

	
	Activity myActivity;
	Context myContext;
	
	RadioButton rF;
	RadioButton rC;
	int tempUnit;
	
	RadioButton rPa;
	RadioButton rhPa;
	RadioButton rkPa;
	RadioButton rAtm;
	RadioButton rmmHg;
	RadioButton rinHg;
	int pUnit;
	
	RadioButton rOff;
	RadioButton r1;
	RadioButton r5;
	RadioButton r15;
	RadioButton r30;
	RadioButton r60;
	int tUnit;
	
	TextView tvServiceStatus;
	TextView tvStoredDrone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preference_activity);

		
		tvStoredDrone = (TextView)findViewById(R.id.tvServiceDrone);
		tvServiceStatus = (TextView)findViewById(R.id.tvServiceStatus);
		
		// Handy to have
		myActivity = this;
		myContext = this;
		
		// Preferences
		myPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		prefEditor = myPreferences.edit();
		
		getPreferences();
		
		
		// Our background monitoring service
		aqmService = new Intent(getApplicationContext(), DroneAQMService.class);

		// Were we started with the intention of setting up the drone?
		Intent myIntent = getIntent();
		if (myIntent != null) {
			boolean needsSetup = myIntent.getBooleanExtra(Constants.NEEDS_SETUP, false);
			if (needsSetup) {
				setupDrone(true);
			}
		}// End of Intent Checking
		
		// Temperature Radios
		rF = (RadioButton)findViewById(R.id.radioF);
		rF.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tempUnit = Constants.FAHRENHEIT;
				prefEditor.putInt(Constants.TEMPERATURE_UNIT, tempUnit);
				prefEditor.commit();
			}
		});
		rC = (RadioButton)findViewById(R.id.radioC);
		rC.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tempUnit = Constants.CELCIUS;
				prefEditor.putInt(Constants.TEMPERATURE_UNIT, tempUnit);
				prefEditor.commit();
			}
		});
		
		if (tempUnit == Constants.FAHRENHEIT) {
			rF.setChecked(true);
		} else {
			rC.setChecked(true);
		}
		
		// Pressure radios
		rPa = (RadioButton)findViewById(R.id.radioPa);
		rPa.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pUnit = Constants.PASCAL;
				prefEditor.putInt(Constants.PRESSURE_UNIT, pUnit);
				prefEditor.commit();
			}
		});
		rhPa = (RadioButton)findViewById(R.id.radiohPa);
		rhPa.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pUnit = Constants.HECTOPASCAL;
				prefEditor.putInt(Constants.PRESSURE_UNIT, pUnit);
				prefEditor.commit();
			}
		});
		rkPa = (RadioButton)findViewById(R.id.radiokPa);
		rkPa.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pUnit = Constants.KILOPASCAL;
				prefEditor.putInt(Constants.PRESSURE_UNIT, pUnit);
				prefEditor.commit();
			}
		});
		rAtm = (RadioButton)findViewById(R.id.radioAtm);
		rAtm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pUnit = Constants.ATMOSPHERE;
				prefEditor.putInt(Constants.PRESSURE_UNIT, pUnit);
				prefEditor.commit();
			}
		});
		rmmHg = (RadioButton)findViewById(R.id.radiommHg);
		rmmHg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pUnit = Constants.MMHG;
				prefEditor.putInt(Constants.PRESSURE_UNIT, pUnit);
				prefEditor.commit();
			}
		});
		rinHg = (RadioButton)findViewById(R.id.radioinHg);
		rinHg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pUnit = Constants.INHG;
				prefEditor.putInt(Constants.PRESSURE_UNIT, pUnit);
				prefEditor.commit();
			}
		});
		
		if (pUnit == Constants.HECTOPASCAL) {
			rhPa.setChecked(true);
		} else if (pUnit == Constants.KILOPASCAL) {
			rkPa.setChecked(true);
		} else if (pUnit == Constants.ATMOSPHERE) {
			rAtm.setChecked(true);
		} else if (pUnit == Constants.MMHG) {
			rmmHg.setChecked(true);
		} else if (pUnit == Constants.INHG) {
			rinHg.setChecked(true);
		} else {
			rPa.setChecked(true);
		}
		
		// Time Radios
		rOff = (RadioButton)findViewById(R.id.radioIOff);
		rOff.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopService(aqmService);
				tvServiceStatus.setText(Constants.SERVICE_OFF);
				Toast.makeText(myContext, "Monitoring service has been stopped", Toast.LENGTH_LONG).show();
			}
		});
		r1 = (RadioButton)findViewById(R.id.radioI1);
		r1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tUnit = Constants.MINUTES_1;
				prefEditor.putInt(Constants.TIME_INTERVAL, tUnit);
				prefEditor.commit();
				stopService(aqmService);
				startService(aqmService);
				tvServiceStatus.setText(Constants.SERVICE_ON);
				Toast.makeText(myContext, "Monitoring service has been restarted with the selected interval", Toast.LENGTH_LONG).show();
			}
		});
		r5 = (RadioButton)findViewById(R.id.radioI5);
		r5.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tUnit = Constants.MINUTES_5;
				prefEditor.putInt(Constants.TIME_INTERVAL, tUnit);
				prefEditor.commit();
				stopService(aqmService);
				startService(aqmService);
				tvServiceStatus.setText(Constants.SERVICE_ON);
				Toast.makeText(myContext, "Monitoring service has been restarted with the selected interval", Toast.LENGTH_LONG).show();
			}
		});
		r15 = (RadioButton)findViewById(R.id.radioI15);
		r15.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tUnit = Constants.MINUTES_15;
				prefEditor.putInt(Constants.TIME_INTERVAL, tUnit);
				prefEditor.commit();
				stopService(aqmService);
				startService(aqmService);
				tvServiceStatus.setText(Constants.SERVICE_ON);
				Toast.makeText(myContext, "Monitoring service has been restarted with the selected interval", Toast.LENGTH_LONG).show();
			}
		});
		r30 = (RadioButton)findViewById(R.id.radioI30);
		r30.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tUnit = Constants.MINUTES_30;
				prefEditor.putInt(Constants.TIME_INTERVAL, tUnit);
				prefEditor.commit();
				stopService(aqmService);
				startService(aqmService);
				tvServiceStatus.setText(Constants.SERVICE_ON);
				Toast.makeText(myContext, "Monitoring service has been restarted with the selected interval", Toast.LENGTH_LONG).show();
			}
		});
		r60 = (RadioButton)findViewById(R.id.radioI60);
		r60.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tUnit = Constants.MINUTES_60;
				prefEditor.putInt(Constants.TIME_INTERVAL, tUnit);
				prefEditor.commit();
				stopService(aqmService);
				startService(aqmService);
				tvServiceStatus.setText(Constants.SERVICE_ON);
				Toast.makeText(myContext, "Monitoring service has been restarted with the selected interval", Toast.LENGTH_LONG).show();
			}
		});
		
		// Never pre-check any of them
		
//		if (tUnit == Constants.MINUTES_15) {
//			r15.setChecked(true);
//		} else if (tUnit == Constants.MINUTES_30) {
//			r30.setChecked(true);
//		} else {
//			r60.setChecked(true);
//		}
		
		// Is the service running?
		if (serviceStatus) {
			tvServiceStatus.setText(Constants.SERVICE_ON);
		} else {
			tvServiceStatus.setText(Constants.SERVICE_OFF);
		}
		
		// What Sensordrone is stored?
		tvStoredDrone.setText(storedDrone);
	}
	
	public void getPreferences() {
		tempUnit = myPreferences.getInt(Constants.TEMPERATURE_UNIT, 1); // Default C
		pUnit = myPreferences.getInt(Constants.PRESSURE_UNIT, 0); // Default Pa
		tUnit = myPreferences.getInt(Constants.TIME_INTERVAL, 60); // Default 60 minutes
		serviceStatus = myPreferences.getBoolean(Constants.SERVICE_STATUS, false); 
		storedDrone = myPreferences.getString(Constants.SD_MAC, "Not Paired");
	}
	
	public void setupDrone(final boolean fromIntent) {
		myDrone = new Drone();
		myDroneHelper = new DroneConnectionHelper();
		myHandler = new DroneEventHandler() {
			
			@Override
			public void parseEvent(DroneEventObject arg0) {
				if (arg0.matches(droneEventType.CONNECTED)) {
					prefEditor.putString(Constants.SD_MAC, myDrone.lastMAC);
					prefEditor.commit();
					myDrone.disconnect();
					Toast.makeText(getApplicationContext(), "Sensordrone Saved!", Toast.LENGTH_SHORT).show();
					tvStoredDrone.setText(myDrone.lastMAC);
					myDrone.unregisterDroneListener(myHandler);
					// If we were set up from an intent, trigger a measurement
					if (fromIntent) {
						DataSync getData = new DataSync(getApplicationContext(), null);
						getData.setSdMC(myDrone.lastMAC);
						getData.execute();
					}
				}
			}
		};
		myDrone.registerDroneListener(myHandler);
		myDroneHelper.scanToConnect(myDrone, myActivity, myContext, false);

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
		getMenuInflater().inflate(R.menu.pref_menu, menu);
		return true;
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
//		case R.id.srvStart:
//			startService(aqmService);
//			tvServiceStatus.setText(Constants.SERVICE_ON);
//			break;
//		case R.id.srvStop:
//			stopService(aqmService);
//			tvServiceStatus.setText(Constants.SERVICE_OFF);
//			break;
		case R.id.stupDrn:
			setupDrone(false);
			break;
		case R.id.prefHelp:
			TxtReader help = new TxtReader(myContext);
			help.displayTxtAlert("Settings", R.raw.settings_help);
			break;
		}
		return true;
	}
}
