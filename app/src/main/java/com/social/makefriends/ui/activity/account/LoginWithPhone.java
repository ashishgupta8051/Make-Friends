package com.social.makefriends.ui.activity.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.social.makefriends.R;
import com.social.makefriends.utils.Connection;

import java.util.concurrent.TimeUnit;

public class LoginWithPhone extends AppCompatActivity {
    private EditText phoneNumber;
    private CountryCodePicker ccp;
    private Button btnSubmit;
    private FirebaseAuth mAuth;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_phone);
        Connection.checkInternet(LoginWithPhone.this,this);

        getSupportActionBar().setTitle("Login With Phone");

        mAuth = FirebaseAuth.getInstance();

        phoneNumber = findViewById(R.id.et_number);
        ccp = findViewById(R.id.ccp);

        btnSubmit = findViewById(R.id.btn_submit);

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginWithPhone.this);
        View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

        TextView title,message;
        title = view.findViewById(R.id.progressBarTitle);
        message = view.findViewById(R.id.progressBarMessage);

        title.setText("Login With Phone");
        message.setText("Sending OTP ....");

        builder.setCancelable(false).setView(view);
        progressDialog = builder.create();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;

        ccp.registerCarrierNumberEditText(phoneNumber);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ccp.isValidFullNumber()) {
                    //Start phone auth here
                    progressDialog.show();
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(ccp.getFullNumberWithPlus().replace(" ", ""))
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(LoginWithPhone.this)
                            .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Please enter correct number", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    super.onCodeSent(s, forceResendingToken);
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(LoginWithPhone.this, OTPVerification.class);
                                    intent.putExtra("number", ccp.getFullNumberWithPlus().replace(" ", ""));
                                    intent.putExtra("verifyId",s);
                                    startActivity(intent);
                                    finish();
                                }
                            }).build();

                    PhoneAuthProvider.verifyPhoneNumber(options);
                } else {
                    Toast.makeText(LoginWithPhone.this, "Please Check the number!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }


}