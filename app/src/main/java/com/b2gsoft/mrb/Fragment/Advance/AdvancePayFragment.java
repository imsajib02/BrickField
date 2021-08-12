package com.b2gsoft.mrb.Fragment.Advance;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.b2gsoft.mrb.Utils.RoundFormat;
import com.b2gsoft.mrb.Utils.SharedPreferences;
import com.b2gsoft.mrb.Utils.StaticValue;
import com.b2gsoft.mrb.Utils.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AdvancePayFragment extends Fragment implements DelAccount, Update {

    private HashMap customerID = new HashMap();
    private HashMap customerAdvance = new HashMap();
    private ArrayAdapter<String> adapterName;
    private ArrayList<String> stringArrayListName = new ArrayList<>();
    private EditText etVehicle, etUnit, etPrice, etPaid, etRemarks;
    private AutoCompleteTextView acName;
    private TextView tvRemainingAdvance, tvDue;
    private TextView tvDate;
    private Spinner spClasses;
    private double total = 0, due = 0;
    private Button btnSave;
    private SharedPreferences sharedPreferences;
    private ConnectionDetector connectionDetector;
    private String stringName = "", stringUnit = "", stringPrice = "", stringDate = "", stringVehicle = "", stringTotal = "", stringRemarks = "";
    private int selectedCustomerId = 0, selectedClassId = 0;
    private ProgressDialog pDialog;
    private String stringClassName = "";

    private boolean isCalculatingTotal = false;
    private boolean isCalculatingPrice = false;
    private boolean clearingInput = false;
    private NumberFormat numberFormatter;

    private DelAccount delAccount;
    private Update update;

    private List<Category> categoryList = new ArrayList<>();
    private CustomCategoryAdapter customCategoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_advance_pay_fragment, container, false);

        sharedPreferences = new SharedPreferences(getContext());
        connectionDetector = new ConnectionDetector(getContext());
        pDialog = new ProgressDialog(getActivity());
        delAccount = this;
        update = this;
        numberFormatter = new DecimalFormat("#0.00");

        acName = (AutoCompleteTextView)view. findViewById(R.id.ac_name);

        spClasses = (Spinner) view.findViewById(R.id.sp_class);

        etVehicle = (EditText)view.findViewById(R.id.et_vehicle_no);
        etUnit = (EditText)view.findViewById(R.id.et_paying_unit);
        etPrice = (EditText) view.findViewById(R.id.et_paying_price);
        etPaid = (EditText) view.findViewById(R.id.et_paid);
        etRemarks = (EditText) view.findViewById(R.id.et_remarks);


        tvRemainingAdvance = (TextView) view.findViewById(R.id.tv_total);
        tvDate = (TextView) view.findViewById(R.id.tv_date);
        tvDue = (TextView) view.findViewById(R.id.tv_due);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getAdvanceDetails();
        setTodaysDate();


        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });


        tvRemainingAdvance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!isCalculatingPrice && !isCalculatingTotal && !clearingInput) {
                    setTotalAndPrice();
                }
            }
        });

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
                    setTotal();
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
                    setTotal();
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

                if(!isCalculatingTotal && !clearingInput) {
                    setPrice();
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


        acName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acName.showDropDown();
            }
        });

        acName.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                stringName = (String) parent.getItemAtPosition(position);
                selectedCustomerId = (int) customerID.get(stringName);

                tvRemainingAdvance.setText("" + RoundFormat.roundTwoDecimals((double) customerAdvance.get(stringName)));
            }
        });

        acName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!clearingInput) {

                    if(TextUtils.isEmpty(acName.getText().toString())) {

                        stringName = "";
                        selectedCustomerId = 0;

                        tvRemainingAdvance.setText("");
                    }
                }
            }
        });

        btnSave = (Button) view.findViewById(R.id.btn_save);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

                try{
                    stringName = acName.getText().toString();
                    selectedCustomerId = (int) customerID.get(stringName);
                }catch (Exception ex){
                    stringName = "";
                }

                stringVehicle = etVehicle.getText().toString();
                stringUnit = etUnit.getText().toString();
                stringPrice = etPrice.getText().toString();
                stringTotal = etPaid.getText().toString();
                stringRemarks = etRemarks.getText().toString();

                if (checkValid()) {
                    if (connectionDetector.isConnectingToInternet()) {
                        Log.e("user", selectedCustomerId+" , "+stringName);
                        saveIncomeEntry();
                        Log.e("user", selectedCustomerId+" , "+stringName);
                    }
                }

            }
        });

        return view;
    }


    private void getAdvanceDetails() {

        showDialog();
        JSONObject jsonObject = new JSONObject();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.AdvanceDetails + sharedPreferences.getSessionToken(), jsonObject, responseAdvanceDetails(), GenericVollyError.errorListener(getContext(), pDialog));
        ApplicationController.getInstance().addToRequestQueue(request);
        Log.e("url", Url.AdvanceDetails + sharedPreferences.getSessionToken());

    }

    private Response.Listener<JSONObject> responseAdvanceDetails() {

        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response.getBoolean(StaticValue.Success)){

                        stringArrayListName.clear();
                        customerID.clear();
                        customerAdvance.clear();

                        JSONObject data = response.getJSONObject(StaticValue.Data);
                        JSONArray details = data.getJSONArray("advanced_names");

                        if(details.length() > 0) {

                            for(int i=0; i<details.length(); i++) {

                                JSONObject jsonObject = details.getJSONObject(i);

                                stringArrayListName.add(jsonObject.getString(StaticValue.Name));
                                customerID.put(jsonObject.getString(StaticValue.Name), jsonObject.getInt("id"));

                                double advance = jsonObject.getDouble("advanced");
                                double delivered = jsonObject.getDouble("delivered");

                                customerAdvance.put(jsonObject.getString(StaticValue.Name), (advance - delivered));
                            }
                        }

                        getIncomeCategories();
                    }
                    else {
                        dismissProgressDialog();
                        Toast.makeText(getContext(), getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    dismissProgressDialog();
                    Log.e("ex", e.getMessage());
                }
            }
        };
    }


    private void getIncomeCategories() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.IncomeCategories + sharedPreferences.getSessionToken(), null, responseIncomeCategories(), GenericVollyError.errorListener(getContext(), pDialog));
        ApplicationController.getInstance().addToRequestQueue(request);
    }

    private Response.Listener<JSONObject> responseIncomeCategories() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    Log.e("res", response.toString());

                    if (response.getBoolean(StaticValue.Success)) {

                        categoryList.clear();

                        JSONObject data = response.getJSONObject(StaticValue.Data);

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

                        adapterName = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, stringArrayListName);
                        adapterName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        acName.setAdapter(adapterName);
                        acName.setThreshold(1);

                        customCategoryAdapter = new CustomCategoryAdapter(getContext(), categoryList, true, false, delAccount,false, update);
                        spClasses.setAdapter(customCategoryAdapter);

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

    private boolean checkValid() {

        if (stringDate.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.give_date), Toast.LENGTH_LONG).show();
            return false;
        } else if (stringName.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.choose_name), Toast.LENGTH_LONG).show();
            return false;
        } else if (stringUnit.isEmpty() || TextUtils.equals(stringUnit, "") || TextUtils.equals(stringUnit, "0") || TextUtils.equals(stringUnit, "0.0")) {
            Toast.makeText(getContext(), getString(R.string.give_unit), Toast.LENGTH_LONG).show();
            return false;
        } else if (stringPrice.isEmpty() || TextUtils.equals(stringPrice, "") || TextUtils.equals(stringPrice, "0") || TextUtils.equals(stringPrice, "0.0")) {
            Toast.makeText(getContext(), getString(R.string.give_price), Toast.LENGTH_LONG).show();
            return false;
        } else if (stringTotal.isEmpty() || TextUtils.equals(stringTotal, "")) {
            Toast.makeText(getContext(), getString(R.string.give_porishodh), Toast.LENGTH_LONG).show();
            return false;
        }else {
            return true;
        }

    }

    private void saveIncomeEntry() {

        showDialog();

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(StaticValue.Date, stringDate);
            jsonObject.put(StaticValue.Name, stringName);
            jsonObject.put(StaticValue.CustomerId, selectedCustomerId);

            if(!TextUtils.equals(stringClassName, "") && !TextUtils.isEmpty(stringClassName)) {

                jsonObject.put(StaticValue.ClassId, selectedClassId);
            }

            jsonObject.put(StaticValue.VehicleNo, stringVehicle);
            jsonObject.put(StaticValue.Unit, stringUnit);
            jsonObject.put(StaticValue.PerUnitPrice, "0");
            jsonObject.put(StaticValue.TotalPrice, 0);

            jsonObject.put(StaticValue.Cash, 0);
            jsonObject.put(StaticValue.Due , 0);
            jsonObject.put("remarks", stringRemarks);
            jsonObject.put(StaticValue.PaymentTypeId, 0);
            jsonObject.put(StaticValue.CreateUserId , sharedPreferences.getUserId());
            jsonObject.put(StaticValue.UpdateUserId , sharedPreferences.getUserId());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Url.SubmitIncome + sharedPreferences.getSessionToken(), jsonObject, responseIncomeEntry(), GenericVollyError.errorListener(getContext(), pDialog));
            Log.e("income", jsonObject.toString()+" "+Url.SubmitIncome + sharedPreferences.getSessionToken());

            ApplicationController.getInstance().addToRequestQueue(request);
        } catch (JSONException e) {
            dismissProgressDialog();
            e.printStackTrace();
        }

    }

    private Response.Listener<JSONObject> responseIncomeEntry() {

        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    if (response.getBoolean(StaticValue.Success)) {

                        JSONObject data = response.getJSONObject(StaticValue.Data);
                        JSONObject details = data.getJSONObject("dailyIncome");

                        int incomeId = details.getInt(StaticValue.Id);

                        save(incomeId);
                    } else {
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

    private void save(int incomeId) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(StaticValue.Date, stringDate);
            jsonObject.put(StaticValue.Name, stringName);

            if(!TextUtils.equals(stringClassName, "") && !TextUtils.isEmpty(stringClassName)) {

                jsonObject.put("income_class_id", selectedClassId);
            }

            jsonObject.put(StaticValue.Poriman, stringUnit);
            jsonObject.put(StaticValue.Dor, stringPrice);
            jsonObject.put(StaticValue.Cash, Double.parseDouble(stringTotal));
            jsonObject.put(StaticValue.Due, due);
            jsonObject.put(StaticValue.IncomeID, incomeId);
            jsonObject.put("remarks", stringRemarks);
            jsonObject.put(StaticValue.CreateUserId , sharedPreferences.getUserId());
            jsonObject.put(StaticValue.UpdateUserId , sharedPreferences.getUserId());
            jsonObject.put("type" , 1);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Url.SubmitAdvance +"?token="+ sharedPreferences.getSessionToken(), jsonObject, response(), GenericVollyError.errorListener(getContext(), pDialog));
            Log.e("advance", jsonObject.toString()+" "+Url.SubmitAdvance  +"&token="+ sharedPreferences.getSessionToken());

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

                        double remainingAdvance = (double) customerAdvance.get(stringName);
                        remainingAdvance = remainingAdvance - Double.parseDouble(stringTotal);
                        customerAdvance.put(stringName, remainingAdvance);

                        JSONObject data = response.getJSONObject(StaticValue.Data);
                        JSONObject value = data.getJSONObject("advancedModule");

                        Advance advance = new Advance();

                        advance.date = value.getString(StaticValue.Date);
                        advance.id = value.getString(StaticValue.Id);
                        advance.name = value.getString(StaticValue.Name);
                        advance.type = value.getString("type");
                        advance.unit = value.getString(StaticValue.Poriman);
                        advance.per_unit_price = value.getString(StaticValue.Dor);
                        advance.price = "0";
                        advance.given = value.getString(StaticValue.Cash);
                        advance.due = value.getString(StaticValue.Due);
                        advance.incomeID = value.getString(StaticValue.IncomeID);
                        advance.classID = value.getString("income_class_id");
                        advance.className = value.getString("class_name");
                        advance.remarks = value.getString("remarks");

                        clearFields();

                        dismissProgressDialog();
                        Toast.makeText(getContext(), getString(R.string.successfully_saved), Toast.LENGTH_LONG).show();
                    } else {
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

        clearingInput = true;

        tvRemainingAdvance.setText("");

        etVehicle.getText().clear();
        stringVehicle = "";

        etUnit.getText().clear();
        stringUnit = "";

        etPrice.getText().clear();
        stringPrice = "";

        total = 0;
        etPaid.getText().clear();

        due = 0;
        tvDue.setText("");

        acName.getText().clear();
        stringName = "";
        selectedCustomerId = 0;

        spClasses.setSelection(0);

        clearingInput = false;

        etRemarks.getText().clear();
        stringRemarks = "";

        hideKeyboard();
    }


    private void hideKeyboard() {

        View view = getActivity().getCurrentFocus();

        if(view != null) {

            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private void setTotal() {

        try {

            isCalculatingTotal = true;

            double advance = 0;
            double unit = 0;
            double price = 0;

            if (tvRemainingAdvance.getText().length() > 0) {
                advance = Double.parseDouble(tvRemainingAdvance.getText().toString());
            }
            if (etUnit.getText().length() > 0) {
                unit = Double.parseDouble(etUnit.getText().toString());
            }
            if (etPrice.getText().length() > 0) {
                price = Double.parseDouble(etPrice.getText().toString());
            }

            total = unit * price;
            etPaid.setText("" + numberFormatter.format(total));

            due = advance - total;
            tvDue.setText("" + numberFormatter.format(due));

            isCalculatingTotal = false;

        } catch (Exception e) {

        }
    }

    private void setPrice() {

        try {
            isCalculatingPrice = true;

            double advance = 0;
            double unit = 0;
            double price = 0;

            if(tvRemainingAdvance.getText().length() > 0) {
                advance = Double.parseDouble(tvRemainingAdvance.getText().toString());
            }
            if(etUnit.getText().toString().length() > 0) {

                unit = Double.parseDouble(etUnit.getText().toString());
            }
            if(etPaid.getText().toString().length() > 0) {

                total = Double.parseDouble(etPaid.getText().toString());
            }

            if(total != 0 && unit != 0) {

                price = total / unit;
                etPrice.setText("" + numberFormatter.format(price));
            }

            due = advance - total;
            tvDue.setText("" + numberFormatter.format(due));

            isCalculatingPrice = false;

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void setTotalAndPrice() {

        try {

            isCalculatingPrice = true;
            isCalculatingTotal = true;

            double advance = 0;
            double unit = 0;
            double price = 0;

            if (tvRemainingAdvance.getText().length() > 0) {

                advance = Double.parseDouble(tvRemainingAdvance.getText().toString());
            }

            if(etUnit.getText().toString().length() > 0) {

                unit = Double.parseDouble(etUnit.getText().toString());
            }

            if(etPrice.getText().toString().length() > 0) {

                price = Double.parseDouble(etPrice.getText().toString());
            }

            etUnit.setText("" + numberFormatter.format(unit));
            etPrice.setText("" + numberFormatter.format(price));

            total = unit * price;
            etPaid.setText("" + numberFormatter.format(total));

            due = advance - total;
            tvDue.setText("" + numberFormatter.format(due));

            isCalculatingPrice = false;
            isCalculatingTotal = false;

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void setTodaysDate() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
        Date todayDate = new Date();

        stringDate = dateFormat.format(todayDate);
        tvDate.setText(stringDate);
    }

    private void setDate() {

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

                stringDate = year + "-" + month + "-" + day;
                tvDate.setText(stringDate);

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

    //region show & hide progress dialog
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

    //endregion

    //region dismiss progress dialog
    @Override
    public void onDestroy() {
        dismissProgressDialog();

        customerID.clear();
        customerAdvance.clear();
        stringArrayListName.clear();
        categoryList.clear();

        super.onDestroy();
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
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
    //endregion

}
