package com.b2gsoft.mrb.Activity.input;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.b2gsoft.mrb.Activity.HomeActivity;
import com.b2gsoft.mrb.Fragment.Advance.AdvancedAddFragment;
import com.b2gsoft.mrb.Fragment.Advance.AdvancedShowFragment;
import com.b2gsoft.mrb.Fragment.Advance.AdvancePayFragment;
import com.b2gsoft.mrb.R;
import com.b2gsoft.mrb.Utils.StaticValue;

import java.util.ArrayList;
import java.util.List;

public class AdvanceActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance);

        getSupportActionBar().hide();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setUpTab();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                setUpTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void setUpTab() {

        tabLayout.getTabAt(0).setText(getString(R.string.entry));
        tabLayout.getTabAt(1).setText(getString(R.string.pay));
        tabLayout.getTabAt(2).setText(getString(R.string.show));
    }


    private void setUpViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new AdvancedAddFragment());
        adapter.addFragment(new AdvancePayFragment());
        adapter.addFragment(new AdvancedShowFragment());

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        @Override
        public int getItemPosition(@NonNull Object object) {

            return POSITION_NONE;
        }

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

    /*public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;
    }*/

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {

            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra(StaticValue.Page, StaticValue.InputPage);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}