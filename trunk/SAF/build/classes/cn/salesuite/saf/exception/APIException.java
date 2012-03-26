/**
 * 
 */
package cn.salesuite.saf.exception;

/**
 * @author Tony Shen
 *
 */
public class APIException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * exception when the latitude is 90, and want to convert it to mercator y coord
	 */
	public static APIException INVALID_LATITUDE_90 = new APIException("invalid latitude for mercator mode: +-90");
	
	/**
	 * exception when latitude is close to 90, and when convert to mercator y coord, it's infinity
	 */
	public static APIException INVALID_MERCATOR_Y_INFINITY=new APIException("invalide latitude to screen coordinate: infinity");
	
	public APIException(String msg){
		super((msg==null)?"":msg);
	}
	
	private APIException(Throwable throwable){
		super(throwable.getMessage()==null ? throwable.getClass().getName() : throwable.getMessage() ,throwable);
	}
	
	public static APIException wrapToAPIException(Throwable throwable){
		if(throwable instanceof APIException) return (APIException)throwable;
		else return new APIException(throwable);
	}
}
