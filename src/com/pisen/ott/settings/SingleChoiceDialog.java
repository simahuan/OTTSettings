package com.pisen.ott.settings;

import java.util.List;

import android.content.Context;
import android.izy.widget.BaseListAdapter;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.pisen.ott.OTTDialog;
import com.pisen.ott.widget.FocusListView;

/**
 * 单选窗口
 * 
 * @author yangyp
 * @version 1.0, 2014年12月11日 下午4:57:41
 */
public class SingleChoiceDialog<T> extends OTTDialog implements OnItemClickListener {

	private TextView title;
	private FocusListView list;
	private SingleChoiceAdpater<T> singleChoiceAdpater;
	private OnItemCheckedChangeListener itemCheckedlistener;
	private Handler mHandler = new Handler();
	private Runnable mDelayRunnable;

	public SingleChoiceDialog(Context context) {
		super(context, R.style.AppDialog_Translucent);
		initView(context);
	}

	public void initView(Context context) {
		setContentView(R.layout.ui_dialog);
		title = (TextView) findViewById(android.R.id.title);
		list = (FocusListView) findViewById(android.R.id.list);
		list.setOnItemClickListener(this);
		singleChoiceAdpater = new SingleChoiceAdpater<T>(context);
		list.setAdapter(singleChoiceAdpater);
	}

	@Override
	public void dismiss() {
		mHandler.removeCallbacks(mDelayRunnable);
		super.dismiss();
	}

	@Override
	public void setTitle(CharSequence title) {
		this.title.setText(title);
	}

	/**
	 * 设置列表内容
	 * 
	 * @param items
	 */
	public void setItems(List<T> items) {
		singleChoiceAdpater.setData(items);
	}

	/**
	 * 设置列表内容
	 * 
	 * @param items
	 */
	public void setItems(T... items) {
		singleChoiceAdpater.addAll(items);
	}

	/**
	 * 设置当前选择Item
	 * 
	 * @param itemIndex
	 */
	public void setCheckedItem(int itemIndex) {
		if (itemIndex < 0) {
			itemIndex = 0;
		}
		singleChoiceAdpater.setCheckedItem(itemIndex);
	}

	public void setCheckedItem(String value) {
		setCheckedItem(singleChoiceAdpater.getPosition((T) value));
	}

	public T getCheckedItem(int position) {
		return singleChoiceAdpater.getItem(position);
	}

	/**
	 * 设置监听当前选中的Item
	 * 
	 * @param l
	 */
	public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener l) {
		this.itemCheckedlistener = l;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		singleChoiceAdpater.setCheckedItem(position);
		mHandler.removeCallbacks(mDelayRunnable);
		mHandler.postDelayed(mDelayRunnable = new Runnable() {
			@Override
			public void run() {
				dismiss();
				if (itemCheckedlistener != null) {
					itemCheckedlistener.onItemCheckedChanged(SingleChoiceDialog.this, position);
				}
			}
		}, 250);
		/*
		 * if (itemCheckedlistener != null) {
		 * itemCheckedlistener.onItemCheckedChanged(this, position); }
		 */
	}

	public interface OnItemCheckedChangeListener {
		void onItemCheckedChanged(OTTDialog dialog, int itemIndex);
	}

	static private class SingleChoiceAdpater<T> extends BaseListAdapter<T> {

		private ViewHolder mViewHolder;
		private int mItemIndex;

		public SingleChoiceAdpater(Context context) {
			super(context);
		}

		public void setCheckedItem(int itemIndex) {
			this.mItemIndex = itemIndex;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getContext(), R.layout.ui_dialog_item, null);
				mViewHolder = new ViewHolder();
				mViewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
				mViewHolder.imgCheck = (ImageView) convertView.findViewById(R.id.imgCheck);
				convertView.setTag(mViewHolder);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}

			T item = getItem(position);
			convertView.setBackgroundResource(getViewBgId(position, getCount()));
			mViewHolder.txtTitle.setText(item.toString());
			mViewHolder.imgCheck.setImageResource(mItemIndex == position ? R.drawable.dialog_ic_on : R.drawable.dialog_ic_off);

			return convertView;
		}

		static class ViewHolder {
			TextView txtTitle;
			ImageView imgCheck;
		}

		private static int getViewBgId(int position, int count) {
			int resid = 0;
			if (count == 1) {
				resid = R.drawable.table_round_bottom;
			} else {
				if (position == count - 1) {
					resid = R.drawable.table_round_bottom;
				} else {
					resid = R.drawable.table_round_middle;
				}
			}
			return resid;
		}
	}
}
