package com.martin.kylin.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.martin.core.ui.views.TitleActionBar;
import com.martin.kylin.R;


public class SecondFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        initUI(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initUI(View view) {
        TitleActionBar mTitleBar = view.findViewById(R.id.title_bar);
        mTitleBar.onMoreBtnClick(new TitleActionBar.OnBtnClickListener() {
            @Override
            public void doNext(View view) {
            }
        });



    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}
