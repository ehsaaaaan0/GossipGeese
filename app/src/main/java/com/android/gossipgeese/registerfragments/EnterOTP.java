package com.android.gossipgeese.registerfragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.gossipgeese.MainActivity;
import com.android.gossipgeese.R;
import com.android.gossipgeese.model.RegisterModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class EnterOTP extends AppCompatActivity {
    EditText getotp;
    AppCompatButton verifyOTP;
    String phoneNumber,full_name,password, date,username;
    String OTPid;
    FirebaseAuth mAuth;
    ProgressDialog creating ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        getotp = findViewById(R.id.edittext_otp);
        verifyOTP = findViewById(R.id.verifyOTP);
        mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = this.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        creating = new ProgressDialog(this);
        creating.setMessage("Creating your Account");
        creating.setCancelable(false);
        creating.setCanceledOnTouchOutside(false);



        Intent i = getIntent();
        phoneNumber = i.getStringExtra("phone");
        full_name = i.getStringExtra("full_name");
        password = i.getStringExtra("password");
        date = i.getStringExtra("date");
        username = i.getStringExtra("image");
        initiateOTP();

        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creating.show();
                if (getotp.getText().toString().isEmpty()){
                    creating.dismiss();
                    Toast.makeText(EnterOTP.this, "Please Enter OTP", Toast.LENGTH_SHORT).show();
                }else{
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(OTPid, getotp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    private void initiateOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        OTPid=s;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        startActivity(new Intent(EnterOTP.this, MainActivity.class));
                                    }else{




                                        Uri uri = Uri.parse(username);
                                        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("profile").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String image = uri.toString();
                                                        String id = FirebaseAuth.getInstance().getUid();
                                                        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                                                            @Override
                                                            public void onSuccess(String s) {

                                                        RegisterModel model = new RegisterModel(id,full_name,phoneNumber,password,image,date,"12:00",s);
                                                        FirebaseDatabase.getInstance().getReference().child("users").child(id).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                creating.dismiss();

                                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(EnterOTP.this, "My Notification");
                                                                builder.setContentTitle("Your Account is Registered SuccessFully");
                                                                builder.setContentText("Let's Have Some Chat with Each Other");
                                                                builder.setSmallIcon(R.drawable.ic_gossipgeese);
                                                                builder.setAutoCancel(true);

                                                                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(EnterOTP.this);
                                                                managerCompat.notify(1, builder.build() );
                                                                startActivity(new Intent(EnterOTP.this, MainActivity.class));
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                creating.dismiss();
                                                                Toast.makeText(EnterOTP.this, "Account Registered, But Unable to Save Information", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });


                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                creating.dismiss();
                                                Toast.makeText(EnterOTP.this, "Unable to Save Information", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            creating.dismiss();
                            Toast.makeText(EnterOTP.this, "Try again latter", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}