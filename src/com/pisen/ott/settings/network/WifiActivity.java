package com.pisen.ott.settings.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.OTTDialog;
import com.pisen.ott.settings.R;
import com.pisen.ott.settings.SingleChoiceDialog;
import com.pisen.ott.settings.SingleChoiceDialog.OnItemCheckedChangeListener;
import com.pisen.ott.settings.network.AccessPoint.ResourceSort;
import com.pisen.ott.widget.FocusLinearLayout;
import com.pisen.ott.widget.FocusLinearLayout.OnItemClickFocusListener;
import com.pisen.ott.widget.FocusLinearLayout.OnItemKeyFocusListener;

/**
 * 无线网络设置(wifi)Activity
 */
public class WifiActivity extends OTTBaseActivity implements OnItemClickListener, OnItemClickFocusListener, OnItemKeyFocusListener {
	static String RE_INPUT_PASS = "reInputPass";
	static String INPUT_PASS = "inputPass";

	TextView txtHeadTitle;// UI head
	TextView txtHeadDescrible;// UI head discribe
	// 当前连接的wifi布局
	RelativeLayout lrelWifiCurrent;
	// wifi开关
	RelativeLayout lrelWifiSwitch;
	// wifi开关的textview
	TextView txtWifiSwitchTitle;
	// wifi开关显示内容数组
	String[] wifiSwitchArray;
	// wifi开关数组索引
	int wifiSwitchArrayindex;
	// 当前连接wifi名称
	TextView txtWifiCurrentName;
	// 当前wifi信号图片
	ImageView imgWifiCurrentSignal;
	ImageView imgWifiCurrentConnectedFlag;

	boolean runThread = true;
	WifiHelper wifiHelper;
	WifiReceiver receiverWifi;// wifi的系统广播Receiver
	AccessPoint currentAp; // UI顶部AP
	AccessPoint connectingAp;// 正在连接的Ap
	int type;// 连接wifi类型:无密码 WEP WPA
	List<AccessPoint> wifiList = null;// AP list 除当前AP外
	WifiConfiguration curConfig = null;// 选中项的配置信息

	LayoutInflater layoutInflater;
	ListView lstWifi;
	MyAdapter adapter; // listview 的适配器
	FocusLinearLayout wifiSettingsLayout;
	// wifi开关对话框
	SingleChoiceDialog<Object> wifiSwitchDialog;
	// wifi连接状态
	private boolean mWasAssociating;
	private boolean mWasAssociated;
	private boolean mWasHandshaking;
	private boolean mConnected;
	private boolean mFourWayHandshake;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.network_wifi);
		// 获得wifi管理器
		wifiHelper = new WifiHelper(this);
		// 初始化UI
		initUI();
		// 注册接收wifi扫描结果的Receiver
		receiverWifi = new WifiReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		registerReceiver(receiverWifi, intentFilter);
		// 启动扫描wifi线程
		new Thread(new MyThread()).start();
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		layoutInflater = LayoutInflater.from(this);
		// 设置head
		txtHeadTitle = (TextView) findViewById(R.id.txtTitle);
		txtHeadDescrible = (TextView) findViewById(R.id.txtSubtitle);
		txtHeadTitle.setText("无线网络");
		txtHeadDescrible.setText("无线网络的相关设置");
		// wifi列表
		lstWifi = (ListView) findViewById(R.id.lst_wifi);
		adapter = new MyAdapter(this);
		lstWifi.setAdapter(adapter);
		lstWifi.setOnItemClickListener(this);
		// 开启关闭wifi
		lrelWifiSwitch = (RelativeLayout) findViewById(R.id.lrel_wifi_switch);
		// lrelWifiSwitch.setOnClickListener(this);
		txtWifiSwitchTitle = (TextView) findViewById(R.id.txt_wifi_switch_title);
		wifiSwitchArray = getResources().getStringArray(R.array.array_on_off);
		wifiSwitchArrayindex = WifiManager.WIFI_STATE_ENABLED == wifiHelper.wifiManager.getWifiState() ? 1 : 0;
		txtWifiSwitchTitle.setText(wifiSwitchArray[wifiSwitchArrayindex]);
		// 当前连接的wifi
		lrelWifiCurrent = (RelativeLayout) findViewById(R.id.lrel_wifi_current);
		// lrelWifiCurrent.setOnClickListener(this);
		txtWifiCurrentName = (TextView) findViewById(R.id.txt_wifi_current_name);
		imgWifiCurrentSignal = (ImageView) findViewById(R.id.imgWifiCurrentSignal);
		imgWifiCurrentConnectedFlag = (ImageView) findViewById(R.id.imgWifiCurrentConnectedFlag);

		setCurrentWifiUi();
		// 绑定Layout
		wifiSettingsLayout = (FocusLinearLayout) findViewById(R.id.wifiSettingsLayout);
		wifiSettingsLayout.setOnItemClickListener(this);
		wifiSettingsLayout.setOnItemKeyListener(this);
	}

	private void setCurrentWifiUi() {
		if (WifiManager.WIFI_STATE_ENABLED == wifiHelper.wifiManager.getWifiState()) {
			WifiInfo info = wifiHelper.wifiManager.getConnectionInfo();
			if (info != null && info.getSupplicantState() == SupplicantState.COMPLETED) {// 有连接的wifi
				lrelWifiSwitch.setBackgroundResource(R.drawable.table_round_top);
				lrelWifiCurrent.setVisibility(View.VISIBLE);
				txtWifiCurrentName.setText(wifiHelper.wifiManager.getConnectionInfo().getSSID());
				imgWifiCurrentConnectedFlag.setVisibility(View.VISIBLE);
			} else {// 没有连接的wifi
				imgWifiCurrentConnectedFlag.setVisibility(View.GONE);
				lrelWifiCurrent.setVisibility(View.GONE);
				lrelWifiSwitch.setBackgroundResource(R.drawable.table_round_single);
			}
		}
	}

	@Override
	protected void onDestroy() {
		receiverWifi.clearAbortBroadcast();
		// 注销广播接收
		unregisterReceiver(receiverWifi);
		// 停止扫描wifi
		runThread = false;
		// 清除wifi链表
		lstWifi = null;
		// 清除wifiHelper数据
		wifiHelper.clear();
		wifiHelper = null;
		super.onDestroy();
	}

	/**
	 * wifi广播接收器
	 */
	public class WifiReceiver extends BroadcastReceiver {

		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context c, Intent intent) {
			String action = intent.getAction();
			// wifi状态改变 已开启 已关闭 开启中 关闭中
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
				int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
				switch (wifiState) {
				case WifiManager.WIFI_STATE_DISABLED:
					txtWifiSwitchTitle.setText("已关闭");
					lrelWifiCurrent.setVisibility(View.GONE);
					lstWifi.setVisibility(View.GONE);
					lrelWifiSwitch.setBackgroundResource(R.drawable.table_round_single);
					break;
				case WifiManager.WIFI_STATE_ENABLED:
					txtWifiSwitchTitle.setText("已开启");
					lstWifi.setVisibility(View.VISIBLE);
					break;
				//
				}
			}
			// 扫描ap
			if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
				// 加载wifiList与currentAP
				wifiHelper.srList = wifiHelper.wifiManager.getScanResults();
				wifiHelper.wifiListAll = wifiHelper.toApList(wifiHelper.srList);
				wifiList = wifiHelper.toApList(wifiHelper.srList);
				wifiList.remove(currentAp);
				wifiHelper.srList = null;
				AccessPoint ap = wifiHelper.findCurApFromList();
				currentAp = null;
				sortWifiList();// 排序
				upCurAp(ap);
				// adapter.notifyDataSetChanged();
				// setCurrentWifiUi();

			}
			// 网络状态变化
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
				NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				WifiInfo wifiInfo = wifiHelper.wifiManager.getConnectionInfo();
				Log.d("testMsg", wifiInfo.getSSID() + "---" + "NETWORK_STATE_CHANGED" + "---" + info.getDetailedState().toString() + "---");
				if (NetworkInfo.DetailedState.CONNECTED.equals(info.getDetailedState())) {// 已连接
					// 设置UI
					AccessPoint ap = wifiHelper.findCurApFromList();
					if (ap != null) {
						ap.msg = "已连接";
						upCurAp(ap);
						imgWifiCurrentConnectedFlag.setVisibility(View.VISIBLE);
						// adapter.notifyDataSetChanged();
					}
				}
				if (NetworkInfo.DetailedState.DISCONNECTED.equals(info.getDetailedState())) {// 已断开
					AccessPoint ap = wifiHelper.findCurApFromList();
					if (ap != null) {
						ap.msg = "已断开";
						imgWifiCurrentConnectedFlag.setVisibility(View.GONE);
						downCurAp();
						// adapter.notifyDataSetChanged();
					}
				}
			}
			// 连接过程状态
			if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
				Log.d("testMsg", "**in  SUPPLICANT_STATE_CHANGED_ACTION**");
				Bundle bundle = intent.getExtras();
				SupplicantState st = (SupplicantState) bundle.getSerializable(WifiManager.EXTRA_NEW_STATE);
				WifiInfo info = wifiHelper.wifiManager.getConnectionInfo();
				AccessPoint ap = wifiHelper.findCurApFromList();
				Log.d("testMsg", info.getSSID() + "---" + st.name() + "---" + WifiInfo.getDetailedStateOf(st).toString());

				switch (st) {
				case ASSOCIATING:
					mWasAssociating = true;
					break;
				case ASSOCIATED:
					mWasAssociated = true;
					// 设置ui
					if (ap != null) {
						upCurAp(ap);
					}
					break;
				case COMPLETED:
					// this just means the supplicant has connected, now
					// we wait for the rest of the framework to catch up
					break;
				case DISCONNECTED:
				case DORMANT:
					Log.d("testMsg", "**in disconnect**");
					if (mWasHandshaking || mFourWayHandshake) {// 密码验证失败
						// notifyListener(mWasHandshaking ?
						// RESULT_BAD_AUTHENTICATION
						// : RESULT_UNKNOWN_ERROR);

						// 取ap的连接失败次数，默认会连接2次，如果全部失败，跳到输入密码页
						if (connectingAp != null && ap != null && ap.equals(currentAp)) {
							if (connectingAp.failConnectedCount >= 1) {// 2次密码验证失败
								// 设置ui
								downCurAp();
								// 计数清0
								connectingAp.failConnectedCount = 0;
								Log.d("testMsg", "**3**");
								// 清除配置信息
								if (info != null && wifiHelper.getConfigedInfoFromWifiInfo(info) != null) {
									Log.d("testMsg", "**4**");
									wifiHelper.wifiManager.removeNetwork(wifiHelper.getConfigedInfoFromWifiInfo(info).networkId);
									wifiHelper.wifiManager.saveConfiguration();
									wifiHelper.wifiConfigList = wifiHelper.wifiManager.getConfiguredNetworks();
								}

								// 跳转重新输入密码
								Intent intentPass = new Intent(getApplication(), WifiPasswordActivity.class);
								intentPass.putExtra("SSID", wifiHelper.formatSSID(info.getSSID()));
								intentPass.putExtra("flag", RE_INPUT_PASS);
								// 获取连接类型type
								String securityType = ap.encryptMode;
								if (securityType.equals(AccessPoint.OPEN)) {
									type = wifiHelper.TYPE_NO_PASSWD;
								} else if (securityType.equals(AccessPoint.WEP)) {
									type = wifiHelper.TYPE_WEP;
								} else if (securityType.equals(AccessPoint.WPA) || securityType.equals(AccessPoint.WPA2)) {
									type = wifiHelper.TYPE_WPA;
								}
								intentPass.putExtra("type", type);
								connectingAp = ap;
								startActivity(intentPass);

							} else {
								Log.d("testMsg", "**count++** " + connectingAp);
								if (connectingAp != null) {
									connectingAp.failConnectedCount++;
								}

							}
						}

					}
					break;
				case INTERFACE_DISABLED:
				case UNINITIALIZED:
					// notifyListener(RESULT_UNKNOWN_ERROR);
					break;
				case FOUR_WAY_HANDSHAKE:
					mFourWayHandshake = true;
				case GROUP_HANDSHAKE:
					mWasHandshaking = true;
					break;
				case INACTIVE:
					if (mWasAssociating && !mWasAssociated) {
						// If we go inactive after 'associating' without ever
						// having
						// been 'associated', the AP(s) must have rejected us.
						// notifyListener(RESULT_REJECTED_BY_AP);
						break;
					}
				case INVALID:
				case SCANNING:
				default:
				}
			}
		}
	}

	/**
	 * wifiList适配器使用的Holder
	 */
	public final class ViewHolder {
		RelativeLayout rl;
		public TextView name;
		public ImageView lock;
		public ImageView signal;
	}

	/**
	 * ListView适配器
	 */
	public class MyAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (wifiList != null) {
				return wifiList.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			if (wifiList != null) {
				return wifiList.get(arg0);
			}
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {

				holder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.network_wifi_list_item, null);
				holder.name = (TextView) convertView.findViewById(R.id.txt_wifi_item_name);
				holder.lock = (ImageView) convertView.findViewById(R.id.img_wifi_item_lock);
				holder.signal = (ImageView) convertView.findViewById(R.id.img_wifi_item_signal);
				holder.rl = (RelativeLayout) convertView.findViewById(R.id.lrel_wifi_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 设置外框背景图片的样式
			if (wifiList.size() > 0) {
				if (wifiList.size() == 1) {// 只有一个
					holder.rl.setBackgroundResource(R.drawable.table_round_single);
				} else if (0 == position) {// 第一个
					holder.rl.setBackgroundResource(R.drawable.table_round_top);
				} else if (wifiList.size() - 1 == position) {// 最后一个
					holder.rl.setBackgroundResource(R.drawable.table_round_bottom);
				} else {// 中间
					holder.rl.setBackgroundResource(R.drawable.table_round_middle);
				}
			}

			// 设置wifi名称
			holder.name.setText(wifiList.get(position).SSID);
			// 设置是否密码保护的图片
			if (wifiList.get(position).encryptMode.equals(AccessPoint.OPEN)) {
				holder.lock.setVisibility(View.GONE);
			} else {
				holder.lock.setVisibility(View.VISIBLE);
			}
			// 设置信号强度图片
			switch (wifiList.get(position).signal) {
			case 0:
				holder.signal.setImageDrawable(getResources().getDrawable(R.drawable.wifi_ic_0));
				break;
			case 1:
				holder.signal.setImageDrawable(getResources().getDrawable(R.drawable.wifi_ic_1));
				break;
			case 2:
				holder.signal.setImageDrawable(getResources().getDrawable(R.drawable.wifi_ic_2));
				break;
			case 3:
				holder.signal.setImageDrawable(getResources().getDrawable(R.drawable.wifi_ic_3));
				break;
			case 4:
				holder.signal.setImageDrawable(getResources().getDrawable(R.drawable.wifi_ic_4));
				break;

			}
			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		if (position < wifiList.size()) {
			// 判断点击项是否在配置列表中
			wifiHelper.wifiConfigList = wifiHelper.wifiManager.getConfiguredNetworks();
			curConfig = wifiHelper.getConfigedInfo(wifiList.get(position));
			connectingAp = wifiList.get(position);
			// imgWifiCurrentConnectedFlag.setVisibility(View.GONE);
			if (curConfig != null) {
				wifiHelper.wifiManager.enableNetwork(curConfig.networkId, true);

			} else {// 无配置信息,弹出对话框要求输入密码
				// 获取连接类型
				String securityType = wifiList.get(position).encryptMode;
				// mapping type
				if (securityType.equals(AccessPoint.OPEN)) {
					type = wifiHelper.TYPE_NO_PASSWD;
				} else if (securityType.equals(AccessPoint.WEP)) {
					type = wifiHelper.TYPE_WEP;
				} else if (securityType.equals(AccessPoint.WPA) || securityType.equals(AccessPoint.WPA2)) {
					type = wifiHelper.TYPE_WPA;
				}
				// 连接wifi
				if (type == wifiHelper.TYPE_NO_PASSWD) {// 无密码
					int wcgID = wifiHelper.wifiManager.addNetwork(wifiHelper.createWifiInfo(wifiList.get(position).SSID, "", type));
					boolean b = wifiHelper.wifiManager.enableNetwork(wcgID, true);
					wifiHelper.wifiConfigList = wifiHelper.wifiManager.getConfiguredNetworks();
				} else {// 有密码
					// 启动activity输入密码
					Intent intent = new Intent(this, WifiPasswordActivity.class);
					intent.putExtra("SSID", wifiList.get(position).SSID);
					intent.putExtra("type", type);
					intent.putExtra("flag", INPUT_PASS);
					startActivity(intent);
				}
			}
		}
	}

	/**
	 * wifiList排序，有配置的排在前边，无配置的排在后边并再次按信号强度排序
	 */
	private void sortWifiList() {

		// 没有配置的从list取出放到另一个list中
		List<AccessPoint> tmpList = new ArrayList<AccessPoint>();
		for (AccessPoint ap : wifiList) {
			if (ap.Config == null) {
				tmpList.add(ap);
			}
		}
		wifiList.removeAll(tmpList);
		// 按信号强度排序
		Collections.sort(tmpList, new ComparatorAccessPoint(ResourceSort.Signal));
		// 加入到wifiList中
		wifiList.addAll(tmpList);

	}

	/** 扫描ap的线程类 */
	private class MyThread implements Runnable {
		@Override
		public void run() {
			while (runThread) {
				wifiHelper.wifiManager.startScan();// 启动wifi扫描
				try {
					Thread.sleep(10000);// 线程暂停10秒，单位毫秒
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onItemClick(View v) {
		switch (v.getId()) {
		//wifi开关
		case R.id.lrel_wifi_switch:
			showWifiSwitchDialog();
			break;
		//当前连接的wifi
		case R.id.lrel_wifi_current:
			WifiInfo info = wifiHelper.wifiManager.getConnectionInfo();
			//已连接的wifi，才可以查看详细信息
			if(info != null && info.getSupplicantState() == SupplicantState.COMPLETED){
				startActivity(new Intent(this, WifiDetailActivity.class));
			}
			
			break;
		}

	}

	/**
	 * 弹出wifi切换对话框
	 */
	private void showWifiSwitchDialog() {
		wifiSwitchDialog = new SingleChoiceDialog<Object>(this);
		wifiSwitchDialog.setTitle("开启无线网络");
		wifiSwitchDialog.setItems(getResources().getStringArray(R.array.array_on_off));
		wifiSwitchDialog.setCheckedItem(WifiManager.WIFI_STATE_ENABLED == wifiHelper.wifiManager.getWifiState() ? 1 : 0);
		wifiSwitchDialog.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
			@Override
			public void onItemCheckedChanged(OTTDialog dialog, int itemIndex) {
				if (0 == itemIndex) {// 关闭
					toggleWifiSwitch(false);
				} else if (1 == itemIndex) {// 开启
					toggleWifiSwitch(true);
				}
				wifiSwitchDialog.dismiss();
			}
		});
		wifiSwitchDialog.show();
	}

	/**
	 * 切换wifi开关
	 */
	private void toggleWifiSwitch(boolean b) {
		if (!wifiHelper.wifiManager.isWifiEnabled() && b) {
			if (WifiManager.WIFI_STATE_DISABLED == wifiHelper.wifiManager.getWifiState()) {
				wifiHelper.wifiManager.setWifiEnabled(true);
			}
		}
		if (wifiHelper.wifiManager.isWifiEnabled() && !b) {
			if (WifiManager.WIFI_STATE_ENABLED == wifiHelper.wifiManager.getWifiState()) {
				wifiHelper.wifiManager.setWifiEnabled(false);
			}
		}
	}

	/**
	 * 设置currentAp与wifiList数据 ,并把Ap放置与界面顶部
	 */
	private void upCurAp(AccessPoint ap) {
		clearCurAp();
		// 设置curAp
		currentAp = ap;
		// 清除wifiList
		wifiList.remove(ap);
		refreshUI();
	}

	/**
	 * 设置currentAp与wifiList数据 ,并把Ap从界面顶部拿下
	 */
	private void downCurAp() {
		clearCurAp();
		imgWifiCurrentConnectedFlag.setVisibility(View.GONE);
		refreshUI();
	}

	/**
	 * 清除currentAP并归还到list
	 */
	private void clearCurAp() {
		// 如果curAp不为空，curAp下来
		if (currentAp != null&&findApFromList(currentAp)) {
			wifiList.add(currentAp);			
		}
		currentAp = null;
	}
	
	private boolean findApFromList(AccessPoint target) {
		if (wifiList != null && wifiList.size() > 0 & target != null) {
			for (AccessPoint ap : wifiList) {
				if (wifiHelper.formatSSID(target.SSID).equals(wifiHelper.formatSSID(ap.SSID))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 刷新顶部Ap与listView
	 */
	private void refreshUI() {
		setCurApUI();
		adapter.notifyDataSetChanged();
	}

	/**
	 * 设置顶部Ap的UI
	 */
	private void setCurApUI() {
		WifiInfo info = wifiHelper.wifiManager.getConnectionInfo();
		if (info != null && info.getSupplicantState() == SupplicantState.COMPLETED) {
			imgWifiCurrentConnectedFlag.setVisibility(View.VISIBLE);
			//测试动画
//			Animation connectAnimation=AnimationUtils.loadAnimation(this, R.anim.connect_wifi);
//			imgWifiCurrentConnectedFlag.startAnimation(connectAnimation);
		} else {
			//测试动画
//			imgWifiCurrentConnectedFlag.clearAnimation();
			imgWifiCurrentConnectedFlag.setVisibility(View.GONE);
		}
		if (currentAp != null) {// 设置
			lrelWifiSwitch.setBackgroundResource(R.drawable.table_round_top);
			lrelWifiCurrent.setVisibility(View.VISIBLE);
			txtWifiCurrentName.setText(wifiHelper.wifiManager.getConnectionInfo().getSSID());
			setWifiSignalUi(imgWifiCurrentSignal, currentAp);// 设置信号Ui
		} else {// 清理
			lrelWifiSwitch.setBackgroundResource(R.drawable.table_round_single);
			lrelWifiCurrent.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置信号图片
	 */
	private void setWifiSignalUi(ImageView v, AccessPoint ap) {
		switch (ap.signal) {
		case 0:
			v.setImageDrawable(getResources().getDrawable(R.drawable.wifi_ic_0));
			break;
		case 1:
			v.setImageDrawable(getResources().getDrawable(R.drawable.wifi_ic_1));
			break;
		case 2:
			v.setImageDrawable(getResources().getDrawable(R.drawable.wifi_ic_2));
			break;
		case 3:
			v.setImageDrawable(getResources().getDrawable(R.drawable.wifi_ic_3));
			break;
		case 4:
			v.setImageDrawable(getResources().getDrawable(R.drawable.wifi_ic_4));
			break;
		}
	}

	@Override
	public void onItemKeyLeft(View v, KeyEvent event) {
		switch (v.getId()) {
		// wifi开关
		case R.id.lrel_wifi_switch:
			if (wifiSwitchArrayindex <= 0) {
				wifiSwitchArrayindex = wifiSwitchArray.length - 1;
			} else {
				wifiSwitchArrayindex--;
			}
			toggleWifiSwitch(wifiSwitchArrayindex == 0 ? false : true);
			break;
		}
	}

	@Override
	public void onItemKeyRight(View v, KeyEvent event) {
		switch (v.getId()) {
		// wifi开关
		case R.id.lrel_wifi_switch:
			if (wifiSwitchArrayindex >= wifiSwitchArray.length - 1) {
				wifiSwitchArrayindex = 0;
			} else {
				wifiSwitchArrayindex++;
			}
			toggleWifiSwitch(wifiSwitchArrayindex == 0 ? false : true);
			break;
		}
	}
}
