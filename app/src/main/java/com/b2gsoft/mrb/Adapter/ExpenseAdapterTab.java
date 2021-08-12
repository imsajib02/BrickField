package com.b2gsoft.mrb.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2gsoft.mrb.Model.Expense;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Utils.RoundFormat;

import java.util.ArrayList;

public class ExpenseAdapterTab extends BaseAdapter {
    private Context context;
    private ArrayList<Expense> expenseArrayList;

    public ExpenseAdapterTab(Context context, ArrayList<Expense> expenseArrayList) {
        this.context = context;
        this.expenseArrayList = expenseArrayList;
    }

    public int getCount() {
        return this.expenseArrayList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.adapter_expense_tab, parent, false);
        }
       // TextView tvExpenseWay = (TextView) convertView.findViewById(R.id.tv_ex_expense_way);
        TextView tvUnit = (TextView) convertView.findViewById(R.id.tv_ex_unit);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.tv_ex_price);
        TextView tvTotal = (TextView) convertView.findViewById(R.id.tv_ex_total);
        TextView tvPaid = (TextView) convertView.findViewById(R.id.tv_ex_paid);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_ex_date);
        TextView tvDue = (TextView) convertView.findViewById(R.id.tv_ex_due);


        Expense expense = (Expense) this.expenseArrayList.get(position);
//        ((TextView) convertView.findViewById(R.id.tv_ex_name)).setText(expense.name);
     //   tvExpenseWay.setText(expense.expense_way_name);
        tvUnit.setText("" + RoundFormat.roundTwoDecimals(Double.parseDouble(expense.unit)));
        tvPrice.setText("" + RoundFormat.roundTwoDecimals(Double.parseDouble(expense.per_unit_price)));
        tvTotal.setText("" + RoundFormat.roundTwoDecimals(Double.parseDouble(expense.total_price)));
        tvDate.setText(expense.date);
        tvPaid.setText("" + RoundFormat.roundTwoDecimals(Double.parseDouble(expense.cash)));
        tvDue.setText("" + RoundFormat.roundTwoDecimals(Double.parseDouble(expense.due)));

        return convertView;
    }
}
