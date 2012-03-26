package cn.salesuite.saf.location;

public abstract class LocationHandler {
	
	public LocationHelper lh = null;
	
	public LocationHandler(LocationHelper _lh){
		this.lh = _lh;
	}
	public abstract void doRequest(DLocation threadLocation);
}
