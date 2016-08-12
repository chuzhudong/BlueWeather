package com.example.blueweather.activity;

import com.example.blueweather.R;
import com.example.blueweather.database.BlueWeatherDB;
import com.example.blueweather.model.Weather;
import com.example.blueweather.util.BaiduLocation;
import com.example.blueweather.util.HttpCallBackListener;
import com.example.blueweather.util.HttpUtil;
import com.example.blueweather.util.LoadRawDataThread;
import com.example.blueweather.util.RawData;
import com.example.blueweather.util.RawDataCallBackListener;
import com.example.blueweather.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity implements OnClickListener{
	
	private static final String CELSIUS= "\u2103";

	private static final String TAG = "WeatherActivity";
	
	private static final int CITY_NONE = -1;
	private static final int NO_WEATHER_DATA = 0;
	private static final int WEATHER_DATA_STORED = 1;
	
	public static final int LEVEL_PRO = 1;
	public static final int LEVEL_CIT = 2;
	public static final int LEVEL_COU = 3;
	
	public static final int MSG_REQUEST_SUCCESS = 4;
	public static final int MSG_REQUEST_ERROR = 5;
	
	public static final int MSG_LOCATED_SUCCESS = 6;
	public static final int MSG_LOCATED_ERROR = 7;
	
	//public static final int SHOW_WEATHER = 4;
	private static final int FROM_APPLICATION = 0;
	private static final int FROM_CITYLIST = 1;
	private static final int FROM_SETTING = 2;
	
	private static final int RAWDATA_NOT_LOADED = 0;
	private static final int RAWDATA_LOADED = 1;
	private static final int RAWDATA_NONE = -1;
	
	private RelativeLayout watherInfoLayout;
	
	private TextView cityNameText;
	
	private ImageView day1Image;
	private TextView day1Temp1;
	private TextView day1Temp2;
	private TextView day1CurrentTemp;
	private TextView day1Weather;
	
	private TextView day2Text;
	private ImageView day2Image;
	private TextView day2Temp1;
	private TextView day2Temp2;
	
	private TextView day3Text;
	private ImageView day3Image;
	private TextView day3Temp1;
	private TextView day3Temp2;
	
	private TextView day4Text;
	private ImageView day4Image;
	private TextView day4Temp1;
	private TextView day4Temp2;
	
	private ImageView gpsStatusImage;
	
	private Button updateBtn;
	
	private Button settingBtn;

	private ProgressDialog progressDialog;
	
	private SharedPreferences.Editor preferenceEditor;
	private SharedPreferences pref;
	
	private Weather[] weather = new Weather[4];
	
	private BlueWeatherDB blueWeatherDB;
	private BaiduLocation baiduLocation;
	
	private String cityName;
	
	private int startFromWhere;
	private int isWeatherDataStored;
	private int isRawDataLoaded;
	private boolean isGpsEnabled;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		watherInfoLayout = (RelativeLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		
		day1Image = (ImageView) findViewById(R.id.day1_image);
		day1Temp1 = (TextView) findViewById(R.id.day1_temp1);
		day1Temp2 = (TextView) findViewById(R.id.day1_temp2);
		day1CurrentTemp = (TextView) findViewById(R.id.day1_current_temp);
		day1Weather = (TextView) findViewById(R.id.day1_weather);
		
		day2Text = (TextView) findViewById(R.id.day2_text);
		day2Image = (ImageView) findViewById(R.id.day2_image);
		day2Temp1 = (TextView) findViewById(R.id.day2_temp1);
		day2Temp2 = (TextView) findViewById(R.id.day2_temp2);
		
		day3Text = (TextView) findViewById(R.id.day3_text);
		day3Image = (ImageView) findViewById(R.id.day3_image);
		day3Temp1 = (TextView) findViewById(R.id.day3_temp1);
		day3Temp2 = (TextView) findViewById(R.id.day3_temp2);
		
		day4Text = (TextView) findViewById(R.id.day4_text);
		day4Image = (ImageView) findViewById(R.id.day4_image);
		day4Temp1 = (TextView) findViewById(R.id.day4_temp1);
		day4Temp2 = (TextView) findViewById(R.id.day4_temp2);
		
		updateBtn = (Button) findViewById(R.id.update);
		settingBtn = (Button) findViewById(R.id.setting);
		
		gpsStatusImage = (ImageView) findViewById(R.id.gps_status);
		
		updateBtn.setOnClickListener(this);
		settingBtn.setOnClickListener(this);
		
		blueWeatherDB = BlueWeatherDB.getInstance(this);
		baiduLocation = new BaiduLocation(this, handler);
		Log.d(TAG, "onCreate");
		
		pref = getSharedPreferences("data", MODE_PRIVATE);
		preferenceEditor = pref.edit();

		for(int i = 0; i < 4; i++) {
			weather[i] = new Weather();//????is
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart() called!");
		isWeatherDataStored = pref.getInt("isWeatherDataStored", CITY_NONE);
		isRawDataLoaded = pref.getInt("isRawDataLoaded", RAWDATA_NONE);
		startFromWhere = pref.getInt("startFromWhere", -1);
		isGpsEnabled = pref.getBoolean("isGpsEnabled", false);
		
		if (RAWDATA_NOT_LOADED == isRawDataLoaded) {
			buildDataBase();
		} else {
			showSinaWeather();
		}
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy is called .........");
		baiduLocation.stopBaiduApi();
		super.onDestroy();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		/*
		 * attention:if launchMode is set to be standard, multiple WeatherActivities will be created 
		 * when it comes to the top. Each one of activities holds a baiduLocation member, and this
		 * case baiduLocation.locationClient.start() called many times. This is not what we want.
		 * We just need on instance of WeatherActivity and BaiduLocation, so WeatherActivity must be registered to be singleTask mode.
		 * But singleTask mode cause another problem--it does not update intent automatically when it comes to the top.
		 * For this reason, onNewIntent() must be overwritten here, and setIntent() must be called in it to update
		 * intent。
		 */
		super.onNewIntent(intent);
		Log.d(TAG, "onNewIntent");
		setIntent(intent);
	}
	
	private void showCity(String cityName) {
		if (!TextUtils.isEmpty(cityName)) {
			if (isGpsEnabled) {
				gpsStatusImage.setVisibility(View.VISIBLE);
			} else {
				gpsStatusImage.setVisibility(View.INVISIBLE);
			}
			String storedCityName = pref.getString("storedCity", cityName);
			cityNameText.setText(storedCityName);
		} else {
			Log.d(TAG,"cityName == null");
		}
	}
	
	private void showProgressDialog(String string) {
		// TODO Auto-generated method stub
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage(string);
			progressDialog.setCancelable(false);
		}
		progressDialog.show();
	}

	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog != null) {
			Log.d(TAG, "dismiss");
			progressDialog.dismiss();
		}
	}
	private void showWeather(Weather [] weather) {
		if (weather != null) {
			day1Image.setImageResource(weather[0].imageId);
			day1Temp1.setText(String.valueOf(weather[0].temp1) + CELSIUS);
			day1Temp2.setText(String.valueOf(weather[0].temp2) + CELSIUS);
			day1CurrentTemp.setText(String.valueOf(weather[0].current_tem + CELSIUS));
			day1Weather.setText(weather[0].weather);
			day2Image.setImageResource(weather[1].imageId);
			day2Temp1.setText(String.valueOf(weather[1].temp1) + CELSIUS);
			day2Temp2.setText(String.valueOf(weather[1].temp2) + CELSIUS);
			day3Image.setImageResource(weather[2].imageId);
			day3Temp1.setText(String.valueOf(weather[2].temp1) + CELSIUS);
			day3Temp2.setText(String.valueOf(weather[2].temp2) + CELSIUS);
			day4Image.setImageResource(weather[3].imageId);
			day4Temp1.setText(String.valueOf(weather[3].temp1) + CELSIUS);
			day4Temp2.setText(String.valueOf(weather[3].temp2) + CELSIUS);
			watherInfoLayout.setVisibility(View.VISIBLE);
		} else {
			Log.d(TAG, "weather == null");
		}
	}
	
	private boolean loadWeatherDataFromLocal(Weather [] weather) {
		
		if (weather != null) {
			weather[0].current_tem = pref.getInt("day1current_temp", -1);
			weather[0].imageId = pref.getInt("day1imgeId", -1);
			weather[0].weather = pref.getString("day1weather", "");
			weather[0].temp1 = pref.getInt("day1temp1", -1);
			weather[0].temp2 = pref.getInt("day1temp2", -1);
			
			weather[1].imageId = pref.getInt("day2imgeId", -1);
			weather[1].temp1 = pref.getInt("day2temp1", -1);
			weather[1].temp2 = pref.getInt("day2temp2", -1);
			
			weather[2].imageId = pref.getInt("day3imgeId", -1);
			weather[2].temp1 = pref.getInt("day3temp1", -1);
			weather[2].temp2 = pref.getInt("day3temp2", -1);
			
			weather[3].imageId = pref.getInt("day4imgeId", -1);
			weather[3].temp1 = pref.getInt("day4temp1", -1);
			weather[3].temp2 = pref.getInt("day4temp2", -1);
			
			return true;
		}
		return false;
	}
	
	private void showRemoteWeatherInfo (String cityName) {
		Log.d(TAG,"showRemoteweatherInfo");
		if(!TextUtils.isEmpty(cityName)) {
			int level = blueWeatherDB.queryCityNameLevel(cityName);
			showProgressDialog("Loading weather data ...");
			HttpUtil.loadRemoteWeaherInfo(handler, cityName, level);
		} else {
			Log.d(TAG,"cityName == null");
		}
	}
	
	private void showLocalWeatherInfo() {
		boolean ret = loadWeatherDataFromLocal(weather);
		if (ret) {					
			showWeather(weather);
			showCity(cityName);
		} else {
			Toast.makeText(this, "Load weather info. failed!", Toast.LENGTH_LONG).show();
		}
	}
	
	private void queryFromRawData(final int level) {
		LoadRawDataThread.LoadData(level, new RawDataCallBackListener() {
			
			@Override
			public void onFinish(final int level, final RawData rawData) {
				boolean result = false;
				String rawDataString = null;
				switch (level) {
				case LEVEL_PRO:
					rawDataString = rawData.provinces;
					Log.d(TAG, rawDataString);
					if (rawDataString != null) {
						result = Utility.handleProvincesData(blueWeatherDB,
								rawDataString);
					} else {
						Log.d(TAG, "Load Province List Failed!");
					}
					sendMessage(LEVEL_PRO);
					break;
				case LEVEL_CIT:
					rawDataString = rawData.cities;
					if (rawDataString != null) {
						result = Utility.handleCitiesData(blueWeatherDB,
								rawDataString);
					} else {
						Log.d(TAG, "Load City List Failed!");
					}
					sendMessage(LEVEL_CIT);
					break;
				case LEVEL_COU:
					rawDataString = rawData.counties;
					if (rawDataString != null) {
						result = Utility.handleCountiesData(blueWeatherDB,
								rawDataString);
					} else {
						Log.d(TAG, "Load County List Failed!");
					}
					isRawDataLoaded = pref.getInt("isRawDataLoaded",//调整位置？？？？
							RAWDATA_NONE);
					if (RAWDATA_NOT_LOADED == isRawDataLoaded) {
						Log.d(TAG, "raw Data loaded");
						preferenceEditor.putInt("isRawDataLoaded", RAWDATA_LOADED);
						preferenceEditor.commit();
					}
					sendMessage(LEVEL_COU);
					break;
				default:
					break;
				}
			}

			@Override
			public void sendMessage(int what) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = what;
				handler.sendMessage(msg);
			}
		});
	}
	
	private void buildDataBase() {
		showProgressDialog("Loading city list at first lanch...");
		queryFromRawData(LEVEL_PRO);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		case R.id.update:
			Log.d(TAG, "update" + cityName);
			showRemoteWeatherInfo(cityName);
			break;
		case R.id.setting:
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			//finish();
			break;
		default:
			break;
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch ( msg.what) {
			case LEVEL_PRO:
				queryFromRawData(LEVEL_CIT);
				break;
			case LEVEL_CIT:
				queryFromRawData(LEVEL_COU);
				break;
			case LEVEL_COU:
				closeProgressDialog();
				showSinaWeather();
				break;
			case MSG_REQUEST_SUCCESS:
				closeProgressDialog();
				showLocalWeatherInfo();
				break;
			case MSG_REQUEST_ERROR:
				closeProgressDialog();
				if (msg.arg1 == LEVEL_COU) {
					String superLevelName = blueWeatherDB.queryCityNameSuperLevelName(cityName, LEVEL_COU);
					showRemoteWeatherInfo(superLevelName);
				} else if (msg.arg1 == LEVEL_CIT){
					
					Toast.makeText(WeatherActivity.this, "Query wether data ERROR, try later!", Toast.LENGTH_SHORT).show();
				}
				showCity(cityName);
				Log.d(TAG, "MSG_REQUEST_ERROR");
				break;
			case MSG_LOCATED_SUCCESS:
				cityName = pref.getString("locatedCity", "上海市");
				if (cityName.equals(pref.getString("storedCity", "上海市"))
						&& pref.getInt("isWeatherDataStored", NO_WEATHER_DATA) == WEATHER_DATA_STORED) {
					showLocalWeatherInfo();
				} else {
					showRemoteWeatherInfo(cityName);
				}
				gpsStatusImage.setVisibility(View.VISIBLE);
				break;
			case MSG_LOCATED_ERROR:
				Toast.makeText(WeatherActivity.this, "Location failed, switch city Manually!", Toast.LENGTH_SHORT).show();
			default:
				break;
			}
		}
	};
	
	private void showSinaWeather() {
		if (!isGpsEnabled) {
			Log.d(TAG, "is GpsEnabled == false");
			if (startFromWhere != FROM_CITYLIST) {
				Log.d(TAG,"fromApplication");
				cityName = pref.getString("storedCity", "霍山县");
				if (isWeatherDataStored == NO_WEATHER_DATA) {
					Log.d(TAG, "No weather data, ");
					showRemoteWeatherInfo(cityName);
				} else if (isWeatherDataStored == WEATHER_DATA_STORED) {
					showLocalWeatherInfo();
				} else {
					Toast.makeText(this, "APP ERROR", Toast.LENGTH_SHORT).show();
				}
			} else {
				Log.d(TAG, "fromCityList");
				cityName = getIntent().getStringExtra("cityName");
				showRemoteWeatherInfo(cityName);
			}
		} else {
			Log.d(TAG, "gps enabled");
			if (startFromWhere == FROM_APPLICATION) {
				baiduLocation.requestLocation();
			} else if (startFromWhere == FROM_SETTING){
				if (pref.getBoolean("isLocated", false) &&
						isWeatherDataStored == WEATHER_DATA_STORED &&
						pref.getString("locatedCity", "").equals(pref.getString("storedCity", ""))) {
					cityName = pref.getString("storedCity", "霍山县");
					showLocalWeatherInfo();
				} else {
					baiduLocation.requestLocation();
				}
			} else {
				cityName = getIntent().getStringExtra("cityName");
				showRemoteWeatherInfo(cityName);
			}
		}
	}
}
