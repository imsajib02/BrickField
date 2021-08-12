package com.b2gsoft.mrb.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.b2gsoft.mrb.Interface.DelAccount;
import com.b2gsoft.mrb.Interface.Update;
import com.b2gsoft.mrb.Model.SubCategory;
import com.b2gsoft.mrb.R;

import java.util.List;

import static com.b2gsoft.mrb.Utils.ApplicationController.getContext;

public class CustomSubCategoryAdapter extends BaseAdapter {

    Context context;
    List<SubCategory> itemList;
    boolean canDelete;
    boolean canEdit;
    private DelAccount delAccount;
    private Update update;

    public CustomSubCategoryAdapter(Context context, List<SubCategory> itemList, boolean canDelete, DelAccount delAccount, boolean canUpdate, Update update) {

        this.context = context;
        this.itemList = itemList;
        this.canDelete = canDelete;
        this.delAccount = delAccount;
        this.update = update;
        this.canEdit = canUpdate;
    }

    @Override
    public int getCount() {
        return this.itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
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

    private View initView(final int position, View view, ViewGroup parent) {

        if(view == null) {

            view = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_layout, parent, false);
        }

        final TextView name = view.findViewById(R.id.tv_name);
        final ImageView delete = view.findViewById(R.id.img_del);
        final ImageView edit = view.findViewById(R.id.img_edit);

        final SubCategory item = this.itemList.get(position);

        if(item != null) {

            name.setText(item.getName());

            if(canEdit) {

                edit.setVisibility(view.VISIBLE);
            }
            else {

                edit.setVisibility(view.GONE);
            }

            if(canDelete) {

                delete.setVisibility(view.VISIBLE);
                delete.setImageResource(R.drawable.icons8_delete_red);
            }
            else {

                delete.setVisibility(view.GONE);
            }
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                update.updateSubCategory(item);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                delAccount.DeleteSubCategory(item);
            }
        });

        return view;
    }
}