package com.example.blueweather.receiver;

import com.example.blueweather.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class AutoUpdateReceiver extends BroadcastReceiver {
	private static final String TAG = "AutoUpdateReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		SharedPreferences pref = context.getSharedPreferences("data", Context.MODE_PRIVATE);
		if (!"นุ".equals(pref.getString("autoUpdateInterval", "นุ"))) {
			String updateInterval = pref.getString("autoUpdateInterval", "นุ");
			String cityName = pref.getString("storedCity", "");
			intent.putExtra("updateInterval", updateInterval);
    		intent.putExtra("cityName", cityName);
			Intent i = new Intent(context, AutoUpdateService.class);
			context.startService(i);
		} else {
			Log.d(TAG,"auto update canceled");
		}
	}
}
