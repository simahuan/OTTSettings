package com.pisen.ott.settings.common.screenpro;

import java.util.HashMap;
import java.util.LinkedList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.pisen.ott.settings.R;

/**
 * A FrameLayout that holds two photos, back to back.
 * 重新继承的布局
 */
public class PhotoCarousel extends FrameLayout {
    private static final String TAG = "PhotoCarousel";
    private static final boolean DEBUG = false;

    private static final int LANDSCAPE = 1;
    private static final int PORTRAIT = 2;
    private final Flipper mFlipper;
    private final PhotoSourcePlexor mPhotoSource;
    private final GestureDetector mGestureDetector;
    private final View[] mPanel;
    private final int mFlipDuration;
    private final int mDropPeriod;
    private final int mBitmapQueueLimit;
    private final HashMap<View, Bitmap> mBitmapStore;
    private final LinkedList<Bitmap> mBitmapQueue;
    private final LinkedList<PhotoLoadTask> mBitmapLoaders;
//  private View mSpinner;
    private int mOrientation;
    private int mWidth;
    private int mHeight;
    private int mLongSide;
    private int mShortSide;
    private long mLastFlipTime;
    private Context mContext;

    //图片源如何加入的问题
	 Resources res = getResources();
	
	 Bitmap bm1 = BitmapFactory.decodeResource(res, R.drawable.test_screenpro_one);
	 Bitmap bm2 = BitmapFactory.decodeResource(res, R.drawable.test_screenpro_two);
	 Bitmap bm3 = BitmapFactory.decodeResource(res, R.drawable.test_screenpro_three);
	 Bitmap bm4 = BitmapFactory.decodeResource(res, R.drawable.test_screenpro_four);
//	 Bitmap bm5 = BitmapFactory.decodeResource(res, R.drawable.test_screenpro_five);
//	 Bitmap bm6 = BitmapFactory.decodeResource(res, R.drawable.test_screenpro_six);
//	 Bitmap bm7 = BitmapFactory.decodeResource(res, R.drawable.test_screenpro_seven);
	
	 class Flipper implements Runnable {
        public void run() {
            if (mBitmapQueue.isEmpty()) {
            		mBitmapQueue.add(bm1);
            		mBitmapQueue.add(bm2);
            		mBitmapQueue.add(bm3);
            		mBitmapQueue.add(bm4);
//            		mBitmapQueue.add(bm5);
//            		mBitmapQueue.add(ImageUtils.resizeBitmap(bm6, 800, 444));
//            		mBitmapQueue.add(ImageUtils.resizeBitmap(bm7, 800, 444));
            } 
            long now = System.currentTimeMillis();
            long elapsed = now - mLastFlipTime;
            Log.d(TAG,"elapsed=:"+elapsed);
            Log.d(TAG,"mDropPeriod=:"+mDropPeriod);
            
            if (elapsed < mDropPeriod) {
                scheduleNext((int) mDropPeriod - elapsed);  //存在动画的调度.周期是相减时间
            } else {
                scheduleNext(mDropPeriod);					//存在动画的调度
                if (changePhoto() ||(elapsed > (5 * mDropPeriod) && canFlip())) {
                    flip(1f);
                    mLastFlipTime = now;
                }
            }
        }

        private void scheduleNext(long delay) {
            removeCallbacks(mFlipper);
            postDelayed(mFlipper, delay);
        }
    }


    public PhotoCarousel(Context context, AttributeSet as) {
        super(context, as);
        this.mContext = context;
        Log.d(TAG,"PhotoCarousel()*****");
        final Resources resources = getResources();
       
        mDropPeriod = resources.getInteger(R.integer.carousel_drop_period);  				//5000ms
        mBitmapQueueLimit = resources.getInteger(R.integer.num_images_to_preload);  		//位图队列限制,5张 
        mFlipDuration = resources.getInteger(R.integer.flip_duration);                      //配置文件可以这样写
                                                                                             
        mPhotoSource = new PhotoSourcePlexor(getContext(),
                getContext().getSharedPreferences("FlipperDream", 0)); 						//要找到相册源 
        
        mBitmapStore = new HashMap<View, Bitmap>();
        mBitmapQueue = new LinkedList<Bitmap>();
        mBitmapLoaders = new LinkedList<PhotoLoadTask>();  									//链式集合装载_图片装载任务,可能注释
        
        mPanel = new View[2];
        mFlipper = new Flipper();
        // this is dead code if the dream calls setInteractive(false)
        mGestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
                        log("fling with " + vX);
                        flip(Math.signum(vX));
                        return true;
                    }
                });
    }
    
    private float lockTo180(float a) {
        return 180f * (float) Math.floor(a / 180f);
    }
    
    private float wrap360(float a) {
        return a - 360f * (float) Math.floor(a / 360f);
    }

    private class PhotoLoadTask extends AsyncTask<Void, Void, Bitmap> {
        private final BitmapFactory.Options mOptions;

        public PhotoLoadTask () {
            mOptions = new BitmapFactory.Options();
            mOptions.inTempStorage = new byte[32768];
        }

        @Override
        public Bitmap doInBackground(Void... unused) {
            Bitmap decodedPhoto;
            if (mLongSide == 0 || mShortSide == 0) {
                return null;
            }
            decodedPhoto = mPhotoSource.next(mOptions, mLongSide, mShortSide);
            return decodedPhoto;
        }

        @Override
        public void onPostExecute(Bitmap photo) {
        	 Log.w(TAG,"onPostExecute_photo =:"+photo);
            if (photo != null) {
                mBitmapQueue.offer(photo);
            }
            mFlipper.run();
        }
    };
    
    private ImageView getBackface() {
        return (ImageView) ((mPanel[0].getAlpha() < 0.5f) ? mPanel[0] : mPanel[1]);
    }

    private boolean canFlip() {
    	 Log.e(TAG,"canFlip=:"+mBitmapStore.containsKey(getBackface()));
         return mBitmapStore.containsKey(getBackface());
    }

    private boolean changePhoto() {
        Bitmap photo = mBitmapQueue.poll();  //这里获得图片
        Log.d(TAG,"photo=:"+photo);
        
        if (photo != null) {
            ImageView destination = getBackface();
            int width = photo.getWidth();
            int height = photo.getHeight();
            int orientation = (width > height ? LANDSCAPE : PORTRAIT);
            
            destination.setImageBitmap(photo);//设置图片 
            destination.setTag(R.id.photo_orientation, Integer.valueOf(orientation));
            destination.setTag(R.id.photo_width, Integer.valueOf(width));
            destination.setTag(R.id.photo_height, Integer.valueOf(height));          
//            setScaleType(destination);//设置集合

            return true;
        } else {
        	 Log.e(TAG,"changePhoto=:photo=null");
             return false;
        }
    }
    
//  设置缩放比例	
    private void setScaleType(View photo) {
        if (photo.getTag(R.id.photo_orientation) != null) {
            int orientation = ((Integer) photo.getTag(R.id.photo_orientation)).intValue();
            int width = ((Integer) photo.getTag(R.id.photo_width)).intValue();
            int height = ((Integer) photo.getTag(R.id.photo_height)).intValue();
            
            if (width < mWidth && height < mHeight) {
                log("too small: FIT_CENTER");
                ((ImageView) photo).setScaleType(ImageView.ScaleType.FIT_XY);
            } else if (orientation == mOrientation) {
                log("orientations match: CENTER_CROP");
                ((ImageView) photo).setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
                log("orientations do not match: CENTER_INSIDE");
                ((ImageView) photo).setScaleType(ImageView.ScaleType.FIT_XY);
            }
        } else {
            	log("no tag!,没有存入图片的方向问题.....");
        }
    }

    
    public void flip(float sgn) {
    	Log.w(TAG,"flip调用....");
        mPanel[0].animate().cancel();
        mPanel[1].animate().cancel();

        float frontY = mPanel[0].getRotationY();
        float backY = mPanel[1].getRotationY();
        float frontA = mPanel[0].getAlpha();
        float backA = mPanel[1].getAlpha();
        
        frontY = wrap360(frontY);
        backY = wrap360(backY);
        
        mPanel[0].setRotationY(frontY);
        mPanel[1].setRotationY(backY);
        
        frontY = lockTo180(frontY + sgn * 180f);
        backY = lockTo180(backY + sgn * 180f);
        frontA = 1f - frontA;
        backA = 1f - backA;
        // Don't rotate
        frontY = backY = 0f;
        ViewPropertyAnimator frontAnim = mPanel[0].animate()
                .rotationY(frontY)
                .alpha(frontA)
                .setDuration(mFlipDuration);
        
        ViewPropertyAnimator backAnim = mPanel[1].animate()
                .rotationY(backY)
                .alpha(backA)
                .setDuration(mFlipDuration
                );
        frontAnim.start();
        backAnim.start();
    }
    
    @Override
    public void onAttachedToWindow() {
        mPanel[0]= findViewById(R.id.front);
        mPanel[1] = findViewById(R.id.back);
        mFlipper.run();
    }
    
    @Override
    protected void onDetachedFromWindow() {
    	 Log.w(TAG,"onDetachedFromWindow=:停止白日梦！");
    	 if(null!=mFlipper){
    	   removeCallbacks(mFlipper);
    	 }
    	super.onDetachedFromWindow();
    }
    
    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mHeight = bottom - top;
        mWidth = right - left;
        mOrientation = (mWidth > mHeight ? LANDSCAPE : PORTRAIT);
        mLongSide = (int) Math.max(mWidth, mHeight);
        mShortSide = (int) Math.min(mWidth, mHeight);
        setScaleType(mPanel[0]);
        setScaleType(mPanel[1]);
        super.onLayout(changed, left, top, right, bottom);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        	mGestureDetector.onTouchEvent(event);
        	return true;
    }

    private void log(String message) {
        if (DEBUG) {
            Log.i(TAG, message);
        }
    }
}
