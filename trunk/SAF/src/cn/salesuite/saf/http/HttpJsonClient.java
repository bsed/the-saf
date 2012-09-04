/**
 * 
 */
package cn.salesuite.saf.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSON;

/**
 * @author Tony Shen
 *
 * 
 */
public class HttpJsonClient extends CommHttpClient{

    public <T> T parseAs(Class<T> cls,InputStream response) throws IOException {
    	String text = getContentAsText(response);
    	return JSON.parseObject(text, cls);
    }
    
    public String getContentAsText(InputStream response) throws IOException {
        final char[] buffer = new char[4096];
        StringBuilder builder = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(response, "UTF-8");
        int read;
        while ((read = reader.read(buffer)) > 0) {
            builder.append(buffer, 0, read);
        }
        return builder.toString();
    }
}
