/**
 * 
 */
package cn.salesuite.saf.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

/**
 * @author Tony Shen
 * 
 */
public class BitmapHelper {
	
    /**
     * 将网络图片的地址转换成Drawable对象
     * 
     * @param imageUrl 图片url
     * @return
     */
    public static Drawable getDrawableFromUrl(String imageUrl) {
        InputStream stream = getInputStreamFromUrl(imageUrl);
        Drawable d = Drawable.createFromStream(stream, "src");
        closeInputStream(stream);
        return d;
    }
    
    /**
     * 将网络图片的地址转换成Bitmap对象
     * 
     * @param imageUrl 图片url
     * @return
     */
    public static Bitmap getBitmapFromUrl(String imageUrl) {
        InputStream stream = getInputStreamFromUrl(imageUrl);
        Bitmap b = BitmapFactory.decodeStream(stream);
        closeInputStream(stream);
        return b;
    }
    
    /**
     * 根据imageUrl获得InputStream，需要自己手动关闭InputStream
     * 
     * @param imageUrl 图片url
     * @return
     */
    private static InputStream getInputStreamFromUrl(String imageUrl) {
        InputStream stream = null;
        try {
            URL url = new URL(imageUrl);
            stream = (InputStream)url.getContent();
        } catch (MalformedURLException e) {
            closeInputStream(stream);
            throw new RuntimeException("MalformedURLException occurred. ", e);
        } catch (IOException e) {
            closeInputStream(stream);
            throw new RuntimeException("IOException occurred. ", e);
        }
        return stream;
    }
    
    /**
     * 关闭InputStream
     * 
     * @param s
     */
    private static void closeInputStream(InputStream s) {
        if (s != null) {
            try {
                s.close();
            } catch (IOException e) {
                throw new RuntimeException("IOException occurred. ", e);
            }
        }
    }
}
