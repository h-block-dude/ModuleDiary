package com.example.hamzah.modulediary.SetUp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.hamzah.modulediary.Helper.AppUtils;
import com.example.hamzah.modulediary.R;
import com.example.hamzah.modulediary.SetUp.ModulePicker.ModulePickerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.COURSE_LENGTH_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.PREF_PROFILE;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.YEAR_OF_ENTRY_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.YEAR_OF_GRAD_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.YEAR_OF_STUDY_KEY;

/**
 * activity to pick year of study based on university and course selected
 */
public class YearPickerActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ListView lvYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Year of study");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String course = getIntent().getExtras().getString("course");
        String university = getIntent().getExtras().getString("university");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        lvYear = (ListView) findViewById(R.id.lvYear);

        //database reference for length of course for that course to be put into a list
        mDatabase.child("courses/" + university + "/" + course).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String temp = dataSnapshot.getValue().toString();
                int numYears = Integer.parseInt(temp);

                //select year of study up to the length of that course
                String[] listItems = new String[numYears];
                for(int i = 0; i < numYears; i++){
                    listItems[i] = Integer.toString(i+1);
                }

                ArrayAdapter adapter = new ArrayAdapter(YearPickerActivity.this, android.R.layout.simple_list_item_1, listItems);
                lvYear.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //launches module selector
        lvYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(YearPickerActivity.this, ModulePickerActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                addYear(Integer.toString(i + 1));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addYear(String yearString) {
        //added to shared preference for database access later on
        SharedPreferences.Editor editor = getSharedPreferences(PREF_PROFILE, MODE_PRIVATE).edit();
        int yearOfStudy = Integer.parseInt(yearString);
        editor.putInt(YEAR_OF_STUDY_KEY, yearOfStudy);
        editor.apply();

        Calendar calendar = Calendar.getInstance();
        editor.putInt(YEAR_OF_ENTRY_KEY, calendar.get(Calendar.YEAR));
        editor.apply();

        SharedPreferences prefs = getSharedPreferences(PREF_PROFILE, MODE_PRIVATE);
        Long courseLength = prefs.getLong(COURSE_LENGTH_KEY, 0);
        if (courseLength != null) {
            int year = Integer.parseInt(yearString);
            int courseLengthInt = AppUtils.getIntfromLong(courseLength);
            int difference = courseLengthInt - year;
            //uses helper function to get year of graduation
            int gradYear = AppUtils.getGraduationYear(difference);
            editor.putInt(YEAR_OF_GRAD_KEY, gradYear);
            editor.apply();
        }
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uID = firebaseAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(uID).child("profile").child("year of study").setValue(yearString);
    }
}
