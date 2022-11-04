package com.android.gossipgeese.registerfragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gossipgeese.MainActivity;
import com.android.gossipgeese.R;
import com.android.gossipgeese.model.RegisterModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsernameandDateofbirth extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    AppCompatButton submit_final;
    String email_phone, password, full_name, register,d;
    TextView dob_text;
    AppCompatButton dob;
    String verificationID;
    ProgressDialog progressDialog;
    LinearLayout picture;
    CircleImageView setPicture;
    Uri uri;
    int reqCode;

    //date picker
    private CheckBox modeDarkDate;
    private CheckBox modeCustomAccentDate;
    private CheckBox vibrateDate;
    private CheckBox dismissDate;
    private CheckBox titleDate;
    private CheckBox showYearFirst;
    private CheckBox showVersion2;
    private CheckBox switchOrientation;
    private CheckBox limitSelectableDays;
    private CheckBox highlightDays;
    private CheckBox defaultSelection;
    private DatePickerDialog dpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usernameand_dateofbirth);
        submit_final = findViewById(R.id.submit_final);
        dob = findViewById(R.id.dob_btn);
        dob_text = findViewById(R.id.dob);
        picture = findViewById(R.id.liner);
        setPicture = findViewById(R.id.setPicture);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = this.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        //check box extra layouts ingnor
        modeDarkDate = findViewById(R.id.mode_dark_date);
        modeCustomAccentDate = findViewById(R.id.mode_custom_accent_date);
        vibrateDate = findViewById(R.id.vibrate_date);
        dismissDate = findViewById(R.id.dismiss_date);
        titleDate = findViewById(R.id.title_date);
        showYearFirst = findViewById(R.id.show_year_first);
        showVersion2 = findViewById(R.id.show_version_2);
        switchOrientation = findViewById(R.id.switch_orientation);
        limitSelectableDays = findViewById(R.id.limit_dates);
        highlightDays = findViewById(R.id.highlight_dates);
        defaultSelection = findViewById(R.id.default_selection);
        //ignore section ends here.

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Account....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        Intent in = getIntent();
        email_phone = in.getStringExtra("email_phone");
        full_name = in.getStringExtra("Full_Name");
        password = in.getStringExtra("password");
        register = in.getStringExtra("register");



        dob.setOnClickListener(v -> {
            picdate();
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(UsernameandDateofbirth.this)
                        //Dexter.withActivity(MainActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent(Intent.ACTION_PICK);

                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        submit_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeProfile();
            }
        });



    }




    private void picdate() {
        dob.setVisibility(View.GONE);
        dob_text.setVisibility(View.VISIBLE);
        Calendar now = Calendar.getInstance();
        if (defaultSelection.isChecked()) {
            now.add(Calendar.DATE, 7);
        }
            /*
            It is recommended to always create a new instance whenever you need to show a Dialog.
            The sample app is reusing them because it is useful when looking for regressions
            during testing
             */
        if (dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    UsernameandDateofbirth.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            dpd.initialize(
                    UsernameandDateofbirth.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        }

        dpd.setThemeDark(modeDarkDate.isChecked());
        dpd.vibrate(vibrateDate.isChecked());
        dpd.dismissOnPause(dismissDate.isChecked());
        dpd.showYearPickerFirst(showYearFirst.isChecked());
        dpd.setVersion(showVersion2.isChecked() ? DatePickerDialog.Version.VERSION_2 : DatePickerDialog.Version.VERSION_1);
        if (modeCustomAccentDate.isChecked()) {
            dpd.setAccentColor(Color.parseColor("#9C27B0"));
        }
        if (titleDate.isChecked()) {
            dpd.setTitle("DatePicker Title");
        }
        if (highlightDays.isChecked()) {
            Calendar date1 = Calendar.getInstance();
            Calendar date2 = Calendar.getInstance();
            date2.add(Calendar.WEEK_OF_MONTH, -1);
            Calendar date3 = Calendar.getInstance();
            date3.add(Calendar.WEEK_OF_MONTH, 1);
            Calendar[] days = {date1, date2, date3};
            dpd.setHighlightedDays(days);
        }
        if (limitSelectableDays.isChecked()) {
            Calendar[] days = new Calendar[13];
            for (int i = -6; i < 7; i++) {
                Calendar day = Calendar.getInstance();
                day.add(Calendar.DAY_OF_MONTH, i * 2);
                days[i + 6] = day;
            }
            dpd.setSelectableDays(days);
        }
        if (switchOrientation.isChecked()) {
            if (dpd.getVersion() == DatePickerDialog.Version.VERSION_1) {
                dpd.setScrollOrientation(DatePickerDialog.ScrollOrientation.HORIZONTAL);
            } else {
                dpd.setScrollOrientation(DatePickerDialog.ScrollOrientation.VERTICAL);
            }
        }

        dpd.setOnCancelListener(dialog -> {
            Log.d("DatePickerDialog", "Dialog was cancelled");
            dpd = null;
        });
        dpd.show(getSupportFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dpd = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag("Datepickerdialog");
        if(dpd != null) dpd.setOnDateSetListener(this);
    }


    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date =+dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        dob_text.setText(date);
        dpd = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (data.getData()!=null){
                uri = data.getData();
                setPicture.setImageURI(uri);
                reqCode = 1;

            }
        }

    }

    private void completeProfile() {
        if (reqCode==1){
            d = dob_text.getText().toString().trim();
            if (TextUtils.isEmpty(d)){
                d = "Not Selected";
            }else{
                if (Objects.equals(register, "email")){
                    progressDialog.show();
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email_phone,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            final StorageReference reference = FirebaseStorage.getInstance().getReference().child("profile").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String id = FirebaseAuth.getInstance().getUid();
                                            String image = uri.toString();
                                            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                                                @Override
                                                public void onSuccess(String s) {

                                            RegisterModel model = new RegisterModel(id,full_name,email_phone,password,image,d,"12:00",s);
                                            FirebaseDatabase.getInstance().getReference().child("users").child(id).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(UsernameandDateofbirth.this, "My Notification");
                                                    builder.setContentTitle("Your Account is Registered SuccessFully");
                                                    builder.setContentText("Let's Have Some Chat With Each Other");
                                                    builder.setSmallIcon(R.drawable.ic_gossipgeese);
                                                    builder.setAutoCancel(true);

                                                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(UsernameandDateofbirth.this);
                                                    managerCompat.notify(1, builder.build() );
                                                    startActivity(new Intent(UsernameandDateofbirth.this, MainActivity.class));

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(UsernameandDateofbirth.this, "Unable to Register Your Account...!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
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

                        }
                    });


                }else{
                    String image = uri.toString();
                    startActivity(new Intent(UsernameandDateofbirth.this, EnterOTP.class)
                            .putExtra("phone",email_phone)
                            .putExtra("full_name",full_name)
                            .putExtra("password",password)
                            .putExtra("date",dob_text.getText().toString().trim())
                            .putExtra("image",image));
                }
            }
        }else{
            Toast.makeText(this, "Please Select Profile Picture", Toast.LENGTH_SHORT).show();
        }
    }

}