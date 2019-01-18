package  com.pisen.ott.settings.upgrade;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.pisen.ott.settings.upgrade.RefreshApp.RefreshAppCallBack;

/**
 * App更新服务
 * 
 * @author MouJunFeng
 * @version 1.0, 2014-6-27 下午5:22:29
 */
public class RefreshAppService extends Service {
	// 绑定服务的binder
	private MyBinder binder = new MyBinder();
	// 更新的返回接口
	private RefreshAppCallBack back;
	// 是否显示提示框
	private boolean isShow;

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	/**
	 * 软件更新
	 */
	public void refresh() {
		RefreshApp app = new RefreshApp(this);
		if (back != null) {
			app.refresh(RefreshAppService.this.isShow, back);
		}
	}

	/**
	 * 绑定service的binder 实现通讯
	 * 
	 * @author MouJunFeng
	 * @version 1.0, 2014-7-24 下午3:22:29
	 * 
	 */
	public class MyBinder extends Binder {
		/**
		 * 获得RefreshAppService实例
		 * 
		 * @return RefreshAppService
		 */
		public RefreshAppService getRefreshAppService() {
			return RefreshAppService.this;
		}

		/**
		 * 获得数据接口
		 * 
		 * @param back
		 *            RefreshAppCallBack
		 */
		public void setRefreshAppCallBack(RefreshAppCallBack back) {
			RefreshAppService.this.back = back;
		}

		/**
		 * 是否显示提示框
		 * 
		 * @param show
		 *            boolean
		 */
		public void setIsShow(boolean show) {
			RefreshAppService.this.isShow = show;
		}
	}

}
