package com.qimai;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class ExpandLinearLayout extends LinearLayout {
    public ExpandLinearLayout(Context context) {
        super(context);
    }

    public ExpandLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        setPadding(0,-getMeasuredHeight(), 0,0);
    }
}
