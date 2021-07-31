package com.social.makefriends;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.social.makefriends.activity.Home;
import com.social.makefriends.loginactivity.Login;
import com.social.makefriends.utils.CheckInternetConnection;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().setStatusBarColor(Color.parseColor("#338CB1"));
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