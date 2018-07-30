package com.example.hamzah.modulediary;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.TextView;

import com.example.hamzah.modulediary.Calendar.CalendarFragment;
import com.example.hamzah.modulediary.Deadlines.DeadlinesFragment;
import com.example.hamzah.modulediary.Helper.AppUtils;
import com.example.hamzah.modulediary.Helper.HelperFirebase;
import com.example.hamzah.modulediary.ModulesList.ModulesFragment;
import com.example.hamzah.modulediary.MyStory.MyStoryFragment;
import com.example.hamzah.modulediary.Settings.SettingsFragment;
import com.example.hamzah.modulediary.Statistics.StatisticsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.AUTUMN;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.COURSE_NAME_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.PREF_PROFILE;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.SEMESTER_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.SPRING;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.UNIVERSITY_NAME_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.USERS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.YEAR_OF_ENTRY_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.YEAR_OF_GRAD_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.YEAR_OF_STUDY_KEY;

/**
 * handles the navigation drawer and main fragment switching
 */
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnMyStorySelectedListener,
        HomeFragment.OnMyStatsListener {

    private FirebaseAuth firebaseAuth;
    private FragmentTransaction transaction;
    private AppBarLayout appBarLayout;
    private ViewOutlineProvider outline;
    private StatisticsFragment statisticsFragment;
    private NavigationView navigationView;
    private String autumn;
    private String spring;
    private String springEnd;
    private DatabaseReference mDatabase;
    private String uID;
    @BindView(R.id.nav_header_home_user_email) TextView navHeaderUserEmail;
    @BindView(R.id.nav_header_home_user_name) TextView navHeaderUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        appBarLayout = (AppBarLayout)findViewById(R.id.home_activity_appBarLayout);
        outline = appBarLayout.getOutlineProvider();

        firebaseAuth = FirebaseAuth.getInstance();
        uID = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();



        FirebaseUser user = firebaseAuth.getCurrentUser();
        statisticsFragment = new StatisticsFragment();

        transaction = getSupportFragmentManager().beginTransaction();

        //gets the users year of study and semester
        findSemester();
        setUniCourse();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //sets the header of the navigation drawer to user email and name
        View header=navigationView.getHeaderView(0);
        TextView name = (TextView)header.findViewById(R.id.nav_header_home_user_name);
        TextView email = (TextView)header.findViewById(R.id.nav_header_home_user_email);
        name.setText(firebaseAuth.getCurrentUser().getDisplayName());
        email.setText(firebaseAuth.getCurrentUser().getEmail());
    }

    private void setUniCourse() {
        SharedPreferences uniPrefs = getSharedPreferences(PREF_PROFILE, Context.MODE_PRIVATE);
        HelperFirebase.university = uniPrefs.getString(UNIVERSITY_NAME_KEY, "");
        HelperFirebase.course = uniPrefs.getString(COURSE_NAME_KEY, "");
    }

    private void findSemester() {
        SharedPreferences uniPrefs = getSharedPreferences(PREF_PROFILE, Context.MODE_PRIVATE);
        String uniName = uniPrefs.getString(UNIVERSITY_NAME_KEY, "");
        mDatabase.child("term_dates")
                .child(uniName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null) {
                            for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                if (postSnapshot.getKey().equalsIgnoreCase("autumn")){
                                    autumn = postSnapshot.getValue(String.class);
                                } else if (postSnapshot.getKey().equalsIgnoreCase("spring")) {
                                    spring = postSnapshot.getValue(String.class);
                                } else if (postSnapshot.getKey().equalsIgnoreCase("spring_end")){
                                    springEnd = postSnapshot.getValue(String.class);
                                }
                            }
                            setSemester();
                            setYear();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setYear() {
        Date temp = new Date();
        Date springEndDate = AppUtils.getDateFromString(springEnd);
        Date today = AppUtils.getDateFromString(AppUtils.dateToFullString(temp, "dd-MM-yyyy"));

        SharedPreferences prefs = getSharedPreferences(PREF_PROFILE, MODE_PRIVATE);
        int gradYear = prefs.getInt(YEAR_OF_GRAD_KEY, 0);
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        calendar.clear();
        int difference = gradYear - thisYear;

        int yearOfStudy = prefs.getInt(YEAR_OF_STUDY_KEY, 0);
        HelperFirebase.year_of_study = "Y" + Integer.toString(yearOfStudy);

        //checks if summer and not final year
        if (today.after(springEndDate) & difference != 0){
            //gets and sets year of study according to year of entry, current year and grad year
            int entryYear = prefs.getInt(YEAR_OF_ENTRY_KEY, 0);
            int differenceThisYearEntryYear = (thisYear - entryYear) + 1;
            SharedPreferences.Editor gradEditor = getSharedPreferences(PREF_PROFILE, MODE_PRIVATE).edit();
            gradEditor.putInt(YEAR_OF_STUDY_KEY, differenceThisYearEntryYear);
            gradEditor.apply();
            mDatabase.child(USERS)
                    .child(uID)
                    .child("profile")
                    .child("year of study")
                    .setValue(differenceThisYearEntryYear);
        }

        HomeFragment fragment = new HomeFragment();
        transaction.add(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    private void setSemester() {
        Date temp = new Date();
        Date autumnDate = AppUtils.getDateFromString(autumn);
        Date springDate = AppUtils.getDateFromString(spring);
        Date today = AppUtils.getDateFromString(AppUtils.dateToFullString(temp, "dd-MM-yyyy"));

        SharedPreferences.Editor editor = getSharedPreferences(PREF_PROFILE, MODE_PRIVATE).edit();
        if (!(today.before(autumnDate) || today.after(springDate))){
            HelperFirebase.semester = AUTUMN;
            editor.putString(SEMESTER_KEY, "S1");
            editor.apply();
        } else {
            HelperFirebase.semester = SPRING;
            editor.putString(SEMESTER_KEY, "S2");
            editor.apply();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //handles the fragment switching based on navigation drawer item clicks
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        transaction = getSupportFragmentManager().beginTransaction();
        if (id == R.id.nav_home) {
            getSupportActionBar().setTitle("Home");
            appBarLayout.setOutlineProvider(outline);
            transaction.replace(R.id.fragmentContainer, new HomeFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_calendar) {
            //handles shadow beneath the toolbar
            appBarLayout.setOutlineProvider(outline);
            getSupportActionBar().setTitle("Calendar");
            transaction.replace(R.id.fragmentContainer, new CalendarFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_modules) {
            appBarLayout.setOutlineProvider(null);
            getSupportActionBar().setTitle("Modules");
            transaction.replace(R.id.fragmentContainer, new ModulesFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_statistics) {
            //hides the shadow under the toolbar
            getSupportActionBar().setTitle("Statistics");
            appBarLayout.setOutlineProvider(null);
            transaction.replace(R.id.fragmentContainer, statisticsFragment).addToBackStack(null).commit();
        } else if (id == R.id.nav_story) {
            appBarLayout.setOutlineProvider(outline);
            getSupportActionBar().setTitle("My Story");
            transaction.replace(R.id.fragmentContainer, new MyStoryFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_settings) {
            appBarLayout.setOutlineProvider(outline);
            getSupportActionBar().setTitle("Settings");
            transaction.replace(R.id.fragmentContainer, new SettingsFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_deadlines) {
            appBarLayout.setOutlineProvider(outline);
            getSupportActionBar().setTitle("Deadlines");
            transaction.replace(R.id.fragmentContainer, new DeadlinesFragment()).addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //home fragment listener to launch story fragment and set navigation option to my Story
    @Override
    public void onMyStorySelected() {
        navigationView.setCheckedItem(R.id.nav_story);
        transaction = getSupportFragmentManager().beginTransaction();
        appBarLayout.setOutlineProvider(outline);
        getSupportActionBar().setTitle("My Story");
        transaction.replace(R.id.fragmentContainer, new MyStoryFragment()).addToBackStack(null).commit();
    }

    ////home fragment listener to launch statistics fragment and set navigation option to my statistics
    @Override
    public void onMyStats() {
        navigationView.setCheckedItem(R.id.nav_statistics);
        transaction = getSupportFragmentManager().beginTransaction();
        getSupportActionBar().setTitle("Statistics");
        appBarLayout.setOutlineProvider(null);
        transaction.replace(R.id.fragmentContainer, statisticsFragment).addToBackStack(null).commit();
    }
}
