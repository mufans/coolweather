package com.example.coolweather.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.coolweather.model.City;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;

public class CoolWeatherDB {
	public static final String DB_NAME = "cool_weather";
	public static final int VERSION = 1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbhelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbhelper.getWritableDatabase();
	}

	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues cv = new ContentValues();
			cv.put("province_name", province.getProvinceName());
			cv.put("province_code", province.getProvinceCode());
			db.insert("Province", null, cv);
		}
	}

	public List<Province> getProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String provinceName = cursor.getString(cursor
						.getColumnIndex("province_name"));
				String provinceCode = cursor.getString(cursor
						.getColumnIndex("province_code"));
				province.setId(id);
				province.setProvinceCode(provinceCode);
				province.setProvinceName(provinceName);
				list.add(province);
			} while (cursor.moveToNext());
		}
		if (cursor != null)
			cursor.close();
		return list;
	}

	public void saveCity(City city) {
		if (city != null) {
			ContentValues cv = new ContentValues();
			cv.put("city_name", city.getCityName());
			cv.put("city_code", city.getCityCode());
			cv.put("province_id", city.getProvinceId());
			db.insert("City", null, cv);
		}
	}

	public List<City> getCities() {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String cityName = cursor.getString(cursor
						.getColumnIndex("city_name"));
				String cityCode = cursor.getString(cursor
						.getColumnIndex("city_code"));
				int provinceId = cursor.getInt(cursor
						.getColumnIndex("province_id"));
				city.setCityCode(cityCode);
				city.setCityName(cityName);
				city.setId(id);
				city.setProvinceId(provinceId);
				list.add(city);

			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	public void saveCounty(County county) {
		ContentValues cv = new ContentValues();
		cv.put("county_name", county.getCountyName());
		cv.put("county_code", county.getCountyCode());
		cv.put("city_id", county.getCityId());
		db.insert("County", null, cv);
	}

	public List<County> getCounties() {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String countyName = cursor.getString(cursor
						.getColumnIndex("county_name"));
				String countyCode = cursor.getString(cursor
						.getColumnIndex("county_code"));
				int cityId = cursor.getInt(cursor.getColumnIndex("city_id"));
				county.setId(id);
				county.setCountyCode(countyCode);
				county.setCountyName(countyName);
				county.setCityId(cityId);
				list.add(county);
			} while (cursor.moveToNext());

		}
		cursor.close();
		return list;
	}
}
