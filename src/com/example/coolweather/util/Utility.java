package com.example.coolweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.coolweather.db.CoolWeatherDB;
import com.example.coolweather.model.City;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;

public class Utility {
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB db, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] provinces = response.split(",");
			if (provinces != null && provinces.length > 0) {
				for (String provinceStr : provinces) {
					String[] arr = provinceStr.split("\\|");
					String code = arr[0];
					String provinceName = arr[1];
					Province province = new Province();
					province.setProvinceCode(code);
					province.setProvinceName(provinceName);
					db.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	public synchronized static boolean handlCitiesResponse(CoolWeatherDB db,
			String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] citiesStr = response.split(",");
			if (citiesStr != null && citiesStr.length > 0) {
				for (int i = 0; i < citiesStr.length; i++) {
					String cityStr = citiesStr[i];
					String[] arr = cityStr.split("\\|");
					City city = new City();
					city.setCityCode(arr[0]);
					city.setCityName(arr[1]);
					city.setProvinceId(provinceId);
					db.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	public synchronized static boolean handleCountiesResponse(CoolWeatherDB db,
			String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] countiesStr = response.split(",");
			if (null != countiesStr && countiesStr.length > 0) {
				for (String countyStr : countiesStr) {
					String[] arr = countyStr.split("\\|");
					County county = new County();
					county.setCountyCode(arr[0]);
					county.setCountyName(arr[1]);
					county.setCityId(cityId);
					db.saveCounty(county);
				}
				return true;
			}
		}

		return false;
	}

	public static void handleWeatherResponse(Context context, String response) {
		try {
			JSONObject jsonObj = new JSONObject(response);
			JSONObject weatherInfo = jsonObj.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesc = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesc,publishTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveWeatherInfo(Context context, String cityname,
			String weatherCode, String temp1, String temp2, String weatherDesc ,String publishTime) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyƒÍM‘¬d»’", Locale.CHINA);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.putBoolean("city_selected", true);
		editor.putString("cityName", cityname);
		editor.putString("weatherCode", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weatherDesc", weatherDesc);
		editor.putString("publishTime", publishTime);
		editor.putString("currenttime", format.format(new Date()));
		editor.commit();
	}
}
