package com.pisen.ott.settings.util;

import android.content.Context;

/**
 * http 管理类
 * 
 * @author MouJunFeng
 * @version 1.0, 2014-6-16 上午9:20:38
 */
public abstract class HttpManager {
	/** 路由,广告,当日达,品友会,连接超时 */
	public static final int WEBWIEW_CONNECT_TIME_OUT = 5000;
	/** 连接超时 */
	protected final int CONNECT_TIME_OUT = 10000;
	/** 读取超时 */
	protected final int READ_TIME_OUT = 10000;
	// 当前环境
	private Context ctx;
//	// 提示对话框（数据加载对话框）
//	public CustomProgressDialog myDialog;
	// 获得返回数据的接口
	private OnHttpCallBack result;
	// 判断当前是否显示和隐藏提示框
	private boolean visible;

	public HttpManager(Context ctx) {
		this.ctx = ctx;
	}

	public Context getCtx() {
		return ctx;
	}

	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}

	public OnHttpCallBack getResult() {
		return result;
	}

	public void setResult(OnHttpCallBack result) {
		this.result = result;
	}

	public void setDialogShow() {
		this.visible = true;
	}

	public void setDialogHide() {
		this.visible = false;
	}

	/**
	 * 
	 * 显示提示框
	 * 
	 * @param title
	 *            标题
	 * @param message
	 *            内容
	 */
	public void showDialog(String title, String message) {
		if (ctx != null && visible) {
//			myDialog = ViewEffect.createQueryTipDialog(ctx, message, null);
//			myDialog.show();
		}
	}

	/**
	 * 
	 * 关闭提示框
	 */
	public void closeDialog() {
//		if (myDialog != null && ctx != null && visible) {
//			myDialog.dismiss();
//		}
	}

	/**
	 * 返回数据的接口
	 * 
	 * @author MouJunFeng
	 * @version 1.0 2014-5-26 上午9:12:47
	 */
	public interface OnHttpCallBack {
		void getHttpResult(String result);
	}
}
