package com.pisen.ott.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.izy.widget.FocusScroller;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.GridLayout;

import com.pisen.ott.settings.R;

/**
 * 支持按键焦点浮层切换
 * 
 * @author yangyp
 * @version 1.0, 2014年12月16日 下午4:38:55
 */
public class FocusGridLayout extends GridLayout implements IMoveFocus, OnFocusChangeListener, OnClickListener {

	private FocusScroller mScroller;
	private Drawable mDrawable;
	private static final int FocusBorder = 26;
	private Rect currentRect; // 当前焦点位置
	private OnClickListener mOnItemClickListener;

	public FocusGridLayout(Context context) {
		super(context);
		initViews(context);
	}

	public FocusGridLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews(context);
	}

	public FocusGridLayout(Context context, AttributeSet attrs, int defStyle) {
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
		mDrawable = getResources().getDrawable(R.drawable.setting_item_focus);
		currentRect = new Rect();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (getChildCount() > 0) {
			View child = getChildAt(0);
			if (currentRect.isEmpty()) {
				currentRect.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
				child.requestFocus();
			}

			for (int i = 0, N = getChildCount(); i < N; i++) {
				View c = getChildAt(i);
				c.setOnFocusChangeListener(this);
				c.setOnClickListener(this);
			}
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (hasFocus()) {
			// 判断开始矩形是否已经到达目标位置
			if (mScroller.computeScrollOffset()) {
				Rect crect = mScroller.getCurrRect();
				currentRect.set(crect);

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
			outRect.set(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
			if (currentRect.isEmpty()) {
				currentRect.set(outRect);
			}

			mScroller.abortAnimation();
			if (!currentRect.equals(outRect)) {
				mScroller.startScroll(currentRect, outRect);
			}

			v.bringToFront();
		}
	}

	public void setOnItemClickListener(OnClickListener l) {
		this.mOnItemClickListener = l;
	}

	@Override
	public void onClick(View v) {
		onFocusChange(v, true);
		// v.requestFocus();
		// currentRect.set(new Rect(v.getLeft(), v.getTop(), v.getRight(),
		// v.getBottom()));
		// v.bringToFront();

		if (mOnItemClickListener != null) {
			mOnItemClickListener.onClick(v);
		}
	}
}
