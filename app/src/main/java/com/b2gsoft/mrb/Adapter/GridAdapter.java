package com.b2gsoft.mrb.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2gsoft.mrb.Model.Category;
import com.b2gsoft.mrb.R;

import java.util.List;

public class GridAdapter extends BaseAdapter {

    private Context context;
    private List<Category> categoryList;

    public GridAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.custom_grid, parent, false);
        }

        TextView textView = (TextView)convertView.findViewById(R.id.tv_grid);

        textView.setText(categoryList.get(position).getName());

        return convertView;
    }
}
