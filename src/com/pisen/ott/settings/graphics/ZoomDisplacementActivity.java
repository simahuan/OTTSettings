package com.pisen.ott.settings.graphics;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.OTTDialog;
import com.pisen.ott.settings.R;
import com.pisen.ott.settings.SettingConfig;
import com.pisen.ott.settings.graphics.RangeAdjustmentDialog.OnRangeAdjustmentChangeListener;
import com.pisen.ott.widget.FocusLinearLayout;
import com.pisen.ott.widget.FocusLinearLayout.OnItemClickFocusListener;
import com.pisen.ott.widget.FocusLinearLayout.OnItemKeyFocusListener;

/**
 * 缩放与位移
 * @author Liuhc
 * @version 1.0 2014年12月11日10:35:16
 */
public class ZoomDisplacementActivity extends OTTBaseActivity implements OnItemClickFocusListener,OnItemKeyFocusListener{

	private TextView txtZoom;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graphics_zoomdisp);
		setTitle(R.string.graphics_zoom_displacement, R.string.graphics_zoom_displacement_des);
		
		FocusLinearLayout layout = (FocusLinearLayout) findViewById(R.id.zoomdispLayout);
		layout.setOnItemClickListener(this);
		layout.setOnItemKeyListener(this);
		
		txtZoom = (TextView) findViewById(R.id.txtZoom);
		txtZoom.setText(SettingConfig.getScreenZoom()+"%");
	}
	
	@Override
	public void onItemClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.zoomLayout:
			showAdjustDialog();
			break;
		case R.id.displacementLayout:
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onItemKeyLeft(View v, KeyEvent event) {
		switch (v.getId()) {
		case R.id.zoomLayout:
			int progress = SettingConfig.getScreenZoom();
			if (progress > 0) {
				--progress;
				SettingConfig.setScreenZoom(progress);
				txtZoom.setText(SettingConfig.getScreenZoom()+"%");
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemKeyRight(View v, KeyEvent event) {
		switch (v.getId()) {
		case R.id.zoomLayout:
			int progress = SettingConfig.getScreenZoom();
			if (progress < 100) {
				++progress;
				SettingConfig.setScreenZoom(progress);
				txtZoom.setText(SettingConfig.getScreenZoom()+"%");
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 缩放对话框
	 */
	private void showAdjustDialog(){
		RangeAdjustmentDialog dialog = new RangeAdjustmentDialog(ZoomDisplacementActivity.this);
		dialog.setTitle(R.string.graphics_zoom_content);
		dialog.setOnRangeAdjustmentChangeListener(new OnRangeAdjustmentChangeListener() {
			@Override
			public void onRangeAdjustmentChange(OTTDialog dialog, int progress) {
				SettingConfig.setScreenZoom(progress);
				txtZoom.setText(SettingConfig.getScreenZoom()+"%");
			}
		});
		dialog.show();
	}
}
