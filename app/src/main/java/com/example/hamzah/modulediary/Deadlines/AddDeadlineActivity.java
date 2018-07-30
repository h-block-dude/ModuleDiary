package com.example.hamzah.modulediary.Deadlines;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.DEADLINES;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.FULL_YEAR;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.MODULES;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.USERS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.year_of_study;

public class AddDeadlineActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.spinnerDeadlineModule) Spinner spinnerModule;
    @BindView(R.id.spinnerDeadlinetype) Spinner spinnerType;
    @BindView(R.id.etDeadlineCourseworkTitle) EditText etCourseworkTitle;
    @BindView(R.id.etDeadlineDate) EditText etDate;
    private String assessmentType = "";
    private String module = "";
    private String sDate = "";
    private List<String> assessmentSpinnerArray;
    private List<String> moduleSpinnerArray;
    private DatabaseReference mDatabaseModules;
    private DatabaseReference mDatabaseDeadlines;
    private FirebaseAuth auth;
    private String uID;
    private Calendar calendar;
    private int year, month, day;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deadline);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        uID = auth.getCurrentUser().getUid();
        mDatabaseDeadlines = FirebaseDatabase.getInstance().getReference();
        mDatabaseModules = FirebaseDatabase.getInstance().getReference()
                .child(USERS)
                .child(uID)
                .child(MODULES)
                .child(HelperFirebase.year_of_study);

        ButterKnife.bind(this);

        setDate();

        //fills spinner array and sets the spinner up
        fillAssessmentTypeSpinner();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, assessmentSpinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
        //spinner listener
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                assessmentType = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getSemesterModules();
        etDate.setOnClickListener(this);
    }

    //sets the date for the datepicker dialog
    private void setDate() {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = ((calendar.get(Calendar.MONTH)));
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    //loads the active modules into the spinner array
    private void getSemesterModules() {
        moduleSpinnerArray = new ArrayList<>();
        mDatabaseModules.child(HelperFirebase.semester).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    ModulesSetUpBean module = postSnapshot.getValue(ModulesSetUpBean.class);
                    moduleSpinnerArray.add(module.getId() + " - " + module.getTitle());
                }
                getFullYearModules();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFullYearModules() {
        mDatabaseModules.child(FULL_YEAR).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    ModulesSetUpBean module = postSnapshot.getValue(ModulesSetUpBean.class);
                    moduleSpinnerArray.add(module.getId() + " - " + module.getTitle());
                }
                setUpModulesSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUpModulesSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, moduleSpinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModule.setAdapter(adapter);
        spinnerModule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                module = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void fillAssessmentTypeSpinner() {
        assessmentSpinnerArray = new ArrayList<>();
        assessmentSpinnerArray.add("Coursework");
        assessmentSpinnerArray.add("Exam");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        //button click to show the datepicker dialog
        switch(id){
            case R.id.etDeadlineDate:
                Dialog dialog = new DatePickerDialog(this, myDateListener, year, month, day);
                dialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_deadline, menu);
        return true;
    }

    //sets the selected date within the text field
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            String day = Integer.toString(i2);
            String month = Integer.toString(i1 + 1);
            String year = Integer.toString(i);
            sDate = day + "-" + month + "-" + year;
            Toast.makeText(AddDeadlineActivity.this, " " + sDate, Toast.LENGTH_SHORT).show();
            etDate.setText(sDate);
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, i2);
            cal.set(Calendar.MONTH, i1 + 1);
            cal.set(Calendar.YEAR, i);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.MILLISECOND, 0);
            date = cal.getTime();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //handles the back button and done button presses
        switch(id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_add_new_deadline:
                saveData();
                finish();
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //saves the data to the database
    private void saveData() {
        DeadlinesBean deadline = new DeadlinesBean();
        deadline.setDate(sDate);
        String[] temp = module.split(" ");
        String s1 = temp[0];
        deadline.setModuleId(s1);
        deadline.setModuleTitle(temp[2]);
        deadline.setType(assessmentType);
        deadline.setDateObject(Long.toString(date.getTime()));
        deadline.setTitle(etCourseworkTitle.getText().toString());
        mDatabaseDeadlines.child(USERS)
                .child(uID)
                .child(DEADLINES)
                .child(year_of_study).push().setValue(deadline);
    }
}
