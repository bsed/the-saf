/**
 * 
 */
package cn.salesuite.saf.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

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
	
	private void executeResponseCallback(
			final OnResponseReceivedListener callback, final InputStream response) {
			callback.onResponseReceived(response);
	}

	public interface OnResponseReceivedListener{
		public void onResponseReceived(InputStream response);
	}
}
