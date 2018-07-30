package com.example.hamzah.modulediary.Statistics;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hamzah.modulediary.Helper.AppUtils;
import com.example.hamzah.modulediary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.AVERAGE_MOOD;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.DAY_RANKING;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.GLOBAL_STATS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.MODULE_RANKING;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.MOOD_ENTRY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.NEGATIVE;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.NEUTRAL;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.NO_DATA;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.NUMBER_OF_MOODS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.POSITIVE;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.PREF_PROFILE;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.TOTAL;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.USERS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.USER_STATS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.YEAR_OF_STUDY_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.course;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.university;

public class AutumnFragment extends Fragment {

    //binds views using ButterKnife binder library
    @BindView(R.id.tvStatsAvgMoodGlobal) TextView tvAvgMoodGlobal;
    @BindView(R.id.tvStatsAvgMoodLocal) TextView tvAvgMoodLocal;
    @BindView(R.id.tvStatsPosMoodGlobal) TextView tvMoodPosGlobal;
    @BindView(R.id.tvStatsPosMoodLocal) TextView tvMoodPosLocal;
    @BindView(R.id.tvStatsNeutralMoodGlobal) TextView tvMoodNeuGlobal;
    @BindView(R.id.tvStatsNeutralMoodLocal) TextView tvMoodNeuLocal;
    @BindView(R.id.tvStatsNegMoodGlobal) TextView tvMoodNegGlobal;
    @BindView(R.id.tvStatsNegMoodLocal) TextView tvMoodNegLocal;
    @BindView(R.id.tvStatsBestModuleGlobal) TextView tvModuleBestGlobal;
    @BindView(R.id.tvStatsBestModuleLocal) TextView tvModuleBestLocal;
    @BindView(R.id.tvStatsWorstModuleGlobal) TextView tvModuleWorstGlobal;
    @BindView(R.id.tvStatsWorstModuleLocal) TextView tvModuleWorstLocal;
    @BindView(R.id.tvStatsBestDayGlobal) TextView tvBestDayGlobal;
    @BindView(R.id.tvStatsBestDayLocal) TextView tvBestDayLocal;
    @BindView(R.id.tvStatsWorstDayGlobal) TextView tvWorstDayGlobal;
    @BindView(R.id.tvStatsWorstDayLocal) TextView tvWorstDayLocal;
    @BindView(R.id.progressBarPositiveGlobal) ProgressBar pgBarPosGlobal;
    @BindView(R.id.progressBarPositiveLocal) ProgressBar pgBarPosLocal;
    @BindView(R.id.progressBarNeutralGlobal) ProgressBar pgBarNeuGlobal;
    @BindView(R.id.progressBarNeutralLocal) ProgressBar pgBarNeuLocal;
    @BindView(R.id.progressBarNegativeGlobal) ProgressBar pgBarNegGlobal;
    @BindView(R.id.progressBarNegativeLocal) ProgressBar pgBarNegLocal;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private String uID;
    private int average;
    //these change depending on the fragment being used
    private String year_of_study = "Y1";
    private String semester = "S1";
    private static final String ARG_PARAM = "param";
    private DatabaseReference myLocalRef;
    private DatabaseReference myGlobalRef;


    public AutumnFragment() {
        // Required empty public constructor
    }

    public static AutumnFragment newInstance(String param) {
        AutumnFragment fragment = new AutumnFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            semester = getArguments().getString(ARG_PARAM);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        uID= auth.getCurrentUser().getUid();
        myLocalRef = mDatabase.child(USERS).child(uID).child(USER_STATS).child(year_of_study).child(semester);
        myGlobalRef = mDatabase.child(GLOBAL_STATS).child(university).child(course).child(year_of_study).child(semester);
        SharedPreferences uniPrefs = getActivity().getSharedPreferences(PREF_PROFILE, Context.MODE_PRIVATE);
        int yearOfStudy = uniPrefs.getInt(YEAR_OF_STUDY_KEY, 1);
        year_of_study = "Y" + Integer.toString(yearOfStudy);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_autumn, container,false);
        ButterKnife.bind(this, rootView);


        //set global stats
        getGlobalMoodAverage();
        getGlobalPositivePercent();
        getGlobalNeutralPercent();
        getGlobalNegativePercent();
        getGlobalBestModule();
        getGlobalWorstModule();
        getGlobalBestDay();
        getGlobalWorstDay();

        //set local stats
        setLocalMoodAverage();
        setLocalPositivePercent();
        setLocalNeutralPercent();
        setLocalNegativePercent();
        setLocalBestModule();
        setLocalWorstModule();
        setLocalBestDay();
        setLocalWorstDay();

        return rootView;
    }

    private void setLocalWorstDay() {
        /**
         * this queries the best module
         * if tied it is selected lexicographically
         */
        final Query myBestModuleQuery = myLocalRef
                .child(DAY_RANKING)
                .orderByValue()
                .limitToFirst(1);

                    myBestModuleQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            tvWorstDayLocal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            tvWorstDayLocal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }

    private void setLocalBestDay() {
        /**
         * this queries the best module
         * if tied it is selected lexicographically
         */
        final Query myBestDayQuery = myLocalRef
                .child(DAY_RANKING)
                .orderByValue()
                .limitToLast(1);

                    myBestDayQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                tvBestDayLocal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            tvBestDayLocal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }

    private void setLocalWorstModule() {
        /**
         * this queries the best module
         * if tied it is selected lexicographically
         */
        final Query myBestModuleQuery = myLocalRef
                .child(MODULE_RANKING)
                .orderByValue()
                .limitToFirst(1);

                    myBestModuleQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            tvModuleWorstLocal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            tvModuleWorstLocal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }

    private void setLocalBestModule() {
        /**
         * this queries the best module
         * if tied it is selected lexicographically
         */
        final Query myBestModuleQuery = myLocalRef
                .child(MODULE_RANKING)
                .orderByValue()
                .limitToLast(1);

                    myBestModuleQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            tvModuleBestLocal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            tvModuleBestLocal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }

    private void setLocalNegativePercent() {
        mDatabase.child(USERS)
                .child(uID)
                .child(USER_STATS)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY).child(NEGATIVE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long negative = (Long) dataSnapshot.getValue();
                    getTotalPercentLocal(negative, NEGATIVE);
                } else{
                    tvMoodNegLocal.setText(NO_DATA);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setLocalNeutralPercent() {
        mDatabase.child(USERS)
                .child(uID)
                .child(USER_STATS)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY).child(NEUTRAL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long neutral = (Long) dataSnapshot.getValue();
                    getTotalPercentLocal(neutral, NEUTRAL);
                } else {
                    tvMoodNeuLocal.setText(NO_DATA);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setLocalPositivePercent() {
        mDatabase.child(USERS)
                .child(uID)
                .child(USER_STATS)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY).child(POSITIVE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long positive = (Long) dataSnapshot.getValue();
                    getTotalPercentLocal(positive, POSITIVE);
                } else {
                    tvMoodPosLocal.setText(NO_DATA);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getTotalPercentLocal(final Long positive, final String mood) {
        mDatabase.child(USERS)
                .child(uID)
                .child(USER_STATS)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY).child(TOTAL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long total = (Long) dataSnapshot.getValue();
                    int percent = AppUtils.getPercentageFromLongs(positive, total);
                    if (mood.equalsIgnoreCase(POSITIVE)) {
                        //animation code for the mood percentage rings
                        tvMoodPosLocal.setText(Integer.toString(percent) + "%");
                        ObjectAnimator animation = ObjectAnimator.ofInt (pgBarPosLocal, "progress", 0, (percent * 10));
                        animation.setDuration (1000); //in milliseconds
                        animation.setInterpolator (new AccelerateDecelerateInterpolator());
                        animation.start ();
                        pgBarPosLocal.clearAnimation();
                    } else if (mood.equalsIgnoreCase(NEUTRAL)){
                        ObjectAnimator animation = ObjectAnimator.ofInt (pgBarNeuLocal, "progress", 0, (percent * 10));
                        animation.setDuration (1000); //in milliseconds
                        animation.setInterpolator (new AccelerateDecelerateInterpolator());
                        animation.start ();
                        pgBarNeuLocal.clearAnimation();
                        tvMoodNeuLocal.setText(Integer.toString(percent) + "%");
                    } else if (mood.equalsIgnoreCase(NEGATIVE)){
                        tvMoodNegLocal.setText(Integer.toString(percent) + "%");
                        ObjectAnimator animation = ObjectAnimator.ofInt (pgBarNegLocal, "progress", 0, (percent * 10));
                        animation.setDuration (1000); //in milliseconds
                        animation.setInterpolator (new AccelerateDecelerateInterpolator());
                        animation.start ();
                        pgBarNegLocal.clearAnimation();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                    tvAvgMoodLocal.setText(NO_DATA);
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
                    average = AppUtils.getAveragefromLong(total, numberOfMoods);
                    String mood = "";
                    switch(average){
                        case 1:
                            mood = "POSITIVE";
                            break;
                        case 2:
                            mood = "NEUTRAL";
                            break;
                        case 3:
                            mood = "NEGATIVE";
                            break;
                        default:
                            break;
                    }
                    tvAvgMoodLocal.setText(mood);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getGlobalWorstDay() {
        /**
         * this queries the best module
         * if tied it is selected lexicographically
         */
        final Query myBestModuleQuery = myGlobalRef
                .child(DAY_RANKING)
                .orderByValue()
                .limitToFirst(1);

                    myBestModuleQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            tvWorstDayGlobal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            tvWorstDayGlobal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }

    private void getGlobalBestDay() {
        /**
         * this queries the best module
         * if tied it is selected lexicographically
         */
        final Query myBestModuleQuery = myGlobalRef
                .child(DAY_RANKING)
                .orderByValue()
                .limitToLast(1);

                    myBestModuleQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            tvBestDayGlobal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            tvBestDayGlobal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }

    private void getGlobalWorstModule() {
        /**
         * this queries the best module
         * if tied it is selected lexicographically
         */
        final Query myBestModuleQuery = myGlobalRef
                .child(MODULE_RANKING)
                .orderByValue()
                .limitToFirst(1);

                    myBestModuleQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            tvModuleWorstGlobal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            tvModuleWorstGlobal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }

    private void getGlobalBestModule() {
        /**
         * this queries the best module
         * if tied it is selected lexicographically
         */
        final Query myBestModuleQuery = myGlobalRef
                .child(MODULE_RANKING)
                .orderByValue()
                .limitToLast(1);

                    myBestModuleQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            tvModuleBestGlobal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            tvModuleBestGlobal.setText(dataSnapshot.getKey());
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }

    private void getGlobalNegativePercent() {
        mDatabase.child(GLOBAL_STATS)
                .child(university)
                .child(course)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY).child(NEGATIVE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long negative = (Long) dataSnapshot.getValue();
                    getTotalPercentGlobal(negative, NEGATIVE);
                } else{
                    tvMoodNegGlobal.setText(NO_DATA);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getGlobalNeutralPercent() {
        mDatabase.child(GLOBAL_STATS)
                .child(university)
                .child(course)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY).child(NEUTRAL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long neutral = (Long) dataSnapshot.getValue();
                    getTotalPercentGlobal(neutral, NEUTRAL);
                } else {
                    tvMoodNeuGlobal.setText(NO_DATA);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getGlobalPositivePercent() {
        mDatabase.child(GLOBAL_STATS)
                .child(university)
                .child(course)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY).child(POSITIVE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long positive = (Long) dataSnapshot.getValue();
                    getTotalPercentGlobal(positive, POSITIVE);
                } else {
                    tvMoodPosGlobal.setText(NO_DATA);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * get total for percentage equation
     * work out percentage value
     * and set respective text views
     */
    private void getTotalPercentGlobal(final Long positive, final String mood) {
        mDatabase.child(GLOBAL_STATS)
                .child(university)
                .child(course)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY).child(TOTAL).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long total = (Long) dataSnapshot.getValue();
                    int percent = AppUtils.getPercentageFromLongs(positive, total);
                    if (mood.equalsIgnoreCase(POSITIVE)) {
                        tvMoodPosGlobal.setText(Integer.toString(percent) + "%");
                        ObjectAnimator animation = ObjectAnimator.ofInt (pgBarPosGlobal, "progress", 0, (percent * 10));
                        animation.setDuration (1000); //in milliseconds
                        animation.setInterpolator (new AccelerateDecelerateInterpolator());
                        animation.start ();
                        pgBarPosGlobal.clearAnimation();
                    } else if (mood.equalsIgnoreCase(NEUTRAL)){
                        ObjectAnimator animation = ObjectAnimator.ofInt (pgBarNeuGlobal, "progress", 0, (percent * 10));
                        animation.setDuration (1000); //in milliseconds
                        animation.setInterpolator (new AccelerateDecelerateInterpolator());
                        animation.start ();
                        pgBarNeuGlobal.clearAnimation();
                        tvMoodNeuGlobal.setText(Integer.toString(percent) + "%");
                    } else if (mood.equalsIgnoreCase(NEGATIVE)){
                        tvMoodNegGlobal.setText(Integer.toString(percent) + "%");
                        ObjectAnimator animation = ObjectAnimator.ofInt (pgBarNegGlobal, "progress", 0, (percent * 10));
                        animation.setDuration (1000); //in milliseconds
                        animation.setInterpolator (new AccelerateDecelerateInterpolator());
                        animation.start ();
                        pgBarNegGlobal.clearAnimation();
                    }
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
                    tvAvgMoodGlobal.setText(NO_DATA);
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
                    average = AppUtils.getAveragefromLong(total, numberOfMoods);
                    String mood = "";
                    switch(average){
                        case 1:
                            mood = "POSITIVE";
                            break;
                        case 2:
                            mood = "NEUTRAL";
                            break;
                        case 3:
                            mood = "NEGATIVE";
                            break;
                        default:
                            break;
                    }
                    tvAvgMoodGlobal.setText(mood);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
