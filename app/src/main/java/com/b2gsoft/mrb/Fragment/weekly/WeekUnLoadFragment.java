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
public class WeekUnLoadFragment extends Fragment {


    public WeekUnLoadFragment() {
        // Required empty public constructor
    }
    private EditText etExName, etExUnit, etExAmount, etExPrice;
    private double ExUnit =0, ExAmount = 0, ExPrice = 0;

    private EditText etKhorakiName, etKhorakiUnit, etKhorakiAmount, etKhorakiPrice;
    private double KhorakiUnit=0, KhorakiAmount=0, KhorakiPrice=0;

    private EditText etGateUnit, etGateAmount, etGatePrice;
    private double GateUnit=0, GateAmount=0, GatePrice=0;

    private EditText etFelanoUnit, etFelanoAmount, etFelanoPrice;
    private double FelanoUnit=0, FelanoAmount=0, FelanoPrice=0;

    private EditText etOthersUnit, etOthersAmount, etOthersPrice;
    private double OthersUnit=0, OthersAmount=0, OthersPrice=0;

    private EditText etGiven;

    private TextView tvDate, tvKhorakiDate;

    private TextView tvExTotal, tvKhorakiTotal,tvGateTotal,tvFelanoTotal,tvOthersTotal, tvTotal1, tvTotal2, tvDue;
    private double ExTotal=0, KhorakiTotal=0, GateTotal=0,FelanoTotal=0, OthersTotal=0, Total1 = 0, Total2 = 0, Due = 0, Given = 0 ;

    private String stDate = "", stKhorakiDate = "";

    private Button btnSave;

    private ConnectionDetector connectionDetector;

    int pos = 0;

    private ProgressDialog pDialog;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_week_unload, container, false);

        pDialog = new ProgressDialog(getContext());

        sharedPreferences = new SharedPreferences(getContext());

        connectionDetector = new ConnectionDetector(getContext());

        btnSave = (Button)view.findViewById(R.id.btn_save);

        tvDate = (TextView)view.findViewById(R.id.tv_ex_date);
        tvKhorakiDate = (TextView)view.findViewById(R.id.tv_khoraki_date);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(1);
            }
        });

        tvKhorakiDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(2);
            }
        });

        etExName = (EditText)view.findViewById(R.id.et_ex_name);
        etExUnit = (EditText)view.findViewById(R.id.et_ex_unit);
        etExAmount = (EditText)view.findViewById(R.id.et_ex_amount);
        etExPrice = (EditText)view.findViewById(R.id.et_ex_price);
        tvExTotal = (TextView)view.findViewById(R.id.tv_ex_total);

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

        etKhorakiName = (EditText)view.findViewById(R.id.et_khoraki_name);
        etKhorakiAmount = (EditText)view.findViewById(R.id.et_khoraki_amount);
        etKhorakiUnit = (EditText)view.findViewById(R.id.et_khoraki_unit);
        etKhorakiPrice = (EditText)view.findViewById(R.id.et_khoraki_price);
        tvKhorakiTotal = (TextView)view.findViewById(R.id.tv_khoraki_total);

        etKhorakiUnit.addTextChangedListener(new TextWatcher() {
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
        etKhorakiAmount.addTextChangedListener(new TextWatcher() {
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
        etKhorakiPrice.addTextChangedListener(new TextWatcher() {
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

        etGateUnit = (EditText)view.findViewById(R.id.et_gate_unit);
        etGateAmount = (EditText)view.findViewById(R.id.et_gate_amount);
        etGatePrice = (EditText)view.findViewById(R.id.et_gate_price);
        tvGateTotal = (TextView)view.findViewById(R.id.tv_gate_total);


        etGateUnit.addTextChangedListener(new TextWatcher() {
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
        etGateAmount.addTextChangedListener(new TextWatcher() {
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
        etGatePrice.addTextChangedListener(new TextWatcher() {
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


        etFelanoUnit = (EditText)view.findViewById(R.id.et_adla_unit);
        etFelanoAmount = (EditText)view.findViewById(R.id.et_adla_amount);
        etFelanoPrice = (EditText)view.findViewById(R.id.et_adla_price);
        tvFelanoTotal = (TextView)view.findViewById(R.id.tv_adla_total);


        etFelanoUnit.addTextChangedListener(new TextWatcher() {
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
        etFelanoAmount.addTextChangedListener(new TextWatcher() {
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
        etFelanoPrice.addTextChangedListener(new TextWatcher() {
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

        etOthersUnit = (EditText)view.findViewById(R.id.et_others_unit);
        etOthersPrice = (EditText)view.findViewById(R.id.et_others_price);
        etOthersAmount = (EditText)view.findViewById(R.id.et_others_amount);
        tvOthersTotal = (TextView)view.findViewById(R.id.tv_others_total);


        etOthersUnit.addTextChangedListener(new TextWatcher() {
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
        etOthersAmount.addTextChangedListener(new TextWatcher() {
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
        etOthersPrice.addTextChangedListener(new TextWatcher() {
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

        tvTotal1 = (TextView)view.findViewById(R.id.tv_total);
        tvTotal2 = (TextView)view.findViewById(R.id.tv_total_bill);
        tvDue = (TextView)view.findViewById(R.id.tv_due);
        etGiven = (EditText)view.findViewById(R.id.et_bill);


        etGiven.addTextChangedListener(new TextWatcher() {
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

                if (connectionDetector.isConnectingToInternet()){
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
            tvExTotal.setText("" + ExTotal);

            if (etKhorakiUnit.getText().length() > 0) {
                KhorakiUnit = Double.parseDouble(etKhorakiUnit.getText().toString());
            }
            if (etKhorakiAmount.getText().length() > 0) {
                KhorakiAmount = Double.parseDouble(etKhorakiAmount.getText().toString());
            }
            if (etKhorakiPrice.getText().length() > 0) {
                KhorakiPrice = Double.parseDouble(etKhorakiPrice.getText().toString());
            }

            KhorakiTotal = KhorakiUnit * KhorakiAmount * KhorakiPrice;
            tvKhorakiTotal.setText("" + KhorakiTotal);
            Total1 = ExTotal- KhorakiTotal;
            tvTotal1.setText(""+ RoundFormat.roundTwoDecimals(Total1));


            ExTotal = ExUnit * ExPrice * ExAmount;
            tvExTotal.setText("" + RoundFormat.roundTwoDecimals(ExTotal));

            if (etGateUnit.getText().length() > 0) {
                GateUnit = Double.parseDouble(etGateUnit.getText().toString());
            }
            if (etGateAmount.getText().length() > 0) {
                GateAmount = Double.parseDouble(etGateAmount.getText().toString());
            }
            if (etGatePrice.getText().length() > 0) {
                GatePrice = Double.parseDouble(etGatePrice.getText().toString());
            }

            GateTotal = GateUnit * GateAmount * GatePrice;
            tvGateTotal.setText("" + RoundFormat.roundTwoDecimals(GateTotal));

            if (etFelanoUnit.getText().length() > 0) {
                FelanoUnit = Double.parseDouble(etFelanoUnit.getText().toString());
            }
            if (etFelanoAmount.getText().length() > 0) {
                FelanoAmount = Double.parseDouble(etFelanoAmount.getText().toString());
            }
            if (etFelanoPrice.getText().length() > 0) {
                FelanoPrice = Double.parseDouble(etFelanoPrice.getText().toString());
            }

            FelanoTotal = FelanoUnit * FelanoAmount * FelanoPrice;
            tvFelanoTotal.setText("" + RoundFormat.roundTwoDecimals(FelanoTotal));

            if (etOthersUnit.getText().length() > 0) {
                OthersUnit = Double.parseDouble(etOthersUnit.getText().toString());
            }
            if (etOthersPrice.getText().length() > 0) {
                OthersPrice = Double.parseDouble(etOthersPrice.getText().toString());
            }
            if (etOthersAmount.getText().length() > 0) {
                OthersAmount = Double.parseDouble(etOthersAmount.getText().toString());
            }

            OthersTotal = OthersUnit * OthersPrice * OthersAmount;
            tvOthersTotal.setText("" + RoundFormat.roundTwoDecimals(OthersTotal));


            Total2 = Total1 + OthersTotal + GateTotal + FelanoTotal;
            tvTotal2.setText(""+ RoundFormat.roundTwoDecimals(Total2));

            if (etGiven.getText().length() > 0) {
                Given = Double.parseDouble(etGiven.getText().toString());
            }

            Due = Total2 - Given;
            tvDue.setText(""+RoundFormat.roundTwoDecimals(Due));

        } catch (Exception e) {
        }
    }

    private void setDate(final int i) {
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
                if (i == 1) {
                    stDate = year + "-" + month + "-" + day;
                    tvDate.setText(stDate);
                } else if (i == 2) {
                    stKhorakiDate = year + "-" + month + "-" + day;
                    tvKhorakiDate.setText(stKhorakiDate);
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

    private void save() {

        showDialog();

        JSONObject jsonObject = new JSONObject();
        JSONArray week_expense_sector_list = new JSONArray();
        JSONObject exObject = new JSONObject();
        JSONObject khorakiObject = new JSONObject();
        JSONObject othersObject = new JSONObject();
        JSONObject GateObject = new JSONObject();
        JSONObject FelanoObject = new JSONObject();
        JSONObject billGiveObject = new JSONObject();
        JSONObject dueObject = new JSONObject();
        try {
            exObject.put(StaticValue.Week_expense_sector_name_id, 7);
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

            khorakiObject.put(StaticValue.Week_expense_sector_name_id, 7);
            //khorakiObject.put(StaticValue.Week_expense_sub_sector_name_id, pos+1);
            khorakiObject.put(StaticValue.Week_expense_type_name_id, 1);
            khorakiObject.put(StaticValue.Date, stKhorakiDate);
            khorakiObject.put(StaticValue.Week, "");
            khorakiObject.put(StaticValue.Name, etKhorakiName.getText().toString());
            khorakiObject.put(StaticValue.Unit, KhorakiUnit);
            khorakiObject.put(StaticValue.Poriman, KhorakiAmount);
            khorakiObject.put(StaticValue.Dor, KhorakiPrice);
            khorakiObject.put(StaticValue.UserId, sharedPreferences.getUserId());
            khorakiObject.put(StaticValue.UpdateUserId, sharedPreferences.getUserId());

            week_expense_sector_list.put(khorakiObject);

            GateObject.put(StaticValue.Week_expense_sector_name_id, 7);
            //exObject.put(StaticValue.Week_expense_sub_sector_name_id, pos+1);
            GateObject.put(StaticValue.Week_expense_type_name_id, 15);
            GateObject.put(StaticValue.Date, stDate);
            GateObject.put(StaticValue.Name, "");

            GateObject.put(StaticValue.Unit, GateUnit);
            GateObject.put(StaticValue.Week, "");
            GateObject.put(StaticValue.Poriman, GateAmount);
            GateObject.put(StaticValue.Dor, GatePrice);
            GateObject.put(StaticValue.UserId, sharedPreferences.getUserId());
            GateObject.put(StaticValue.UpdateUserId, sharedPreferences.getUserId());

            week_expense_sector_list.put(GateObject);

            FelanoObject.put(StaticValue.Week_expense_sector_name_id, 7);
            //exObject.put(StaticValue.Week_expense_sub_sector_name_id, pos+1);
            FelanoObject.put(StaticValue.Week_expense_type_name_id, 16);
            FelanoObject.put(StaticValue.Date, stDate);
            FelanoObject.put(StaticValue.Name, "");

            FelanoObject.put(StaticValue.Unit, FelanoUnit);
            FelanoObject.put(StaticValue.Week, "");
            FelanoObject.put(StaticValue.Poriman, FelanoAmount);
            FelanoObject.put(StaticValue.Dor, FelanoPrice);
            FelanoObject.put(StaticValue.UserId, sharedPreferences.getUserId());
            FelanoObject.put(StaticValue.UpdateUserId, sharedPreferences.getUserId());

            week_expense_sector_list.put(FelanoObject);


            othersObject.put(StaticValue.Week_expense_sector_name_id, 7);
            //exObject.put(StaticValue.Week_expense_sub_sector_name_id, pos+1);
            othersObject.put(StaticValue.Week_expense_type_name_id, 14);
            othersObject.put(StaticValue.Date, stDate);
            othersObject.put(StaticValue.Week, "");
            othersObject.put(StaticValue.Name, "");
            othersObject.put(StaticValue.Unit, OthersUnit);
            othersObject.put(StaticValue.Poriman, OthersAmount);
            othersObject.put(StaticValue.Dor, OthersPrice);
            othersObject.put(StaticValue.UserId, sharedPreferences.getUserId());
            othersObject.put(StaticValue.UpdateUserId, sharedPreferences.getUserId());

            week_expense_sector_list.put(othersObject);

            billGiveObject.put(StaticValue.Week_expense_sector_name_id, 7);
            //exObject.put(StaticValue.Week_expense_sub_sector_name_id, pos+1);
            billGiveObject.put(StaticValue.Week_expense_type_name_id, 7);
            billGiveObject.put(StaticValue.Date, stDate);
            billGiveObject.put(StaticValue.Name, "বিল দেয়া");
            billGiveObject.put(StaticValue.Week, "");
            billGiveObject.put(StaticValue.Unit, 1);
            billGiveObject.put(StaticValue.Poriman, 1);
            billGiveObject.put(StaticValue.Dor, Given);
            billGiveObject.put(StaticValue.UserId, sharedPreferences.getUserId());
            billGiveObject.put(StaticValue.UpdateUserId, sharedPreferences.getUserId());

            week_expense_sector_list.put(billGiveObject);


            dueObject.put(StaticValue.Week_expense_sector_name_id, 7);
            //exObject.put(StaticValue.Week_expense_sub_sector_name_id, pos+1);
            dueObject.put(StaticValue.Week_expense_type_name_id, 8);
            dueObject.put(StaticValue.Date, stDate);
            dueObject.put(StaticValue.Week, "");
            dueObject.put(StaticValue.Name, "বকেয়া");
            dueObject.put(StaticValue.Unit, 1);
            dueObject.put(StaticValue.Poriman, 1);
            dueObject.put(StaticValue.Dor, Due);
            dueObject.put(StaticValue.UserId, sharedPreferences.getUserId());
            dueObject.put(StaticValue.UpdateUserId, sharedPreferences.getUserId());

            week_expense_sector_list.put(dueObject);

            jsonObject.put("week_expense_sector_list", week_expense_sector_list);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Url.WeeklyBillSave + sharedPreferences.getSessionToken(), jsonObject, response(), GenericVollyError.errorListener(getContext(), pDialog));

            Log.e("load", Url.WeeklyBillSave + sharedPreferences.getSessionToken() + " " + jsonObject);
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

