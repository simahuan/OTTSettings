package com.pisen.ott;

import android.content.Context;
import android.izy.app.dialog.BlurDialog;
import android.os.Bundle;

/**
 * 弹出窗口基类
 * 
 * @author yangyp
 * @version 1.0, 2014年12月24日 下午1:46:04
 */
public abstract class OTTDialog extends BlurDialog {

	public OTTDialog(Context context) {
		super(context);
	}

	public OTTDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBlurRadius(6);
		setDownScaleFactor(3f);
	}
}
