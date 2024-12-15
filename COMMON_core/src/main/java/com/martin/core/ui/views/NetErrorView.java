package com.martin.core.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.martin.core.R;


public class NetErrorView extends LinearLayout {
    private Context context = null;
    private Button refreshBtn;
    private OnRefreshListener onRefreshListener;

    public interface OnRefreshListener {
        public void onRefresh();
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public NetErrorView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public NetErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public NetErrorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.net_error_view, this);
        refreshBtn = findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (onRefreshListener != null){
                    onRefreshListener.onRefresh();
                }
            }
        });

    }

    public void show() {
        this.setVisibility(VISIBLE);
    }

    public void hide() {
        this.setVisibility(GONE);
    }
}
