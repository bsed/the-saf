/**
 * 
 */
package cn.salesuite.saf.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import android.util.Log;

import com.alibaba.fastjson.JSON;

/**
 * @author Tony Shen
 *
 * 
 */
public class HttpJsonPost extends HttpJsonClient{
	
	private static final String TAG = "HttpJsonPost";
	
    public HttpJsonPost() {
        super();
    }
	
	/**
	 * 创建http请求
	 * @param url
	 * @param customizedHeader
	 * @param entity  a raw {@link HttpEntity} to send with the request, for example, use this to send json payloads to a server by passing a {@link org.apache.http.entity.StringEntity}.
	 * @param callback
	 * @throws IOException
	 */
	public void makeHTTPRequest(String url,Map<String, String> customizedHeader,HttpEntity entity,
			OnResponseReceivedListener callback)
			throws  IOException {
		Log.d("URL",url);
		 
		if(customizedHeader==null){
			customizedHeader = new HashMap<String, String>();
		}
		customizedHeader.put("Accept-Encoding", "gzip");
		customizedHeader.put("Connection" , "Keep-Alive");
		customizedHeader.put("Content-Type" , "application/json;charset=utf-8");
		customizedHeader.put("Accept" , "application/json");
		
		HttpPost httpRequest = createHttpPost(url, customizedHeader);
		if (entity!=null)
			httpRequest.setEntity(entity);
		HttpResponse httpResponse = executeHttpRequest(httpRequest);
		HttpEntity httpEntity = httpResponse.getEntity();
		
		//success
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
			//String response = EntityUtils.toString(httpEntity);
			InputStream instream = httpEntity.getContent();
			if (contentEncoding!=null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				instream = new GZIPInputStream(instream);
				executeResponseCallback(callback, instream);
			} else {
				executeResponseCallback(callback, instream);
			}
		} else {
			Log.e("error", "Request failure! url:"+url);
			
			if(httpEntity != null)
				httpEntity.consumeContent();
		}

		httpClient.getConnectionManager().shutdown();
	}
	
	public HttpPost createHttpPost(String url,Map<String, String> customizedHeader) {
		HttpPost httpRequest = new HttpPost(url);

		if(customizedHeader != null) {
			for (Map.Entry<String, String> item : customizedHeader.entrySet()) {
				Header header = new BasicHeader(item.getKey(), item.getValue());
				httpRequest.addHeader(header);
				Log.i(TAG, item.getKey()+"="+item.getValue());
			}
		}

		return httpRequest;
	}
	
    public StringEntity objToEntity(Object jsonObj) throws IOException {
    	String json = JSON.toJSONString(jsonObj);
        return stringToEntity(json);
    }
    
    public StringEntity stringToEntity(String json) throws IOException {
    	return new StringEntity(json, "utf-8");
    }

	private void executeResponseCallback(
			final OnResponseReceivedListener callback, final InputStream response) {
			callback.onResponseReceived(response);
	}
}
