package com.social.makefriends.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import android.provider.MediaStore;
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
import com.social.makefriends.databinding.FragmentUploadStatusBinding;
import com.social.makefriends.model.Status;
import com.social.makefriends.model.UserDetails;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class UploadStatus extends Fragment {
    private Uri finalUrl;
    private String currentUserId;
    private FirebaseAuth firebaseAuth;
    private FragmentUploadStatusBinding binding;
    private ActivityResultLauncher<Intent> pickResultLauncher;
    private static final int PERMISSION = 999;
    private DatabaseReference statusRef,userDetailsRef;
    private StorageReference storageReference;
    private String Caption,CurrentDate,CurrentTime,PostImageUrl;
    private long countPost = 0;
    private long countPostProfile = 0;
    private Date date;
    private byte[] downsizedImageBytes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUploadStatusBinding.inflate(getLayoutInflater(),container,false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        date = new Date();

        binding.pickPostStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    pickResultLauncher.launch(intent);
                }else {
                    requestPermission();
                }
            }
        });

        pickResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        finalUrl = intent.getData();

                        Bitmap fullBitmap = null;
                        try {
                            fullBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), finalUrl);
                            int scaleWidth =  (fullBitmap.getWidth() / 2);
                            int scaleHeight = (fullBitmap.getHeight() / 2);
                            downsizedImageBytes = getDownsizedImageBytes(fullBitmap, scaleWidth, scaleHeight);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        binding.pickPostStatus.setVisibility(View.GONE);
                        binding.postStatus.setVisibility(View.VISIBLE);
                        binding.shareStatus.setVisibility(View.VISIBLE);
                        Picasso.get().load(finalUrl).noFade().into(binding.postStatus);

                        binding.postStatus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (ContextCompat.checkSelfPermission(requireContext(),
                                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    pickResultLauncher.launch(intent);
                                }else {
                                    requestPermission();
                                }
                            }
                        });
                    }else {
                        Log.e("Result","Error in Pick Image");
                    }
                });

        binding.shareStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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
        });

        return binding.getRoot();
    }

    private void SendPostInformation(androidx.appcompat.app.AlertDialog progressDialog, TextView message) {
        String userId = firebaseAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference("All Status").child(firebaseAuth.getCurrentUser().getUid());
        statusRef = FirebaseDatabase.getInstance().getReference("All Status").child(firebaseAuth.getCurrentUser().getUid());
        userDetailsRef = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getCurrentUser().getUid());

       /* Calendar Date = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        CurrentDate = currentDate.format(Date.getTime());

        Calendar Time = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        CurrentTime = currentTime.format(Time.getTime());*/


        storageReference.putBytes(downsizedImageBytes).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
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
                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                            assert userDetails != null;
                            String userName = userDetails.getUsersName();
                            String name = userDetails.getUserName();
                            String profileImage;
                            if (userDetails.getUserProfileImageUrl().equals("None")){
                                 profileImage = "https://firebasestorage.googleapis.com/v0/b/make-friends-7338f.appspot.com/o/Default%20Profile%20Image%2Fprofile_image.png?alt=media&token=5a8a1e76-a503-4241-af43-ae48e39dc528";
                            }else {
                                 profileImage = userDetails.getUserProfileImageUrl();
                            }
                            long lastUpdate = date.getTime();

                            HashMap<String,Object> addStatus = new HashMap<>();
                            addStatus.put("userName",userName);
                            addStatus.put("name",name);
                            addStatus.put("profileImage",profileImage);
                            addStatus.put("userId",firebaseAuth.getCurrentUser().getUid());
                            addStatus.put("lastUpdate",lastUpdate);

                            String  key = statusRef.push().getKey();
                            Status status = new Status(PostImageUrl,false,false,key,lastUpdate);

                            statusRef.updateChildren(addStatus);
                            statusRef.child("status").child(key).setValue(status);
                            progressDialog.dismiss();
                            Toast.makeText(requireContext(), "Status Upload Successfully",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(requireContext(),Home.class));
                            requireActivity().finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();
                            Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    progressDialog.dismiss();
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

   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    pickResultLauncher.launch(intent);
                    Toast.makeText(requireContext(), "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Enable Permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*/

    public byte[] getDownsizedImageBytes(Bitmap fullBitmap, int scaleWidth, int scaleHeight) throws IOException {

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, scaleWidth, scaleHeight, true);

        // 2. Instantiate the downsized image content as a byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] downsizedImageBytes = baos.toByteArray();

        return downsizedImageBytes;
    }
}