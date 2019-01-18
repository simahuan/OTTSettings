package com.pisen.ott.settings.common;

import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
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
 * TODO
 * 
 * @author mahuan
 * @date 2014年12月10日 下午4:02:24
 */
public class ScreenProActivity extends OTTBaseActivity implements OnItemClickFocusListener, OnItemKeyFocusListener {
	private final String TAG = "ScreenProActivity";
	private static final String[] TIME_ITEM = new String[] { SettingConfig.ScreenProTimeDefault, SettingConfig.OneMinutes, SettingConfig.FourMinutes,
			SettingConfig.EightMinutes };
	private TextView txt_screenpro_value;
	private FocusLinearLayout screenProtocolLayout;
	SingleChoiceDialog<String> dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_screen_protocol);
		// 这里可以首次拿到休眠设置的时间值
		try {
			Log.d(TAG,
					"android.provider.Settings.System.SCREEN_OFF_TIMEOUT=:"
							+ Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT));
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		initView();
	}

	public void initView() {
		setTitle(R.string.common_screen_protocol_title, R.string.common_screen_protocol_subtitle);
		screenProtocolLayout = (FocusLinearLayout) findViewById(R.id.screenProtocolLayout);
		screenProtocolLayout.setOnItemClickListener(this);
		screenProtocolLayout.setOnItemKeyListener(this);

		txt_screenpro_value = (TextView) findViewById(R.id.txt_screenpro_value);
		txt_screenpro_value.setText(SettingConfig.getScreenPro());
	}

	@Override
	public void onItemClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.lrel_screenpro_time:
			SingleChoiceDialogScreenPro();
			break;
		default:
			break;
		}
	}

	/*
	 * 屏保设置
	 */
	public void SingleChoiceDialogScreenPro() {
		dialog = new SingleChoiceDialog<String>(this);
		dialog.setTitle("屏保时间");
		dialog.setItems(TIME_ITEM);
		dialog.setCheckedItem(SettingConfig.getScreenProIndex());
		dialog.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
			@Override
			public void onItemCheckedChanged(OTTDialog dialog, int whichItem) {
				 SettingConfig.setScreensaverTime(getTimeOutMillisecond(whichItem));
				//Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, 500);
				SettingConfig.setScreenProIndex(whichItem);
				SettingConfig.setScreenPro(TIME_ITEM[whichItem]);
				txt_screenpro_value.setText(TIME_ITEM[whichItem]);
			}
		});
		dialog.show();
	}

	public int getTimeOutMillisecond(int whichItem) {
		int tempTime = 0;
		switch (whichItem) {
		case 0:
			tempTime = SettingConfig.ScreenProTimeDefMill;
			break;
		case 1:
			tempTime = SettingConfig.OneMinutesMill;
			break;
		case 2:
			tempTime = SettingConfig.FourMinutesMill;
			break;
		case 3:
			tempTime = SettingConfig.EightMinutesMill;
			break;
		}
		return tempTime;
	}

	@Override
	public void onItemKeyLeft(View v, KeyEvent event) {
		switch (v.getId()) {
		case R.id.lrel_screenpro_time:
			txt_screenpro_value.setText(getPreviousItem(TIME_ITEM, txt_screenpro_value.getText().toString()));
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemKeyRight(View v, KeyEvent event) {
		switch (v.getId()) {
		case R.id.lrel_screenpro_time:
			txt_screenpro_value.setText(getNextItem(TIME_ITEM, txt_screenpro_value.getText().toString()));
			break;
		default:
			break;
		}
	}

	/**
	 * 上一项内容
	 */
	private String getPreviousItem(String[] values, String item) {
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(item)) {
				if (i == 0) {
					SettingConfig.setScreenProIndex(values.length - 1);
					SettingConfig.setScreenPro(TIME_ITEM[values.length - 1]);
					return values[values.length - 1];
				} else {
					SettingConfig.setScreenProIndex(i - 1);
					SettingConfig.setScreenPro(TIME_ITEM[i - 1]);
					return values[i - 1];
				}
			}
		}
		return item;
	}

	/**
	 * 下一项内容
	 */
	private String getNextItem(String[] values, String item) {
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(item)) {
				if (i == (values.length - 1)) {
					SettingConfig.setScreenProIndex(0);
					SettingConfig.setScreenPro(TIME_ITEM[0]);
					return values[0];
				} else {
					SettingConfig.setScreenProIndex(i + 1);
					SettingConfig.setScreenPro(TIME_ITEM[i + 1]);
					return values[i + 1];
				}
			}
		}
		return item;
	}
}
