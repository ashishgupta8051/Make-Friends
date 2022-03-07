package com.social.makefriends.ui.activity.chat.wallpaper;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.social.makefriends.R;
import com.social.makefriends.model.UserDetails;
import com.social.makefriends.ui.activity.chat.ChatWithFriends;
import com.social.makefriends.ui.activity.chat.ViewWallpaper;
import com.social.makefriends.utils.Connection;
import com.social.makefriends.utils.SharedPrefManager;

import java.io.IOException;
import java.util.HashMap;

public class SelectWallpaper extends AppCompatActivity {
    private String userId,value,image,currentUserId,chatWallpaper;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    private ImageView defaultImg,lightImg,darkImg,galleryImg,wallpaper;
    private Button defaultBtn,lightBtn,darkBtn,galleryBtn,set;
    Uri imagePath;
    private ScrollView scrollView;
    Bitmap bitmap;
    private AlertDialog progressDialog;
    private ActivityResultLauncher<Intent> launcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_wallpaper);

        Connection.checkInternet(SelectWallpaper.this,this);
        getSupportActionBar().setTitle("Wallpaper");

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        userId = getIntent().getExtras().get("UserId").toString();
        value = getIntent().getExtras().get("Value").toString();
        image = getIntent().getExtras().get("Image").toString();
        chatWallpaper = getIntent().getExtras().get("ChatBackground").toString();

        userRef = FirebaseDatabase.getInstance().getReference("User Details").child(currentUserId);

        AlertDialog.Builder builder = new AlertDialog.Builder(SelectWallpaper.this);
        View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

        TextView title,message;
        title = view.findViewById(R.id.progressBarTitle);
        message = view.findViewById(R.id.progressBarMessage);

        title.setText("Set Wallpaper");

        builder.setCancelable(false).setView(view);
        progressDialog = builder.create();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;

        defaultImg = findViewById(R.id.imageView1);
        lightImg = findViewById(R.id.imageView2);
        darkImg = findViewById(R.id.imageView3);
        galleryImg = findViewById(R.id.imageView4);

        defaultBtn = findViewById(R.id.default_wallpaper);
        lightBtn = findViewById(R.id.light_wallpaper);
        darkBtn = findViewById(R.id.dark_wallpaper);
        galleryBtn = findViewById(R.id.gallery_wallpaper);

        scrollView = findViewById(R.id.scroll);

        wallpaper = findViewById(R.id.pick_image_wallpaper);
        set = findViewById(R.id.set_pick_WallpaperBtn);

        defaultImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectWallpaper.this, ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","d");
                intent.putExtra("transfer","def");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        defaultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectWallpaper.this,ViewWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("Wallpaper","d");
                intent.putExtra("transfer","def");
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        lightImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectWallpaper.this,LightWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        lightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectWallpaper.this,LightWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        darkImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectWallpaper.this,DarkWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        darkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectWallpaper.this,DarkWallpaper.class);
                intent.putExtra("Value",value);
                intent.putExtra("Image",image);
                intent.putExtra("UserId",userId);
                intent.putExtra("ChatBackground",chatWallpaper);
                startActivity(intent);
                finish();
            }
        });

        galleryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent((Intent.ACTION_PICK));
                intent.setType("image/*");
                launcher.launch(intent);
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent((Intent.ACTION_PICK));
                intent.setType("image/*");
                launcher.launch(intent);
            }
        });

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imagePath = data.getData();
                            scrollView.setVisibility(View.GONE);

                            getSupportActionBar().hide();


                            set.setVisibility(View.VISIBLE);
                            wallpaper.setVisibility(View.VISIBLE);
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            wallpaper.setImageBitmap(bitmap);

                            set.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    progressDialog.show();
                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("UserProfileImage")
                                            .child(currentUserId).child("Wallpaper").child(currentUserId+".jpg");
                                    storageReference.putFile(imagePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            if (task.isSuccessful()) {
                                                return storageReference.getDownloadUrl();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(SelectWallpaper.this, "Something is wrong. Please Try Again !!!", Toast.LENGTH_SHORT).show();
                                                throw task.getException();
                                            }
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()){
                                                Uri downloadUri = task.getResult();
                                                String Wallpaper = downloadUri.toString();
                                                HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                                                updateWallpaper.put("chatBackgroundWall",Wallpaper);
                                                userRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    progressDialog.dismiss();
                                                                    UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                                                    String Image = userDetails.getChatBackgroundWall();
                                                                    new SharedPrefManager(SelectWallpaper.this).saveWallpaper(Wallpaper);
                                                                    Toast.makeText(SelectWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(SelectWallpaper.this, ChatWithFriends.class);
                                                                    intent.putExtra("UserId",userId);
                                                                    intent.putExtra("Value",value);
                                                                    intent.putExtra("Image",image);
                                                                    intent.putExtra("ChatBackground",Image);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    Toast.makeText(SelectWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(SelectWallpaper.this, "Wallpaper did not uploaded", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }else {
                                                progressDialog.dismiss();
                                                Toast.makeText(SelectWallpaper.this, "Wallpaper did not uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }else {
                            Log.e("Error","Select Wallpaper error");
                        }
                    }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(SelectWallpaper.this, ChatWithFriends.class);
        intent1.putExtra("UserId",userId);
        intent1.putExtra("Value",value);
        intent1.putExtra("Image",image);
        intent1.putExtra("ChatBackground",chatWallpaper);
        startActivity(intent1);
        finish();
    }
}