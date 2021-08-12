package com.b2gsoft.mrb.Activity.input;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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
import com.b2gsoft.mrb.Activity.HomeActivity;
import com.b2gsoft.mrb.Adapter.RoundAdapter;
import com.b2gsoft.mrb.Model.Round;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Utils.ApplicationController;
import com.b2gsoft.mrb.Utils.ConnectionDetector;
import com.b2gsoft.mrb.Utils.GenericVollyError;
import com.b2gsoft.mrb.Utils.SharedPreferences;
import com.b2gsoft.mrb.Utils.StaticValue;
import com.b2gsoft.mrb.Interface.UpdateRound;
import com.b2gsoft.mrb.Utils.Url;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.b2gsoft.mrb.Utils.ApplicationController.getContext;

public class RoundActivity extends AppCompatActivity implements UpdateRound {

    private EditText etRoundName;
    private TextView tvStartDate, tvEndDate, tvTotalDays;
    private Button btnSave;
    private LinearLayout layout1;
    private ListView lvRound;

    private RoundAdapter roundAdapter;
    private UpdateRound updateRound;

    private ProgressDialog pDialog;
    private SharedPreferences sharedPreferences;
    private ConnectionDetector connectionDetector;

    private List<Round> roundList = new ArrayList<>();

    private String stringName = "", startDate = "", endDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round);

        updateRound = this;

        pDialog = new ProgressDialog(RoundActivity.this);
        connectionDetector = new ConnectionDetector(RoundActivity.this);
        sharedPreferences = new SharedPreferences(RoundActivity.this);

        etRoundName = (EditText) findViewById(R.id.et_round_name);

        tvStartDate = (TextView) findViewById(R.id.tv_start_date);
        tvEndDate = (TextView) findViewById(R.id.tv_end_date);
        tvTotalDays = (TextView) findViewById(R.id.tv_total_days);

        btnSave = (Button) findViewById(R.id.btn_save);

        layout1 = (LinearLayout) findViewById(R.id.ll_1);

        lvRound = (ListView) findViewById(R.id.lv_round);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getRoundList();

        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(true);
            }
        });

        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(false);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();

                stringName = etRoundName.getText().toString();

                if(checkValid()) {
                    if(connectionDetector.isConnectingToInternet()) {
                        save();
                    }
                }
            }
        });
    }


    private void getRoundList() {

        showDialog();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url.Round+ "?token=" + sharedPreferences.getSessionToken(), null, responseRoundList(), GenericVollyError.errorListener(getContext(), pDialog));
        ApplicationController.getInstance().addToRequestQueue(request);
    }


    private Response.Listener<JSONObject> responseRoundList() {

        return new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {

                try {

                    if (response.getBoolean(StaticValue.Success)) {

                        roundList.clear();

                        JSONObject data = response.getJSONObject(StaticValue.Data);
                        JSONArray rounds = data.getJSONArray("rounds");

                        if(rounds.length() > 0) {

                            for(int i=0; i<rounds.length(); i++) {

                                JSONObject value = rounds.getJSONObject(i);

                                Round round = new Round();

                                round.setId(value.getInt(StaticValue.Id));
                                round.setName(value.getString(StaticValue.Name));
                                round.setStartDate(value.getString("start_date"));
                                round.setEndDate(value.getString("end_date"));

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                                try {

                                    Date Date1 = sdf.parse(round.getStartDate());
                                    Date Date2 = sdf.parse(round.getEndDate());

                                    long diff = Date2.getTime() - Date1.getTime();

                                    round.setTotalDays(String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)));

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                roundList.add(round);
                            }
                        }

                        roundAdapter = new RoundAdapter(RoundActivity.this, roundList, updateRound);
                        roundAdapter.notifyDataSetChanged();
                        lvRound.setAdapter(roundAdapter);

                        if(roundList.size() > 0) {

                            layout1.setVisibility(View.VISIBLE);
                            lvRound.setVisibility(View.VISIBLE);
                        }

                        dismissProgressDialog();
                        return;
                    }
                    else {
                        dismissProgressDialog();
                        Toast.makeText(getContext(), getString(R.string.failed_to_get_data), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    dismissProgressDialog();
                    Log.e("income", e.getMessage());
                }
            }
        };
    }


    private boolean checkValid() {

        if(TextUtils.equals(stringName, "")) {
            Toast.makeText(this, getString(R.string.give_round_name), Toast.LENGTH_SHORT).show();
            return false;
        } else if(TextUtils.equals(startDate, "")) {
            Toast.makeText(this, getString(R.string.give_start_date), Toast.LENGTH_SHORT).show();
            return false;
        } /*else if(TextUtils.equals(endDate, "")) {
            Toast.makeText(this, getString(R.string.end_date), Toast.LENGTH_SHORT).show();
            return false;
        }*/
        else {
            return true;
        }
    }


    private void save() {

        showDialog();

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("name", stringName);
            jsonObject.put("start_date", startDate);
            //jsonObject.put("end_date", endDate);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Url.Round+ "?token=" + sharedPreferences.getSessionToken(), jsonObject, responseAdd(), GenericVollyError.errorListener(getContext(), pDialog));
            ApplicationController.getInstance().addToRequestQueue(request);

        } catch (JSONException e) {
            Log.e("ex", e.getMessage());
        }
    }


    private Response.Listener<JSONObject> responseAdd() {

        return new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {

                try {

                    if (response.getBoolean(StaticValue.Success)) {

                        JSONObject data = response.getJSONObject(StaticValue.Data);
                        JSONObject details = data.getJSONObject("round");

                        Round round = new Round();

                        round.setId(details.getInt(StaticValue.Id));
                        round.setName(details.getString(StaticValue.Name));
                        round.setStartDate(details.getString("start_date"));
                        //round.setEndDate(details.getString("end_date"));
                        round.setTotalDays("");

                        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

                        if(!TextUtils.isEmpty(round.getEndDate())) {

                            try {

                                Date Date1 = sdf.parse(round.getStartDate());
                                Date Date2 = sdf.parse(round.getEndDate());

                                long diff = Date2.getTime() - Date1.getTime();

                                round.setTotalDays(String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)));

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        else {


                        }*/

                        roundList.add(round);

                        if(roundAdapter == null) {

                            roundAdapter = new RoundAdapter(RoundActivity.this, roundList, updateRound);
                            roundAdapter.notifyDataSetChanged();
                            lvRound.setAdapter(roundAdapter);
                        }
                        else {

                            roundAdapter.notifyDataSetChanged();
                        }

                        if(roundList.size() > 0) {

                            layout1.setVisibility(View.VISIBLE);
                            lvRound.setVisibility(View.VISIBLE);
                        }

                        dismissProgressDialog();
                        Toast.makeText(getContext(), getString(R.string.successfully_saved), Toast.LENGTH_LONG).show();

                        clearFields();

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

        etRoundName.getText().clear();
        tvStartDate.setText("");

        hideKeyboard();
    }


    private void hideKeyboard() {

        View view = this.getCurrentFocus();

        if(view != null) {

            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private void setDate(final boolean isStartDate) {

        LayoutInflater inflater = LayoutInflater.from(RoundActivity.this);
        View view = inflater.inflate(R.layout.date_pick, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(RoundActivity.this);
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

                if(isStartDate) {

                    startDate = year + "-" + month + "-" + day;
                    tvStartDate.setText(startDate);
                }
                else {

                    endDate = year + "-" + month + "-" + day;
                    tvEndDate.setText(endDate);
                }

                if(!TextUtils.equals(startDate, "") && !TextUtils.equals(endDate, "")) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

                    try {

                        Date Date1 = sdf.parse(startDate);
                        Date Date2 = sdf.parse(endDate);

                        long diff = Date2.getTime() - Date1.getTime();

                        tvTotalDays.setText("" + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
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
        }
    }


    public void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }


    public void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra(StaticValue.Page, StaticValue.InputPage);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void updateRoundData(final Round round) {

        LayoutInflater inflater = LayoutInflater.from(RoundActivity.this);
        View view = inflater.inflate(R.layout.update_round_layout, null);

        final TextView tvRoundName = (TextView) view.findViewById(R.id.tv_round_name);
        final TextView tvStartDate = (TextView) view.findViewById(R.id.tv_start_date);
        final TextView tvEndDate = (TextView) view.findViewById(R.id.tv_end_date);
        final TextView tvTotalDays = (TextView) view.findViewById(R.id.tv_total_days);

        Button btnUpdate = (Button)view.findViewById(R.id.btn_update);

        tvRoundName.setText(round.getName());
        tvStartDate.setText(round.getStartDate());

        tvStartDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //SetDate(tvStartDate, tvEndDate, tvTotalDays, true);
            }
        });

        tvEndDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SetDate(tvStartDate, tvEndDate, tvTotalDays, false);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(RoundActivity.this);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        dialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                hideKeyboard();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
                dateFormat.setLenient(false);

                try {

                    dateFormat.parse(tvEndDate.getText().toString());

                    dialog.cancel();
                    showDialog();

                    JSONObject jsonObject = new JSONObject();

                    try {

                        //jsonObject.put("start_date", tvStartDate.getText().toString());
                        jsonObject.put("end_date", tvEndDate.getText().toString());

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, Url.Round + "/" + round.getId() + "?token=" + sharedPreferences.getSessionToken(), jsonObject, responseUpdate(), GenericVollyError.errorListener(RoundActivity.this, pDialog));
                        Log.e("ex", jsonObject + " " + Url.Round +"/"+ round.getId() + "?token=" + sharedPreferences.getSessionToken());
                        ApplicationController.getInstance().addToRequestQueue(request);

                    } catch (JSONException e) {
                        Log.e("ex", e.getMessage());
                    }

                } catch (Exception e) {

                    Toast.makeText(RoundActivity.this, getString(R.string.give_end_date), Toast.LENGTH_SHORT).show();
                    //e.printStackTrace();
                }
            }
        });
    }


    private Response.Listener<JSONObject> responseUpdate() {

        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                dismissProgressDialog();

                try {

                    if (response.getBoolean(StaticValue.Success)) {
                        Toast.makeText(getContext(), getString(R.string.successfully_updated), Toast.LENGTH_LONG).show();
                        getRoundList();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.failed_to_update), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("income", e.getMessage());
                }
            }
        };
    }


    private void SetDate(final TextView startDate, final TextView endDate, final TextView tvTotalDays, final boolean isStartDate) {

        LayoutInflater inflater = LayoutInflater.from(RoundActivity.this);
        View view = inflater.inflate(R.layout.date_pick, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(RoundActivity.this);
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

                if(isStartDate) {
                    startDate.setText(year + "-" + month + "-" + day);
                }
                else {
                    endDate.setText(year + "-" + month + "-" + day);
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

                try {

                    Date Date1 = sdf.parse(startDate.getText().toString());
                    Date Date2 = sdf.parse(endDate.getText().toString());

                    long diff = Date2.getTime() - Date1.getTime();

                    tvTotalDays.setText("" + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

                } catch (ParseException e) {
                    e.printStackTrace();
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
}
