package com.example.blueweather.model;

public class Weather {
	
	public int imageId;
	
	public int temp1;
	
	public int temp2;
	
	public String weather;
	
	public int current_tem;
	
	public String updateTime;
	
	public Weather () {
		
	}
	
	public Weather (int imageId, int temp1, int temp2, String weather, int current_tem) {
		this.imageId = imageId;
		this.temp1 = temp1;
		this.temp2 = temp2;
		this.current_tem = current_tem;
		this.weather = weather;
	}
	
	public void setImageId (int imageId) {
		this.imageId = imageId;
	}
	
	public void setTemp1 (int temp1) {
		this.temp1 = temp1;
	}
	
	public void setTemp2 (int temp2) {
		this.temp2 = temp2;
	}
	
	public void setCurrentTem (int current_tem) {
		this.current_tem = current_tem;
	}
	
	public void setWeather (String weather) {
		this.weather = weather;
	}
}
