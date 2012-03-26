/**
 * 
 */
package cn.salesuite.saf.config;

/**
 * @author Tony Shen
 *
 */
public class CONFIG {

	/**
	 * Set Log level
	 * 0: no log
	 * 1: error
	 * 2: warn
	 * 3: info
	 * 4: debug
	 */
	public static int LOG_LEVEL=4; 
		
	/**
	 * number of messages should be saved in log
	 */
	public static int LOG_SIZE=200;
	
	/**
	 * Set tag name
	 * 0:Simple Name
	 * 1:Canonical Name
	 */
	public static int TAG_LEVEL = 0;
}
