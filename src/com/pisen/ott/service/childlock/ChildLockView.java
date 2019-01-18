package com.pisen.ott.service.childlock;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.izy.preference.PreferencesUtils;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pisen.ott.service.LockScreenService;
import com.pisen.ott.service.window.WindowViewBase;
import com.pisen.ott.settings.R;
import com.pisen.ott.settings.SettingConfig;
import com.pisen.ott.settings.common.childlock.DealWithKeyEvent;
import com.pisen.ott.settings.common.childlock.DealWithKeyEvent.IhandleDifferentAction;

/**
 * 密码锁界面
 * 
 * @author yangyp
 * @version 1.0, 2014年12月23日 上午9:54:57
 */
public class ChildLockView extends WindowViewBase implements View.OnClickListener, IhandleDifferentAction {

	protected Button btnCancle = null; // 重答问题，重新输入
	protected Button btnOk = null; // 找回密码，换问题
	protected EditText edtInputPassWord = null;
	public static boolean IsRetreivePassWord = false; // 是否是找回密码界面
	protected static Context context = null;
	protected LinearLayout ShowTextTip = null; // 按键输入提示文字
	protected LinearLayout ButtonShow = null; // 是否隐藏按钮布局
	protected LinearLayout lryPasswordShow = null; // 显示密码页面
	protected LinearLayout lryInputDialog = null; // 输入密码页面
	protected TextView txtDialogTile = null; // dialog标题
	protected View viewDivideline = null;
	protected LinearLayout PassWordNotSet = null; // 用于扩展的，当未设置初始密码的时候可以显示的布局
	public DealWithKeyEvent handleKeyEvent = null; // 键盘监听处理对象
	private ArrayList<StringBuilder> questionSet = null; // 找回密码问题集合
	private ArrayList<StringBuilder> answerSet = null; // 找回密码密码集合
	private int questionIndex = 0; // 标记第几个问题。
	private final static String retrievePassword = "com.pisen.ott.retrievePassword"; // 已找回密码广播
	private InputMethodManager imm; // 输入法管理对象
	private ImageView passWordTips1 = null;
	private ImageView passWordTips2 = null;
	private ImageView passWordTips3 = null;
	private ImageView passWordTips4 = null;
	private ImageView passWordTips5 = null;
	private ImageView passWordTips6 = null;
	private ImageView passWordTips7 = null;
	private ImageView passWordTips8 = null;
	private ImageView[] ImageSet = new ImageView[8];
	private int[] ResInterage = { R.drawable.key_ic_left, R.drawable.key_ic_right, R.drawable.key_ic_up, R.drawable.key_ic_down };
	ArrayList<String> keyList = new ArrayList<String>() {
		{
			add("L");
			add("R");
			add("U");
			add("D");
		}
	};
	private String passWrod = PreferencesUtils.getString(SettingConfig.ChildrenPassWordContent, "LLLL");
	public static boolean isPassWordShow = false;
	public Handler closeViewHandler =  new Handler()  //防止有时候键盘消息无响应问题。
	{
		
		@Override
		public void handleMessage(Message msg) {
		
		}
	};
	
	public ChildLockView(Context contextPara) {
		super(contextPara);
		View.inflate(contextPara, R.layout.ui_dialog_alert_unlock, this);
		initialBaseData(contextPara);
		initViews(context);
		setControlListen();
	}

	/**
	 * 初始化非控件成员变量
	 * 
	 * @param contextPara
	 */
	public void initialBaseData(Context contextPara) {
		handleKeyEvent = new DealWithKeyEvent(contextPara, this);
		context = contextPara;
		questionSet = getQuestionSetFromXML(R.xml.quetionset);
		answerSet = getQuestionSetFromXML(R.xml.anwserset);
		imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	/**
	 * 显示密码页面
	 */
	public void initDisplayPassWord() {

		lryInputDialog.setVisibility(View.GONE);
		lryPasswordShow.setVisibility(View.VISIBLE);
		isPassWordShow = true;
		ShowPassWord();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.i("view", "onresume..");
		isPassWordShow = false;
		lryInputDialog.setVisibility(View.VISIBLE);
		lryPasswordShow.setVisibility(View.GONE);
		super.onResume();
	}

	/**
	 * 显示密码
	 */
	public void ShowPassWord() {
		int i = 0;
		for (; i < passWrod.length(); i++) {
			ImageSet[i].setBackgroundResource(ResInterage[keyList.indexOf(String.valueOf(passWrod.charAt(i)))]);
		}
		if (i <= 5) {
			for (; i < 8; i++) {
				ImageSet[i].setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 把监听设置回原来的状态
	 */
	public void recoveryListen() {
		edtInputPassWord.setOnKeyListener(handleKeyEvent);
		btnCancle.setText("重新输入");
		btnOk.setText("找回密码");
	}

	/**
	 * 是否显示键盘
	 * 
	 * @param isShow
	 */
	public void showKeyBoard(boolean isShow) {
		if (isShow) {
			imm.showSoftInput(edtInputPassWord, InputMethodManager.SHOW_FORCED);
		} else {
			// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			imm.hideSoftInputFromWindow(edtInputPassWord.getWindowToken(), 0);
		}
	}

	/**
	 * 设置儿童锁输入界面
	 * 
	 * @param isReconvery
	 */
	public void recoveryInitiView(boolean isReconvery) {
		if (isReconvery) {
			setBtnIsShow(false);
			txtDialogTile.setText("请输入儿童锁密码");
			edtInputPassWord.setBackgroundResource(R.drawable.textfield_default);
		} else {
			setBtnIsShow(true);
			txtDialogTile.setText("密码输入不正确");
			edtInputPassWord.setBackgroundResource(R.drawable.textfield_default_error);
			cleanEditTextContent();
			btnCancle.requestFocus();
		}
	}

	/**
	 * 设置未设置密码的显示
	 * 
	 * @param isVisible
	 */
	public void setPassWordNotSetVisible(int isVisible) {
		this.PassWordNotSet.setVisibility(isVisible);
	}

	/**
	 * 清空editatext的内容
	 */
	public void cleanEditTextContent() {
		if (this.edtInputPassWord != null)
			this.edtInputPassWord.setText("");
	}

	/**
	 * 初始化控件
	 * 
	 * @param context
	 */
	private void initViews(Context context) {
		btnCancle = (Button) findViewById(R.id.btn_cancle);
		btnOk = (Button) findViewById(R.id.btn_ok);
		ShowTextTip = (LinearLayout) findViewById(R.id.ShowTextTip);
		ButtonShow = (LinearLayout) findViewById(R.id.buttonShow);
		edtInputPassWord = (EditText) findViewById(R.id.edtPassword);
		txtDialogTile = (TextView) findViewById(R.id.dialog_title);
		viewDivideline = (View) findViewById(R.id.view_divideline);
		PassWordNotSet = (LinearLayout) findViewById(R.id.lrel_password_notSet);
		lryInputDialog = (LinearLayout) findViewById(R.id.alertDialogTip);
		lryPasswordShow = (LinearLayout) findViewById(R.id.lryPassWordShow);
		passWordTips1 = (ImageView) findViewById(R.id.imgPassWord1);
		passWordTips2 = (ImageView) findViewById(R.id.imgPassWord2);
		passWordTips3 = (ImageView) findViewById(R.id.imgPassWord3);
		passWordTips4 = (ImageView) findViewById(R.id.imgPassWord4);
		passWordTips5 = (ImageView) findViewById(R.id.imgPassWord5);
		passWordTips6 = (ImageView) findViewById(R.id.imgPassWord6);
		passWordTips7 = (ImageView) findViewById(R.id.imgPassWord7);
		passWordTips8 = (ImageView) findViewById(R.id.imgPassWord8);
		ImageSet[0] = passWordTips1;
		ImageSet[1] = passWordTips2;
		ImageSet[2] = passWordTips3;
		ImageSet[3] = passWordTips4;
		ImageSet[4] = passWordTips5;
		ImageSet[5] = passWordTips6;
		ImageSet[6] = passWordTips7;
		ImageSet[7] = passWordTips8;
	}

	/**
	 * 设置所有控件的监听
	 */
	private void setControlListen() {
		btnCancle.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		edtInputPassWord.setOnKeyListener(handleKeyEvent);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_cancle:
			edtInputPassWord.requestFocus();
			if (!getIsRetreivePassWord())
				recoveryInitiView(true);
			else
				txtDialogTile.setText(getQuestionContent());
			break;
		case R.id.btn_ok:
			// retrieve password through popup other alertDialog
			if (!getIsRetreivePassWord()) {
				jumpRetrivePassword();
			} else {
				questionIndex++;
				txtDialogTile.setText(getQuestionContent());
			}
			edtInputPassWord.requestFocus();
			break;
		default:
			break;
		}

		if (getIsRetreivePassWord()) {
			showKeyBoard(true);
		}
		cleanEditTextContent();
		invalidate();
	}

	@Override
	public void handDifferenceAcion(int keyCode, String OutputPassWord) {

		cleanEditTextContent();
		edtInputPassWord.append(OutputPassWord);
	}

	/**
	 * 获取问题内容
	 * 
	 * @return
	 */
	public String getQuestionContent() {
		return questionSet.get(questionIndex % questionSet.size()).toString();
	}

	/**
	 * 获取问题答案集
	 */
	public String getAnswerContent() {
		return answerSet.get(questionIndex % questionSet.size()).toString();
	}

	/**
	 * 设置是否在找回密码界面
	 * 
	 * @param isRetrive
	 */
	public void setRetreivePassWordState(boolean isRetrive) {
		IsRetreivePassWord = isRetrive;
	}

	/**
	 * 判断是否是在找回密码界面
	 * 
	 * @return
	 */
	public boolean getIsRetreivePassWord() {
		return IsRetreivePassWord;
	}

	/**
	 * 处理回车键的消息，或者ok键的消息
	 */
	public void handOkKeyPress() {
		// TODO Auto-generated method stub

		if (getIsRetreivePassWord()) {
			// 在找回密码界面
			if (comparePasswordToInput(getAnswerContent())) {
				// 输入密码正确
				setRetreivePassWordState(false);
				if (!isPassWordShow) {
					Log.i("isPassWordShow", "handOkKeyPress result is true.");
					initDisplayPassWord();
					showKeyBoard(false);
					lryPasswordShow.requestLayout();
					lryPasswordShow.requestFocus();
					lryPasswordShow.setOnKeyListener(new OnKeyListener() {

						@Override
						public boolean onKey(View v, int keyCode, KeyEvent event) {
							// TODO Auto-generated method stub
							Log.i("lryPasswordShow", "onkey....");

							return false;
						}
					});
					recoveryListen();
					closeViewHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							LockScreenService.sendCmd(context, LockScreenService.CMD_SCREEN_UNLOCK);
						}
					}, 8000);
				}
				cleanEditTextContent();
			} else {
				txtDialogTile.setText("答案输入不正确");
				recoveryInitiViewSecond(false);
			}
		} else { // 在输入密码界面
			if (comparePasswordToInput(SettingConfig.getChildLockContent().toString())) {// 输入密码正确
				cleanEditTextContent();
				LockScreenService.sendCmd(context, LockScreenService.CMD_SCREEN_UNLOCK);
			} else {
				recoveryInitiView(false);
			}
		}
	}

	/**
	 * 输入密码是否正确
	 * 
	 * @return
	 */
	public boolean comparePasswordToInput(String str) {
		return edtInputPassWord.getText().toString().equalsIgnoreCase(str);
	}

	/**
	 * 找回密码界面的视图恢复
	 */
	public void recoveryInitiViewSecond(boolean isRecovery) {
		if (isRecovery) {
			edtInputPassWord.setBackgroundResource(R.drawable.textfield_default);
		} else {
			edtInputPassWord.setBackgroundResource(R.drawable.textfield_default_error);
			btnOk.requestFocus();
		}
	}

	/**
	 * 设置布局是否显示
	 * 
	 * @param positiveContent
	 * @param cancleContent
	 * @param isShow
	 */
	public void setBtnContent(String positiveContent, String cancleContent, boolean isShow) {
		btnCancle.setText(positiveContent);
		btnOk.setText(cancleContent);
	}

	/**
	 * 设置button是否可见
	 * 
	 * @param isShow
	 */
	public void setBtnIsShow(boolean isShow) {
		if (isShow) {
			ButtonShow.setVisibility(View.VISIBLE);
			ShowTextTip.setVisibility(View.GONE);
		} else {
			ButtonShow.setVisibility(View.GONE);
			ShowTextTip.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 改变editview的属性
	 */
	public void setEditViewContent() {
		edtInputPassWord.setSingleLine();
		edtInputPassWord.requestFocus();
		edtInputPassWord.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub

				Log.i("keycode", "keyCode: " + keyCode);
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					handOkKeyPress();
					btnCancle.requestFocus();
					invalidate();
					return true;
				}
				if (isPassWordShow)
					LockScreenService.sendCmd(context, LockScreenService.CMD_SCREEN_UNLOCK);
				invalidate();
				return false;
			}
		});
	}

	/**
	 * 改变dialog内容为找回密码内容
	 */
	public void jumpRetrivePassword() {
		IsRetreivePassWord = true;
		setBtnContent("再答一次", "换个问题", true);
		txtDialogTile.setText(questionSet.get(0).toString());
		setEditViewContent();
	}

	/**
	 * 从xml中获取问题集合，或者问题答案
	 */
	public ArrayList<StringBuilder> getQuestionSetFromXML(int XmlId) {
		ArrayList<StringBuilder> keySet = new ArrayList<StringBuilder>();
		Resources Res = context.getResources();
		XmlResourceParser xrp = Res.getXml(XmlId);
		try {
			// 如果没有到文件尾继续执行
			while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
				// 如果是开始标签
				if (xrp.getEventType() == XmlPullParser.TEXT) {
					keySet.add(new StringBuilder(xrp.getText()));
				}
				// 下一个标签
				xrp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return keySet;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		Log.i("keyEvent", "action: " + KeyEvent.ACTION_DOWN + event.getAction());
		Log.i("keyEvent", "keyCode: " + event.getKeyCode());
		if (isPassWordShow && (event.getAction() == KeyEvent.ACTION_DOWN))
			LockScreenService.sendCmd(context, LockScreenService.CMD_SCREEN_UNLOCK);
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 结束view
	 */
	@Override
	public void finishView() {
		// TODO Auto-generated method stub

	}
}