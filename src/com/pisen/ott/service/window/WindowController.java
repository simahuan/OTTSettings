package com.pisen.ott.service.window;

/**
 * 智能窗口管理接口
 * 
 * @author yangyp
 * @version 1.0, 2014-6-26 下午12:06:22
 */
public interface WindowController {

	/**
	 * 显示窗口
	 */
	// void show();

	/**
	 * 退出窗口
	 */
	void hide();

	/**
	 * 把事务添加到一个后退栈中，在用户按下返回键时，返回之前状态
	 * 
	 * @param view
	 */
	void addToBackStack(WindowViewBase view);

	/**
	 * 弹出当前视图控制器
	 */
	void popBackStack();

	/**
	 * 弹出到指定视图控制器
	 */
	// void popBackStack(WindowViewBase view);
}
