package com.martin.cmpt.camera;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by DingJinZhu on 2020/5/28.
 * Description:
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core_activity_main);
        ((TextView) findViewById(R.id.core_tv)).setText("Camera component page!");
    }
}
