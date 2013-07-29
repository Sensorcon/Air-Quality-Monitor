package com.sensorcon.airqualitymonitor.database;

public class DBCO {

	public DBCO() {
		
	}
	
	public DBCO(long id, long value) {
		super();
		this.id = id;
		this.value = value;
	}


	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	private long value;
	public long getValue() {
		return value;
	}



	public void setValue(long value) {
		this.value = value;
	}


	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
