package com.b2gsoft.mrb.Activity.expense;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.b2gsoft.mrb.Activity.HomeActivity;
import com.b2gsoft.mrb.Fragment.Expense.ExpenseMillOneFragment;
import com.b2gsoft.mrb.R;

import java.util.ArrayList;
import java.util.List;

public class MillExpenseActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mill_expense);

        getSupportActionBar().hide();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setUpTab();

    } private void setUpTab() {

        tabLayout.getTabAt(0).setText("মিল ১");
        tabLayout.getTabAt(1).setText("মিল ২");
        tabLayout.getTabAt(2).setText("মিল ৩");
        tabLayout.getTabAt(3).setText("মিল ৪");
        tabLayout.getTabAt(4).setText("মিল ৫");

    }

    private void setUpViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new ExpenseMillOneFragment(1));
        adapter.addFragment(new ExpenseMillOneFragment(2));
        adapter.addFragment(new ExpenseMillOneFragment(3));
        adapter.addFragment(new ExpenseMillOneFragment(4));
        adapter.addFragment(new ExpenseMillOneFragment(5));

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
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
