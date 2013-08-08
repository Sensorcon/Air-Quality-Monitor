package com.sensorcon.airqualitymonitor;

import com.sensorcon.airqualitymonitor.database.DBCO;
import com.sensorcon.airqualitymonitor.database.DBCO2;
import com.sensorcon.airqualitymonitor.database.DBDataBlob;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HistoryAdapter extends ArrayAdapter<DBDataBlob> {
	
	private final DBDataBlob[] myBlobs;
	private final Context myContext;


	public HistoryAdapter(Context context, DBDataBlob[] blobArray) {		
		super(context, R.layout.history_row, blobArray);
		this.myBlobs = blobArray;
		this.myContext = context;
		
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.history_row, parent, false);
		TextView rowTV = (TextView)rowView.findViewById(R.id.tvHistory);
		rowView.setBackgroundColor(Color.BLACK);
		rowTV.setTextColor(Color.BLACK);
		


		String timeStamp = myBlobs[position].getDbDateTime().toString();
		String year = timeStamp.substring(0, 4);
		String month = timeStamp.substring(4, 6);
		String day = timeStamp.substring(6, 8);
		String hour = timeStamp.substring(9,11);
		String minute = timeStamp.substring(11,13);
		String second = timeStamp.substring(13,15);


//		int[] status = statusIndicator(myBlobs[position].getDbCO(), myBlobs[position].getDbCO2());
		int result = Math.max(myBlobs[position].getCoStatus(), myBlobs[position].getCo2Status());
		
//		myBlobs[position].setCoStatus(status[0]);
//		myBlobs[position].setCo2Status(status[1]);
//		myBlobs[position].setStatus(result);
		
		if (result == 0) {
			rowView.setBackgroundResource(R.drawable.rounded_good);
		} else if (result == 1) {
			rowView.setBackgroundResource(R.drawable.rounded_meh);
		} else if (result == 2) {
			rowView.setBackgroundResource(R.drawable.rounded_bad);
		} else {
			rowView.setBackgroundResource(R.drawable.rounded_unknown);
		}

		rowTV.setText(
				month +"/" + day + "/" + year + " (" + hour + ":" + minute + ")"
				);
		
		return rowView;
		
	}
	

	private int[] statusIndicator(DBCO co, DBCO2 co2) {
		long coValue = co.getValue();
		long co2value = co2.getValue();
		
		int coStatus;
		int co2Status;
		int quality[];
		
		if (coValue < Constants.CO_GOOD) {
			coStatus = 0;
		} else if (coValue >= Constants.CO_GOOD && coValue < 200) {
			coStatus = 1;
		} else {
			coStatus = 2;
		}
		
		if (co2value < 1000) {
			co2Status = 0;
		} else if (co2value >= 1000 && co2value < 1500) {
			co2Status = 1;
		} else {
			co2Status = 2;
		}
		
		quality = new int[2];
		quality[0] = coStatus;
		quality[1] = co2Status;
		
		return quality;
	}


}
