package com.varkasa.varkasa.varkasa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText mDesplayName;

    private EditText eemail;

    private EditText passWord;

    private Button mCreateButton;

    private FirebaseAuth mAuth;

    private Toolbar mToolbar;

    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //mToolbar set

        mToolbar=(Toolbar)findViewById(R.id.reg_toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Creat Account");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressDialog=new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();

        mDesplayName=(EditText) findViewById(R.id.reg_desplay_name);
        eemail=(EditText) findViewById(R.id.reg_email);
        passWord=(EditText) findViewById(R.id.reg_password);

        mCreateButton=(Button) findViewById(R.id.regButton);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desplayName= mDesplayName.getText().toString();
                String emails= eemail.getText().toString();
                String passwords= passWord.getText().toString();

                if(!TextUtils.isEmpty(desplayName)||!TextUtils.isEmpty(emails)|| !TextUtils.isEmpty(passwords) )
                {
                    progressDialog.setTitle("Registering user");
                    progressDialog.setMessage("Please wait while we create your account !");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    register_user(desplayName,emails,passwords);


                }



            }


        });
    }

    private void register_user(final String desplayName, String emails, String passwords) {


    mAuth.createUserWithEmailAndPassword(emails,passwords).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

            if(task.isComplete())
            {

                 FirebaseUser current_user=FirebaseAuth.getInstance().getCurrentUser();

                String uid=current_user.getUid();

                databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                HashMap<String,String> userMap=new HashMap<>();

                userMap.put("name",desplayName);

                userMap.put("status","i am using hash map");

                userMap.put("image","defalt");

                userMap.put("thumb_image","defalt");

                databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {

                            progressDialog.dismiss();
                            Intent i =new Intent(RegisterActivity.this,MainActivity.class);

                            startActivity(i);
                            finish();

                        }
                    }
                });



             /*    */



            }
            else
            {
                progressDialog.hide();

                Toast.makeText(RegisterActivity.this, "you got some error please try again ", Toast.LENGTH_SHORT).show();
            }

        }
    });
    }

}
