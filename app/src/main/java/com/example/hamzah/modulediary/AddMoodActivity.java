package com.example.hamzah.modulediary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.hamzah.modulediary.Helper.AppUtils;
import com.example.hamzah.modulediary.MyStory.StoryBean;
import com.example.hamzah.modulediary.SetUp.ModulePicker.ModulesSetUpBean;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.AVERAGE_MOOD;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.COURSE_NAME_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.CURRENT;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.DAY_RANKING;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.GLOBAL_STATS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.MAX;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.MODULES;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.MODULE_RANKING;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.MOODS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.MOOD_ENTRY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.NEGATIVE;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.NEUTRAL;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.NUMBER_OF_MOODS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.POSITIVE;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.PREF_PROFILE;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.SEMESTER_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.STREAK;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.TOTAL;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.UNIVERSITY_NAME_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.USERS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.USER_STATS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.YEAR_OF_STUDY_KEY;

public class AddMoodActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner spMood;
    private Button ibTag;
    private EditText etComment;
    private List<String> spinnerArray;
    private ArrayList<String> tempList;
    private ArrayList<String> moduleCodeList;
    private DatabaseReference mDatabase;
    private FloatingActionButton fab;
    private String uID;
    private String mood = "None";
    private List<Integer> mSelectedItems;
    private ArrayList<String> tagList;
    private boolean isStory = false;
    private int current;
    private int max;
    private int moodNumber;
    private String university = "";
    private String course = "";
    private String year_of_study = "";
    private String semester = "";
    @BindView(R.id.clAddMood) CoordinatorLayout clLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add mood");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get and set user profile details from sharedpreferences file
        SharedPreferences uniPrefs = getSharedPreferences(PREF_PROFILE, Context.MODE_PRIVATE);
        int yearOfStudy = uniPrefs.getInt(YEAR_OF_STUDY_KEY, 1);
        year_of_study = "Y" + Integer.toString(yearOfStudy);
        semester = uniPrefs.getString(SEMESTER_KEY, "S1");
        university = uniPrefs.getString(UNIVERSITY_NAME_KEY, "");
        course = uniPrefs.getString(COURSE_NAME_KEY, "");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        uID = auth.getCurrentUser().getUid();

        //initialise array adapters
        tempList = new ArrayList<>();
        mSelectedItems = new ArrayList();

        spMood = (Spinner) findViewById(R.id.spAddMood);
        ibTag = (Button) findViewById(R.id.btnAddMoodTag);
        etComment = (EditText)findViewById(R.id.etAddMoodComment);

        ibTag.setOnClickListener(this);

        //populates mood spinner
        fillArray();

        //array adapter for mood spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMood.setAdapter(adapter);
        spMood.setOnItemSelectedListener(this);

        //fab only appears when positive mood is selected
        fab = (FloatingActionButton) findViewById(R.id.fab_add_mood);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isStory){
                    isStory = true;
                    //indicator to show mood is selected
                    Snackbar snackbar = Snackbar
                            .make(clLayout, "Mood will be added to story", Snackbar.LENGTH_LONG);

                    snackbar.show();
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favourite_full));
                } else{
                    isStory = false;
                    Snackbar snackbar = Snackbar
                            .make(clLayout, "Mood will not be added to story", Snackbar.LENGTH_LONG);

                    snackbar.show();
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favourite_border));
                }
            }
        });
    }

    private void fillArray() {
        spinnerArray = new ArrayList<>();
        spinnerArray.add("None");
        spinnerArray.add("Positive");
        spinnerArray.add("Neutral");
        spinnerArray.add("Negative");
    }

    //spinner listeners to change status bar and toolbar colours depending on mood selected
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = (String) adapterView.getItemAtPosition(i);
        switch(i){
            case 0:
                mood = "none";
                fab.setVisibility(View.INVISIBLE);
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                break;
            case 1:
                mood = POSITIVE;
                fab.setVisibility(View.VISIBLE);
                getWindow().setStatusBarColor(getResources().getColor(R.color.positiveMoodStatusBar));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.positiveMoodToolbar)));
                break;
            case 2:
                mood = NEUTRAL;
                fab.setVisibility(View.INVISIBLE);
                getWindow().setStatusBarColor(getResources().getColor(R.color.neutralMoodStatusBar));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.neutralMoodToolbar)));
                break;
            case 3:
                mood = NEGATIVE;
                fab.setVisibility(View.INVISIBLE);
                getWindow().setStatusBarColor(getResources().getColor(R.color.negativeMoodStatusBar));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.negativeMoodToolbar)));
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_mood, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_add_mood_done) {
            //set up MoodBean for database storage
            MoodBean moodBean = new MoodBean();
            String comment = etComment.getText().toString();
            moodBean.setComment(comment);
            moodBean.setMood(mood);
            setUpTagList();
            moodBean.setTags(tagList);
            Date date = new Date();
            String sDate = AppUtils.dateToFullString(date, "dd-MM-yyyy");

            //handle streak events
            if (moodBean.getMood().equalsIgnoreCase(POSITIVE)){
                iterateStreak();
            } else {
                clearStreak();
            }

            //add mood tally for specific module tags
            //for best and worst module ranking
            addMoodTallyToModuleTagsGlobal();

            //store user mood for access
            saveToUserMood(moodBean, sDate);

            //add to global mood average
            setMoodAverageValues();

            //add to global mood entry
            addToMoodEntry();

            //add to the best and worst day tally
            addMoodTallyToDays();

            //set up story bean for firebase entry
            if(isStory){
                StoryBean story = new StoryBean();
                story.setTags(tagList);
                story.setComment(comment);
                saveToStory(story, date, sDate);
            }

            finish();
            return true;
        } else if (id == android.R.id.home) {
                onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addMoodTallyToDays() {
        String day = AppUtils.dateToFullString(new Date(), "EEE");

        mDatabase.child(GLOBAL_STATS)
                .child(university)
                .child(course)
                .child(year_of_study)
                .child(semester)
                .child(DAY_RANKING)
                .child(day)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        if (mutableData.getValue() != null) {
                            Long moduleValue = (Long) mutableData.getValue();
                            if (mood.equalsIgnoreCase(POSITIVE)) {
                                int updateValue = AppUtils.getIntfromLong(moduleValue) + 1;
                                mutableData.setValue(updateValue);
                            } else if (mood.equalsIgnoreCase(NEGATIVE)) {
                                int updateValue = AppUtils.getIntfromLong(moduleValue) - 1;
                                mutableData.setValue(updateValue);
                            }
                        } else {
                            if (mood.equalsIgnoreCase(POSITIVE)) {
                                mutableData.setValue(1);
                            } else if (mood.equalsIgnoreCase(NEGATIVE)) {
                                int updateValue = -1;
                                mutableData.setValue(updateValue);
                            }
                        }

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });

        addMoodTallyToDaysLocal(day);
    }

    private void addMoodTallyToDaysLocal(String day) {
        mDatabase.child(USERS)
                .child(uID)
                .child(USER_STATS)
                .child(year_of_study)
                .child(semester)
                .child(DAY_RANKING)
                .child(day)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        if (mutableData.getValue() != null) {
                            Long moduleValue = (Long) mutableData.getValue();
                            if (mood.equalsIgnoreCase(POSITIVE)) {
                                int updateValue = AppUtils.getIntfromLong(moduleValue) + 1;
                                mutableData.setValue(updateValue);
                            } else if (mood.equalsIgnoreCase(NEGATIVE)) {
                                int updateValue = AppUtils.getIntfromLong(moduleValue) - 1;
                                mutableData.setValue(updateValue);
                            }
                        } else {
                            if (mood.equalsIgnoreCase(POSITIVE)) {
                                mutableData.setValue(1);
                            } else if (mood.equalsIgnoreCase(NEGATIVE)) {
                                int updateValue = -1;
                                mutableData.setValue(updateValue);
                            }
                        }

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
    }

    /**
     * adds a tally for the modules to determine best and worst
     * using a transaction handler to account for concurrent modifications
     */
    private void addMoodTallyToModuleTagsGlobal() {

        for (String module : tagList) {
            if (!module.equalsIgnoreCase("none")) {
                mDatabase.child(GLOBAL_STATS)
                        .child(university)
                        .child(course)
                        .child(year_of_study)
                        .child(semester)
                        .child(MODULE_RANKING)
                        .child(module)
                        .runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                if (mutableData.getValue() != null) {
                                    Long moduleValue = (Long) mutableData.getValue();
                                    if (mood.equalsIgnoreCase(POSITIVE)) {
                                        int updateValue = AppUtils.getIntfromLong(moduleValue) + 1;
                                        mutableData.setValue(updateValue);
                                    } else if (mood.equalsIgnoreCase(NEGATIVE)) {
                                        int updateValue = AppUtils.getIntfromLong(moduleValue) - 1;
                                        mutableData.setValue(updateValue);
                                    }
                                } else {
                                    if (mood.equalsIgnoreCase(POSITIVE)) {
                                        mutableData.setValue(1);
                                    } else if (mood.equalsIgnoreCase(NEGATIVE)) {
                                        int updateValue = -1;
                                        mutableData.setValue(updateValue);
                                    }
                                }

                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        });
            }
        }
        addMoodTallyToModuleTagsLocal();
    }

    private void addMoodTallyToModuleTagsLocal() {
        for (String module : tagList) {
            if (!module.equalsIgnoreCase("none")) {
                mDatabase.child(USERS)
                        .child(uID)
                        .child(USER_STATS)
                        .child(year_of_study)
                        .child(semester)
                        .child(MODULE_RANKING)
                        .child(module)
                        .runTransaction(new Transaction.Handler() {
                            //transaction to handle race condition possibility
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                if (mutableData.getValue() != null) {
                                    Long moduleValue = (Long) mutableData.getValue();
                                    if (mood.equalsIgnoreCase(POSITIVE)) {
                                        int updateValue = AppUtils.getIntfromLong(moduleValue) + 1;
                                        mutableData.setValue(updateValue);
                                    } else if (mood.equalsIgnoreCase(NEGATIVE)) {
                                        int updateValue = AppUtils.getIntfromLong(moduleValue) - 1;
                                        mutableData.setValue(updateValue);
                                    }
                                } else {
                                    if (mood.equalsIgnoreCase(POSITIVE)) {
                                        mutableData.setValue(1);
                                    } else if (mood.equalsIgnoreCase(NEGATIVE)) {
                                        int updateValue = -1;
                                        mutableData.setValue(updateValue);
                                    }
                                }

                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        });
            }
        }
    }

    private void addToMoodEntry() {
        switch(mood){
            case POSITIVE:
                addToPositiveMood();
                break;
            case NEUTRAL:
                addToNeutralMood();
                break;
            case NEGATIVE:
                addtoNegativeMood();
                break;
        }
        addToTotalMood();
    }

    private void addToTotalMood() {
        mDatabase.child(GLOBAL_STATS)
                .child(university)
                .child(course)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY)
                .child(TOTAL)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Long totalValue = (Long) dataSnapshot.getValue();
                            int updateValue = AppUtils.getIntfromLong(totalValue) + 1;
                            dataSnapshot.getRef().setValue(updateValue);
                        } else {
                            dataSnapshot.getRef().setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mDatabase.child(USERS)
                .child(uID)
                .child(USER_STATS)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY)
                .child(TOTAL)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Long totalValue = (Long) dataSnapshot.getValue();
                            int updateValue = AppUtils.getIntfromLong(totalValue) + 1;
                            dataSnapshot.getRef().setValue(updateValue);
                        } else {
                            dataSnapshot.getRef().setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addtoNegativeMood() {
        //add to global node
        mDatabase.child(GLOBAL_STATS)
                .child(university)
                .child(course)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY)
                .child(NEGATIVE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Long negativeValue = (Long) dataSnapshot.getValue();
                            int updateValue = AppUtils.getIntfromLong(negativeValue) + 1;
                            dataSnapshot.getRef().setValue(updateValue);
                        } else {
                            dataSnapshot.getRef().setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        //add to local node
        mDatabase.child(USERS)
                .child(uID)
                .child(USER_STATS)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY)
                .child(NEGATIVE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Long negativeValue = (Long) dataSnapshot.getValue();
                            int updateValue = AppUtils.getIntfromLong(negativeValue) + 1;
                            dataSnapshot.getRef().setValue(updateValue);
                        } else {
                            dataSnapshot.getRef().setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addToNeutralMood() {
        //add to global node
        mDatabase.child(GLOBAL_STATS)
                .child(university)
                .child(course)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY)
                .child(NEUTRAL)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Long neutralValue = (Long) dataSnapshot.getValue();
                            int updateValue = AppUtils.getIntfromLong(neutralValue) + 1;
                            dataSnapshot.getRef().setValue(updateValue);
                        } else {
                            dataSnapshot.getRef().setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        //add to local node
        mDatabase.child(USERS)
                .child(uID)
                .child(USER_STATS)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY)
                .child(NEUTRAL)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Long neutralValue = (Long) dataSnapshot.getValue();
                            int updateValue = AppUtils.getIntfromLong(neutralValue) + 1;
                            dataSnapshot.getRef().setValue(updateValue);
                        } else {
                            dataSnapshot.getRef().setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addToPositiveMood() {
        //add to global node
        mDatabase.child(GLOBAL_STATS)
                .child(university)
                .child(course)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY)
                .child(POSITIVE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Long positiveValue = (Long) dataSnapshot.getValue();
                            int updateValue = AppUtils.getIntfromLong(positiveValue) + 1;
                            dataSnapshot.getRef().setValue(updateValue);
                        } else {
                            dataSnapshot.getRef().setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        //add to local node
        mDatabase.child(USERS)
                .child(uID)
                .child(USER_STATS)
                .child(year_of_study)
                .child(semester)
                .child(MOOD_ENTRY)
                .child(POSITIVE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Long positiveValue = (Long) dataSnapshot.getValue();
                            int updateValue = AppUtils.getIntfromLong(positiveValue) + 1;
                            dataSnapshot.getRef().setValue(updateValue);
                        } else {
                            dataSnapshot.getRef().setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setMoodAverageValues() {
        switch(mood){
            case POSITIVE:
                moodNumber = 1;
                break;
            case NEUTRAL:
                moodNumber = 2;
                break;
            case NEGATIVE:
                moodNumber = 3;
                break;
            default:
                break;
        }

        setLocalMoodAverage();

        //set and replace total for moods
        mDatabase.child(GLOBAL_STATS)
                .child(university)
                .child(course)
                .child(year_of_study)
                .child(semester)
                .child(AVERAGE_MOOD).child(TOTAL).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long newMoodNumber = (Long) dataSnapshot.getValue() + moodNumber;
                    dataSnapshot.getRef().setValue(newMoodNumber);
                } else {
                    dataSnapshot.getRef().setValue(moodNumber);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //set and replace number of moods
        mDatabase.child(GLOBAL_STATS)
                .child(university)
                .child(course)
                .child(year_of_study)
                .child(semester)
                .child(AVERAGE_MOOD).child(NUMBER_OF_MOODS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long newMoodNumber = (Long) dataSnapshot.getValue() + 1;
                    dataSnapshot.getRef().setValue(newMoodNumber);
                } else {
                    dataSnapshot.getRef().setValue(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setLocalMoodAverage() {
        //set and replace total for moods
        mDatabase.child(USERS)
                .child(uID)
                .child(USER_STATS)
                .child(year_of_study)
                .child(semester)
                .child(AVERAGE_MOOD).child(TOTAL).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long newMoodNumber = (Long) dataSnapshot.getValue() + moodNumber;
                    dataSnapshot.getRef().setValue(newMoodNumber);
                } else {
                    dataSnapshot.getRef().setValue(moodNumber);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //set and replace number of moods
        mDatabase.child(USERS)
                .child(uID)
                .child(USER_STATS)
                .child(year_of_study)
                .child(semester)
                .child(AVERAGE_MOOD).child(NUMBER_OF_MOODS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Long newMoodNumber = (Long) dataSnapshot.getValue() + 1;
                    dataSnapshot.getRef().setValue(newMoodNumber);
                } else {
                    dataSnapshot.getRef().setValue(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void clearStreak() {
        //get current streak
        mDatabase.child(USERS).child(uID).child(STREAK).child(CURRENT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    current = AppUtils.getIntfromLong((Long) dataSnapshot.getValue());
                    compareToMax(current);
                } else{
                    current = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child(USERS).child(uID).child(STREAK).child(CURRENT).removeValue();
    }

    private void compareToMax(final int mCurrent) {
        //get max streak
        mDatabase.child(USERS)
                .child(uID)
                .child(STREAK)
                .child(MAX)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() !=null){
                            max = AppUtils.getIntfromLong((long)dataSnapshot.getValue());
                            //if current streak is greater, set as max and clear
                            if (mCurrent > max){
//                                mDatabase.child("users").child(uID).child("streak").child("max").setValue(current);
                                dataSnapshot.getRef().setValue(current);
                            }
                        } else {
                            max = mCurrent;
//                            mDatabase.child("users").child(uID).child("streak").child("max").setValue(max);
                            dataSnapshot.getRef().setValue(max);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void iterateStreak() {
        mDatabase.child(USERS)
                .child(uID)
                .child(STREAK)
                .child(CURRENT)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() !=null){
                            long currentStreak = (long) dataSnapshot.getValue();
                            int streak = ((Long) currentStreak).intValue();
                            int tempInt = streak + 1;
                            setNewCurrentStreak(tempInt);
                        } else{
                            setNewCurrentStreak(1);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //saves to user moods firebase
    private void saveToUserMood(MoodBean mood, String date) {
        mDatabase.child(USERS).child(uID).child(MOODS).child(year_of_study).child("S2").child(date).setValue(mood);
    }

    //saves to story node
    private void saveToStory(StoryBean story, Date date, String sDate) {
        // TODO: 07/04/2017 add index as key to cycle through randomly later
        //positive story addition
        story.setDate(sDate);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        String day = sdf.format(date);
        story.setDay(day);

        mDatabase.child("users").child(uID).child("story").child(sDate).setValue(story);
    }

    private void setNewCurrentStreak(int streak) {
        mDatabase.child(USERS).child(uID).child(STREAK).child(CURRENT).setValue(streak);
    }

    private void setUpTagList() {
        tempList = moduleCodeList;
        if(mSelectedItems.size() != 0 ) {
            tagList = new ArrayList<>();
            for (Integer i : mSelectedItems) {
                tagList.add(tempList.get(i));
            }
        } else {
            tagList = new ArrayList<>();
            tagList.add("none");
        }
    }

    /**
     * onclick listener for button press detection
     * handles population of alert dialog box with modules and checked items
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.btnAddMoodTag:
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child(USERS)
                        .child(uID)
                        .child(MODULES)
                        .child(year_of_study)
                        .child(semester)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                tempList = new ArrayList<>();
                                moduleCodeList = new ArrayList<>();
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    ModulesSetUpBean module = postSnapshot.getValue(ModulesSetUpBean.class);
                                    tempList.add(module.getTitle());
                                    moduleCodeList.add(module.getId());
                                }

                                CharSequence[] thislist = tempList.toArray(new CharSequence[tempList.size()]);
                                mSelectedItems = new ArrayList();
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddMoodActivity.this);
                                builder.setTitle("Add tag")
                                        .setMultiChoiceItems(thislist, null,new DialogInterface.OnMultiChoiceClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                                                if (isChecked) {
                                                    // If the user checked the item, add it to the selected items
                                                    mSelectedItems.add(which);
                                                } else if (mSelectedItems.contains(which)) {
                                                    // Else, if the item is already in the array, remove it
                                                    mSelectedItems.remove(Integer.valueOf(which));
                                                }
                                            }
                                        })
                                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                builder.create().show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                break;
            default:
                break;
        }
    }
}
