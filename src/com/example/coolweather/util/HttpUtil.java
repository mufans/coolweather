package com.example.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	public static void sendHttpRequest(final String urlStr,
			final HttpCallBackListener callBack) {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				URL url = null;
				HttpURLConnection httpUrlConnection = null;
				try {
					url = new URL(urlStr);
					httpUrlConnection = (HttpURLConnection) url
							.openConnection();
					httpUrlConnection.setRequestMethod("GET");
					httpUrlConnection.setReadTimeout(8000);
					httpUrlConnection.setConnectTimeout(8000);
					InputStream is = httpUrlConnection.getInputStream();
					BufferedReader br = new BufferedReader(
							new InputStreamReader(is));
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					if (callBack != null) {
						callBack.onFinish(sb.toString());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (callBack != null) {
						callBack.onError(e);
					}
				} finally {
					if (httpUrlConnection != null) {
						httpUrlConnection.disconnect();
					}
				}
			}
		}.start();

	}
}
