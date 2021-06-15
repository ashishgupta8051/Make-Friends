package com.social.makefriends.loginactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.model.UserDetails;
import com.social.makefriends.settings.SettingsActivity;

import java.util.HashMap;

public class ChangePassword extends AppCompatActivity {
    private String value;
    private DatabaseReference userRef;
    private Button changePasswordButton;
    private EditText currentPassword,changeLoginPassword,changeLoginPasswordAgain;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setTitle("Change Password");

        value = getIntent().getExtras().get("Value").toString();

        changeLoginPassword = (EditText) findViewById(R.id.change_login_password);
        currentPassword = (EditText) findViewById(R.id.currentPassword);
        changeLoginPasswordAgain = (EditText) findViewById(R.id.change_login_password2);

        changePasswordButton = (Button) findViewById(R.id.change_password_button);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseUser.getUid());

        //Progress Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
        View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

        TextView title;
        title = view.findViewById(R.id.progressBarTitle);

        title.setText("Change Password");

        builder.setCancelable(false).setView(view);
        AlertDialog progressDialog = builder.create();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserDetails userDetails = snapshot.getValue(UserDetails.class);
                    String password = userDetails.getUserPassword();

                    changePasswordButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String CurrentPass = currentPassword.getText().toString();
                            String NewPass = changeLoginPassword.getText().toString();
                            String NewPass2 = changeLoginPasswordAgain.getText().toString();
                            if (TextUtils.isEmpty(CurrentPass)) {
                                currentPassword.requestFocus();
                                currentPassword.setError("Enter current Password");
                            }else if (TextUtils.isEmpty(NewPass)){
                                changeLoginPassword.requestFocus();
                                changeLoginPassword.setError("Enter new Password");
                            }else if (TextUtils.isEmpty(NewPass2)){
                                changeLoginPasswordAgain.requestFocus();
                                changeLoginPasswordAgain.setError("Enter new Password Again");
                            }else if (!NewPass2.equals(NewPass)){
                                changeLoginPasswordAgain.requestFocus();
                                changeLoginPasswordAgain.setError("Password should be matched");
                            }else if (!CurrentPass.equals(password)){
                                currentPassword.requestFocus();
                                currentPassword.setError("Current password is wrong");
                            }else {
                                progressDialog.show();
                                firebaseUser.updatePassword(NewPass2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        HashMap<String,Object> updatePass = new HashMap<String, Object>();
                                        updatePass.put("userPassword",NewPass2);
                                        userRef.updateChildren(updatePass);
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Password has been successfully changed", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ChangePassword.this, SettingsActivity.class);
                                        intent.putExtra("UserFriendsValue",value);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Something is wrong. Password has not been changed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }else {
                    Log.e("Error","User does not exists !!!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChangePassword.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChangePassword.this, SettingsActivity.class);
        intent.putExtra("UserFriendsValue",value);
        startActivity(intent);
        finish();
    }
}