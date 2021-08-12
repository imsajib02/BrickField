package com.b2gsoft.mrb.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2gsoft.mrb.Model.Brick;
import com.b2gsoft.mrb.R;

import java.util.ArrayList;

public class BrickAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Brick> brickArrayList;

    public BrickAdapter(Context context, ArrayList<Brick> brickArrayList) {
        this.context = context;
        this.brickArrayList = brickArrayList;
    }

    public int getCount() {
        return this.brickArrayList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.brick_list_layout, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        TextView tvUnit = (TextView) convertView.findViewById(R.id.tv_unit);

        final Brick brick = (Brick) this.brickArrayList.get(position);

        tvName.setText(brick.name);
        tvUnit.setText(brick.unit);

        return convertView;
    }
}
