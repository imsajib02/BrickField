package com.b2gsoft.mrb.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.b2gsoft.mrb.Interface.Update;
import com.b2gsoft.mrb.Model.Customer;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Interface.DelAccount;

import java.util.List;

public class CustomStringAdapter extends ArrayAdapter<Customer> {

    Context context;
    List<Customer> itemList;
    private DelAccount delAccount;
    private Update update;

    public CustomStringAdapter(Context context, List<Customer> itemList, DelAccount delAccount, Update update) {

        super(context, 0, itemList);
        this.context = context;
        this.itemList = itemList;
        this.delAccount = delAccount;
        this.update = update;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View view, ViewGroup parent) {

        if(view == null) {

            view = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_layout, parent, false);
        }

        TextView name = view.findViewById(R.id.tv_name);
        final ImageView delete = view.findViewById(R.id.img_del);
        final ImageView edit = view.findViewById(R.id.img_edit);

        final Customer customer = getItem(position);

        if(customer != null) {

            name.setText(customer.name);
            delete.setVisibility(view.VISIBLE);
            edit.setVisibility(view.VISIBLE);
            delete.setImageResource(R.drawable.icons8_delete_red);
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                update.updateCustomer(customer);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                delAccount.DeleteCustomer(customer);
            }
        });

        return view;
    }
}