package com.b2gsoft.mrb.Fragment.Expense;


import android.annotation.SuppressLint;
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
import com.b2gsoft.mrb.Adapter.ExpenseAdapterTab;
import com.b2gsoft.mrb.Model.Expense;
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

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ExpenseOilFragment extends Fragment {


    private ConnectionDetector connectionDetector;
    /* access modifiers changed from: private */
    public ArrayList<Expense> expenseArrayList;
    /* access modifiers changed from: private */
    public ListView listViewExpense;
    private ProgressDialog pDialog;
    private SharedPreferences sharedPreferences;
    int pos = 0;

    private LinearLayout linearLayoutTotal;
    private TextView tvTotal, tvPaid;

    private int sub_id = 1;
    public ExpenseOilFragment(int i) {
        sub_id =i;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense_mill_one, container, false);

        pDialog = new ProgressDialog(getActivity());
        connectionDetector = new ConnectionDetector(getContext());
        sharedPreferences = new SharedPreferences(getContext());
        listViewExpense = (ListView)view. findViewById(R.id.lv_expense);
        linearLayoutTotal = (LinearLayout)view.findViewById(R.id.ll_total);

        tvTotal = (TextView)view.findViewById(R.id.tv_total_balance);
        tvPaid = (TextView)view.findViewById(R.id.tv_total_paid);

        if (connectionDetector.isConnectingToInternet()) {
            getExpense();
        }
        return view;
    }


    private void getExpense() {
        showDialog();
        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.ExpenseWiseHistory+"9&expense_sub_sector_id="+sub_id+"&token=" + sharedPreferences.getSessionToken(), jsonObject, response(), GenericVollyError.errorListener(getContext(), pDialog));
        Log.e("exw", Url.ExpenseWiseHistory+ pos+"&token=" + sharedPreferences.getSessionToken());
        ApplicationController.getInstance().addToRequestQueue(request);
    }

    private Response.Listener<JSONObject> response() {
        return new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                dismissProgressDialog();
                try {
                    Log.e("res", response.toString());
                    try {
                        if (response.getBoolean(StaticValue.Success)) {
                            JSONArray data = response.getJSONArray(StaticValue.Data);
                            if (data.length() > 0) {
                                double total =0, paid =0;

                                expenseArrayList = new ArrayList();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject value = data.getJSONObject(i);
                                    JSONObject expenseSector = value.getJSONObject("expense_sector");
                                    Expense expense = new Expense();
                                    expense.expense_way_name = expenseSector.getString(StaticValue.Name);
                                    expense.name = value.getString(StaticValue.ExpenseSub);
                                    expense.unit = value.getString(StaticValue.Unit);
                                    expense.per_unit_price = value.getString(StaticValue.PerUnitPrice);
                                    expense.total_price = value.getString(StaticValue.TotalPrice);
                                    expense.payment_type_id = value.getString(StaticValue.PaymentTypeId);
                                    expense.date = value.getString(StaticValue.ExpenseDate);
                                    expense.cash = value.getString(StaticValue.Cash);
                                    expense.due = value.getString(StaticValue.Due);

                                    total += Double.parseDouble(expense.total_price);
                                    paid += Double.parseDouble(expense.cash);

                                    expenseArrayList.add(expense);
                                }
                                ExpenseAdapterTab expenseAdapter = new ExpenseAdapterTab(getContext(), expenseArrayList);
                                expenseAdapter.notifyDataSetChanged();
                                listViewExpense.setAdapter(expenseAdapter);

                                linearLayoutTotal.setVisibility(View.VISIBLE);


                                tvTotal.setText(""+ RoundFormat.roundTwoDecimals(total));
                                tvPaid.setText(""+ RoundFormat.roundTwoDecimals(paid)+"");
                            }
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), response.getString(StaticValue.Message), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Log.e("expense", e.getMessage());
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

}