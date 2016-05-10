package com.floo.mpm_survey;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.floo.fragment.Fragment_Home;


public class MainActivity extends AppCompatActivity /*implements AsyncResponse */{
    DrawerLayout drawer;
    Toolbar toolbar;
    ActionBar actionBar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Stetho.initializeWithDefaults(this);

        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null){
            setupNavigationDrawerContent(navigationView);
        }
        setupNavigationDrawerContent(navigationView);

        //Fragment pertama
        setFragment(0);
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case android.R.id.home:
               drawer.openDrawer(GravityCompat.START);
               return true;
           /*case R.id.action_sync:
               DataFetcherTask task = new DataFetcherTask(this);
               task.delegate = this;
               progressDialog = ProgressDialog.show(this, "Mohon Tunggu",
                       "Inisialisasi Data.....", true);

               return true;*/
       }
        return super.onOptionsItemSelected(item);
    }

    private void setupNavigationDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener(){
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.nav_Survey:
                                item.setChecked(true);
                                setFragment(0);
                                drawer.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.nav_Sigout:
                                Intent res=new Intent(MainActivity.this, LoginActivity.class);
                                finish();
                                startActivity(res);
                                return true;
                        }
                        return true;
                    }
                }
        );
    }

    public void setFragment(int position){
        FragmentManager fm;
        FragmentTransaction ft;
        switch (position){
            case 0:
                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();
                Fragment_Home homee = new Fragment_Home();
                ft.replace(R.id.content_fragment, homee);
                ft.commit();
                break;
        }
    }

    /*
    @Override
    public void processFinish(String output) {

    }
*/
   /* public void onBackPressed(){
            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    Intent intent = new Intent(
                                            Intent.ACTION_MAIN);
                                    intent.addCategory(Intent.CATEGORY_HOME);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    finish();
                                    startActivity(intent);
                                }
                            }).create().show();

    }*/


}
