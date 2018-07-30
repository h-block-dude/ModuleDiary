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

import com.example.hamzah.modulediary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.COURSE_LENGTH_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.COURSE_NAME_KEY;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.PREF_PROFILE;

/**
 * activity to pick course based on university
 */
public class CoursePickerActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ListView lvCourses;
    private ArrayList<String> courseList;
    private ArrayList<Long> yearList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Course");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String university = getIntent().getExtras().getString("university");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        lvCourses = (ListView) findViewById(R.id.lvCourses);

        //finds courses based on the university previously selected in the database

        mDatabase.child("courses/" + university).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                courseList = new ArrayList<String>();
                yearList = new ArrayList<Long>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    Long year = postSnapshot.getValue(Long.class);
                    courseList.add(key);
                    yearList.add(year);
                }
                String[] listItems = new String[courseList.size()];
                for(int i = 0; i < courseList.size(); i++){
                    listItems[i] = courseList.get(i);
                }

                ArrayAdapter adapter = new ArrayAdapter(CoursePickerActivity.this, android.R.layout.simple_list_item_1, listItems);
                lvCourses.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        lvCourses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CoursePickerActivity.this, YearPickerActivity.class);
                intent.putExtra("course", courseList.get(i));
                intent.putExtra("university", university);
                startActivity(intent);
                addCourse(courseList.get(i), yearList.get(i));
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

    private void addCourse(String course, Long courseLength) {
        //added to shared preference for database access later on
        SharedPreferences.Editor editor = getSharedPreferences(PREF_PROFILE, MODE_PRIVATE).edit();
        editor.putLong(COURSE_LENGTH_KEY, courseLength);
        editor.putString(COURSE_NAME_KEY, course);
        editor.apply();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uID = firebaseAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(uID).child("profile").child("course").setValue(course);
    }
}
