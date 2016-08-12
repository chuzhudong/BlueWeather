package com.example.blueweather.model;

public class County {

	private int countyId;
	
	private String countyName;
	
	private int cityId;
	
	private int countySort;
	
	public County() {
		return;
	}
	
	public County(int countyId, String countyName, int cityId, int countySort) {
		this.countyId = countyId;
		this.countyName = countyName;
		this.cityId = cityId;
		this.countySort = countySort;
	}
	
	public void setCountyId(int countyId) {
		this.countyId = countyId;
	}
	
	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}
	
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	
	public void setCountySort(int countySort) {
		this.countySort = countySort;
	}

	public int getCountyId() {
		return countyId;
	}
	
	public int getCityId() {
		return cityId;
	}
	
	public String getCountyName() {
		return countyName;
	}
	
	public int getCountySort() {
		return countySort;
	}
}
