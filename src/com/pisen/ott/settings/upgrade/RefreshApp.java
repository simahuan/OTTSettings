package  com.pisen.ott.settings.upgrade;


import android.content.Context;
import android.izy.util.parse.GsonUtils;
import android.util.Log;

import com.pisen.ott.settings.util.HttpGetRequest;
import com.pisen.ott.settings.util.HttpKeys;
import com.pisen.ott.settings.util.HttpManager.OnHttpCallBack;

/**
 * 更新App
 * 
 * @author MouJunFeng
 * @version 1.0, 2014-6-4 下午3:13:12
 */
public class RefreshApp {
	// 当前环境
	private Context ctx;

	public RefreshApp(Context ctx) {
		this.ctx = ctx;
	}

	/**
	 * 更新
	 * 
	 * @param back
	 *            返回数据的接口
	 * @param isShow
	 *            是否显示提示框
	 */
	public void refresh(final boolean isShow, final RefreshAppCallBack back) {
		if (this.ctx != null) {
			HttpGetRequest getRequest = new HttpGetRequest(this.ctx);
			getRequest.setDialogHide();
			getRequest.execute(HttpKeys.REFRESH_APP, "", new OnHttpCallBack() {
				@Override
				public void getHttpResult(String result) {
					AppVersionResult jsonResult = GsonUtils.jsonDeserializer(result, AppVersionResult.class);
					if (jsonResult == null || jsonResult.isDataNull()) {
						back.callBack(result);
						return;
					}
					AppVersion appVersion = jsonResult.AppVersion;
					if (isShow || appVersion.IsAutoUpgrade == 1) {
						try {
							back.downLoad(appVersion);
						} catch (Exception e) {
							back.callBack(result);
							Log.w("RefreshApp", "版本更新下载出错: refresh -> getHttpResult(String)");
						}
					}
				}
			});
		}
	}

	/**
	 * 软件更新返回数据接口
	 * 
	 * @author MouJunFeng
	 * @version 1.0, 2014-7-24 下午3:13:12
	 */
	public interface RefreshAppCallBack {
		/**
		 * 出现错误时候
		 * 
		 * @param result
		 */
		void callBack(String result);

		/**
		 * 获得数据的时候
		 * 
		 * @param apkUrl
		 * @param HttpVersion
		 * @throws NumberFormatException
		 * @throws Exception
		 */
		void downLoad(AppVersion app) throws NumberFormatException, Exception;
	}

}
