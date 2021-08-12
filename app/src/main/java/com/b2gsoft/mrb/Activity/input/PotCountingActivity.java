package com.b2gsoft.mrb.Activity.input;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.b2gsoft.mrb.Activity.HomeActivity;
import com.b2gsoft.mrb.Adapter.PotAdapter;
import com.b2gsoft.mrb.Model.Category;
import com.b2gsoft.mrb.Model.Pot;
import com.b2gsoft.mrb.Model.SubCategory;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Utils.ApplicationController;
import com.b2gsoft.mrb.Utils.ConnectionDetector;
import com.b2gsoft.mrb.Utils.GenericVollyError;
import com.b2gsoft.mrb.Utils.SharedPreferences;
import com.b2gsoft.mrb.Utils.StaticValue;
import com.b2gsoft.mrb.Interface.UpdatePot;
import com.b2gsoft.mrb.Utils.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.b2gsoft.mrb.Utils.ApplicationController.getContext;

public class PotCountingActivity extends AppCompatActivity implements UpdatePot {

    private Button btnSave;
    private Button btnShow;
    public ConnectionDetector connectionDetector;
    public EditText etAmount;
    public EditText etLine;
    public EditText etPot;
    private LinearLayout llSearchTotal;
    public TextView tvTotal;
    public ListView listViewMill;
    private ProgressDialog pDialog;
    public ArrayList<Pot> potArrayList;
    private SharedPreferences sharedPreferences;
    public String stringAmount;
    public String stringDate = "";
    public String stringFromDate = "";
    public String stringLine = "";
    public String stringPot;
    public String stringToDate = "";
    public String stringTotal;
    public TextView tvDate;
    public TextView tvFromDate;
    public TextView tvToDate;
    public TextView tvTotalBalance;
    public TextView tvMot;
    public Spinner spMill;
    private ArrayAdapter<SubCategory> adapterExpense;

    private UpdatePot updatePot;
    private NumberFormat numberFormatter;

    private List<SubCategory> millSubList = new ArrayList<>();
    private Category category = new Category();

    private double total = 0;
    private String stringMillSubID = "";

    //private LinearLayout linearLayoutTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pot_counting);

        sharedPreferences = new SharedPreferences(PotCountingActivity.this);
        connectionDetector = new ConnectionDetector(PotCountingActivity.this);
        pDialog = new ProgressDialog(PotCountingActivity.this);
        updatePot = this;
        numberFormatter = new DecimalFormat("#0.00");

        //linearLayoutTotal = (LinearLayout) findViewById(R.id.ll_total);

        tvDate = (TextView) findViewById(R.id.tv_date);
        tvFromDate = (TextView) findViewById(R.id.tv_from_date);
        tvToDate = (TextView) findViewById(R.id.tv_to_date);
        tvTotalBalance = (TextView) findViewById(R.id.tv_total_balance);
        tvMot = (TextView) findViewById(R.id.tv_mot);

        spMill = (Spinner) findViewById(R.id.sp_mill);

        llSearchTotal = (LinearLayout) findViewById(R.id.ll_total);

        etPot = (EditText) findViewById(R.id.et_pot_no);
        etLine = (EditText) findViewById(R.id.et_line);
        etAmount = (EditText) findViewById(R.id.et_amount);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        listViewMill = (ListView) findViewById(R.id.lv_mill);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnShow = (Button) findViewById(R.id.btn_show);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getPotDetails();

        setTodaysDate();

        tvDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setDate(1);
            }
        });

        tvFromDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setDate(2);
            }
        });

        tvToDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setDate(3);
            }
        });

        etLine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                setTotal();
            }
        });

        etAmount.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                setTotal();
            }
        });


        spMill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SubCategory subCategory = (SubCategory) parent.getSelectedItem();

                String subID = String.valueOf(subCategory.getId());

                if(!TextUtils.equals(subID, stringMillSubID)) {

                    stringMillSubID = String.valueOf(subCategory.getId());
                    tvMot.setText("" + numberFormatter.format(subCategory.getPotTotal()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                hideKeyboard();

                stringPot = etPot.getText().toString();
                stringLine = etLine.getText().toString();
                stringAmount = etAmount.getText().toString();

                if (checkValid()) {
                    if (connectionDetector.isConnectingToInternet()) {
                        save();
                    }
                }

            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (stringFromDate.equals("") || stringToDate.equals("")) {
                    Toast.makeText(getContext(), "Please select from & to date", Toast.LENGTH_LONG).show();
                } else if (connectionDetector.isConnectingToInternet()) {
                    getData();
                }
            }
        });
    }


    private void getPotDetails() {

        showDialog();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.PotDetails + sharedPreferences.getSessionToken(), null, responsePotDetails(), GenericVollyError.errorListener(getApplicationContext(), pDialog));
        Log.e("ex", Url.PotDetails + sharedPreferences.getSessionToken());
        ApplicationController.getInstance().addToRequestQueue(request);
    }

    private Response.Listener<JSONObject> responsePotDetails() {

        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getBoolean(StaticValue.Success)) {

                        JSONObject data = response.getJSONObject(StaticValue.Data);

                        JSONObject mill = data.getJSONObject("mill");

                        category.setId(mill.getInt(StaticValue.Id));
                        category.setName(mill.getString(StaticValue.Name));

                        JSONArray subCategories = mill.getJSONArray("expense_sub_sector");

                        if(subCategories.length() > 0) {

                            for(int i=0; i<subCategories.length(); i++) {

                                JSONObject value = subCategories.getJSONObject(i);

                                SubCategory subCategory = new SubCategory();

                                subCategory.setId(value.getInt(StaticValue.Id));
                                subCategory.setName(value.getString(StaticValue.Name));
                                subCategory.setPotTotal(value.getDouble("total_pot"));

                                millSubList.add(subCategory);
                            }
                        }

                        adapterExpense = new ArrayAdapter<SubCategory>(getApplicationContext(), android.R.layout.simple_spinner_item, millSubList);
                        adapterExpense.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spMill.setAdapter(adapterExpense);

                        dismissProgressDialog();
                        return;
                    }
                    else {
                        dismissProgressDialog();
                        Toast.makeText(getContext(), getString(R.string.failed_to_get_data), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    dismissProgressDialog();
                    Log.e("income", e.getMessage());
                }
            }
        };
    }


    public void getData() {

        showDialog();

        JSONObject jsonObject = new JSONObject();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.GetPot + stringMillSubID + "&from=" + stringFromDate + "&to=" + stringToDate + "&token=" + sharedPreferences.getSessionToken(),
                jsonObject, responseData(), GenericVollyError.errorListener(getContext(), pDialog));

        Log.e("pot", Url.GetPot + "1&from=" + stringFromDate + "&to=" + stringToDate + "&token=" + sharedPreferences.getSessionToken());
        ApplicationController.getInstance().addToRequestQueue(request);
    }


    private Response.Listener<JSONObject> responseData() {

        return new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {

                try {
                    if (response.getBoolean(StaticValue.Success)) {

                        JSONArray data = response.getJSONArray(StaticValue.Data);

                        if (data.length() > 0) {

                            potArrayList = new ArrayList();

                            double total = 0;

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject jsonObject = data.getJSONObject(i);
                                Pot pot = new Pot();
                                pot.id = jsonObject.getInt(StaticValue.Id);
                                pot.potNo = jsonObject.getString(StaticValue.PotNo);
                                pot.line = jsonObject.getString(StaticValue.Line);
                                pot.date = jsonObject.getString(StaticValue.PotDate);
                                pot.amount = jsonObject.getString(StaticValue.Porimap);
                                pot.total = jsonObject.getString(StaticValue.Mot);
                                total += Double.parseDouble(pot.total);
                                potArrayList.add(pot);
                            }

                            llSearchTotal.setVisibility(View.VISIBLE);
                            tvTotalBalance.setText(""+total);

                            PotAdapter potAdapter = new PotAdapter(getContext(), potArrayList, updatePot);
                            potAdapter.notifyDataSetChanged();
                            listViewMill.setAdapter(potAdapter);
                        } else {
                            potArrayList = new ArrayList();

                            //linearLayoutTotal.setVisibility(View.GONE);
                            PotAdapter potAdapter = new PotAdapter(getContext(), potArrayList, updatePot);
                            potAdapter.notifyDataSetChanged();
                            listViewMill.setAdapter(potAdapter);
                        }

                        dismissProgressDialog();
                    }
                    else {
                        dismissProgressDialog();
                        Toast.makeText(getContext(), getString(R.string.failed_to_get_data), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    dismissProgressDialog();
                    e.printStackTrace();
                }
            }
        };
    }


    public boolean checkValid() {
        if (stringDate.equals("")) {
            Toast.makeText(getContext(), getString(R.string.give_date), Toast.LENGTH_LONG).show();
            return false;
        } else if (stringPot.equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.give_pot_no), Toast.LENGTH_SHORT).show();
            return false;
        } else if (stringLine.equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.give_line_no), Toast.LENGTH_SHORT).show();
            return false;
        } else if (stringAmount.equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.give_unit), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    public void save() {

        showDialog();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(StaticValue.Mill, stringMillSubID);
            jsonObject.put(StaticValue.PotDate, stringDate);
            jsonObject.put(StaticValue.PotNo, stringPot);
            jsonObject.put(StaticValue.Line, stringLine);
            jsonObject.put(StaticValue.Porimap, stringAmount);
            jsonObject.put(StaticValue.Mot, total);
            jsonObject.put(StaticValue.UserId, sharedPreferences.getUserId());
            jsonObject.put(StaticValue.UpdateUserId, sharedPreferences.getUserId());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Url.PotSave + sharedPreferences.getSessionToken(), jsonObject, response(total), GenericVollyError.errorListener(getContext(), pDialog));

            Log.e("pot", Url.PotSave + sharedPreferences.getSessionToken() + " " + jsonObject);
            ApplicationController.getInstance().addToRequestQueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Response.Listener<JSONObject> response(final double total) {

        return new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {

                try {
                    if (response.getBoolean(StaticValue.Success)) {

                        for(int i=0; i<millSubList.size(); i++) {

                            if(TextUtils.equals(stringMillSubID, String.valueOf(millSubList.get(i).getId()))) {

                                millSubList.get(i).setPotTotal(millSubList.get(i).getPotTotal() + total);
                                tvMot.setText("" + numberFormatter.format(millSubList.get(i).getPotTotal()));
                                break;
                            }
                        }

                        dismissProgressDialog();
                        Toast.makeText(getContext(), getString(R.string.successfully_saved), Toast.LENGTH_LONG).show();

                        if(!TextUtils.isEmpty(stringFromDate) && !TextUtils.isEmpty(stringToDate)) {

                            getData();
                        }

                        clearFields();

                    } else {
                        dismissProgressDialog();
                        Toast.makeText(getContext(), getString(R.string.failed_to_save), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    dismissProgressDialog();
                    e.printStackTrace();
                }
            }
        };
    }


    private void clearFields() {

        setTodaysDate();

        etPot.getText().clear();
        etLine.getText().clear();
        etAmount.getText().clear();

        stringPot = "";
        stringLine = "";
        stringAmount = "";

        hideKeyboard();
    }


    private void hideKeyboard() {

        View view = this.getCurrentFocus();

        if(view != null) {

            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private void setTotal() {

        try {

            double line = 0;
            double price = 0;
            if (etLine.getText().length() > 0) {
                line = Double.parseDouble(etLine.getText().toString());
            }
            if (etAmount.getText().length() > 0) {
                price = Double.parseDouble(etAmount.getText().toString());
            }

            total = line * price;
            tvTotal.setText("" + numberFormatter.format(total));

        } catch (Exception e) {

        }
    }


    private void setTodaysDate() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
        Date todayDate = new Date();

        stringDate = dateFormat.format(todayDate);
        tvDate.setText(stringDate);
    }


    public void setDate(final int i) {

        View view = LayoutInflater.from(PotCountingActivity.this).inflate(R.layout.date_pick, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(PotCountingActivity.this);

        builder.setView(view);
        builder.setCancelable(false);

        final DatePicker simpleDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
        simpleDatePicker.setSpinnersShown(false);

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int day = simpleDatePicker.getDayOfMonth();
                int year = simpleDatePicker.getYear();
                int month = simpleDatePicker.getMonth() + 1;
                if (i == 1) {
                    stringDate = year + "-" + month + "-" + day;
                    tvDate.setText(stringDate);
                } else if (i == 2) {
                    stringFromDate = year + "-" + month + "-" + day;
                    tvFromDate.setText(stringFromDate);
                } else if (i == 3) {
                    stringToDate = year + "-" + month + "-" + day;
                    tvToDate.setText(stringToDate);
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }


    private void showDialog() {
        try {
            pDialog.setMessage(getString(R.string.please_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        } catch (Exception e) {
        }
    }


    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }


    public void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra(StaticValue.Page, StaticValue.InputPage);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void UpdatePot(final Pot pot) {

        LayoutInflater inflater = LayoutInflater.from(PotCountingActivity.this);
        View view = inflater.inflate(R.layout.layout_pot_update, null);

        final EditText etPotNo = (EditText)view. findViewById(R.id.et_pot_no);
        final EditText etLine = (EditText)view. findViewById(R.id.et_line);
        final EditText etPoriman = (EditText)view. findViewById(R.id.et_poriman);
        final TextView tvTotal = (TextView)view. findViewById(R.id.tv_total);

        Button btnSave = (Button)view.findViewById(R.id.btn_save);

        etPotNo.setText(pot.potNo);
        etLine.setText(pot.line);
        etPoriman.setText(pot.amount);
        tvTotal.setText(pot.total);


        etLine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setTotalForUpdate(etPoriman, etLine, tvTotal);
            }
        });


        etPoriman.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setTotalForUpdate(etPoriman, etLine, tvTotal);
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(PotCountingActivity.this);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etPotNo.getText().toString().isEmpty()) {

                    Toast.makeText(PotCountingActivity.this, getString(R.string.give_pot_no), Toast.LENGTH_SHORT).show();

                } else if (etLine.getText().toString().isEmpty()) {

                    Toast.makeText(PotCountingActivity.this, getString(R.string.give_line_no), Toast.LENGTH_SHORT).show();

                } else if (etPoriman.getText().toString().isEmpty()) {

                    Toast.makeText(PotCountingActivity.this, getString(R.string.give_unit), Toast.LENGTH_SHORT).show();

                } else {

                    dialog.cancel();
                    showDialog();

                    JSONObject jsonObject = new JSONObject();

                    try {
                        double mot = Double.parseDouble(etLine.getText().toString()) * Double.parseDouble(etPoriman.getText().toString());

                        jsonObject.put(StaticValue.PotNo, etPotNo.getText().toString());
                        jsonObject.put(StaticValue.Line, etLine.getText().toString());
                        jsonObject.put(StaticValue.Porimap, etPoriman.getText().toString());
                        jsonObject.put(StaticValue.Mot, mot);
                        jsonObject.put(StaticValue.Mill, stringMillSubID);

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, Url.PotUpdate + pot.id + "?token=" + sharedPreferences.getSessionToken(), jsonObject, responseUpdate(pot.total, mot), GenericVollyError.errorListener(PotCountingActivity.this, pDialog));
                        ApplicationController.getInstance().addToRequestQueue(request);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private Response.Listener<JSONObject> responseUpdate(final String total, final double newTotal) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                dismissProgressDialog();

                try {
                    if (response.getBoolean(StaticValue.Success)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.successfully_updated), Toast.LENGTH_LONG).show();

                        for(int i=0; i<millSubList.size(); i++) {

                            if(TextUtils.equals(stringMillSubID, String.valueOf(millSubList.get(i).getId()))) {

                                millSubList.get(i).setPotTotal(millSubList.get(i).getPotTotal() - Double.parseDouble(total) + newTotal);
                                tvMot.setText("" + numberFormatter.format(millSubList.get(i).getPotTotal()));
                                break;
                            }
                        }

                        getData();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_update), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    private void setTotalForUpdate(EditText poriman, EditText etLine, TextView total) {

        try {

            double line = 0;
            double amount = 0;

            if (etLine.getText().length() > 0) {
                line = Double.parseDouble(etLine.getText().toString());
            }

            if (poriman.getText().length() > 0) {
                amount = Double.parseDouble(poriman.getText().toString());
            }

            total.setText("" + numberFormatter.format(line * amount));

        } catch (Exception e) {

        }
    }
}
