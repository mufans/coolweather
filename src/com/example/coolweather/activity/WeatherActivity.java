package com.example.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.R;
import com.example.coolweather.util.HttpCallBackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

public class WeatherActivity extends Activity implements OnClickListener {
	TextView publishTv;
	TextView areaName;
	TextView current_date;
	TextView weather_desc;
	TextView temp1;
	TextView temp2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_weather);
		publishTv = (TextView) findViewById(R.id.tv_publishtxt);
		areaName = (TextView) findViewById(R.id.tv_cityname);
		current_date = (TextView) findViewById(R.id.current_date);
		weather_desc = (TextView) findViewById(R.id.weather_desc);
		temp1 = (TextView) findViewById(R.id.temp1);
		temp2 = (TextView) findViewById(R.id.temp2);
		String countyCode = getIntent().getStringExtra("countyCode");
		if(!TextUtils.isEmpty(countyCode)){
			publishTv.setText("同步中。。。");
			qureryWeatherCode(countyCode);
		}else{
			showWeatherInfo();
		}
		Button refresh = (Button) findViewById(R.id.btn_refresh);
		Button choose = (Button) findViewById(R.id.btn_choosearea);
		refresh.setOnClickListener(this);
		choose.setOnClickListener(this);
	}
	private void qureryWeatherCode(String countyCode){
		String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFromServer(address,"weatherCode");
	}
	private void queryFromServer(String address, final String type) {
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if("weatherCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						String[] arr = response.split("\\|");
						if(arr != null && arr.length>1){
							String weatherCode = arr[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherInfo".equals(type)){
					if(!TextUtils.isEmpty(response)){
						Utility.handleWeatherResponse(WeatherActivity.this, response);
						runOnUiThread(new Runnable() {
							public void run() {
								showWeatherInfo();
							}
						});
					}
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new  Runnable() {
					public void run() {
						Toast.makeText(getApplicationContext(), "error", 1000).show();
					}
				});
			}
		});
	}
	private void queryWeatherInfo(String weatherCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFromServer(address, "weatherInfo");
	}
	
	private void showWeatherInfo(){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		publishTv.setText(sp.getString("publishTime", ""));
		areaName.setText(sp.getString("cityName", ""));
		current_date.setText(sp.getString("currenttime"	, ""));
		weather_desc.setText(sp.getString("weatherDesc", ""));
		temp1.setText(sp.getString("temp1", ""));
		temp2.setText(sp.getString("temp2", ""));
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_choosearea:
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			SharedPreferences sp1 = PreferenceManager.getDefaultSharedPreferences(this);
			sp1.edit().putBoolean("city_selected", false).commit();
			intent.putExtra("fromWeather", true);
			startActivity(intent);
			finish();
			break;
		case R.id.btn_refresh:
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = sp.getString("weatherCode", "");
			if(!TextUtils.isEmpty(weatherCode)){
				publishTv.setText("同步中。。。");
				queryWeatherInfo(weatherCode);
			}
			break;
		}
	}
	

}
