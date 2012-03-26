package cn.salesuite.saf.location;

import java.util.ArrayList;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.skyhookwireless.wps.WPSReturnCode;

public class LocationHelper implements LocationListener{

	public static int status = 0;
	public boolean isGPSEnabled = false;
	public boolean isNetworkEnabled = false;
	public boolean isDecartaEnabled = false;
	public static final String WIFI_PROVIDER = "wifi";
	public static final String CELLID_PROVIDER = "cellid";
	public static final String SKYHOOK_PROVIDER = "skyhook";
	
	protected static final int GPS_STARTED = 1;
	protected static final int NETWORK_STARTED = 2;
	protected static final int STOPPED = 3;
	protected static final int LOCATING = 4;
	protected static final int WIFI_STARTED = 5;
	
	public LocationManager mLocationManager=null;
	private Context context = null;	
	private Location currentLocation = null;
	private Handler mHandler =  new Handler();   	
	private ArrayList<LocationListener> listeners = new ArrayList<LocationListener>();
	
	private WifiInfoManager wifiManager = null;
	private CellIDInfoManager cellIDManager = null;
	private ArrayList<WifiInfo> wifi = new ArrayList<WifiInfo>();
	private ArrayList<CellIDInfo> cellID = new ArrayList<CellIDInfo>();
//	private MyPhoneStateListener _listener;
	private LocationThread locationThread = null; 
	private DLocation location = new DLocation();
	public String locationHandler;

	public LocationHelper(Context context,String _locationHandler){
		status = STOPPED;
		/*wifiManager = new WifiInfoManager();
		cellIDManager = new CellIDInfoManager();*/
		
		locationHandler = _locationHandler;
		
		locationThread = new LocationThread(this,context);
		locationThread.addRequest(location);
		locationThread.start();
	}
	
	/**
	 * LocationListner implementation: used for receiving location updates
	 */
    public void onLocationChanged(Location location) {
    	Log.d("LocationHelper:onLocationChanged", location.getLatitude()+" "+location.getLongitude()+" "+location.getProvider());

    	if(currentLocation!=null && !location.getProvider().equals(currentLocation.getProvider())){
	    	if(!location.getProvider().equals(LocationManager.GPS_PROVIDER) && isQualified(currentLocation)){// new location is GPS
	    		if(currentLocation.getProvider().equals(LocationManager.GPS_PROVIDER)){ //old location is GPS
	    			return;    			
	    		}else{
	    			if(!location.getProvider().equals(CELLID_PROVIDER) && !currentLocation.getProvider().equals(CELLID_PROVIDER)){//both old and new are WIFI or Network
	    				if(!location.getProvider().equals(SKYHOOK_PROVIDER) && !location.hasBearing() 
	    						&& location.getBearing()>currentLocation.getBearing()){
	    					return;
	    				}
	    			}else{
	    				if(currentLocation.getProvider().equals(SKYHOOK_PROVIDER) 
	    						&& location.getProvider().equals(CELLID_PROVIDER) && status==WIFI_STARTED){
	    			        this.currentLocation = location;
	    			        for(LocationListener listener:listeners){
	    			        	listener.onLocationChanged(location);
	    			        }
	    			        status = 0;
	    			        this.location.clear();
	    					return;
	    				}
	    				
//	    				if(!currentLocation.getProvider().equals(CELLID_PROVIDER)){//old is not cellid means new is cellid
//	    					return;
//	    				}
	    			}
	    		}
	    		
	    	}
    	}
    	
        this.currentLocation = location;
        for(LocationListener listener:listeners){
        	listener.onLocationChanged(location);
        }
        
        if(location.getProvider().equals(LocationManager.GPS_PROVIDER) || location.getProvider().equals(LocationManager.NETWORK_PROVIDER)){
        	mHandler.removeCallbacks(mRunnable);
        	isDecartaEnabled = false;
        	status = GPS_STARTED;
        }
        
    }
    
    public void onProviderDisabled(String provider) {
    	if(mLocationManager.getProviders(true)==null){
	    	mLocationManager.removeUpdates(this);
	    	//isGPSEnabled = false;
	    	mHandler.post(mRunnable);
    	}
    	/*
    	if(isNetworkEnabled && status == STOPPED){
    		mHandler.post(mRunnable);
    	}
    	*/
    }
    public void onProviderEnabled(String provider) {    	
    		//if(isGPSEnabled == false){
    			Criteria criteria = new Criteria();
    			if(status != GPS_STARTED){
    				Location gpsLocation = mLocationManager.getLastKnownLocation(provider);
		    		if(gpsLocation != null && isQualified(gpsLocation)){
			    		onLocationChanged(gpsLocation);
			    	}  
    			}
	    		//criteria.setAccuracy(Criteria.ACCURACY_FINE);
		    	mLocationManager.requestLocationUpdates(provider,
		    			LocationUtil.MIN_LOCATION_UPDATE_TIME, 
		    			LocationUtil.MIN_LOCATION_UPDATE_DISTANCE, 
		                this);
		    	//isGPSEnabled = true;		    	
		    	//mHandler.removeCallbacks(mRunnable);
		    	//isDecartaEnabled = false;
    		//}
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
    	//if(provider.equals(mLocationManager.GPS_PROVIDER)){
    		if(status==LocationProvider.AVAILABLE){
    			//mHandler.removeCallbacks(mRunnable);    			
    		}else{
    			//mHandler.post(mRunnable);
    		}
    	//}
    }	
    
    public void setContext(Context context){
    	this.context = context;
    }
    
    public void removeContext(){
    	this.context = null;
    }
    
    public Context getContext(){
    	return this.context;
    }
    
    public void start(){
    	if(context != null){
	    	mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	    	
	    	try{
	    		Criteria criteria = new Criteria();
	    		//Location gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    		/*if(currentLocation != null && isQualified(currentLocation)){
		    		onLocationChanged(currentLocation);
		    	}*/
	    		mHandler.removeCallbacks(mRunnable);
	    		
		    	if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){			    	
		    		//criteria.setAccuracy(Criteria.ACCURACY_FINE);
			    	
		    		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		    				LocationUtil.MIN_LOCATION_UPDATE_TIME, 
			        		LocationUtil.MIN_LOCATION_UPDATE_DISTANCE, 
			                this);		    	
			    	//isGPSEnabled = true;
		    	}
		    	
		    	if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
	    			
	    			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
	    					LocationUtil.MIN_LOCATION_UPDATE_TIME, 
			        		LocationUtil.MIN_LOCATION_UPDATE_DISTANCE, 
			                this);
		    		//isDecartaEnabled = false;
		    		//status = LOCATING;
	    		}
		    	
		    	
		    	/*if (AppConfig.LOCATION_VENDER.equals("Skyhook") && AppUtil.isWiFiActive(context)) {
		    		    mHandler.removeCallbacks(mRunnable);
		    		    if (!locationThread.isAlive()) {
		    		    	locationThread.doRequest();
		    		    }
		    		    status=WIFI_STARTED;
		    		    isDecartaEnabled = false;
		    	} else {*/
		    		/*if(currentLocation != null && isQualified(currentLocation)){
			    		mHandler.postDelayed(mRunnable, AppUtil.TWO_MINUTES);
			    	}else{
			    		mHandler.post(mRunnable);
			    	}	 */ 
		    		mHandler.post(mRunnable);
		    		status = LOCATING;
		    		isDecartaEnabled = true;
		    	//}
	    		
//	    	    mHandler.post(mRunnable);
	    	    
	    	    
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
    	}
    }
    
    public void stop(){
    	mLocationManager.removeUpdates(this);
    	mHandler.removeCallbacks(mRunnable);
    	status = STOPPED;
    } 
    
    public void registerLocationListener(cn.salesuite.saf.location.LocationHelper.LocationListener listener){
    	this.listeners.add(listener);
    }
    
    public void unregisterLocationListener(cn.salesuite.saf.location.LocationHelper.LocationListener listener){
    	if(this.listeners.contains(listener)){
    		this.listeners.remove(listener);
    	}
    }
    
    public boolean isProviderEnabled(String provider){
    	if(provider.equals(WIFI_PROVIDER)||provider.equals(CELLID_PROVIDER)){
			return isDecartaEnabled;
		}else{
			return mLocationManager.isProviderEnabled(provider);
		}
	}
   
    @SuppressWarnings("unchecked")
	public void requestLocation(){
	    
    	//if (wifi != null)   wifi.clear();
    	//if (cellID != null) cellID.clear();
    	/*try{
    		location.WiFi = wifiManager.getWifiInfo(getContext());
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	try{
	    	location.CellID = cellIDManager.getCellIDInfo(getContext());
	    }catch(Exception e){
			e.printStackTrace();
		}*/
    	if(locationThread.getStatus() == LocationThread.STATUS_BUSY){
    		return;
    	}
    	location.isReady = true;
    	synchronized(location){
	    	//Call Google Gear to retrieve location
	    	Log.d("LocationHelper", "Thread nofitying");
	    	location.notify();
    	}
    	
    }
    
    public boolean isQualified(Location location){
    	if(location == null) return false;
    	
		long history = location.getTime();			
		
		if(location.getProvider().equals(LocationManager.GPS_PROVIDER)){
			long now = LocationUtil.getUTCTime();
			Log.d("isQualified","isQualified: {history:"+history+" now:"+now+"}");
			
			if(now - history >LocationUtil.TWO_MINUTES){
				return false;
			}
		}else{
			long now = LocationUtil.getLocalTime();
			Log.d("isQualified","isQualified: {history:"+history+" now:"+now+"}");
			
			if(now - history >LocationUtil.TWO_MINUTES){
				return false;
			}
		}
		return true;
	}
	
    public void destroy(){
    	this.context = null;
    	this.locationThread = null;
    }
    
    public void startDecartaLocation(){
//    	isDecartaEnabled = true;
        /*if (isDecartaEnabled) {
        	mHandler.post(mRunnable);
		    status = LOCATING;
        } else if (AppConfig.LOCATION_VENDER.equals("Skyhook") && AppUtil.isWiFiActive(context)) {
        	if (locationThread instanceof GoogleGearThread) {
        		locationThread = new SkyhookThread(this,context); 
        	}
		    if (!locationThread.isAlive()) {
		    	locationThread.doRequest();
		    }
		    status=WIFI_STARTED;
		    isDecartaEnabled = false;
        }*/
    }
    
    private Runnable mRunnable = new Runnable() {   
		
		
        public void run() {
        	
        	requestLocation();
        	
        	//retrieve location every 2 minutes
            mHandler.postDelayed(mRunnable, LocationUtil.TWO_MINUTES);
        }   
           
    };   
    
    public interface LocationListener{
    	public abstract void onLocationChanged(Location location);
    }

	public CellIDInfoManager getCellIDManager() {
		return cellIDManager;
	}

	public LocationThread getLocationThread() {
		return locationThread;
	}
	
	public void handleSkyhookError(WPSReturnCode error){
		Log.e("Skyhook", error.name());
		location.forceGoogle = true;
		requestLocation();
	}
}