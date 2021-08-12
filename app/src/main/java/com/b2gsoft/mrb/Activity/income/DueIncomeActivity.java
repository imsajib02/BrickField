package com.b2gsoft.mrb.Activity.income;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.b2gsoft.mrb.Activity.HomeActivity;
import com.b2gsoft.mrb.Adapter.DueIncomeAdapter;
import com.b2gsoft.mrb.Model.Income;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Utils.ApplicationController;
import com.b2gsoft.mrb.Utils.ConnectionDetector;
import com.b2gsoft.mrb.Interface.DueSubmit;
import com.b2gsoft.mrb.Utils.GenericVollyError;
import com.b2gsoft.mrb.Utils.SharedPreferences;
import com.b2gsoft.mrb.Utils.StaticValue;
import com.b2gsoft.mrb.Utils.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DueIncomeActivity extends AppCompatActivity implements DueSubmit {

    private ConnectionDetector connectionDetector;
    private String dateS;
    /* access modifiers changed from: private */
    public ArrayList<Income> incomeArrayList;
    /* access modifiers changed from: private */
    public ListView listViewincome;
    private ProgressDialog pDialog;
    private SharedPreferences sharedPreferences;
    private TextView tvDate;
    private DueSubmit dueSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_due_income);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.pDialog = new ProgressDialog(this);
        this.connectionDetector = new ConnectionDetector(getApplicationContext());
        this.sharedPreferences = new SharedPreferences(getApplicationContext());

        dueSubmit = this;

        this.dateS = new SimpleDateFormat("dd/MMM/yyyy").format(Calendar.getInstance().getTime());
        this.listViewincome = (ListView) findViewById(R.id.lv_income);
        this.tvDate = (TextView) findViewById(R.id.tv_date);

        TextView textView = this.tvDate;
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.today));
        sb.append(" : ");
        sb.append(this.dateS);
        textView.setText(sb.toString());

        if (this.connectionDetector.isConnectingToInternet()) {
            getIncome();
        }
    }

    private void getIncome() {
        showDialog();
        JSONObject jsonObject = new JSONObject();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.DueIncomeHistory+sharedPreferences.getSessionToken(), jsonObject, response(), GenericVollyError.errorListener(getApplicationContext(), pDialog));

        Log.e("req", Url.DueIncomeHistory+sharedPreferences.getSessionToken());
        ApplicationController.getInstance().addToRequestQueue(request);
    }

    private Response.Listener<JSONObject> response() {
        return new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {

                dismissProgressDialog();
                try {
                    Log.e("res", response.toString());

                    if (response.getBoolean(StaticValue.Success)) {
                        JSONArray data = response.getJSONArray(StaticValue.Data);
                        if (data.length() > 0) {
                            incomeArrayList = new ArrayList();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject value = data.getJSONObject(i);
                                Income income = new Income();

                                income.id = value.getString("id");
                                income.name = value.getString(StaticValue.Name);
                                income.vehicle_no = value.getString(StaticValue.VehicleNo);
                                income.class_id = value.getString(StaticValue.ClassId);
                                income.class_name = value.getString("class_name");
                                income.unit = value.getString(StaticValue.Unit);
                                income.per_unit_price = value.getString(StaticValue.PerUnitPrice);
                                income.total_price = value.getString(StaticValue.TotalPrice);
                                income.income_date = value.getString(StaticValue.Date);
                                income.cash = value.getString(StaticValue.Cash);
                                income.due = value.getString(StaticValue.Due);
                                income.remarks = value.getString("remarks");
                                //income.payment_type_id = value.getString(StaticValue.PaymentTypeId);
                                incomeArrayList.add(income);
                            }
                            DueIncomeAdapter incomeAdapter = new DueIncomeAdapter(getApplicationContext(), incomeArrayList, dueSubmit);
                            incomeAdapter.notifyDataSetChanged();
                            listViewincome.setAdapter(incomeAdapter);
                        }
                        return;
                    }
                    else {
                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_get_data), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e("income", e.getMessage());
                }
            }
        };
    }

    private void showDialog() {
        try {
            this.pDialog.setMessage(getString(R.string.please_wait));
            this.pDialog.setIndeterminate(false);
            this.pDialog.setCancelable(false);
            this.pDialog.setCanceledOnTouchOutside(false);
            this.pDialog.show();
        } catch (Exception e) {
        }
    }

    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    /* access modifiers changed from: private */
    public void dismissProgressDialog() {
        if (this.pDialog != null && this.pDialog.isShowing()) {
            this.pDialog.dismiss();
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

    @Override
    public void SubmitDue(String id) {
        showDialog();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.DueIncomeSubmit +id + "?token="+sharedPreferences.getSessionToken(), jsonObject, responseSubmit(), GenericVollyError.errorListener(getApplicationContext(), pDialog));
        Log.e("req", Url.DueIncomeSubmit +id + "?token="+sharedPreferences.getSessionToken());

        ApplicationController.getInstance().addToRequestQueue(request);
    }

    private Response.Listener<JSONObject> responseSubmit() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                dismissProgressDialog();
                try {
                    Log.e("res", response.toString());

                    if (response.getBoolean(StaticValue.Success)){
                        Toast.makeText(getApplicationContext(), getString(R.string.due_payment_success), Toast.LENGTH_LONG).show();
                        getIncome();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), getString(R.string.due_payment_failed), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}