package com.varkasa.varkasa.varkasa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private FirebaseUser currentUser;

    private CircleImageView circleImageView;

    private TextView dispalyName;

    private  TextView statust;

    private Button chamgestatusl;

    private  Button changeimage;

    private StorageReference imageStoragereference;

    private ProgressDialog prograss;

    private static final int GALERY_PICK=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        circleImageView=(CircleImageView)findViewById(R.id.profile_image) ;

        dispalyName=(TextView) findViewById(R.id.textView2) ;
        statust=(TextView) findViewById(R.id.textView4) ;

        chamgestatusl=(Button)findViewById(R.id.changeStatus);
        changeimage=(Button)findViewById(R.id.changeImage);
            imageStoragereference= FirebaseStorage.getInstance().getReference();

        prograss=new ProgressDialog(this);

        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=currentUser.getUid();

        databaseReference= FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);

        databaseReference.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name=dataSnapshot.child("name").getValue().toString().trim();
                String status=dataSnapshot.child("status").getValue().toString().trim();
                final String image=dataSnapshot.child("image").getValue().toString().trim();
                String thumb_image=dataSnapshot.child("thumb_image").getValue().toString().trim();


                dispalyName.setText(name);
                statust.setText(status);
                if(!image.equals("defalt")) {
                  //  Picasso.with(getBaseContext()).load(image).placeholder(R.drawable.saquib).into(circleImageView);

                    Picasso.with(getBaseContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.saquib).into(circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getBaseContext()).load(image).placeholder(R.drawable.saquib).into(circleImageView);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        chamgestatusl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String st=statust.getText().toString().trim();
                Intent i =new Intent(SettingsActivity.this,StatusActivity.class);

                i.putExtra("status",st);

                startActivity(i);
            }
        });

        changeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




          /*      Intent galerryIntent =new Intent();
                galerryIntent.setType("image/*");
                galerryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galerryIntent,"select image"),GALERY_PICK);*/

           CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);


            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALERY_PICK && requestCode==RESULT_OK) {

            Uri imageUrl=data.getData();

            CropImage.activity(imageUrl)
                    .setAspectRatio(1,1)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                prograss.setTitle("Uploading Image..");
                prograss.setMessage("Please wait while we upload and process the image");
                prograss.setCancelable(false);
                prograss.show();
                Uri resultUri = result.getUri();
                final File thimb_filePath=new File(resultUri.getPath());

                String current_user=currentUser.getUid();



                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(60)
                        .compressToBitmap(thimb_filePath);

                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                final byte[] thumb_byte=baos.toByteArray();

                StorageReference filepath=imageStoragereference.child("profile_images").child(current_user+".jpg");

                final StorageReference thumb_filepaths=imageStoragereference.child("profile_images").child("thumbs").child(current_user+".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful())
                        {

                          final String dowloadUrl =task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepaths.putBytes(thumb_byte);

                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_dowloadUrl =thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful())
                                    {


                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put("image", dowloadUrl);
                                        update_hashMap.put("thumb_image", thumb_dowloadUrl);

                                        databaseReference.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful())
                                                {
                                                    prograss.dismiss();

                                                    Toast.makeText(SettingsActivity.this, " Success Uploading", Toast.LENGTH_SHORT).show();

                                                }


                                            }
                                        });
                                    }

                                    else
                                    {

                                        Toast.makeText(SettingsActivity.this, "File is Not uploaded thumbnail", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                        }
                        else
                        {

                            Toast.makeText(SettingsActivity.this, "File is Not uploaded", Toast.LENGTH_SHORT).show();

                        }

                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


}
