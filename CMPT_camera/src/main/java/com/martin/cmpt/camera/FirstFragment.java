package com.martin.cmpt.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by DingJinZhu on 2020/6/9.
 * Description:
 */
public class FirstFragment extends Fragment {

    private Bundle bd;


    public static Fragment newInstance(Bundle bd) {
        Fragment frag = new FirstFragment();
        frag.setArguments(bd);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bd = getArguments();
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tv = view.findViewById(R.id.info_tv);
        tv.setText("Fragment ONE!");
    }

}
