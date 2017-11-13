package com.varkasa.varkasa.varkasa;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText statuseChange;

    private Button statusbutton;

    private DatabaseReference databaseReference;

    private FirebaseUser currentUser;

    private ProgressDialog prograss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        toolbar=(Toolbar)findViewById(R.id.statusToolbar);
        statuseChange=(EditText)findViewById(R.id.statusName);
        statusbutton=(Button)findViewById(R.id.statubutton);

        currentUser= FirebaseAuth.getInstance().getCurrentUser();

        String uid=currentUser.getUid();

        String settingsTestus=getIntent().getStringExtra("status");


        statuseChange.setText(settingsTestus);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(uid);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        statusbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prograss=new ProgressDialog(StatusActivity.this);
                prograss.setTitle("Saving Changes");
                prograss.setMessage("Please wait while we save the changes");
                prograss.setCancelable(false);
                prograss.show();
                String status=statuseChange.getText().toString().trim();

                databaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {

                            prograss.dismiss();
                        }

                    }
                });
            }
        });
    }
}
