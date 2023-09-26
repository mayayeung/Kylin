package com.martin.kylin;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.martin.cmpt.demo.DemoActivity;
import com.martin.core.callback.MyCallback;
import com.martin.core.utils.BitmapUtils;
import com.martin.core.utils.ToastUtils;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.demo_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DemoActivity.launchSelf(MainActivity.this);
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "camera");
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        ToastUtils.showToastOnce("failed to create media file directory");
                        return;
                    }
                }

                File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "111" + ".jpg");
                if (mediaFile.exists()) {
                    ToastUtils.showToastOnce("exist !!!");
                }
                BitmapUtils.createScaledBitmap(MainActivity.this,mediaStorageDir.getPath()+"/111.jpg",700, new MyCallback<String>() {
                    @Override
                    public void onPrepare() {
                    }

                    @Override
                    public void onSuccess(String imgPath) {
                        ToastUtils.showToastOnce(imgPath);
                    }

                    @Override
                    public void onError() {
                    }
                });
            }
        });
    }
}
