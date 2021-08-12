package com.b2gsoft.mrb.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.b2gsoft.mrb.Utils.ApplicationController;
import com.b2gsoft.mrb.Utils.ConnectionDetector;
import com.b2gsoft.mrb.Utils.GenericVollyError;
import com.b2gsoft.mrb.Utils.SharedPreferences;
import com.b2gsoft.mrb.Utils.StaticValue;
import com.b2gsoft.mrb.Utils.Url;
import com.b2gsoft.mrb.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private TextView tvLogin;
    private TextInputEditText etUser, etPassword;
    private ConnectionDetector connectionDetector;
    private SharedPreferences sharedPreferences;
    private ProgressDialog pDialog;
    private Context context = LoginActivity.this;

    final int REQUEST_ALL = 1;
    boolean STORAGE_PERMISSION_DENIED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pDialog = new ProgressDialog(context);
        connectionDetector = new ConnectionDetector(context);
        sharedPreferences = new SharedPreferences(context);

        etUser = (TextInputEditText)findViewById(R.id.tiet_username);
        etPassword = (TextInputEditText)findViewById(R.id.tiet_password);
        tvLogin = (TextView)findViewById(R.id.tvLogin);

        requestPermission();

        if(sharedPreferences.isLoggedIn()) {

            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra(StaticValue.Page, StaticValue.DashboardPage);
            startActivity(intent);
            finish();
        }

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etUser.getText().length()<1){

                    Toast.makeText(getApplicationContext(), getString(R.string.give_phone), Toast.LENGTH_SHORT).show();
                }
                else if (etPassword.getText().length()<1){

                    Toast.makeText(getApplicationContext(), getString(R.string.give_password), Toast.LENGTH_SHORT).show();
                }
                else {
                    if (connectionDetector.isConnectingToInternet()){

                        login();
                    }
                }
            }
        });

    }

    private void login() {

        showDialog();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("username", etUser.getText().toString());
            jsonObject.put("password", etPassword.getText().toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Url.LOGIN,
                    jsonObject, response(), GenericVollyError.errorListener(getApplicationContext(), pDialog));

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

                dismissProgressDialog();
                try {
                    Log.e("login", response.toString());
                    if (response.getBoolean(StaticValue.Success)){

                        JSONObject data = response.getJSONObject(StaticValue.Data);

                        sharedPreferences.setIsLoggedIn(true);
                        sharedPreferences.setSessionToken(data.getString(StaticValue.Token));

                        JSONObject user = data.getJSONObject("user");

                        sharedPreferences.setUserName(user.getString("name"));
                        sharedPreferences.setUserId(user.getInt("id"));
                        sharedPreferences.setUserPhone(user.getString("contact_number"));
                        sharedPreferences.setUserTypeId(user.getInt("user_type_id"));
                        sharedPreferences.setUserType(user.getString("user_type_name"));
                        sharedPreferences.setRole(user.getString("role"));

                        if(!TextUtils.equals(user.getString("role"), StaticValue.SuperAdmin)) {

                            JSONObject permission = user.getJSONObject("permissions");

                            sharedPreferences.setTotalBalancePermission(permission.getString("total_balance"));
                            sharedPreferences.setCurrentBalancePermission(permission.getString("current_balance"));
                            sharedPreferences.setEditPermission(permission.getString("data_update"));

//                            if(TextUtils.equals(user.getString("role"), StaticValue.Manager)) {
//
//                                sharedPreferences.setAdminId(user.getInt("admin_id"));
//                            }
                        }

                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra(StaticValue.Page, StaticValue.DashboardPage);
                        startActivity(intent);
                        finish();

                    }
                    else {
                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_login), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("logiin", e.getMessage());
                }

            }
        };
    }


    //region show & hide progress dialog
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

    //endregion

    //region dismiss progress dialog
    @Override
    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }
    //endregion

    private void requestPermission()
    {
        try {
            if (ContextCompat.checkSelfPermission(LoginActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ALL);
            } else {

                //
            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_ALL)
        {
            if(grantResults.length > 0)
            {
                for(int i=0; i<permissions.length; i++)
                {
                    if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    {
                        if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        {
                            STORAGE_PERMISSION_DENIED = false;
                        }
                        else
                        {
                            STORAGE_PERMISSION_DENIED = true;
                        }
                    }
                }

                showAlertAndRequest();
            }
        }
    }

    public void showAlertAndRequest()
    {
        if(STORAGE_PERMISSION_DENIED)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(getString(R.string.permission_alert_text))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            requestPermission();
                        }
                    })

                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();

            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
        }
    }
}
