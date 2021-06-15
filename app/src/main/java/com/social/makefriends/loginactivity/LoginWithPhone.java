package com.social.makefriends.loginactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.social.makefriends.R;

public class LoginWithPhone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_phone);

        getSupportActionBar().setTitle("Login With Phone");
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}