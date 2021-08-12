package com.b2gsoft.mrb.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2gsoft.mrb.Model.LoadUnload;
import com.b2gsoft.mrb.R;

import java.util.ArrayList;
import java.util.List;

public class LoadUnloadAdapter extends BaseAdapter {

    private Context context;
    private List<LoadUnload> loadUnloadListList = new ArrayList<>();

    public LoadUnloadAdapter(Context context, List<LoadUnload> loadUnloadListList) {
        this.context = context;
        this.loadUnloadListList = loadUnloadListList;
    }

    public int getCount() {
        return this.loadUnloadListList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.layout_load_unload, parent, false);
        }

        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_date);
        TextView tvAmount = (TextView) convertView.findViewById(R.id.tv_amount);

        final LoadUnload loadUnload = loadUnloadListList.get(position);

        tvDate.setText(loadUnload.date);
        tvAmount.setText(loadUnload.chamberAmount);

        return convertView;
    }
}
