package com.example.hamzah.modulediary.ModulesList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.AUTUMN;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.FULL_YEAR;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.SPRING;


public class ModulesPagerAdapter extends FragmentPagerAdapter {

    public ModulesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return ModulesSemesterFragment.newInstance(AUTUMN);
            case 1:
                return ModulesSemesterFragment.newInstance(SPRING);
            case 2:
                return ModulesSemesterFragment.newInstance(FULL_YEAR);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
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
                return "Year long";
            default:
                return null;
        }
//        return super.getPageTitle(position);
    }
}
