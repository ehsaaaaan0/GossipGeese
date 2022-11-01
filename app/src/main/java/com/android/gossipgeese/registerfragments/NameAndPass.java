package com.android.gossipgeese.registerfragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.gossipgeese.R;


public class NameAndPass extends AppCompatActivity {
    AppCompatButton create_username;
    EditText full_name,password_register;
    String email_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_and_pass);
        create_username = findViewById(R.id.create_username);
        full_name = findViewById(R.id.full_name);
        password_register = findViewById(R.id.password_register);

        Intent i = getIntent();
        email_phone = i.getStringExtra("email_phone");
        String register = i.getStringExtra("register");

        create_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fname = full_name.getText().toString().trim();
                String pass = password_register.getText().toString().trim();
                if (TextUtils.isEmpty(fname)){
                    Toast.makeText(NameAndPass.this, "Please Enter your Full Name", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(pass)){
                    Toast.makeText(NameAndPass.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(NameAndPass.this, UsernameandDateofbirth.class);
                    intent.putExtra("email_phone", email_phone);
                    intent.putExtra("Full_Name", fname);
                    intent.putExtra("password", pass);
                    intent.putExtra("register", register);
                    startActivity(intent);
                }
            }
        });
    }
}