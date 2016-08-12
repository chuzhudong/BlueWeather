package com.example.blueweather.activity;

import java.util.ArrayList;

import com.example.blueweather.R;
import com.example.blueweather.service.AutoUpdateService;
import com.example.blueweather.util.Utility;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingActivity extends Activity implements OnClickListener{

	private RelativeLayout changeCity;
	private RelativeLayout enableGps;
	private RelativeLayout refreshTime;
	private ImageView enableGpsImage;
	private TextView refreshTimeText;
	private Button back;
	
	private boolean isGpsEnabled;
	private String refTime;
	
	private AlertDialog.Builder alertDialogBuilder;
	
	private final static String[] hourList = {"关", "2h", "4h", "6h", "12h", "24h"};
	private final static String TAG = "SettingActivity";
	
	private static final int FROM_APPLICATION = 0;
	private static final int FROM_CITYLIST = 1;
	private static final int FROM_SETTING = 2;
	
	private SharedPreferences.Editor preferenceEditor;
	private SharedPreferences pref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_layout);
		changeCity = (RelativeLayout)findViewById(R.id.layout_changecity);
		enableGps = (RelativeLayout)findViewById(R.id.layout_enablegps);
		refreshTime = (RelativeLayout) findViewById(R.id.layout_refreshtime);
		enableGpsImage = (ImageView)findViewById(R.id.enablegps_image);
		refreshTimeText = (TextView) findViewById(R.id.time_text);
		back = (Button)findViewById(R.id.setting_back);
		
		preferenceEditor = this.getSharedPreferences("data", MODE_PRIVATE).edit();
		pref = getSharedPreferences("data", MODE_PRIVATE);
		
		alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("请选择自动刷新周期");
		alertDialogBuilder.setCancelable(true);
		alertDialogBuilder.setItems(hourList, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            	refreshTimeText.setText(hourList[which]);
            	if (hourList[which].equals("关")) {
            		Log.d(TAG, "stop serviece");
            		Intent intent = new Intent(SettingActivity.this, AutoUpdateService.class);
            		stopService(intent);
            		refreshTimeText.setTextColor(Color.parseColor("#FFFFFF"));
            	} else {
            		String oldUpdateInterval = pref.getString("autoUpdateInterval", "关");
            		Intent intent = new Intent(SettingActivity.this, AutoUpdateService.class);
            		if (!"关".equals(oldUpdateInterval)) {
            			stopService(intent);
            		}
            		int interval = Utility.getUpdateInterval(hourList[which]);
            		String cityName = pref.getString("storedCity", "");
            		intent.putExtra("updateInterval", interval);
            		intent.putExtra("cityName", cityName);
            		startService(intent);
            		refreshTimeText.setTextColor(Color.parseColor("#63B8FF"));
            	}
            	preferenceEditor.putString("autoUpdateInterval", hourList[which]);
            	preferenceEditor.commit();
            	dialog.dismiss();
            }
        });
		
		changeCity.setOnClickListener(this);
		enableGps.setOnClickListener(this);
		refreshTime.setOnClickListener(this);
		
		back.setOnClickListener(this);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.layout_changecity:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_setting_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.layout_enablegps:
			isGpsEnabled = pref.getBoolean("isGpsEnabled", false);
			if (isGpsEnabled == false) {
				enableGpsImage.setImageResource(R.drawable.check_on);
				preferenceEditor.putBoolean("isGpsEnabled", true);
			} else {
				enableGpsImage.setImageResource(R.drawable.check_off);
				preferenceEditor.putBoolean("isGpsEnabled", false);
			}
			preferenceEditor.commit();
			break;
		case R.id.layout_refreshtime:
			alertDialogBuilder.show();
			break;
		case R.id.setting_back:
			onBackPressed();
		default:
			break;
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		isGpsEnabled = pref.getBoolean("isGpsEnabled", false);
		refTime = pref.getString("autoUpdateInterval", "关");
		if (isGpsEnabled == false) {
			enableGpsImage.setImageResource(R.drawable.check_off);
		} else {
			enableGpsImage.setImageResource(R.drawable.check_on);
		}
		refreshTimeText.setText(refTime);
    	if (refTime.equals("关")) {
    		refreshTimeText.setTextColor(Color.parseColor("#FFFFFF"));
    	} else {
    		refreshTimeText.setTextColor(Color.parseColor("#63B8FF"));
    	}
    	preferenceEditor.commit();
	}
	
	@Override
	public void onBackPressed() {
		preferenceEditor.putInt("startFromWhere", FROM_SETTING);
		preferenceEditor.commit();
		Intent intent = new Intent(this, WeatherActivity.class);
		startActivity(intent);
		finish();
	}
}
