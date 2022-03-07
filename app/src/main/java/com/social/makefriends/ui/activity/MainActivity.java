package com.social.makefriends.ui.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.social.makefriends.R;
import com.social.makefriends.model.ConnectionModel;
import com.social.makefriends.network.CheckInternetConnections;
import com.social.makefriends.ui.activity.account.Login;
import com.social.makefriends.ui.activity.home.Home;
import com.social.makefriends.utils.Connection;
import com.social.makefriends.utils.InternetAlertDialog;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*CheckInternetConnections checkInternetConnections = new CheckInternetConnections(getApplicationContext());
        checkInternetConnections.observe(this, new Observer<ConnectionModel>() {
            @Override
            public void onChanged(ConnectionModel connectionModel) {
                if (!connectionModel.getIsConnected()){
                    AlertDialog alertDialog = InternetAlertDialog.internetAlertDialog(MainActivity.this);
                    alertDialog.show();
                }
            }
        });*/

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