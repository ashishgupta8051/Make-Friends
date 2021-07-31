package com.social.makefriends.settings;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.social.makefriends.R;
import com.social.makefriends.activity.UserProfile;
import com.social.makefriends.loginactivity.ChangePassword;
import com.social.makefriends.loginactivity.Login;
import com.social.makefriends.manage.userpost.FavouritePost;
import com.social.makefriends.utils.CheckInternetConnection;

public class SettingsActivity extends AppCompatActivity {
    private String value;
    private TextView savePostTxt,themeTxt,logoutTxt,changePassword;
    private LoginManager loginManager;
    private FirebaseAuth firebaseAuth;
    private BroadcastReceiver broadcastReceiver = new CheckInternetConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        getSupportActionBar().setTitle("Setting");

        value = getIntent().getExtras().get("UserFriendsValue").toString();

        firebaseAuth = FirebaseAuth.getInstance();
        loginManager = LoginManager.getInstance();

        savePostTxt = findViewById(R.id.save_post);
        themeTxt = findViewById(R.id.theme);
        logoutTxt = findViewById(R.id.logout);
        changePassword = findViewById(R.id.changePasswordEdt);

        logoutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        savePostTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, FavouritePost.class);
                intent.putExtra("Value",value);
                startActivity(intent);
                finish();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangePassword.class);
                intent.putExtra("Value",value);
                startActivity(intent);
                finish();
            }
        });



        themeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogBox();
            }
        });
    }

    private void showDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Select Theme");
        String[] items = {"Light Theme","Dark Theme"};
        int checkedItem = 0;
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Toast.makeText(SettingsActivity.this, "Switch to Light Theme", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(SettingsActivity.this, "Switch to Dark Theme", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebaseAuth.signOut();
                loginManager.logOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.setCancelable(false).create();
        alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this,UserProfile.class);
        intent.putExtra("UserFriendsValue",value);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter =  new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

}