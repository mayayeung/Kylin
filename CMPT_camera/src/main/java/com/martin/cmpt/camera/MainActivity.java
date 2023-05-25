package com.martin.cmpt.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


/**
 * Created by DingJinZhu on 2020/5/28.
 * Description:
 */
public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_main);
        Button settings = findViewById(R.id.camera_settings);
        settings.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ddd", "MainActivity---task id: " + getTaskId());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.camera_settings) {
            Intent it = new Intent(this, VideoRecordActivity.class);
            startActivity(it);
        }
    }

}
