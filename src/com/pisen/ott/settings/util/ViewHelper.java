package com.pisen.ott.settings.util;

import android.util.SparseArray;
import android.view.View;

/**
 * 数据适配器帮助类
 * @author Liuhc
 * @version 1.0 2014年11月21日 上午11:06:46
 */
public class ViewHelper {

	/**
	 * ListView使用ViewHolder极简写法(可用于findViewById)
	 * @param convertView
	 * @param id 控件id
	 * @return 返回当前控件对象
	 */
	public static <T extends View> T getView(View convertView, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            convertView.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = convertView.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
