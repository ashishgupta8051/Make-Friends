package com.social.friends.friendrequest.chatting.background;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.social.friends.R;

public class LightWallpaper extends AppCompatActivity {
    private String userId,value,image,currentUserId,chatWallpaper;
    private CardView cardView1,cardView2,cardView3,cardView4,cardView5,cardView6,cardView7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_wallpaper);

        getSupportActionBar().setTitle("Light Wallpaper");

        userId = getIntent().getExtras().get("UserId").toString();
        value = getIntent().getExtras().get("Value").toString();
        image = getIntent().getExtras().get("Image").toString();
        chatWallpaper = getIntent().getExtras().get("ChatBackground").toString();

        cardView1 = findViewById(R.id.card1);
        cardView2 = findViewById(R.id.card2);
        cardView3 = findViewById(R.id.card3);
        cardView4 = findViewById(R.id.card4);
        cardView5 = findViewById(R.id.card5);
        cardView6 = findViewById(R.id.card6);
        cardView7 = findViewById(R.id.card7);

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LightWallpaper.this,ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","l1");
                intent.putExtra("transfer","lig");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LightWallpaper.this,ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","l2");
                intent.putExtra("transfer","lig");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LightWallpaper.this,ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","l3");
                intent.putExtra("transfer","lig");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LightWallpaper.this,ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","l4");
                intent.putExtra("transfer","lig");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        cardView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LightWallpaper.this,ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","l5");
                intent.putExtra("transfer","lig");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        cardView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LightWallpaper.this,ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","l6");
                intent.putExtra("transfer","lig");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        cardView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LightWallpaper.this,ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","l7");
                intent.putExtra("transfer","lig");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(LightWallpaper.this, SelectWallpaper.class);
        intent1.putExtra("UserId",userId);
        intent1.putExtra("Value",value);
        intent1.putExtra("Image",image);
        intent1.putExtra("ChatBackground",chatWallpaper);
        startActivity(intent1);
        finish();
    }
}