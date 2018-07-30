package com.example.hamzah.modulediary.SetUp.ModulePicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hamzah.modulediary.HomeActivity;
import com.example.hamzah.modulediary.R;

public class ModulePickerActivity extends AppCompatActivity implements ModulesSetUpAdapter.OnListItemSelectedListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private int creditsTotal = 0;
    private TextView tvCreditsTotal;
    private MenuItem menuItem;

    /**
     * carries the tab layout showing the modules available
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_picker);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Select Modules");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        tvCreditsTotal = (TextView) findViewById(R.id.tvCreditsTotal);
        tvCreditsTotal.setText("Credits: " + creditsTotal + "/120");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    //callback listener from fragments to update number of credits
    @Override
    public void onModuleSelected(int credits) {
        creditsTotal += credits;
        tvCreditsTotal.setText("Credits: " + creditsTotal + "/120");
        if(creditsTotal == 120){
            menuItem.setIcon(getResources().getDrawable(R.drawable.ic_done_arrow_white));
        } else {
            menuItem.setIcon(getResources().getDrawable(R.drawable.ic_done_arrow_greyed));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_module_picker, menu);
        menuItem = menu.findItem(R.id.action_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            //checks to see the required number of credits are made available
            if(creditsTotal == 120){
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else{
                Toast.makeText(this, "Please make sure to select 120 credits worth of modules", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
