package com.b2gsoft.mrb.Fragment.weekly;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Utils.ApplicationController;
import com.b2gsoft.mrb.Utils.ConnectionDetector;
import com.b2gsoft.mrb.Utils.GenericVollyError;
import com.b2gsoft.mrb.Utils.RoundFormat;
import com.b2gsoft.mrb.Utils.SharedPreferences;
import com.b2gsoft.mrb.Utils.StaticValue;
import com.b2gsoft.mrb.Utils.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeekSalaryFragment extends Fragment {



    public WeekSalaryFragment() {
        // Required empty public constructor
    }

    private EditText etExName, etExUnit, etExAmount, etExPrice;
    private double ExUnit = 0, ExAmount = 0, ExPrice = 0;

    private TextView tvDate;

    private Button btnSave;

    private ConnectionDetector connectionDetector;
    private String stDate = "";
    private double ExTotal = 0;
    private TextView tvExTotal;


    private ProgressDialog pDialog;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_week_salary, container, false);

        pDialog = new ProgressDialog(getContext());

        sharedPreferences = new SharedPreferences(getContext());

        connectionDetector = new ConnectionDetector(getContext());

        btnSave = (Button) view.findViewById(R.id.btn_save);

        tvDate = (TextView) view.findViewById(R.id.tv_ex_date);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

        etExName = (EditText) view.findViewById(R.id.et_ex_name);
        etExUnit = (EditText) view.findViewById(R.id.et_ex_unit);
        etExAmount = (EditText) view.findViewById(R.id.et_ex_amount);
        etExPrice = (EditText) view.findViewById(R.id.et_ex_price);
        tvExTotal = (TextView) view.findViewById(R.id.tv_ex_total);

        etExUnit.addTextChangedListener(new TextWatcher() {
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
        etExAmount.addTextChangedListener(new TextWatcher() {
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
        etExPrice.addTextChangedListener(new TextWatcher() {
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


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (connectionDetector.isConnectingToInternet()) {
                    save();
                }
            }
        });
        return view;
    }

    private void setTotal() {
        try {

            if (etExUnit.getText().length() > 0) {
                ExUnit = Double.parseDouble(etExUnit.getText().toString());
            }
            if (etExAmount.getText().length() > 0) {
                ExAmount = Double.parseDouble(etExAmount.getText().toString());
            }
            if (etExPrice.getText().length() > 0) {
                ExPrice = Double.parseDouble(etExPrice.getText().toString());
            }

            ExTotal = ExUnit * ExPrice * ExAmount;
            tvExTotal.setText("" + RoundFormat.roundTwoDecimals(ExTotal));

        } catch (Exception e) {
        }
    }

    private void save() {

        showDialog();

        JSONObject jsonObject = new JSONObject();
        JSONArray week_expense_sector_list = new JSONArray();
        JSONObject exObject = new JSONObject();
        try {
            exObject.put(StaticValue.Week_expense_sector_name_id, 13);
            //exObject.put(StaticValue.Week_expense_sub_sector_name_id, pos+1);
            exObject.put(StaticValue.Week_expense_type_name_id, 0);
            exObject.put(StaticValue.Name, etExName.getText().toString());
            exObject.put(StaticValue.Date, stDate);
            exObject.put(StaticValue.Week, "");
            exObject.put(StaticValue.Unit, ExUnit);
            exObject.put(StaticValue.Poriman, ExAmount);
            exObject.put(StaticValue.Dor, ExPrice);
            exObject.put(StaticValue.UserId, sharedPreferences.getUserId());
            exObject.put(StaticValue.UpdateUserId, sharedPreferences.getUserId());

            week_expense_sector_list.put(exObject);


            jsonObject.put("week_expense_sector_list", week_expense_sector_list);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Url.WeeklyBillSave + sharedPreferences.getSessionToken(), jsonObject, response(), GenericVollyError.errorListener(getContext(), pDialog));

            Log.e("salary", Url.WeeklyBillSave + sharedPreferences.getSessionToken() + " " + jsonObject);
            ApplicationController.getInstance().addToRequestQueue(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private Response.Listener<JSONObject> response() {
        return new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                dismissProgressDialog();
                try {
                    if (response.getBoolean(StaticValue.Success)) {
                        Toast.makeText(getContext(), response.getString(StaticValue.Message), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), response.getString(StaticValue.Message), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }



    private void setDate() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.date_pick, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setCancelable(false);
        final DatePicker simpleDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
        simpleDatePicker.setSpinnersShown(false);
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int day = simpleDatePicker.getDayOfMonth();
                int year = simpleDatePicker.getYear();
                int month = simpleDatePicker.getMonth() + 1;

                stDate = year + "-" + month + "-" + day;
                tvDate.setText(stDate);

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

    /* access modifiers changed from: private */
    public void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }
}
