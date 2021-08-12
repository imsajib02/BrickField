package com.b2gsoft.mrb.Fragment.Advance;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.b2gsoft.mrb.Adapter.AdvanceAdapter;
import com.b2gsoft.mrb.Adapter.CustomCategoryAdapter;
import com.b2gsoft.mrb.Interface.Update;
import com.b2gsoft.mrb.Model.Advance;
import com.b2gsoft.mrb.Model.Category;
import com.b2gsoft.mrb.Model.Customer;
import com.b2gsoft.mrb.Model.SubCategory;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Utils.ApplicationController;
import com.b2gsoft.mrb.Utils.ConnectionDetector;
import com.b2gsoft.mrb.Interface.DelAccount;
import com.b2gsoft.mrb.Utils.GenericVollyError;
import com.b2gsoft.mrb.Utils.SharedPreferences;
import com.b2gsoft.mrb.Utils.StaticValue;
import com.b2gsoft.mrb.Interface.UpdateAdvance;
import com.b2gsoft.mrb.Utils.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdvancedShowFragment extends Fragment implements UpdateAdvance, DelAccount, Update {

    private ConnectionDetector connectionDetector;
    /* access modifiers changed from: private */
    private AdvanceAdapter advanceAdapter;
    private ArrayList<Advance> advanceArrayList = new ArrayList<>();
    private ArrayList<Advance> filterList = new ArrayList<>();
    /* access modifiers changed from: private */
    private ListView listViewAdvance;
    private Spinner spName;
    private ProgressDialog pDialog;
    private SharedPreferences sharedPreferences;
    private NumberFormat numberFormatter;
    private int pos = 0;
    private LinearLayout linearLayoutTotal;
    private TextView tvTotalUnit, tvPaidUnit, tvTotalAdvance, tvPaidAdvance, tvDueUnit, tvDueAdvance;
    private UpdateAdvance updateSubmit;
    private ArrayList<String> stringArrayListName = new ArrayList<>();
    private ArrayAdapter<String> adapterName;
    private int selectedClassId = 0;

    private String selectedName = "";
    private String tempName = "", stringClassName = "";

    private double totalUnit = 0, paidUnit = 0, totalAdvance = 0, paidAdvance = 0;

    private boolean settingData = false;
    private boolean isCalculatingTotal = false;
    private boolean isCalculatingPrice = false;

    private DelAccount delAccount;
    private Update update;
    private List<Category> categoryList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_advanced_show, container, false);

        updateSubmit = this;
        numberFormatter = new DecimalFormat("#0.00");
        delAccount = this;
        update = this;
        pDialog = new ProgressDialog(getActivity());
        connectionDetector = new ConnectionDetector(getContext());
        sharedPreferences = new SharedPreferences(getContext());
        listViewAdvance = (ListView)view.findViewById(R.id.lv_expense);
        linearLayoutTotal = (LinearLayout)view.findViewById(R.id.ll_total);

        spName = (Spinner) view.findViewById(R.id.sp_name);

        tvTotalUnit = (TextView)view.findViewById(R.id.tv_total_unit);
        tvPaidUnit = (TextView)view.findViewById(R.id.tv_paid_unit);
        tvTotalAdvance = (TextView)view.findViewById(R.id.tv_total_advance);
        tvPaidAdvance = (TextView)view.findViewById(R.id.tv_paid_advance);
        tvDueUnit = (TextView)view.findViewById(R.id.tv_due_unit);
        tvDueAdvance = (TextView)view.findViewById(R.id.tv_due_advance);

        if(connectionDetector.isConnectingToInternet()) {
            getAdvance();
        }

        spName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String name = stringArrayListName.get(position);

                if(!TextUtils.equals(selectedName, name)) {

                    selectedName = name;

                    totalUnit = 0;
                    paidUnit = 0;
                    totalAdvance = 0;
                    paidAdvance = 0;

                    filterList.clear();

                    for(int i=0; i<advanceArrayList.size(); i++) {

                        if(TextUtils.equals(advanceArrayList.get(i).name, name)) {

                            if(TextUtils.equals(advanceArrayList.get(i).type, StaticValue.AdvanceAddition)) {

                                totalUnit += Double.parseDouble(advanceArrayList.get(i).unit);
                                totalAdvance += Double.parseDouble(advanceArrayList.get(i).price);
                            }
                            else if(TextUtils.equals(advanceArrayList.get(i).type, StaticValue.AdvanceDeduction)) {

                                paidUnit += Double.parseDouble(advanceArrayList.get(i).unit);
                                paidAdvance += Double.parseDouble(advanceArrayList.get(i).given);
                            }

                            filterList.add(advanceArrayList.get(i));
                        }
                    }

                    advanceAdapter = new AdvanceAdapter(getContext(), filterList, updateSubmit, AdvancedShowFragment.this);
                    advanceAdapter.notifyDataSetChanged();
                    listViewAdvance.setAdapter(advanceAdapter);

                    if(filterList.size() > 0) {

                        linearLayoutTotal.setVisibility(View.VISIBLE);

                        tvTotalUnit.setText(""+ numberFormatter.format(totalUnit));
                        tvPaidUnit.setText(""+ numberFormatter.format(paidUnit));
                        tvTotalAdvance.setText(""+ numberFormatter.format(totalAdvance));
                        tvPaidAdvance.setText(""+ numberFormatter.format(paidAdvance));
                        tvDueUnit.setText(""+ numberFormatter.format(totalUnit - paidUnit));
                        tvDueAdvance.setText(""+ numberFormatter.format(totalAdvance - paidAdvance));
                    }
                    else {

                        linearLayoutTotal.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void getAdvance() {
        showDialog();
        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.SubmitAdvance+"?token=" + sharedPreferences.getSessionToken(), jsonObject, response(), GenericVollyError.errorListener(getContext(), pDialog));
        Log.e("exw", Url.SubmitAdvance+"?token=" + sharedPreferences.getSessionToken());
        ApplicationController.getInstance().addToRequestQueue(request);
    }


    private Response.Listener<JSONObject> response() {
        return new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {

                try {
                    Log.e("res", response.toString());

                    if (response.getBoolean(StaticValue.Success)) {

                        categoryList.clear();

                        JSONObject data = response.getJSONObject(StaticValue.Data);

                        JSONArray details = data.getJSONArray("advances");

                        if(details.length() > 0) {

                            advanceArrayList.clear();
                            filterList.clear();
                            stringArrayListName.clear();

                            tempName = selectedName;
                            selectedName = "";

                            if(adapterName != null) {
                                adapterName.notifyDataSetChanged();
                            }

                            for (int i = 0; i < details.length(); i++) {

                                JSONObject value = details.getJSONObject(i);
                                Advance advance = new Advance();

                                advance.date = value.getString(StaticValue.Date);
                                advance.id = value.getString(StaticValue.Id);
                                advance.name = value.getString(StaticValue.Name);

                                boolean nameFound = false;

                                for(int j=0; j<stringArrayListName.size(); j++) {

                                    if(TextUtils.equals(advance.name, stringArrayListName.get(j))) {

                                        nameFound = true;
                                        break;
                                    }
                                }

                                if(!nameFound) {

                                    stringArrayListName.add(advance.name);
                                }

                                advance.type = value.getString("type");
                                advance.unit = value.getString(StaticValue.Poriman);
                                advance.per_unit_price = value.getString(StaticValue.Dor);

                                if(TextUtils.equals(advance.type, StaticValue.AdvanceAddition)) {

                                    advance.price = value.getString(StaticValue.Cash);
                                }
                                else {

                                    advance.price = "0";
                                }

                                if(TextUtils.equals(advance.type, StaticValue.AdvanceDeduction)) {

                                    advance.given = value.getString(StaticValue.Cash);
                                }
                                else {

                                    advance.given = "0";
                                }

                                advance.due = value.getString(StaticValue.Due);
                                advance.incomeID = value.getString(StaticValue.IncomeID);
                                advance.classID = value.getString("income_class_id");
                                advance.className = value.getString("class_name");
                                advance.remarks = value.getString("remarks");

                                advanceArrayList.add(advance);
                            }
                        }

                        adapterName = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, stringArrayListName);
                        adapterName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapterName.notifyDataSetChanged();
                        spName.setAdapter(adapterName);

                        if(!TextUtils.equals(tempName, "")) {

                            int index = 0;

                            for(int i=0; i<stringArrayListName.size(); i++) {

                                if(TextUtils.equals(tempName, stringArrayListName.get(i))) {

                                    index = i;
                                    break;
                                }
                            }

                            spName.setSelection(index);
                        }


                        JSONArray categories = data.getJSONArray("income_classes");

                        if(categories.length() > 0) {

                            Category hint = new Category();
                            hint.setName(getString(R.string.select_income_class));

                            categoryList.add(hint);

                            for(int i=0; i<categories.length(); i++) {

                                JSONObject value = categories.getJSONObject(i);

                                Category category = new Category();

                                category.setId(value.getInt(StaticValue.Id));
                                category.setName(value.getString(StaticValue.Name));

                                categoryList.add(category);
                            }
                        }

                        dismissProgressDialog();
                        return;
                    }
                    else {
                        dismissProgressDialog();
                        Toast.makeText(getContext(), getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    dismissProgressDialog();
                    Log.e("income", e.getMessage());
                }
            }
        };
    }

    /*public void calculateTotalAndGiven(final ArrayList<Advance> list) {

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                totalUnit = 0;
                paidUnit = 0;
                totalAdvance = 0;
                paidAdvance = 0;

                for(int i=0; i<list.size(); i++) {

                    total += Double.parseDouble(list.get(i).price);
                    paid += Double.parseDouble(list.get(i).given);
                }

                tvTotalUnit.setText(""+ RoundFormat.roundTwoDecimals(totalUnit));
                tvPaidUnit.setText(""+ RoundFormat.roundTwoDecimals(paidUnit));
                tvTotalAdvance.setText(""+ RoundFormat.roundTwoDecimals(totalAdvance));
                tvPaidAdvance.setText(""+ RoundFormat.roundTwoDecimals(paidAdvance));
            }
        });
    }*/

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

        totalUnit = 0;
        paidUnit = 0;
        totalAdvance = 0;
        paidAdvance = 0;

        selectedName = "";
        stringArrayListName.clear();
        advanceArrayList.clear();
        filterList.clear();
        categoryList.clear();

        tvTotalUnit.setText("");
        tvPaidUnit.setText("");
        tvTotalAdvance.setText("");
        tvPaidAdvance.setText("");
        tvDueUnit.setText("");
        tvDueAdvance.setText("");

        super.onDestroy();
    }

    /* access modifiers changed from: private */
    public void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    private void hideKeyboard() {

        View view = getActivity().getCurrentFocus();

        if(view != null) {

            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void UpdateAdvanceHistory(final Advance advance, final int position) {

        isCalculatingPrice = false;
        isCalculatingTotal = false;

        selectedClassId = 0;
        stringClassName = "";

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.advanced_add_update, null);

        final Spinner spClasses = (Spinner) view.findViewById(R.id.sp_class);
        final EditText etUnit = (EditText) view.findViewById(R.id.et_unit);
        final EditText etPrice = (EditText) view.findViewById(R.id.et_price);
        final EditText etTotal = (EditText) view.findViewById(R.id.et_total);
        final TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);

        Button btnSave = (Button)view.findViewById(R.id.btn_save);

        settingData = true;

        CustomCategoryAdapter customCategoryAdapter = new CustomCategoryAdapter(getContext(), categoryList, true, false, delAccount, false, update);
        spClasses.setAdapter(customCategoryAdapter);

        for(int i=0; i<categoryList.size(); i++) {

            if(!TextUtils.equals(advance.classID, "null") && !TextUtils.isEmpty(advance.classID) && advance.classID != null) {

                if(Integer.parseInt(advance.classID) == categoryList.get(i).getId()) {

                    spClasses.setSelection(i);
                }
            }
        }

        etUnit.setText(advance.unit);
        etPrice.setText(advance.per_unit_price);

        if(TextUtils.equals(advance.type, StaticValue.AdvanceAddition)) {

            tvTitle.setText(getString(R.string.advance_add_title));
            etTotal.setText(advance.price);
        }
        else if(TextUtils.equals(advance.type, StaticValue.AdvanceDeduction)) {

            tvTitle.setText(getString(R.string.advance_porishodh_title));
            etTotal.setText(advance.given);
        }

        settingData = false;

        etUnit.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!settingData && !isCalculatingPrice && !isCalculatingTotal) {
                    setTotal(etUnit, etPrice, etTotal);
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

                if(!settingData && !isCalculatingPrice) {
                    setTotal(etUnit, etPrice, etTotal);
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

                if(!settingData && !isCalculatingTotal) {
                    setPrice(etUnit, etPrice, etTotal);
                }
            }
        });


        spClasses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position > 0) {

                    view.findViewById(R.id.img_del).setVisibility(View.GONE);

                    selectedClassId = categoryList.get(position).getId();
                    stringClassName = categoryList.get(position).getName();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                hideKeyboard();

                dialog.cancel();
                showDialog();

                JSONObject jsonObject = new JSONObject();

                if(TextUtils.equals(advance.type, StaticValue.AdvanceAddition)) {

                    try {

                        if(!TextUtils.equals(stringClassName, "") && !TextUtils.isEmpty(stringClassName)) {

                            jsonObject.put("income_class_id", selectedClassId);
                        }

                        jsonObject.put(StaticValue.Poriman, etUnit.getText().toString());
                        jsonObject.put(StaticValue.Dor, etPrice.getText().toString());
                        jsonObject.put(StaticValue.Cash, etTotal.getText().toString());
                        jsonObject.put(StaticValue.Due, "0");

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, Url.SubmitAdvance + "/" + advance.id + "?token=" + sharedPreferences.getSessionToken(), jsonObject, responseUpdate(position), GenericVollyError.errorListener(getContext(), pDialog));
                        Log.e("ex", jsonObject + " " + Url.SubmitAdvance +"/"+ advance.id + "?token=" + sharedPreferences.getSessionToken());
                        ApplicationController.getInstance().addToRequestQueue(request);

                    } catch (JSONException e) {
                        Log.e("ex", e.getMessage());
                    }
                }
                else if(TextUtils.equals(advance.type, StaticValue.AdvanceDeduction)) {

                    try {

                        if(!TextUtils.equals(stringClassName, "") && !TextUtils.isEmpty(stringClassName)) {

                            jsonObject.put(StaticValue.ClassId, selectedClassId);
                        }

                        jsonObject.put(StaticValue.Unit, etUnit.getText().toString());
                        jsonObject.put(StaticValue.PerUnitPrice, "0");
                        jsonObject.put(StaticValue.Cash, "0");
                        jsonObject.put(StaticValue.Due, "0");
                        jsonObject.put(StaticValue.TotalPrice, "0");

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, Url.IncomeUpdate + advance.incomeID + "?token=" + sharedPreferences.getSessionToken(), jsonObject, responseUpdateIncome(position, advance, etUnit.getText().toString(), etPrice.getText().toString(), etTotal.getText().toString()), GenericVollyError.errorListener(getContext(), pDialog));
                        Log.e("ex", jsonObject + " " + Url.IncomeUpdate + advance.incomeID + "&token=" + sharedPreferences.getSessionToken());
                        ApplicationController.getInstance().addToRequestQueue(request);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private Response.Listener<JSONObject> responseUpdateIncome(final int position, final Advance advance, final String unit, final String price, final String total) {

        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response.getBoolean(StaticValue.Success)) {
                        updateDeduction(position, advance, unit, price, total);
                    } else {
                        dismissProgressDialog();
                        Toast.makeText(getContext(), getString(R.string.failed_to_update), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    dismissProgressDialog();
                    e.printStackTrace();
                }
            }
        };
    }

    private void updateDeduction(int position, Advance advance, String unit, String price, String total) {

        JSONObject jsonObject = new JSONObject();

        try {

            if(!TextUtils.equals(stringClassName, "") && !TextUtils.isEmpty(stringClassName)) {

                jsonObject.put("income_class_id", selectedClassId);
            }

            jsonObject.put(StaticValue.Poriman, unit);
            jsonObject.put(StaticValue.Dor, price);
            jsonObject.put(StaticValue.Cash, total);
            jsonObject.put(StaticValue.Due, "0");

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, Url.SubmitAdvance +"/"+ advance.id + "?token=" + sharedPreferences.getSessionToken(), jsonObject, responseUpdate(position), GenericVollyError.errorListener(getContext(), pDialog));
            Log.e("ex", jsonObject + " " + Url.SubmitAdvance +"/"+ advance.id + "?token=" + sharedPreferences.getSessionToken());
            ApplicationController.getInstance().addToRequestQueue(request);

        } catch (JSONException e) {
            Log.e("ex", e.getMessage());
        }
    }

    private Response.Listener<JSONObject> responseUpdate(int position) {

        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("Advance Update ", response.toString());

                dismissProgressDialog();

                try {

                    if (response.getBoolean(StaticValue.Success)) {
                        Toast.makeText(getContext(), getString(R.string.successfully_updated), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.failed_to_update), Toast.LENGTH_LONG).show();
                    }

                    getAdvance();

                } catch (JSONException e) {
                    Log.e("income", e.getMessage());
                }
            }
        };
    }

    private void setTotal(EditText etUnit, EditText etPrice, EditText etTotal) {

        try {

            isCalculatingTotal = true;

            double total = 0;
            double unit = 0;
            double price = 0;

            if (etUnit.getText().length() > 0) {
                unit = Double.parseDouble(etUnit.getText().toString());
            }
            if (etPrice.getText().length() > 0) {
                price = Double.parseDouble(etPrice.getText().toString());
            }

            total = unit * price;
            etTotal.setText("" + numberFormatter.format(total));

            isCalculatingTotal = false;

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void setPrice(EditText etUnit, EditText etPrice, EditText etTotal) {

        try {

            isCalculatingPrice = true;

            double total = 0;
            double unit = 0;
            double price = 0;

            if (etUnit.getText().length() > 0) {
                unit = Double.parseDouble(etUnit.getText().toString());
            }
            if (etTotal.getText().length() > 0) {
                total = Double.parseDouble(etTotal.getText().toString());
            }

            if(total != 0 && unit != 0) {

                price = total / unit;
                etPrice.setText("" + numberFormatter.format(price));
            }

            isCalculatingPrice = false;

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public void Delete(String id) {

    }

    @Override
    public void DeleteCategory(Category category) {

    }

    @Override
    public void DeleteSubCategory(SubCategory subCategory) {

    }

    @Override
    public void DeleteCustomer(Customer customer) {

    }

    @Override
    public void updateCustomer(Customer customer) {

    }

    @Override
    public void updateCategory(Category category) {

    }

    @Override
    public void updateSubCategory(SubCategory subCategory) {

    }
}

