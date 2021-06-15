package com.social.friends.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.social.friends.R;
import com.social.friends.loginactivity.Login;
import com.social.friends.model.AllPost;
import com.social.friends.model.UserPost;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Post extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ImageView post_image;
    private EditText caption;
    private Button post_button;
    private Uri uri;
    private String Caption,CurrentDate,CurrentTime,PostImageUrl;
    private long countPost = 0;
    private long countPostProfile = 0;
    private static final int PERMISSION = 999;
    private ActivityResultLauncher<Intent> pickResultLauncher;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getSupportActionBar().setTitle("Post");

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        firebaseAuth = FirebaseAuth.getInstance();

        post_image = (ImageView)findViewById(R.id.post_image);

        post_button = (Button)findViewById(R.id.upload);

        caption = (EditText)findViewById(R.id.post_caption);

        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Post.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    pickResultLauncher.launch(intent);
                }else {
                    requestPermission();
                }
            }
        });

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Caption = caption.getText().toString();
                if (uri == null){
                    Toast.makeText(Post.this, "Please select Image", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(Caption)){
                    caption.setError("Write your caption");
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Post.this);
                    View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

                    TextView title,message;
                    title = view.findViewById(R.id.progressBarTitle);
                    message = view.findViewById(R.id.progressBarMessage);

                    title.setText("Uploading Post Image");

                    builder.setCancelable(false).setView(view);
                    AlertDialog progressDialog = builder.create();
                    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                    progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                    progressDialog.show();

                    SendPostInformation(progressDialog,message);
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigationview);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        bottomNavigationView.setSelectedItemId(R.id.nav_post);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_post:
                        return true;
                    case R.id.nav_chat:
                        startActivity(new Intent(getApplicationContext(), Chats.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_notification:
                        startActivity(new Intent(getApplicationContext(), Notifications.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.nav_profile:
                        Intent intent = new Intent(getApplicationContext(),UserProfile.class);
                        intent.putExtra("UserFriendsValue","A");
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                }
                return true;
            }
        });

        pickResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            Intent dsPhotoEditorIntent = new Intent(getApplicationContext(), DsPhotoEditorActivity.class);
                            dsPhotoEditorIntent.setData(uri);
                            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#FF6200EE"));
                            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#FFFFFF"));

                            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE,new int[]{
                                    DsPhotoEditorActivity.TOOL_WARMTH,DsPhotoEditorActivity.TOOL_PIXELATE});
                            launcher.launch(dsPhotoEditorIntent);
//                            startActivityForResult(dsPhotoEditorIntent, 200);

                        }else {
                            Log.e("Error","Pick Image Error");
                        }
                    }
        });

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent intent = result.getData();
                            Uri data = intent.getData();
                            Picasso.get().load(data).into(post_image);
                        }else {
                            Log.e("Error","Pick Image Error 2");
                        }
                    }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    pickResultLauncher.launch(intent);
                    Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Enable Permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

  /*  @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            uri = data.getData();
            switch (requestCode){
                case PICK_IMAGE:
                    Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
                    dsPhotoEditorIntent.setData(uri);
                    dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#FF6200EE"));
                    dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#FFFFFF"));

                    dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE,new int[]{
                            DsPhotoEditorActivity.TOOL_WARMTH,DsPhotoEditorActivity.TOOL_PIXELATE});
                    startActivityForResult(dsPhotoEditorIntent, 200);
                    break;
                case 200:
                    Picasso.get().load(uri).fit().into(post_image);
                    Log.i("URI",String.valueOf(uri));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    private void SendPostInformation(AlertDialog progressDialog, TextView message) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post").child(userId);
        String key = databaseReference.push().getKey();
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User Details").child(userId);
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("All Post");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Post").child(userId).child(key);

        Calendar Date = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        CurrentDate = currentDate.format(Date.getTime());

        Calendar Time = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        CurrentTime = currentTime.format(Time.getTime());

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    countPost = snapshot.getChildrenCount();
                }else {
                    countPost = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Post.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    countPostProfile = snapshot.getChildrenCount();
                }else {
                    countPostProfile = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Post.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        storageReference.putFile(uri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                message.setText("Uploaded " + (int) progress + "%");
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (task.isSuccessful()){
                    return storageReference.getDownloadUrl();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(Post.this, "Image Url not downloaded. Please Try Again !!!", Toast.LENGTH_SHORT).show();
                    throw task.getException();
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    PostImageUrl = downloadUri.toString();
                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String Name = snapshot.child("userName").getValue().toString();
                                String UsersName = snapshot.child("usersName").getValue().toString();
                                String UserProfilePic = snapshot.child("userProfileImageUrl").getValue().toString();
                                String TotalPostCount = String.valueOf(countPost);
                                String TotalPostCountProfile = String.valueOf(countPostProfile);
                                UserPost userPost = new UserPost(key,PostImageUrl,TotalPostCountProfile,userId);
                                AllPost allPost = new AllPost(key,Name,UserProfilePic,CurrentDate,CurrentTime,PostImageUrl,Caption,userId,TotalPostCount,UsersName);
                                databaseReference.child(key).setValue(userPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            databaseReference2.child(key).setValue(allPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(Post.this, "Post Uploaded Successful", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Post.this,Home.class));
                                                    finish();
                                                }
                                            });
                                        }else {
                                            Toast.makeText(Post.this, "Post Uploaded Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Post.this, error.getCode(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(Post.this, "Something is wrong. Please try again !!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),Home.class));
        finish();
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Post.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(Post.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
        } else {
            ActivityCompat.requestPermissions(Post.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
        }
    }


}