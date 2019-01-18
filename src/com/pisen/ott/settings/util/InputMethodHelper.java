package com.pisen.ott.settings.util;

import java.util.List;

import android.content.Context;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

/**
 * 输入法 工具类
 * @author  fq
 * @date    2014年12月16日 上午10:47:38
 */
public class InputMethodHelper {
	private static final Exception Exception = null;
	public InputMethodManager imm;
	public String defaultInputMethodId;
	public String defaultInputMethodName;
	public List<InputMethodInfo> list;
	public String[] inputMethodNames ;
	/**
	 * 构造函数
	 */
	public InputMethodHelper(Context context){
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		defaultInputMethodId= Settings.Secure.getString(context.getContentResolver(),
	            Settings.Secure.DEFAULT_INPUT_METHOD);
		
		list = imm.getEnabledInputMethodList();
		defaultInputMethodName =(String) list.get(getItemId(defaultInputMethodId)).loadLabel(context.getPackageManager());
		//构建输入法名称数组
		inputMethodNames = new String[list.size()];
		for(int i=0;i<list.size();i++){
            CharSequence label = list.get(i).loadLabel(context.getPackageManager());
			inputMethodNames[i]= (String) label;		
		}
	}
    
	public  int getItemId(String inputMethodId){
		for(int i=0;i<list.size();i++){
            if(list.get(i).getId().equals(inputMethodId)){
            	return i;
            }		
		}
		return -1;
	}
	
	public int getArrayIndex(String inputMethodName){
		for(int i=0;i<inputMethodNames.length;i++){
			if(inputMethodNames[i].equals(inputMethodName)){
				return i;
			}
		}
		return -1;		
	}
}
