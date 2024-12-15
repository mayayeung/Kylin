package com.martin.cmpt.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ScanActivity extends AppCompatActivity {
    private LinearLayout mainLayout;
    private View scanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_activity);
        mainLayout = findViewById(R.id.main_layout);
        scanView = findViewById(R.id.scan_view);
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View button = inflater.inflate(R.layout.button_layout, mainLayout,true);
    }

    public static void launchSelf(Context context) {
        Intent intent = new Intent(context, ScanActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        scanQrCodeAnim(mainLayout, scanView, 400, 600, hasFocus);
    }

    public void scanQrCodeAnim(View frame, View animImg, int margintop, int marginBottom, boolean hasFocus) {
        int[] location = new int[2];
        frame.getLocationInWindow(location);
        int left = frame.getLeft();
        int right = frame.getRight();
        int top = frame.getTop();
        int bottom = frame.getBottom();

        Animation anim = new TranslateAnimation(left, left, top + margintop, bottom - marginBottom);
        anim.setDuration(3000);
        anim.setRepeatMode(Animation.RESTART);
        anim.setRepeatCount(Animation.INFINITE);

        if (hasFocus) {
            animImg.startAnimation(anim);
        } else {
            animImg.clearAnimation();
        }
    }

}
