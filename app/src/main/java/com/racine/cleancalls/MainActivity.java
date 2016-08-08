package com.racine.cleancalls;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements ListFragment.OnTabSelectedListener {
    private TextView tab_1;
    private TextView tab_2;
    private TextView tab_3;

    private ViewPageIndicator indicator;
    private ViewPager container;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Fragment fragment;

    protected FragmentManager fragmentManager;
    protected FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        tab_1 = (TextView)findViewById(R.id.tab_1);
        tab_2 = (TextView)findViewById(R.id.tab_2);
        tab_3 = (TextView)findViewById(R.id.tab_3);

        indicator = (ViewPageIndicator) findViewById(R.id.indicator);
        container = (ViewPager) findViewById(R.id.container);

        //
        mSectionsPagerAdapter = new SectionsPagerAdapter(fragmentManager);

        // Set up the ViewPager with the sections adapter.
        container.setOffscreenPageLimit(0);
        container.setAdapter(mSectionsPagerAdapter);

        indicator.setViewPager(container);
        indicator.setSelectedColor(getResources().getColor(R.color.white));

        tab_1.setOnClickListener(onClicker);
        tab_2.setOnClickListener(onClicker);
        tab_3.setOnClickListener(onClicker);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if (position == 0) {
//                    fragment = new ListFragment();
//                } else if (position == 1) {
//                    fragment = new ListFragment();
//                } else {
//                    fragment = new ListFragment();
//                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private View.OnClickListener onClicker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            transaction = fragmentManager.beginTransaction();
            switch (v.getId()) {
                case R.id.tab_1:
                    indicator.setCurrentItem(0);
                    break;
                case R.id.tab_2:
                    indicator.setCurrentItem(1);
                    break;
                case R.id.tab_3:
                    indicator.setCurrentItem(2);
                    break;
                default:
                    break;
            }
            transaction.commit();
        }
    };

    @Override
    public void onArticleSelected(Object obj, int what) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                fragment = new ListFragment();
                return fragment;
            } else if (position == 1) {
                fragment = new ListFragment();
                return fragment;
            } else {
                fragment = new ListFragment();
                return fragment;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
