package com.pisen.ott.settings.common.childlock;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Toast;

import com.pisen.ott.service.LockScreenService;
import com.pisen.ott.service.childlock.ChildLockView;

/**
 * 主要是把特殊的键盘消息编码
 *  编码规则：
 *    左键---> 'L'
 *    右键---> 'R'
 *    上 键---> 'U'
 *    下键---> 'D'
 * @author mugabutie
 *
 */
public class DealWithKeyEvent implements OnKeyListener
{
	/**
	 *  此接口处理此类处理不了的不同消息
	 * @author mugabutie
	 *
	 */
	public interface IhandleDifferentAction 
	{
	   public void handDifferenceAcion(int keyCode, String OutputPassWord);
	   public void handOkKeyPress();
	   public void finishView();
	}
	private Context context = null;
    private  String strInputPassword = "";
	public static String strleftKeyValue = "L"; //左键
    public static String strrightKeyValue = "R"; //右键
    public static String strupKeyValue = "U";    //上键
    public static String strdownKeyValue = "D";  //下键
    public static boolean isValidateKey; //设置是否响应当前的返回键
    public IhandleDifferentAction IhandleDifferenceAction = null;
    
	public DealWithKeyEvent(Context ctx, IhandleDifferentAction IhandDifferent)
	{
		this.context = ctx;
		this.IhandleDifferenceAction = IhandDifferent;
	}
	
	public void setHandleInterface(IhandleDifferentAction I)
	{
		this.IhandleDifferenceAction = I;
	}
	
	public void setstrInputPassword(String InputPassword)
	{
		this.strInputPassword = InputPassword;
	}
	
	/**
	 * 设置是否响应返回键
	 * @param isValidate
	 */
	public void setIsValidateKey(boolean isValidate)
	{
		isValidateKey = isValidate;
	}
	
	/**
	 * 检测当前密码是否超过8位
	 * @param KeyEventValue
	 * @return
	 */
	public boolean isHandleMessage(int KeyEventValue)
	{
		if(KeyEventValue == KeyEvent.KEYCODE_DPAD_UP
				|| KeyEventValue == KeyEvent.KEYCODE_DPAD_DOWN
				|| KeyEventValue == KeyEvent.KEYCODE_DPAD_LEFT
				|| KeyEventValue == KeyEvent.KEYCODE_DPAD_RIGHT)
		{
			if(strInputPassword.length() >= 8)
			{
				Toast.makeText(context, "密码不能超过8位", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
	
		return true;
	}
	/**
	 * 转码
	 * @param KeyEventValue
	 */
	public boolean HandleKeyEvent(int KeyEventValue)
	{
		if(!isHandleMessage(KeyEventValue))
		{
			return false;
		}
		switch(KeyEventValue)
		{
		case KeyEvent.KEYCODE_DPAD_UP:
			Log.i("onKeyDown","keyCode: KEYCODE_DPAD_UP");
			strInputPassword += strupKeyValue;
           break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			Log.i("onKeyDown","keyCode: KEYCODE_DPAD_DOWN");
			strInputPassword += strdownKeyValue;
		   break;
		case KeyEvent.KEYCODE_ENTER:
			Log.i("onKeyDown","keyCode: KEYCODE_ENTER");
	
           break;
		case KeyEvent.KEYCODE_BACK:
			Log.i("onKeyDown","keyCode: KEYCODE_BACK");
			deleteWord();
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			Log.i("onKeyDown","keyCode: KEYCODE_DPAD_LEFT");
			strInputPassword += strleftKeyValue;
		   break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			Log.i("onKeyDown","keyCode: KEYCODE_DPAD_RIGHT");
			strInputPassword += strrightKeyValue;
			default:
				break;
		}
		if(ChildLockView.isPassWordShow)
		{
			ChildLockView.isPassWordShow = false;
			LockScreenService.sendCmd(context, LockScreenService.CMD_SCREEN_UNLOCK);
		}
		return true;
	}
	
	/**
	 * 回删字符
	 */
    public void deleteWord()
    {
    	if(!strInputPassword.isEmpty())
    	{
    		strInputPassword = strInputPassword.subSequence(0, strInputPassword.length()-1).toString();
    	}else if(isValidateKey)
    	{
    		IhandleDifferenceAction.finishView();
    	}
    }

	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == KeyEvent.ACTION_DOWN)
		{
	    	if(HandleKeyEvent(keyCode))
	    	{
		    IhandleDifferenceAction.handDifferenceAcion(keyCode, strInputPassword);
		    if(keyCode == KeyEvent.KEYCODE_ENTER)
		    {
			IhandleDifferenceAction.handOkKeyPress();
			strInputPassword = "";
		    }
	    	}
		}
	  return true;
	}
}
