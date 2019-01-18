package com.pisen.ott.settings.graphics;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.OTTDialog;
import com.pisen.ott.settings.R;
import com.pisen.ott.settings.SettingConfig;
import com.pisen.ott.settings.SingleChoiceDialog;
import com.pisen.ott.settings.SingleChoiceDialog.OnItemCheckedChangeListener;
import com.pisen.ott.widget.FocusLinearLayout;
import com.pisen.ott.widget.FocusLinearLayout.OnItemClickFocusListener;
import com.pisen.ott.widget.FocusLinearLayout.OnItemKeyFocusListener;

/**
 * 图像与声音UI
 * @author Liuhc
 * @version 1.0 2014年12月9日 下午2:44:24
 */
public class GraphicsSettingsActivity extends OTTBaseActivity implements OnItemClickFocusListener,OnItemKeyFocusListener{

	private TextView txtResolution;
	private TextView txtVoiceOutput;
	private TextView txtCompression;
	
	private String[] voices = null;
	private String[] compressions = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graphics);
		setTitle(R.string.graphics_title, R.string.graphics_title_describle);
		((FocusLinearLayout) findViewById(R.id.graphicsLayout)).setOnItemClickListener(this);
		((FocusLinearLayout) findViewById(R.id.graphicsLayout)).setOnItemKeyListener(this);
		
		txtResolution = (TextView) findViewById(R.id.txtResolution);
		txtVoiceOutput = (TextView) findViewById(R.id.txtZoom);
		txtCompression = (TextView) findViewById(R.id.txtCompression);
		
		txtVoiceOutput.setText(SettingConfig.getAudioOutput());
		txtCompression.setText(SettingConfig.getCompression());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		txtResolution.setText(SettingConfig.getResolution());
	}

	@Override
	public void onItemClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.resolutionLayout:
			startActivity(new Intent(this, ResolutionActivity.class));
			break;
		case R.id.zoomDisplacementLayout:
			startActivity(new Intent(this, ZoomDisplacementActivity.class));
			break;
		case R.id.voiceOutputLayout:
			showVoiceOutputDialog();
			break;
		case R.id.compressionLayout:
			showCompDialog();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onItemKeyLeft(View v, KeyEvent event) {
		switch (v.getId()) {
		case R.id.voiceOutputLayout:
			if (voices == null) {
				voices = SettingConfig.getAudioOutputList(this);
			}
			txtVoiceOutput.setText(getPreviousItem(voices, txtVoiceOutput.getText().toString()));
			break;
		case R.id.compressionLayout:
			if (compressions == null) {
				compressions = SettingConfig.getCompressionList(this);
			}
			txtCompression.setText(getPreviousItem(compressions, txtCompression.getText().toString()));
			break;
		default:
			break;
		}		
	}

	
	@Override
	public void onItemKeyRight(View v, KeyEvent event) {
		switch (v.getId()) {
		case R.id.voiceOutputLayout:
			if (voices == null) {
				voices = SettingConfig.getAudioOutputList(this);
			}
			txtVoiceOutput.setText(getNextItem(voices, txtVoiceOutput.getText().toString()));
			break;
		case R.id.compressionLayout:
			if (compressions == null) {
				compressions = SettingConfig.getCompressionList(this);
			}
			txtCompression.setText(getNextItem(compressions, txtCompression.getText().toString()));
			break;
		default:
			break;
		}			
	}
	
	/**
	 * 获取上一项内容
	 * @param values
	 * @param item
	 * @return
	 */
	private String getPreviousItem(String[] values,String item){
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(item)) {
				if (i == 0)
					return values[values.length-1];
				else
					return values[i-1];
			}
		}
		return item;
	}
	
	/**
	 * 获取下一项内容
	 * @param values
	 * @param item
	 * @return
	 */
	private String getNextItem(String[] values,String item){
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(item)) {
				if (i == (values.length - 1))
					return values[0];
				else
					return values[i+1];
			}
		}
		return item;
	}
	
	/**
	 * 声音输出对话框
	 */
	private void showVoiceOutputDialog(){
		final SingleChoiceDialog<Object> voiceDialog = new SingleChoiceDialog<Object>(this);
		voiceDialog.setTitle(getString(R.string.graphics_voice_output));
		voiceDialog.setItems(SettingConfig.getAudioOutputList(GraphicsSettingsActivity.this));
		voiceDialog.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
			@Override
			public void onItemCheckedChanged(OTTDialog dialog, int itemIndex) {
				SettingConfig.setAudioOutput(voiceDialog.getCheckedItem(itemIndex).toString());
				txtVoiceOutput.setText(SettingConfig.getAudioOutput());
			}
		});
		voiceDialog.setCheckedItem(SettingConfig.getAudioOutput());
		voiceDialog.show();
	}
	
	/**
	 * 压缩对话框
	 */
	private void showCompDialog(){
		final SingleChoiceDialog<Object> compDialog = new SingleChoiceDialog<Object>(this);
		compDialog.setTitle(getString(R.string.graphics_compression));
		compDialog.setItems(SettingConfig.getCompressionList(GraphicsSettingsActivity.this));
		compDialog.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
			@Override
			public void onItemCheckedChanged(OTTDialog dialog, int itemIndex) {
				SettingConfig.setCompression(compDialog.getCheckedItem(itemIndex).toString());
				txtCompression.setText(SettingConfig.getCompression());
			}
		});
		compDialog.setCheckedItem(SettingConfig.getCompression());
		compDialog.show();
	}
	
}
