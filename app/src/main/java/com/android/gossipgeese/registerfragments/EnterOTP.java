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
import com.google.firebase.auth.PhoneAuthOptions;
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
    String phone,full_name,password, date,username;
    String OTPid;
    FirebaseAuth mAuth;
    ProgressDialog creating ;
    private String verificationId;

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
        String phoneNumber = getIntent().getStringExtra("phone");
        full_name = i.getStringExtra("full_name");
        password = i.getStringExtra("password");
        date = i.getStringExtra("date");
        username = i.getStringExtra("image");
        phone = phoneNumber;
        sendVerificationCode(phoneNumber);
        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creating.show();
                if (getotp.getText().toString().isEmpty()){
                    creating.dismiss();
                    Toast.makeText(EnterOTP.this, "Please Enter OTP", Toast.LENGTH_SHORT).show();
                }else{
                    verifyCode(getotp.getText().toString());
                }
            }
        });
    }

    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    // initializing our callbacks for on
    // verification callback method.
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // when we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have already created.
            verificationId = s;
        }

        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            final String code = phoneAuthCredential.getSmsCode();

            // checking if the code
            // is null or not.
            if (code != null) {
                // if the code is not null then
                // we are setting that code to
                // our OTP edittext field.
                getotp.setText(code);

                // after setting this code
                // to OTP edittext field we
                // are calling our verifycode method.
                verifyCode(code);
            }
        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            Toast.makeText(EnterOTP.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    // below method is use to verify code from Firebase.
    private void verifyCode(String code) {
        // below line is used for getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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

                                                                RegisterModel model = new RegisterModel(id,full_name,phone,password,image,date,"12:00",s);
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
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(EnterOTP.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

