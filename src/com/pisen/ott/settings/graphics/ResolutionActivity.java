package com.pisen.ott.settings.graphics;

import android.content.Context;
import android.izy.widget.BaseListAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;
import com.pisen.ott.settings.SettingConfig;
import com.pisen.ott.settings.util.ViewHelper;
import com.pisen.ott.widget.FocusListView;

/**
 * 分辨率设置UI
 * 
 * @author Liuhc
 * @version 1.0 2014年12月10日15:21:55
 */
public class ResolutionActivity extends OTTBaseActivity implements OnItemClickListener {

	private ResolutionAdapter resolutionAdapter;
	private FocusListView lstResolution;
	private int checkedPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graphics_resolution);
		setTitle(R.string.graphics_resolution, R.string.graphics_resolution_describle);

		lstResolution = (FocusListView) findViewById(R.id.lstResolution);
		resolutionAdapter = new ResolutionAdapter(this);
		lstResolution.setAdapter(resolutionAdapter);
		lstResolution.setOnItemClickListener(this);
		resolutionAdapter.setData(SettingConfig.getResolutionList(this));
		resolutionAdapter.setCheckedItem(getIndex(SettingConfig.getResolution()));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position != checkedPosition) {
			resolutionAdapter.setCheckedItem(position);
			checkedPosition = position;
			SettingConfig.setResolution(resolutionAdapter.getItem(position).toString());
			ResolutionActivity.this.finish();
		}
	}

	
	private int getIndex(String value){
		return resolutionAdapter.getPosition(value);
	}
	
	class ResolutionAdapter extends BaseListAdapter<Object> {

		private int itemIndex;

		public ResolutionAdapter(Context context) {
			super(context);
		}

		public void setCheckedItem(int itemIndex) {
			this.itemIndex = itemIndex;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.ui_dialog_item, parent, false);
			}

			TextView ivName = ViewHelper.getView(convertView, R.id.txtTitle);
			ImageView ivStatu = ViewHelper.getView(convertView, R.id.imgCheck);

			ivName.setText(getItem(position).toString());
			ivStatu.setImageResource(itemIndex == position ? R.drawable.dialog_ic_on : R.drawable.dialog_ic_off);
			convertView.setBackgroundResource(getViewBgId(position, getCount()));
			return convertView;
		}

		private int getViewBgId(int position, int count) {
			int resid = 0;
			if (count == 1) {
				resid = R.drawable.table_round_single;
			} else {
				if (position == 0) {
					resid = R.drawable.table_round_top;
				} else if (position == (count - 1)) {
					resid = R.drawable.table_round_bottom;
				} else {
					resid = R.drawable.table_round_middle;
				}
			}
			return resid;
		}
	}
}
