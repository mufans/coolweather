package com.example.coolweather.util;

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
}
