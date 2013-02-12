package cn.salesuite.saf.weibo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;


public class OAuthHttpClient {
	private static final int CONNECTION_TIMEOUT = 20000;

	public OAuthHttpClient() {
	}
	public static String exec(String url) throws Exception {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
		Log.i(">>", url);
		HttpGet request = new HttpGet(url);
		request.setHeader("Content-Encoding", "UTF-8");
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpResponse response = null;
		response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			Log.i(">>", "SC_OK");
			return EntityUtils.toString(response.getEntity(), "UTF-8");
		} else {
			Log.i(">>", "error:" + EntityUtils.toString(response.getEntity()));
		}
		return null;
	}
	public String exeGet(String url, String queryString) throws Exception {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
		HttpGet request = new HttpGet(url + "?" + queryString);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpResponse response = null;
		response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			Log.i(">>", "SC_OK");
			return EntityUtils.toString(response.getEntity(), "UTF-8");
		} else {
			Log.i(">>", "error:" + EntityUtils.toString(response.getEntity()));
		}
		return null;
	}
	public static String exePost(String url, String queryString) throws Exception {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
		HttpPost request = new HttpPost(url);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		StringEntity s = new StringEntity(queryString);
		s.setContentEncoding("UTF-8");
		s.setContentType("application/x-www-form-urlencoded");
		request.setEntity(s);
		HttpResponse response = null;
		response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			Log.i(">>", "SC_OK:");
			return EntityUtils.toString(response.getEntity(), "UTF-8");
		} else {
			Log.i(">>", "error:" + EntityUtils.toString(response.getEntity()));
		}
		return null;
	}

	public String exePost(String url, String headName, String headValue)
			throws Exception {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
		HttpPost request = new HttpPost(url);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		request.getParams().setBooleanParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, true);
		request.addHeader(headName, headValue);
		request.setHeader("Content-Encoding", "UTF-8");
		HttpResponse response = null;
		response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			Log.i(">>", "SC_OK");
			return EntityUtils.toString(response.getEntity(), "UTF-8");
		} else {
			Log.i(">>", "error:" + EntityUtils.toString(response.getEntity()));
		}
		return null;
	}

	public String exePost(String url, HashMap<String, String> params)
			throws Exception {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
		HttpPost request = new HttpPost(url);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

		List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		for (Iterator<String> i = params.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			pairs.add(new BasicNameValuePair(key, params.get(key)));
		}
		request.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
		request.getParams().setBooleanParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

		HttpResponse response = null;
		response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			Log.i(">>", "SC_OK");
			return EntityUtils.toString(response.getEntity(), "UTF-8");
		} else {
			Log.i(">>", "error:" + EntityUtils.toString(response.getEntity()));
		}
		return null;
	}

	public String exePost(String url, String headName, String headValue,
			HashMap<String, String> params) throws Exception {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

		HttpPost request = new HttpPost(url);
		List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();

		for (Iterator<String> i = params.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			pairs.add(new BasicNameValuePair(key, params.get(key)));
		}
		request.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
		request.getParams().setBooleanParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
		request.addHeader(headName, headValue);

		HttpResponse response = null;
		response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			Log.i(">>", "SC_OK");
			return EntityUtils.toString(response.getEntity(), "UTF-8");
		} else {
			Log.i(">>", "error:" + EntityUtils.toString(response.getEntity()));
		}
		return null;
	}

	public String exeGet(String url, String headName, String headValue)
			throws Exception {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, CONNECTION_TIMEOUT);
		HttpGet request = new HttpGet(url);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		request.addHeader(headName, headValue);
		HttpResponse response = null;
		response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			Log.i(">>", "SC_OK");
			return EntityUtils.toString(response.getEntity(), "UTF-8");
		} else {
			Log.i(">>", "error:" + EntityUtils.toString(response.getEntity()));
		}
		return null;
	}
	public String exePost(String urlStr, HashMap<String, String> params,
			String pic) throws Exception {
		String BOUNDARY = "---------------------------7d4a6d158c"; // 数据分隔线
		String MULTIPART_FORM_DATA = "multipart/form-data";

		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false);// 不使用Cache
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Charset", "UTF-8");
		StringBuilder sb = new StringBuilder();
		// 上传的参数参数部分
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append("--");
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data; name=\""+entry.getKey()+"\"\r\n\r\n");
			sb.append(entry.getValue());
			sb.append("\r\n");
		}
		//Utils.Log(">>", sb.toString());
		// 上传文件部分
		sb.append("--"); 
		sb.append(BOUNDARY); 
		sb.append("\r\n"); 
		sb.append("Content-Disposition: form-data; name=\"pic\"; filename=\"head.jpg\"\r\n"); 
		sb.append("Content-Type: application/octet-stream\r\n\r\n"); 
		byte[] data = sb.toString().getBytes(); 
		byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes(); 
		// HTTP header: 
		conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY); 
		byte[] content = readFileImage(pic);
		conn.setRequestProperty("Content-Length", String.valueOf(data.length + content.length + end_data.length)); 
		// 输出: 
		OutputStream output = conn.getOutputStream(); 
		output.write(data); 
		output.write(content); 
		output.write(end_data); 
		output.flush();	
		output.close();
		int cah = conn.getResponseCode();
		String result="";
		if (cah == 200) {
			InputStream is = conn.getInputStream();
			int ch;
			StringBuilder b = new StringBuilder();
			while( (ch = is.read()) != -1 ){
				b.append((char)ch);
			}
			is.close();
			result=b.toString();
		} else if (cah == 400) {
			result=conn.getResponseMessage();
			Log.i(">>", "400:"+conn.getResponseMessage()+",url="+urlStr);
		} else {
			result="404 error!";
			Log.i(">>", "404:"+",url="+urlStr);
		}
		conn.disconnect();
		return result;
	}

	public byte[] readFileImage(String filename) throws IOException {
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(filename));
		int len = bufferedInputStream.available();
		byte[] bytes = new byte[len];
		int r = bufferedInputStream.read(bytes);
		if (len != r) {
			bytes = null;
			throw new IOException("read file error!");
		}
		bufferedInputStream.close();
		return bytes;
	}
}
