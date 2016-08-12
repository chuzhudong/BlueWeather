package com.example.blueweather.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlueWeatherOpenHelper extends SQLiteOpenHelper {

	/**
	 * Province表建表语句
	 */
	public static final String CREATE_PROVINCE = "create table Province ("
			+ "id integer primary key autoincrement, " 
			+ "province_name text,"
			+ "province_sort integer,"
			+ "province_remark text)";

	/**
	 * City表建表语句
	 */
	public static final String CREATE_CITY = "create table City ("
			+ "id integer primary key autoincrement," 
			+ "city_name text, "
			+ "province_id integer, " 
			+ "city_sort integer)";

	/**
	 * County表建表语句
	 */
	public static final String CREATE_COUNTY = "create table County ("
			+ "id integer primary key autoincrement, " 
			+ "county_name text, "
			+ "city_id integer, "
			+ "county_sort integer)";

	public BlueWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists Province");
		db.execSQL("drop table if exists City");
		db.execSQL("drop table if exists County");
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTY);
	}

}
