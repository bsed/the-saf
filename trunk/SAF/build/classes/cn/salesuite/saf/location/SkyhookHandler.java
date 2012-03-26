/**
 * 
 */
package cn.salesuite.saf.location;

import android.content.Context;

import com.skyhookwireless.wps.WPSAuthentication;
import com.skyhookwireless.wps.WPSContinuation;
import com.skyhookwireless.wps.WPSLocation;
import com.skyhookwireless.wps.WPSLocationCallback;
import com.skyhookwireless.wps.WPSReturnCode;
import com.skyhookwireless.wps.WPSStreetAddressLookup;
import com.skyhookwireless.wps.XPS;

/**
 * @author Tony Shen
 *
 */
public class SkyhookHandler extends LocationHandler{

	private static final int LOCATION_MESSAGE = 1;
	private static final int ERROR_MESSAGE = 2;
	private static final int DONE_MESSAGE = 3;
	private XPS xps;
	private Context context;
		
	/*private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(final Message msg)
        {
            switch (msg.what)
            {
            case LOCATION_MESSAGE:
                Location _location = (Location) msg.obj;
                android.location.Location loc = new android.location.Location(LocationHelper.SKYHOOK_PROVIDER);
				loc.setLatitude((Double) _location.getLatitude());
				loc.setLongitude((Double) _location.getLongitude());
				int start = _location.toString().indexOf("+/-")+3;
				int end = _location.toString().indexOf("m");
				String accuracy = _location.toString().substring(start, end);
				loc.setAccuracy(Float.parseFloat(accuracy));
				loc.setTime(AppUtil.getUTCTime());
                lh.onLocationChanged(loc);
                return;
            case ERROR_MESSAGE:
//                _tv.setText(((WPSReturnCode) msg.obj).name());
                return;
            case DONE_MESSAGE:
            	return;
            }
        }
    };*/
	
	public SkyhookHandler(LocationHelper _lh,Context _context) {
		super(_lh);
		this.context = _context;
	}
	
	public void doRequest(DLocation threadLocation) {
		
		xps = new XPS(context);
		WPSAuthentication auth =
			   new WPSAuthentication("decarta", "decarta");
		xps.getLocation(auth,
			   WPSStreetAddressLookup.WPS_NO_STREET_ADDRESS_LOOKUP,
			                         callback);				
			
	}

	public MyLocationCallback getCallback() {
		return callback;
	}

	/**
     * A single callback class that will be used to handle all notifications
     * sent by WPS to our app.
     */
    private class MyLocationCallback
        implements WPSLocationCallback
    {
        public void done()
        {
        	/*mHandler.sendMessage(mHandler.obtainMessage(DONE_MESSAGE));*/
        }

        public WPSContinuation handleError(WPSReturnCode error)
        {
        	/*mHandler.sendMessage(mHandler.obtainMessage(ERROR_MESSAGE,
                                                        error));*/
        	lh.handleSkyhookError(error);
        	
        	return WPSContinuation.WPS_STOP;
            /*if (location.CellID == null && (location.WiFi==null || location.WiFi.get(0).mac == null))
            	return WPSContinuation.WPS_STOP;            	
            else
            	return WPSContinuation.WPS_CONTINUE;*/
        }

        public void handleWPSLocation(WPSLocation _location)
        {
        	/*mHandler.sendMessage(mHandler.obtainMessage(LOCATION_MESSAGE,
                                                        location));*/
        	android.location.Location loc = new android.location.Location(LocationHelper.SKYHOOK_PROVIDER);
			loc.setLatitude((Double) _location.getLatitude());
			loc.setLongitude((Double) _location.getLongitude());
			int start = _location.toString().indexOf("+/-")+3;
			int end = _location.toString().indexOf("m");
			String accuracy = _location.toString().substring(start, end);
			loc.setAccuracy(Float.parseFloat(accuracy));
			loc.setTime(LocationUtil.getUTCTime());
            lh.onLocationChanged(loc);
            return;
        }
    }
    
    private final MyLocationCallback callback = new MyLocationCallback();

	
	public XPS getXps() {
		return xps;
	}

}
