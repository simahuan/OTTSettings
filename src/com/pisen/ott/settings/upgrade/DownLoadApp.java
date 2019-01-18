package  com.pisen.ott.settings.upgrade;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pisen.ott.settings.R;


/**
 * 软件更新
 * 
 * @author MouJunFeng
 * @version 1.0, 2014-6-3 下午4:05:52
 */
public class DownLoadApp {

	private Context mContext;

	// 返回的安装包url
	public String apkUrl = "";

	// 下载选择提示框
	public Dialog noticeDialog;
	// 下载进度条显示
	private Dialog downloadDialog;
	/* 下载包安装路径 */
	private static final String savePath = Environment
			.getExternalStorageDirectory().getPath()
			+ File.separator
			+ "PisenRouter";

	private static final String saveFileName = savePath + File.separator
			+ "UpdateDemoRelease.apk";

	/* 进度条与通知ui刷新的handler和msg常量 */
	private ProgressBar mProgress;

	// 正在下载
	private static final int DOWN_UPDATE = 1;
	// 下载完成
	private static final int DOWN_OVER = 2;
	//网络异常
	private static final int ERROR = 3;

	private int progress;

	private Thread downLoadThread;

	private boolean interceptFlag = false;
	// 显示文字
	private TextView txtInfo;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				if (txtInfo != null) {
					txtInfo.setText("已下载 "+(progress + 1)+"%");
				}
				break;
			case DOWN_OVER:
				installApk();
				break;
			case ERROR:
				Toast.makeText(mContext, "没有找到资源", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};
	/**
	 * 显示正在下载的进度条提示框
	 */
	public void showDownloadDialog(Context ctx) {
		this.mContext = ctx;
		AlertDialog.Builder builder = new Builder(ctx);
		builder.setTitle("软件版本更新");
		final LayoutInflater inflater = LayoutInflater.from(ctx);
//		View v = inflater.inflate(R.layout.project_update_version, null);
//		mProgress = (ProgressBar) v.findViewById(R.id.progress);
//		txtInfo = (TextView) v.findViewById(R.id.txt_info);	
		builder.setCancelable(false);
//		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.setCancelable(false);
		downloadDialog.setCanceledOnTouchOutside(false);
		downloadDialog.show();

		downloadApk();
	}

	/**
	 * 下载的线程
	 */
	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				URL url = new URL(apkUrl);
				
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.

				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(ERROR);
			} catch (IOException e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(ERROR);
			}

		}
	};

	/**
	 * 下载apk
	 * 
	 * @param LoginKey
	 */

	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 安装apk
	 * 
	 * @param LoginKey
	 */
	private void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
