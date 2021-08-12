package com.b2gsoft.mrb.Activity.input;

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
import com.b2gsoft.mrb.Fragment.weekly.WeekFiringFragment;
import com.b2gsoft.mrb.Fragment.weekly.WeekLoadFragment;
import com.b2gsoft.mrb.Fragment.weekly.WeekMatiMixingFragment;
import com.b2gsoft.mrb.Fragment.weekly.WeekMillFragment;
import com.b2gsoft.mrb.Fragment.weekly.WeekOthersFragment;
import com.b2gsoft.mrb.Fragment.weekly.WeekSalaryFragment;
import com.b2gsoft.mrb.Fragment.weekly.WeekUnLoadFragment;
import com.b2gsoft.mrb.R;


import java.util.ArrayList;
import java.util.List;

public class WeeklyBillActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_bill);

        getSupportActionBar().hide();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setUpTab();

    }

    private void setUpTab() {
        tabLayout.getTabAt(0).setText("মিল");
        tabLayout.getTabAt(1).setText("লোড");
        tabLayout.getTabAt(2).setText("আনলোড");
        tabLayout.getTabAt(3).setText("ফায়ারিং");
        tabLayout.getTabAt(4).setText("মাটি মিক্সিং");
        tabLayout.getTabAt(5).setText("বেতন");
        tabLayout.getTabAt(6).setText("বিবিধ");

    }

    private void setUpViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new WeekMillFragment());
        adapter.addFragment(new WeekLoadFragment());
        adapter.addFragment(new WeekUnLoadFragment());
        adapter.addFragment(new WeekFiringFragment());
        adapter.addFragment(new WeekMatiMixingFragment());
        adapter.addFragment(new WeekSalaryFragment());
        adapter.addFragment(new WeekOthersFragment());

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
