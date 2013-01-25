/**
 * 
 */
package cn.salesuite.saf.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 封装图片和文字的Adapter<br>
 * 使用方法：<br>
 * <pre>
 * <code>
 *  IconSimpleAdapter adapter = new IconSimpleAdapter(ListClusteredPin.this, data,
 *     R.layout.list_poi_row, new String[] { "ic_red_circle","name","address","distance","poi_bearing"},
 *     new int[] {R.id.ic_red_circle,R.id.name, R.id.address, R.id.distance, R.id.poi_bearing});
 *  listView.setAdapter(adapter);
 *  </code>
 *  </pre>
 * @author Tony Shen
 *
 */
public class IconSimpleAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private List<Map<String, Object>> mList;
	private String[] mKeys;
	private int mResource;
	private int[] mViews;

	/**
	 * 构造IconSimpleAdapter
	 * @param context
	 * @param list
	 * @param resource
	 * @param keys
	 * @param views
	 */
	public IconSimpleAdapter(Context context, List<Map<String, Object>> list, int resource, String[] keys, int[] views) {
		mInflater = LayoutInflater.from(context);
		mList = list;
		mKeys = keys;
		mResource = resource;
		mViews = views;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		
		Map<String, Object> map = mList.get(position);
		convertView = mInflater.inflate(mResource, null);
			
		for(int i=0;i<mViews.length;i++){	
			View uiElement = convertView.findViewById(mViews[i]);
			if(uiElement instanceof ImageView){
				if (map.get(mKeys[i])!=null) {
					if(map.get(mKeys[i]) instanceof Bitmap){
						((ImageView)uiElement).setImageBitmap((Bitmap)map.get(mKeys[i]));
					}else{
						int icon = Integer.parseInt(map.get(mKeys[i]).toString());
						if(icon == 0){
							((ImageView)uiElement).setVisibility(View.GONE);
						}else{
							((ImageView)uiElement).setImageResource(icon);
						}
					}
				}
			}else if(uiElement instanceof TextView){
				if (map.get(mKeys[i]) != null) {
					if(map.get(mKeys[i]).toString().length() == 0) {
						((TextView)uiElement).setVisibility(View.GONE);
					}
					((TextView)uiElement).setText(map.get(mKeys[i]).toString());
				}
			}
		}
			
		return convertView;
	}
	
	@Override  
    public void notifyDataSetChanged() {  
        super.notifyDataSetChanged();  
    }  

}
