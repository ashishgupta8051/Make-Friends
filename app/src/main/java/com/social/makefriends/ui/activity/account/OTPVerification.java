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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
import com.social.makefriends.R;
import com.social.makefriends.ui.activity.home.Home;
import com.social.makefriends.ui.activity.userprofile.UpdateProfile;
import com.social.makefriends.model.UserDetails;
import com.social.makefriends.utils.Connection;
import com.social.makefriends.utils.SharedPrefManager;

import java.util.HashMap;

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

        Connection.checkInternet(OTPVerification.this,this);

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
                        progressDialog.show();
                        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
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

                    HashMap<String,Object> addUserDetails = new HashMap<>();
                    addUserDetails.put("userName","");
                    addUserDetails.put("userEmail","");
                    addUserDetails.put("userDob","");
                    addUserDetails.put("userAddress","");
                    addUserDetails.put("userBio","");
                    addUserDetails.put("userProfileImageUrl","None");
                    addUserDetails.put("usersName","");
                    addUserDetails.put("loginDetails",loginDetails);
                    addUserDetails.put("userUid",CurrentUserUid);
                    addUserDetails.put("onlineDate","");
                    addUserDetails.put("onlineTime","");
                    addUserDetails.put("onlineStatus","");
                    addUserDetails.put("chatBackgroundWall","d");
                    addUserDetails.put("userPassword","");


                    databaseReference.setValue(addUserDetails);
                    sharedPrefManager.saveWallpaper("d");

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