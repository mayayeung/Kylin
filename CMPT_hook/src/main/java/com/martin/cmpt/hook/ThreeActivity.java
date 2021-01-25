package com.martin.cmpt.hook;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2018/2/26 0026.
 */

public class ThreeActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Log.d("=======","==name====="+bundle.getString("name"));
            Log.d("=======","==age====="+bundle.getInt("age"));
        }

    }
}
