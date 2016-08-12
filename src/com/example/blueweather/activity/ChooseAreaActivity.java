package com.example.blueweather.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.example.blueweather.R;
import com.example.blueweather.database.BlueWeatherDB;
import com.example.blueweather.model.City;
import com.example.blueweather.model.County;
import com.example.blueweather.model.Province;
import com.example.blueweather.util.LoadRawDataThread;
import com.example.blueweather.util.RawData;
import com.example.blueweather.util.RawDataCallBackListener;
import com.example.blueweather.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PRO = 1;
	public static final int LEVEL_CIT = 2;
	public static final int LEVEL_COU = 3;
	
	private static final int CITY_NONE = -1;
	private static final int NO_WEATHER_DATA = 0;
	private static final int WEATHER_DATA_STORED = 1;
	
	private static final int RAWDATA_NOT_LOADED = 0;
	private static final int RAWDATA_LOADED = 1;
	private static final int RAWDATA_NONE = -1;
	
	private static final int FROM_APPLICATION = 0;
	private static final int FROM_CITYLIST = 1;
	private static final int FROM_SETTING = 2;

	private static final String TAG = "ChooseAreaActivity";
	
	private boolean isFromSettingActivity;
	
	SharedPreferences.Editor preferenceEditor;
	SharedPreferences pref;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private BlueWeatherDB blueWeatherDB;
	private RawData rawData;
	private List<String> dataList = new ArrayList<String>();

	/**
	 * list of provinces
	 */
	private List<Province> provinceList;
	/**
	 * List of city
	 */
	private List<City> cityList;
	/**
	 * List of county
	 */
	private List<County> countyList;
	/**
	 * the selected province
	 */
	private Province selectedProvince;
	/**
	 * the selected city
	 */
	private City selectedCity;
	/**
	 * the selected county
	 */
	private County selectedCounty;
	/**
	 * the current selected Level
	 */
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		isFromSettingActivity = getIntent().getBooleanExtra("from_setting_activity", false);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		blueWeatherDB = BlueWeatherDB.getInstance(this);
		// rawData = new RawData(this);
		preferenceEditor = getSharedPreferences("data", MODE_PRIVATE).edit();
		pref = getSharedPreferences("data", MODE_PRIVATE);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (currentLevel == LEVEL_PRO) {
					selectedProvince = provinceList.get(index);
					queryCities();
					Log.d(TAG, "currentLevel = " + currentLevel + "selectedPro = " + selectedProvince.getProName());
				} else if (currentLevel == LEVEL_CIT) {
					selectedCity = cityList.get(index);
					queryCounties();
					Log.d(TAG, "currentLevel = " + currentLevel + "selectedCit = " + selectedCity.getCityName());
				} else if (currentLevel == LEVEL_COU) {
					selectedCounty = countyList.get(index);
					Log.d(TAG, "currentLevel = " + currentLevel + "selected County" + selectedCounty.getCountyName());
					toShowNewCityWeather(selectedCounty.getCountyName(), LEVEL_COU);
				}
			}
		});
		queryProvinces(); // 没有点击的时候，直接加载省级数据
	}

	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryProvinces() {
		provinceList = blueWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PRO;
		} else {
			// queryFromRawData(0, LEVEL_PRO);
			queryFromRawData(LEVEL_PRO);
		}
	}

	/**
	 * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryCities() {
		cityList = blueWeatherDB.loadCities(selectedProvince.getProId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProName());
			currentLevel = LEVEL_CIT;
		} else {
			// queryFromRawData(selectedProvince.getProId(), LEVEL_CIT);
			queryFromRawData(LEVEL_CIT);
		}
	}

	/**
	 * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryCounties() {
		SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
		int isRawDataLoaded = pref.getInt("isRawDataLoaded", RAWDATA_NONE);
		countyList = blueWeatherDB.loadCounties(selectedCity.getCityId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COU;
		} else if (RAWDATA_NOT_LOADED == isRawDataLoaded) { //加入if判断是防止有些 lEVEl_CIT没有LEVE_COU
			// queryFromRawData(selectedCity.getCityId(), LEVEL_COU);
			queryFromRawData(LEVEL_COU);
		} else if (RAWDATA_LOADED == isRawDataLoaded) { //countList为空且RAWDATA_LOADED，表明要显示LEVEL_CIT的天气
			Log.d(TAG, "selected City" + selectedCity.getCityName());
			toShowNewCityWeather(selectedCity.getCityName(), LEVEL_CIT);
		}
	}

	private void queryFromRawData(final int level) {
		showProgressDialog("Loading city list at first lauch...");
		LoadRawDataThread.LoadData(level, new RawDataCallBackListener() {
			@Override
			public void onFinish(final int level, final RawData rawData) {
				boolean result = false;
				String rawDataString = null;
				switch (level) {
				case LEVEL_PRO:
					rawDataString = rawData.provinces;
					if (rawDataString != null) {
						result = Utility.handleProvincesData(blueWeatherDB,
								rawDataString);
					} else {
						Toast.makeText(getApplicationContext(),
								"Load Provice List Failed!", Toast.LENGTH_SHORT);
					}
					break;
				case LEVEL_CIT:
					rawDataString = rawData.cities;
					if (rawDataString != null) {
						result = Utility.handleCitiesData(blueWeatherDB,
								rawDataString);
					} else {
						Toast.makeText(getApplicationContext(),
								"Load City List Failed!", Toast.LENGTH_SHORT);
					}
					break;
				case LEVEL_COU:
					rawDataString = rawData.counties;
					if (rawDataString != null) {
						result = Utility.handleCountiesData(blueWeatherDB,
								rawDataString);
					} else {
						Toast.makeText(getApplicationContext(),
								"Load County List Failed!", Toast.LENGTH_SHORT);
					}
					break;
				default:
					break;
				}
				if (result) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							switch (level) {
							case LEVEL_PRO:
								queryProvinces();
								break;
							case LEVEL_CIT:
								queryCities();
								break;
							case LEVEL_COU:
								int isRawDataLoaded = pref.getInt(
										"isRawDataLoaded", RAWDATA_NONE);
								if (RAWDATA_NOT_LOADED == isRawDataLoaded) {
									preferenceEditor.putInt("isRawDataLoaded",
											RAWDATA_LOADED);
									preferenceEditor.commit();
								}
								queryCounties();
								break;
							default:
								break;
							}
						}
					});
				}
			}

			@Override
			public void sendMessage(int level) {
				// TODO Auto-generated method stub
				
			}
		});
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
			progressDialog.dismiss();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COU) {
			queryCities();
		} else if (currentLevel == LEVEL_CIT) {
			queryProvinces();
		} else if (currentLevel == LEVEL_PRO) {
			if (isFromSettingActivity == true) {
				Intent intent = new Intent(this, SettingActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
	
	private void toShowNewCityWeather(String cityName, int level) {
		if (!TextUtils.isEmpty(cityName))
		{
			Log.d(TAG,"toShowNewCityWeather" + cityName);
			preferenceEditor.putInt("isWeatherDataStored", NO_WEATHER_DATA);
			preferenceEditor.putString("storedCity", cityName);
			preferenceEditor.putInt("startFromWhere", FROM_CITYLIST);
			preferenceEditor.putBoolean("isGpsEnabled", false);
			preferenceEditor.putBoolean("isLocated", false);
			preferenceEditor.commit();
			Intent intent = new Intent(this, WeatherActivity.class);
			intent.putExtra("cityName", cityName);
			intent.putExtra("cityLevel", level);
			startActivity(intent);
		} else {
			//
		}
		finish();
	}
}
