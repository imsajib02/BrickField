package com.b2gsoft.mrb.Activity.expense;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.b2gsoft.mrb.Fragment.Expense.ExpenseOilFragment;
import com.b2gsoft.mrb.R;

import java.util.ArrayList;
import java.util.List;

public class OilExpenseActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_oil_expense);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setUpTab();

    } private void setUpTab() {

        tabLayout.getTabAt(0).setText("ডিজেল");
        tabLayout.getTabAt(1).setText("মবিল");
        tabLayout.getTabAt(2).setText("অন্যান্য");

    }

    private void setUpViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new ExpenseOilFragment(1));
        adapter.addFragment(new ExpenseOilFragment(2));
        adapter.addFragment(new ExpenseOilFragment(3));

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
            startActivity(new Intent(getApplicationContext(), ExpenseSummaryActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
