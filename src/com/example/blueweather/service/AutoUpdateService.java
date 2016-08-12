package com.example.blueweather.service;

import com.example.blueweather.activity.BlueWeatherApplication;
import com.example.blueweather.database.BlueWeatherDB;
import com.example.blueweather.receiver.AutoUpdateReceiver;
import com.example.blueweather.util.HttpUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class AutoUpdateService extends Service {
	
	private AlarmManager manager;
	private PendingIntent pi;
	
	private static final String TAG = "AutoUpdateService";
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "start serviece");
		String cityName = intent.getStringExtra("cityName");
		int interval = intent.getIntExtra("updateInterval", 4);
		Log.d(TAG, "cityName + interval" + cityName + interval);
		int level = BlueWeatherDB.getInstance(BlueWeatherApplication.getContext()).queryCityNameLevel(cityName);
		HttpUtil.loadRemoteWeaherInfo(null, cityName, level);
		manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = interval * 60 * 60 * 1000; // 这是8小时的毫秒数
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void updateWeather() {
		//
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestory");
		manager.cancel(pi);
	}
}
