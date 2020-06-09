package com.martin.cmpt.camera;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by DingJinZhu on 2020/5/28.
 * Description:
 */
public class CameraActivity extends FragmentActivity {
    private FragPageAdapter adapter;
    private MenuScrollTitle menu;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<Fragment> fragments=new ArrayList<>();
    private String[] titles = {"普通模式", "拍题模式","拍题模式"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        initView();
    }

    private void initView() {
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tablayout_container);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        fragments.add(new FirstFragment());
        fragments.add(new SecondFragment());
        fragments.add(new SecondFragment());
        for (String tab : titles) {
            tabLayout.addTab(tabLayout.newTab().setText(tab));
        }
        adapter = new FragPageAdapter(getSupportFragmentManager(), titles, fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }


}
