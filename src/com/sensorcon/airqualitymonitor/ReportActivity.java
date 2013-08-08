package com.sensorcon.airqualitymonitor;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;

public class ReportActivity extends Activity {
	
		
	private TextView dateTV;
	private TextView timeTV;
	private TextView tempTV;
	private TextView humidityTV;
	private TextView pressureTV;
	private TextView coTV;
	private TextView co2TV;

	private ImageView face;
	private TextView tvStatus;

	SharedPreferences myPreferences;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_activity);
		
		myPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		dateTV = (TextView)findViewById(R.id.tvDate);
		timeTV = (TextView)findViewById(R.id.tvTime);
		tempTV = (TextView)findViewById(R.id.tvTemp);
		humidityTV = (TextView)findViewById(R.id.tvHumidity);
		pressureTV = (TextView)findViewById(R.id.tvPressure);
		coTV = (TextView)findViewById(R.id.tvCO);
		co2TV = (TextView)findViewById(R.id.tvCO2);


		
		Intent myIntent = getIntent();
		
		int coStatus = myIntent.getIntExtra("co_status", 3);
		int co2Status = myIntent.getIntExtra("co2_status", 3); 
		
		if (coStatus == Constants.STATUS_GOOD) {
			coTV.setBackgroundResource(R.drawable.rounded_good_small);
		} else if (coStatus == Constants.STATUS_MODERATE) {
			coTV.setBackgroundResource(R.drawable.rounded_meh_small);

		} else if (coStatus == Constants.STATUS_BAD) {
			coTV.setBackgroundResource(R.drawable.rounded_bad_small);

		} else{
			coTV.setBackgroundResource(R.drawable.rounded_unknown_small);

		}
		
		if (co2Status == 0) {
			co2TV.setBackgroundResource(R.drawable.rounded_good_small);
		} else if (co2Status == 1) {
			co2TV.setBackgroundResource(R.drawable.rounded_meh_small);

		} else if (co2Status == 2) {
			co2TV.setBackgroundResource(R.drawable.rounded_bad_small);

		} else{
			co2TV.setBackgroundResource(R.drawable.rounded_unknown_small);
		}
		
		face = (ImageView)findViewById(R.id.reportFace);
		tvStatus = (TextView)findViewById(R.id.tvReportStatus);
		int status = Math.max(coStatus, co2Status);
		
		if (status == 0) {
			face.setImageResource(R.drawable.face_good);
			tvStatus.setText("Good");
		} else if (status == 1) {
			face.setImageResource(R.drawable.face_moderate);
			tvStatus.setText("Moderate");
		} else if (status == 2) {
			face.setImageResource(R.drawable.face_bad);
			tvStatus.setText("Bad");
		} else {
			face.setImageResource(R.drawable.face_unknown);
			tvStatus.setText("N/A");
		}
		
		
		dateTV.setText(myIntent.getStringExtra("date"));
		timeTV.setText(myIntent.getStringExtra("time"));
		humidityTV.setText(myIntent.getStringExtra("humidity") + " %");
		coTV.setText(myIntent.getStringExtra("co") + " ppm");
		if (myIntent.getStringExtra("co2").equals("-1")) {
			co2TV.setText("N/A");
		} else {
			co2TV.setText(myIntent.getStringExtra("co2") + " ppm");
		}
		

		float tValue = myIntent.getLongExtra("temp", 0);
		int tempPref = myPreferences.getInt(Constants.TEMPERATURE_UNIT, Constants.CELCIUS);
		if (tempPref != Constants.CELCIUS) {
			double fahrenheit = tValue * (9.0 / 5.0) + 32;
			tempTV.setText(String.format("%.0f", fahrenheit) + " F");
		} else {
			tempTV.setText(String.format("%.0f", tValue) + " C");
		}

		
		float pValue = myIntent.getLongExtra("pressure", 0);
		int pPref = myPreferences.getInt(Constants.PRESSURE_UNIT, Constants.PASCAL);
		if (pPref == Constants.HECTOPASCAL) {
			pressureTV.setText(String.format("%.2f", pValue/100) + " hPa");
		} else if (pPref == Constants.KILOPASCAL) {
			pressureTV.setText(String.format("%.3f", pValue/1000) + " kPa");
		} else if (pPref == Constants.ATMOSPHERE) {
			pressureTV.setText(String.format("%.3f", pValue * 0.00000986923267) + " atm");
		}  else if (pPref == Constants.MMHG) {
			pressureTV.setText(String.format("%.0f", pValue * 0.00750061683) + " mmHg");
		} else if (pPref == Constants.INHG) {
			pressureTV.setText(String.format("%.2f", pValue * 0.000295299830714) + " inHg");
		} else {
			pressureTV.setText(String.format("%.0f", pValue) + " Pa");
		}


	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
