package cn.salesuite.saf.weibo;

public class OAuthException extends RuntimeException {

	/**
	 * OAuth Exception
	 */
	private static final long serialVersionUID = 1L;
	private boolean mVisible;
	
	/**
	 * @param detailMessage
	 */
	public OAuthException(String detailMessage) {
		this(detailMessage,null);
	}
	/**
	 * @param detailMessage
	 * @param exception
	 */
	public OAuthException(String detailMessage, Exception exception) {
		this(detailMessage, exception,true);
	}
	/**
	 * @param detailMessage
	 * @param exception
	 * @param visible
	 */
	public OAuthException(String detailMessage, Exception exception,boolean visible) {
		super(detailMessage, exception);
		this.mVisible=visible;
	}
	public boolean isVisible(){
	    return this.mVisible;
	}
}

