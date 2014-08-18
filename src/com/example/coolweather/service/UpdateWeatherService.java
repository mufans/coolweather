package com.example.coolweather.service;

import com.example.coolweather.receiver.AutoUpateReceiver;
import com.example.coolweather.util.HttpCallBackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class UpdateWeatherService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				updateWeather();
			};
		}.start();
		AlarmManager am = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		long time = SystemClock.elapsedRealtime() + 8 * 60 * 60 * 1000;
		Intent in = new Intent(this, AutoUpateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, in, 0);
		am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, pi);
		return super.onStartCommand(intent, flags, startId);
	}

	public void updateWeather() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		String weatherCode = sp.getString("weatherCode", "");
		String addr = "http://www.weather.com.cn/data/cityinfo/" + weatherCode
				+ ".html";
		HttpUtil.sendHttpRequest(addr, new HttpCallBackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				Utility.handleWeatherResponse(getApplicationContext(), response);
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				e.printStackTrace();
			}
		});
	}
}
