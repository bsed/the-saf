/**
 * 
 */
package cn.salesuite.saf.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * @author Tony Shen
 * 
 */
public class BitmapHelper {
	
	/**
	 * 将网络图片的地址转换成Drawable对象
	 * @param imageUrl
	 * @return
	 */
	public static Drawable ImageUrl2Drawable(String imageUrl) {
		try {
			URL aryURI = new URL(imageUrl);
			URLConnection conn = aryURI.openConnection();
			InputStream is = conn.getInputStream();
			Bitmap bm = BitmapFactory.decodeStream(is);
			return new BitmapDrawable(bm);
		} catch (MalformedURLException e) {
			Log.e("ERROR", "ImageUrl2Drawable方法发生MalformedURLException异常，imageUrl：" + imageUrl, e);
			return null;
		} catch (IOException e) {
			Log.e("ERROR", "ImageUrl2Drawable方法发生IOException异常，imageUrl：" + imageUrl, e);
			return null;
		}
	}
}
