package com.pisen.ott.settings.about;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;
import com.pisen.ott.widget.FocusLinearLayout;
import com.pisen.ott.widget.FocusLinearLayout.OnItemClickFocusListener;

/**
 * TODO
 * @author  mahuan
 * @date    2014年12月10日 下午3:18:22
 */
public class LegalInfosActivity extends OTTBaseActivity implements OnItemClickFocusListener{
	private FocusLinearLayout legalInfosLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_legal_infos);
		initView();
	}
	
	public void initView(){
		   	setTitle(R.string.about_legal_information);
		   	legalInfosLayout = (FocusLinearLayout) findViewById(R.id.legalInfosLayout);
		   	legalInfosLayout.setOnItemClickListener(this);
//		    findViewById(R.id.lrel_user_agreement).setOnClickListener(this);
	}

	@Override
	public void onItemClick(View v) {
		switch (v.getId()) {
		case R.id.lrel_user_agreement:
			LegalInfosActivity.this.startActivity(new Intent(LegalInfosActivity.this,UserAgreementActivity.class));
			break;
		default:
			break;
		}
	}
}
