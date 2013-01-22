/**
 * 
 */
package cn.salesuite.saf.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

/**
 * 适用于AutoComplete的Adapter
 * @author Tony Shen
 *
 */
public class SearchAdapter<T> extends BaseAdapter implements Filterable {
	private List<T> mObjects;

	private final Object mLock = new Object();

	private int mResource;

	private int mDropDownResource;

	private int mFieldId = 0;

	private boolean mNotifyOnChange = true;
	private Context mContext;
	private ArrayList<T> mOriginalValues;
	private SearchFilter mFilter;
	private LayoutInflater mInflater;
	
	public SearchAdapter(Context context, int textViewResourceId) {
		init(context, textViewResourceId, 0, new ArrayList<T>());
	}
	
	public SearchAdapter(Context context, int resource, int textViewResourceId) {
		init(context, resource, textViewResourceId, new ArrayList<T>());
	}

	public SearchAdapter(Context context, int textViewResourceId, T[] objects) {
		if (objects == null) {
			init(context, textViewResourceId, 0, null);
		} else {
			init(context, textViewResourceId, 0, Arrays.asList(objects));
		}
	}
	
	public SearchAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
		if (objects == null) {
			init(context, resource, textViewResourceId, null);  
		} else {
			init(context, resource, textViewResourceId, Arrays.asList(objects));  
		}
    }
	
	public SearchAdapter(Context context, int textViewResourceId,
			List<T> objects) {
		if (objects == null) {
			init(context, textViewResourceId, 0, null);
		} else {
			init(context, textViewResourceId, 0, objects);
		}
	}

	public SearchAdapter(Context context, int resource, int textViewResourceId,
			List<T> objects) {
		if (objects == null) {
			init(context, resource, textViewResourceId, null);
		} else {
			init(context, resource, textViewResourceId, objects);
		}
	}
	
	public void add(T object) {
		if (mOriginalValues != null) {
			synchronized (mLock) {
				mOriginalValues.add(object);
				if (mNotifyOnChange)
					notifyDataSetChanged();
			}
		} else {
			mObjects.add(object);
			if (mNotifyOnChange)
				notifyDataSetChanged();
		}
	}
 
    public void addAll(Collection<? extends T> collection) {  
        if (mOriginalValues != null) {  
            synchronized (mLock) {  
                mOriginalValues.addAll(collection);  
                if (mNotifyOnChange) notifyDataSetChanged();  
            }  
        } else {  
            mObjects.addAll(collection);  
            if (mNotifyOnChange) notifyDataSetChanged();  
        }  
    }  

    public void addAll(T ... items) {  
        if (mOriginalValues != null) {  
            synchronized (mLock) {  
                for (T item : items) {  
                    mOriginalValues.add(item);  
                }  
                if (mNotifyOnChange) notifyDataSetChanged();  
            }  
        } else {  
            for (T item : items) {  
                mObjects.add(item);  
            }  
            if (mNotifyOnChange) notifyDataSetChanged();  
        }  
    }  

    public void insert(T object, int index) {  
        if (mOriginalValues != null) {  
            synchronized (mLock) {  
                mOriginalValues.add(index, object);  
                if (mNotifyOnChange) notifyDataSetChanged();  
            }  
        } else {  
            mObjects.add(index, object);  
            if (mNotifyOnChange) notifyDataSetChanged();  
        }  
    }  
  
    public void remove(T object) {  
        if (mOriginalValues != null) {  
            synchronized (mLock) {  
                mOriginalValues.remove(object);  
            }  
        } else {  
            mObjects.remove(object);  
        }  
        if (mNotifyOnChange) notifyDataSetChanged();  
    }  
 
    public void clear() {  
        if (mOriginalValues != null) {  
            synchronized (mLock) {  
                mOriginalValues.clear();  
            }  
        } else {
            mObjects.clear();  
        }  
        if (mNotifyOnChange) notifyDataSetChanged();  
    }  
 
    public void sort(Comparator<? super T> comparator) {  
        Collections.sort(mObjects, comparator);  
        if (mNotifyOnChange) notifyDataSetChanged();  
    }  
  
    @Override  
    public void notifyDataSetChanged() {  
        super.notifyDataSetChanged();  
        mNotifyOnChange = true;  
    }  

    public void setNotifyOnChange(boolean notifyOnChange) {  
        mNotifyOnChange = notifyOnChange;  
    }
    
    private void init(Context context, int resource, int textViewResourceId, List<T> objects) {  
        mContext = context;  
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        mResource = mDropDownResource = resource;  
        if (objects != null) {
        	mObjects = new ArrayList<T>(objects);
		}else{
	        mObjects = new ArrayList<T>();  
		}
        
        mFieldId = textViewResourceId; 
    }  

    public Context getContext() {  
        return mContext;  
    }  
 
    public int getCount() {
    	if (mObjects!=null) {
    		return mObjects.size(); 
    	}
        return 0; 
    }  
  
    public T getItem(int position) {  
        return mObjects.get(position);  
    }  
 
    public int getPosition(T item) {  
        return mObjects.indexOf(item);  
    }  
 
    public long getItemId(int position) {  
        return position;  
    }  

    public View getView(int position, View convertView, ViewGroup parent) {  
        return createViewFromResource(position, convertView, parent, mResource);  
    }
    
    private View createViewFromResource(int position, View convertView, ViewGroup parent,  
            int resource) {  
        View view;  
        TextView text;  
        if (convertView == null) {  
            view = mInflater.inflate(resource, parent, false);  
        } else {  
            view = convertView;  
        }  
        try {  
            if (mFieldId == 0) {  
                //  If no custom field is assigned, assume the whole resource is a TextView  
                text = (TextView) view;  
            } else {  
                //  Otherwise, find the TextView field within the layout  
                text = (TextView) view.findViewById(mFieldId);  
            }  
        } catch (ClassCastException e) {  
            throw new IllegalStateException(  
                    "SearchAdapter requires the resource ID to be a TextView", e);  
        }  
        T item = getItem(position);  
        if (item instanceof CharSequence) {  
            text.setText((CharSequence)item);  
        } else {  
            text.setText(item.toString());  
        }  
        return view;  
    }  

    public void setDropDownViewResource(int resource) {  
        this.mDropDownResource = resource;  
    }  

    @Override  
    public View getDropDownView(int position, View convertView, ViewGroup parent) {  
        return createViewFromResource(position, convertView, parent, mDropDownResource);  
    }  

    public static ArrayAdapter<CharSequence> createFromResource(Context context,  
            int textArrayResId, int textViewResId) {  
        CharSequence[] strings = context.getResources().getTextArray(textArrayResId);  
        return new ArrayAdapter<CharSequence>(context, textViewResId, strings);  
    }  
 
    @Override
    public Filter getFilter() {  
        if (mFilter == null) {  
            mFilter = new SearchFilter();  
        }  
        return mFilter;  
    }  

	private class SearchFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();
			if (mOriginalValues == null) {
				synchronized (mLock) {
					mOriginalValues = new ArrayList<T>(mObjects);
				}
			}
			if (prefix == null || prefix.length() == 0) {
				synchronized (mLock) {
					ArrayList<T> list = new ArrayList<T>(mOriginalValues);
					results.values = list;
					results.count = list.size();
				}
			} else {
				ArrayList<T> values = mOriginalValues;
				int count = values.size();
				ArrayList<T> newValues = new ArrayList<T>(count);
				newValues = (ArrayList<T>) mObjects;
				results.values = newValues;
				results.count = newValues.size();
			}
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			mObjects = (List<T>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}

	}
}
