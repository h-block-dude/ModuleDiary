package com.example.hamzah.modulediary.SetUp.ModulePicker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.hamzah.modulediary.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.AUTUMN;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.COURSE_NAME_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.MODULES;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.PREF_PROFILE;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.UNIVERSITY_NAME_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.YEAR_OF_STUDY_KEY;

/**
 * shows list of semester 1 modules
 */
public class Semester1Fragment extends Fragment {

    private ListView lvModules;
    private ArrayList<ModulesSetUpBean> modules;
    private static ModulesSetUpAdapter adapter;
    private DatabaseReference mDatabase;

    public Semester1Fragment() {

    }

    public static Semester1Fragment newInstance(int sectionNumber) {
        Semester1Fragment fragment = new Semester1Fragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_semester_1, container, false);
        lvModules = (ListView) rootView.findViewById(R.id.lvSem1Modules);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        getModules();

        return rootView;
    }

    private void getModules() {
        //shared preferences accessed to get database locations
        SharedPreferences uniPrefs = getActivity().getSharedPreferences(PREF_PROFILE, Context.MODE_PRIVATE);
        String uniName = uniPrefs.getString(UNIVERSITY_NAME_KEY, "");
        String courseName = uniPrefs.getString(COURSE_NAME_KEY, "");
        int yearOfStudy = uniPrefs.getInt(YEAR_OF_STUDY_KEY, 1);
        mDatabase.child(MODULES)
                .child(uniName)
                .child(courseName)
                .child("Year " + Integer.toString(yearOfStudy))
                .child(AUTUMN)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                modules = new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    ModulesSetUpBean module = postSnapshot.getValue(ModulesSetUpBean.class);
                    module.setId(postSnapshot.getKey());
                    modules.add(module);
                }
                adapter = new ModulesSetUpAdapter(getActivity(), modules);
                lvModules.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
