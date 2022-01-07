package com.martin.cmpt.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class DemoActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);
    }

    public static void launchSelf(Context context) {
        Intent intent = new Intent(context, DemoActivity.class);
        context.startActivity(intent);
    }


}
