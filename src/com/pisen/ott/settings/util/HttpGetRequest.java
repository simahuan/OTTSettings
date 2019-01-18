package com.pisen.ott.settings.util;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;

/**
 * http get 方式请求
 * 
 * @author MouJunFeng
 * @version 1.0, 2014-6-3 上午9:54:33
 */
public class HttpGetRequest extends HttpManager {

	public HttpGetRequest(Context ctx) {
		super(ctx);
	}

	/**
	 * 
	 * Http get请求
	 * 
	 * @param url
	 *            地址
	 * @param map
	 *            参数
	 * @param result
	 *            返回的接口
	 */
	public void execute(String url, Map<String, String> map, OnHttpCallBack result) {
		String param = "";
		if (map != null && (!map.isEmpty())) {
			for (Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				param = key + "=" + value + "&";
			}
			param = param.substring(0, param.length() - 1);
		}
		execute(url, param, result);

	}

	public void execute(String url, String params, OnHttpCallBack result) {
		execute(new String[] { "温馨提示", "数据加载中，请稍候!" }, url, params, result);

	}

	/**
	 * 
	 * Http post请求
	 * 
	 * @param dialogs
	 *            提示框信息
	 * @param url
	 *            地址
	 * @param params
	 *            参数
	 * @param result
	 *            void
	 */
	public void execute(String[] dialogs, String url, String params, OnHttpCallBack result) {
		this.setResult(result);
		//showDialog(dialogs[0], dialogs[1]);
		//new MyAsyncTask(params).execute(url);
	}

	/**
	 * 
	 * Http 异步加载
	 * 
	 * @author MouJunFeng
	 * @version 1.0 2014-5-26 上午10:17:13
	 */
	private class MyAsyncTask extends AsyncTask<String, Integer, String> {
		private String param;

		public MyAsyncTask(String param) {
			this.param = param;
		}

		@Override
		protected String doInBackground(String... params) {
			String result = "";
			try {
				String url = params[0] + param;
				if (!url.contains("?")) {
					url = params[0] + "?" + param;
				}
				HttpGet httpGet = new HttpGet(url);
				httpGet.setHeader("Content-Type", "application/json; charset=utf-8");
				// 实例化一个默认的Http客户端
				HttpClient client = new DefaultHttpClient();
				// 设置连接超时时间为3s
				HttpConnectionParams.setConnectionTimeout(client.getParams(), CONNECT_TIME_OUT);
				// 设置读取超时为5s
				HttpConnectionParams.setSoTimeout(client.getParams(), READ_TIME_OUT);
				// 执行请求，并获得响应数据
				HttpResponse httpResponse = client.execute(httpGet);
				// 判断是否请求成功，为200时表示成功，其他均问有问题。
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					// 通过HttpEntity获得返回数据
					result = EntityUtils.toString(httpResponse.getEntity());
					return result;
				}
				return result;
			} catch (Exception e) {
				return result;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			closeDialog();
			OnHttpCallBack callback = HttpGetRequest.this.getResult();
			if (callback != null) {
				callback.getHttpResult(result);
			}
		}
	}

}
