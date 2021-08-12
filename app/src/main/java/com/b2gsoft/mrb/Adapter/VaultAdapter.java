package com.b2gsoft.mrb.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.b2gsoft.mrb.Model.Vault;
import com.b2gsoft.mrb.R;

import java.util.ArrayList;

public class VaultAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Vault> vaultArrayList;

    public VaultAdapter(Context context, ArrayList<Vault> vaultArrayList) {
        this.context = context;
        this.vaultArrayList = vaultArrayList;
    }

    @Override
    public int getCount() {
        return vaultArrayList.size();
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

        TextView textViewTitle = (TextView)convertView.findViewById(R.id.tv_title);
        TextView textViewMsg = (TextView)convertView.findViewById(R.id.tv_msg);

        Vault vault = vaultArrayList.get(position);

        textViewTitle.setText(vault.title);
        textViewMsg.setText(vault.msg);

        return convertView;
    }
}
