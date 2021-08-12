package com.b2gsoft.mrb.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2gsoft.mrb.Model.CustomerWiseIncome;
import com.b2gsoft.mrb.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class CustomerWiseIncomeAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CustomerWiseIncome> customerWiseIncomeList;
    private NumberFormat numberFormatter;

    public CustomerWiseIncomeAdapter(Context context, ArrayList<CustomerWiseIncome> customerWiseIncomeList) {
        this.context = context;
        this.customerWiseIncomeList = customerWiseIncomeList;
        this.numberFormatter = new DecimalFormat("#0.00");
    }

    public int getCount() {
        return this.customerWiseIncomeList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.layout_customer_wise_income, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        TextView tvUnit = (TextView) convertView.findViewById(R.id.tv_unit);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
        TextView tvCash = (TextView) convertView.findViewById(R.id.tv_cash);
        TextView tvDue = (TextView) convertView.findViewById(R.id.tv_due);


        CustomerWiseIncome customerWiseIncome = (CustomerWiseIncome) this.customerWiseIncomeList.get(position);

        tvName.setText(customerWiseIncome.name);
        tvUnit.setText("" + numberFormatter.format(customerWiseIncome.total_unit));
        tvPrice.setText("" + numberFormatter.format(customerWiseIncome.total_price));
        tvCash.setText("" + numberFormatter.format(customerWiseIncome.total_cash));
        tvDue.setText("" + numberFormatter.format(customerWiseIncome.total_due));

        return convertView;
    }
}
