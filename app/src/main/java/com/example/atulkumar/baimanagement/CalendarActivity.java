package com.example.atulkumar.baimanagement;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView noProfileYet;
    private CalendarView calendarView;
    private TextView gm;
    private TextView lu;
    private TextView di;
    private FirebaseDatabase mDatabase;

    private CheckBox checkBreak;
    private CheckBox checkLu;
    private CheckBox checkDi;

    static boolean b = false;
    static boolean l = false;
    static boolean d = false;

    private Button saveButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance();
        noProfileYet = (TextView)findViewById(R.id.no_profile_text);

        calendarView = (CalendarView)findViewById(R.id.calendar);
        gm = (TextView)findViewById(R.id.breakfast);
        lu = (TextView)findViewById(R.id.lunch);
        di = (TextView)findViewById(R.id.dinner);

        checkBreak = (CheckBox)findViewById(R.id.check_breakfast);
        checkLu = (CheckBox)findViewById(R.id.check_lunch);
        checkDi = (CheckBox)findViewById(R.id.check_dinner);

        saveButton = (Button)findViewById(R.id.save_button);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                onChange(dayOfWeek);

            }
        });


        if(false) {
            noProfileYet.setVisibility(View.VISIBLE);
            noProfileYet.setText("No profile exits yet, Create new Profile");
            noProfileYet.setTextColor(getResources().getColor(R.color.errorColor));
        }else{
            showProfile(/*reference*/);
        }






        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onResume(){
        super.onResume();




    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?

        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.check_breakfast:
                if (checked){
                    b = true;
                } else{
                    b = false;
                }
                break;
            case R.id.check_lunch:
                if (checked){
                    l = true;
                } else{
                    l = false;
                }
                break;
            case R.id.check_dinner:
                if(checked){
                    d = true;
                }else{
                    d = false;
                }

                break;
            // TODO: Veggie sandwich
        }

        Toast.makeText(this," b->"+b+" l->" +l+" d->"+d,Toast.LENGTH_SHORT).show();
        if(d|| b || l ){
            saveButton.setVisibility(View.VISIBLE);
        }else{
            saveButton.setVisibility(View.GONE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.logout) {
            Logout();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Logout(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(CalendarActivity.this);
        dialog.setMessage("Do you want to logout? ")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAuth.signOut();
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = dialog.create();
        alert.setTitle("Logout");
        alert.show();


    }

    private void onChange(int dayOfWeek){


        gm.setVisibility(View.VISIBLE);
        lu.setVisibility(View.VISIBLE);
        di.setVisibility(View.VISIBLE);

        checkBreak.setVisibility(View.VISIBLE);
        checkLu.setVisibility(View.VISIBLE);
        checkDi.setVisibility(View.VISIBLE);



        if(checkBreak.isChecked()){
            checkBreak.setChecked(false);
        }
        if(checkLu.isChecked()){
            checkLu.setChecked(false);
        }
        if(checkDi.isChecked()){
            checkDi.setChecked(false);
        }




        /*if(dayOfWeek ==  1 || dayOfWeek == 7){
            gm.setVisibility(View.VISIBLE);
            lu.setVisibility(View.VISIBLE);
            di.setVisibility(View.VISIBLE);

            checkBreak.setVisibility(View.VISIBLE);
            checkLu.setVisibility(View.VISIBLE);
            checkDi.setVisibility(View.VISIBLE);
        }else{
            lu.setVisibility(View.GONE);
            di.setVisibility(View.VISIBLE);

            checkLu.setVisibility(View.GONE);
            checkDi.setVisibility(View.VISIBLE);
        }

        //gm.setVisibility(View.VISIBLE);*/

    }


    






    public void showProfile(){

    }
}
