package com.martin.cmpt.player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;

public class PlayerActivity extends FragmentActivity {
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity_main);
        mainLayout = findViewById(R.id.main_layout);
        LayoutInflater inflater = LayoutInflater.from(this);
        View button = inflater.inflate(R.layout.button_layout, mainLayout,true);
    }

    public static void launchSelf(Context context) {
        Intent intent = new Intent(context, PlayerActivity.class);
        context.startActivity(intent);
    }
}
