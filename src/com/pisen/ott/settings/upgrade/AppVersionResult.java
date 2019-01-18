package com.pisen.ott.settings.upgrade;


/**
 * app版本信息
 * 
 * @author yangyp
 * @version 1.0, 2014年8月1日 下午7:21:12
 */
public class AppVersionResult extends JsonResult {

	public AppVersion AppVersion;

	/**
	 * 判断内容是否为空
	 * 
	 * @return true为空，false不为空
	 */
	public boolean isDataNull() {
		return AppVersion == null;
	}
}
