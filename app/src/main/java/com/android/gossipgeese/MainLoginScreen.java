package com.android.gossipgeese;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.gossipgeese.model.RegisterModel;
import com.android.gossipgeese.registerfragments.EnterOTP;
import com.android.gossipgeese.registerfragments.NameAndPass;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

public class MainLoginScreen extends AppCompatActivity {
    TextView register;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    ProgressDialog dialog;
    Button btnGoogle,btn_fb;
    EditText getPhone, getEmail,getPass;
    private static final String EMAIL = "email";
    LoginButton loginButton;
    CallbackManager callbackManager;
    AppCompatButton phone, email;
    View phonev, emailv;
    Button signin;
    int check=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login_screen);
        register = findViewById(R.id.clickSignIn);
        signin = findViewById(R.id.btSignIn);
        phone = findViewById(R.id.phoneNumber);
        email = findViewById(R.id.emailAddress);
        phonev = findViewById(R.id.phonenumberview);
        emailv = findViewById(R.id.emailView);
        getEmail = findViewById(R.id.EmailAddress);
        getPass = findViewById(R.id.etPassword);
        getPhone = findViewById(R.id.number);
        btn_fb = findViewById(R.id.btn_fb);
        btnGoogle = findViewById(R.id.btngoogle);
        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Logging in...!");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
        emailv.setBackgroundColor(Color.parseColor("#000000"));
        phonev.setBackgroundColor(Color.parseColor("#DCDCDC"));

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check =1;
                phonev.setBackgroundColor(Color.parseColor("#000000"));
                emailv.setBackgroundColor(Color.parseColor("#DCDCDC"));
                getPass.setVisibility(View.GONE);
                getEmail.setVisibility(View.GONE);
                getPhone.setVisibility(View.VISIBLE);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check=2;
                emailv.setBackgroundColor(Color.parseColor("#000000"));
                phonev.setBackgroundColor(Color.parseColor("#DCDCDC"));
                getPhone.setVisibility(View.GONE);
                getEmail.setVisibility(View.VISIBLE);
                getPass.setVisibility(View.VISIBLE);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check==2){
                    String email = getEmail.getText().toString().trim();
                    String pass = getPass.getText().toString().trim();
                    if (TextUtils.isEmpty(email)){
                        getEmail.setError("Please Enter Email");
                    }else if (TextUtils.isEmpty(pass)){
                        getPass.setError("Please Enter Password");
                    }else{
                        dialog.show();
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                dialog.dismiss();
                                startActivity(new Intent(MainLoginScreen.this,MainActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(MainLoginScreen.this, "Unable to Find Account", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else{
                    String phoneNumber = getPhone.getText().toString().trim();
                    if (TextUtils.isEmpty(phoneNumber)){
                        getPhone.setError("Please Enter Phone Number");
                    }else{
                        if (phoneNumber.startsWith("0")){
                            phoneNumber = phoneNumber.substring(1);
                            Intent i = new Intent(MainLoginScreen.this, EnterOTP.class);
                            i.putExtra("phone" ,"+92"+phoneNumber);
                            startActivity(i);
                        }else{
                            Intent i = new Intent(MainLoginScreen.this, EnterOTP.class);
                            i.putExtra("phone" ,"+92"+phoneNumber);
                            startActivity(i);
                        }
                    }
                }
            }
        });



        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                } else {
                                    // App code
                                    handleFacebookAccessToken(loginResult.getAccessToken());
                                }
                            }
                        }).executeAsync();

            }

            @Override
            public void onCancel() {
                Log.d("onCancel", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("onError", "facebook:onError");
                Toast.makeText(MainLoginScreen.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        btn_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });

        // Initialize sign in options
        // the client-id is copied form
        // google-services.json file
        GoogleSignInOptions googleSignInOptions=new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("253041757426-ub7htd0s92ab655vtrjssdj2drf3uq3e.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Initialize sign in client
        googleSignInClient= GoogleSignIn.getClient(MainLoginScreen.this ,googleSignInOptions);

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize sign in intent
                Intent intent=googleSignInClient.getSignInIntent();
                // Start activity for result
                startActivityForResult(intent,10099102);
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainLoginScreen.this, MainSignupScreen.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Check condition
        if(requestCode==10099102)
        {
            super.onActivityResult(requestCode, resultCode, data);
            // When request code is equal to 100
            // Initialize task
            Task<GoogleSignInAccount> signInAccountTask=GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            // check condition
            if(signInAccountTask.isSuccessful())
            {

                try {
                    GoogleSignInAccount googleSignInAccount=signInAccountTask
                            .getResult(ApiException.class);
                    if(googleSignInAccount!=null)
                    {
                        dialog.show();

                        AuthCredential authCredential= GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken()
                                        ,null);
                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // Check condition
                                        if(task.isSuccessful())
                                        {
                                            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
                                                @Override
                                                public void onSuccess(String s) {

                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            String image = user.getPhotoUrl().toString();
                                            String id = FirebaseAuth.getInstance().getUid();
                                            String name = user.getDisplayName();
                                            String email = user.getEmail();
                                            RegisterModel model = new RegisterModel(id,name,email,image,"12:00",s);
                                            FirebaseDatabase.getInstance().getReference().child("users").child(id).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    dialog.dismiss();
                                               startActivity(new Intent(MainLoginScreen.this, MainActivity.class));
                                                }
                                            });
                                                }
                                            });

                                        }

                                        else
                                        {
                                            dialog.dismiss();
                                            Toast.makeText(MainLoginScreen.this, "Error. Try Again Latter", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }
                catch (ApiException e)
                {
                    e.printStackTrace();
                }
            }
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void printHashKey() {

        // Add code to print out the key hash
        try {

            PackageInfo info
                    = getPackageManager().getPackageInfo(
                    "com.android.gossipgeese",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:",
                        Base64.encodeToString(
                                md.digest(),
                                Base64.DEFAULT));
                Log.d("hashajaaaa",Base64.encodeToString(
                        md.digest(),
                        Base64.DEFAULT));
                Toast.makeText(MainLoginScreen.this, Base64.encodeToString(
                        md.digest(),
                        Base64.DEFAULT), Toast.LENGTH_SHORT).show();
            }
        }

        catch (PackageManager.NameNotFoundException e) {
            Log.d("haserror", e.getMessage());
        }

        catch (NoSuchAlgorithmException e) {
        }

    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("creating", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Toast.makeText(MainLoginScreen.this, user.getEmail()+"\n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainLoginScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            startActivity(new Intent(MainLoginScreen.this,MainActivity.class));
        }
    }
}