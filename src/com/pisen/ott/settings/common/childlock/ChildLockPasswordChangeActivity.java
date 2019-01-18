package com.pisen.ott.settings.common.childlock;

import android.content.Intent;
import android.izy.preference.PreferencesUtils;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;
import com.pisen.ott.settings.SettingConfig;
import com.pisen.ott.settings.common.childlock.DealWithKeyEvent.IhandleDifferentAction;

public class ChildLockPasswordChangeActivity extends OTTBaseActivity implements IhandleDifferentAction{
    private EditText currentPassWordEdit ;
    private EditText firstInputPassWordEdit ;
    private EditText confirmPassWordEdit;
    private static String  strInputPassword = "";
    private DealWithKeyEvent handleKeyEvent = null;
    public TextWatcher modifyPassWordWacher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
		
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
	};
 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_ENTER)
		{
			pressOkKey(R.id.edtConfirmPassword);
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 按下ok键
	 */
	public  void pressOkKey(int id)
	{
		if(id == R.id.initialEdit)
		{
			if(strInputPassword.equalsIgnoreCase(PreferencesUtils.getString(SettingConfig.ChildrenPassWordContent, "")))
			{
				
			}else
			{
				Toast.makeText(this, "密码不正确,请从新输入", Toast.LENGTH_SHORT).show();
			}
		}else if(id == R.id.edtPassword)
		{
			
		}else if(id == R.id.edtConfirmPassword)
		{
			
		}
		
		strInputPassword = "";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_childlock_password_change);
		handleKeyEvent = new DealWithKeyEvent(this, this);
		initView();
		setListen();
		
	}


	/**
	 * 初始化视图
	 */
	protected void initView()
	{
		currentPassWordEdit = (EditText)findViewById(R.id.initialEdit);
		currentPassWordEdit.setInputType(InputType.TYPE_NULL);
		firstInputPassWordEdit = (EditText)findViewById(R.id.edtPassword);
		firstInputPassWordEdit.setInputType(InputType.TYPE_NULL);
		confirmPassWordEdit = (EditText)findViewById(R.id.edtConfirmPassword);
		confirmPassWordEdit.setInputType(InputType.TYPE_NULL);
	}
	/**
	 * 设置监听
	 */
	protected void setListen()
	{
		currentPassWordEdit.setOnKeyListener(handleKeyEvent);
		firstInputPassWordEdit.setOnKeyListener(handleKeyEvent);
		confirmPassWordEdit.setOnKeyListener(handleKeyEvent);
		handleKeyEvent.setIsValidateKey(true);
	}
	@Override
	public void handDifferenceAcion(int keyCode, String OutputPassWord) {
		// TODO Auto-generated method stub
		EditText edtInputPassWord = currentPassWordEdit;
		
			  switch(whichFocus())
			    {
			    case 1:
			    	edtInputPassWord = currentPassWordEdit;
			    	break;
			    case 2:
			    	edtInputPassWord = firstInputPassWordEdit;
			    	break;
			    case 3:
			    	edtInputPassWord = confirmPassWordEdit;
			    	break;
			    	default:
			    		break;
			    }
				edtInputPassWord.setText("");
				edtInputPassWord.append(OutputPassWord);
		   
		
	}
	/**
	 * 判断当前那个edittext获取到焦点
	 * @return
	 */
	public int whichFocus()
	{
		if(currentPassWordEdit.isFocused())
		{
		  return 1;
		}else if(firstInputPassWordEdit.isFocused())
		{
			 return 2;
		}else if(confirmPassWordEdit.isFocused())
		{
			
			 return 3;
		}
		 return 3;
	}
	
	
	@Override
	public void finishView() {
		// TODO Auto-generated method stub
		finish();
	}
	@Override
	public void handOkKeyPress() {
		// TODO Auto-generated method stub
		if(currentPassWordEdit.isFocused())
		{
			if(!strInputPassword.equalsIgnoreCase(PreferencesUtils.getString(SettingConfig.ChildrenPassWordContent, "")))
			{
				Toast.makeText(this, "密码不正确,请重新输入", Toast.LENGTH_SHORT).show();
				currentPassWordEdit.setText("");
			}else
			{
				currentPassWordEdit.clearFocus();
				firstInputPassWordEdit.requestFocus();
			}
		}else if(firstInputPassWordEdit.isFocused())
		{
		if(firstInputPassWordEdit.getText().length() < 4)
		{
			Toast.makeText(this, "密码不能少于4位", Toast.LENGTH_SHORT).show();
			return;
		}
			firstInputPassWordEdit.clearFocus();
			confirmPassWordEdit.requestFocus();
		}else if(confirmPassWordEdit.isFocused())
		{
			
			if(firstInputPassWordEdit.getText().toString().equalsIgnoreCase(
					confirmPassWordEdit.getText().toString()))
			{
				PreferencesUtils.setString(SettingConfig.ChildrenPassWordContent, firstInputPassWordEdit.getText().toString());
				Toast.makeText(this, "完成修改密码", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(this, ChildLockActivity.class));
				finish();
			}else
			{
				Toast.makeText(this, "两次输入的密码不匹配", Toast.LENGTH_SHORT).show();
				firstInputPassWordEdit.setText("");
				confirmPassWordEdit.setText("");
				firstInputPassWordEdit.requestFocus();
				confirmPassWordEdit.clearFocus();
			}
		}
	}


	
}
