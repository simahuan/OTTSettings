package com.pisen.ott.settings.network;

import java.util.List;

import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;

/**
 * 当前连接的wifi的详细信息展示Activity
 * 
 * @author fq
 * @date 2014年12月13日 下午4:04:02
 */
public class WifiDetailActivity extends OTTBaseActivity implements OnClickListener {
	private WifiHelper wifiHelper;
	private WifiInfo wifiInfo;
	private DhcpInfo dhcpInfo;

	private RelativeLayout lrelWifiDetailDisconnect;
	private TextView txtWifiDetailIp;
	private TextView txtWifiDetailMask;
	private TextView txtWifiDetailGateway;
	private TextView txtWifiDetailDns;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		wifiHelper = new WifiHelper(this);
		wifiHelper.wifiManager = ((WifiManager) getSystemService("wifi"));
		dhcpInfo = wifiHelper.wifiManager.getDhcpInfo();
		wifiInfo = wifiHelper.wifiManager.getConnectionInfo();
		setContentView(R.layout.network_wifi_detail);
		setTitle("当前连接的WIFI");
		txtWifiDetailIp = (TextView) findViewById(R.id.txt_wifi_detail_ip);
		txtWifiDetailMask = (TextView) findViewById(R.id.txt_wifi_detail_mask);
		txtWifiDetailGateway = (TextView) findViewById(R.id.txt_wifi_detail_gateway);
		txtWifiDetailDns = (TextView) findViewById(R.id.txt_wifi_detail_dns);
		txtWifiDetailIp.setText(intToIp(dhcpInfo.ipAddress));
		txtWifiDetailMask.setText(intToIp(dhcpInfo.netmask));
		txtWifiDetailGateway.setText(intToIp(dhcpInfo.gateway));
		txtWifiDetailDns.setText(intToIp(dhcpInfo.dns1));
		lrelWifiDetailDisconnect = (RelativeLayout) findViewById(R.id.lrel_wifi_detail_disconnect);
		lrelWifiDetailDisconnect.setOnClickListener(this);
	}

	private String intToIp(int paramInt) {
		return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lrel_wifi_detail_disconnect:	
			WifiInfo info = wifiHelper.wifiManager.getConnectionInfo();
			if(info!=null&&wifiHelper.getConfigedInfoFromWifiInfo(info)!=null){
				// 取消保存
				wifiHelper.wifiManager.removeNetwork(wifiHelper.getConfigedInfoFromWifiInfo(info).networkId);
				wifiHelper.wifiManager.saveConfiguration();
				// 断开连接
//				wifiHelper.wifiManager.disconnect();
				this.finish();
			}
			break;
		}
	}

}
