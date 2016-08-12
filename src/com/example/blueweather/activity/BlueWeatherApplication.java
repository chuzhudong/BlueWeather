package com.example.blueweather.activity;

import com.baidu.mapapi.SDKInitializer;
import com.example.blueweather.database.BlueWeatherDB;
import com.example.blueweather.service.AutoUpdateService;
import com.example.blueweather.util.RawData;
import com.example.blueweather.util.Utility;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BlueWeatherApplication extends Application {
	
	private static final int RAWDATA_NOT_LOADED = 0;//应用启动过，但是没有加载过 省市列表
	private static final int RAWDATA_LOADED = 1;//省市列列表已经加载过
	private static final int RAWDATA_NONE = -1;//应用没启动过
	private static final int CITY_NONE = -1;
	private static final int NO_WEATHER_DATA = 0;
	private static final int WEATHER_DATA_STORED = 1;
	
	private static final int FROM_APPLICATION = 0;
	private static final int FROM_CITYLIST = 1;
	private static final int FROM_SETTING = 2;
	
	private static final String DEFAULT_CITY_NAME = "上海市";
	private static final String TAG = "BlueWeatherApplication";
	
	private static Context context;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		context = getApplicationContext();
		SDKInitializer.initialize(this.getApplicationContext());
		
		SharedPreferences.Editor preferenceEditor = this.getSharedPreferences("data", MODE_PRIVATE).edit();
		SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
		int isRawDataLoaded = pref.getInt("isRawDataLoaded", RAWDATA_NONE);
		int isWeatherDataStored = pref.getInt("isWeatherDataStored", CITY_NONE);
		
		if (RAWDATA_NONE == isRawDataLoaded) {
			preferenceEditor.putInt("isRawDataLoaded", RAWDATA_NOT_LOADED);
			preferenceEditor.commit();
		} else if (RAWDATA_NOT_LOADED == isRawDataLoaded) {	
			
		} else {	
		}
		
		if (isWeatherDataStored == CITY_NONE) {
			preferenceEditor.putString("storedCity", DEFAULT_CITY_NAME);
			preferenceEditor.putInt("isWeatherDataStored", NO_WEATHER_DATA);
			preferenceEditor.commit();
		} else if (isWeatherDataStored == NO_WEATHER_DATA) {
			
		} else {
			
		}
		
		String oldUpdateInterval = pref.getString("autoUpdateInterval", "关");
		Log.d("application", oldUpdateInterval);
		if (!"关".equals(oldUpdateInterval)) {
			Intent intent = new Intent(context, AutoUpdateService.class);
			int interval = Utility.getUpdateInterval(oldUpdateInterval);
			String cityName = pref.getString("storedCity", "");
			intent.putExtra("updateInterval", interval);
			intent.putExtra("cityName", cityName);
			startService(intent);
		}

		preferenceEditor.putInt("startFromWhere", FROM_APPLICATION);
		preferenceEditor.commit();
	}
	
	public static Context getContext() {
		return context;
	}
	
}
