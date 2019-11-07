package com.qimai;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class CanScrollLinearLayout extends LinearLayout {
    private static final String TAG = "CanScrollLinearLayout";
    boolean mIntercept = false;

    public CanScrollLinearLayout(Context context) {
        super(context);
    }

    public CanScrollLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CanScrollLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    float mLastY = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mIntercept = false;
                mLastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float delaY = ev.getY() - mLastY;
                if (Math.abs(delaY) > 20) {
                    mIntercept = true;
                    Log.d(TAG, "onInterceptTouchEvent:                     mIntercept = true");
                } else {
                    mIntercept = false;
                }
        }

        return mIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isHandle = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isHandle = true;
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float delaY = event.getY() - mLastY;
                if (Math.abs(delaY) > 20) {
                    isHandle = true;
                    Log.d(TAG, "onInterceptTouchEvent:                     onTouchEvent = true");
                    //处理header滑动

                }
                break;
        }


        return isHandle;
    }
}
