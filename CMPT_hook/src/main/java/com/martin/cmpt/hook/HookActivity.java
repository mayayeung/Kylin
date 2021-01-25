package com.martin.cmpt.hook;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.LinearLayout;

public class HookActivity extends FragmentActivity {
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hook_activity_main);
        mainLayout = findViewById(R.id.main_layout);
    }

}
