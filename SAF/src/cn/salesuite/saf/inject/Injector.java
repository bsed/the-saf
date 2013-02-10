/**
 * 
 */
package cn.salesuite.saf.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import cn.salesuite.saf.inject.annotation.InjectResource;
import cn.salesuite.saf.inject.annotation.InjectView;

/**
 * 注入view、resource到Activity<br>
 * 在Activity中使用注解，需要使用Injector.injectInto(this);
 * @author Tony Shen
 *
 */
public class Injector {
    public static boolean LOG_PERFORMANCE;

    protected final Context context;
    protected final Object target;
    protected final Activity activity;
    protected final Resources resources;
    protected final Class<?> clazz;
    
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
        } else {
            activity = null;
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
                }
            }
        }
        if (LOG_PERFORMANCE) {
            long time = System.currentTimeMillis() - start;
            Log.d("greenInject", "Injected fields in " + time + "ms (" + fields.length + " fields checked)");
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
	 * 查找resource，目前暂不支持Arrays、Colors、Animations等 
	 * @param type
	 * @param field
	 * @param annotation
	 * @return
	 */
	private Object findResource(Class<?> type, Field field,
			InjectResource annotation) {
        int id = annotation.id();
        if (type == String.class) {
            return context.getString(id);
        } else if (Drawable.class.isAssignableFrom(type)) {
            return resources.getDrawable(id);
        } else if (Bitmap.class.isAssignableFrom(type)) {
            return BitmapFactory.decodeResource(resources, id);
        } else {
            throw new InjectException("Cannot inject for type " + type + " (field " + field.getName() + ")");
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
