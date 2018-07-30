package com.example.hamzah.modulediary.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hamzah.modulediary.Deadlines.DeadlinesBean;
import com.example.hamzah.modulediary.Helper.HelperFirebase;
import com.example.hamzah.modulediary.MoodBean;
import com.example.hamzah.modulediary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hirondelle.date4j.DateTime;

import static com.example.hamzah.modulediary.Helper.HelperFirebase.DEADLINES;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.USERS;
import static com.example.hamzah.modulediary.Helper.HelperFirebase.year_of_study;

/**
 * custom adapter to fill the individual date cell views in the
 * Caldroid calendar view
 */

public class CaldroidCustomAdapter extends CaldroidGridAdapter {

    private FirebaseAuth auth;
    private DatabaseReference mMoodsDatabase;
    private DatabaseReference mDeadlinesDatabase;
    private String uID;
    private List<DateTime> indicatorDates = new ArrayList<>();
    private List<DateTime> deadlineDates = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context
     * @param month
     * @param year
     * @param caldroidData
     * @param extraData
     */
    public CaldroidCustomAdapter(Context context, int month, int year,
                                 Map<String, Object> caldroidData,
                                 Map<String, Object> extraData) {
        super(context, month, year, caldroidData, extraData);
        auth = FirebaseAuth.getInstance();
        uID = auth.getCurrentUser().getUid();

        //set the references for the database
        mMoodsDatabase = FirebaseDatabase.getInstance().getReference()
                .child(USERS)
                .child(uID)
                .child(HelperFirebase.MOODS)
                .child(HelperFirebase.year_of_study)
                .child(HelperFirebase.semester);
        mDeadlinesDatabase = FirebaseDatabase.getInstance().getReference()
                .child(USERS)
                .child(uID)
                .child(DEADLINES)
                .child(year_of_study);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cellView = convertView;

        // For reuse
        if (convertView == null) {
            cellView = inflater.inflate(R.layout.custom_calendar_cell, null);
        }

        //store padding for the cell
        int topPadding = cellView.getPaddingTop();
        int leftPadding = cellView.getPaddingLeft();
        int bottomPadding = cellView.getPaddingBottom();
        int rightPadding = cellView.getPaddingRight();

        //initialise views in the cell
        TextView tv1 = (TextView) cellView.findViewById(R.id.tv1);
        final View moodLine = cellView.findViewById(R.id.viewCalendarMoodLine);
        final ImageView ivCircle = (ImageView) cellView.findViewById(R.id.ivCalendarDeadlineIndicator);

        //set text colour for date
        tv1.setTextColor(Color.BLACK);

        //get dateTime of this cell
        final DateTime dateTime = this.datetimeList.get(position);
        Resources resources = context.getResources();

        //sets the visibility of views depending on the deadlines
        mDeadlinesDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    Set<DateTime> dates = new HashSet<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        DeadlinesBean deadline = postSnapshot.getValue(DeadlinesBean.class);
                        DateTime compareDateTime = convertStringDateToDateTime(deadline.getDate());
                        dates.add(compareDateTime);
                    }
                    deadlineDates.clear();
                    deadlineDates.addAll(dates);
                    setDeadlineIndicator(dateTime, ivCircle);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //sets the visibility of views depending on the deadlines
        mMoodsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Set<DateTime> dates = new HashSet<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        MoodBean mood = postSnapshot.getValue(MoodBean.class);
                        String date = postSnapshot.getKey();
                        DateTime compareDateTime = convertStringDateToDateTime(date);
                        dates.add(compareDateTime);
                        if (dateTime.equals(compareDateTime)){
                            switch(mood.getMood()){
                                case HelperFirebase.POSITIVE:
                                    moodLine.setBackgroundColor(context.getResources().getColor(R.color.positiveMoodToolbar));
                                    break;
                                case HelperFirebase.NEUTRAL:
                                    moodLine.setBackgroundColor(context.getResources().getColor(R.color.neutralMoodToolbar));
                                    break;
                                case HelperFirebase.NEGATIVE:
                                    moodLine.setBackgroundColor(context.getResources().getColor(R.color.negativeMoodToolbar));
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    indicatorDates.clear();
                    indicatorDates.addAll(dates);
                    setMoodLine(dateTime, moodLine);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //set color of the dates in previous / next month
        if (dateTime.getMonth() != month) {
            tv1.setTextColor(resources
                    .getColor(com.caldroid.R.color.caldroid_darker_gray));
        }

        boolean shouldResetDiabledView = false;
        boolean shouldResetSelectedView = false;

        // Customize for disabled dates and date outside min/max dates
        if ((minDateTime != null && dateTime.lt(minDateTime))
                || (maxDateTime != null && dateTime.gt(maxDateTime))
                || (disableDates != null && disableDates.indexOf(dateTime) != -1)) {

            tv1.setTextColor(CaldroidFragment.disabledTextColor);
            if (CaldroidFragment.disabledBackgroundDrawable == -1) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.disable_cell);
            } else {
                cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
            }

            if (dateTime.equals(getToday())) {
                tv1.setTextColor(Color.BLACK);
                tv1.setTextSize(18);
                tv1.setTypeface(Typeface.DEFAULT_BOLD);
            }

        } else {
            shouldResetDiabledView = true;
        }

        // Customize for selected dates
        if (selectedDates != null && selectedDates.indexOf(dateTime) != -1) {
            cellView.setBackgroundColor(resources
                    .getColor(com.caldroid.R.color.caldroid_sky_blue));

            tv1.setTextColor(Color.BLACK);

        } else {
            shouldResetSelectedView = true;
        }

        if (shouldResetDiabledView && shouldResetSelectedView) {
            // Customize for today
            if (dateTime.equals(getToday())) {
                //sets text for today's date as black, bigger font and bold style
                tv1.setTextColor(Color.BLACK);
                tv1.setTextSize(18);
                tv1.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                cellView.setBackgroundResource(com.caldroid.R.drawable.cell_bg);
            }
        }

        tv1.setText("" + dateTime.getDay());

        //recovers padding
        cellView.setPadding(leftPadding, topPadding, rightPadding,
                bottomPadding);

        // Set custom color if required
        setCustomResources(dateTime, cellView, tv1);

        return cellView;
    }

    private void setDeadlineIndicator(DateTime dateTime, ImageView ivCircle) {
        if(deadlineDates.contains(dateTime)){
            ivCircle.setVisibility(View.VISIBLE);
        } else {
            ivCircle.setVisibility(View.INVISIBLE);
        }
    }

    //decides if indicator is shown or not
    private void setMoodLine(DateTime dateTime, View moodLine) {
        if (indicatorDates.contains(dateTime)){
            moodLine.setVisibility(View.VISIBLE);
        } else {
            moodLine.setVisibility(View.INVISIBLE);
        }
    }

    //helper functions to convert date
    private DateTime convertStringDateToDateTime(String date) {
        String[] parts = date.split("-");
        String day = parts[0];
        String month = parts[1];
        String year = parts[2];

        return new DateTime(Integer.parseInt(year), (Integer.parseInt(month)), Integer.parseInt(day), 0, 0, 0, 0);
    }
}
