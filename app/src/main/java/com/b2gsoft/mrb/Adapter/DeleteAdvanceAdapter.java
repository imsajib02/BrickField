package com.b2gsoft.mrb.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.b2gsoft.mrb.Model.User;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Interface.DelAccount;
import com.b2gsoft.mrb.Utils.SharedPreferences;
import com.b2gsoft.mrb.Utils.StaticValue;

import java.util.ArrayList;

public class DeleteAdvanceAdapter extends BaseAdapter {
    private SharedPreferences sharedPreferences;
    private Context context;
    private ArrayList<User> userArrayList;
    private DelAccount delAccount;

    public DeleteAdvanceAdapter(Context context, ArrayList<User> userArrayList, DelAccount delAccount) {
        sharedPreferences = new SharedPreferences(context);
        this.context = context;
        this.userArrayList = userArrayList;
        this.delAccount = delAccount;
    }

    public int getCount() {
        return userArrayList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.adapter_delete_user, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tv_in_name);
        TextView tvPhone = (TextView) convertView.findViewById(R.id.tv_in_phone);
        TextView tvType= (TextView) convertView.findViewById(R.id.tv_in_type);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.iv_ex_del);

        final User user = userArrayList.get(position);

        tvName.setText(user.name);
        tvPhone.setText(user.phone);
        tvType.setText(user.type);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.equals(sharedPreferences.getRole(), StaticValue.Admin)) {

                    delAccount.Delete(user.id);
                }
                else if(TextUtils.equals(sharedPreferences.getRole(), StaticValue.Manager)) {

                    if(TextUtils.equals(sharedPreferences.getEditPermission(), "1")) {

                        delAccount.Delete(user.id);
                    }
                }
            }
        });


        return convertView;
    }
}
