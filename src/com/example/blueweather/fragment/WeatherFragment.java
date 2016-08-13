package com.example.blueweather.fragment;

import com.example.blueweather.R;
import com.example.blueweather.model.Weather;
import com.example.blueweather.activity.WeatherActivity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherFragment extends Fragment {
	
	private static final String CELSIUS= "\u2103";
	private static final String TAG = "WeatherFragment";
	
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
	
	@Override
	public void onAttach(Context  context) {
		super.onAttach(context);
		for(int i = 0; i < 4; i++) {
			//
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.weather_fragment, container, false);
		day1Image = (ImageView) view.findViewById(R.id.day1_image);
		day1Temp1 = (TextView) view.findViewById(R.id.day1_temp1);
		day1Temp2 = (TextView) view.findViewById(R.id.day1_temp2);
		day1CurrentTemp = (TextView) view.findViewById(R.id.day1_current_temp);
		day1Weather = (TextView) view.findViewById(R.id.day1_weather);
		
		day2Text = (TextView) view.findViewById(R.id.day2_text);
		day2Image = (ImageView) view.findViewById(R.id.day2_image);
		day2Temp1 = (TextView) view.findViewById(R.id.day2_temp1);
		day2Temp2 = (TextView) view.findViewById(R.id.day2_temp2);
		
		day3Text = (TextView) view.findViewById(R.id.day3_text);
		day3Image = (ImageView) view.findViewById(R.id.day3_image);
		day3Temp1 = (TextView) view.findViewById(R.id.day3_temp1);
		day3Temp2 = (TextView) view.findViewById(R.id.day3_temp2);
		
		day4Text = (TextView) view.findViewById(R.id.day4_text);
		day4Image = (ImageView) view.findViewById(R.id.day4_image);
		day4Temp1 = (TextView) view.findViewById(R.id.day4_temp1);
		day4Temp2 = (TextView) view.findViewById(R.id.day4_temp2);
		
		return view;
	}
	
	
	public void showWeather(Weather[] weather) {
		if (weather[0] != null) {
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
		} else {
			Log.d(TAG, "weather == null");
		}
	}
}
