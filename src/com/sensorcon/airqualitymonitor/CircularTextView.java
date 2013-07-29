package com.sensorcon.airqualitymonitor;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class CircularTextView extends TextView {

	public CircularTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CircularTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CircularTextView(Context context) {
		super(context);

	}
	
	public void setStatusBad() {
		this.setGravity(Gravity.CENTER);
		this.setBackgroundResource(R.drawable.gas_view_bad);
	}
	public void setStatusGood() {
		this.setGravity(Gravity.CENTER);
		this.setBackgroundResource(R.drawable.gas_view_good);
	}
	public void setStatusModerate() {
		this.setGravity(Gravity.CENTER);
		this.setBackgroundResource(R.drawable.gas_view_moderate);
	}
	public void setStatusNull() {
		this.setGravity(Gravity.CENTER);
		this.setBackgroundResource(0);
	}
	

}
