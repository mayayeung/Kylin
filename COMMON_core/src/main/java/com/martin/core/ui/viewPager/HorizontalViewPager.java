package com.martin.core.ui.viewPager;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.viewpager.widget.ViewPager;


/**
 * Created by Wang on 2017/4/13.
 */

public class HorizontalViewPager extends ViewPager {
    private final static String TAG = "HorizontalViewPager";
    public HorizontalViewPager(Context context) {
        super(context);
    }

    public HorizontalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    int mLastX;
    int mLastY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            boolean intercept =  super.onInterceptTouchEvent(ev);
            Log.d(TAG,"onInterceptTouchEvent:"+ ev.getAction() + ":"+intercept);
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            switch (ev.getAction()){
                case  MotionEvent.ACTION_DOWN: {
                    break;
                }
                case  MotionEvent.ACTION_MOVE: {
                    if(!intercept && (Math.abs(x-mLastX) > Math.abs(y-mLastY))){
                        intercept = true;
                    }
                    break;
                }
                case  MotionEvent.ACTION_UP: {
                    break;
                }
                default:
                    break;

            }
            mLastX = x;
            mLastY = y;
            return intercept;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean touch = super.onTouchEvent(ev);
        Log.d(TAG,"onTouchEvent:"+ ev.getAction() + ":"+touch);
        return touch;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        Log.d(TAG,"canScrollHorizontally:"+direction);
        return super.canScrollHorizontally(direction);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        Log.d(TAG,"canScroll:"+v.getClass().getName());
        return super.canScroll(v, checkV, dx, x, y);
    }
}
