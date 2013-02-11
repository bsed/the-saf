/**
 * 
 */
package cn.salesuite.saf.imagecache;

import java.io.File;

import cn.salesuite.saf.config.SAFConfig;

import android.content.Context;

/**
 * @author Tony Shen
 * 
 * 
 */
public class FileCache {

	private File cacheDir;

	public FileCache(Context context) {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),SAFConfig.CACHE_DIR);
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists()) {
			boolean b = cacheDir.mkdirs();
			if (!b) {
				cacheDir = context.getCacheDir();
				if (!cacheDir.exists())
					cacheDir.mkdirs();
			}
		}
	}

	public File getFile(String url) {
		
		String filename = String.valueOf(url.hashCode());
		// 另一种解决方案 (thanks to grantland)
		// String filename = URLEncoder.encode(url);
		File f = new File(cacheDir, filename);
		return f;

	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}
}
