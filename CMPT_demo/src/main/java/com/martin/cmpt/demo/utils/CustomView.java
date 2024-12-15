package com.martin.cmpt.demo.utils;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Created by DingJinZhu on 2022/10/8.
 * Description:
 */
public class CustomView extends TextView {


    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //宽度
        String specMode_width = "";
        int specModeWidth = MeasureSpec.getMode(widthMeasureSpec);
        switch (specModeWidth) {
            case MeasureSpec.EXACTLY:
                specMode_width = "EXACTLY";
                break;
            case MeasureSpec.AT_MOST:
                specMode_width = "AT_MOST";
                break;
            case MeasureSpec.UNSPECIFIED:
                specMode_width = "UNSPECIFIED";
                break;
        }
        //高度
        String specMode_height = "";
        int specModeHeight = MeasureSpec.getMode(heightMeasureSpec);
        switch (specModeHeight) {
            case MeasureSpec.UNSPECIFIED:
                specMode_height = "UNSPECIFIED";
                break;
            case MeasureSpec.AT_MOST:
                specMode_height = "AT_MOST";
                break;
            case MeasureSpec.EXACTLY:
                specMode_height = "EXACTLY";
                break;
        }
        Log.e("TAG", "specMode_width = " + specMode_width + " , specMode_height = " +
                specMode_height);
        Log.e("TAG", "specSize_width = " + MeasureSpec.getSize(widthMeasureSpec) +
                " , specSize_height = " + MeasureSpec.getSize(heightMeasureSpec));
    }


}
