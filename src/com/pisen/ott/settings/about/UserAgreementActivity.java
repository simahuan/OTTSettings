package com.pisen.ott.settings.about;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;

/**
 * TODO
 * @author  mahuan
 * @date    2014年12月10日 下午3:18:22
 */
public class UserAgreementActivity extends OTTBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_user_agreement);
		setTitle(R.string.about_user_agreement);
		initView();
	}

	private void setTitleInfo(){
		TextView title = (TextView) findViewById(R.id.txtTitle);
		((TextView)findViewById(R.id.txtSubtitle)).setText("");
		title.setText(getString(R.string.about_user_agreement));
		title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	public void initView(){
//		setTitleInfo();
	}
}
