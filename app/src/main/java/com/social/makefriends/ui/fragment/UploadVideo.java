package com.social.makefriends.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.social.makefriends.R;
import com.social.makefriends.ui.activity.home.Home;
import com.social.makefriends.databinding.FragmentUploadVideoBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UploadVideo extends Fragment {
    private FragmentUploadVideoBinding binding;
    private FirebaseAuth firebaseAuth;
    private Uri finalUri;
    private String CurrentDate,CurrentTime,PostImageUrl;
    private long countPost = 0;
    private long countPostProfile = 0;
    private static final int PERMISSION = 999;
    private ActivityResultLauncher<Intent> pickResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUploadVideoBinding.inflate(getLayoutInflater(), container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        binding.upload.setOnClickListener(view -> {
            if (finalUri == null){
                Toast.makeText(requireContext(), "Please select the video", Toast.LENGTH_SHORT).show();
            }else if (binding.postCaption.getText().toString().isEmpty()){
                binding.postCaption.requestFocus();
                binding.postCaption.setError("Enter your caption");
            }else {
                binding.postVideo.pause();
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                View view2 = getLayoutInflater().inflate(R.layout.progressdialog,null);

                TextView title,message;
                title = view2.findViewById(R.id.progressBarTitle);
                message = view2.findViewById(R.id.progressBarMessage);

                title.setText("Uploading Post Video");

                builder.setCancelable(false).setView(view2);
                AlertDialog progressDialog = builder.create();
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                progressDialog.show();

                uploadVideo(progressDialog,message);
            }
        });

        binding.pickVideo.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");
                pickResultLauncher.launch(intent);
            }else {
                requestPermission();
            }
        });

        pickResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        assert intent != null;
                        finalUri = intent.getData();

                        binding.postVideo.setVisibility(View.VISIBLE);
                        binding.pickVideo.setVisibility(View.GONE);
                        binding.pickVideo2.setVisibility(View.VISIBLE);
                        binding.postVideo.setVideoURI(finalUri);
                        binding.postVideo.start();

                        binding.pickVideo2.setOnClickListener(view -> {
                            binding.postVideo.pause();
                            Intent intent1 = new Intent(Intent.ACTION_PICK);
                            intent1.setType("video/*");
                            pickResultLauncher.launch(intent1);
                        });
                    }else {
                        Log.e("Result","Error in Pick Video");
                    }
                });

        return binding.getRoot();
    }

    private void uploadVideo(AlertDialog progressDialog, TextView message) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference userPostRef = FirebaseDatabase.getInstance().getReference("Post").child(userId);
        String key = userPostRef.push().getKey();
        DatabaseReference userDetailsRef = FirebaseDatabase.getInstance().getReference("User Details").child(userId);
        DatabaseReference allPostRef = FirebaseDatabase.getInstance().getReference("All Post");
        StorageReference postUrlRef = FirebaseStorage.getInstance().getReference("Post").child(userId).child(key);

        Calendar Date = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        CurrentDate = currentDate.format(Date.getTime());

        Calendar Time = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        CurrentTime = currentTime.format(Time.getTime());

        allPostRef.addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        userPostRef.addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        postUrlRef.putFile(finalUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                message.setText("Uploaded " + (int) progress + "%");
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (task.isSuccessful()){
                    return postUrlRef.getDownloadUrl();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "Image Url not downloaded. Please Try Again !!!", Toast.LENGTH_SHORT).show();
                    throw task.getException();
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    PostImageUrl = downloadUri.toString();
                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String Name = snapshot.child("userName").getValue().toString();
                                String UsersName = snapshot.child("usersName").getValue().toString();
                                String UserProfilePic = snapshot.child("userProfileImageUrl").getValue().toString();
                                String TotalPostCount = String.valueOf(countPost);
                                String TotalPostCountProfile = String.valueOf(countPostProfile);

                                //For User Post
                                HashMap<String,Object> addUserPost = new HashMap<>();
                                addUserPost.put("postId",key);
                                addUserPost.put("postType","video");
                                addUserPost.put("postImage",PostImageUrl);
                                addUserPost.put("postCount",TotalPostCountProfile);
                                addUserPost.put("userId",userId);

                                userPostRef.child(key).setValue(addUserPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            //For All Post
                                            HashMap<String,Object> allPost = new HashMap<>();
                                            allPost.put("key",key);
                                            allPost.put("postType","video");
                                            allPost.put("userName",Name);
                                            allPost.put("userProfilePic",UserProfilePic);
                                            allPost.put("currentDate",CurrentDate);
                                            allPost.put("currentTime",CurrentTime);
                                            allPost.put("postImage",PostImageUrl);
                                            allPost.put("caption",binding.postCaption.getText().toString());
                                            allPost.put("currentUserId",userId);
                                            allPost.put("countPost",TotalPostCount);
                                            allPost.put("usersName",UsersName);

                                            allPostRef.child(key).setValue(allPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(requireContext(), "Post Uploaded Successful", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(requireContext(), Home.class));
                                                    requireActivity().finish();
                                                }
                                            });
                                        }else {
                                            Toast.makeText(requireContext(), "Post Uploaded Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(requireContext(), error.getCode(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(requireContext(), "Something is wrong. Please try again !!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("video/*");
                    pickResultLauncher.launch(intent);
                    Toast.makeText(requireContext(), "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Enable Permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.postVideo.pause();
    }
}