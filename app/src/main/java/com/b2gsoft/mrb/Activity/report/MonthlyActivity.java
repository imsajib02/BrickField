package com.b2gsoft.mrb.Activity.report;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.b2gsoft.mrb.Activity.HomeActivity;
import com.b2gsoft.mrb.Fragment.monthly.ExpenseFragment;
import com.b2gsoft.mrb.Fragment.monthly.IncomeFragment;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Utils.StaticValue;

import java.util.ArrayList;
import java.util.List;

public class MonthlyActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly);

        getSupportActionBar().hide();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setUpTab();
    }

    private void setUpTab() {
        tabLayout.getTabAt(0).setText(getString(R.string.income_text));
        tabLayout.getTabAt(1).setText(getString(R.string.expense_text));
    }

    private void setUpViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new IncomeFragment());
        adapter.addFragment(new ExpenseFragment());

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        private void addFragment(Fragment fragment) {

            fragments.add(fragment);
        }
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
}
