package com.example.hamzah.modulediary.Deadlines;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hamzah.modulediary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.DEADLINES;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.USERS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.year_of_study;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeadlinesFragment extends Fragment {

    @BindView(R.id.lvDeadlines) ListView lvDeadlines;
    @BindView(R.id.tvDeadlinesNoDeadlines) TextView tvNoDeadlines;
    private ArrayList<DeadlinesBean> deadlinesArray;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private String uID;
    private DeadlinesAdapter adapter;

    public DeadlinesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        uID = auth.getCurrentUser().getUid();

        View rootView = inflater.inflate(R.layout.fragment_deadlines, container, false);
        ButterKnife.bind(this, rootView);

        setUpList();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_deadline, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //handles button click to add new deadline
        switch (id){
            case R.id.menu_add_deadline:
                startActivity(new Intent(getActivity(), AddDeadlineActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //sets up list view for deadlines
    private void setUpList() {
        mDatabase.child(USERS)
                .child(uID)
                .child(DEADLINES)
                .child(year_of_study)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount() != 0) {
                            lvDeadlines.setVisibility(View.VISIBLE);
                            tvNoDeadlines.setVisibility(View.GONE);
                            deadlinesArray = new ArrayList<>();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                DeadlinesBean deadline = postSnapshot.getValue(DeadlinesBean.class);
                                deadlinesArray.add(deadline);
                            }
                            adapter = new DeadlinesAdapter(getActivity(), deadlinesArray);
                            lvDeadlines.setAdapter(adapter);
                        } else{
                            lvDeadlines.setVisibility(View.GONE);
                            tvNoDeadlines.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}
