/**
 * 
 */
package cn.salesuite.saf.location;

import java.io.Serializable;

/**
 * 封装经纬度的对象
 * @author Tony Shen
 *
 */
public class Position implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private double lat;
	private double lon;
	
	public Position(double lat, double lon){
		lat=(lat+90+180)%180-90;
		lon=(lon+180+360)%360-180;					
		this.lat=lat;
		this.lon=lon;
	}
	
	public Position(String latlon){
		int index1=-1;
		if(latlon.indexOf(",")>-1){
			index1=latlon.indexOf(",");
			
		}else if(latlon.indexOf(" ")>-1){
			index1=latlon.indexOf(" ");
		}
		
		this.lat = Double.parseDouble(latlon.substring(0, index1));
		this.lat = (this.lat + 90 + 180) % 180 - 90;
		this.lon = Double.parseDouble(latlon.substring(index1 + 1));
		this.lon = (this.lon + 180 + 360) % 360 - 180;	
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}
	
	@Override
	public Position clone(){
		return new Position(this.lat,this.lon);
	}
	
	@Override
	public String toString(){
		return lat+" "+lon;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Position other = (Position) obj;
		if(other.lat==this.lat && other.lon==this.lon)
			return true;
		else
			return false;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
		hash = 97 * hash + (int) (Double.doubleToLongBits(this.lon) ^ (Double.doubleToLongBits(this.lon) >>> 32));
		return hash;
	}
	
}
