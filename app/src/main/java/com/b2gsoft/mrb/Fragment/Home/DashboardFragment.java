package com.b2gsoft.mrb.Fragment.Home;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.b2gsoft.mrb.Activity.expense.DailyExpenseHistoryActivity;
import com.b2gsoft.mrb.Activity.expense.DueExpenseActivity;
import com.b2gsoft.mrb.Activity.income.DailyIncomeHistoryActivity;
import com.b2gsoft.mrb.Activity.income.DueIncomeActivity;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Utils.ApplicationController;
import com.b2gsoft.mrb.Utils.ConnectionDetector;
import com.b2gsoft.mrb.Utils.SharedPreferences;
import com.b2gsoft.mrb.Utils.StaticValue;
import com.b2gsoft.mrb.Utils.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DashboardFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private ConnectionDetector connectionDetector;

    private CardView cvLayoutExpense, cvLayoutIncome, cvLayoutDueIncome, cvLayoutDueExpense;
    private LinearLayout dashboardLayout;
    private TextView tvDailyExpense, tvDailyIncome, tvDueBillIncome, tvDueBillExpense, tvTotalBalance, tvCurrentBalance;
    private NumberFormat numberFormatter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        sharedPreferences = new SharedPreferences(getContext());
        connectionDetector = new ConnectionDetector(getContext());
        numberFormatter = new DecimalFormat("#0.00");

        tvDailyIncome = (TextView) view.findViewById(R.id.tv_daily_income);
        tvDailyExpense = (TextView) view.findViewById(R.id.tv_daily_expense);
        tvDueBillExpense = (TextView) view.findViewById(R.id.tv_daily_due_expense);
        tvDueBillIncome = (TextView) view.findViewById(R.id.tv_daily_due_income);
        tvTotalBalance = (TextView) view.findViewById(R.id.tv_daily_balance);
        tvCurrentBalance = (TextView) view.findViewById(R.id.tv_current_balance);

        cvLayoutExpense = (CardView) view.findViewById(R.id.ll_daily_expense);
        cvLayoutIncome = (CardView) view.findViewById(R.id.ll_daily_income);
        cvLayoutDueIncome = (CardView) view.findViewById(R.id.ll_daily_due_income);
        cvLayoutDueExpense = (CardView) view.findViewById(R.id.ll_daily_due_expense);

        dashboardLayout = (LinearLayout) view.findViewById(R.id.dashboard_layout);

        if(TextUtils.equals(sharedPreferences.getRole(), StaticValue.SuperAdmin)) {

            dashboardLayout.setVisibility(View.INVISIBLE);
        }

        if(connectionDetector.isConnectingToInternet()) {

            if(!TextUtils.equals(sharedPreferences.getRole(), StaticValue.SuperAdmin)) {

                getDashboardData();
            }
        }


        cvLayoutIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), DailyIncomeHistoryActivity.class));
                getActivity().finish();
            }
        });

        cvLayoutExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), DailyExpenseHistoryActivity.class));
                getActivity().finish();
            }
        });

        cvLayoutDueIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), DueIncomeActivity.class));
                getActivity().finish();
            }
        });

        cvLayoutDueExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), DueExpenseActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }


    private void getDashboardData() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.DashBoardData + sharedPreferences.getSessionToken() + "&user_id=" +sharedPreferences.getUserId(), null, response(), error());
        ApplicationController.getInstance().addToRequestQueue(request);
    }


    private Response.Listener<JSONObject> response() {

        return new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    Log.e("ex", response.toString());

                    if(response.getBoolean(StaticValue.Success)) {

                        JSONObject jsonObject = response.getJSONObject("data");
                        double total = Double.parseDouble(jsonObject.getString("totalBalance"));
                        double current = Double.parseDouble(jsonObject.getString("currentBalance"));
                        double expense_due = Double.parseDouble(jsonObject.getString("expenseTotalDueSum"));
                        double income_due = Double.parseDouble(jsonObject.getString("incomeTotalDueSum"));

                        tvDailyExpense.setText(jsonObject.getString("totalDailyExpenseCashSum"));
                        tvDailyIncome.setText(jsonObject.getString("totalDailyIncomeCashSum"));
                        tvDueBillExpense.setText(""+ numberFormatter.format(expense_due));
                        tvDueBillIncome.setText(""+ numberFormatter.format(income_due));
                        tvTotalBalance.setText(""+ numberFormatter.format(total));
                        tvCurrentBalance.setText(""+ numberFormatter.format(current));

                        JSONObject user = jsonObject.getJSONObject("user");

                        sharedPreferences.setRole(user.getString("role"));

                        if(!TextUtils.equals(user.getString("role"), StaticValue.SuperAdmin)) {

                            JSONObject permission = user.getJSONObject("permissions");

                            sharedPreferences.setTotalBalancePermission(permission.getString("total_balance"));
                            sharedPreferences.setCurrentBalancePermission(permission.getString("current_balance"));
                            sharedPreferences.setEditPermission(permission.getString("data_update"));

                            if(TextUtils.equals(user.getString("role"), StaticValue.Manager)) {

                                if(TextUtils.equals(permission.getString("total_balance"), "0")) {

                                    tvTotalBalance.setVisibility(View.INVISIBLE);
                                }
                                else {

                                    tvTotalBalance.setVisibility(View.VISIBLE);
                                }

                                if(TextUtils.equals(permission.getString("current_balance"), "0")) {

                                    tvCurrentBalance.setVisibility(View.INVISIBLE);
                                }
                                else {

                                    tvCurrentBalance.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                    else {
                        Toast.makeText(getContext(), getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("ex", e.getMessage());
                }
            }
        };
    }


    private Response.ErrorListener error() {

        return new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                try {
                    if (error instanceof NetworkError) {

                        Log.e("ex", "Network problem");
                        //Toast.makeText(context, "Network problem", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {

                        Log.e("ex", "Server Error");
                        //Toast.makeText(context, "Server error", Toast.LENGTH_LONG).show();
                    } else if (error instanceof VolleyError) {

                        Log.e("ex", "Volley problem");
                        //Toast.makeText(context, "Volley error", Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e){
                    Log.e("ex", e.getMessage());
                }
            }
        };
    }
}
