package com.example.blueweather.model;

public class Province {

	private int proId;

	private String proName;

	private int proSort;

	private String proRemark;
	
	public Province() {
		return;
	}

	public Province(int proId, String proName, int proSort, String proRemark) {
		this.proId = proId;
		this.proName = proName;
		this.proSort = proSort;
		this.proRemark = proRemark;
	}

	public void setProId(int proId) {
		this.proId = proId;
	}
	
	public void setProName(String proName) {
		this.proName = proName;
	}
	
	public void setProSort(int proSort) {
		this.proSort = proSort;
	}
	
	public void setProRemark(String proRemark) {
		this.proRemark = proRemark;	
	}
	
	public int getProId() {
		return proId;
	}
	
	public String getProName() {
		return proName;
	}
	
	public int getProSort() {
		return proSort;
	}
	
	public String getProRemark() {
		return proRemark;	
	}
}



