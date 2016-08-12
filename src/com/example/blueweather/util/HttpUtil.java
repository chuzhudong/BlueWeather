package com.example.blueweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.example.blueweather.activity.BlueWeatherApplication;
import com.example.blueweather.model.Weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class HttpUtil {
	
	public static final int LEVEL_PRO = 1;
	public static final int LEVEL_CIT = 2;
	public static final int LEVEL_COU = 3;
	
	public static final int MSG_REQUEST_SUCCESS = 4;
	public static final int MSG_REQUEST_ERROR = 5;
	
	public static final String TAG = "HttpUtil";
	
	private static final int CITY_NONE = -1;
	private static final int NO_WEATHER_DATA = 0;
	private static final int WEATHER_DATA_STORED = 1;
	

	public static void sendHttpRequest(final int day, final String address,  //Not used in this application
			final HttpCallBackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					if (listener != null) {
						// 回调onFinish()方法
						listener.onFinish(day, response.toString());
					}
				} catch (Exception e) {
					if (listener != null) {
						// 回调onError()方法
						listener.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}

	public static void loadRemoteWeaherInfo(final Handler handler, final String cityName, final int level) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Context context = BlueWeatherApplication.getContext();
				SharedPreferences.Editor preferenceEditor = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
				HttpURLConnection connection = null;
				int day = -1;
				Log.d(TAG, "st= " + cityName + ", level = " + level);				
				String st = Utility.filterString(level, cityName);
				Log.d(TAG, "st= " + st + ", level = " + level);
				for (day = 1; day <= 4; day++) {
					String address = "http://php.weather.sina.com.cn/xml.php?city="
							+ Utility.unicodeToGb2312(st) + "&password=DJOYnieT8234jlsK&day=" + Integer.toString(day - 1);
					Log.d(TAG, address);
					try {
						URL url = new URL(address);
						connection = (HttpURLConnection) url.openConnection();
						connection.setRequestMethod("GET");
						connection.setConnectTimeout(5000);
						connection.setReadTimeout(5000);
						InputStream in = connection.getInputStream();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(in));
						StringBuilder response = new StringBuilder();
						String line;
						while ((line = reader.readLine()) != null) {
							response.append(line);
						}
						line = response.toString();
						if (!TextUtils.isEmpty(line)) {
							Weather mWeather = Utility.parseXMLWithPull(line);
							if (mWeather != null) {
								preferenceEditor.putString("day" + Integer.toString(day) + "weather", mWeather.weather);
								preferenceEditor.putInt("day" + Integer.toString(day) + "imgeId", mWeather.imageId);
								preferenceEditor.putInt("day" + Integer.toString(day) + "temp1", mWeather.temp1);
								preferenceEditor.putInt("day" + Integer.toString(day) + "temp2", mWeather.temp2);
								preferenceEditor.putInt("day" + Integer.toString(day) + "current_temp", mWeather.current_tem);
								preferenceEditor.commit();
							} else {
								Log.d(TAG, "mWeather == null error");
								break;
							}
						} else {
							Log.d(TAG,"line empty error");
							break;	
						}
					} catch (Exception e) {
						e.printStackTrace();
						Log.d(TAG, "http connection error");
						break;
					} finally {
						if (connection != null) {
							connection.disconnect();
						}
					}
				}
				if (day == 5) {
					preferenceEditor.putInt("isWeatherDataStored", WEATHER_DATA_STORED);
					preferenceEditor.putString("storedCity", cityName);
					preferenceEditor.commit();
					if (handler != null) {
						Log.d(TAG, "sengding MSG_REQUEST_SUCCESS");
						sendMyMessage(MSG_REQUEST_SUCCESS);
					} else {
						Log.d(TAG, "update weather info successful, but not send message");
					}
				} else {
					preferenceEditor.putInt("isWeatherDataStored", NO_WEATHER_DATA);
					preferenceEditor.putString("storedCity", cityName);
					preferenceEditor.commit();
					if (handler != null) {
						Log.d(TAG, "sengding MSG_REQUEST_ERROR");
						sendMyMessage(MSG_REQUEST_ERROR, level);
					} else {
						Log.d(TAG, "update weather info failed, and not send message");
					}
				}
			}
			
			private void sendMyMessage(int what) {
				Message msg = new Message();
				msg.what = what;
				handler.sendMessage(msg);
			}
			
			private void sendMyMessage(int what, int level) {
				Message msg = new Message();
				msg.what = what;
				msg.arg1 = level;
				handler.sendMessage(msg);
			}
		}).start();
	}
}
