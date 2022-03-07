package com.social.makefriends.ui.activity.chat.wallpaper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.social.makefriends.R;
import com.social.makefriends.ui.activity.chat.ViewWallpaper;
import com.social.makefriends.utils.Connection;

public class DarkWallpaper extends AppCompatActivity {
    private String userId,image,value,chatWallpaper;
    private CardView card1,card2,card3,card4,card5,card6,card7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dark_wallpaper);

        getSupportActionBar().setTitle("Dark Wallpaper");

        Connection.checkInternet(DarkWallpaper.this,this);

        userId = getIntent().getExtras().get("UserId").toString();
        value = getIntent().getExtras().get("Value").toString();
        image = getIntent().getExtras().get("Image").toString();
        chatWallpaper = getIntent().getExtras().get("ChatBackground").toString();

        card1 = findViewById(R.id.card_1);
        card2 = findViewById(R.id.card_2);
        card3 = findViewById(R.id.card_3);
        card4 = findViewById(R.id.card_4);
        card5 = findViewById(R.id.card_5);
        card6 = findViewById(R.id.card_6);
        card7 = findViewById(R.id.card_7);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DarkWallpaper.this, ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","d1");
                intent.putExtra("transfer","dar");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DarkWallpaper.this,ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","d2");
                intent.putExtra("transfer","dar");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DarkWallpaper.this,ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","d3");
                intent.putExtra("transfer","dar");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DarkWallpaper.this,ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","d4");
                intent.putExtra("transfer","dar");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DarkWallpaper.this,ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","d5");
                intent.putExtra("transfer","dar");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DarkWallpaper.this,ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","d6");
                intent.putExtra("transfer","dar");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        card7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DarkWallpaper.this,ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","d7");
                intent.putExtra("transfer","dar");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DarkWallpaper.this,SelectWallpaper.class);
        intent.putExtra("Value",value);
        intent.putExtra("Image",image);
        intent.putExtra("UserId",userId);
        intent.putExtra("ChatBackground",chatWallpaper);
        startActivity(intent);
        finish();
    }

}