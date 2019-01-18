package com.pisen.ott.service;

import android.view.KeyEvent;

/**
 * 遥控服务 手柄遥控与手机遥控连接与控制
 * 
 * @pdOid 3fb791e7-f12e-4f4a-919a-dac938661040
 */
public class RemoteControlService {
	/**
	 * 绑定遥控按键
	 * 
	 * @pdOid eb636799-1ba2-4283-af24-107a83537863
	 */
	public void bindKeyEvent() {
		// TODO: implement
	}

	/**
	 * 接收遥控按键消息
	 * 
	 * @param event
	 * @pdOid e6dc9400-6c45-405c-ae7a-403c262c69b9
	 */
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO: implement
		return true;
	}

	/**
	 * 发送系统按键消息
	 * 
	 * @pdOid 3d7846a4-964c-4ab3-a4cd-94dc793fa833
	 */
	public void sendKeyEvent() {
		// TODO: implement
	}

}
