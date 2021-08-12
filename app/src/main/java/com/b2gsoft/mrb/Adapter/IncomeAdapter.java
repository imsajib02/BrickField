package com.b2gsoft.mrb.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2gsoft.mrb.Model.Income;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Utils.RoundFormat;

import java.util.ArrayList;

public class IncomeAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Income> incomeArrayList;

    public IncomeAdapter(Context context, ArrayList<Income> incomeArrayList) {
        this.context = context;
        this.incomeArrayList = incomeArrayList;
    }

    public int getCount() {
        return this.incomeArrayList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.adapter_monthly, parent, false);
        }

        TextView tvExpenseWay = (TextView) convertView.findViewById(R.id.tv_ex_expense_way);
        TextView tvTotal = (TextView) convertView.findViewById(R.id.tv_ex_total);
        TextView tvPaid = (TextView) convertView.findViewById(R.id.tv_ex_paid);
        TextView tvDue = (TextView) convertView.findViewById(R.id.tv_ex_due);
        TextView tvUnit = (TextView) convertView.findViewById(R.id.tv_ex_amount);


        Income income = (Income) this.incomeArrayList.get(position);

        tvTotal.setText("" + RoundFormat.roundTwoDecimals(Double.parseDouble(income.total_price)));
        //tvDate.setText(income.income_date);
        tvPaid.setText("" + RoundFormat.roundTwoDecimals(Double.parseDouble(income.cash)));
        tvDue.setText("" + RoundFormat.roundTwoDecimals(Double.parseDouble(income.due)));
        tvUnit.setText("" + RoundFormat.roundTwoDecimals(Double.parseDouble(income.unit)));
        tvExpenseWay.setText(income.class_name);


        return convertView;
    }
}