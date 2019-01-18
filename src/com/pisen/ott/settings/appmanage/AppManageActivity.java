package com.pisen.ott.settings.appmanage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.izy.util.LogUtils;
import android.izy.util.StringUtils;
import android.izy.widget.BaseListAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.pisen.ott.settings.R;
import com.pisen.ott.settings.util.KeyUtils;
import com.pisen.ott.settings.util.ViewHelper;

/**
 * 管理应用程序
 * @author Liuhc
 * @version 1.0 2014年11月20日 上午10:53:38
 */
public class AppManageActivity extends Activity implements OnCheckedChangeListener {

	private GridView gridApp;
	private ProgressBar progressLoading;
	private AppAdpater appAdpater;
	private List<AppInfo> appInfos = new ArrayList<AppInfo>();

	private enum AppFilter {
		down, running, all
	};

	private PackageManager packageMgr;
	private AppFilter appFilter = AppFilter.down;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appmanage);

		RadioGroup rg = (RadioGroup) findViewById(R.id.grdoMenu);
		rg.setOnCheckedChangeListener(this);

		gridApp = (GridView) findViewById(R.id.gridApp);
		progressLoading = (ProgressBar) findViewById(R.id.progressLoading);
		packageMgr = AppManageActivity.this.getPackageManager();

		appAdpater = new AppAdpater(this);
		gridApp.setAdapter(appAdpater);
		new AppInfoAsyncTask().execute();
	}
	
	/**
	 * 根据条件查询应用程序信息
	 * @param filter
	 * @return
	 */
	private List<AppInfo> queryFilterAppInfo(AppFilter filter) {

		List<ApplicationInfo> listAppcations = packageMgr.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(packageMgr));

		if (appInfos == null) {
			appInfos = new ArrayList<AppInfo>();
		}
		appInfos.clear();

		if (filter == null || filter == AppFilter.down) {
			for (ApplicationInfo app : listAppcations) {
				// 非系统程序
				if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
					appInfos.add(turn2AppInfo(app,-1,null));
				}
				// 本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
				else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
					appInfos.add(turn2AppInfo(app,-1,null));
				}
			}
		} else if (filter == AppFilter.running) {
			 return queryAllRunningAppInfo(listAppcations);
		} else if (filter == AppFilter.all) {
			for (ApplicationInfo app : listAppcations) {
				appInfos.add(turn2AppInfo(app,-1,null));
			}
		}
		return appInfos;
	}

	/**
	 * 获取正在所有正在运行的应用程序
	 * @param listAppcations
	 * @return
	 */
	private List<AppInfo> queryAllRunningAppInfo(List<ApplicationInfo> listAppcations) {
		// 保存所有正在运行的包名 以及它所在的进程信息
		Map<String, RunningAppProcessInfo> pgkProcessAppMap 
			= new HashMap<String, RunningAppProcessInfo>();

		ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcessList = mActivityManager.getRunningAppProcesses();

		for (RunningAppProcessInfo appProcess : appProcessList) {
			//int pid = appProcess.pid; // pid
			//String processName = appProcess.processName; // 进程名
			String[] pkgNameList = appProcess.pkgList; // 获得运行在该进程里的所有应用程序包

			for (int i = 0; i < pkgNameList.length; i++) {
				String pkgName = pkgNameList[i];
				pgkProcessAppMap.put(pkgName, appProcess);
			}
		}
		// 保存所有正在运行的应用程序信息
		List<AppInfo> runningAppInfos = new ArrayList<AppInfo>();
		for (ApplicationInfo app : listAppcations) {
			if (pgkProcessAppMap.containsKey(app.packageName)) {
				// 获得该packageName的 pid 和 processName
				int pid = pgkProcessAppMap.get(app.packageName).pid;
				String processName = pgkProcessAppMap.get(app.packageName).processName;
				runningAppInfos.add(turn2AppInfo(app, pid, processName));
			}
		}
		return runningAppInfos;

	}

	// 构造一个AppInfo对象 ，并赋值
	private AppInfo turn2AppInfo(ApplicationInfo app, int pid, String processName) {
		AppInfo appInfo = new AppInfo();
		appInfo.setAppLabel((String) app.loadLabel(packageMgr));
		appInfo.setAppIcon(app.loadIcon(packageMgr));
		appInfo.setPkgName(app.packageName);
		appInfo.setAppName(app.loadLabel(packageMgr).toString());
		if (pid > 0) {
			appInfo.setPid(pid);
		}
		if (!StringUtils.isEmpty(processName)) {
			appInfo.setProcessName(processName);
		}
		return appInfo;
	}

	public class AppInfoAsyncTask extends AsyncTask<Void, Integer, List<AppInfo>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressLoading.setVisibility(View.VISIBLE);
			gridApp.setVisibility(View.INVISIBLE);
		}

		@Override
		protected List<AppInfo> doInBackground(Void... params) {
			return queryFilterAppInfo(appFilter);
		}

		@Override
		protected void onPostExecute(List<AppInfo> result) {
			super.onPostExecute(result);
			LogUtils.i(KeyUtils.TAG_LHC, "size:" + result.size());
			appAdpater.setData(result);
			progressLoading.setVisibility(View.INVISIBLE);
			gridApp.setVisibility(View.VISIBLE);
		}

	}

	public class AppAdpater extends BaseListAdapter<AppInfo> {

		public AppAdpater(Context context) {
			super(context);
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = LayoutInflater.from(getContext()).inflate(R.layout.activity_appmanage_item, arg2, false);
			}

			ImageView txtAppmanageIcon = ViewHelper.getView(arg1, R.id.txtAppmanageIcon);
			TextView txtAppmanageName = ViewHelper.getView(arg1, R.id.txtAppmanageName);

			txtAppmanageIcon.setImageDrawable(getItem(arg0).getAppIcon());
			txtAppmanageName.setText(getItem(arg0).getAppName());
			return arg1;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		
		case R.id.rdoDown:
			appFilter = AppFilter.down;
			break;
		case R.id.rdoRunning:
			appFilter = AppFilter.running;
			break;
		case R.id.rdoAll:
			appFilter = AppFilter.all;
			break;
		default:
			appFilter = AppFilter.down;
			break;
		}
		new AppInfoAsyncTask().execute();
	}

}
