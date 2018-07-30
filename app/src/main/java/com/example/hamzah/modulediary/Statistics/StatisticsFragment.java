package com.example.hamzah.modulediary.Statistics;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hamzah.modulediary.R;

/**
 * handles view pager and tab layout for settings fragment
 */
public class StatisticsFragment extends Fragment {

    private StatisticsPagerAdapter mPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    public static StatisticsFragment newInstance(String param1, String param2) {
        StatisticsFragment fragment = new StatisticsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        viewPager = (ViewPager)rootView.findViewById(R.id.vpStats);
        mPagerAdapter = new StatisticsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout = (TabLayout)rootView.findViewById(R.id.statisticsTabs);
        tabLayout.setupWithViewPager(viewPager);


        // Inflate the layout for this fragment
        return rootView;
    }
}
