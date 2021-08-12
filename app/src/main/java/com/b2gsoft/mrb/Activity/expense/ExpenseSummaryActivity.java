package com.b2gsoft.mrb.Activity.expense;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.b2gsoft.mrb.Activity.HomeActivity;
import com.b2gsoft.mrb.Adapter.GridAdapter;
import com.b2gsoft.mrb.Model.Category;
import com.b2gsoft.mrb.Model.SubCategory;
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

import java.util.ArrayList;
import java.util.List;

public class ExpenseSummaryActivity extends AppCompatActivity {

    private GridView gridView;
    private ProgressDialog pDialog;
    private SharedPreferences sharedPreferences;
    private ConnectionDetector connectionDetector;

    private List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_summary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = new SharedPreferences(getApplicationContext());
        connectionDetector = new ConnectionDetector(getApplicationContext());
        pDialog = new ProgressDialog(ExpenseSummaryActivity.this);

        gridView = (GridView) findViewById(R.id.grid);

        getExpenseCategories();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Category category = categoryList.get(position);

                Intent intent = new Intent(getApplicationContext(), ExpenseWiseHistoryActivity.class);
                intent.putExtra("expense", category);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getExpenseCategories() {

        showDialog();

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("user", "");

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.ExpenseCategories + sharedPreferences.getSessionToken(), jsonObject, responseExpenseCategories(), GenericVollyError.errorListener(getApplicationContext(), pDialog));
            Log.e("ex", jsonObject + " " + Url.CreateUser + sharedPreferences.getSessionToken());
            ApplicationController.getInstance().addToRequestQueue(request);
        } catch (JSONException e) {
            Log.e("ex", e.getMessage());
        }
    }

    private Response.Listener<JSONObject> responseExpenseCategories() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismissProgressDialog();
                try {
                    Log.e("res", response.toString());

                    if (response.getBoolean(StaticValue.Success)) {

                        JSONObject data = response.getJSONObject(StaticValue.Data);

                        JSONArray categories = data.getJSONArray("expenseSector");

                        if(categories.length() > 0) {

                            for(int i=0; i<categories.length(); i++) {

                                JSONObject value = categories.getJSONObject(i);

                                Category category = new Category();

                                category.setId(value.getInt(StaticValue.Id));
                                category.setName(value.getString(StaticValue.Name));

                                JSONArray subCategories = value.getJSONArray("subSectors");

                                List<SubCategory> subCategoryList = new ArrayList<>();

                                if(subCategories.length() > 0) {

                                    for(int j=0; j<subCategories.length(); j++) {

                                        JSONObject subValue = subCategories.getJSONObject(j);

                                        SubCategory subCategory = new SubCategory();

                                        subCategory.setId(subValue.getInt(StaticValue.Id));
                                        subCategory.setName(subValue.getString(StaticValue.Name));

                                        subCategoryList.add(subCategory);
                                    }
                                }

                                category.setSubCategoryList(subCategoryList);

                                categoryList.add(category);
                            }

                            GridAdapter gridAdapter = new GridAdapter(getApplicationContext(), categoryList);
                            gridView.setAdapter(gridAdapter);
                        }

                        return;
                    }
                    else {
                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_get_data), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e("income", e.getMessage());
                }
            }
        };
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra(StaticValue.Page, StaticValue.ReportPage);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra(StaticValue.Page, StaticValue.ReportPage);
        startActivityForResult(intent, 0);
        finish();
        return true;
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

    //endregion

    //region dismiss progress dialog
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
