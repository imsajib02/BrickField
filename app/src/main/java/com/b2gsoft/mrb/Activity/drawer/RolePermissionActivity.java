package com.b2gsoft.mrb.Activity.drawer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.b2gsoft.mrb.Activity.HomeActivity;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Utils.ApplicationController;
import com.b2gsoft.mrb.Utils.ConnectionDetector;
import com.b2gsoft.mrb.Utils.GenericVollyError;
import com.b2gsoft.mrb.Utils.SharedPreferences;
import com.b2gsoft.mrb.Utils.StaticValue;
import com.b2gsoft.mrb.Utils.Url;

import org.json.JSONException;
import org.json.JSONObject;

public class RolePermissionActivity extends AppCompatActivity {

    private CheckBox total_balance_checkbox, current_balance_checkbox, edit_update_checkbox;
    private Button save;

    private String totalBalancePermission, currentBalancePermission, editPermission;

    private ProgressDialog pDialog;
    private SharedPreferences sharedPreferences;
    private ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_permission);

        total_balance_checkbox = (CheckBox) findViewById(R.id.total_balance_checkbox);
        current_balance_checkbox = (CheckBox) findViewById(R.id.current_balance_checkbox);
        edit_update_checkbox = (CheckBox) findViewById(R.id.edit_update_checkbox);
        save = (Button) findViewById(R.id.btn_save_permission);

        pDialog = new ProgressDialog(RolePermissionActivity.this);
        sharedPreferences = new SharedPreferences(getApplicationContext());
        connectionDetector = new ConnectionDetector(getApplicationContext());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setPermission();

        total_balance_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    totalBalancePermission = "1";
                }
                else {
                    totalBalancePermission = "0";
                }
            }
        });


        current_balance_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    currentBalancePermission = "1";
                }
                else {
                    currentBalancePermission = "0";
                }
            }
        });


        edit_update_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    editPermission = "1";
                }
                else {
                    editPermission = "0";
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(isChanged()) {

                    if(connectionDetector.isConnectingToInternet()) {

                        savePermission();
                    }
                }
            }
        });
    }

    private void savePermission() {

        showDialog();

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("total_balance", totalBalancePermission);
            jsonObject.put("current_balance", currentBalancePermission);
            jsonObject.put("data_update", editPermission);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Url.SetPermission + sharedPreferences.getSessionToken(), jsonObject, response(), GenericVollyError.errorListener(getApplicationContext(), pDialog));
            ApplicationController.getInstance().addToRequestQueue(request);

        } catch (JSONException e) {
            dismissProgressDialog();
            e.printStackTrace();
        }
    }

    private Response.Listener<JSONObject> response() {

        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("Permission Set", response.toString());

                dismissProgressDialog();

                try {
                    if (response.getBoolean(StaticValue.Success)) {

                        if(total_balance_checkbox.isChecked()) {

                            sharedPreferences.setTotalBalancePermission("1");
                        }
                        else {

                            sharedPreferences.setTotalBalancePermission("0");
                        }

                        if(current_balance_checkbox.isChecked()) {

                            sharedPreferences.setCurrentBalancePermission("1");
                        }
                        else {

                            sharedPreferences.setCurrentBalancePermission("0");
                        }

                        if(edit_update_checkbox.isChecked()) {

                            sharedPreferences.setEditPermission("1");
                        }
                        else {

                            sharedPreferences.setEditPermission("0");
                        }

                        Toast.makeText(getApplicationContext(), getString(R.string.successfully_saved), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_save), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    private void showDialog() {

        try {
            pDialog.setMessage(getString(R.string.please_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void dismissProgressDialog() {

        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    private boolean isChanged() {

        if(TextUtils.equals(totalBalancePermission, sharedPreferences.getTotalBalancePermission()) &&
                TextUtils.equals(currentBalancePermission, sharedPreferences.getCurrentBalancePermission()) &&
                TextUtils.equals(editPermission, sharedPreferences.getEditPermission())) {

            return false;
        }
        else {

            return true;
        }
    }

    private void setPermission() {

        if(TextUtils.equals(sharedPreferences.getTotalBalancePermission(), "1")) {

            total_balance_checkbox.setChecked(true);
            totalBalancePermission = "1";
        }
        else {
            totalBalancePermission = "0";
        }

        if(TextUtils.equals(sharedPreferences.getCurrentBalancePermission(), "1")) {

            current_balance_checkbox.setChecked(true);
            currentBalancePermission = "1";
        }
        else {
            currentBalancePermission = "0";
        }

        if(TextUtils.equals(sharedPreferences.getEditPermission(), "1")) {

            edit_update_checkbox.setChecked(true);
            editPermission = "1";
        }
        else {
            editPermission = "0";
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == 4) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra(StaticValue.Page, StaticValue.DashboardPage);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra(StaticValue.Page, StaticValue.DashboardPage);
        startActivityForResult(intent, 0);
        finish();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
