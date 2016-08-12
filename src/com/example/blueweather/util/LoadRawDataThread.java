package com.example.blueweather.util;

import com.example.blueweather.activity.BlueWeatherApplication;

public class LoadRawDataThread {
	public static void LoadData(final int level, final RawDataCallBackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				RawData rawData = new RawData(BlueWeatherApplication.getContext());
				if (listener != null) {
					listener.onFinish(level, rawData);
				}
			}
		}).start();
	}
}

