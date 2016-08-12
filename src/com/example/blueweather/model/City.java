package com.example.blueweather.model;

public class City {

	private int cityId;

	private String cityName;

	private int proId;

	private int citySort;

	public City() {
		return;
	}
	
	public City(int cityId, String cityName, int proId, int citySort) {
		this.cityId = cityId;
		this.cityName = cityName;
		this.proId = proId;
		this.citySort = citySort;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public void setProId(int proId) {
		this.proId = proId;
	}

	public void setCitySort(int citySort) {
		this.citySort = citySort;
	}

	public int getCityId() {
		return cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public int getProId() {
		return proId;
	}

	public int getCitySort() {
		return citySort;
	}

}
