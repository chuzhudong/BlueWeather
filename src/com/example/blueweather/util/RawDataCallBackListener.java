package com.example.blueweather.util;

public interface RawDataCallBackListener {

	void onFinish(int level, RawData rawData);
	void sendMessage(int level);
	
}
