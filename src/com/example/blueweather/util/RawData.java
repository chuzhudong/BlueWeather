package com.example.blueweather.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.blueweather.R;
import com.example.blueweather.database.BlueWeatherDB;

public class RawData {

	public String provinces;
	
	public String cities;
	
	public String counties;
	
	private Context context;
	
	public RawData(Context ctx) {
		context = ctx;
		provinces = getRawString(R.raw.province);
		cities = getRawString(R.raw.city);
		counties = getRawString(R.raw.county);
	}
	
	public String getRawString(int textId) {
		InputStream inputStream = context.getResources().openRawResource(textId);
		if (inputStream != null) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(inputStream, "utf8"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			StringBuffer sb = new StringBuffer(""); 
			String line; 
			if (reader != null) {
				try {  
		     	  while ((line = reader.readLine()) != null) {  
		     		  sb.append(line);
		     	  }  
				} catch (IOException e) {  
					e.printStackTrace();  
				}
			}
		    return sb.toString();  
		}
		return null;
	}
}
