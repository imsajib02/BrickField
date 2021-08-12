package com.b2gsoft.mrb.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2gsoft.mrb.Model.Advance;
import com.b2gsoft.mrb.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class SummeryAdvanceAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Advance> displayedAdvanceList = new ArrayList<>();
    private NumberFormat numberFormatter;

    public SummeryAdvanceAdapter(Context context, ArrayList<Advance> displayedAdvanceList) {
        this.context = context;
        this.displayedAdvanceList = displayedAdvanceList;
        this.numberFormatter = new DecimalFormat("#0.00");
    }

    public int getCount() {
        return this.displayedAdvanceList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.summery_advance_layout, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        TextView tvUnit = (TextView) convertView.findViewById(R.id.tv_unit);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
        TextView tvTaken = (TextView) convertView.findViewById(R.id.tv_taken);
        TextView tvPorishodh = (TextView) convertView.findViewById(R.id.tv_porishodh);
        TextView tvRemarks = (TextView) convertView.findViewById(R.id.tv_remarks);


        final Advance advance = displayedAdvanceList.get(position);

        tvName.setText(advance.name);

        if(!TextUtils.equals(advance.unit, "null") && !TextUtils.equals(advance.unit, "")) {
            tvUnit.setText("" + numberFormatter.format(Double.parseDouble(advance.unit)));
        }
        else {
            tvUnit.setText("");
        }

        if(!TextUtils.equals(advance.per_unit_price, "null") && !TextUtils.equals(advance.per_unit_price, "")) {
            tvPrice.setText("" + numberFormatter.format(Double.parseDouble(advance.per_unit_price)));
        }
        else {
            tvPrice.setText("");
        }

        if(TextUtils.equals(advance.given, "null") || TextUtils.isEmpty(advance.given) || advance.given == null) {

            tvTaken.setText("");
        }
        else {

            tvTaken.setText("" + numberFormatter.format(Double.parseDouble(advance.given)));
        }

        if(TextUtils.equals(advance.paid, "null") || TextUtils.isEmpty(advance.paid) || advance.paid == null) {

            tvPorishodh.setText("");
        }
        else {

            tvPorishodh.setText("" + numberFormatter.format(Double.parseDouble(advance.paid)));
        }

        if(TextUtils.equals(advance.remarks, "null") || TextUtils.isEmpty(advance.remarks) || advance.remarks == null) {

            tvRemarks.setText("");
        }
        else {

            tvRemarks.setText(advance.remarks);
        }

        return convertView;
    }
}
