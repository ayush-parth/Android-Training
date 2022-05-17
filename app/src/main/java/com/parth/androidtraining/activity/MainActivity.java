package com.parth.androidtraining.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.parth.androidtraining.R;
import com.parth.androidtraining.fragments.MatchListFragment;
import com.parth.androidtraining.fragments.RecyclerViewMatchListFragment;
import com.parth.androidtraining.model.enums.MatchType;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TabLayout mainTabLayout;
    private ViewPager viewPager;
    //private MatchListFragment upcomingMatchFragment;
    //private MatchListFragment finishedMatchFragment;
    private RecyclerViewMatchListFragment upcomingMatchFragment;
    private RecyclerViewMatchListFragment finishedMatchFragment;
    private ArrayList<Fragment> fragmentList = new ArrayList<>();

    private static final String[] tabTitles = new String[]{"Upcoming", "Finished"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainTabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        setupTabsInTabLayout();
        setupViewPager(viewPager);

        mainTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setupTabsInTabLayout() {
        TabLayout.Tab upcomingTab = mainTabLayout.newTab();
        upcomingTab.setText("Upcoming");

        TabLayout.Tab finishedTab = mainTabLayout.newTab();
        finishedTab.setText("Finished");

        mainTabLayout.addTab(upcomingTab);
        mainTabLayout.addTab(finishedTab);
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            fragmentList.add(fragment);
            notifyDataSetChanged();
        }
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //upcomingMatchFragment = new MatchListFragment("https://mocki.io/v1/30786c0a-390e-41d5-9ad8-549ed26cba64",MatchType.UPCOMING);
        //finishedMatchFragment = new MatchListFragment("https://mocki.io/v1/2389d44c-81aa-4e04-bd2e-b8c7e17572c0",MatchType.FINISHED);
        upcomingMatchFragment = new RecyclerViewMatchListFragment(MatchType.UPCOMING,"https://mocki.io/v1/30786c0a-390e-41d5-9ad8-549ed26cba64");
        finishedMatchFragment = new RecyclerViewMatchListFragment(MatchType.FINISHED,
                "https://mocki.io/v1/2389d44c-81aa-4e04-bd2e-b8c7e17572c0");
        viewPagerAdapter.addFragment(upcomingMatchFragment);
        viewPagerAdapter.addFragment(finishedMatchFragment);
        viewPager.setAdapter(viewPagerAdapter);
    }
}