/**
 * 
 */
package cn.salesuite.saf.location;

import android.content.Context;
import android.util.Log;

/**
 * @author Tony Shen
 *
 */
public class LocationThread extends Thread{

	LocationHelper lh = null;
	DLocation location = new DLocation();
	LocationHandler lhd = null;
	private Context context;
	private WifiInfoManager wifiManager = null;
	private CellIDInfoManager cellIDManager = null;
	public static final int STATUS_BUSY = 1;
	public static final int STATUS_IDLE = 2;
	private  int status = STATUS_IDLE;
	
	
	public LocationThread(LocationHelper lh,Context _context){
		this.lh = lh;
		this.context = _context;
		wifiManager = new WifiInfoManager();
		cellIDManager = new CellIDInfoManager();
	}
	
	public int getStatus(){
		return status;
	}
	
	public void addRequest(DLocation location){
		this.location = location;
	}
	
	public void readParameters(DLocation tlocation){
		try{
			tlocation.WiFi = wifiManager.getWifiInfo(this.context);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	try{
    		tlocation.CellID = cellIDManager.getCellIDInfo(this.context);
	    }catch(Exception e){
			e.printStackTrace();
		}
	    
	}
	
	public void run(){
		DLocation threadLocation = null;
		while (true) {
			synchronized(location) {
				if(location == null){
					Log.d("LocationThread", "location is null");
					try {
						location.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				while (location.isReady==false){
					try {
						location.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				threadLocation = location.clone();
				location.clear();
				
				if (!threadLocation.forceGoogle && lh.locationHandler.equalsIgnoreCase("Skyhook")) {
//			    	if(LocationUtil.isWiFiActive(context)){
				    	if(!(lhd instanceof SkyhookHandler)){
				    		lhd = new SkyhookHandler(lh,context);				    		
				    	}
//			    	}else{
//			    		if(!(lhd instanceof GoogleGearHandler)){
//				    		lhd = new GoogleGearHandler(lh,context);				    		
//				    	}
//			    	}			    	
				}else if(threadLocation.forceGoogle || lh.locationHandler.equalsIgnoreCase("Google")){
					if(!(lhd instanceof GoogleGearHandler)){
			    		lhd = new GoogleGearHandler(lh,context);				    		
			    	}
				}
				if(lhd != null){
					if(lhd instanceof GoogleGearHandler){
						readParameters(threadLocation);
						if(threadLocation.CellID == null && (threadLocation.WiFi==null || threadLocation.WiFi.get(0).mac == null)){
							continue;
						}						
					}
					status = STATUS_BUSY;
					lhd.doRequest(threadLocation);
					status = STATUS_IDLE;
				}
				
			}
		}
	}
	
}