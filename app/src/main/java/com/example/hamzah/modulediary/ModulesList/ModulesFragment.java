package com.example.hamzah.modulediary.ModulesList;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hamzah.modulediary.R;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Modules fragment consists of the tab layout and modulesSemesterFragment fragment
 * This is the parent fragment created to allow the tab layout to exist
 */
public class ModulesFragment extends Fragment implements View.OnClickListener {

    String uID;
    private ModulesPagerAdapter mPagerAdapter;
    @BindView(R.id.modulesTabs) TabLayout tabLayout;
    @BindView(R.id.vpModules) ViewPager vpModules;


    public ModulesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_modules, container, false);
        ButterKnife.bind(this, rootView);
        mPagerAdapter = new ModulesPagerAdapter(getChildFragmentManager());
        vpModules.setAdapter(mPagerAdapter);
        vpModules.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(vpModules);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        uID = auth.getCurrentUser().getUid();


        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            default:
                break;
        }
    }
}
