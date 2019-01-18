package com.pisen.ott.settings.upgrade;


import java.io.Serializable;

/**
 * json抽象类
 * 
 * @author yangyp
 * @version 1.0, 2014-7-3 下午7:40:34
 */
public class JsonResult implements Serializable {

	private static final long serialVersionUID = 1L;
	public String ErrCode;
	public String ErrMsg;
	public boolean IsError;
}
