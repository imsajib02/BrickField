package com.b2gsoft.mrb.Activity.income;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.b2gsoft.mrb.Activity.HomeActivity;
import com.b2gsoft.mrb.Adapter.DailyIncomeAdapter;
import com.b2gsoft.mrb.Model.Income;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Utils.ApplicationController;
import com.b2gsoft.mrb.Utils.ConnectionDetector;
import com.b2gsoft.mrb.Utils.GenericVollyError;
import com.b2gsoft.mrb.Utils.SharedPreferences;
import com.b2gsoft.mrb.Utils.StaticValue;
import com.b2gsoft.mrb.Interface.UpdateIncome;
import com.b2gsoft.mrb.Utils.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DailyIncomeHistoryActivity extends AppCompatActivity implements UpdateIncome {

    private ConnectionDetector connectionDetector;
    private String dateS;
    public ArrayList<Income> incomeArrayList;
    public ListView listViewincome;
    private ProgressDialog pDialog;
    private SharedPreferences sharedPreferences;
    private TextView tvDate;
    private UpdateIncome updateIncome;
    private NumberFormat numberFormatter;

    boolean isCalculatingTotal = false;
    boolean isCalculatingPrice = false;
    boolean clearingInput = false;

    private LinearLayout linearLayoutTotal, llLayoutTotalPaid, llTotalDue;
    private TextView tvTotal, tvPaid, tvTotalDue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_income_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(getApplicationContext());
        connectionDetector = new ConnectionDetector(getApplicationContext());
        sharedPreferences = new SharedPreferences(getApplicationContext());
        numberFormatter = new DecimalFormat("#0.00");

        dateS = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        updateIncome = DailyIncomeHistoryActivity.this;

        linearLayoutTotal = (LinearLayout) findViewById(R.id.ll_total);
        llLayoutTotalPaid = (LinearLayout) findViewById(R.id.ll_total_paid);
        llTotalDue = (LinearLayout) findViewById(R.id.ll_total_due);

        tvTotal = (TextView) findViewById(R.id.tv_total_balance);
        tvPaid = (TextView) findViewById(R.id.tv_total_paid);
        tvTotalDue = (TextView) findViewById(R.id.tv_total_due);

        listViewincome = (ListView) findViewById(R.id.lv_income);
        tvDate = (TextView) findViewById(R.id.tv_date);

        tvDate.setText(dateS);

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

        if (this.connectionDetector.isConnectingToInternet()) {
            getIncome();
        }
    }

    private void setDate() {

        LayoutInflater inflater = LayoutInflater.from(DailyIncomeHistoryActivity.this);
        View view = inflater.inflate(R.layout.date_pick, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(DailyIncomeHistoryActivity.this);
        builder.setView(view);
        builder.setCancelable(false);
        final DatePicker simpleDatePicker = (DatePicker) view.findViewById(R.id.datePicker); // initiate a date picker

        simpleDatePicker.setSpinnersShown(false);

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int day = simpleDatePicker.getDayOfMonth(); // get the selected day of the month
                int year = simpleDatePicker.getYear(); // get the selected day of the month
                int month = simpleDatePicker.getMonth() + 1; // get the selected day of the month

                dateS = year + "-" + month + "-" + day;
                tvDate.setText(dateS);
                getIncome();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void getIncome() {
        showDialog();
        JSONObject jsonObject = new JSONObject();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.DailyIncomeHistory + dateS + "&token=" + sharedPreferences.getSessionToken(), jsonObject, response(), GenericVollyError.errorListener(getApplicationContext(), pDialog));
        ApplicationController.getInstance().addToRequestQueue(request);
        Log.e("url", Url.DailyIncomeHistory + dateS + "&token=" + sharedPreferences.getSessionToken());
    }

    private Response.Listener<JSONObject> response() {
        return new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                dismissProgressDialog();
                try {
                    Log.e("res", response.toString());

                    if (response.getBoolean(StaticValue.Success)) {

                        JSONObject data = response.getJSONObject(StaticValue.Data);

                        JSONArray incomeData = data.getJSONArray("daily_incomes");

                        if (incomeData.length() > 0) {

                            double total =0, paid =0, due = 0;

                            incomeArrayList = new ArrayList();

                            for (int i = 0; i < incomeData.length(); i++) {

                                JSONObject value = incomeData.getJSONObject(i);

                                Income income = new Income();

                                income.id = value.getString(StaticValue.Id);
                                income.name = value.getString(StaticValue.Name);
                                income.vehicle_no = value.getString(StaticValue.VehicleNo);
                                income.class_id = value.getString(StaticValue.ClassId);
                                income.class_name = value.getString(StaticValue.ClassName);
                                income.unit = value.getString(StaticValue.Unit);
                                income.per_unit_price = value.getString(StaticValue.PerUnitPrice);
                                income.total_price = value.getString(StaticValue.TotalPrice);
                                income.income_date = value.getString(StaticValue.Date);
                                income.cash = value.getString(StaticValue.Cash);
                                income.due = value.getString(StaticValue.Due);
                                income.remarks = value.getString("remarks");

                                total += Double.parseDouble(income.total_price);
                                paid += Double.parseDouble(income.cash);
                                //income.payment_type_id = value.getString(StaticValue.PaymentTypeId);
                                incomeArrayList.add(income);
                            }

                            DailyIncomeAdapter incomeAdapter = new DailyIncomeAdapter(getApplicationContext(), incomeArrayList, updateIncome);
                            incomeAdapter.notifyDataSetChanged();
                            listViewincome.setAdapter(incomeAdapter);

                            linearLayoutTotal.setVisibility(View.VISIBLE);
                            llLayoutTotalPaid.setVisibility(View.VISIBLE);
                            llTotalDue.setVisibility(View.VISIBLE);

                            tvTotal.setText(""+ numberFormatter.format(total));
                            tvPaid.setText(""+ numberFormatter.format(paid));
                            tvTotalDue.setText(""+ numberFormatter.format(total-paid));
                        }
                        else{
                            incomeArrayList = new ArrayList();

                            linearLayoutTotal.setVisibility(View.GONE);
                            llLayoutTotalPaid.setVisibility(View.GONE);
                            llTotalDue.setVisibility(View.GONE);

                            DailyIncomeAdapter incomeAdapter = new DailyIncomeAdapter(getApplicationContext(), incomeArrayList, updateIncome);
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
    public void UpdateIncomeHistory(final Income income) {

        LayoutInflater inflater = LayoutInflater.from(DailyIncomeHistoryActivity.this);
        View view = inflater.inflate(R.layout.activity_income_dialog, null);

        final EditText etUnit = (EditText)view. findViewById(R.id.et_unit);
        final EditText etPrice = (EditText)view. findViewById(R.id.et_price);
        final EditText etPaid = (EditText)view. findViewById(R.id.et_paid);
        final EditText etTotal = (EditText)view. findViewById(R.id.et_total);
        final TextView tvDue = (TextView)view. findViewById(R.id.tv_due);

        Button btnSave = (Button)view.findViewById(R.id.btn_save);

        if(TextUtils.equals(income.class_name, getString(R.string.malik_joma))) {

            etUnit.setEnabled(false);
            etPrice.setEnabled(false);
            etTotal.setEnabled(false);

            clearingInput = true;

            etUnit.getText().clear();
            etPrice.getText().clear();
            etTotal.getText().clear();
            tvDue.setText("");

            clearingInput = false;
        }
        else {

            etUnit.setEnabled(true);
            etPrice.setEnabled(true);
            etTotal.setEnabled(true);
        }

        etUnit.setText(income.unit);
        etPrice.setText(income.per_unit_price);
        etPaid.setText(income.cash);
        etTotal.setText(income.total_price);
        tvDue.setText(income.due);

        etUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!isCalculatingPrice && !isCalculatingTotal && !clearingInput) {
                    setTotal(etUnit, etPrice, etPaid, etTotal, tvDue, income.class_name);
                }
            }
        });

        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!isCalculatingPrice && !clearingInput) {
                    setTotal(etUnit, etPrice, etPaid, etTotal, tvDue, income.class_name);
                }
            }
        });

        etTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!isCalculatingTotal && !clearingInput) {
                    setPrice(etUnit, etPrice, etPaid, etTotal, tvDue);
                }
            }
        });

        etPaid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!TextUtils.equals(income.class_name, getString(R.string.malik_joma))) {
                    setTotalAndPrice(etUnit, etPrice, etPaid, etTotal, tvDue);
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(DailyIncomeHistoryActivity.this);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etUnit.getText().toString().isEmpty()) {

                    Toast.makeText(DailyIncomeHistoryActivity.this, getString(R.string.give_unit), Toast.LENGTH_SHORT).show();

                } else if (etPrice.getText().toString().isEmpty()) {

                    Toast.makeText(DailyIncomeHistoryActivity.this, getString(R.string.give_price), Toast.LENGTH_SHORT).show();

                }else {

                    dialog.cancel();
                    showDialog();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        //jsonObject.put(StaticValue.Id, expense.id);
                        jsonObject.put(StaticValue.Unit, etUnit.getText().toString());
                        jsonObject.put(StaticValue.PerUnitPrice, etPrice.getText().toString());
                        jsonObject.put(StaticValue.Cash, etPaid.getText().toString());
                        jsonObject.put(StaticValue.Due, tvDue.getText().toString());
                        jsonObject.put(StaticValue.TotalPrice, etTotal.getText().toString());

                        /*jsonObject.put(StaticValue.PaymentTypeId, 0);
                        jsonObject.put(StaticValue.CreateUserId, sharedPreferences.getUserId());
                        jsonObject.put(StaticValue.UpdateUserId, sharedPreferences.getUserId());*/
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, Url.IncomeUpdate + income.id + "?token=" + sharedPreferences.getSessionToken(), jsonObject, responseUpdate(), GenericVollyError.errorListener(DailyIncomeHistoryActivity.this, pDialog));
                        Log.e("ex", jsonObject + " " + Url.IncomeUpdate + income.id + "&token=" + sharedPreferences.getSessionToken());
                        ApplicationController.getInstance().addToRequestQueue(request);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void setTotal(EditText etUnit, EditText etPrice, EditText etPaid, EditText etTotal, TextView tvDue, String className) {

        try {

            isCalculatingTotal = true;

            double total = 0;
            double due = 0;
            double unit = 0;
            double price = 0;
            double paid = 0;

            if (etUnit.getText().length() > 0) {
                unit = Double.parseDouble(etUnit.getText().toString());
            }
            if (etPrice.getText().length() > 0) {
                price = Double.parseDouble(etPrice.getText().toString());
            }
            if (etPaid.getText().length() > 0) {
                paid = Double.parseDouble(etPaid.getText().toString());
            }

            if(!TextUtils.equals(className, getString(R.string.malik_joma))) {

                if(unit != 0 && price != 0) {

                    total = unit * price;
                    etTotal.setText("" + numberFormatter.format(total));
                }

                total = Double.parseDouble(etTotal.getText().toString());

                due = total - paid;
                tvDue.setText("" + numberFormatter.format(due));
            }

            isCalculatingTotal = false;

        } catch (Exception e) {

        }
    }


    private void setPrice(EditText etUnit, EditText etPrice, EditText etPaid, EditText etTotal, TextView tvDue) {

        try {
            isCalculatingPrice = true;

            double total = 0;
            double due = 0;
            double unit = 0;
            double price = 0;
            double paid = 0;

            if(etUnit.getText().toString().length() > 0) {

                unit = Double.parseDouble(etUnit.getText().toString());
            }
            if(etTotal.getText().toString().length() > 0) {

                total = Double.parseDouble(etTotal.getText().toString());
            }
            if(etPaid.getText().toString().length() > 0) {

                paid = Double.parseDouble(etPaid.getText().toString());
            }

            if(total != 0 && unit != 0) {

                price = total / unit;
                etPrice.setText("" + numberFormatter.format(price));
            }

            total = Double.parseDouble(etTotal.getText().toString());

            if(total != 0 && paid != 0) {

                due = total - paid;
                tvDue.setText("" + numberFormatter.format(due));
            }

            isCalculatingPrice = false;

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    private void setTotalAndPrice(EditText etUnit, EditText etPrice, EditText etPaid, EditText etTotal, TextView tvDue) {

        try {

            isCalculatingPrice = true;
            isCalculatingTotal = true;

            double total = 0;
            double due = 0;
            double unit = 0;
            double price = 0;
            double paid = 0;

            if(etUnit.getText().toString().length() > 0) {

                unit = Double.parseDouble(etUnit.getText().toString());
            }
            if(etPrice.getText().toString().length() > 0) {

                price = Double.parseDouble(etPrice.getText().toString());
            }
            if(etPaid.getText().toString().length() > 0) {

                paid = Double.parseDouble(etPaid.getText().toString());
            }

            etUnit.setText("" +numberFormatter.format(unit));
            etPrice.setText("" +numberFormatter.format(price));

            if(unit != 0 && price != 0) {

                total = unit * price;
                etTotal.setText("" + numberFormatter.format(total));
            }

            total = Double.parseDouble(etTotal.getText().toString());

            due = total - paid;
            tvDue.setText("" + numberFormatter.format(due));

            isCalculatingPrice = false;
            isCalculatingTotal = false;

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private Response.Listener<JSONObject> responseUpdate() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                dismissProgressDialog();

                try {
                    if (response.getBoolean(StaticValue.Success)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.successfully_updated), Toast.LENGTH_LONG).show();
                        getIncome();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_update), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
