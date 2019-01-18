package com.pisen.ott.settings.common.location;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.izy.util.LogUtils;
import android.izy.util.StringUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pisen.ott.settings.R;
import com.pisen.ott.settings.common.location.ScrollerNumberPicker.OnSelectListener;
import com.pisen.ott.settings.util.KeyUtils;
import com.pisen.ott.widget.FocusLinearLayout;

/**
 * 城市选择器
 * 
 * @author Liuhc
 * @version 1.0 2014年12月4日 下午5:10:21
 */
public class CityPicker extends FocusLinearLayout/* LinearLayout */{

	/** 滑动控件 */
	private ScrollerNumberPicker mProvincePicker; // 省（直辖市、自治州、自治区）
	private ScrollerNumberPicker mCityPicker; // 城市
	private ScrollerNumberPicker mAreaPicker; // 区域

	private String mProvinceId = "";
	private String mCityId = "";
	private String mAreaId = "";
	private Context mContext;
	private List<CityInfo> mCityList = new ArrayList<CityInfo>();
	private Paint linePaint;
	private Rect outRect = new Rect();

	public CityPicker(Context context) {
		super(context);
	}

	public CityPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void initViews(Context context) {
		super.initViews(context);
		this.mContext = context;
		linePaint = new Paint();
		linePaint.setColor(0xff000000);
		linePaint.setAntiAlias(true);
		linePaint.setStrokeWidth(1f);
	}

	@Override
	protected void handlerKeyUp(KeyEvent event) {
		super.handlerKeyUp(event);
		View v = getFocusedChild();
		int keyCode = event.getKeyCode();
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			onItemKeyUp(v, event);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			onItemKeyDown(v, event);
			break;
		default:
			break;
		}
	}

	/**
	 * 按上键处理
	 * 
	 * @param v
	 * @param event
	 */
	private void onItemKeyUp(View v, KeyEvent event) {
		switch (v.getId()) {
		case R.id.province:
			mProvincePicker.nextItem();
			break;
		case R.id.city:
			mCityPicker.nextItem();
			break;
		case R.id.area:
			mAreaPicker.nextItem();
			break;
		default:
			break;
		}
	}

	/**
	 * 按下键处理
	 * 
	 * @param v
	 * @param event
	 */
	private void onItemKeyDown(View v, KeyEvent event) {
		switch (v.getId()) {
		case R.id.province:
			mProvincePicker.prevItem();
			break;
		case R.id.city:
			mCityPicker.prevItem();
			break;
		case R.id.area:
			mAreaPicker.prevItem();
			break;
		default:
			break;
		}
	}

	@Override
	protected void setDefaultFocusedChild() {
		// super.setDefaultFocusedChild();
		setDefaultFocusedChild(this);
	}

	private void setDefaultFocusedChild(View v) {
		ViewGroup layout = (ViewGroup) v;
		for (int i = 0, N = layout.getChildCount(); i < N; i++) {
			View child = layout.getChildAt(i);
			if (child instanceof ScrollerNumberPicker) {
				child.setOnFocusChangeListener(this);
				if (currentRect.isEmpty()) {
					child.getHitRect(currentRect);
					child.requestFocus();
				}
			}
			if (child instanceof ViewGroup) {
				setDefaultFocusedChild(child);
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//mCityPicker.getViewRect(outRect);
		//canvas.drawLine(outRect.left, outRect.top, outRect.left, outRect.bottom, linePaint);
		//canvas.drawLine(outRect.right, outRect.top, outRect.right, outRect.bottom, linePaint);

		super.onDraw(canvas);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		LayoutInflater.from(getContext()).inflate(R.layout.ui_weather_citypicker, this);
		// 获取控件引用
		mProvincePicker = (ScrollerNumberPicker) findViewById(R.id.province);
		mCityPicker = (ScrollerNumberPicker) findViewById(R.id.city);
		mAreaPicker = (ScrollerNumberPicker) findViewById(R.id.area);

		// mProvincePicker.requestFocus();

		// 设置省数据
		mProvincePicker.setData(CityManager.getInstance(mContext).getProvince());
		// 设置县数据
		mCityPicker.setData(CityManager.getInstance(mContext).getCity("01"));
		// 设置市镇数据
		mCityList = CityManager.getInstance(mContext).getArea("0101");
		mAreaId = mAreaPicker.setData(mCityList);

		mProvincePicker.setOnSelectListener(new OnSelectListener() {
			@Override
			public void endSelect(int id, String city_id, String cityName) {
				if (StringUtils.isEmpty(cityName)) {
					return;
				}

				if (mProvincePicker.getSelectedText().equals(cityName)) {
					if (!mProvinceId.equals(city_id)) {
						mProvinceId = city_id;
						mCityId = mCityPicker.setData(CityManager.getInstance(mContext).getCity(city_id));
						if (!StringUtils.isEmpty(mCityId)) {
							mCityList.clear();
							mCityList = CityManager.getInstance(mContext).getArea(mCityId);
							mAreaId = mAreaPicker.setData(mCityList);
						}
					}
				}

			}

			@Override
			public void selecting(int id, String city_id, String cityName) {
			}
		});

		mCityPicker.setOnSelectListener(new OnSelectListener() {
			@Override
			public void endSelect(int id, String city_id, String cityName) {
				if (StringUtils.isEmpty(cityName)) {
					return;
				}

				if (mCityPicker.getSelectedText().equals(cityName)) {
					if (!mCityId.equals(city_id)) {
						mCityId = city_id;
						mCityList.clear();
						mCityList = CityManager.getInstance(mContext).getArea(city_id);
						mAreaId = mAreaPicker.setData(mCityList);
					}
				}

			}

			@Override
			public void selecting(int id, String city_id, String cityName) {
			}
		});

		mAreaPicker.setOnSelectListener(new OnSelectListener() {
			@Override
			public void endSelect(int id, String city_id, String cityName) {
				if (StringUtils.isEmpty(cityName)) {
					return;
				}
				if (mAreaPicker.getSelectedText().equals(cityName)) {
					if (!mAreaId.equals(city_id)) {
						mAreaId = city_id;
					}
				}
			}

			@Override
			public void selecting(int id, String city_id, String cityName) {
			}
		});
	}

	/**
	 * 保存选择城市信息
	 * @return
	 */
	public void getSaveCurrentCity() {
		for (CityInfo info : mCityList) {
			if (info.city_id.equals(mAreaId)) {
				CityManager.getInstance(mContext).setDefaultCityAreaid(info.weather_id);
				CityManager.getInstance(mContext).setDefaultCityName(info.cityName);
				LogUtils.i(KeyUtils.TAG_LHC, "当前城市："+info.cityName);
				return;
			}
		}
	}

}
