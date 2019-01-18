package com.pisen.ott.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.izy.widget.FocusScroller;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.pisen.ott.settings.R;

/**
 * 支持按键焦点浮层切换
 * 
 * @author yangyp
 * @version 1.0, 2014年12月16日 下午4:39:18
 */
public class FocusLinearLayout extends LinearLayout implements IMoveFocus, OnFocusChangeListener {

	private FocusScroller mScroller;
	private Drawable mDrawable;
	private static final int FocusBorder = 18;
	protected Rect currentRect; // 当前焦点位置
	private OnItemClickFocusListener mOnItemClickListener;
	private OnItemKeyFocusListener mOnItemKeyListener;

	public FocusLinearLayout(Context context) {
		super(context);
		initViews(context);
	}

	public FocusLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews(context);
	}

	public FocusLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initViews(context);
	}

	protected void initViews(Context context) {
		setWillNotDraw(false);
		// setClipToOutline(true);
		setClipChildren(false);
		setClipToPadding(false);
		setChildrenDrawingOrderEnabled(true);
		mScroller = new FocusScroller(context);
		mDrawable = getResources().getDrawable(R.drawable.item_bg_focus);
		currentRect = new Rect();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		setDefaultFocusedChild();
	}

	/**
	 * 设置默认显示的焦点
	 */
	protected void setDefaultFocusedChild() {
		for (int i = 0, N = getChildCount(); i < N; i++) {
			View child = getChildAt(i);
			if (child instanceof AbsListView) {
				child.requestFocus();
			} else if (child instanceof ViewGroup) {
				child.setOnFocusChangeListener(this);
				// child.setOnClickListener(this);
				if (currentRect.isEmpty()) {
					// currentRect.set(child.getLeft(), child.getTop(),
					// child.getRight(), child.getBottom());
					child.getHitRect(currentRect);
					child.requestFocus();
				}
			}
		}
	}

	public Rect getCurrentRect() {
		return new Rect(currentRect);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (hasFocus()) {
			// 判断开始矩形是否已经到达目标位置
			if (mScroller.computeScrollOffset()) {
				Rect currRect = mScroller.getCurrRect();
				currentRect.set(currRect);
				invalidate();
			}

			canvas.save();
			mDrawable.setBounds(currentRect.left - FocusBorder, currentRect.top - FocusBorder, currentRect.right + FocusBorder, currentRect.bottom
					+ FocusBorder);
			mDrawable.draw(canvas);
			canvas.restore();
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			Rect outRect = new Rect();
			// outRect.set(v.getLeft(), v.getTop(), v.getRight(),
			// v.getBottom());
			v.getHitRect(outRect);
			if (currentRect.isEmpty()) {
				currentRect.set(outRect);
				// invalidate();
			}

			mScroller.abortAnimation();
			if (!currentRect.equals(outRect)) {
				mScroller.startScroll(currentRect, outRect);
				// invalidate();
			}
		}
		invalidate();
	}

	public void setOnItemKeyListener(OnItemKeyFocusListener l) {
		this.mOnItemKeyListener = l;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getAction()) {
		case KeyEvent.ACTION_DOWN:
			break;
		case KeyEvent.ACTION_UP:
			handlerKeyUp(event);
			break;
		case KeyEvent.ACTION_MULTIPLE:
			break;
		}

		return super.dispatchKeyEvent(event);
	}

	/**
	 * 处理选中行的左右键消息
	 * 
	 * @param event
	 */
	protected void handlerKeyUp(KeyEvent event) {
		View v = getFocusedChild();
		int keyCode = event.getKeyCode();
		switch (keyCode) {
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			onFocusChange(v, true);
			if (mOnItemClickListener != null) {
				mOnItemClickListener.onItemClick(v);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (mOnItemKeyListener != null) {
				mOnItemKeyListener.onItemKeyLeft(v, event);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (mOnItemKeyListener != null) {
				mOnItemKeyListener.onItemKeyRight(v, event);
			}
			break;
		default:
			break;
		}
	}

	public void setOnItemClickListener(OnItemClickFocusListener l) {
		this.mOnItemClickListener = l;
	}

	/*
	 * @Override public void onClick(View v) { onFocusChange(v, true); if
	 * (mOnItemClickListener != null) { mOnItemClickListener.onClick(v); } }
	 */

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			Rect outRect = new Rect();
			for (int i = getChildCount() - 1; i >= 0; i--) {
				View child = getChildAt(i);
				child.getHitRect(outRect);
				if (outRect.contains(x, y)) {
					onFocusChange(child, true);
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onItemClick(child);
					}
					break;
				}
			}
		}

		return super.dispatchTouchEvent(ev);
	}

	public interface OnItemClickFocusListener {
		void onItemClick(View v);
	}

	public interface OnItemKeyFocusListener {

		void onItemKeyLeft(View v, KeyEvent event);

		void onItemKeyRight(View v, KeyEvent event);
	}
}
