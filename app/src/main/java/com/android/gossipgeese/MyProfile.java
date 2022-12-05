package com.android.gossipgeese;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {
    ImageView back;
    CircleImageView profile;
    EditText name,bio,mail;
    AppCompatButton update;
    ProgressDialog dialog;
    String naemS ,bioS,mailS;
    Uri sFile;
    int reqCode =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        back = findViewById(R.id.back);
        profile = findViewById(R.id.profile_image);
        name = findViewById(R.id.etname);
        bio = findViewById(R.id.etabout);
        mail= findViewById(R.id.etemail);
        update = findViewById(R.id.setup);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Setting-Up Profile");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    dialog.show();
                    Picasso.get().load(snapshot.child("image").getValue(String.class)).into(profile);
                    name.setText(snapshot.child("name").getValue(String.class));
                    mail.setText(snapshot.child("mail").getValue(String.class));
                    bio.setText(snapshot.child("bio").getValue(String.class));
                    naemS = snapshot.child("name").getValue(String.class);
                    mailS = snapshot.child("mail").getValue(String.class);
                    bioS = snapshot.child("bio").getValue(String.class);
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*"); // */*
                startActivityForResult(intent , 45 );
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpProfile();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getData() != null){
            sFile = data.getData();
            profile.setImageURI(sFile);
            reqCode=1;
        }
    }


    private void setUpProfile() {
        if (reqCode==1){
            //Image Selected
            dialog.show();
            final StorageReference reference = FirebaseStorage.getInstance().getReference().child("profile")
                    .child(FirebaseAuth.getInstance().getUid());
            reference.putFile(sFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                                    .child("image").setValue(uri.toString());
                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("name").setValue(name.getText().toString().trim());
                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("mail").setValue(mail.getText().toString().trim());
                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("bio").setValue(bio.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MyProfile.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                }
            });
        }else{
            //Image Not Selected
            dialog.show();
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("name").setValue(name.getText().toString().trim());
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("bio").setValue(bio.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dialog.dismiss();
                }
            });
        }
    }
}