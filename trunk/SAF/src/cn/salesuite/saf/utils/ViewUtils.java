/**
 * 
 */
package cn.salesuite.saf.utils;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.view.View;

/**
 * @author Tony
 * 
 */
public class ViewUtils {
	
	/**
	 * 设置view组件gone或者visible
	 * 
	 * @param view
	 * @param gone
	 * @return view
	 */
	public static <V extends View> V setGone(final V view, final boolean gone) {
		if (view != null)
			if (gone) {
				if (GONE != view.getVisibility())
					view.setVisibility(GONE);
			} else {
				if (VISIBLE != view.getVisibility())
					view.setVisibility(VISIBLE);
			}
		return view;
	}

	/**
	 * 设置view组件invisible或者visible
	 * 
	 * @param view
	 * @param invisible
	 * @return view
	 */
	public static <V extends View> V setInvisible(final V view,
			final boolean invisible) {
		if (view != null)
			if (invisible) {
				if (INVISIBLE != view.getVisibility())
					view.setVisibility(INVISIBLE);
			} else {
				if (VISIBLE != view.getVisibility())
					view.setVisibility(VISIBLE);
			}
		return view;
	}

}
