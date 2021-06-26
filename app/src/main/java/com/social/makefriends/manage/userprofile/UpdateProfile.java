package com.social.makefriends.manage.userprofile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.social.makefriends.R;
import com.social.makefriends.activity.UserProfile;
import com.social.makefriends.model.UserDetails;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfile extends AppCompatActivity {
    private static final int PERMISSION = 999;
    private EditText name, email, dob, address, bio, userName;
    private FirebaseAuth firebaseAuth;
    private CircleImageView UpdateProfileImage;
    private StorageReference profileImagePath;
    private static int PICK = 123;
    private String userId;
    private int CountUserName = 0;
    private String Value;
    private ActivityResultLauncher<Intent> dpLauncher;
    private ActivityResultLauncher<Intent> launcher;
    private TextView loginDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setTitle("Update Profile");

        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();

        profileImagePath = FirebaseStorage.getInstance().getReference("UserProfileImage");

        name = (EditText) findViewById(R.id.update_name);
        email = (EditText) findViewById(R.id.update_email);
        dob = (EditText) findViewById(R.id.update_Dob);
        address = (EditText) findViewById(R.id.update_address);
        bio = (EditText) findViewById(R.id.update_bio);

        userName = (EditText) findViewById(R.id.update_username);

        loginDetails = findViewById(R.id.account_details);

        UpdateProfileImage = (CircleImageView) findViewById(R.id.update_profile_Image);

        Value = getIntent().getExtras().get("Value").toString();

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        UpdateProfile.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        dob.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String ProfileImage = snapshot.child("userProfileImageUrl").getValue().toString();
                    if (ProfileImage.equals("None")) {
                        UpdateProfileImage.setImageResource(R.drawable.profile_image);
                    } else {
                        Picasso.get().load(ProfileImage).fit().placeholder(R.drawable.profile_image).into(UpdateProfileImage);
                    }

                    UserDetails userDetails = snapshot.getValue(UserDetails.class);
                    name.setText(userDetails.getUserName());
                    userName.setText(userDetails.getUsersName());
                    email.setText(userDetails.getUserEmail());
                    dob.setText(userDetails.getUserDob());
                    address.setText(userDetails.getUserAddress());
                    bio.setText(userDetails.getUserBio());
                    loginDetails.setText(userDetails.getLoginDetails());
                } else {
                    Log.e("Error", "Update Profile");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfile.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        UpdateProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(UpdateProfile.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                  CropImage.startPickImageActivity(UpdateProfile.this);
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    dpLauncher.launch(intent);
                }else {
                    requestPermission();
                }
            }
        });

        dpLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            /*if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri ImageUri = CropImage.getPickImageResultUri(this, data);

            if (CropImage.isReadExternalStoragePermissionsRequired(this, ImageUri)) {
                uri = ImageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                startCrop(ImageUri);
            }
        }*/
                            /*if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

            }
        }*/

                            Intent intent = result.getData();
                            Uri uri = intent.getData();

                            Intent dsPhotoEditorIntent = new Intent(getApplicationContext(), DsPhotoEditorActivity.class);
                            dsPhotoEditorIntent.setData(uri);
                            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#FF6200EE"));
                            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#FFFFFF"));

                            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE,new int[]{
                                    DsPhotoEditorActivity.TOOL_WARMTH,DsPhotoEditorActivity.TOOL_PIXELATE});
                            launcher.launch(dsPhotoEditorIntent);

                            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfile.this);
                            View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

                            TextView title;
                            title = view.findViewById(R.id.progressBarTitle);

                            title.setText("Update Profile Image");

                            builder.setCancelable(false).setView(view);
                            AlertDialog progressDialog = builder.create();
                            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                            progressDialog.show();

                            StorageReference profileImageRef = profileImagePath.child(firebaseAuth.getUid()).child("Image").child(firebaseAuth.getUid() + ".jpg");
                            profileImageRef.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (task.isSuccessful()) {
                                        return profileImageRef.getDownloadUrl();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(UpdateProfile.this, "Something is wrong. Please Try Again !!!", Toast.LENGTH_SHORT).show();
                                        throw task.getException();
                                    }
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        String ProfileImageUri = downloadUri.toString();
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());
                                        databaseReference.child("userProfileImageUrl").setValue(ProfileImageUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(UpdateProfile.this, UpdateProfile.class);
                                                    intent.putExtra("Value",Value);
                                                    startActivity(intent);
                                                    finish();
                                                    Toast.makeText(UpdateProfile.this, "Profile Image Upload Successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(UpdateProfile.this, "Profile Image Upload Failed !!!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("All Post");
                                        Query query = databaseReference1.orderByChild("currentUserId").equalTo(userId);
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        String key = dataSnapshot.getKey();

                                                        Map<String, Object> updateDp = new HashMap<String, Object>();

                                                        updateDp.put("userProfilePic", ProfileImageUri);

                                                        databaseReference1.child(key).updateChildren(updateDp);
                                                    }
                                                } else {
                                                    Toast.makeText(UpdateProfile.this, "Please Upload Some Post", Toast.LENGTH_SHORT);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(UpdateProfile.this, error.getCode(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Comment");
                                        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        String postId = dataSnapshot.getKey();
                                                        Query query1 = databaseReference2.child(postId).orderByChild("currentUserId").equalTo(userId);
                                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists()) {
                                                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                                                        String commentId = dataSnapshot1.getKey();
                                                                        Map<String, Object> updateName = new HashMap<String, Object>();

                                                                        updateName.put("profilePic", ProfileImageUri);
                                                                        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("Comment").child(postId).child(commentId);
                                                                        databaseReference3.updateChildren(updateName);
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                Toast.makeText(UpdateProfile.this, error.getCode(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    Toast.makeText(UpdateProfile.this, "", Toast.LENGTH_SHORT);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(UpdateProfile.this, error.getCode(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("Search Friend History");
                                        databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        String Key = dataSnapshot.getKey();
                                                        Query query1 = databaseReference3.child(Key).orderByChild("search_UserId").equalTo(userId);
                                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists()){
                                                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                                                        String key = dataSnapshot1.getKey();
                                                                        Map<String,Object> updateDp = new HashMap<String, Object>();
                                                                        updateDp.put("search_ProfilePic",ProfileImageUri);
                                                                        databaseReference3.child(Key).child(key).updateChildren(updateDp);
                                                                    }
                                                                }else {
                                                                    Log.e("Error", "User Not Found");
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                Toast.makeText(UpdateProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    Log.e("Error", "Path not found");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(UpdateProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(UpdateProfile.this, "Something is wrong. Please Try Again !!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else {
                            Log.e("Error","Dp change error");
                        }
                    }
        });

      /*  launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent intent = result.getData();
                            Uri uri = intent.getData();


                        }else {
                            Log.e("Error","Dp change error 2");
                        }
                    }
        });*/
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(UpdateProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(UpdateProfile.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
        } else {
            ActivityCompat.requestPermissions(UpdateProfile.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    CropImage.startPickImageActivity(UpdateProfile.this);
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    dpLauncher.launch(intent);
                    Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Enable Permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /*private void startCrop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save: {
                updateProfile();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateProfile() {
        String Name = name.getText().toString();
        String UserName = userName.getText().toString();
        String Email = email.getText().toString();
        String Dob = dob.getText().toString();
        String Address = address.getText().toString();
        String Bio = bio.getText().toString();

        for (int i = 0; i <= UserName.length(); i++){
            CountUserName = i;
        }

        if (TextUtils.isEmpty(Name)) {
            name.setError("Enter name");
        } else if (TextUtils.isEmpty(UserName)) {
            userName.setError("Enter UserName");
        } else if (CountUserName < 5 || CountUserName > 20 ){
            userName.setError("Your username must be a minimum of 5 characters and not greater than 20 characters.");
        } else if (TextUtils.isEmpty(Email)) {
            email.setError("Enter email");
        } else if (TextUtils.isEmpty(Dob)) {
            dob.setError("Select Date of Birth");
        } else if (TextUtils.isEmpty(Address)) {
            address.setError("Enter address");
        } else if (TextUtils.isEmpty(Bio)) {
            bio.setError("Enter you bio");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateProfile.this);
            View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

            TextView title;
            title = view.findViewById(R.id.progressBarTitle);

            title.setText("Update Profile");

            builder.setView(view);
            AlertDialog progressDialog = builder.create();
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
            progressDialog.show();


            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());
            Query checkUserNameQuery = FirebaseDatabase.getInstance().getReference("User Details").orderByChild("usersName").equalTo(UserName);
            checkUserNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String GetUserName = snapshot.child("usersName").getValue().toString();
                            SendUserName(GetUserName);
                        }

                        private void SendUserName(String getUserName) {
                            if (snapshot.getChildrenCount() > 0 && !getUserName.equals(UserName)) {
                                progressDialog.dismiss();
                                userName.setError("Choose a different username");
                                Toast.makeText(UpdateProfile.this, "Choose a different username", Toast.LENGTH_SHORT).show();
                            } else {
                                Map<String, Object> updateProfile = new HashMap<String, Object>();
                                updateProfile.put("userName", Name);
                                updateProfile.put("userEmail", Email);
                                updateProfile.put("userDob", Dob);
                                updateProfile.put("userAddress", Address);
                                updateProfile.put("userBio", Bio);
                                updateProfile.put("usersName", UserName);

                                databaseReference.updateChildren(updateProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(UpdateProfile.this, "Profile Update Successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                                            intent.putExtra("UserFriendsValue", Value);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(UpdateProfile.this, "Profile Update Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(UpdateProfile.this, error.getCode(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UpdateProfile.this, error.getCode(), Toast.LENGTH_SHORT).show();
                }
            });

            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("All Post");
            Query query = databaseReference1.orderByChild("currentUserId").equalTo(userId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String key = dataSnapshot.getKey();
                            Map<String, Object> update = new HashMap<String, Object>();
                            update.put("userName", Name);
                            update.put("usersName", UserName);
                            databaseReference1.child(key).updateChildren(update);
                        }
                    } else {
                        Toast.makeText(UpdateProfile.this, "Please Upload Some Post", Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UpdateProfile.this, error.getCode(), Toast.LENGTH_SHORT).show();
                }
            });

            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Comment");
            databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String postId = dataSnapshot.getKey();
                            Query query1 = databaseReference2.child(postId).orderByChild("currentUserId").equalTo(userId);
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                            String commentId = dataSnapshot1.getKey();
                                            Map<String, Object> updateName = new HashMap<String, Object>();

                                            updateName.put("userName", UserName);

                                            DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("Comment").child(postId).child(commentId);
                                            databaseReference3.updateChildren(updateName);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(UpdateProfile.this, error.getCode(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(UpdateProfile.this, "", Toast.LENGTH_SHORT);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UpdateProfile.this, error.getCode(), Toast.LENGTH_SHORT).show();
                }
            });

            DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("Search Friend History");
            databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            String Key = dataSnapshot.getKey();
                            Query query1 = databaseReference3.child(Key).orderByChild("search_UserId").equalTo(userId);
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                            String key = dataSnapshot1.getKey();
                                            Map<String, Object> update = new HashMap<String, Object>();
                                            update.put("search_Name", Name);
                                            update.put("search_UserName", UserName);
                                            databaseReference3.child(Key).child(key).updateChildren(update);
                                        }
                                    }else {
                                        Log.e("Error","User Not found");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(UpdateProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else {
                        Log.e("Error","Path not found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UpdateProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            DatabaseReference databaseReference4 = FirebaseDatabase.getInstance().getReference("Friend");
            databaseReference4.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            String Key = dataSnapshot.getKey();
                            Query query1 = databaseReference4.child(Key).orderByChild("friendUid").equalTo(userId);
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                            String key = dataSnapshot1.getKey();
                                            Map<String, Object> update = new HashMap<String, Object>();
                                            update.put("friend_name", Name);
                                            update.put("friend_userName", UserName);
                                            databaseReference4.child(Key).child(key).updateChildren(update);
                                        }
                                    }else {
                                        Log.e("Error","not exists2");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(UpdateProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else {
                        Log.e("Error","not exists1");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UpdateProfile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), UserProfile.class);
        intent.putExtra("UserFriendsValue", Value);
        startActivity(intent);
        finish();
    }
}