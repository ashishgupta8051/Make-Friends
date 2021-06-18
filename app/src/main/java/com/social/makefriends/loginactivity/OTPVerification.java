package com.social.makefriends.loginactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
import com.social.makefriends.MainActivity;
import com.social.makefriends.R;
import com.social.makefriends.activity.Home;
import com.social.makefriends.manage.userprofile.UpdateProfile;
import com.social.makefriends.model.UserDetails;
import com.social.makefriends.notification.Token;
import com.social.makefriends.utils.SharedPrefManager;

import java.util.concurrent.TimeUnit;

public class OTPVerification extends AppCompatActivity {
    private TextView number;
    private OtpView otpView;
    private Button btnVerify;
    private String phoneNumber,verifyId,Url;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDetailsRef;
    private SharedPrefManager sharedPrefManager;
    private String loginDetails = "You are login with your 𝐏𝐡𝐨𝐧𝐞 𝐍𝐮𝐦𝐛𝐞𝐫. Your phone number is hidden and nobody can see your phone number.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpvarification);

        getSupportActionBar().setTitle("Otp Verification");
        sharedPrefManager = new SharedPrefManager(this);

        number = findViewById(R.id.number);
        otpView = findViewById(R.id.otp_view);
        btnVerify = findViewById(R.id.btn_verify);

        phoneNumber = getIntent().getStringExtra("number");
        verifyId = getIntent().getStringExtra("verifyId");

        String num = "Verify " + phoneNumber;
        number.setText(num);

        Url = "d";

        AlertDialog.Builder builder = new AlertDialog.Builder(OTPVerification.this);
        View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

        TextView title,message;
        title = view.findViewById(R.id.progressBarTitle);
        message = view.findViewById(R.id.progressBarMessage);

        title.setText("Login With Phone");
        message.setText("Please Wait ....");

        builder.setCancelable(false).setView(view);
        AlertDialog progressDialog = builder.create();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;

        firebaseAuth = FirebaseAuth.getInstance();
        userDetailsRef = FirebaseDatabase.getInstance().getReference("User Details");

        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyId, otp);

                btnVerify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.show();
                                    Check(progressDialog);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(OTPVerification.this, "Verification failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void Check(AlertDialog progressDialog) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userDetailsRef.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                String id = userDetails.getChatBackgroundWall();
                                progressDialog.dismiss();
                                sharedPrefManager.saveWallpaper(id);

                                //get Token Id
                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(new OnCompleteListener<String>() {
                                            @Override
                                            public void onComplete(@NonNull Task<String> task) {
                                                if (task.isSuccessful()) {
                                                    String token = task.getResult();
                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
                                                    Token token1 = new Token(token);
                                                    reference.child(firebaseAuth.getCurrentUser().getUid()).setValue(token1);
                                                }else {
                                                    Toast.makeText(getApplicationContext(), task.getResult().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                progressDialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), Home.class));
                                finish();
                            }else {
                                Log.d("Error","Not found user details");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    String CurrentUserUid = firebaseAuth.getCurrentUser().getUid();
                    String Dob = "",Address = "",Bio = "",ProfilePic = "None",usersName = "";
                    UserDetails userDetails = new UserDetails("","",Dob,Address,Bio,ProfilePic,usersName,loginDetails,CurrentUserUid,"",""
                            ,"",Url,"");
                    databaseReference.setValue(userDetails);
                    sharedPrefManager.saveWallpaper("d");

                    //get Token Id
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (task.isSuccessful()) {
                                        String token = task.getResult();
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
                                        Token token1 = new Token(token);
                                        reference.child(CurrentUserUid).setValue(token1);
                                    }else {
                                        Toast.makeText(getApplicationContext(), task.getResult().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    progressDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), UpdateProfile.class);
                    intent.putExtra("Value","A");
                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplicationContext(), "Login Successful, Please update your Profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),LoginWithPhone.class));
        finish();
    }
}