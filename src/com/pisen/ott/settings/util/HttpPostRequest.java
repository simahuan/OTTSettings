package com.pisen.ott.settings.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
/**
 * http post提交
 * 
 * @author MouJunFeng
 * @version 1.0, 2014-6-16 上午9:20:14
 */
public class HttpPostRequest extends HttpManager{

	public HttpPostRequest(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}
	/**
	 *  http请求
	 * @param url 地址
	 * @param result OnHttpCallBack
	 */
	public void execute(String url,OnHttpCallBack result) {
		execute(url, null, result);	
	}
	/**
	 *  http请求
	 * @param url 地址
	 * @param map map
	 * @param result OnHttpCallBack
	 */
	public void execute(String url, Map<String, String> map, OnHttpCallBack result) {		
		execute(new String[]{"温馨提示","数据加载中，请稍候！"}, url, map, result);	
	}
	/**
	 *  http请求
	 * @param dialogs 提示框
	 * @param url 地址
	 * @param map map
	 * @param result OnHttpCallBack
	 */
	public void execute(String[] dialogs ,String url, Map<String, String> map, OnHttpCallBack result) {
		this.setResult(result);
		showDialog(dialogs[0] , dialogs[1]);
		new MyAsyncTask(map).execute(url);
	}
	/**
	 * 
	 * Http 异步加载
	 * @author MouJunFeng
	 * @version 1.0 2014-5-26 上午10:17:13
	 */
	private class MyAsyncTask extends AsyncTask<String, Integer, String> {
		private Map<String, String> map;

		public MyAsyncTask(Map<String, String> map) {
			this.map = map;
		}

		@Override
		protected String doInBackground(String... params) {	
			String result = "";
			try {
				HttpPost httpPost = new HttpPost(params[0]);
				List <BasicNameValuePair> param = null;
				if(map != null && !map.isEmpty()){	
					param = new ArrayList <BasicNameValuePair>();
					for(Entry<String, String> entry : this.map.entrySet()){
						String key = entry.getKey();
						String value = entry.getValue();
						param.add(new BasicNameValuePair(key, value));
					}
					httpPost.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
				}
				// 实现将请求 的参数封装封装到HttpEntity中。
//				enti entity = new StringEntity(param, "UTF-8");
				// 使用HttpPost请求方式
				
//				httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
				// 设置请求参数到Form中。
				
				// 实例化一个默认的Http客户端
				HttpClient client = new DefaultHttpClient();
				// 设置连接超时时间为3s
				HttpConnectionParams.setConnectionTimeout(client.getParams(),
						CONNECT_TIME_OUT);
				// 设置读取超时为5s
				HttpConnectionParams.setSoTimeout(client.getParams(),
						READ_TIME_OUT);
				// 执行请求，并获得响应数据
				HttpResponse httpResponse = client.execute(httpPost);
				// 判断是否请求成功，为200时表示成功，其他均问有问题。
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					// 通过HttpEntity获得返回数据
					result = EntityUtils.toString(httpResponse
							.getEntity());
					return result;
				}
				return result;
			}catch(Exception e){
				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			closeDialog();
			OnHttpCallBack callback = HttpPostRequest.this.getResult();
			if (callback != null) {
				callback.getHttpResult(result);
			}
		}
		
	}

}
