package com.sensorcon.airqualitymonitor;

import com.sensorcon.airqualitymonitor.database.DBCO;
import com.sensorcon.airqualitymonitor.database.DBCO2;
import com.sensorcon.airqualitymonitor.database.DBDataHandler;
import com.sensorcon.airqualitymonitor.database.DBDateTime;
import com.sensorcon.airqualitymonitor.database.DBHumidity;
import com.sensorcon.airqualitymonitor.database.DBPressure;
import com.sensorcon.airqualitymonitor.database.DBTemperature;
import com.sensorcon.sensordrone.android.Drone;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

public class DroneAQMService extends Service {

	SharedPreferences myPreferences;
	Editor prefEditor;

	final static String SERVICE_STATUS = "SERVICE_STATUS";

	NotificationManager notifier;
	android.support.v4.app.NotificationCompat.Builder notifyConnect;
	android.support.v4.app.NotificationCompat.Builder notifyDisconnect;
	android.support.v4.app.NotificationCompat.Builder notifyNotSetUp;


	DroneAlarm droneTask;

	Drone myDrone;
	String sdMC;


	public void setSdMC(String sdMC) {
		this.sdMC = sdMC;
	}

	DBDateTime dateTime;
	DBCO coData;
	DBCO2 co2Data;
	DBTemperature tempData;
	DBHumidity humidityData;
	DBPressure presureData;

	DBDataHandler dbHandler;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();


		myPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		prefEditor = myPreferences.edit();

		myDrone = new Drone();

		// Set up our notifications
		notifier = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		// If we don't want to do anything, then we need an empty Intent for some versions of Android
		 PendingIntent emptyIntent = PendingIntent.getActivity(
			      this, 
			      0, 
			      new Intent(),  //Dummy Intent do nothing 
			      Intent.FLAG_ACTIVITY_NEW_TASK);
		 
		// If the MAC isn't there, abort the service
		this.setSdMC(myPreferences.getString(Constants.SD_MAC, ""));
		if (sdMC == "") {
			notifyNotSetUp = new Builder(this);
			notifyNotSetUp.setContentTitle("AQM Service Not Started");
			notifyNotSetUp.setContentInfo("Sensordrone not set up");
			notifyNotSetUp.setSmallIcon(R.drawable.ic_launcher);
			notifyNotSetUp.setContentIntent(emptyIntent);
			notifier.notify(Constants.NOTIFY_SERVICE_STATUS, notifyNotSetUp.build());
			this.stopSelf();
		}



		// Service Start
		notifyConnect = new android.support.v4.app.NotificationCompat.Builder(this);
		notifyConnect.setContentTitle("AQM Service Started");
		notifyConnect.setContentIntent(emptyIntent);
		notifyConnect.setSmallIcon(R.drawable.ic_launcher);

		// Service Stop
		notifyDisconnect = new android.support.v4.app.NotificationCompat.Builder(this);
		notifyDisconnect.setContentTitle("AQM Service Stopped");
		notifyDisconnect.setContentIntent(emptyIntent);
		notifyDisconnect.setSmallIcon(R.drawable.ic_launcher);


		// Store that the Service in Enabled
		prefEditor.putBoolean(Constants.SERVICE_STATUS, true);
		prefEditor.commit();

		notifier.notify(Constants.NOTIFY_SERVICE_STATUS, notifyConnect.build());

		droneTask = new DroneAlarm();

		droneTask.setAlarm(getApplicationContext());

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		prefEditor.putBoolean(Constants.SERVICE_STATUS, false);
		prefEditor.commit();
		// Don't notify if SD isn't set up (different notification fired in onCreate)
		if (sdMC != "") {
			notifier.notify(Constants.NOTIFY_SERVICE_STATUS, notifyDisconnect.build());
		}
		//		myTimer.cancel();
		droneTask.CancelAlarm(getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}


}
