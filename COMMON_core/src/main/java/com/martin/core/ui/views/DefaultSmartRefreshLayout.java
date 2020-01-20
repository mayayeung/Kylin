package com.martin.core.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.martin.core.R;

/**
 * Created by DingJinZhu on 2019/6/20.
 * Description: SmartRefreshLayout的默认配置，包括了Material风格的header，球脉冲风格的footer
 */
public class DefaultSmartRefreshLayout extends SmartRefreshLayout {
    public DefaultSmartRefreshLayout(Context context) {
        super(context);
    }

    public DefaultSmartRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DefaultSmartRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        setDefaultConfig();
    }

    private void setDefaultConfig() {
        //设置 Header 为 Material风格
        this.setRefreshHeader(new MaterialHeader(getContext()).setShowBezierWave(false));
        //设置 Footer 为 球脉冲
        this.setRefreshFooter(new BallPulseFooter(getContext())
                .setSpinnerStyle(SpinnerStyle.Translate)
                .setAnimatingColor(getResources().getColor(R.color.app_color)));
        this.setEnableLoadMoreWhenContentNotFull(false);
        this.setEnableFooterFollowWhenNoMoreData(true);
    }
}
