package com.sensorcon.airqualitymonitor;

import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class CO2Info extends Activity{

	
	TextView gasLabel;
	TableLayout table;
	TxtReader infoGrabber;
	
	Context myContext;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aq_guide);
		
		infoGrabber = new TxtReader(this);
		myContext = this;
		
		// Set out main label
		gasLabel = (TextView)findViewById(R.id.tvGasInfo);
		gasLabel.setText(makeFancyString("Carbon Dioxide", Typeface.BOLD));
		gasLabel.setTextSize(42);
		
		// Set up our table layout; we'll build everything into that
		table = (TableLayout)findViewById(R.id.tlInfo);
		

		String test = "Typical outdoor ambient (fresh air) CO2 levels are around 350-450 ppm, depending on location";
		addTextRow(test, Typeface.ITALIC);
		
		// Good
		addImageRow(R.drawable.face_good);
		addTextRow("Air Quality Rating:", Typeface.BOLD_ITALIC);
		addTextRow("Good", Typeface.NORMAL);
		addTextRow("PPM Reading:", Typeface.BOLD_ITALIC);
		addTextRow("0 - 1000", Typeface.NORMAL);
		addTextRow("Possible Meaning:", Typeface.BOLD_ITALIC);
		addTextRow("¥ In properly ventilated buildings, CO2 readings are likely to be between 600 and 1000,", Typeface.NORMAL);
		addTextRow("What you should do:", Typeface.BOLD_ITALIC);
		addTextRow("¥ Keep an eye on it if it is closer to 1000; see if the value goes up or down over time or in different locations.",
					Typeface.NORMAL);
		
		
		// Moderate
		addImageRow(R.drawable.face_moderate);
		addTextRow("Air Quality Rating:", Typeface.BOLD_ITALIC);
		addTextRow("Moderate", Typeface.NORMAL);
		addTextRow("PPM Reading:", Typeface.BOLD_ITALIC);
		addTextRow("1000 - 1500", Typeface.NORMAL);
		addTextRow("Possible Meaning:", Typeface.BOLD_ITALIC);
		addTextRow("Values over 1000 could be a sign of poor building ventiliation. Sensitive people may notice headaches and/or general fatigue.",
					Typeface.NORMAL);
		addTextRow("What you should do:", Typeface.BOLD_ITALIC);
		addTextRow("¥ Check the outdoor CO2 level, as this will affect the indoor level as well. Typcially, the indoor level should not more than 600 ppm above the outdoor level",
				Typeface.NORMAL);

		// Bad
		addImageRow(R.drawable.face_bad);
		addTextRow("Air Quality Rating:", Typeface.BOLD_ITALIC);
		addTextRow("Bad", Typeface.NORMAL);
		addTextRow("PPM Reading:", Typeface.BOLD_ITALIC);
		addTextRow("Greater than 1500", Typeface.NORMAL);
		addTextRow("Possible Meaning:", Typeface.BOLD_ITALIC);
		addTextRow("You are in a location with poor ventilation.", Typeface.NORMAL);
		addTextRow("What you should do:", Typeface.BOLD_ITALIC);
		addTextRow("¥ Try to improve the ventilation if possible, such as opening a window, or manually turning on any air exchanges.", Typeface.NORMAL);

		// More information
		addImageRow(R.drawable.face_unknown);
		addTextRow("More Information:", Typeface.BOLD_ITALIC);
		addLinkRow("https://en.wikipedia.org/wiki/Carbon_dioxide_in_Earth's_atmosphere", Typeface.NORMAL);
		addLinkRow("https://en.wikipedia.org/wiki/Indoor_air_quality#Carbon_dioxide", Typeface.NORMAL);
		addTextRow("The CO2 Module for the Sensordrone can be purchased at our site:", Typeface.NORMAL);
		addLinkRow("http://www.sensorcon.com/ambient-co2-sensor-carbon-dioxide-sensor-module-for-sensordrone/", Typeface.NORMAL);
		




	}
	
	public void addTextRow(String msg, int typefaceStyle) {
		// Hard coded to add to our TableLayout!
		TableRow generalRow = new TableRow(myContext);
		generalRow.setPadding(0, 5, 0, 0);
		TextView generalText = new TextView(myContext);
		generalText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		SpannableString fancyString = new SpannableString(msg);
		fancyString.setSpan(new StyleSpan(typefaceStyle), 0, fancyString.length(), 0);
		generalText.setText(fancyString);
		generalRow.addView(generalText);
		table.addView(generalRow);
	}
	
	public void addLinkRow(String link, int typefaceStyle) {
		TableRow generalRow = new TableRow(myContext);
		generalRow.setPadding(0, 5, 0, 0);
		TextView generalText = new TextView(myContext);
		generalText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		SpannableString fancyString = new SpannableString(link);
		fancyString.setSpan(new StyleSpan(typefaceStyle), 0, fancyString.length(), 0);
		generalText.setText(fancyString);
		
		Pattern pattern = Pattern.compile(link);
		Linkify.addLinks(generalText, pattern, "");
		
		
		generalRow.addView(generalText);
		table.addView(generalRow);
	}
	
	public void addImageRow(int drawabale) {
		TableRow generalRow = new TableRow(myContext);
		generalRow.setPadding(0, 20, 0, 0);
		generalRow.setGravity(Gravity.LEFT);
		ImageView generalImage = new ImageView(myContext);
		generalImage.setImageResource(drawabale);
		generalRow.addView(generalImage);
		table.addView(generalRow);
	}
	
	public SpannableString makeFancyString(String msg, int typefaceStyle) {
		SpannableString fancy = new SpannableString(msg);
		fancy.setSpan(new StyleSpan(typefaceStyle), 0, fancy.length(), 0);
		return fancy;
	}
	
	
}

