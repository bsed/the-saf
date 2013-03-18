/**
 * 
 */
package cn.salesuite.saf.imagecache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;
import cn.salesuite.saf.utils.IOUtil;

/**
 * @author Tony Shen
 *
 * 
 */
public class ImageLoader {
	
	MemoryCache memoryCache;
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    int stub_id;
    Handler handler=new Handler();
    
    public ImageLoader(Context context,int default_img_id){
    	memoryCache = new MemoryCache();
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(8);
        stub_id = default_img_id;
    }
    
    public ImageLoader(Context context,int default_img_id,String fileDir){
    	memoryCache = new MemoryCache();
        fileCache = new FileCache(context,fileDir);
        executorService = Executors.newFixedThreadPool(8);
        stub_id = default_img_id;
    }
    
    /**
     * 显示图片，如果未能获取图片，则显示全局的默认图片
     * @param url
     * @param imageView
     */
    public void displayImage(String url, ImageView imageView) {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else {
            queuePhoto(url, imageView);
            imageView.setImageResource(stub_id);
        }
    }
    
    /**
     * 显示图片，可自定义默认显示的图片
     * @param url
     * @param imageView
     * @param imageId 默认图片，可能区别与default_img_id
     */
    public void displayImage(String url, ImageView imageView, int imageId) {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else {
            queuePhoto(url, imageView, imageId);
            imageView.setImageResource(imageId);
        }
    }
        
    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }
    
    private void queuePhoto(String url, ImageView imageView, int imageId) {
        PhotoToLoad p=new PhotoToLoad(url, imageView, imageId);
        executorService.submit(new PhotosLoader(p));
    }
    
    public Bitmap getBitmap(String url) {
        File f=fileCache.getFile(url);
        
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
        
        //from web
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            IOUtil.copyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex){
           ex.printStackTrace();
           if(ex instanceof OutOfMemoryError)
               memoryCache.clear();
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale+=1;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
    
    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;
        public int imageId;
        
        public PhotoToLoad(String u, ImageView i){
            url=u; 
            imageView=i;
            this.imageId = stub_id;
        }
        
        public PhotoToLoad(String u, ImageView i, int imageId){
            url=u; 
            imageView=i;
            this.imageId = imageId;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            if(imageViewReused(photoToLoad))
                return;
            Bitmap bmp=getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if(imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
            handler.post(bd);
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){
        	bitmap=b;
        	photoToLoad=p;
        }
        
        public void run(){
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(photoToLoad.imageId);
        }
    }

    /**
     * 清空所有缓存
     */
    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }
    
    /**
     * 清空内存中的缓存
     */
    public void clearMemCache() {
    	memoryCache.clear();
    }

	public MemoryCache getMemoryCache() {
		return memoryCache;
	}
}
