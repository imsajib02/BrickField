package com.b2gsoft.mrb.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2gsoft.mrb.Model.Pot;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Interface.UpdatePot;
import com.b2gsoft.mrb.Utils.SharedPreferences;
import com.b2gsoft.mrb.Utils.StaticValue;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class PotAdapter extends BaseAdapter {

    private SharedPreferences sharedPreferences;
    private Context context;
    private ArrayList<Pot> potArrayList;
    private UpdatePot updatePot;
    private NumberFormat numberFormatter;

    public PotAdapter(Context context, ArrayList<Pot> potArrayList, UpdatePot updatePot) {
        this.context = context;
        sharedPreferences = new SharedPreferences(context);
        this.potArrayList = potArrayList;
        this.updatePot = updatePot;
        this.numberFormatter = new DecimalFormat("#0.00");
    }

    public int getCount() {
        return this.potArrayList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.adapter_pot, parent, false);
        }

        TextView tvPotNo = (TextView) convertView.findViewById(R.id.tv_pot_no);
        TextView tvLine = (TextView) convertView.findViewById(R.id.tv_pot_line);
        TextView tvAmount = (TextView) convertView.findViewById(R.id.tv_pot_amount);
        TextView tvTotal = (TextView) convertView.findViewById(R.id.tv_pot_total);

        final Pot pot = (Pot) this.potArrayList.get(position);

        ((TextView) convertView.findViewById(R.id.tv_pot_date)).setText(pot.date);
        tvPotNo.setText(pot.potNo);
        tvLine.setText(pot.line);
        tvAmount.setText("" + numberFormatter.format(Double.parseDouble(pot.amount)));
        tvTotal.setText("" + numberFormatter.format(Double.parseDouble(pot.total)));

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(TextUtils.equals(sharedPreferences.getRole(), StaticValue.Admin)) {

                    updatePot.UpdatePot(pot);
                }
                else if(TextUtils.equals(sharedPreferences.getRole(), StaticValue.Manager)) {

                    if(TextUtils.equals(sharedPreferences.getEditPermission(), "1")) {

                        updatePot.UpdatePot(pot);
                    }
                }

                return false;
            }
        });

        return convertView;
    }
}