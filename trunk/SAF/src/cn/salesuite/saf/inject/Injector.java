/**
 * 
 */
package cn.salesuite.saf.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import cn.salesuite.saf.inject.annotation.InjectExtra;
import cn.salesuite.saf.inject.annotation.InjectResource;
import cn.salesuite.saf.inject.annotation.InjectSystemService;
import cn.salesuite.saf.inject.annotation.InjectView;

/**
 * 注入view、resource、systemservice到Activity<br>
 * 在Activity中使用注解，首先需要使用Injector.injectInto(this);
 * @author Tony Shen
 *
 */
public class Injector {
    public static boolean TEST_FLAG;
    public static String TAG = "Injector";

    protected final Context context;
    protected final Object target;
    protected final Activity activity;
    protected final Resources resources;
    protected final Class<?> clazz;
    private final Bundle extras;
    
    public Injector(Context context) {
        this(context, context);
    }
    
    public Injector(Context context, Object target) {
        if (context == null || target == null) {
            throw new IllegalArgumentException("Context/target may not be null");
        }
        this.context = context;
        this.target = target;
        resources = context.getResources();
        if (context instanceof Activity) {
            activity = (Activity) context;
            Intent intent = activity.getIntent();
            if (intent != null) {
                extras = intent.getExtras();
            } else {
                extras = null;
            }
        } else {
            activity = null;
            extras = null;
        }
        clazz = target.getClass();
    }
    
    public static Injector injectInto(Context context) {
        return inject(context, context);
    }
    
    public static Injector inject(Context context, Object target) {
        Injector injector = new Injector(context, target);
        injector.injectAll();
        return injector;
    }

	private void injectAll() {
        injectFields();
	}

	private void injectFields() {
		long start = System.currentTimeMillis();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == InjectView.class) {
                    int id = ((InjectView) annotation).id();
                    View view = findView(field, id);
                    injectIntoField(field, view);
                } else if (annotation.annotationType() == InjectResource.class) {
                    Object ressource = findResource(field.getType(), field, (InjectResource) annotation);
                    injectIntoField(field, ressource);
                } else if (annotation.annotationType() == InjectSystemService.class) {
                	String serviceName = ((InjectSystemService) annotation).value();
                	Object service = context.getSystemService(serviceName);
                    injectIntoField(field, service);
                } else if (annotation.annotationType() == InjectExtra.class) {
                    if (extras != null) {
                        Object value = extras.get(((InjectExtra) annotation).key());
                        injectIntoField(field, value);
                    }
                }
            }
        }
        if (TEST_FLAG) {
            long time = System.currentTimeMillis() - start;
            Log.d(TAG, "Injected fields in " + time + "ms (" + fields.length + " fields checked)");
        }
	}

	/**
	 * 查找view
	 * @param field
	 * @param viewId
	 * @return
	 */
	private View findView(Field field, int viewId) {
        if (activity == null) {
            throw new InjectException("Views can be injected only in activities (member " + field.getName() + " in "
                    + context.getClass());
        }
        View view = activity.findViewById(viewId);
        if (view == null) {
            throw new InjectException("View not found for member " + field.getName());
        }
        return view;
	}
	
	/**
	 * 查找resource，目前暂不支持Animations等 
	 * @param cls
	 * @param field
	 * @param annotation
	 * @return
	 */
	private Object findResource(Class<?> cls, Field field,
			InjectResource annotation) {
        int id = annotation.id();
        if (cls == int.class) {          //从color.xml中获取对应的值
        	return context.getResources().getColor(id);
        } else if (cls == String.class) {//从strings.xml中获取对应的值
            return context.getString(id);
        } else if (Drawable.class.isAssignableFrom(cls)) {//从drawable文件中获取资源
            return resources.getDrawable(id);
        } else if (Bitmap.class.isAssignableFrom(cls)) {
            return BitmapFactory.decodeResource(resources, id);
        } else if (cls.isArray()) {      //从arrays.xml中获取对应的值
        	if (cls.getComponentType() == String.class) {
        		return context.getResources().getStringArray(id);
        	}
        	throw new InjectException("Cannot inject for type " + cls + " (field " + field.getName() + ")");
        }
        else {
            throw new InjectException("Cannot inject for type " + cls + " (field " + field.getName() + ")");
        }
	}
	
	private void injectIntoField(Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new InjectException("Could not inject into field " + field.getName(), e);
        }
	}
}
