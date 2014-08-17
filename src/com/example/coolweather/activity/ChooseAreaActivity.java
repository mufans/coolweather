package com.example.coolweather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.R;
import com.example.coolweather.db.CoolWeatherDB;
import com.example.coolweather.model.City;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;
import com.example.coolweather.util.HttpCallBackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

public class ChooseAreaActivity extends Activity {
	private final static int LEVEL_PROVINCE = 0;
	private final static int LEVEL_CITY = 1;
	private final static int LEVEL_COUNTY = 2;
	private int currentLevel;
	private List<String> datalist = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	private Province selectedProvince;
	private City selectedCity;
	private CoolWeatherDB db;
	TextView titleTxt;
	ListView listView;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choosearea);
		titleTxt = (TextView) findViewById(R.id.title_txt);
		listView = (ListView) findViewById(R.id.list);
		db = CoolWeatherDB.getInstance(this);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, datalist);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					queryCity();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					queryCounty();
				}
			}

		});
		queryProvince();
	}

	private void queryProvince() {
		provinceList = db.getProvinces();
		if (provinceList.size() > 0) {
			datalist.clear();
			for (Province privince : provinceList) {
				datalist.add(privince.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleTxt.setText("ÖÐ¹ú");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer("province", null);
		}
	}

	private void queryFromServer(final String type, final String code) {
		String url = null;
		if (!TextUtils.isEmpty(code)) {
			url = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		} else {
			url = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(url, new HttpCallBackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(db, response);
				} else if ("city".equals(type)) {
					result = Utility.handlCitiesResponse(db, response,
							selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(db, response,
							selectedCity.getId());
				}
				if (result) {
					runOnUiThread(new Runnable() {
						public void run() {
							dismissDialog();
							if ("province".equals(type)) {
								queryProvince();
							} else if ("city".equals(type)) {
								queryCity();
							} else if ("county".equals(type)) {
								queryCounty();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						dismissDialog();
						Toast.makeText(ChooseAreaActivity.this, "error",
								Toast.LENGTH_SHORT).show();
					}
				});

			}
		});
	}

	private void queryCity() {
		cityList = db.getCities();
		if (cityList.size() > 0) {
			datalist.clear();
			for (City city : cityList) {
				datalist.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			titleTxt.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
			listView.setSelection(0);
		} else {
			queryFromServer("city", selectedProvince.getProvinceCode());
		}
	}

	private void queryCounty() {
		countyList = db.getCounties();
		if (countyList.size() > 0) {
			datalist.clear();
			for (County county : countyList) {
				datalist.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			currentLevel = LEVEL_COUNTY;
			titleTxt.setText(selectedCity.getCityName());
		} else {
			queryFromServer("county", selectedCity.getCityCode());
		}
	}

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("loading...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	private void dismissDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCity();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvince();
		} else {
			super.onBackPressed();
		}

	}
}
