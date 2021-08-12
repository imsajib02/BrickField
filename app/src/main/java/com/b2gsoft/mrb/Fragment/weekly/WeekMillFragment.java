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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
public class WeekMillFragment extends Fragment {

    private EditText etExName, etExUnit, etExAmount, etExPrice;
    private double ExUnit =0, ExAmount = 0, ExPrice = 0;

    private EditText etkhadliUnit, etkhadliAmount, etkhadliPrice;
    private double KhadliUnit=0, KhadliAmount=0, KhadliPrice=0;

    private EditText etringUnit, etringAmount, etringPrice;
    private double RingUnit=0, RingAmount=0, RingPrice=0;

    private EditText etlakriUnit, etlakriAmount,  etlakriPrice;
    private double LakriUnit=0, LakriAmount=0,  LakriPrice=0;

    private EditText ethandleUnit, ethandleAmount, ethandlePrice;
    private double HandleUnit=0, HandleAmount=0, HandlePrice=0;

    private EditText etpaperUnit, etpaperAmount, etpaperPrice;
    private double PaperUnit=0, PaperAmount=0, PaperPrice=0;

    private EditText etKhorakiName, etKhorakiUnit, etKhorakiAmount, etKhorakiPrice;
    private double KhorakiUnit=0, KhorakiAmount=0, KhorakiPrice=0;
    private Spinner spMill;

    private TextView tvDate, tvKhorakiDate;

    private ProgressDialog pDialog;
    private SharedPreferences sharedPreferences;
    private TextView tvExTotal, tvKhorakiTotal,tvpaperTotal,tvhandleTotal,tvlakriTotal,tvringTotal,tvkhadliTotal , tvTotal1, tvTotal2, tvDue;
    private EditText etGiven;

    private double ExTotal=0, KhorakiTotal=0,PaperTotal=0, HandleTotal, LakriTotal=0, RingTotal=0,KhadliTotal=0, Total1 = 0, Total2 = 0, Due = 0, Given = 0 ;
    ArrayAdapter<CharSequence> adapterMill;
    private String stDate = "", stKhorakiDate = "";

    private Button btnSave;

    private ConnectionDetector connectionDetector;

    int pos = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_week_mill_one, container, false);

        pDialog = new ProgressDialog(getContext());

        sharedPreferences = new SharedPreferences(getContext());

        connectionDetector = new ConnectionDetector(getContext());

        btnSave = (Button)view.findViewById(R.id.btn_save);

        spMill = (Spinner)view.findViewById(R.id.sp_mill);
        adapterMill = ArrayAdapter.createFromResource(getContext(), R.array.mill_type, android.R.layout.simple_spinner_item);
        adapterMill.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMill.setAdapter(adapterMill);

        spMill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                pos = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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


        etkhadliAmount = (EditText)view.findViewById(R.id.et_khadli_amount);
        etkhadliUnit = (EditText)view.findViewById(R.id.et_khadli_unit);
        etkhadliPrice = (EditText)view.findViewById(R.id.et_khadli_price);
        tvkhadliTotal = (TextView)view.findViewById(R.id.tv_khadli_total);


        etkhadliUnit.addTextChangedListener(new TextWatcher() {
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
        etkhadliPrice.addTextChangedListener(new TextWatcher() {
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
        etkhadliAmount.addTextChangedListener(new TextWatcher() {
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


        etringAmount = (EditText)view.findViewById(R.id.et_ring_amount);
        etringPrice = (EditText)view.findViewById(R.id.et_ring_price);
        etringUnit = (EditText)view.findViewById(R.id.et_ring_unit);
        tvringTotal = (TextView)view.findViewById(R.id.tv_ring_total);


        etringUnit.addTextChangedListener(new TextWatcher() {
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
        etringAmount.addTextChangedListener(new TextWatcher() {
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
        etringPrice.addTextChangedListener(new TextWatcher() {
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

        etlakriAmount = (EditText)view.findViewById(R.id.et_lakri_amount);
        etlakriPrice = (EditText)view.findViewById(R.id.et_lakri_price);
        etlakriUnit = (EditText)view.findViewById(R.id.et_lakri_unit);
        tvlakriTotal = (TextView)view.findViewById(R.id.tv_lakri_total);


        etlakriUnit.addTextChangedListener(new TextWatcher() {
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
        etlakriAmount.addTextChangedListener(new TextWatcher() {
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
        etlakriPrice.addTextChangedListener(new TextWatcher() {
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

        ethandleAmount = (EditText)view.findViewById(R.id.et_handle_amount);
        ethandlePrice = (EditText)view.findViewById(R.id.et_handle_price);
        ethandleUnit = (EditText)view.findViewById(R.id.et_handle_unit);
        tvhandleTotal = (TextView)view.findViewById(R.id.tv_handle_total);


        ethandleUnit.addTextChangedListener(new TextWatcher() {
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
        ethandleAmount.addTextChangedListener(new TextWatcher() {
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
        ethandlePrice.addTextChangedListener(new TextWatcher() {
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

        etpaperPrice = (EditText)view.findViewById(R.id.et_paper_price);
        etpaperAmount = (EditText)view.findViewById(R.id.et_paper_amount);
        etpaperUnit = (EditText)view.findViewById(R.id.et_paper_unit);
        tvpaperTotal = (TextView)view.findViewById(R.id.tv_paper_total);


        etpaperUnit.addTextChangedListener(new TextWatcher() {
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
        etpaperAmount.addTextChangedListener(new TextWatcher() {
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
        etpaperPrice.addTextChangedListener(new TextWatcher() {
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

    private void save() {

        showDialog();

        JSONObject jsonObject = new JSONObject();
        JSONArray week_expense_sector_list = new JSONArray();
        JSONObject exObject = new JSONObject();
        JSONObject khorakiObject = new JSONObject();
        JSONObject khadliObject = new JSONObject();
        JSONObject ringObject = new JSONObject();
        JSONObject lakriObject = new JSONObject();
        JSONObject handleObject = new JSONObject();
        JSONObject paperObject = new JSONObject();
        JSONObject billGiveObject = new JSONObject();
        JSONObject dueObject = new JSONObject();
        try {
            exObject.put(StaticValue.Week_expense_sector_name_id, pos+1);
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

            khorakiObject.put(StaticValue.Week_expense_sector_name_id, pos+1);
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

            khadliObject.put(StaticValue.Week_expense_sector_name_id, pos+1);
            //exObject.put(StaticValue.Week_expense_sub_sector_name_id, pos+1);
            khadliObject.put(StaticValue.Week_expense_type_name_id, 2);
            khadliObject.put(StaticValue.Date, stDate);
            khadliObject.put(StaticValue.Name, "খাদলী");

            khadliObject.put(StaticValue.Unit, KhadliUnit);
            khadliObject.put(StaticValue.Week, "");
            khadliObject.put(StaticValue.Poriman, KhadliAmount);
            khadliObject.put(StaticValue.Dor, KhadliPrice);
            khadliObject.put(StaticValue.UserId, sharedPreferences.getUserId());
            khadliObject.put(StaticValue.UpdateUserId, sharedPreferences.getUserId());

            week_expense_sector_list.put(khadliObject);


            ringObject.put(StaticValue.Week_expense_sector_name_id, pos+1);
            //exObject.put(StaticValue.Week_expense_sub_sector_name_id, pos+1);
            ringObject.put(StaticValue.Week_expense_type_name_id, 3);
            ringObject.put(StaticValue.Date, stDate);
            ringObject.put(StaticValue.Week, "");
            ringObject.put(StaticValue.Name, "রিং ঝাড়");
            ringObject.put(StaticValue.Unit, RingUnit);
            ringObject.put(StaticValue.Poriman, RingAmount);
            ringObject.put(StaticValue.Dor, RingPrice);
            ringObject.put(StaticValue.UserId, sharedPreferences.getUserId());
            ringObject.put(StaticValue.UpdateUserId, sharedPreferences.getUserId());

            week_expense_sector_list.put(ringObject);


            lakriObject.put(StaticValue.Week_expense_sector_name_id, pos+1);
            //exObject.put(StaticValue.Week_expense_sub_sector_name_id, pos+1);
            lakriObject.put(StaticValue.Week_expense_type_name_id, 4);
            lakriObject.put(StaticValue.Date, stDate);
            lakriObject.put(StaticValue.Week, "");
            lakriObject.put(StaticValue.Name, "লাকড়ী");
            lakriObject.put(StaticValue.Unit, LakriUnit);
            lakriObject.put(StaticValue.Poriman, LakriAmount);
            lakriObject.put(StaticValue.Dor, LakriPrice);
            lakriObject.put(StaticValue.UserId, sharedPreferences.getUserId());
            lakriObject.put(StaticValue.UpdateUserId, sharedPreferences.getUserId());

            week_expense_sector_list.put(lakriObject);


            handleObject.put(StaticValue.Week_expense_sector_name_id, pos+1);
            //exObject.put(StaticValue.Week_expense_sub_sector_name_id, pos+1);
            handleObject.put(StaticValue.Week_expense_type_name_id, 5);
            handleObject.put(StaticValue.Date, stDate);
            handleObject.put(StaticValue.Name, "হ্যান্ডেল ব");
            handleObject.put(StaticValue.Week, "");
            handleObject.put(StaticValue.Unit, HandleUnit);
            handleObject.put(StaticValue.Poriman, HandleAmount);
            handleObject.put(StaticValue.Dor, HandlePrice);
            handleObject.put(StaticValue.UserId, sharedPreferences.getUserId());
            handleObject.put(StaticValue.UpdateUserId, sharedPreferences.getUserId());

            week_expense_sector_list.put(handleObject);


            paperObject.put(StaticValue.Week_expense_sector_name_id, pos+1);
            //exObject.put(StaticValue.Week_expense_sub_sector_name_id, pos+1);
            paperObject.put(StaticValue.Week_expense_type_name_id, 6);
            paperObject.put(StaticValue.Date, stDate);
            paperObject.put(StaticValue.Week, "");
            paperObject.put(StaticValue.Name, "কাগজ");
            paperObject.put(StaticValue.Unit, PaperUnit);
            paperObject.put(StaticValue.Poriman, PaperAmount);
            paperObject.put(StaticValue.Dor, PaperPrice);
            paperObject.put(StaticValue.UserId, sharedPreferences.getUserId());
            paperObject.put(StaticValue.UpdateUserId, sharedPreferences.getUserId());

            week_expense_sector_list.put(paperObject);


            billGiveObject.put(StaticValue.Week_expense_sector_name_id, pos+1);
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


            dueObject.put(StaticValue.Week_expense_sector_name_id, pos+1);
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

            Log.e("week", Url.WeeklyBillSave + sharedPreferences.getSessionToken() + " " + jsonObject);
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
            tvKhorakiTotal.setText("" + RoundFormat.roundTwoDecimals(KhorakiTotal));
            Total1 = ExTotal- KhorakiTotal;
            tvTotal1.setText(""+RoundFormat.roundTwoDecimals(Total1));


            ExTotal = ExUnit * ExPrice * ExAmount;
            tvExTotal.setText("" + RoundFormat.roundTwoDecimals(ExTotal));

            if (etkhadliUnit.getText().length() > 0) {
                KhadliUnit = Double.parseDouble(etkhadliUnit.getText().toString());
            }
            if (etkhadliAmount.getText().length() > 0) {
                KhadliAmount = Double.parseDouble(etkhadliAmount.getText().toString());
            }
            if (etkhadliPrice.getText().length() > 0) {
                KhadliPrice = Double.parseDouble(etkhadliPrice.getText().toString());
            }

            KhadliTotal = KhadliUnit * KhadliAmount * KhadliPrice;
            tvkhadliTotal.setText("" + RoundFormat.roundTwoDecimals(KhadliTotal));

            if (etringUnit.getText().length() > 0) {
                RingUnit = Double.parseDouble(etringUnit.getText().toString());
            }
            if (etringAmount.getText().length() > 0) {
                RingAmount = Double.parseDouble(etringAmount.getText().toString());
            }
            if (etringPrice.getText().length() > 0) {
                RingPrice = Double.parseDouble(etringPrice.getText().toString());
            }

            RingTotal = RingUnit * RingAmount * RingPrice;
            tvringTotal.setText("" + RoundFormat.roundTwoDecimals(RingTotal));

            if (etlakriUnit.getText().length() > 0) {
                LakriUnit = Double.parseDouble(etlakriUnit.getText().toString());
            }
            if (etlakriAmount.getText().length() > 0) {
                LakriAmount = Double.parseDouble(etlakriAmount.getText().toString());
            }
            if (etlakriPrice.getText().length() > 0) {
                LakriPrice = Double.parseDouble(etlakriPrice.getText().toString());
            }

            LakriTotal = LakriUnit * LakriAmount * LakriPrice;
            tvlakriTotal.setText("" + RoundFormat.roundTwoDecimals(LakriTotal));

            RingTotal = RingUnit * RingAmount * RingPrice;
            tvringTotal.setText("" + RoundFormat.roundTwoDecimals(RingTotal));

            if (etlakriUnit.getText().length() > 0) {
                LakriUnit = Double.parseDouble(etlakriUnit.getText().toString());
            }
            if (etlakriAmount.getText().length() > 0) {
                LakriAmount = Double.parseDouble(etlakriAmount.getText().toString());
            }
            if (etlakriPrice.getText().length() > 0) {
                LakriPrice = Double.parseDouble(etlakriPrice.getText().toString());
            }

            LakriTotal = LakriUnit * LakriAmount * LakriPrice;
            tvlakriTotal.setText("" + RoundFormat.roundTwoDecimals(LakriTotal));

            if (ethandleUnit.getText().length() > 0) {
                HandleUnit = Double.parseDouble(ethandleUnit.getText().toString());
            }
            if (ethandlePrice.getText().length() > 0) {
                HandlePrice = Double.parseDouble(ethandlePrice.getText().toString());
            }
            if (ethandleAmount.getText().length() > 0) {
                HandleAmount = Double.parseDouble(ethandleAmount.getText().toString());
            }

            HandleTotal = HandleAmount * HandleAmount * HandlePrice;
            tvhandleTotal.setText("" + RoundFormat.roundTwoDecimals(HandleTotal));

            if (etpaperUnit.getText().length() > 0) {
                PaperUnit = Double.parseDouble(etpaperUnit.getText().toString());
            }
            if (etpaperAmount.getText().length() > 0) {
                PaperAmount = Double.parseDouble(etpaperAmount.getText().toString());
            }
            if (etpaperPrice.getText().length() > 0) {
                PaperPrice = Double.parseDouble(etpaperPrice.getText().toString());
            }

            PaperTotal = PaperUnit * PaperAmount * PaperPrice;
            tvpaperTotal.setText("" + RoundFormat.roundTwoDecimals(PaperTotal));

            Total2 = Total1 + KhadliTotal + RingTotal + LakriTotal + HandleTotal + PaperTotal;
            tvTotal2.setText(""+ RoundFormat.roundTwoDecimals(Total2));

            if (etGiven.getText().length() > 0) {
                Given = Double.parseDouble(etGiven.getText().toString());
            }

            Due = Total2 - Given;
            tvDue.setText(""+ RoundFormat.roundTwoDecimals(Due));

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
