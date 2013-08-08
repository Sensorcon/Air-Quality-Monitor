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

public class COInfo extends Activity{


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
		gasLabel.setText(makeFancyString("Carbon Monoxide", Typeface.BOLD));
		gasLabel.setTextSize(42);

		// Set up our table layout; we'll build everything into that
		table = (TableLayout)findViewById(R.id.tlInfo);


		String test = "At concentrations of <5ppm, you probably donÕt need to worry, just keep an eye on it.  At higher concentrations, you need to investigate further.";
		addTextRow(test, Typeface.ITALIC);

		// Good
		addImageRow(R.drawable.face_good);
		addTextRow("Air Quality Rating:", Typeface.BOLD_ITALIC);
		addTextRow("Good", Typeface.NORMAL);
		addTextRow("PPM Reading:", Typeface.BOLD_ITALIC);
		addTextRow("0 - 5", Typeface.NORMAL);
		addTextRow("¥ 0-4.4 ppm CO is the EPA standard for good air quality", Typeface.ITALIC);
		addTextRow("Possible Meaning:", Typeface.BOLD_ITALIC);
		addTextRow("¥ CO is normally present in ambient conditions (0-1ppm)", Typeface.NORMAL);
		addTextRow("¥ Electronics Noise (the sensor is accurate to 2ppm)", Typeface.NORMAL);
		addTextRow("What you should do:", Typeface.BOLD_ITALIC);
		addTextRow("¥ Keep an eye on it, itÕs normal to see small transients  of 1-2ppm. " +
				"If it stays in the 3-5ppm range, there may be a very small amount of CO present.",
				Typeface.NORMAL);
		addTextRow("¥ Concentrations moving between 0 & 2-3ppm are typically sensor noise; "  +
				"typically this happens when the instrument is adjusting to a new ambient temperature.", 
				Typeface.NORMAL);


		// Moderate
		addImageRow(R.drawable.face_moderate);
		addTextRow("Air Quality Rating:", Typeface.BOLD_ITALIC);
		addTextRow("Moderate", Typeface.NORMAL);
		addTextRow("PPM Reading:", Typeface.BOLD_ITALIC);
		addTextRow("5 - 10", Typeface.NORMAL);
		addTextRow("¥ 4.4-9.4 ppm CO is the EPA standard for moderate air quality; highly sensitive groups may start to have adverse health issues.", Typeface.ITALIC);
		addTextRow("Possible Meaning:", Typeface.BOLD_ITALIC);
		addTextRow("Indoors:", Typeface.ITALIC);
		addTextRow("There may be a small source of CO in the building. " + "" +
				"This is typically caused by a gas stove, cigarettes, an attached garage with an opening into the house, " +
				"or a furnace or hot water heater with improper venting.",
				Typeface.NORMAL);
		addTextRow("Outdoors:", Typeface.ITALIC);
		addTextRow("Most likely there is a local source of CO, such as a campfire, grill, or automobile exhaust. " +
				"In urban environments, such concentrations may be encountered, especially at busy intersections.",
				Typeface.NORMAL);
		addTextRow("What you should do:", Typeface.BOLD_ITALIC);
		addTextRow("¥ If indoors, see if you can find elevated levels of CO by walking through the building and taking instantaneous measurements.", Typeface.NORMAL);
		addTextRow("¥ If outdoors, pay attention to whatÕs going on around you to try to identify potential sources.  See if lower concentrations are observed in different locations.", Typeface.NORMAL);

		// Bad
		addImageRow(R.drawable.face_bad);
		addTextRow("Bad", Typeface.NORMAL);
		addTextRow("PPM Reading:", Typeface.BOLD_ITALIC);
		addTextRow("10 - 35", Typeface.NORMAL);
		addTextRow("¥ 9.5-30.4ppm is the EPA classification for unhealthy to very unhealthy CO air quality.", Typeface.ITALIC);
		addTextRow("Possible Meaning:", Typeface.BOLD_ITALIC);
		addTextRow("Indoors:", Typeface.ITALIC);
		addTextRow("¥ These concentrations indicate that there is a local source of CO in the building.", Typeface.NORMAL);
		addTextRow("Outdoors:", Typeface.ITALIC);
		addTextRow("¥ There may be a lot of traffic (cars, motorcycles, ATVs, boats, etc.) around you, or you may be close to a burning fire (camp fire, grill, etc.)", Typeface.NORMAL);
		addTextRow("What you should do:", Typeface.BOLD_ITALIC);
		addTextRow("¥ If indoors, it is again most likely to be coming from combustion processes in the house, like a stove, furnace, hot water heater, fireplace or cigarettes.", Typeface.NORMAL);
		addTextRow("¥ If outdoors, unless you are performing an exhaust test, " +
				"these concentrations are higher than you should be exposed to over extended periods of time. " + 
				"If you smell exhaust, you are not smelling CO (CO is odorless) you are smelling other gases such " + 
				"as NOx, but vehicle exhaust has many toxic gases, including CO, NOx, " + 
				"some particulate matter and unburned hydrocarbon fuel. 10s of ppm CO is " + 
				"typical in a garage where a car was recently operating.", Typeface.NORMAL);

		addTextRow("HAZARDOUS", Typeface.BOLD);
		addTextRow("PPM Reading:", Typeface.BOLD_ITALIC);
		addTextRow("Greater than 35", Typeface.NORMAL);
		addTextRow("Possible Meaning:", Typeface.BOLD_ITALIC);
		addTextRow("Indoors:", Typeface.ITALIC);
		addTextRow("These concentrations are considered hazardous, and are almost certainly coming from a large leak in a furnace or boiler/hot water heater", Typeface.NORMAL);
		addTextRow("Outdoors:", Typeface.ITALIC);
		addTextRow("You are most likely exposed to heavy traffic or close to the exhaust of 1 or more vehicles", Typeface.NORMAL);
		addTextRow("What you should do:", Typeface.BOLD_ITALIC);
		addTextRow("¥ If you are in an environment with such high levels, you should not stay in this environment very long until the CO source is found and corrected.", Typeface.NORMAL);
		addTextRow("¥ If you are indoors and the reading is >35ppm, you should immediately ventilate the building and turn off all combustion processes.", Typeface.NORMAL);
		addTextRow("¥ The National Institute for Occupational Safety and Health specifies 35ppm as an 8-hr time weighted average limit, meaning workers should not be exposed to this time weighted average concentration for more than 8 hours.", Typeface.NORMAL);

		// More information
		addImageRow(R.drawable.face_unknown);
		addTextRow("More Information:", Typeface.BOLD_ITALIC);
		addLinkRow("http://www.epa.gov/ttn/oarpg/t1/memoranda/rg701.pdf", Typeface.NORMAL);
		addLinkRow("https://en.wikipedia.org/wiki/Indoor_air_quality#Carbon_monoxide", Typeface.NORMAL);




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

