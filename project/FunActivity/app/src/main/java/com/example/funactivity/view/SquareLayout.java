package com.example.funactivity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SquareLayout extends RelativeLayout {
    public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (height != width) {
            // 强制让高度等于宽度，重新布局
            height = width;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
