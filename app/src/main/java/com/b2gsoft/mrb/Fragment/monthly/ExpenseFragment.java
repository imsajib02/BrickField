package com.b2gsoft.mrb.Fragment.monthly;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.b2gsoft.mrb.Adapter.TotalAdapter;
import com.b2gsoft.mrb.Model.CustomerWiseIncome;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseFragment extends Fragment {

    private ConnectionDetector connectionDetector;
    private String dateS;
    /* access modifiers changed from: private */
    public ArrayList<CustomerWiseIncome> expenseArrayList;
    /* access modifiers changed from: private */
    public ListView listViewExpense;
    private ProgressDialog pDialog;
    private SharedPreferences sharedPreferences;
    private TextView tvDate;
    private LinearLayout linearLayoutTotal, llTotalPaid, llTotalDue, llTotalUnit;
    private TextView tvTotal, tvPaid, tvDue, tvTotalUnit;
    private NumberFormat numberFormatter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        sharedPreferences = new SharedPreferences(getContext());
        connectionDetector = new ConnectionDetector(getContext());
        pDialog = new ProgressDialog(getActivity());
        numberFormatter = new DecimalFormat("#0.00");

        linearLayoutTotal = (LinearLayout) view.findViewById(R.id.ll_total);
        llTotalPaid = (LinearLayout) view.findViewById(R.id.ll_total_paid);
        llTotalDue = (LinearLayout) view.findViewById(R.id.ll_total_due);
        //llTotalUnit = (LinearLayout) view.findViewById(R.id.ll_total_unit);

        tvTotal = (TextView) view.findViewById(R.id.tv_total_balance);
        tvPaid = (TextView)view.findViewById(R.id.tv_total_paid);
        tvDue = (TextView)view.findViewById(R.id.tv_total_due);
        //tvTotalUnit = (TextView)view.findViewById(R.id.tv_total_unit);


        listViewExpense = (ListView) view.findViewById(R.id.lv_monthly_expense);

        if (connectionDetector.isConnectingToInternet()) {
            getExpense();
        }
        return view;
    }

    private void getExpense() {
        showDialog();
        JSONObject jsonObject = new JSONObject();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.MonthlyExpense + sharedPreferences.getSessionToken(), jsonObject, responseData(), GenericVollyError.errorListener(getContext(), pDialog));

        ApplicationController.getInstance().addToRequestQueue(request);

    }

    private Response.Listener<JSONObject> responseData() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismissProgressDialog();

                try {
                    Log.e("res", response.toString());

                    if (response.getBoolean(StaticValue.Success)) {

                        JSONArray data = response.getJSONArray(StaticValue.Data);

                        if(data.length() > 0) {

                            double total =0, paid =0, unit = 0;

                            expenseArrayList = new ArrayList();

                            for (int i = 0; i < data.length(); i++) {

                                JSONObject value = data.getJSONObject(i);

                                CustomerWiseIncome expense = new CustomerWiseIncome();

                                expense.name = value.getString("expense_sub_sector_name");
                                expense.total_price = Double.parseDouble(value.getString(StaticValue.TotalPrice));
                                expense.total_due = Double.parseDouble(value.getString("total_due"));
                                expense.total_cash = Double.parseDouble(value.getString("total_cash"));
                                expense.total_unit = Double.parseDouble(value.getString("total_unit"));

                                total += expense.total_price;
                                paid += expense.total_cash;
                                //unit += expense.total_unit;

                                expenseArrayList.add(expense);
                            }

                            TotalAdapter expenseAdapter = new TotalAdapter(getContext(), expenseArrayList);
                            expenseAdapter.notifyDataSetChanged();
                            listViewExpense.setAdapter(expenseAdapter);

                            linearLayoutTotal.setVisibility(View.VISIBLE);
                            llTotalPaid.setVisibility(View.VISIBLE);
                            llTotalDue.setVisibility(View.VISIBLE);
                            //llTotalUnit.setVisibility(View.VISIBLE);

                            tvTotal.setText(""+ numberFormatter.format(total));
                            tvPaid.setText(""+ numberFormatter.format(paid));
                            tvDue.setText(""+ numberFormatter.format(total-paid));
                            //tvTotalUnit.setText(""+ RoundFormat.roundTwoDecimals(unit));
                        }else {

                            expenseArrayList = new ArrayList();

                            linearLayoutTotal.setVisibility(View.GONE);
                            llTotalPaid.setVisibility(View.GONE);
                            llTotalDue.setVisibility(View.GONE);
                            //llTotalUnit.setVisibility(View.GONE);

                            TotalAdapter expenseAdapter = new TotalAdapter(getContext(), expenseArrayList);
                            expenseAdapter.notifyDataSetChanged();
                            listViewExpense.setAdapter(expenseAdapter);
                        }
                        return;
                    }
                    else {
                        Toast.makeText(getContext(), getString(R.string.failed_to_get_data), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e("income", e.getMessage());
                }}
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
