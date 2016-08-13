package com.example.blueweather.fragment;

import com.example.blueweather.R;
import com.example.blueweather.activity.SettingActivity;
import com.example.blueweather.activity.WeatherActivity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NetErrorFragment extends Fragment implements OnClickListener {

	private static final String TAG = "NetErrorFragment";
	private ImageView netErrorImage;
	private TextView netErrorText;
	private Button netErrorBtn;
	private WeatherActivity weatherActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.neterror_fragment, container, false);
		weatherActivity = (WeatherActivity) getActivity();
		netErrorImage = (ImageView) view.findViewById(R.id.neterror_image);
		netErrorText = (TextView) view.findViewById(R.id.neterror_text);
		netErrorBtn = (Button) view.findViewById(R.id.neterror_btn);
		netErrorBtn.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		case R.id.neterror_btn:
			weatherActivity.onUpdateClicked();
			break;
		default:
			break;
		}
	}
}
