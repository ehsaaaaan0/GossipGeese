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
import com.hbb20.CountryCodePicker;

public class LoginWithPhone extends Fragment {
    EditText getPhoneNumber;
    AppCompatButton phone_next_;
    String register;
    CountryCodePicker ccp;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public LoginWithPhone() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LoginWithPhone newInstance(String param1, String param2) {
        LoginWithPhone fragment = new LoginWithPhone();
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
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_login_with_phone, container, false);
        getPhoneNumber = view.findViewById(R.id.getPhoneNumber);
        phone_next_ = view.findViewById(R.id.phone_next_);
        ccp = view.findViewById(R.id.countryCode);
        ccp.registerCarrierNumberEditText(getPhoneNumber);

        phone_next_.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String email_phone = getPhoneNumber.getText().toString().trim();
                register = "phone";
                if (TextUtils.isEmpty(email_phone)){
                    getPhoneNumber.setError("Please Enter Valid Phone Number");
                    Toast.makeText(getContext(), "Please Enter Valid Phone Number", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent i = new Intent(getContext(), NameAndPass.class);
                    i.putExtra("email_phone" , ccp.getFullNumberWithPlus().replace(" ",""));
                    i.putExtra("register", register);
                    startActivity(i);

                }
            }
        });
        return view;
    }
}