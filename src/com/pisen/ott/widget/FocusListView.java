package com.pisen.ott.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.OverScroller;

import com.pisen.ott.settings.R;

/**
 * 支持按键焦点浮层切换
 * 
 * @author yangyp
 * @version 1.0, 2014年12月16日 下午4:37:45
 */
public class FocusListView extends ListView implements IMoveFocus {

	// 记录当前焦点所在区间
	private final byte FOCUS_MIDDLE = 0;
	private final byte FOCUS_BOTTOM = 1;
	private final byte FOCUS_TOP = 2;

	// 焦点所在位置
	private byte mFocusState = FOCUS_MIDDLE;

	// 整个ListView的高度
	private int listHeight;

	private OverScroller mScroller;
	private Rect currentRect;
	private Drawable mDrawable;
	private static final int FocusBorder = 18;

	public FocusListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setClipChildren(false);
		setClipToPadding(false);
		setVerticalFadingEdgeEnabled(false);

		mScroller = new OverScroller(context, new DecelerateInterpolator());
		currentRect = new Rect();
		mDrawable = getResources().getDrawable(R.drawable.item_bg_focus);
	}

	/**
	 * 通过此方法设置焦点背景图片
	 * 
	 * @param resourceId
	 */
	public void setFocusBitmap(int resourceId) {
		mDrawable = getResources().getDrawable(resourceId);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		listHeight = getHeight();

		if (getChildCount() > 0) {
			View child = getChildAt(0);
			if (currentRect.isEmpty()) {
				// child.getDrawingRect(currentRect);
				//currentRect.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
				child.getHitRect(currentRect);
				child.requestFocus();
			}
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (hasFocus()) {
			if (mScroller.computeScrollOffset()) {
				int currY = mScroller.getCurrY();
				currentRect.offsetTo(currentRect.left, currY);

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
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		setSelection(0);
	}

	// ListView的item数量实际上是动态改变的，会在一个数值x和x+1甚至x+2之间徘徊，所以利用item的数量来计算焦点的移动是不行的，所以增加的实现此功能的复杂度
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		View view = getSelectedView();
		if (view != null) {
			int itemHeight = currentRect.height();
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_DOWN:
				if (getLastVisiblePosition() == getAdapter().getCount() - 1 && getSelectedItemPosition() == getLastVisiblePosition() - 1
						&& mFocusState == FOCUS_MIDDLE) {
					int top = view.getTop() + itemHeight + getDividerHeight();
					mScroller.startScroll(0, view.getTop(), 0, top - view.getTop());
					mFocusState = FOCUS_MIDDLE;
					break;
				}

				if (getSelectedItemPosition() < getLastVisiblePosition() - 1) {
					int top = view.getTop() + itemHeight + getDividerHeight();
					mScroller.startScroll(0, view.getTop(), 0, top - view.getTop());
					mFocusState = FOCUS_MIDDLE;
				} else if (getSelectedItemPosition() == getLastVisiblePosition() - 1) {
					if (mFocusState != FOCUS_BOTTOM) {
						int top = listHeight - itemHeight - getVerticalFadingEdgeLength() - getDividerHeight();
						mScroller.startScroll(0, view.getTop(), 0, top - view.getTop());
						mFocusState = FOCUS_BOTTOM;
					}
				}

				break;
			case KeyEvent.KEYCODE_DPAD_UP:
				if (getSelectedItemPosition() == getFirstVisiblePosition() + 1) {
					if (mFocusState != FOCUS_TOP) {
						int top = 0 + getDividerHeight() + getVerticalFadingEdgeLength();
						mScroller.startScroll(0, view.getTop(), 0, top - view.getTop());
						mFocusState = FOCUS_TOP;
					}
					break;
				}

				if (getSelectedItemPosition() > getFirstVisiblePosition()) {
					int top = view.getTop() - itemHeight - getDividerHeight();
					mScroller.startScroll(0, view.getTop(), 0, top - view.getTop());
					mFocusState = FOCUS_MIDDLE;
				}
				break;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
