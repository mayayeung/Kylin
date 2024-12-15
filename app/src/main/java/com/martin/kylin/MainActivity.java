package com.martin.kylin;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.gyf.barlibrary.ImmersionBar;
import com.martin.core.ui.adapter.FragmentAdapter;
import com.martin.core.ui.viewPager.NoScrollViewPagerMain;
import com.martin.core.utils.DensityUtils;
import com.martin.kylin.config.Constant;
import com.martin.kylin.fragment.FirstFragment;
import com.martin.kylin.fragment.FirstFragmentV2;
import com.martin.kylin.fragment.SecondFragment;
import com.martin.kylin.fragment.SecondFragmentV2;
import com.martin.kylin.fragment.ThirdFragment;
import com.martin.kylin.fragment.ThirdFragmentV2;


public class MainActivity extends AppCompatActivity {
    FragmentAdapter fragmentAdapter;
    LinearLayout layoutShadow;
    TabLayout tabLayout;
    NoScrollViewPagerMain viewPager;
    private Fragment[] fragmentArray = null;
    private int[] iconArray = null;
    private String[] titleArray = null;
    String currentTab;
    int lastIndex;
    private ImmersionBar immersionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        immersionBar = ImmersionBar.with(this);
        ImmersionBar.with(this)
                .statusBarColor(Constant.firstApp() ? R.color.app_main_color : R.color.app_main_color2)
                .statusBarDarkFont(false).init();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        initTableHost();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initTableHost() {

        viewPager.clearOnPageChangeListeners();
        tabLayout.clearOnTabSelectedListeners();
        viewPager.removeAllViews();
        viewPager.setAdapter(null);
        tabLayout.removeAllTabs();
        tabLayout.setupWithViewPager(null);
        getSupportFragmentManager().getFragments().clear();
        initFragmentArray();


        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentArray);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setOffscreenPageLimit(fragmentArray.length - 1);//预加载的数量
        tabLayout.setupWithViewPager(viewPager);//绑定
        setTabs(titleArray, iconArray);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);//设置TabLayout的模式
        //TabLayout与ViewPager的绑定
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentTab = fragmentArray[position].getClass().getSimpleName();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }


    private void initFragmentArray() {
        if (Constant.firstApp()) {
            iconArray = new int[]{R.drawable.app_msg_selector, R.drawable.app_work_selector, R.drawable.app_person_selector};
            fragmentArray = new Fragment[]{new FirstFragment(), new SecondFragment(),  new ThirdFragment()};
            titleArray = new String[]{"消息", "工作台", "我"};
            currentTab = FirstFragment.class.getSimpleName();
        }else {
            iconArray = new int[]{R.drawable.app2_tab_msg_selector, R.drawable.app2_tab_work_selector, R.drawable.app2_tab_person_selector};
            fragmentArray = new Fragment[]{new FirstFragmentV2(), new SecondFragmentV2(),  new ThirdFragmentV2()};
            titleArray = new String[]{"消息", "工作台", "我"};
            currentTab = FirstFragment.class.getSimpleName();
        }

    }

    /**
     * @param tab_titles tab条目名字
     * @param tab_imgs   tab上条目上的图片
     * @description: 设置添加Tab
     */
    private void setTabs(String[] tab_titles, int[] tab_imgs) {
        int tIndex = 0;
        for (int i = 0; i < tab_titles.length; i++) {
            //获取TabLayout的tab
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (i == tIndex) {
                //设置第默认选中
                tabLayout.selectTab(tab);
            }
            tab.setCustomView(getTabItemView(i));
        }
    }


    private View getTabItemView(final int index) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(Constant.firstApp() ? R.layout.main_tab_item : R.layout.main_tab_item2, null);
        if (!TextUtils.isEmpty(titleArray[index])) {
            TextView textView = view.findViewById(R.id.tab_item);
            textView.setText(titleArray[index]);
            Drawable drawable = ContextCompat.getDrawable(this, iconArray[index]);
            int size = DensityUtils.dp2px(this, 26);//设计图 26*26 dp
            drawable.setBounds(0, 0, size, size);
            textView.setCompoundDrawablePadding(DensityUtils.dp2px(this, 3));
            textView.setCompoundDrawables(null, drawable, null, null);
            textView.setOnClickListener(v -> {
                viewPager.setCurrentItem(index);
                lastIndex = index;
            });
        }
        return view;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (immersionBar != null) {
            immersionBar.destroy();
            immersionBar = null;
        }
    }
}
