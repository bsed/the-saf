/**
 * 
 */
package cn.salesuite.saf.inject;

/**
 * @author Tony Shen
 *
 */
public class InjectException extends RuntimeException{

	private static final long serialVersionUID = -5298989560573894243L;

    public InjectException(String detailMessage) {
        super(detailMessage);
    }

    public InjectException(Throwable throwable) {
        super(throwable);
    }

    public InjectException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
