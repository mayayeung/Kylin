package com.martin.cmpt.camera;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

/**
 * Created by DingJinZhu on 2020/5/28.
 * Description:
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_main);

        FrameLayout preView = findViewById(R.id.camera_preview);
        CameraPreview cameraView = new CameraPreview(this);
        preView.addView(cameraView);
    }
}
