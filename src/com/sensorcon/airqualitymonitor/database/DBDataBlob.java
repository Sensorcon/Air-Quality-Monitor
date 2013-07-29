package com.sensorcon.airqualitymonitor.database;

import com.sensorcon.airqualitymonitor.Constants;

public class DBDataBlob {
	
	private DBDateTime dbDateTime;
	private DBCO dbCO;
	private DBCO2 dbCO2;
	private DBHumidity dbHumidity;
	private DBTemperature dbTemperature;
	private DBPressure dbPressure;
	
	private int coStatus;
	private int co2Status;

	public int getCoStatus() {
		return coStatus;
	}

	public void setCoStatus(int coStatus) {
		this.coStatus = coStatus;
	}

	public int getCo2Status() {
		return co2Status;
	}

	public void setCo2Status(int co2Status) {
		this.co2Status = co2Status;
	}


	private int status;
	
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public DBDataBlob() {
		this.dbDateTime = null;
		this.dbCO = null;
		this.dbCO2 = null;
		this.dbHumidity = null;
		this.dbTemperature = null;
		this.dbPressure = null;
	}
	
	public DBDataBlob(int columnIndex, String dateTime, long coValue, long co2value, long tempValue, long humidityValue, long pressureValue) {
		dbDateTime = new DBDateTime(columnIndex, dateTime);
		dbCO = new DBCO(columnIndex, coValue);
		dbCO2 = new DBCO2(columnIndex, co2value);
		dbTemperature = new DBTemperature(columnIndex, tempValue);
		dbHumidity = new DBHumidity(columnIndex, humidityValue);
		dbPressure = new DBPressure(columnIndex, pressureValue);
		assignQualityLevel();
	}
	
	public DBDataBlob(DBDateTime dbDateTime, DBCO dbCO, DBCO2 dbCO2,
			DBHumidity dbHumidity, DBTemperature dbTemperature,
			DBPressure dbPressure) {
		super();
		this.dbDateTime = dbDateTime;
		this.dbCO = dbCO;
		this.dbCO2 = dbCO2;
		this.dbHumidity = dbHumidity;
		this.dbTemperature = dbTemperature;
		this.dbPressure = dbPressure;
		assignQualityLevel();
	}


	public DBDateTime getDbDateTime() {
		return dbDateTime;
	}


	public void setDbDateTime(DBDateTime dbDateTime) {
		this.dbDateTime = dbDateTime;
	}


	public DBCO getDbCO() {
		return dbCO;
	}


	public void setDbCO(DBCO dbCO) {
		this.dbCO = dbCO;
	}


	public DBCO2 getDbCO2() {
		return dbCO2;
	}


	public void setDbCO2(DBCO2 dbCO2) {
		this.dbCO2 = dbCO2;
	}


	public DBHumidity getDbHumidity() {
		return dbHumidity;
	}


	public void setDbHumidity(DBHumidity dbHumidity) {
		this.dbHumidity = dbHumidity;
	}


	public DBTemperature getDbTemperature() {
		return dbTemperature;
	}


	public void setDbTemperature(DBTemperature dbTemperature) {
		this.dbTemperature = dbTemperature;
	}


	public DBPressure getDbPressure() {
		return dbPressure;
	}


	public void setDbPressure(DBPressure dbPressure) {
		this.dbPressure = dbPressure;
	}
	
	public void assignQualityLevel() {
		long coValue = dbCO.getValue();
		if (coValue < Constants.CO_GOOD) {
			coStatus = 0;
		} else if (coValue >= Constants.CO_GOOD && coValue < Constants.CO_MODERATE) {
			coStatus = 1;
		} else {
			coStatus = 2;
		}
		
		long co2value = dbCO2.getValue();
		if (co2value == -1) {
			co2Status = -1;
		} else if (co2value != -1 && co2value < Constants.CO2_GOOD) {
			co2Status = 0;
		} else if (co2value >= Constants.CO2_GOOD && co2value < Constants.CO2_MODERATE) {
			co2Status = 1;
		} else {
			co2Status = 2;
		}
		
		status = Math.max(coStatus, co2Status);
	}
	
	public String csvString() {
		String csv = "";
		// These are ordered based on DBCReator.ALL_COLUMNS !
//		COLUNM_ID,
//		COLUMN_DATETIME,
//		COLUMN_CO,
//		COLUMN_CO2,
//		COLUMN_HUMIDITY,
//		COLUMN_TEMPERATURE,
//		COLUMN_PRESSURE
		csv += String.valueOf(dbDateTime.getId()) + ",";
		csv += dbDateTime.toString() + ",";
		csv += dbCO.toString() +",";
		csv += dbCO2.toString() + ",";
		csv += dbHumidity.toString() + ",";
		csv += dbTemperature.toString() + ",";
		csv += dbPressure.toString() + "\n";
		return csv;
	}

}
