package com.b2gsoft.mrb.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2gsoft.mrb.Model.Reminder;
import com.b2gsoft.mrb.R;

import java.util.ArrayList;

public class ReminderAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Reminder> reminderArrayList;

    public ReminderAdapter(Context context, ArrayList<Reminder> reminderArrayList) {
        this.context = context;
        this.reminderArrayList = reminderArrayList;
    }

    @Override
    public int getCount() {
        return reminderArrayList.size();
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
            convertView = LayoutInflater.from(this.context).inflate(R.layout.adapter_vault, parent, false);
        }

        TextView textViewDate = (TextView)convertView.findViewById(R.id.tv_date);
        TextView textViewMsg = (TextView)convertView.findViewById(R.id.tv_msg);

        Reminder reminder = reminderArrayList.get(position);

        textViewDate.setText(reminder.date);
        textViewMsg.setText(reminder.msg);

        return convertView;
    }
}
