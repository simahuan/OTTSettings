package com.pisen.ott;

import android.izy.ApplicationSupport;
import android.izy.app.ActivitySupport;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.pisen.ott.settings.R;

/**
 * 基类activity
 * 
 * @author yangyp
 * @version 1.0, 2014年11月21日 上午11:51:43
 */
public abstract class OTTBaseActivity extends ActivitySupport {

	private ViewGroup mActionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	@Override
	public ApplicationSupport getApplicationContext() {
		return (ApplicationSupport) super.getApplicationContext();
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		initNavigationBarView();
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		initNavigationBarView();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		initNavigationBarView();
	}

	private void initNavigationBarView() {
		mActionBar = (ViewGroup) findViewById(R.id.actionbarLayout);
		if (mActionBar != null) {
			TextView txtTitle = (TextView) mActionBar.findViewById(R.id.txtTitle);
			txtTitle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		setTitle(title, null);
	}

	public void setTitle(CharSequence title, CharSequence subtitle) {
		if (mActionBar != null) {
			TextView txtTitle = (TextView) mActionBar.findViewById(R.id.txtTitle);
			TextView txtSubtitle = (TextView) mActionBar.findViewById(R.id.txtSubtitle);
			txtTitle.setText(title);
			txtSubtitle.setText(subtitle);
		}
	}

	@Override
	public void setTitle(int titleId) {
		setTitle(getText(titleId), null);
	}

	public void setTitle(int titleId, int subtitleId) {
		setTitle(getText(titleId), getText(subtitleId));
	}

}
