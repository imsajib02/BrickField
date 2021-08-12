package com.b2gsoft.mrb.Activity.drawer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.b2gsoft.mrb.Activity.HomeActivity;
import com.b2gsoft.mrb.Adapter.ContactAdapter;
import com.b2gsoft.mrb.Model.Contract;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Utils.ApplicationController;
import com.b2gsoft.mrb.Utils.ConnectionDetector;
import com.b2gsoft.mrb.Utils.GenericVollyError;
import com.b2gsoft.mrb.Utils.SharedPreferences;
import com.b2gsoft.mrb.Utils.StaticValue;
import com.b2gsoft.mrb.Utils.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {

    private EditText etName, etPhone;
    private ListView lvContract;
    private ConnectionDetector connectionDetector;
    private Button btnSave;
    private ProgressDialog pDialog;
    private SharedPreferences sharedPreferences;
    private ArrayList<Contract> contractArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("PLAYGROUND", "Permission is not granted, requesting");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 123);
            //  button.setEnabled(false);
        } else {
            Log.d("PLAYGROUND", "Permission is granted");
        }

        sharedPreferences = new SharedPreferences(getApplicationContext());
        connectionDetector = new ConnectionDetector(getApplicationContext());
        pDialog = new ProgressDialog(ContactActivity.this);

        etName = (EditText) findViewById(R.id.et_name);
        etPhone = (EditText) findViewById(R.id.et_phone);

        lvContract = (ListView) findViewById(R.id.lv_contract);

        btnSave = (Button) findViewById(R.id.btn_save);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

                if (etName.getText().toString().equals("")) {

                    Toast.makeText(getApplicationContext(), getString(R.string.give_name), Toast.LENGTH_SHORT).show();

                } else if (etPhone.getText().toString().equals("")) {

                    Toast.makeText(getApplicationContext(), getString(R.string.give_phone), Toast.LENGTH_SHORT).show();

                } else if (etPhone.getText().length() < 11) {

                    Toast.makeText(getApplicationContext(), getString(R.string.invalid_phone), Toast.LENGTH_SHORT).show();

                } else {

                    if (connectionDetector.isConnectingToInternet()) {
                        save();
                    }
                }
            }
        });

        if (connectionDetector.isConnectingToInternet()) {
            getData();
        }
    }

    private void getData() {
        showDialog();
        JSONObject jsonObject = new JSONObject();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.Contracts + sharedPreferences.getSessionToken(), jsonObject, responseData(), GenericVollyError.errorListener(getApplicationContext(), pDialog));
        ApplicationController.getInstance().addToRequestQueue(request);

    }

    private Response.Listener<JSONObject> responseData() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismissProgressDialog();
                try {
                    if (response.getBoolean(StaticValue.Success)){

                        contractArrayList = new ArrayList<>();
                        JSONArray data = response.getJSONArray(StaticValue.Data);

                        if (data.length() > 0) {

                            for (int i = 0; i< data.length(); i++){
                                JSONObject jsonObject = data.getJSONObject(i);
                                Contract contract = new Contract();
                                contract.name = jsonObject.getString("name");
                                contract.phone = jsonObject.getString("contract_number");

                                contractArrayList.add(contract);
                            }
                        }

                        ContactAdapter contactAdapter = new ContactAdapter(getApplicationContext(), contractArrayList);
                        contactAdapter.notifyDataSetChanged();
                        lvContract.setAdapter(contactAdapter);
                    }
                    else {
                        Toast.makeText(ContactActivity.this, getString(R.string.failed_to_get_data), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void save() {

        showDialog();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", etName.getText().toString());
            jsonObject.put("contract_number", etPhone.getText().toString());
            jsonObject.put("create_by_user_id", sharedPreferences.getUserId());


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Url.Contracts + sharedPreferences.getSessionToken(), jsonObject, responseSave(), GenericVollyError.errorListener(getApplicationContext(), pDialog));
            Log.e("ex", jsonObject + " " + Url.Contracts + sharedPreferences.getSessionToken());
            ApplicationController.getInstance().addToRequestQueue(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<JSONObject> responseSave() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismissProgressDialog();

                try {
                    if (response.getBoolean(StaticValue.Success)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.successfully_saved), Toast.LENGTH_LONG).show();
                        clearFields();
                        getData();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_save), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void clearFields() {

        etName.getText().clear();
        etPhone.getText().clear();

        hideKeyboard();
    }

    private void hideKeyboard() {

        View view = this.getCurrentFocus();

        if(view != null) {

            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra(StaticValue.Page, StaticValue.DashboardPage);
        startActivityForResult(intent, 0);
        finish();
        return true;
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

}
