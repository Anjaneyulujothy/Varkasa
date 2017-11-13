package com.varkasa.varkasa.varkasa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText email;

    private  EditText password;

    private Button login;

    private ProgressDialog prograss;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        toolbar=(Toolbar)findViewById(R.id.login_toolbar);

        email=(EditText)findViewById(R.id.login_mail);

        password=(EditText)findViewById(R.id.login_password);

        login=(Button)findViewById(R.id.loginButton);

        prograss=new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String e=email.getText().toString().trim();

                String p=password.getText().toString().trim();
                if(!TextUtils.isEmpty(e) || !TextUtils.isEmpty(p))
                {
                    prograss.setTitle("Loging In");
                    prograss.setMessage("Please wait while we check your credentials.");
                    prograss.setCancelable(false);
                    prograss.show();
                    userLogin(e,p);
                }
            }
        });



        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void userLogin(String e, String p) {

        mAuth.signInWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {

                    prograss.dismiss();
                    Intent i =new Intent(LoginActivity.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
                else
                {
                    prograss.hide();

                    Toast.makeText(LoginActivity.this, "you got some error please try again ", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
