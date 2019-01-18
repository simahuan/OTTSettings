package com.pisen.ott.settings.appmanage;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * 应用程序信息实体类
 * @author Liuhc
 * @version 1.0 2014年11月21日 下午1:47:57
 */
public class AppInfo {
  
	private String appLabel;    //应用程序标签
	private Drawable appIcon ;  //应用程序图像
	private Intent intent ;     //启动应用程序的Intent ，一般是Action为Main和Category为Lancher的Activity
	private String pkgName ;    //应用程序所对应的包名
	private String appName ;	//应用程序名称
	private int pid;			//pid
	private String processName;	//进程名
	
	public AppInfo(){}
	
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppLabel() {
		return appLabel;
	}
	public void setAppLabel(String appName) {
		this.appLabel = appName;
	}
	public Drawable getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	public Intent getIntent() {
		return intent;
	}
	public void setIntent(Intent intent) {
		this.intent = intent;
	}
	public String getPkgName(){
		return pkgName ;
	}
	public void setPkgName(String pkgName){
		this.pkgName=pkgName ;
	}
}
