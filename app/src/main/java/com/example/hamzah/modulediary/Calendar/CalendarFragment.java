package com.example.hamzah.modulediary.Calendar;


import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hamzah.modulediary.Helper.AppUtils;
import com.example.hamzah.modulediary.Helper.HelperFirebase;
import com.example.hamzah.modulediary.MoodBean;
import com.example.hamzah.modulediary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    private CaldroidCustomFragment caldroidFragment;
    @BindView(R.id.rlBottomSheetCalendar) View mLayoutBottomSheet;
    @BindView(R.id.tvMoodBottomSheetCalendar) TextView tvMood;
    @BindView(R.id.tvTagsBottomSheetCalendar) TextView tvTags;
    @BindView(R.id.tvCommentBottomSheetCalendar) TextView tvComment;
    @BindView(R.id.viewMoodBottomSheetUnderlineCalendar) View viewMoodUnderline;
    private BottomSheetBehavior mBottomSheetBehavior;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private String uID;

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        auth = FirebaseAuth.getInstance();
        uID = auth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child(HelperFirebase.USERS)
                .child(uID)
                .child(HelperFirebase.MOODS)
                .child(HelperFirebase.year_of_study)
                .child(HelperFirebase.semester);

        //sets up the views
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        ButterKnife.bind(this, rootView);

        mBottomSheetBehavior = BottomSheetBehavior.from(mLayoutBottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        setUpCalendar();
        setUpCalendarListener();

        return rootView;
    }

    private void setUpCalendar() {
        caldroidFragment = new CaldroidCustomFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, true);
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getChildFragmentManager().beginTransaction();
        t.replace(R.id.llCalendar, caldroidFragment);
        t.commit();

    }

    private void setUpCalendarListener() {
        final CaldroidListener listener = new CaldroidListener() {
            //handles when a user clicks on a date
            @Override
            public void onSelectDate(Date date, View view) {
                String sDate = AppUtils.dateToFullString(date, "dd-MM-yyyy");

                //queries that date inside of the database
                Query findDate = mDatabase.orderByKey().equalTo(sDate);

                findDate.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null) {
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                MoodBean mood = postSnapshot.getValue(MoodBean.class);
                                String shortMood = "";
                                //sets the text for the bottom sheet depending on the mood retrieved
                                switch(mood.getMood()){
                                    case HelperFirebase.POSITIVE:
                                        shortMood = "POSITIVE";
                                        tvMood.setTextColor(getResources().getColor(R.color.positiveMoodToolbar));
                                        viewMoodUnderline.setBackgroundColor(getResources().getColor(R.color.positiveMoodToolbar));
                                        break;
                                    case HelperFirebase.NEUTRAL:
                                        shortMood = "NEUTRAL";
                                        tvMood.setTextColor(getResources().getColor(R.color.neutralMoodToolbar));
                                        viewMoodUnderline.setBackgroundColor(getResources().getColor(R.color.neutralMoodToolbar));
                                        break;
                                    case HelperFirebase.NEGATIVE:
                                        shortMood = "NEGATIVE";
                                        tvMood.setTextColor(getResources().getColor(R.color.negativeMoodToolbar));
                                        viewMoodUnderline.setBackgroundColor(getResources().getColor(R.color.negativeMoodToolbar));
                                        break;
                                    default:
                                        break;
                                }
                                setTags(mood);
                                tvMood.setText(shortMood);
                                if (mood.getComment().equalsIgnoreCase("")){
                                    tvComment.setText("No comment");
                                } else{
                                    tvComment.setText(mood.getComment());
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChangeMonth(int month, int year) {
                super.onChangeMonth(month, year);
            }
        };
        caldroidFragment.setCaldroidListener(listener);
    }

    //helper method to set the module tags
    private void setTags(MoodBean mood) {
        String tags = "";
        //handles displaying the no tag option
        if (!mood.getTags().get(0).equalsIgnoreCase("none")) {
            for (int i = 0; i < mood.getTags().size(); i++ ) {
                if(i != mood.getTags().size() -1) {
                    tags = tags + mood.getTags().get(i) + ", ";
                } else {
                    tags = tags + mood.getTags().get(i);
                }
            }
            tvTags.setText(tags);
        } else {
            tvTags.setText("No tags selected");
        }
    }

}
