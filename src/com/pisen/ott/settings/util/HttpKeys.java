package com.pisen.ott.settings.util;

/**
 * http 请求地址
 * 
 * @author MouJunFeng
 * @version 1.0, 2014-6-16 下午2:42:36
 */
public class HttpKeys {

	public static final String BASE_URL = "http://soa.pisen.com.cn";
	/**
	 * 启动页广告图片地址
	 */
	public static final String START_BANNER_URL = BASE_URL + "/adsense/AdsenseService.svc/rest/GetAdvertises?code=A00001";
	/**
	 * 主界面广告图片地址
	 */
	public static final String MAIN_BANNER_URL = BASE_URL + "/adsense/AdsenseService.svc/rest/GetAdvertises?code=I00002";

	/**
	 * 软件更新
	 */
	public static final String REFRESH_APP = "";//BASE_URL + "/platform.app/AppVersionService.svc/rest/GetLatestVersion?AppKey=13602889";
	/**
	 * 登录
	 */
	public static final String USER_LOGIN = "http://sso.pisen.com.cn/api/Token/EncryptRequestToken";

	/**
	 * 帮助
	 */
	public static final String HELP_URL = "http://cloud.pisen.com.cn/route/CloudService.svc/GetDocuments?categoryCode=D00001";
	public static final String HELP_WEBPAGE_URL = "http://cloud.pisen.com.cn/mobile/Help/HelpDetail/%s";
	/**
	 * 意见反馈反馈
	 */
	public static final String FEED_BACK_URL = "http://cloud.pisen.com.cn/route/CloudService.svc/AddFeedback";
}
