package com.martin.cmpt.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.martin.cmpt.demo.utils.DensityUtil;
import com.martin.core.utils.DensityUtils;


//扇形进度条
public class SectorProgressView extends View {
    int min_size = DensityUtil.dp2px(50);
    Paint paint1, paint2, paint3; //paint1 外圆  paint2 内圆  paint3 扇形
    int color1 = Color.parseColor("#FFFFFF"), color2 = Color.parseColor("#50000000"), color3 = Color.parseColor("#FFFFFF");
    int ringWidth = DensityUtil.dp2px(2);
    int progress = 0;

    public SectorProgressView(Context context) {
        super(context);
        initPaint();
    }

    public SectorProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public SectorProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    private void initPaint() {
        paint1 = new Paint();
        paint1.setAntiAlias(true);
        paint1.setColor(color1);
        paint1.setStrokeWidth(ringWidth);
        paint1.setStyle(Paint.Style.STROKE);

        paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setColor(color2);

        paint3 = new Paint();
        paint3.setAntiAlias(true);
        paint3.setColor(color3);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = min_size;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int outerRadius = Math.min(width / 2, height / 2) - ringWidth / 2;
        canvas.drawCircle(width / 2, height / 2, outerRadius, paint1);
        canvas.drawCircle(width / 2, height / 2, outerRadius - ringWidth / 2, paint2);
        int sectorRadius = outerRadius - ringWidth;
        RectF rectF = new RectF(width / 2 - sectorRadius, height / 2 - sectorRadius, width / 2 + sectorRadius, height / 2 + sectorRadius);
        canvas.drawArc(rectF, 0, (int) (360 * progress / 100.0), true, paint3);
    }
}
