package com.example.blueweather.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.blueweather.model.City;
import com.example.blueweather.model.County;
import com.example.blueweather.model.Province;

public class BlueWeatherDB {
	
	public static final int LEVEL_PRO = 1;
	public static final int LEVEL_CIT = 2;
	public static final int LEVEL_COU = 3;
	
	private static final String TAG = "BlueWeatherDB";
	/**
	 * name of database
	 */
	private static final String DB_NAME = "blue_weather";
	
	/**
	 * version of database
	 */
	private static final int DB_VERSION = 1;

	private static BlueWeatherDB blueWeatherDB;
	
	private SQLiteDatabase db;
	
	/**
	 * private constructor
	 */
	private BlueWeatherDB(Context context) {
		BlueWeatherOpenHelper dbHelper = new BlueWeatherOpenHelper(context, DB_NAME, null, DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * get instance of BlueWeatherDB
	 */
	
	public synchronized static BlueWeatherDB getInstance(Context context) {
		if (null == blueWeatherDB) {
			blueWeatherDB = new BlueWeatherDB(context);
		}
		return blueWeatherDB;
	}
	
	/**
	 * save Province Object to blueWeatherDB
	 */
	public void saveProvince(Province province)	 {
		if (province != null) {
			ContentValues value = new ContentValues();
			value.put("province_name", province.getProName());
			value.put("province_sort", province.getProSort());
			value.put("province_remark", province.getProRemark());
			db.insert("Province", null, value);
		}
	}
	
	/**
	 * load Province Objects to list
	 */
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setProId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProSort(cursor.getInt(cursor.getColumnIndex("province_sort")));
				province.setProRemark(cursor.getString(cursor.getColumnIndex("province_remark")));
				list.add(province);
			} while(cursor.moveToNext());
		}
		return list;
	}
	
	/**
	 * save Province Object to blueWeatherDB
	 */
	public void saveCity(City city)	 {
		if (city != null) {
			ContentValues value = new ContentValues();
			value.put("city_name", city.getCityName());
			value.put("province_id", city.getProId());
			value.put("city_sort", city.getCitySort());
			db.insert("City", null, value);
		}
	}
	
	/**
	 * load City Object to list
	 */
	public List<City> loadCities(int provinceId)	 {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?", new String[] {String.valueOf(provinceId)}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setCityId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setProId(cursor.getInt(cursor.getColumnIndex("province_id")));
				city.setCitySort(cursor.getInt(cursor.getColumnIndex("city_sort")));
				list.add(city);
			} while(cursor.moveToNext());
		}
		return list;
	}
	
	/**
	 * save County Object to blueWeatherDB
	 */
	public void saveCounty(County county)	 {
		if (county != null) {
			ContentValues value = new ContentValues();
			value.put("county_name", county.getCountyName());
			value.put("city_id", county.getCityId());
			value.put("county_sort", county.getCountySort());
			db.insert("County", null, value);
		}
	}
	
	/**
	 * load County Object to list
	 */
	public List<County> loadCounties(int cityId){
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id = ?", new String[] {String.valueOf(cityId)}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setCountyId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				county.setCountySort(cursor.getInt(cursor.getColumnIndex("county_sort")));
				list.add(county);
			} while(cursor.moveToNext());
		}
		return list;
	}
	
	/**
	 * 查询cityName是属于哪一个级别
	 */
	public int queryCityNameLevel(String cityName) {
		Log.d(TAG, "in queryCityNameLevel" + cityName);
		int level = -1;
		Cursor cursor = db.query("County", null, "county_name = ?", new String[] {cityName}, null, null, null);
		if (cursor.moveToFirst()) {
			Log.d(TAG, "city = " + cityName + "level = " + LEVEL_COU);
			return LEVEL_COU;
		} else {
			cursor = db.query("City", null, "city_name = ?", new String[] {cityName}, null, null, null);
			if (cursor.moveToFirst()) {
				Log.d(TAG, "city = " + cityName + "level = " + LEVEL_CIT);
				return LEVEL_CIT;
			} else {
				cursor = db.query("Province", null, "province_name = ?", new String[] {cityName}, null, null, null);
				if (cursor.moveToFirst()) {
					Log.d(TAG, "city = " + cityName + "level = " + LEVEL_PRO);
					return LEVEL_PRO;
				}
			}
		}
		return level;
	}
	
	public String queryCityNameSuperLevelName(String cityName, int level) {
		switch (level) {
		case LEVEL_COU:
		{
			Cursor cursor = db.query("County", null, "county_name = ?", new String[] {cityName}, null, null, null);
			if (cursor.moveToFirst()) {
				int cityId = cursor.getInt(cursor.getColumnIndex("city_id"));
				cursor = db.query("City", null, "id = ?", new String[] {String.valueOf(cityId)}, null, null, null);
				if (cursor.moveToFirst()) {
					return cursor.getString(cursor.getColumnIndex("city_name"));
				}
			}
			break;
		}
		case LEVEL_CIT:
		{
			Cursor cursor = db.query("City", null, "city_name = ?", new String[] {cityName}, null, null, null);
			if (cursor.moveToFirst()) {
				int provinceId = cursor.getInt(cursor.getColumnIndex("province_id"));
				cursor = db.query("Province", null, "id = ?", new String[] {String.valueOf(provinceId)}, null, null, null);
				if (cursor.moveToFirst()) {
					return cursor.getString(cursor.getColumnIndex("province_name"));
				}
			}
			break;
		}
		default:
			break;
		}
		return null;
	}
}

