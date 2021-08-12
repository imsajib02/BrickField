package com.b2gsoft.mrb.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2gsoft.mrb.Model.Expense;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Interface.DueSubmit;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class DueExpenseAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Expense> expenseArrayList;
    private DueSubmit dueSubmit;
    private NumberFormat numberFormatter;

    public DueExpenseAdapter(Context context, ArrayList<Expense> expenseArrayList, DueSubmit dueSubmit) {
        this.context = context;
        this.expenseArrayList = expenseArrayList;
        this.dueSubmit = dueSubmit;
        this.numberFormatter = new DecimalFormat("#0.00");
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
            convertView = LayoutInflater.from(this.context).inflate(R.layout.adapter_due_expense, parent, false);
        }
        TextView tvExpenseWay = (TextView) convertView.findViewById(R.id.tv_ex_expense_way);
        TextView tvTotal = (TextView) convertView.findViewById(R.id.tv_ex_total);
        TextView tvPaid = (TextView) convertView.findViewById(R.id.tv_ex_paid);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_ex_date);
        TextView tvDue = (TextView) convertView.findViewById(R.id.tv_ex_due);
        TextView tvRemarks = (TextView) convertView.findViewById(R.id.tv_remarks);
        //ImageView imageView = (ImageView)convertView.findViewById(R.id.iv_ex_pay);

        final Expense expense = (Expense) this.expenseArrayList.get(position);

        ((TextView) convertView.findViewById(R.id.tv_ex_name)).setText(expense.name);
        tvExpenseWay.setText(expense.expense_way_name);
        tvTotal.setText("" + numberFormatter.format(Double.parseDouble(expense.total_price)));
        tvDate.setText(expense.date);
        tvPaid.setText("" + numberFormatter.format(Double.parseDouble(expense.cash)));
        tvDue.setText("" + numberFormatter.format(Double.parseDouble(expense.due)));

        if(!TextUtils.equals(expense.remarks, "null") && !TextUtils.equals(expense.remarks, "")) {
            tvRemarks.setText(expense.remarks);
        }
        else {
            tvRemarks.setText("");
        }

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            dueSubmit.SubmitDue(expense.id);
//
//            }
//        });

        return convertView;
    }
}
