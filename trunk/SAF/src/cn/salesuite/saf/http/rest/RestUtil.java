/**
 * 
 */
package cn.salesuite.saf.http.rest;

import java.io.IOException;

import com.alibaba.fastjson.JSON;

/**
 * @author Tony
 *
 */
public class RestUtil {
	
	/**
	 * 验证charset
	 * @param charset
	 * @return
	 */
	public static String getValidCharset(String charset) {
		if (charset != null && charset.length() > 0)
			return charset;
		else
			return RestConstant.CHARSET_UTF8;
	}
	
	/**
	 * 根据返回的http body内容转换成json对象
	 * @param cls
	 * @param body
	 * @return
	 * @throws IOException
	 */
    public static <T> T parseAs(Class<T> cls,String body) throws IOException {
    	return JSON.parseObject(body, cls);
    }
}
