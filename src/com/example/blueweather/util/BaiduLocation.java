package com.example.blueweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.blueweather.activity.BlueWeatherApplication;
import android.content.SharedPreferences;

public class BaiduLocation {
	
	private LocationClient locationClient;
	private MyLocationListenner locationListener;
	private LocationClientOption option;
	private GeoCoder geoCoder;
	
	private static final String TAG = "BaiduLocation";
	private static final int MSG_LOCATED_SUCCESS = 6;
	private static final int MSG_LOCATED_ERROR = 7;
	
	private static final int RAWDATA_NOT_LOADED = 0;
	private static final int RAWDATA_LOADED = 1;
	private static final int RAWDATA_NONE = -1;
	
	private SharedPreferences.Editor preferenceEditor;
	private SharedPreferences pref;
	
	private Handler msgHandler;
	
	public BaiduLocation(Context context, Handler handler) {
		msgHandler = handler;
		locationClient = new LocationClient(context);
		locationListener = new MyLocationListenner();
		locationClient.registerLocationListener(locationListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(999);//只定位一次，requestLocation一次就定位一次；
		locationClient.setLocOption(option);
		locationClient.start();
		
		context = BlueWeatherApplication.getContext();
		pref = context.getSharedPreferences("data", context.MODE_PRIVATE);
		preferenceEditor = pref.edit();

		geoCoder = GeoCoder.newInstance();
		geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
					Log.d(TAG, "reverse GeoCode failed!");
					return;
				}
				onSaveGeoResult(result.getAddress());
			}

			@Override
			public void onGetGeoCodeResult(GeoCodeResult arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void stopBaiduApi() {
		if (locationClient != null) {
			locationClient.stop();
		}
	}
	
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null) {
				return;
			}
			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
			Log.d(TAG, "OnReceiveLocation succ.");
		}
	}
	
	public void onSaveGeoResult(String source) {
		if (!TextUtils.isEmpty(source)) {
			String des = "";
			String charSet = "省市区州盟县旗";
			int pos[] = {0, -1, -1, -1};
			int iOfPos = 0, iOfSource = 0;
			Log.d(TAG, "locationg adrress = " + source);
			for ( ; iOfPos < 4 && iOfSource < source.length(); iOfSource++) {
				if (-1 != charSet.indexOf(source.charAt(iOfSource))) {
					iOfPos += 1;
					pos[iOfPos] = iOfSource + 1;
				}
			}
			if(iOfPos > 0) {
				des = source.substring(pos[iOfPos - 1], pos[iOfPos]);
				preferenceEditor.putBoolean("isLocated", true);
				preferenceEditor.putString("locatedCity", des);
				preferenceEditor.commit();
				if (pref.getInt("isRawDataLoaded", RAWDATA_NONE) == RAWDATA_LOADED
						&& pref.getBoolean("isGpsEnabled", false)) {
					/*two kind of events will trigger to locate: 1. locationClient.start() called; 
					 * 2.locationClient.requestLocation() called.
					 * locationClient.start() is called whenever WeatherActivity is created. This 
					 * causes trouble when blueWeatherDB not ready or location option disabled。
					 * so when locationCLient locates successfully, those feathers mentioned above should 
					 * be checked.
					 */
					Log.d(TAG, "sengding mesg MSG_LOCATED_SUSS");
					sendMessage(MSG_LOCATED_SUCCESS);
				} else {
					Log.d(TAG, "located sucess, but cannot send message");
				}
			} else {
				sendMessage(MSG_LOCATED_ERROR);
				Log.d(TAG, "invalid address");
			}
		}
	}
	
	private void sendMessage(int what) {
		Message msg = new Message();
		msg.what = what;
		msgHandler.sendMessage(msg);
	}
	
	public void requestLocation() {
		preferenceEditor.putBoolean("isLocated", false);
		preferenceEditor.putString("locatedCity", "上海市");
		locationClient.requestLocation();
	}
}
