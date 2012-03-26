package cn.salesuite.saf.location;

import java.util.ArrayList;

import android.location.Location;

public class DLocation {
	public ArrayList<WifiInfo> WiFi = null;
	public ArrayList<CellIDInfo> CellID = null;
	public Location location = null;
	public boolean isReady = false;
	public boolean forceGoogle = false;
	public DLocation(){
		
	}
	
	public DLocation clone(){
		DLocation location = new DLocation();
		location.CellID = CellID;
		location.WiFi = WiFi;
		location.isReady = isReady;
		location.forceGoogle = forceGoogle;
		return location;
		
	}
	
	public void clear(){
		CellID = null;
		WiFi = null;
		isReady = false;
		forceGoogle = false;
	}
	

}
