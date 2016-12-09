/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maomao.framework.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ProxySelector;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.maomao.framework.exception.NotifyException;

/**
 * http工具类
 * 
 * @author maomao
 * 
 */
public class HttpUtils {
	/**
	 * 下载一个文件
	 * 
	 * @param url
	 */
	public static File download(String url, String path) throws NotifyException {
		// 生成一个httpclient对象
		CloseableHttpClient httpclient = HttpClientBuilder.create().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).build();
		HttpGet httpget = new HttpGet(url);
		File file = new File(path);
		FileOutputStream fout = null;
		InputStream in = null;
		try {
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			in = entity.getContent();
			fout = new FileOutputStream(file);
			int l = -1;
			byte[] tmp = new byte[1024];
			while ((l = in.read(tmp)) != -1) {
				fout.write(tmp, 0, l);
			}
			return file;
		} catch (Exception e) {
			throw new NotifyException("文件下载失败");
		} finally {
			// 关闭低层流。
			try {
				fout.flush();
				fout.close();
				in.close();
				httpclient.close();
			} catch (Exception e) {
			}
		}
	}

	public static JSONObject postFile4Json(String url, File file) throws Exception {
		HttpClient httpclient = HttpClientBuilder.create().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).build();
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);
			HttpEntity entity = new FileEntity(file);
			httpPost.setHeader("content-Type", "multipart/form-data");
			httpPost.setHeader("filename", URLEncoder.encode(file.getName(), "utf-8"));
			httpPost.setHeader("Content-Length", String.valueOf(entity.getContentLength()));
			httpPost.setHeader("filelength", String.valueOf(file.length()));
			httpPost.setEntity(entity); // 设置实体对象

			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			String returnVal = EntityUtils.toString(responseEntity);
			return JsonUtils.String2JSONObject(returnVal);
		} finally {
			// System.out.println("ok!");
			if (httpPost != null)
				httpPost.releaseConnection();
		}
	}

	public static String postJson(String url, JSON jo) throws Exception {
		String content = jo.toString();
		HttpClient httpclient = HttpClientBuilder.create().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).build();
		HttpPost httpPost = null;
		try {

			httpPost = new HttpPost(url);
			StringEntity stringEntity = new StringEntity(content, "utf-8");
			httpPost.addHeader("Content-Type", "text/xml");
			httpPost.setEntity(stringEntity);

			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String returnVal = EntityUtils.toString(entity);
			return returnVal;
		} finally {
			// System.out.println("ok!");
			if (httpPost != null)
				httpPost.releaseConnection();
		}
	}

	/**
	 * 向一个URL提交字符串内容
	 * 
	 * @param url
	 * @param content
	 */
	public static String postString(String url, String content) throws Exception {
		HttpClient httpclient = HttpClientBuilder.create().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).build();
		HttpPost httpPost = null;
		try {

			httpPost = new HttpPost(url);
			StringEntity stringEntity = new StringEntity(content, "utf-8");
			httpPost.addHeader("Content-Type", "text/xml");
			httpPost.setEntity(stringEntity);

			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String returnVal = EntityUtils.toString(entity);
			return returnVal;
		} finally {
			// System.out.println("ok!");
			if (httpPost != null)
				httpPost.releaseConnection();
		}
	}

	/**
	 * post请求，返回JSONObject
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static JSONObject post4JsonObject(String url, Map<String, String> params) throws Exception {
		String returnVal = post4String(url, params);
		JSONObject jo = (JSONObject) JsonUtils.String2JSONObject(returnVal);
		return jo;
	}

	/**
	 * Post请求，返回JSONArray
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static JSONArray post4JsonArray(String url, Map<String, String> params) throws Exception {
		String returnVal = post4String(url, params);
		JSONArray jo = (JSONArray) JSONArray.toJSON(returnVal);
		return jo;
	}

	/**
	 * POST请求，返回String
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String post4String(String url, Map<String, String> params) throws Exception {
		HttpClient httpclient = HttpClientBuilder.create().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).build();
		HttpPost httpPost = null;
		try {

			httpPost = new HttpPost(url);
			if (params != null && params.size() > 0) {
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				String key, value;
				for (Iterator<String> keys = params.keySet().iterator(); keys.hasNext();) {
					key = keys.next();
					value = params.get(key);
					nvps.add(new BasicNameValuePair(key, value));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
			}

			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			String returnVal = EntityUtils.toString(entity);
			return returnVal;
		} finally {
			// System.out.println("ok!");
			if (httpPost != null)
				httpPost.releaseConnection();
		}
	}

	/**
	 * GET请求，返回String
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static JSONObject get4JsonObject(String url, Map<String, String> params) throws Exception {
		String returnVal = get4String(url, params);
		JSONObject jo = (JSONObject) JSONObject.parse(returnVal);
		return jo;
	}

	/**
	 * Post请求，返回JSONArray
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static JSONArray get4JsonArray(String url, Map<String, String> params) throws Exception {
		String returnVal = get4String(url, params);
		JSONArray jo = (JSONArray) JSONArray.toJSON(returnVal);
		return jo;
	}

	/**
	 * GET请求，返回String
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static String get4String(String url, Map<String, String> params) throws Exception {
		HttpClient httpclient = HttpClientBuilder.create().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).build();
		HttpGet httpGet = null;
		try {

			httpGet = new HttpGet(url);
			if (params != null && params.size() > 0) {
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				String key, value;
				for (Iterator<String> keys = params.keySet().iterator(); keys.hasNext();) {
					key = keys.next();
					value = params.get(key);
					nvps.add(new BasicNameValuePair(key, value));
				}
			}

			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String returnVal = EntityUtils.toString(entity);
			return returnVal;
		} finally {
			if (httpGet != null)
				httpGet.releaseConnection();
		}
	}

	public static String fillUrlParams(String url, String[] values) {
		int i = 0;
		String s = url;
		int idx = 0;
		while ((idx = s.indexOf("[?]")) != -1) {
			s = s.substring(0, idx) + values[i] + s.substring(idx + 3);
			i++;
		}
		return s;
	}
}
