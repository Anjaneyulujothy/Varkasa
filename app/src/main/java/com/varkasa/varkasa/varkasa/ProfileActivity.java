package com.varkasa.varkasa.varkasa;

import android.app.ProgressDialog;
import android.icu.text.DateFormat;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

 private ImageView profileImage;

    private ProgressDialog prograss;

    private DatabaseReference mNotificationDatabase;

    private TextView profileName,profileStatus,friendsCount;
    private Button sendRequest,profile_Decline;
    private DatabaseReference databaseReference;

    private DatabaseReference friendReqestDatabase;

    private FirebaseUser mcurrentuser;

    private String currentstate;

    private DatabaseReference mfriendDatabas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id=getIntent().getStringExtra("user_id");

        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        friendReqestDatabase= FirebaseDatabase.getInstance().getReference().child("Friend_Request");

       mfriendDatabas= FirebaseDatabase.getInstance().getReference().child("Friends");

        mNotificationDatabase=FirebaseDatabase.getInstance().getReference().child("notifications");

        mcurrentuser= FirebaseAuth.getInstance().getCurrentUser();


        profileImage=(ImageView)findViewById(R.id.profile_image);
        profileName=(TextView) findViewById(R.id.profile_displayName);
        profileStatus=(TextView) findViewById(R.id.profile_status);
        friendsCount=(TextView) findViewById(R.id.profile_totalFriends);
         sendRequest=(Button) findViewById(R.id.profile_send_req_btn);

        profile_Decline=(Button) findViewById(R.id.profile_decline_btn);

        currentstate="not_friend";


        prograss=new ProgressDialog(ProfileActivity.this);
        prograss.setTitle("Loading User Data");
        prograss.setMessage("Please wait while we load user data");
        prograss.setCancelable(false);
        prograss.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String disoplay_name=dataSnapshot.child("name").getValue().toString();
                String disoplay_status=dataSnapshot.child("status").getValue().toString();
                String disoplay_image=dataSnapshot.child("image").getValue().toString();

                profileName.setText(disoplay_name);
                profileStatus.setText(disoplay_status );
                Picasso.with(ProfileActivity.this).load(disoplay_image).placeholder(R.drawable.saquib).into(profileImage);

                //Reqest future

                friendReqestDatabase.child(mcurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id))
                        {

                            String req_type=dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received"))
                            {



                                currentstate="req_received";

                                sendRequest.setText("Accept Friend Request");
                                profile_Decline.setVisibility(View.VISIBLE);
                                profile_Decline.setEnabled(true);


                            }

                            else  if(req_type.equals("sent"))
                            {

                                currentstate="req_sent";

                                sendRequest.setText("Cancel Friend Request");

                                profile_Decline.setVisibility(View.INVISIBLE);
                                profile_Decline.setEnabled(false);

                            }

                            prograss.dismiss();

                        }


                            else
                            {

                                mfriendDatabas.child(mcurrentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChild(user_id))
                                        {

                                            currentstate="friends";

                                            sendRequest.setText("UnFriend This persone");

                                            profile_Decline.setVisibility(View.INVISIBLE);
                                            profile_Decline.setEnabled(false);
                                        }
                                        prograss.dismiss();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        prograss.dismiss();

                                    }
                                });
                            }




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
sendRequest.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        sendRequest.setEnabled(false);

        // not Frends state
        if(currentstate.equals("not_friend"))
        {

            friendReqestDatabase.child(mcurrentuser.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
           friendReqestDatabase.child(user_id).child(mcurrentuser.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {

            HashMap<String,String> notification=new HashMap<>();

            notification.put("from",mcurrentuser.getUid());

            notification.put("type","request");

        mNotificationDatabase.child(user_id).push().setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                currentstate="req_sent";

                sendRequest.setText("Cancel Friend Request");

                profile_Decline.setVisibility(View.INVISIBLE);
                profile_Decline.setEnabled(false);

            }
        });


       // Toast.makeText(ProfileActivity.this, "Request Sent successfully", Toast.LENGTH_SHORT).show();

               }
                 });


                    }
                    else
                    {

                        Toast.makeText(ProfileActivity.this, "Faild sending request", Toast.LENGTH_SHORT).show();
                    }
                    sendRequest.setEnabled(true);

                }
            });
        }



        //cansel friend request


        if(currentstate.equals("req_sent"))
        {

            friendReqestDatabase.child(mcurrentuser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {


                    friendReqestDatabase.child(user_id).child(mcurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            sendRequest.setEnabled(true);

                            currentstate="not_friends";

                            sendRequest.setText("Send Friend Request");

                            profile_Decline.setVisibility(View.INVISIBLE);
                            profile_Decline.setEnabled(false);

                        }
                    });
                }
            });
        }



        //req recevied


        if(currentstate.equals("req_received"))
        {

            final String currentDate= DateFormat.getDateTimeInstance().format(new Date());
            mfriendDatabas.child(mcurrentuser.getUid()).child(user_id).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    mfriendDatabas.child(user_id).child(mcurrentuser.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {




                            friendReqestDatabase.child(mcurrentuser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    friendReqestDatabase.child(user_id).child(mcurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            sendRequest.setEnabled(true);

                                            currentstate="not_friend";

                                            sendRequest.setText("UnFriend This persone");
                                            profile_Decline.setVisibility(View.INVISIBLE);
                                            profile_Decline.setEnabled(false);

                                        }
                                    });
                                }
                            });

                        }
                    });

                }
            });


        }



        // ------------ UNFRIENDS ---------

        if(currentstate.equals("friends")){

            mfriendDatabas.child(mcurrentuser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    friendReqestDatabase.child(user_id).child(mcurrentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            sendRequest.setEnabled(true);

                            currentstate="friends";

                            sendRequest.setText("SEND FRIEND REQUEST");

                        }
                    });



                }
            });



        }


    }
});

    }
}
