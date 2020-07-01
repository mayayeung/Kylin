package com.martin.cmpt.camera.widget;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import me.pqpo.smartcropperlib.view.CropImageView;

/**
 * Created by DingJinZhu on 2020/7/1.
 * Description:
 */
public class MyCropImageView extends CropImageView {
    private static final String TAG = "CropImageView";

    public MyCropImageView(Context context) {
        super(context);
    }

    public MyCropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setFullImgCrop() {
        super.setFullImgCrop();
        if (getDrawable() == null) {
            Log.w(TAG, "should call after set drawable");
            return;
        }
        setCropPoints(getFullImgCropPoints());
    }

    private Point[] getFullImgCropPoints() {
        Point[] points = new Point[4];
        Drawable drawable = getDrawable();
        if (drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            points[0] = new Point(0, 0);
            points[1] = new Point(width, 0);
            points[2] = new Point(width, height);
            points[3] = new Point(0, height);
        }
        return points;
    }
}
