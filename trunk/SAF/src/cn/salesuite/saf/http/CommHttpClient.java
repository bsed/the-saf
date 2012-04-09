/**
 * 
 */
package cn.salesuite.saf.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import cn.salesuite.saf.utils.StringHelper;

import android.text.TextUtils;
import android.util.Log;

/**
 * @author Tony Shen
 *
 */
public class CommHttpClient {

	private static final String TAG = "CommHttpClient";
	public static final int ONE_MINUTE = 60000;
	private DefaultHttpClient httpClient;
	
	public CommHttpClient() {
		httpClient = createHttpClient();
	}
	
	/**
	 * 当httpsFlag为true时,创建httpsCilent<br>
	 * 当httpsFlag为false时,创建httpCilent
	 * @param useHttpsFlag
	 */
	public CommHttpClient(boolean httpsFlag) {
		if (httpsFlag) {
			httpClient = createHttpsClient();
		} else {
			httpClient = createHttpClient();
		}
	}

	private static DefaultHttpClient createHttpClient() {
		return new DefaultHttpClient(createHttpParams());
	}
	
	private static HttpParams createHttpParams() {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
		HttpConnectionParams.setConnectionTimeout(httpParams, ONE_MINUTE);
		HttpConnectionParams.setSoTimeout(httpParams, ONE_MINUTE);
		HttpConnectionParams.setSocketBufferSize(httpParams, 1024*2);
		return httpParams;
	}
	
	/**
	 * 创建基于https能访问的DefaultHttpClient
	 * @return
	 */
	private DefaultHttpClient createHttpsClient() {
		try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
	
	/**
	 * 
	 * @param url
	 * @param postdata
	 * @param customizedHeader
	 * @param callback
	 * @throws IOException
	 */
	public void makeHTTPRequest(String url,
			Map<String, String> postdata, Map<String, String> customizedHeader,
			OnResponseReceivedListener callback)
			throws  IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(url).append("?");
		for (Map.Entry<String, String> item : postdata.entrySet()) {
			sb.append("").append(item.getKey()).append("=").append(
					item.getValue()).append("&");
		}
		Log.d("URL", sb.toString());

		HttpPost httpRequest = createHttpPost(url, postdata, customizedHeader);
		HttpResponse httpResponse = executeHttpRequest(httpRequest);
		HttpEntity httpEntity = httpResponse.getEntity();
		//success
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			//String response = EntityUtils.toString(httpEntity);
			executeResponseCallback(callback, httpEntity.getContent());
		} else {
			Log.e("error", "Request failure! url:"+url);
			
			if(httpEntity != null)
				httpEntity.consumeContent();
		}

		httpClient.getConnectionManager().shutdown();
	}
	
	public HttpPost createHttpPost(String url, Map<String, String> postdata, Map<String, String> customizedHeader) {
		HttpPost httpRequest = new HttpPost(url);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		if(postdata != null) {
			for (Map.Entry<String, String> item : postdata.entrySet()) {
				params.add(new BasicNameValuePair(item.getKey(), item.getValue()));
				Log.d(TAG, item.getKey()+"="+item.getValue());
			}
		}
		if(customizedHeader != null) {
			for (Map.Entry<String, String> item : customizedHeader.entrySet()) {
				Header header = new BasicHeader(item.getKey(), item.getValue());
				httpRequest.addHeader(header);
				Log.i(TAG, item.getKey()+"="+item.getValue());
			}
		}
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unable to encode http parameters.");
		}
		return httpRequest;
	}
	
	public HttpResponse executeHttpRequest(HttpRequestBase httpRequest) throws IOException {
		Log.d(TAG, "executing HttpRequest for:" + httpRequest.getURI().toString());
		
		try {
			//httpClient.getConnectionManager().closeExpiredConnections();
			return httpClient.execute(httpRequest);
		} catch (IOException e) {
			httpRequest.abort();
			throw e;
		}
	}
	
	/**
	 * 把所有的非空传入参数，通过a-z排序后，
	 * 使用key=value&key=value连接
	 * @param params
	 * @return
	 */
	public static String sortParams(Map<String,String> params) {
		StringBuilder sb = new StringBuilder();
		String str = null;
		
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> mRemoveList = new ArrayList<String>();

		for (Map.Entry<String, String> item : params.entrySet()) {
			if(item.getValue()==null){
				mRemoveList.add(item.getKey());
			}
			list.add(item.getKey());
		}
		
		int removeCount = mRemoveList.size();
		for(int i=0;i<removeCount;i++){
			params.remove(mRemoveList.get(i));
			list.remove(mRemoveList.get(i));
		} 
		Collections.sort(list);
		
		for(int i=0;i<list.size();i++)
		{
			String key = list.get(i);
			String value = "";
			if(!TextUtils.isEmpty(key)){
				value = params.get(key); 
				sb.append(key).append("=").append(value).append("&"); 
			} 
		}
		str = sb.toString();
		if(!TextUtils.isEmpty(str)){
			str = str.substring(0, str.length()-1);
		}

		return str;
	}
	
	/**
	 * 并对字符串进行md5加密
	 * @param needMD5String
	 * @return
	 */
	public static String getSign(String needMD5String) {
		try {
			needMD5String = URLEncoder.encode(needMD5String, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return StringHelper.md5(needMD5String);
	}
	
	private void executeResponseCallback(
			final OnResponseReceivedListener callback, final InputStream response) {
			callback.onResponseReceived(response);
	}

	public interface OnResponseReceivedListener{
		public void onResponseReceived(InputStream response);
	}
}
