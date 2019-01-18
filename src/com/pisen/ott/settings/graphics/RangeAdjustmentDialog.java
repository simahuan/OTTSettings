package com.pisen.ott.settings.graphics;

import android.content.Context;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.pisen.ott.OTTDialog;
import com.pisen.ott.settings.R;
import com.pisen.ott.settings.SettingConfig;

/**
 * 范围调整窗口
 * 
 * @author yangyp
 * @version 1.0, 2014年12月12日 上午9:05:16
 */
public class RangeAdjustmentDialog extends OTTDialog implements OnSeekBarChangeListener {

	private TextView txtTitle;
	private TextView txtMin;
	private SeekBar sekRange;
	private OnRangeAdjustmentChangeListener rangeAdjustmentChangeListener;

	public RangeAdjustmentDialog(Context context) {
		super(context, R.style.AppDialog_Translucent);
		initViews(context);
	}

	private void initViews(Context context) {
		setContentView(R.layout.ui_dialog_range_adjustment);
		txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtMin = (TextView) findViewById(R.id.txtMin);
		sekRange = (SeekBar) findViewById(R.id.sekRange);
		sekRange.setOnSeekBarChangeListener(this);
		sekRange.setProgress(SettingConfig.getScreenZoom());
	}

	public void setOnRangeAdjustmentChangeListener(OnRangeAdjustmentChangeListener l) {
		this.rangeAdjustmentChangeListener = l;
	}

	@Override
	public void setTitle(CharSequence title) {
		txtTitle.setText(title);
	}

	@Override
	public void setTitle(int titleId) {
		setTitle(getContext().getText(titleId));
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (progress == 0){
			txtMin.setText(progress + "");
		}else{
			txtMin.setText(progress + "%");
		}
		if (rangeAdjustmentChangeListener != null) {
			rangeAdjustmentChangeListener.onRangeAdjustmentChange(this, progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (rangeAdjustmentChangeListener != null) {
			rangeAdjustmentChangeListener.onRangeAdjustmentChange(this, seekBar.getProgress());
		}
	}

	/**
	 * 范围值变化监听
	 * 
	 * @author yangyp
	 * @version 1.0, 2014年12月12日 上午11:34:03
	 */
	public interface OnRangeAdjustmentChangeListener {

		/**
		 * 范围监听回调
		 * 
		 * @param dialog
		 * @param progress
		 */
		void onRangeAdjustmentChange(OTTDialog dialog, int progress);
	}
}
