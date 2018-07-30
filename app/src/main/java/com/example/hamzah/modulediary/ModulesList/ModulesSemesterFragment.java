package com.example.hamzah.modulediary.ModulesList;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.hamzah.modulediary.Helper.HelperFirebase;
import com.example.hamzah.modulediary.R;
import com.example.hamzah.modulediary.SetUp.ModulePicker.ModulesSetUpBean;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.MODULES;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.USERS;

/**
 * Depending on the semester value passed to this fragment,
 * a different semester/ full year fragment is formed
 */
public class ModulesSemesterFragment extends Fragment {

    private static final String ARG_PARAM = "param";
    private String semester = "";
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private String uID;
    private ArrayList<ModulesSetUpBean> modules;
    private static ModulesListAdapter adapter;
    @BindView(R.id.lvModulesList) ListView lvModules;

    public ModulesSemesterFragment() {
        // Required empty public constructor
    }

    public static ModulesSemesterFragment newInstance(String param) {
        ModulesSemesterFragment fragment = new ModulesSemesterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            semester = getArguments().getString(ARG_PARAM);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        uID= auth.getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_modules_semester, container, false);
        ButterKnife.bind(this, rootView);

        getModules();


        return rootView;
    }

    private void getModules() {
        mDatabase.child(USERS)
                .child(uID)
                .child(MODULES)
                .child(HelperFirebase.year_of_study)
                .child(semester)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        modules = new ArrayList<>();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            ModulesSetUpBean module = postSnapshot.getValue(ModulesSetUpBean.class);
                            module.setId(postSnapshot.getKey());
                            modules.add(module);
                        }
                        adapter = new ModulesListAdapter(getActivity(), modules);
                        lvModules.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}
