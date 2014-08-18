package com.example.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;

import com.example.coolweather.service.UpdateWeatherService;

public class AutoUpateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent in = new Intent(context,UpdateWeatherService.class);
		context.startService(in);
	}

}
