/**
 * 
 */
package cn.salesuite.saf.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Tony Shen
 *
 * 
 */
public class BasePrefs {

	private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    protected BasePrefs(Context context, String prefsName) {
        prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    }
    
    protected boolean getBoolean(String key, boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }
    
    protected float getFloat(String key, float defValue) {
        return prefs.getFloat(key, defValue);
    }
    
    protected int getInt(String key, int defValue) {
        return prefs.getInt(key, defValue);
    }
    
    protected long getLong(String key, long defValue) {
        return prefs.getLong(key, defValue);
    }
    
    protected String getString(String key, String defValue) {
        return prefs.getString(key, defValue);
    }

    protected void putBoolean(String key, boolean v) {
        ensureEditorAvailability();
        editor.putBoolean(key, v);
    }

    protected void putFloat(String key, float v) {
        ensureEditorAvailability();
        editor.putFloat(key, v);
    }

    protected void putInt(String key, int v) {
        ensureEditorAvailability();
        editor.putInt(key, v);
    }

    protected void putLong(String key, long v) {
        ensureEditorAvailability();
        editor.putLong(key, v);
    }

    protected void putString(String key, String v) {
        ensureEditorAvailability();
        editor.putString(key, v);
    }

    public void save() {
        if (editor != null) {
            editor.commit();
        }
    }

    private void ensureEditorAvailability() {
        if (editor == null) {
            editor = prefs.edit();
        }
    }
}
