package com.b2gsoft.mrb.Activity.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;

import com.b2gsoft.mrb.Activity.HomeActivity;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Utils.SharedPreferences;
import com.b2gsoft.mrb.Utils.StaticValue;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName, tvPhone, tvType;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = new SharedPreferences(getApplicationContext());

        tvName = (TextView) findViewById(R.id.tv_name);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvType = (TextView) findViewById(R.id.tv_type);

        tvName.setText(sharedPreferences.getUserName());
        tvPhone.setText(sharedPreferences.getUserPhone());
        tvType.setText(sharedPreferences.getRole());
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra(StaticValue.Page, StaticValue.DashboardPage);
        startActivityForResult(intent, 0);
        finish();
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra(StaticValue.Page, StaticValue.DashboardPage);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
