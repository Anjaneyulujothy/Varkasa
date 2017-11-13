package com.varkasa.varkasa.varkasa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);



    }

    public void create(View view)
    {


        Intent i =new Intent(StartActivity.this,RegisterActivity.class);

        startActivity(i);
    }

    public void loginPage(View view)
    {


        Intent i =new Intent(StartActivity.this,LoginActivity.class);



        startActivity(i);

    }
}
