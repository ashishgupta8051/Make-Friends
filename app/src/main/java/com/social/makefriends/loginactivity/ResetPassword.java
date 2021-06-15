package com.social.makefriends.loginactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.social.makefriends.R;

public class ResetPassword extends AppCompatActivity {
    private EditText reset;
    private Button button;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        reset = (EditText)findViewById(R.id.reset_mail);

        button = (Button)findViewById(R.id.send_mail);

        firebaseAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = reset.getText().toString();
                if (TextUtils.isEmpty(Email)){
                    reset.requestFocus();
                    reset.setError("Enter your email id");
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this);
                    View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

                    TextView title;
                    title = view.findViewById(R.id.progressBarTitle);

                    title.setText("Reset Password");

                    builder.setCancelable(false).setView(view);
                    AlertDialog progressDialog = builder.create();
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                    progressDialog.show();

                    firebaseAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(ResetPassword.this, "Please check your email id for reset password", Toast.LENGTH_SHORT).show();
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(ResetPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ResetPassword.this,Login.class));
        finish();
    }
}