package com.example.hamzah.modulediary.Statistics;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class StatisticsPagerAdapter extends FragmentPagerAdapter {

    public StatisticsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return AutumnFragment.newInstance("S1");
            case 1:
                return AutumnFragment.newInstance("S2");
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Autumn";
            case 1:
                return "Spring";
//            case 2:
//                return "Overall";
            default:
                return null;
        }
//        return super.getPageTitle(position);
    }
}
