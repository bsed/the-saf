/**
 * 
 */
package cn.salesuite.saf.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import cn.salesuite.saf.location.Position;

/**
 * @author Tony Shen
 *
 */
public class DistanceUtil {

//	private final static double PI   = 3.14159265354;  
    private final static double D2R     = 0.017453 ;  
    private final static double a2  = 6378137.0;  
    private final static double e2  = 0.006739496742337;
    
    private final static String M = "米";
    private final static String KM = "公里";
    
    /**
     * 计算地球上2点的距离
     * @param p1
     * @param p2
     * @return
     */
	public static double getDistance(Position p1, Position p2) {  
        if(p1.getLon() == p2.getLon() && p1.getLat() == p2.getLat() ) {  
            return 0.0;  
        } else {  
            double fdLambda =(p1.getLon() - p2.getLon()) * D2R;  
            double fdPhi = (p1.getLat() - p2.getLat()) * D2R;  
            double fPhimean = ((p1.getLat() + p2.getLat()) / 2.0) * D2R;  
            double fTemp = 1 - e2 * (Math.pow (Math.sin(fPhimean),2));  
            double fRho = (a2 * (1 - e2)) / Math.pow (fTemp, 1.5);  
            double fNu = a2 / (Math.sqrt(1 - e2 * (Math.sin(fPhimean) * Math.sin(fPhimean))));  
            double fz = Math.sqrt (Math.pow(Math.sin(fdPhi / 2.0), 2) +  
                    Math.cos(p2.getLat() * D2R) * Math.cos( p1.getLat()*D2R ) * Math.pow( Math.sin(fdLambda / 2.0),2));  
            fz = 2 * Math.asin(fz);  
            double fAlpha = Math.cos(p2.getLat() * D2R) * Math.sin(fdLambda) * 1 / Math.sin(fz);  
            fAlpha = Math.asin (fAlpha);  
            double fR = (fRho * fNu) / ((fRho * Math.pow( Math.sin(fAlpha),2)) + (fNu * Math.pow( Math.cos(fAlpha),2)));
            return fz * fR; 
        }  
    }
	
	/**
	 * 格式化距离
	 * @param distance
	 * @return
	 */
	public static String getFormatDistance(double distance) {
		DecimalFormat df = new DecimalFormat("0.00");
		if (distance < 1000) {
			df = new DecimalFormat("0");
			return df.format(distance) + M;
		} else {
			return df.format(distance / 1000) + KM;
		}
	}
	
	public static BigDecimal getPosition2BigDecimal(String position){
		BigDecimal bd = new  BigDecimal(Double.parseDouble(position));
		bd = bd.setScale(5,4);
		return bd;
	}
	
	/**
	 * 根据经纬度解析出所在城市的名称
	 * @param center_position
	 * @param context
	 * @return
	 */
	public static String getCityFromPosition(String center_position, Context context) {
		String[] centers = center_position.split(",");
		Geocoder mGeocoder01 = new Geocoder(context);
		List<Address> lstAddress = null;
		JSONObject data;
		try {
			lstAddress = mGeocoder01.getFromLocation(Double.parseDouble(centers[0])/ 1000000, Double.parseDouble(centers[1])/ 1000000, 3);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (lstAddress == null) return "";
		if (lstAddress.size() != 0) {
			Address adsLocation = lstAddress.get(0);
			StringBuilder sb = new StringBuilder();
			String ads = adsLocation.toString().replace("Address","");
			int start = adsLocation.toString().replace("Address","").indexOf("],");
			sb.append("[").append(ads.substring(start+2, ads.length()));
			try {
				data = new JSONObject(sb.toString().replace("[", "{\"")
						.replace("]", "\"}").replace(",", "\",\"")
						.replace("=", "\"=\""));
				return data.getString("locality");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			return "";
		}
		return "";
	}
}
