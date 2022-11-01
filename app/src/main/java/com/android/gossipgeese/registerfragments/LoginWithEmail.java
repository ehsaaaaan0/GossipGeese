package com.android.gossipgeese.registerfragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.gossipgeese.R;


public class LoginWithEmail extends Fragment {
    AppCompatButton emailnext;
    EditText email_next;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginWithEmail() {
        // Required empty public constructor
    }


    public static LoginWithEmail newInstance(String param1, String param2) {
        LoginWithEmail fragment = new LoginWithEmail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_login_with_email, container, false);
        emailnext = view.findViewById(R.id.emailnext);
        email_next = view.findViewById(R.id.email_next);


        emailnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_phone = email_next.getText().toString().trim();
                String register ="email";
                if (TextUtils.isEmpty(email_phone)){
                    Toast.makeText(getContext(), "Please Provide a Valid Email address", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent i = new Intent(getContext(), NameAndPass.class);
                    i.putExtra("email_phone" , email_phone);
                    i.putExtra("register" , register);
                    startActivity(i);
                }
            }
        });

        return view;
    }
}