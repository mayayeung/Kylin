package com.martin.cmpt.camera.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.martin.cmpt.camera.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Squirrel桓 on 2018/12/15.
 */
public class HorizontalAdapter implements AutoCenterHorizontalScrollView.HAdapter {
    List<String> names = new ArrayList<>();
    private Context context;

    public HorizontalAdapter(Context context, List<String> names) {
        this.names = names;
        this.context = context;
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemView(int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_tab_layout, null, false);
        HViewHolder hViewHolder = new HViewHolder(v);
        hViewHolder.textView.setText(names.get(i));
        return hViewHolder;
    }

    @Override
    public void onSelectStateChanged(RecyclerView.ViewHolder viewHolder, int position, boolean isSelected) {
        if (isSelected) {
            ((HViewHolder) viewHolder).textView.setTextColor(Color.parseColor("#4991E1"));
            ((HViewHolder) viewHolder).textView.setTypeface(Typeface.DEFAULT_BOLD);
            ((HViewHolder) viewHolder).indicator.setVisibility(View.VISIBLE);
        } else {
            ((HViewHolder) viewHolder).textView.setTextColor(Color.parseColor("#FFFFFF"));
            ((HViewHolder) viewHolder).textView.setTypeface(Typeface.DEFAULT);
            ((HViewHolder) viewHolder).indicator.setVisibility(View.GONE);
        }
    }

    public static class HViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;
        public final View indicator;

        public HViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_tab_name);
            indicator = itemView.findViewById(R.id.tv_tab_indicator);
        }
    }
}
