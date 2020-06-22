package com.martin.cmpt.player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.martin.core.utils.CrumbView;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core_activity_main);

        CrumbView crumbView = (CrumbView) findViewById(R.id.crumb_view);
        crumbView.setActivity(this);

        int firstLevel = 1;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setBreadCrumbTitle(getString(R.string.crumb_title, firstLevel));
        ft.replace(R.id.frag_container, MyFragment.getInstance(firstLevel));
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    public static void launchSelf(Context context) {
        Intent intent = new Intent(context, PlayerActivity.class);
        context.startActivity(intent);
    }
}
