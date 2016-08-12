package com.example.blueweather.util;

public interface HttpCallBackListener { //Not used in this application
	
	void onFinish(int day, String reponse);
	void sendMessage(int what);
	void onError(Exception e);

}
