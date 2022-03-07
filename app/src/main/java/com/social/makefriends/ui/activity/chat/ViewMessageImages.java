package com.social.makefriends.ui.activity.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.social.makefriends.R;
import com.social.makefriends.utils.Connection;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ViewMessageImages extends AppCompatActivity {
    private String CurrentUserId,UserId,Value,Image,MsgId,Message,chatWallpaper;
    private FirebaseAuth firebaseAuth;
    private ImageView imageView,backButton,download;
    private ProgressBar progressBar;
    Bitmap bitmap;
    private static final  int PERMISSION = 999;
    private boolean check = true;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message_images);

        Connection.checkInternet(ViewMessageImages.this,this);

        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserId = firebaseAuth.getCurrentUser().getUid();

        UserId = getIntent().getExtras().get("UserId").toString();
        Value = getIntent().getExtras().get("Value").toString();
        Image = getIntent().getExtras().get("Image").toString();
        MsgId = getIntent().getExtras().get("MsgId").toString();
        Message = getIntent().getExtras().get("Message").toString();
        chatWallpaper = getIntent().getExtras().get("ChatBackground").toString();

        imageView = findViewById(R.id.message_details_image);
        backButton = findViewById(R.id.back);
        download = findViewById(R.id.download_image);
        progressBar = findViewById(R.id.messageProgressBar);

        backButton.setBackgroundColor(Color.TRANSPARENT);
        download.setBackgroundColor(Color.TRANSPARENT);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(ViewMessageImages.this,ChatWithFriends.class);
                intent2.putExtra("UserId",UserId);
                intent2.putExtra("Image",Image);
                intent2.putExtra("Value",Value);
                intent2.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent2);
                finish();
            }
        });

        Glide.with(ViewMessageImages.this).load(Message).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@SuppressLint("CheckResult") @Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource)  {
                progressBar.setVisibility(View.GONE);
                return false;
            }

            @SuppressLint("CheckResult")
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ViewMessageImages.this,download);
                popupMenu.inflate(R.menu.download_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.download:
                                if (Build.VERSION.SDK_INT >= 23){
                                    if (ContextCompat.checkSelfPermission(ViewMessageImages.this,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                        downloadImage();
                                    }else {
                                        requestPermission();
                                    }
                                }else {
                                    downloadImage();
                                }
                        }
                        return true;
                    }
                });

                popupMenu.show();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check == true){
                    backButton.setVisibility(View.GONE);
                    download.setVisibility(View.GONE);
                    check = false;
                }else {
                    backButton.setVisibility(View.VISIBLE);
                    download.setVisibility(View.VISIBLE);
                    check = true;
                }
            }
        });

    }


    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ViewMessageImages.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewMessageImages.this);
            builder.setTitle("Permission needed");
            builder.setMessage("This permission is needed because of this and that");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(ViewMessageImages.this,
                            new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION);
                }
            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else {
            ActivityCompat.requestPermissions(ViewMessageImages.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Enable Permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void downloadImage() {
        String title = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(Message));
        request.setTitle(title);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir("/Make Friends/Message/Image",title+".jpg");

        DownloadManager downloadManager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        request.setMimeType("image/*");
//        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        downloadManager.enqueue(request);
    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(ViewMessageImages.this,ChatWithFriends.class);
        intent2.putExtra("UserId",UserId);
        intent2.putExtra("Image",Image);
        intent2.putExtra("Value",Value);
        intent2.putExtra("ChatBackground",chatWallpaper);
        startActivity(intent2);
        finish();
    }


}