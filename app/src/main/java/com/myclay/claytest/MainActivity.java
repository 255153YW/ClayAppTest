package com.myclay.claytest;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.myclay.claytest.Fragments.DoorDetailsFragment;
import com.myclay.claytest.Fragments.HistoryFragment;
import com.myclay.claytest.Fragments.HomeFragment;
import com.myclay.claytest.Fragments.LoginFragment;
import com.myclay.claytest.Fragments.ManageDoorFragment;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHomeFragment();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null) {
            SharedPreferences settings = getSharedPreferences("SharedPref", MODE_PRIVATE);
            String auth = settings.getString("authToken", null);

            if(auth == null){
                showLoginFragment();
            }
            else{
                //check stored credential with server then show menu
                showHomeFragment();

            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fragmentManager = getFragmentManager();

            Fragment home_fragment = fragmentManager.findFragmentByTag("home");
            Fragment login_fragment = fragmentManager.findFragmentByTag("login");

            if (home_fragment != null && home_fragment.isVisible()) {
                //this.finishAffinity();
                finish();
            }
            else if (login_fragment != null && login_fragment.isVisible()) {
                //this.finishAffinity();
                finish();
            }
            else if(fragmentManager.getBackStackEntryCount() != 0) {
                fragmentManager.popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
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
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        SharedPreferences settings = getSharedPreferences("SharedPref", MODE_PRIVATE);
        String auth = settings.getString("authToken", null);

        if(auth == null){
            showLoginFragment();
        }
        else{
            switch(id){
                case R.id.nav_home:
                    showHomeFragment();
                    break;
                case R.id.nav_manage:
                    showManageDoorFragment();
                    break;
                case R.id.nav_history:
                    showHistoryFragment();
                    break;
                case R.id.nav_settings:
                    break;
                case R.id.nav_logout:
                    logout();
                    break;
                default:
                    System.out.println("id not handled: " + id);
                    break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showHomeFragment(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        HomeFragment home_fragment = new HomeFragment();
        transaction.replace(R.id.main_viewport, home_fragment, "home");
        transaction.addToBackStack("home");
        transaction.commit();
    }

    public void showLoginFragment(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        LoginFragment login_fragment = new LoginFragment();
        transaction.replace(R.id.main_viewport, login_fragment, "login");
        transaction.addToBackStack("login");
        transaction.commit();
    }

    public void showManageDoorFragment(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        ManageDoorFragment manage_fragment = new ManageDoorFragment();
        transaction.replace(R.id.main_viewport, manage_fragment, "manage");
        transaction.addToBackStack("manage");
        transaction.commit();
    }

    public void showHistoryFragment(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        HistoryFragment history_fragment = new HistoryFragment();
        transaction.replace(R.id.main_viewport, history_fragment, "history");
        transaction.addToBackStack("history");
        transaction.commit();
    }

    public void showDetailsFragment(JSONObject selectedDoor){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        DoorDetailsFragment details_fragment = DoorDetailsFragment.newInstance(selectedDoor);
        transaction.replace(R.id.main_viewport, details_fragment, "doorDetails");
        transaction.addToBackStack("doorDetails");
        transaction.commit();
    }

    private void logout(){
        SharedPreferences settings = getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        // Commit the edits!
        editor.commit();
        showLoginFragment();
    }

}
