package com.b2gsoft.mrb.Fragment.LoadUnload;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.b2gsoft.mrb.Adapter.LoadUnloadAdapter;
import com.b2gsoft.mrb.Model.LoadUnload;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UnloadFragment extends Fragment {

    private TextView tvDate, tvFromDate, tvToDate, tvTotal;
    private EditText etChamber;
    private Button btnSave, btnShow;
    private ListView lvChamber;
    private LinearLayout llTotal;

    private LoadUnloadAdapter unloadAdapter;

    private SharedPreferences sharedPreferences;
    private ConnectionDetector connectionDetector;
    private ProgressDialog pDialog;

    private String stringDate = "", stringChamberAmount = "", stringStartDate = "", stringEndDate = "";
    private double totalAmount = 0;

    private int DATE = 1;
    private int STARTDATE = 2;
    private int ENDDATE = 3;

    private List<LoadUnload> unloadList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_unload, container, false);

        sharedPreferences = new SharedPreferences(getContext());
        connectionDetector = new ConnectionDetector(getContext());
        pDialog = new ProgressDialog(getActivity());

        etChamber = (EditText)view.findViewById(R.id.et_chamber);

        llTotal = (LinearLayout) view.findViewById(R.id.ll_total);

        lvChamber = (ListView) view.findViewById(R.id.lv_chamber);

        tvDate = (TextView) view.findViewById(R.id.tv_date);
        tvFromDate = (TextView) view.findViewById(R.id.tv_from_date);
        tvToDate = (TextView) view.findViewById(R.id.tv_to_date);
        tvTotal = (TextView) view.findViewById(R.id.tv_total);

        btnSave = (Button) view.findViewById(R.id.btn_save);
        btnShow = (Button) view.findViewById(R.id.btn_show);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTodaysDate();

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDate(DATE);
            }
        });

        tvFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDate(STARTDATE);
            }
        });

        tvToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDate(ENDDATE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

                stringChamberAmount = etChamber.getText().toString();

                if(isValid()) {

                    if(connectionDetector.isConnectingToInternet()) {

                        saveUnload();
                    }
                }
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(stringStartDate.isEmpty()) {

                    Toast.makeText(getContext(), getString(R.string.give_start_date), Toast.LENGTH_SHORT).show();
                }
                else if(stringEndDate.isEmpty()) {

                    Toast.makeText(getContext(), getString(R.string.give_end_date), Toast.LENGTH_SHORT).show();
                }
                else {

                    if(connectionDetector.isConnectingToInternet()) {

                        getUnloads();
                    }
                }
            }
        });

        return view;
    }


    private void getUnloads() {

        showDialog();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.Load + sharedPreferences.getSessionToken() + "&type=unload&start=" +stringStartDate+ "&end=" +stringEndDate, null, responseLoads(), GenericVollyError.errorListener(getContext(), pDialog));

        ApplicationController.getInstance().addToRequestQueue(request);
    }


    private Response.Listener<JSONObject> responseLoads() {

        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getBoolean(StaticValue.Success)) {

                        unloadList.clear();
                        totalAmount = 0;

                        JSONObject data = response.getJSONObject(StaticValue.Data);
                        JSONArray unloads = data.getJSONArray("loads");

                        if(unloads.length() > 0) {

                            for(int i=0; i<unloads.length(); i++) {

                                JSONObject value = unloads.getJSONObject(i);

                                LoadUnload unload = new LoadUnload();

                                unload.id = value.getInt(StaticValue.Id);
                                unload.date = value.getString(StaticValue.Date);
                                unload.date = unload.date.substring(0, unload.date.indexOf(" "));
                                unload.chamberAmount = value.getString("chamber");

                                unloadList.add(unload);

                                totalAmount = totalAmount + Double.parseDouble(unload.chamberAmount);
                            }

                            llTotal.setVisibility(View.VISIBLE);
                            tvTotal.setText("" + RoundFormat.roundTwoDecimals(totalAmount));
                        }
                        else {

                            llTotal.setVisibility(View.INVISIBLE);
                        }

                        unloadAdapter = new LoadUnloadAdapter(getContext(), unloadList);
                        unloadAdapter.notifyDataSetChanged();
                        lvChamber.setAdapter(unloadAdapter);

                        dismissProgressDialog();

                        return;
                    }
                    else {
                        dismissProgressDialog();
                        Toast.makeText(getContext(), getString(R.string.failed_to_save), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    dismissProgressDialog();
                    Log.e("income", e.getMessage());
                }
            }
        };
    }


    private void clearFields() {

        setTodaysDate();

        etChamber.getText().clear();
        stringChamberAmount = "";

        hideKeyboard();
    }


    private void hideKeyboard() {

        View view = getActivity().getCurrentFocus();

        if(view != null) {

            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private boolean isValid() {

        if(stringDate.isEmpty()) {

            Toast.makeText(getContext(), getString(R.string.give_date), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(stringChamberAmount.isEmpty()) {

            Toast.makeText(getContext(), getString(R.string.give_chamber), Toast.LENGTH_SHORT).show();
            return false;
        }
        else {

            return true;
        }
    }


    private void saveUnload() {

        showDialog();

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(StaticValue.Date, stringDate);
            jsonObject.put("chamber", stringChamberAmount);
            jsonObject.put("type", StaticValue.Unload);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Url.Load + sharedPreferences.getSessionToken(), jsonObject, response(), GenericVollyError.errorListener(getContext(), pDialog));

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

                try {

                    if (response.getBoolean(StaticValue.Success)) {

                        /*JSONObject data = response.getJSONObject(StaticValue.Data);
                        JSONObject loadDetails = data.getJSONObject("load");

                        LoadUnload load = new LoadUnload();

                        load.id = loadDetails.getInt(StaticValue.Id);
                        load.date = loadDetails.getString(StaticValue.Date);
                        load.chamberAmount = loadDetails.getString("chamber");

                        loadList.add(load);
                        loadAdapter.notifyDataSetChanged();

                        totalAmount = totalAmount + Double.parseDouble(load.chamberAmount);

                        llTotal.setVisibility(View.VISIBLE);
                        tvTotal.setText("" +RoundFormat.roundTwoDecimals(totalAmount));*/

                        dismissProgressDialog();

                        Toast.makeText(getContext(), getString(R.string.successfully_saved), Toast.LENGTH_LONG).show();

                        if(!TextUtils.isEmpty(stringStartDate) && !TextUtils.isEmpty(stringEndDate)) {

                            getUnloads();
                        }

                        clearFields();
                    }
                    else {

                        dismissProgressDialog();
                        Toast.makeText(getContext(), getString(R.string.failed_to_save), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    dismissProgressDialog();
                    Log.e("income", e.getMessage());
                }
            }
        };
    }


    private void setTodaysDate() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
        Date todayDate = new Date();

        stringDate = dateFormat.format(todayDate);
        tvDate.setText(stringDate);
    }


    private void setDate(final int value) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.date_pick, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                if(value == DATE) {

                    stringDate = year + "-" + month + "-" + day;
                    tvDate.setText(stringDate);
                }
                else if(value == STARTDATE) {

                    stringStartDate = year + "-" + month + "-" + day;
                    tvFromDate.setText(stringStartDate);
                }
                else if(value == ENDDATE) {

                    stringEndDate = year + "-" + month + "-" + day;
                    tvToDate.setText(stringEndDate);
                }

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
}
