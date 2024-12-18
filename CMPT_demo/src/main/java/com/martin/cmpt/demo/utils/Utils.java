package com.martin.cmpt.demo.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class Utils {
    public static float dp2px(float paramFloat) {
        return TypedValue.applyDimension(1, paramFloat, Resources.getSystem().getDisplayMetrics());
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
