package com.social.friends;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.social.friends.activity.Home;
import com.social.friends.loginactivity.Login;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor((ContextCompat.getColor(MainActivity.this, R.color.Splash)));
        getSupportActionBar().hide();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Thread thread = new Thread(){
            public void run() {
                try {
                    sleep(2*1000);
                    if (firebaseUser == null){
                        startActivity(new Intent(MainActivity.this, Login.class));
                    }else {
                        startActivity(new Intent(MainActivity.this, Home.class));
                    }
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}