package com.martin.cmpt.demo.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by DingJinZhu on 2022/1/7.
 * Description:
 */
public class SimpleDoodleView extends View {
    private Path path;
    private Paint paint;

    public SimpleDoodleView(Context context) {
        super(context);
    }

    public SimpleDoodleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path,paint);
/*

        paint.setStrokeWidth(20);
        canvas.drawPoint(200,200,paint);

        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        canvas.drawCircle(200,200,100,paint);

        canvas.translate(200, 200);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(200,0,100,paint);
*/


        canvas.translate(400, 800);
        paint.setStrokeWidth(20);
        paint.setColor(Color.RED);
        canvas.drawPoint(0, 0, paint);

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        canvas.drawLine(-400, 0, getWidth() - 400, 0, paint);
        canvas.drawLine(0, -800, 0, getHeight() - 800, paint);

        paint.setColor(Color.BLUE);
        Rect rt = new Rect(0, -400, 400, 0);
        canvas.drawRect(rt, paint);

        canvas.save();
        canvas.scale(1.3f, 1.3f);
        paint.setColor(Color.GREEN);
        canvas.drawRect(rt, paint);
        canvas.restore();
        canvas.drawLine(0, 0, 400, -400, paint);

        canvas.save();
//        canvas.scale(1f, 1f, 200, 0);
        canvas.translate(200, 0);
        canvas.scale(0.5f, 0.5f);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(50);
        canvas.drawPoint(0, 0, paint);
//        paint.setColor(Color.MAGENTA);
//        paint.setStrokeWidth(5);
//        canvas.drawRect(rt, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }
}
