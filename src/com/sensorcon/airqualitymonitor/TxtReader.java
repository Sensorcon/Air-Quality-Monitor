package com.sensorcon.airqualitymonitor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;


public class TxtReader {
	
	Context myContext;
		
	public TxtReader(Context context) {
		this.myContext = context;
	}
	
	// Used for easily getting the contents of a txt file into a String
	public String readRawTXT(int rawResourceId) {
		
		// The ins and outs
		InputStream in = myContext.getResources().openRawResource(rawResourceId);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		// Loop through
		try {
			int aChar = in.read();
			while (aChar != -1) {
				out.write(aChar);
				aChar = in.read();
			}
		} catch (IOException e) {
			// Whoops
			return "Well this is embarassing...\nThere was an error parsing the bundled file.";
		}
		
		return out.toString();
	}
	
	// Display the contents of a txt file in an AlertDialog
	public void displayTxtAlert(String title, int rawResourceId) {
		AlertDialog myAlert;
		AlertDialog.Builder myBuilder = new Builder(myContext);
		myBuilder.setTitle(title);
		myBuilder.setMessage(readRawTXT(rawResourceId));
		myAlert = myBuilder.create();
		myAlert.show();
	}

}
