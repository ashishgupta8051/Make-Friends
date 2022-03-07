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
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.social.makefriends.R;
import com.social.makefriends.ui.activity.userprofile.UpdateProfile;
import com.social.makefriends.utils.Connection;
import com.social.makefriends.utils.SharedPrefManager;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {
    private TextView Signin;
    private EditText name,email,password;
    private Button register;
    private FirebaseAuth firebaseAuth;
    private String Name,Email,Password;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Connection.checkInternet(SignUp.this,this);

        sharedPrefManager = new SharedPrefManager(this);

        Signin = (TextView)findViewById(R.id.signin);
        register = (Button)findViewById(R.id.register);

        name = (EditText)findViewById(R.id.name);
        email = (EditText)findViewById(R.id.Email);
        password = (EditText)findViewById(R.id.Password);

        firebaseAuth = FirebaseAuth.getInstance();

        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this,Login.class));
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void validate() {
         Name = name.getText().toString().trim();
         Email = email.getText().toString().trim();
         Password = password.getText().toString().trim();

        if (TextUtils.isEmpty(Name)){
            name.requestFocus();
            name.setError("Enter Name");
        } else if (TextUtils.isEmpty(Email)){
            email.requestFocus();
            email.setError("Enter Email Id");
        }else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            email.requestFocus();
            email.setError("Enter Valid Email Id");
        }else if (TextUtils.isEmpty(Password)){
            password.requestFocus();
            password.setError("Enter Password");
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
            View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

            TextView title;
            title = view.findViewById(R.id.progressBarTitle);

            title.setText("Creating new Account");

            builder.setCancelable(false).setView(view);
            AlertDialog progressDialog = builder.create();
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        SendUserData();
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this, "Registration Successful, Please Update Your Profile", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this, UpdateProfile.class);
                        intent.putExtra("Value","A");
                        startActivity(intent);
                        finish();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void SendUserData() {
        String CurrentUserUid = firebaseAuth.getCurrentUser().getUid();
        HashMap<String,Object> addUserDetails = new HashMap<>();
        addUserDetails.put("userName",Name);
        addUserDetails.put("userEmail",Email);
        addUserDetails.put("userDob","");
        addUserDetails.put("userAddress","");
        addUserDetails.put("userBio","");
        addUserDetails.put("userProfileImageUrl","None");
        addUserDetails.put("usersName","");
        addUserDetails.put("loginDetails","You are login with 𝐄𝐦𝐚𝐢𝐥 and 𝐏𝐚𝐬𝐬𝐰𝐨𝐫𝐝.");
        addUserDetails.put("userUid",CurrentUserUid);
        addUserDetails.put("onlineDate","");
        addUserDetails.put("onlineTime","");
        addUserDetails.put("onlineStatus","");
        addUserDetails.put("chatBackgroundWall","d");
        addUserDetails.put("userPassword",Password);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());
        databaseReference.setValue(addUserDetails);
        sharedPrefManager.saveWallpaper("d");
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUp.this,Login.class));
        finish();
    }


}