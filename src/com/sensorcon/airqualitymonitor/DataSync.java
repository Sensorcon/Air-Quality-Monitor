package com.sensorcon.airqualitymonitor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.sensorcon.airqualitymonitor.database.DBCO;
import com.sensorcon.airqualitymonitor.database.DBCO2;
import com.sensorcon.airqualitymonitor.database.DBDataBlob;
import com.sensorcon.airqualitymonitor.database.DBDataHandler;
import com.sensorcon.airqualitymonitor.database.DBDateTime;
import com.sensorcon.airqualitymonitor.database.DBHumidity;
import com.sensorcon.airqualitymonitor.database.DBPressure;
import com.sensorcon.airqualitymonitor.database.DBTemperature;
import com.sensorcon.sensordrone.DroneEventHandler;
import com.sensorcon.sensordrone.DroneEventObject;
import com.sensorcon.sensordrone.DroneEventObject.droneEventType;
import com.sensorcon.sensordrone.android.Drone;

public class DataSync extends AsyncTask<Void, Void, Void> {

	Drone myDrone;
	String sdMC = "";
	AirQualityMonitor runningApp = null;

	Context context;

	NotificationManager notifier;
	NotificationCompat.Builder notifyLowBattery;
	NotificationCompat.Builder notiftyAirQualityModerate;
	NotificationCompat.Builder notiftyAirQualityBad;



	public void setContext(Context context) {
		this.context = context;
	}


	public void setSdMC(String sdMC) {
		this.sdMC = sdMC;
	}

	public DataSync(Context context) {
		this.context = context;
	}

	public DataSync(Context context, AirQualityMonitor currentApp) {
		this.context = context;
		this.runningApp = currentApp;
	}

	DBDateTime dateTime;
	DBCO coData;
	DBCO2 co2Data;
	DBTemperature tempData;
	DBHumidity humidityData;
	DBPressure presureData;

	DBDataHandler dbHandler;

	SharedPreferences myPreferences;
	Editor prefEditor;

	private Object lock = new Object();

	@Override
	protected Void doInBackground(Void... params) {

		if (sdMC == "") {
			return null;
		}

		myDrone = new Drone();

		notifier = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

		DroneEventHandler myHandler = new DroneEventHandler() {

			@Override
			public void parseEvent(DroneEventObject arg0) {

				// Program it in order...
				if (arg0.matches(droneEventType.CONNECTED)) {

					publishProgress(null);

					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
					String currentDateandTime = sdf.format(new Date());
					dateTime = new DBDateTime();
					dateTime.setValue(currentDateandTime);

					Log.d("AQIA_SERVICE", dateTime.toString());

					myDrone.uartWrite("K 1\r\n".getBytes());
					myDrone.setLEDs(255, 0, 0);
					myDrone.measureBatteryVoltage();
				}

				if (arg0.matches(droneEventType.BATTERY_VOLTAGE_MEASURED)) {
					// Make a notification if the Drone's battery is low
					if (myDrone.batteryVoltage_Volts < Constants.LOW_BATTERY_NOTIFY) {
						notifyLowBattery = new Builder(context);
						notifyLowBattery.setContentTitle("Low Battery!");
						notifyLowBattery.setContentText("Your Sensordrones battery is getting low! Please charge it up.");
						notifyLowBattery.setSmallIcon(R.drawable.ic_launcher);
						notifier.notify(Constants.NOTIFY_LOW_BATTERY, notifyLowBattery.build());
					}
					myDrone.enableTemperature();
				}

				if (arg0.matches(droneEventType.TEMPERATURE_ENABLED)) {

					myDrone.measureTemperature();
				}

				if (arg0.matches(droneEventType.TEMPERATURE_MEASURED)) {
					publishProgress(null);

					tempData = new DBTemperature();
					tempData.setValue((long) myDrone.temperature_Celcius);

					Log.d("AQIA_SERVICE", tempData.toString());

					myDrone.enableHumidity();
				}

				if (arg0.matches(droneEventType.HUMIDITY_ENABLED)) {

					myDrone.measureHumidity();
				}

				if (arg0.matches(droneEventType.HUMIDITY_MEASURED)) {
					publishProgress(null);

					humidityData = new DBHumidity();
					humidityData.setValue((long) myDrone.humidity_Percent);

					Log.d("AQIA_SERVICE", humidityData.toString());

					myDrone.enablePressure();
				}

				if (arg0.matches(droneEventType.PRESSURE_ENABLED)) {

					// Give the Sensor a second to start up
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					myDrone.measurePressure();
				}

				if (arg0.matches(droneEventType.PRESSURE_MEASURED)) {
					publishProgress(null);

					presureData = new DBPressure();
					presureData.setValue((long) myDrone.pressure_Pascals);

					Log.d("AQIA_SERVICE", presureData.toString());

					myDrone.disablePressure();

				}

				if (arg0.matches(droneEventType.PRESSURE_DISABLED)) {

					myDrone.enablePrecisionGas();
				}

				if (arg0.matches(droneEventType.PRECISION_GAS_ENABLED)) {

					// Give the Sensor a second to start up
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					myDrone.measurePrecisionGas();
				}

				if (arg0.matches(droneEventType.PRECISION_GAS_MEASURED)) {
					publishProgress(null);

					coData = new DBCO();
					coData.setValue((long) myDrone.precisionGas_ppmCarbonMonoxide);

					Log.d("AQIA_SERVICE", coData.toString());

					myDrone.uartWrite("Z\r\n".getBytes());
					myDrone.uartRead();

				}

				if (arg0.matches(droneEventType.UART_READ)) {
					publishProgress(null);


					// We store -1, unless something is found
					String result = "-1";


					//					byte[] data = myDrone.uartReadBuffer.array();
					//					printPacket(data);
					//					// Clear our array in case we got multiple reads
					//					byte[] empty = {0x00};
					//					myDrone.uartReadBuffer = ByteBuffer.wrap(empty);
					//					printPacket(myDrone.uartReadBuffer.array());
					//					for (int i=0; i < data.length; i++) {
					//						if (data[i] == 0x5a && i < data.length-7) {
					//							byte[] value = {data[i+2], data[i+3], data[i+4], data[i+5], data [i+6]};
					//							result = new String(value);
					//							continue;
					//						}
					//					}

					try {
						int avail = myDrone.uartInputStream.available();
						boolean needData = true;
						for (int i=0; i < avail; i++) {

							if ((byte)myDrone.uartInputStream.read() == 0x5a && i < avail-7 && needData) {
								Log.d("AQM", "Got it!");
								myDrone.uartInputStream.read();
								byte[] value = new byte[5];
								myDrone.uartInputStream.read(value);
								result = new String(value);
								i +=6;
								needData = false;
							}
						}
					} catch (IOException e1) {

					}




					Log.d("AQIA_SERVICE", result);
					myDrone.uartWrite("K 0\r\n".getBytes());
					myDrone.setLEDs(0, 0, 0);
					myDrone.disconnect();

					Log.d("AQI_SERVICE", "Logging to database");
					co2Data = new DBCO2();
					long value = Long.parseLong(result);
					co2Data.setValue(value);

					DBDataHandler myDBHandler = new DBDataHandler(context);
					myDBHandler.open();
					long id = myDBHandler.addData(dateTime, coData, co2Data, tempData, humidityData, presureData);
					myDBHandler.close();
					Log.d("AQIA_SERVICE", String.valueOf(id));

					// Make a blob to get a quality assessment
					DBDataBlob statusBlob = new DBDataBlob(dateTime, coData, co2Data, humidityData, tempData, presureData);

					// Don't throw a notification if the user is measuring from main UI
					if (runningApp == null) {
						if (statusBlob.getStatus() == Constants.STATUS_GOOD) {
							// Clear notification if the reading has returned to normal
							notifier.cancel(Constants.NOTIFY_AQ_STATUS);
						} else if (statusBlob.getStatus() == Constants.STATUS_MODERATE) {
							notiftyAirQualityModerate = new Builder(context);
							notiftyAirQualityModerate.setContentTitle("Air Quality Alert!");
							notiftyAirQualityModerate.setContentText("Your air quality is Moderate!");
							notiftyAirQualityModerate.setSmallIcon(R.drawable.ic_launcher);
							notifier.notify(Constants.NOTIFY_AQ_STATUS, notiftyAirQualityModerate.build());

						} else if (statusBlob.getStatus() == Constants.STATUS_BAD) {
							notiftyAirQualityBad = new Builder(context);
							notiftyAirQualityBad.setContentTitle("Air Quality Alert!");
							notiftyAirQualityBad.setContentText("Your air quality is Bad!");
							notiftyAirQualityBad.setSmallIcon(R.drawable.ic_launcher);
							notifier.notify(Constants.NOTIFY_AQ_STATUS, notiftyAirQualityBad.build());
						}
					}


					synchronized (lock) {
						lock.notify();
					}
				}

			}
		};

		myDrone.registerDroneListener(myHandler);

		if (myDrone.btConnect(sdMC)) {
			try {
				synchronized (lock) {
					// We'll give it 10 seconds
					lock.wait(10000);
				}
			} catch (InterruptedException e) {
				return null;
			}
		} 


		return null;
	}

	public void printPacket(byte[] packet) {
		String data = "";
		for (int i=0; i < packet.length; i++) {
			data += Integer.toHexString(packet[i] &0xff) + ":";
		}
		Log.d("AQM", data);
	}
	@Override
	protected void onPostExecute(Void result) {
		if (runningApp != null) {
			runningApp.updateDisplay();
		}
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		if (runningApp != null) {
			runningApp.animateFace();
		}
	}
}