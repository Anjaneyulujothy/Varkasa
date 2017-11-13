package com.varkasa.varkasa.varkasa;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private Toolbar mToolBar;

    private ViewPager viewPager;
    private SectionsPageAdapter sectionsPageAdapter;


    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



     mToolBar=(Toolbar)findViewById(R.id.mainpagetoolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("I Chat");


        viewPager=(ViewPager)findViewById(R.id.mainViewPager);

        sectionsPageAdapter=new SectionsPageAdapter(getSupportFragmentManager());


        viewPager.setAdapter(sectionsPageAdapter);

        tabLayout=(TabLayout)findViewById(R.id.main_tabs);

        tabLayout.setupWithViewPager(viewPager);


        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
       FirebaseUser currentuser = mAuth.getCurrentUser();

        if(currentuser==null)
        {

            sendToStart();
        }
    }

    private void sendToStart() {

        Intent i=new Intent(MainActivity.this,StartActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.logout)
        {

            FirebaseAuth.getInstance().signOut();

            sendToStart();
        }


        if(item.getItemId()==R.id.main_settings)
        {


            Intent setingsActivity=new Intent(MainActivity.this,SettingsActivity.class);

            startActivity(setingsActivity);

        }


        if(item.getItemId()==R.id.main_allusers)
        {

            Intent setingsActivity=new Intent(MainActivity.this,UsersActivity.class);

            startActivity(setingsActivity);
          

        }



        return true;
    }
}
