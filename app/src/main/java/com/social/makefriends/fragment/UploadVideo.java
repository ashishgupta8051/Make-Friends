package com.social.makefriends.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.social.makefriends.R;
import com.social.makefriends.databinding.FragmentUploadVideoBinding;

import java.io.File;

public class UploadVideo extends Fragment {
    private FragmentUploadVideoBinding binding;
    private FirebaseAuth firebaseAuth;
    private ImageView post_image,post_image_def;
    private EditText caption;
    private Button post_button;
    private Uri finalUri;
    private String Caption,CurrentDate,CurrentTime,PostImageUrl;
    private long countPost = 0;
    private long countPostProfile = 0;
    private static final int PERMISSION = 999;
    private ActivityResultLauncher<Intent> pickResultLauncher;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUploadVideoBinding.inflate(getLayoutInflater(), container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        binding.postVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("video/*");
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
                        Uri uri = intent.getData();
                        Toast.makeText(requireContext(),String.valueOf(uri),Toast.LENGTH_SHORT).show();
                    }else {
                        Log.e("Result","Error in Pick Video");
                    }
                });

        return binding.getRoot();
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

    @Override
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
    }

}