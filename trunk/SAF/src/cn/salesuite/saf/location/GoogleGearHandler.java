package cn.salesuite.saf.location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class GoogleGearHandler extends LocationHandler{

	private String tokenData = null;
	private Context context;
	private final String GOOGLE_GEARS_URL = "http://www.google.cn/loc/json";
	
	public GoogleGearHandler(LocationHelper _lh,Context _context) {
		super(_lh);
		this.context = _context;
	}
	
	public void doRequest(DLocation threadLocation){
					
			JSONObject holder = new JSONObject();
			Location loc = null;
			try {
				JSONObject data,current_data;
				
				JSONArray array = new JSONArray();
				
				holder.put("version", "1.1.0");
				holder.put("host", "maps.google.com");
				holder.put("request_address", false);
				if(tokenData != null){
					holder.put("access_token", tokenData);
				}
				if(threadLocation.WiFi!=null){
					Log.d("WIFI", "size:"+threadLocation.WiFi.size());
				}
				if(threadLocation.CellID!=null){
					Log.d("CellID", "size:"+threadLocation.CellID.size());
				}
				
				if (threadLocation.WiFi!=null && threadLocation.WiFi.size() != 0) {
					int wifi_size = threadLocation.WiFi.size();
					array = new JSONArray();
					for (int i = 0; i < wifi_size; i++) {
						data = new JSONObject();
						data.put("mac_address", threadLocation.WiFi.get(i).mac);
						data.put("signal_strength", threadLocation.WiFi.get(i).signal_strength);
						data.put("age", 0);
						array.put(data);
					}
					holder.put("wifi_towers", array);
					loc = new Location(LocationHelper.WIFI_PROVIDER);
					
				}else{
					if (threadLocation.CellID != null && threadLocation.CellID.size()>0 ){
						holder.put("home_mobile_country_code", threadLocation.CellID.get(0).mobileCountryCode);
						holder.put("home_mobile_network_code", threadLocation.CellID.get(0).mobileNetworkCode);
						holder.put("radio_type", threadLocation.CellID.get(0).radioType);
						
						/*if ("460".equals(threadLocation.CellID.get(0).mobileCountryCode)) 
							holder.put("address_language", "zh_CN");
						else
							holder.put("address_language", "en_US");*/
	
						current_data = new JSONObject();
						current_data.put("cell_id", threadLocation.CellID.get(0).cellId);
						current_data.put("location_area_code", threadLocation.CellID.get(0).locationAreaCode);
						current_data.put("mobile_country_code", threadLocation.CellID.get(0).mobileCountryCode);
						current_data.put("mobile_network_code", threadLocation.CellID.get(0).mobileNetworkCode);
						current_data.put("signal_strength", threadLocation.CellID.get(0).signal_strength);
						current_data.put("age", 0);
						array.put(current_data);
						
						if (threadLocation.CellID.size() > 1) {
							for (int i = 1; i < threadLocation.CellID.size(); i++) {
								data = new JSONObject();
								data.put("cell_id", threadLocation.CellID.get(i).cellId);
								data.put("location_area_code", threadLocation.CellID.get(i).locationAreaCode);
								data.put("mobile_country_code", threadLocation.CellID.get(i).mobileCountryCode);
								data.put("mobile_network_code", threadLocation.CellID.get(i).mobileNetworkCode);
								data.put("signal_strength", threadLocation.CellID.get(i).signal_strength);
								data.put("age", 0);
								array.put(data);
							}
						}
						holder.put("cell_towers", array);
						
						loc = new Location(LocationHelper.CELLID_PROVIDER);
					}
				}
				//Log.d("location", "Location request sent");
				//Log.e("Location request sent1", holder.toString());
				byte[] response = null;
				response = LocationUtil.postViaHttpConnection(holder.toString().getBytes(),GOOGLE_GEARS_URL);
				if (response == null) {
									
				}
				//Log.d("location", "Location response received");
				//Log.e("Locaiton response received1", new String(response));
				data = new JSONObject(new String(response));
				if (tokenData == null && data.has("access_token")) {
					tokenData = (String) data.getString("access_token");
				}
				if (data.has("location")) {
					data = (JSONObject) data.get("location");
					
					if(loc != null){
						loc.setLatitude((Double) data.get("latitude"));
						loc.setLongitude((Double) data.get("longitude"));
						loc.setAccuracy(Float.parseFloat(data.get("accuracy").toString()));
						loc.setTime(LocationUtil.getUTCTime());
						if(loc.getProvider().equals(LocationHelper.WIFI_PROVIDER) && loc.getAccuracy()>LocationUtil.ONE_KM){
							if (threadLocation.CellID != null && threadLocation.CellID.size()>0 ){
								holder.put("home_mobile_country_code", threadLocation.CellID.get(0).mobileCountryCode);
								holder.put("home_mobile_network_code", threadLocation.CellID.get(0).mobileNetworkCode);
								holder.put("radio_type", threadLocation.CellID.get(0).radioType);
								
								/*if ("460".equals(threadLocation.CellID.get(0).mobileCountryCode)) 
									holder.put("address_language", "zh_CN");
								else
									holder.put("address_language", "en_US");*/
			
								current_data = new JSONObject();
								current_data.put("cell_id", threadLocation.CellID.get(0).cellId);
								current_data.put("location_area_code", threadLocation.CellID.get(0).locationAreaCode);
								current_data.put("mobile_country_code", threadLocation.CellID.get(0).mobileCountryCode);
								current_data.put("mobile_network_code", threadLocation.CellID.get(0).mobileNetworkCode);
								current_data.put("signal_strength", threadLocation.CellID.get(0).signal_strength);
								current_data.put("age", 0);
								array.put(current_data);
								
								if (threadLocation.CellID.size() > 1) {
									for (int i = 1; i < threadLocation.CellID.size(); i++) {
										data = new JSONObject();
										data.put("cell_id", threadLocation.CellID.get(i).cellId);
										data.put("location_area_code", threadLocation.CellID.get(i).locationAreaCode);
										data.put("mobile_country_code", threadLocation.CellID.get(i).mobileCountryCode);
										data.put("mobile_network_code", threadLocation.CellID.get(i).mobileNetworkCode);
										data.put("signal_strength", threadLocation.CellID.get(i).signal_strength);
										data.put("age", 0);
										array.put(data);
									}
								}
								holder.put("cell_towers", array);
								
								loc = new Location(LocationHelper.CELLID_PROVIDER);
								//Log.d("location", "Location request sent");
								//Log.e("Location request sent2", holder.toString());
								
								response = LocationUtil.postViaHttpConnection(holder.toString().getBytes(),GOOGLE_GEARS_URL);
								if (response == null) {
										
								}
								//Log.d("location", "Location response received");
								//Log.e("Locaiton response received2", new String(response));
								data = new JSONObject(new String(response));
								if (tokenData == null && data.has("access_token")) {
									tokenData = (String) data.getString("access_token");
								}
								if (data.has("location")) {
									data = (JSONObject) data.get("location");
									
									if(loc != null){
										loc.setLatitude((Double) data.get("latitude"));
										loc.setLongitude((Double) data.get("longitude"));
										loc.setAccuracy(Float.parseFloat(data.get("accuracy").toString()));
										loc.setTime(LocationUtil.getUTCTime());
										lh.onLocationChanged(loc);
									}
								}
							}
						}else{
							lh.onLocationChanged(loc);
						}
					}
				}else{
					if (threadLocation.WiFi!=null && threadLocation.WiFi.size() != 0) {
						if (threadLocation.CellID != null && threadLocation.CellID.size()>0 ){
							holder.put("home_mobile_country_code", threadLocation.CellID.get(0).mobileCountryCode);
							holder.put("home_mobile_network_code", threadLocation.CellID.get(0).mobileNetworkCode);
							holder.put("radio_type", threadLocation.CellID.get(0).radioType);
							
							/*if ("460".equals(threadLocation.CellID.get(0).mobileCountryCode)) 
								holder.put("address_language", "zh_CN");
							else
								holder.put("address_language", "en_US");*/
	
							current_data = new JSONObject();
							current_data.put("cell_id", threadLocation.CellID.get(0).cellId);
							current_data.put("location_area_code", threadLocation.CellID.get(0).locationAreaCode);
							current_data.put("mobile_country_code", threadLocation.CellID.get(0).mobileCountryCode);
							current_data.put("mobile_network_code", threadLocation.CellID.get(0).mobileNetworkCode);
							current_data.put("signal_strength", threadLocation.CellID.get(0).signal_strength);
							current_data.put("age", 0);
							array.put(current_data);
							
							if (threadLocation.CellID.size() > 1) {
								for (int i = 1; i < threadLocation.CellID.size(); i++) {
									data = new JSONObject();
									data.put("cell_id", threadLocation.CellID.get(i).cellId);
									data.put("location_area_code", threadLocation.CellID.get(i).locationAreaCode);
									data.put("mobile_country_code", threadLocation.CellID.get(i).mobileCountryCode);
									data.put("mobile_network_code", threadLocation.CellID.get(i).mobileNetworkCode);
									data.put("signal_strength", threadLocation.CellID.get(i).signal_strength);
									data.put("age", 0);
									array.put(data);
								}
							}
							holder.put("cell_towers", array);
							
							//Log.d("location", "Location request with cellid only sent");
							//Log.e("Location send3", holder.toString());
							response = LocationUtil.postViaHttpConnection(holder.toString().getBytes(),GOOGLE_GEARS_URL);
							if(response == null) {	
								
							}
							//Log.d("location", "Location response received");
							//Log.e("Locaiton response receive3", new String(response));
							data = new JSONObject(new String(response));
							if (tokenData == null && data.has("access_token")) {
								tokenData = (String) data.getString("access_token");
							}
							if (data.has("location")) {
								data = (JSONObject) data.get("location");
								
								loc = new Location(LocationHelper.CELLID_PROVIDER);
								loc.setLatitude((Double) data.get("latitude"));
								loc.setLongitude((Double) data.get("longitude"));
								loc.setAccuracy(Float.parseFloat(data.get("accuracy").toString()));
								loc.setTime(LocationUtil.getUTCTime());
								lh.onLocationChanged(loc);
							}
						}
					} 
				}
			} catch (JSONException e) {
				Log.e("LocationHelper", "JSONException:"+e.getMessage());
			} catch (Exception e){
				Log.e("LocationHelper", "error caught:" + e.getMessage());
			}
			//return null;
		
	}
	
}
