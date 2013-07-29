package com.sensorcon.airqualitymonitor.database;

public class DBDateTime {

	String year;
	String month;
	String day;
	String hour;
	String minute;
	String second;

	public DBDateTime() {

	}
	public DBDateTime(long id, String value) {
		super();
		this.id = id;
		this.value = value;
		year = value.substring(0, 4);
		month = value.substring(4, 6);
		day = value.substring(6, 8);
		hour = value.substring(9,11);
		minute = value.substring(11,13);
		second = value.substring(13,15);
	}


	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	private String value;
	public String getValue() {
		return value;
	}



	public void setValue(String value) {
		this.value = value;
	}

	public String getTimeStamp() {
		return hour + ":" + minute + ":" + second;
	}
	
	public String getMMDDYYYY() {
		return month + "/" + day + "/" + year;
	}

	public String getSecond() {
		return value.substring(13,15);
	}
	public String getMinute() {
		return value.substring(11,13);
	}
	public String getHour() {
		return value.substring(9,11);
	}
	public String getYear() {
		return value.substring(0, 4);
	}
	public String getMonth() {
		return value.substring(4, 6);
	}
	public String getDay() {
		return value.substring(6, 8);
	}

	@Override
	public String toString() {
		return value;
	}
}
