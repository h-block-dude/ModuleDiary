package com.example.hamzah.modulediary.SetUp.ModulePicker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return Semester1Fragment.newInstance(30);
            case 1:
                return Semester2Fragment.newInstance();
            default:
                return YearLongFragment.newInstance();
        }
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
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
                return "Autumn";
            case 1:
                return "Spring";
            case 2:
                return "Year Long";
        }
        return null;
    }
}
