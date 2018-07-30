package com.example.hamzah.modulediary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hamzah.modulediary.Deadlines.DeadlinesBean;
import com.example.hamzah.modulediary.Helper.AppUtils;
import com.example.hamzah.modulediary.Helper.HelperFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.AVERAGE_MOOD;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.CURRENT;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.DEADLINES;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.GLOBAL_STATS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.MAX;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.MOODS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.NEGATIVE;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.NEUTRAL;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.NO_DATA;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.NUMBER_OF_MOODS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.POSITIVE;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.STREAK;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.TOTAL;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.USERS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.USER_STATS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.course;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.semester;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.university;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.year_of_study;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private ImageButton btnAdd;
    private TextView tvCurrentStreak, tvMaxStreak;
    private DatabaseReference mDatabase;
    private String uID;
    @BindView(R.id.tvHomeMood) TextView tvMood;
    @BindView(R.id.tvHomeMoodTitle) TextView tvMoodTitle;
    @BindView(R.id.ibHomeAddMood) ImageButton ibAddMood;
    @BindView(R.id.llHomeMood) LinearLayout llMood;
    @BindView(R.id.btnHomeMyStory) Button btnMyStory;
    @BindView(R.id.btnHomeStats) Button btnStats;
    @BindView(R.id.tvHomeAverageMoodGlobal) TextView tvGlobalMood;
    @BindView(R.id.tvHomeAverageMoodLocal) TextView tvLocalMood;
    @BindView(R.id.tvHomeNextDeadline) TextView tvNextDeadline;
    private String TAG = "HomeFragment";
    private OnMyStorySelectedListener mStoryListener;
    private OnMyStatsListener mStatsListener;

    public interface OnMyStorySelectedListener {
        public void onMyStorySelected();
    }

    public interface OnMyStatsListener {
        public void onMyStats();
    }

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        uID = auth.getCurrentUser().getUid();

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);

        //sets the daily mood if it has been added
        checkDailyMood();

        btnAdd = (ImageButton) rootView.findViewById(R.id.ibHomeAddMood);
        tvCurrentStreak = (TextView) rootView.findViewById(R.id.tvHomeCurrentStreak);
        tvMaxStreak = (TextView) rootView.findViewById(R.id.tvHomeMaxStreak);

        //sets the stats in their respective views
        getGlobalMoodAverage();
        setLocalMoodAverage();
        setDeadline();
        setStreak();

        btnAdd.setOnClickListener(this);
        btnMyStory.setOnClickListener(this);
        btnStats.setOnClickListener(this);

        return rootView;
    }

    private void setDeadline() {
        Query nextDeadline = mDatabase
                .child(USERS)
                .child(uID)
                .child(DEADLINES)
                .child(year_of_study)
                .orderByChild("dateObject")
                .limitToLast(1);
        nextDeadline.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        DeadlinesBean deadline = postSnapshot.getValue(DeadlinesBean.class);
                        tvNextDeadline.setText(deadline.getDate());
                    }
                } else {
                    tvNextDeadline.setText(NO_DATA);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkDailyMood() {
        //looks for today's date in database mood log
        String today = AppUtils.dateToFullString(new Date(), "dd-MM-yyyy");
        Query todayMood = mDatabase.child(USERS).child(uID).child(MOODS).child(year_of_study)
                .child(HelperFirebase.semester)
                .orderByKey()
                .equalTo(today);

        todayMood.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() != 0) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                MoodBean mood = postSnapshot.getValue(MoodBean.class);
                                switch (mood.getMood()) {
                                    case POSITIVE:
                                        tvMood.setText("POSITIVE");
                                        setTextColours();
                                        llMood.setBackgroundColor(getResources().getColor(R.color.positiveMoodToolbar));
                                        break;
                                    case NEUTRAL:
                                        tvMood.setText("NEUTRAL");
                                        setTextColours();
                                        llMood.setBackgroundColor(getResources().getColor(R.color.neutralMoodToolbar));
                                        break;
                                    case NEGATIVE:
                                        tvMood.setText("NEGATIVE");
                                        setTextColours();
                                        llMood.setBackgroundColor(getResources().getColor(R.color.negativeMoodToolbar));
                                        break;
                                    default:
                                        break;
                                }
                            }
                        } else {
                            //if date doesn't exist, set no data
                            tvMood.setText("No mood selected");
                            tvMood.setTextColor(getResources().getColor(android.R.color.black));
                            tvMoodTitle.setTextColor(getResources().getColor(android.R.color.black));
                            ibAddMood.setImageDrawable(getResources().getDrawable(R.drawable.ic_create_black));
                            llMood.setBackgroundColor(getResources().getColor(android.R.color.white));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setTextColours() {
        tvMood.setTextColor(getResources().getColor(android.R.color.white));
        tvMoodTitle.setTextColor(getResources().getColor(android.R.color.white));
        ibAddMood.setImageDrawable(getResources().getDrawable(R.drawable.ic_create_white));
    }


    private void setLocalMoodAverage() {
        mDatabase.child(USERS)
                .child(uID)
                .child(USER_STATS)
                .child(year_of_study)
                .child(semester)
                .child(AVERAGE_MOOD).child(TOTAL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long total = (Long) dataSnapshot.getValue();
                    getNumberOfMoodsLocal(total);
                } else {
                    tvLocalMood.setTextColor(getResources().getColor(android.R.color.black));
                    tvLocalMood.setText(NO_DATA);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getNumberOfMoodsLocal(final Long total) {
        mDatabase.child(USERS)
                .child(uID)
                .child(USER_STATS)
                .child(year_of_study)
                .child(semester)
                .child(AVERAGE_MOOD).child(NUMBER_OF_MOODS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long numberOfMoods = (Long) dataSnapshot.getValue();
                    int average = AppUtils.getAveragefromLong(total, numberOfMoods);
                    String mood = "";
                    switch(average){
                        case 1:
                            mood = "POSITIVE";
                            tvLocalMood.setTextColor(getResources().getColor(R.color.positiveMoodToolbar));
                            break;
                        case 2:
                            mood = "NEUTRAL";
                            tvLocalMood.setTextColor(getResources().getColor(R.color.neutralMoodToolbar));
                            break;
                        case 3:
                            mood = "NEGATIVE";
                            tvLocalMood.setTextColor(getResources().getColor(R.color.negativeMoodToolbar));
                            break;
                        default:;
                            tvLocalMood.setTextColor(getResources().getColor(android.R.color.black));
                            break;
                    }
                    tvLocalMood.setText(mood);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getGlobalMoodAverage() {
        mDatabase.child(GLOBAL_STATS)
                .child(university)
                .child(course)
                .child(year_of_study)
                .child(semester)
                .child(AVERAGE_MOOD).child(TOTAL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long total = (Long) dataSnapshot.getValue();
                    getNumberOfMoodsGlobal(total);
                } else {
                    tvGlobalMood.setTextColor(getResources().getColor(android.R.color.black));
                    tvGlobalMood.setText(NO_DATA);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getNumberOfMoodsGlobal(final Long total) {
        mDatabase.child(GLOBAL_STATS)
                .child(university)
                .child(course)
                .child(year_of_study)
                .child(semester)
                .child(AVERAGE_MOOD).child(NUMBER_OF_MOODS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long numberOfMoods = (Long) dataSnapshot.getValue();
                    int average = AppUtils.getAveragefromLong(total, numberOfMoods);
                    String mood = "";
                    switch(average){
                        case 1:
                            mood = "POSITIVE";
                            tvGlobalMood.setTextColor(getResources().getColor(R.color.positiveMoodToolbar));
                            break;
                        case 2:
                            mood = "NEUTRAL";
                            tvGlobalMood.setTextColor(getResources().getColor(R.color.neutralMoodToolbar));
                            break;
                        case 3:
                            mood = "NEGATIVE";
                            tvGlobalMood.setTextColor(getResources().getColor(R.color.negativeMoodToolbar));
                            break;
                        default:;
                            tvGlobalMood.setTextColor(getResources().getColor(android.R.color.black));
                            break;
                    }
                    tvGlobalMood.setText(mood);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setStreak() {
        mDatabase.child(USERS).child(uID).child(STREAK).child(CURRENT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    int streak = AppUtils.getIntfromLong((Long) dataSnapshot.getValue());
                    tvCurrentStreak.setText(Integer.toString(streak));
                } else {
                    tvCurrentStreak.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child(USERS).child(uID).child(STREAK).child(MAX).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    int streak = AppUtils.getIntfromLong((Long) dataSnapshot.getValue());
                    tvMaxStreak.setText(Integer.toString(streak));
                } else {
                    tvMaxStreak.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //attach story listener
        try {
            mStoryListener = (OnMyStorySelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //handle fragment onclick's
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.ibHomeAddMood:
                startActivity(new Intent(getActivity(), AddMoodActivity.class));
                break;
            case R.id.btnHomeMyStory:
                ((OnMyStorySelectedListener)getActivity()).onMyStorySelected();
                break;
            case R.id.btnHomeStats:
                ((OnMyStatsListener)getActivity()).onMyStats();
                break;
            default:
                break;
        }
    }
}
