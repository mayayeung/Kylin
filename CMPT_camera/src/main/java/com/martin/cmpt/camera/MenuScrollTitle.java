package com.martin.cmpt.camera;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.martin.cmpt.camera.Utils.ScreenUtils;

import java.util.List;

public class MenuScrollTitle extends HorizontalScrollView implements RadioGroup.OnCheckedChangeListener {
    private View mView;
    private RadioGroup rg_all_title;
    private ImageView iv_leng;
    private List<String> mDatas;
    private int mDefaultId = 56845;
    private ViewPager viewPager;
 
    public MenuScrollTitle(Context context) {
        this(context, null);
    }
 
    public MenuScrollTitle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
 
    public MenuScrollTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
 
    private void initView() {
        mView = inflate(getContext(), R.layout.item_title_scrollview, this);
        rg_all_title = mView.findViewById(R.id.rg_all_title);
        iv_leng = mView.findViewById(R.id.iv_leng);
        rg_all_title.setOnCheckedChangeListener(this);
    }
 
    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        initViewPager();
    }
 
    private void initViewPager() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                onMoveLeng(position, positionOffset);
            }
 
            @Override
            public void onPageSelected(int position) {
                onMoveRadio(position);
            }
 
            @Override
            public void onPageScrollStateChanged(int state) {
 
            }
        });
    }
 
    public void onMoveLeng(int id, float positionOffset) {
        RadioButton button = findViewById(id + mDefaultId);
        int left = button.getLeft();
        int width = button.getWidth() / 2;
        int lengmove = (int) (left + (width - iv_leng.getWidth() / 2) + (positionOffset * width * 2));
        final int moveX = (int) (left - ScreenUtils.getScreenWidth(getContext()) / 2 + width + (positionOffset * width * 2));
        smoothScrollTo(moveX, 0);
        iv_leng.setTranslationX(lengmove);
    }
 
    public void onMoveRadio(int id) {
        RadioButton button = findViewById(id + mDefaultId);
        button.setChecked(true);
    }
 
    public void setDatas(List<String> datas) {
        this.mDatas = datas;
        addTitle();
    }
 
    public void addTitle() {
        rg_all_title.removeAllViews();
        for (int i = 0; i < mDatas.size(); i++) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setBackground(null);
            radioButton.setButtonDrawable(null);
            radioButton.setText(mDatas.get(i));
            radioButton.setTextColor(Color.parseColor("#00ff00"));
            radioButton.setTextSize(13);
            radioButton.setPadding(40, 0, 40, 0);
            radioButton.setId(mDefaultId + i);
            rg_all_title.addView(radioButton);
            if (i == 0) {
                radioButton.setChecked(true);
            }
        }
 
    }
 
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (viewPager != null) {
            viewPager.setCurrentItem(checkedId - mDefaultId);
        }
    }
}