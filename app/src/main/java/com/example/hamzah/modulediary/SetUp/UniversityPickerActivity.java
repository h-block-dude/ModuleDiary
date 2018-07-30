package com.example.hamzah.modulediary.SetUp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import static com.example.hamzah.modulediary.Helper.HelperFirebase.PREF_PROFILE;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.UNIVERSITY_NAME_KEY;

/**
 * activity to pick university from database list
 */
public class UniversityPickerActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ListView lvUniversities;
    private ArrayList<String> universityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select University");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        lvUniversities = (ListView) findViewById(R.id.lvUniversities);

        //fetch universities from database and display in a list
        mDatabase.child("universities").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                universityList = new ArrayList<String>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();

                    universityList.add(key);

                }
                String[] listItems = new String[universityList.size()];
                for(int i = 0; i < universityList.size(); i++){
                    listItems[i] = universityList.get(i);
                }

                ArrayAdapter adapter = new ArrayAdapter(UniversityPickerActivity.this, android.R.layout.simple_list_item_1, listItems);
                lvUniversities.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //launches coursePicker activity and passes university selected
        lvUniversities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(UniversityPickerActivity.this, CoursePickerActivity.class);
                intent.putExtra("university", universityList.get(i));
                startActivity(intent);
                addUniversity(universityList.get(i));
            }
        });
    }

    //adds university to the database user profile
    private void addUniversity(String university) {
        //added to shared preference for database access later on
        SharedPreferences.Editor universityEditor = getSharedPreferences(PREF_PROFILE, MODE_PRIVATE).edit();
        universityEditor.putString(UNIVERSITY_NAME_KEY, university);
        universityEditor.apply();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uID = firebaseAuth.getCurrentUser().getUid();
        Log.d("UniversityPicker", "uID: " + uID + " university " + university);
        mDatabase.child("users").child(uID).child("profile").child("university").setValue(university);
    }
}
