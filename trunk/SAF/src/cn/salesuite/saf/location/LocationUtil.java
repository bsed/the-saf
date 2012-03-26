/**
 * 
 */
package cn.salesuite.saf.location;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

import com.decarta.shifting.ShiftTool;

import cn.salesuite.saf.exception.APIException;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * 定位框架的帮助类，抽象出来是为了方便移植代码
 * @author Tony Shen
 *
 */
public class LocationUtil {
	
	public static final int MIN_LOCATION_UPDATE_DISTANCE = 0;
	public static final int MIN_LOCATION_UPDATE_TIME = 0;
	
	public static final long HALF_HOUR = 1800000;
	public static final long ONE_HOUR = 3600000;
	public static final long FIVE_MINUTES = 300000;
	public static final long ONE_MINUTE = 60000;
	public static final long TWO_MINUTES = 120000;
	public static final int POSITION_TIMEOUT = 120000;
	public static final int ONE_KM = 1000;

	public static Long getUTCTime() {
		Calendar cal=Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("gmt"));
		return cal.getTimeInMillis();
	}
	
	public static Long getLocalTime() {
		Calendar cal=Calendar.getInstance();
		return cal.getTimeInMillis();
	}
	
	public static boolean isWiFiActive(Context inContext) { 
		WifiManager wm=null;
		try{
			wm = (WifiManager) inContext.getSystemService(Context.WIFI_SERVICE);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(wm==null || wm.isWifiEnabled()==false) return false;
		
		return true;
    }  
	
	public static byte[] postViaHttpConnection(byte[] requestToSend, String urlStr) throws APIException{
		int status = 0;
		HttpURLConnection c = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			if(requestToSend != null){
				//Log.d("HttpConnection","postViaHttpConnection request:\n"+new String(requestToSend,"UTF-8"));
			}else{
				//Log.d("HttpConnection","postViaHttpConnection urlStr:\n"+urlStr);
			}
			URL url = new URL(urlStr);

			c = (HttpURLConnection) url.openConnection();
			c.setConnectTimeout(POSITION_TIMEOUT);
			c.setReadTimeout(POSITION_TIMEOUT);	
			
			if(requestToSend != null){
				c.setDoInput(true);
				c.setUseCaches(false);
				c.setDoOutput(true);
				c.setRequestMethod("POST");
				
				// Getting the output stream may flush the headers				
				os = c.getOutputStream();
				os.write(requestToSend);
				os.flush(); // Optional, openInputStream will flush
			}
			// Get the status code, causing the connection to be made
			status = c.getResponseCode();
			if ((status == HttpURLConnection.HTTP_NOT_IMPLEMENTED)
					|| (status == HttpURLConnection.HTTP_VERSION)
					|| (status == HttpURLConnection.HTTP_INTERNAL_ERROR)
					|| (status == HttpURLConnection.HTTP_GATEWAY_TIMEOUT)
					|| (status == HttpURLConnection.HTTP_BAD_GATEWAY)) {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
				if (c != null) {
					c.disconnect();
				}
				return null;
			}
			// Only HTTP_OK (200) means the content is returned.
			if (status != HttpURLConnection.HTTP_OK) {
				throw new APIException("Response status not OK [" + status + "]");
			}

			is = c.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int realRead;
			while ((realRead = is.read(buffer)) != -1) {
				baos.write(buffer, 0, realRead);
			}
			byte[] ret = baos.toByteArray();
			baos.close();
			is.close();
			Log.d("HttpConnection","postViaHttpConnection resp length:"+ret.length);
			//Log.i("WebServices","postViaHttpConnection resp:"+new String(ret));
			return ret;

		}catch (Exception e) {
			Log.e("HttpConnection generic",e.getMessage());
			return null;
		} catch(OutOfMemoryError e){
			Log.e("HttpConnection",e.getMessage());
			return null;
		}finally{
			try {
				if (os != null) {
					os.close();
				}
				if (c != null) {
					c.disconnect();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	}
	
	/**
	 * 判断经纬度是否在中国
	 * @param pos
	 * @return
	 */
	public static boolean positionInChina(Position pos){
		if(pos.getLat()>18.167 && pos.getLat()<53.55){
			if(pos.getLon()>73.667 && pos.getLon()<135.033){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解决经纬度在中国的偏移问题
	 * @param Position
	 * @return
	 */
	public static Position doShift(Position pos){
		if(pos.getLat()<0 || pos.getLon()<0){
			return pos;
		}
		
		if(!positionInChina(pos)){
			return pos;
		}
		
		if(ShiftTool.initialized == false){
			ShiftTool.shift(0, pos.getLon(), pos.getLat(),0);
		}
		String rtn = ShiftTool.shift(1, pos.getLon(), pos.getLat(),0);
		
		if(rtn.equals("0.0,0.0") || rtn.equals("0,0")){
			return pos;
		}
		return new Position(Double.parseDouble(rtn.split(",")[0]),Double.parseDouble(rtn.split(",")[1]));		
	}
}
