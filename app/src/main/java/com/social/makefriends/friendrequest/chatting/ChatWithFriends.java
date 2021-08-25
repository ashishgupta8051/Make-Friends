package com.social.makefriends.friendrequest.chatting;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.social.makefriends.R;
import com.social.makefriends.activity.Chats;
import com.social.makefriends.adapter.RetrieveMessageAdapter;
import com.social.makefriends.friendrequest.SendFriendRequest;
import com.social.makefriends.friendrequest.chatting.background.SelectWallpaper;
import com.social.makefriends.model.ChatMessages;
import com.social.makefriends.model.ExistsChatUser;
import com.social.makefriends.model.UserDetails;
import com.social.makefriends.notification.Client;
import com.social.makefriends.notification.Data;
import com.social.makefriends.notification.MyResponse;
import com.social.makefriends.notification.Senders;
import com.social.makefriends.notification.Token;
import com.social.makefriends.utils.APIService;
import com.social.makefriends.utils.CheckInternetConnection;
import com.social.makefriends.utils.SharedPrefManager;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class ChatWithFriends extends AppCompatActivity {
    private String UserId,Value,Image,CurrentUserId,CurrentDate,CurrentTime,MessageType,MsgSentDetails,
            CurrentTime2,CurrentDate2,Position,CameraImageUri,MultipleImageUri,FileUri,AudioUri,VideoUri,chatBackground;
    private EmojiEditText send_message_input;
    private ImageView SendMessage,Thumb,back_image,Emoji,chatWallpaperBackground;
    private ImageView Attachment,muetIconImg;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private CircleImageView ProfileImage;
    private TextView Name,OnlineStatus;
    private DatabaseReference MessageRef,CheckUserIdRef,UserDetails,muteNotificationRef,muteNotificationRefD;
    private StorageReference storageMsgRef;
    private RetrieveMessageAdapter retrieveMessageAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<ChatMessages> list = new ArrayList<>();
    private RecyclerView chat_recycler;
    private ValueEventListener msgSendListener;
    private static final  int PERMISSION = 999;
    private ArrayList<Uri> selectedImageUri = new ArrayList<>();
    private int up = 0;
    private int k = 0;
    private ProgressBar progressBar;
    private Bitmap cameraImageMap;
    private Uri selected_message_image;
    private Uri file_uri;
    private Boolean check = false;
    private SharedPrefManager sharedPrefManager;
    private APIService apiService;
    private ActivityResultLauncher<Intent> cameraResultLauncher;
    private ActivityResultLauncher<Intent> galleryResultLauncher;
    private ActivityResultLauncher<Intent> fileResultLauncher;
    private ActivityResultLauncher<Intent> audioResultLauncher;
    private ActivityResultLauncher<Intent> videoResultLauncher;
    private BroadcastReceiver broadcastReceiver = new CheckInternetConnection();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_friend);

        sharedPrefManager = new SharedPrefManager(this);
        String wallpeprb = sharedPrefManager.getBackgroundWallpaper();

        apiService = Client.getClient().create(APIService.class);

        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        //make change more option to white
        toolbar.getOverflowIcon().setTint(Color.WHITE);
        //Hide soft input keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        progressBar = findViewById(R.id.progressBar3);

        UserId = getIntent().getExtras().get("UserId").toString();
        Value = getIntent().getExtras().get("Value").toString();
        Image = getIntent().getExtras().get("Image").toString();
        chatBackground = getIntent().getExtras().get("ChatBackground").toString();

        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserId = firebaseAuth.getCurrentUser().getUid();
        firebaseUser = firebaseAuth.getCurrentUser();

        storageMsgRef = FirebaseStorage.getInstance().getReference("Messages");
        MessageRef = FirebaseDatabase.getInstance().getReference("Messages");
        CheckUserIdRef = FirebaseDatabase.getInstance().getReference("Exists Chat User");
        UserDetails = FirebaseDatabase.getInstance().getReference("User Details");
        muteNotificationRef = FirebaseDatabase.getInstance().getReference("Notification").child(CurrentUserId);
        muteNotificationRefD = FirebaseDatabase.getInstance().getReference("Notification").child(UserId);

        send_message_input = (EmojiEditText)findViewById(R.id.chat_input_box);
        SendMessage = (ImageView)findViewById(R.id.chat_message3);
        Thumb = (ImageView)findViewById(R.id.thumb);
        Attachment = (ImageView) findViewById(R.id.chat_attachment);
        back_image = (ImageView)findViewById(R.id.chat_back_button);
        Emoji = (ImageView)findViewById(R.id.emoji);

        muetIconImg = findViewById(R.id.mute_icon);

        ProfileImage = (CircleImageView)findViewById(R.id.chat_User_Profile_Image);

        Name = (TextView)findViewById(R.id.chat_User_Name);
        OnlineStatus = (TextView)findViewById(R.id.chat_Online_Status);

        chatWallpaperBackground = findViewById(R.id.chat_background);
        if (wallpeprb.contentEquals("d")){
            chatWallpaperBackground.setImageResource(R.drawable.d);
        }else if (wallpeprb.contentEquals("l1")){
            chatWallpaperBackground.setImageResource(R.drawable.l1);
        }else if (wallpeprb.contentEquals("l2")){
            chatWallpaperBackground.setImageResource(R.drawable.l2);
        }else if (wallpeprb.contentEquals("l3")){
            chatWallpaperBackground.setImageResource(R.drawable.l3);
        }else if (wallpeprb.contentEquals("l4")){
            chatWallpaperBackground.setImageResource(R.drawable.l4);
        }else if (wallpeprb.contentEquals("l5")){
            chatWallpaperBackground.setImageResource(R.drawable.l5);
        }else if (wallpeprb.contentEquals("l6")){
            chatWallpaperBackground.setImageResource(R.drawable.l6);
        }else if (wallpeprb.contentEquals("l7")){
            chatWallpaperBackground.setImageResource(R.drawable.l7);
        }else if (wallpeprb.contentEquals("d1")){
            chatWallpaperBackground.setImageResource(R.drawable.d1);
        }else if (wallpeprb.contentEquals("d2")){
            chatWallpaperBackground.setImageResource(R.drawable.d2);
        }else if (wallpeprb.contentEquals("d3")){
            chatWallpaperBackground.setImageResource(R.drawable.d3);
        }else if (wallpeprb.contentEquals("d4")){
            chatWallpaperBackground.setImageResource(R.drawable.d4);
        }else if (wallpeprb.contentEquals("d5")){
            chatWallpaperBackground.setImageResource(R.drawable.d5);
        }else if (wallpeprb.contentEquals("d6")){
            chatWallpaperBackground.setImageResource(R.drawable.d6);
        }else if (wallpeprb.contentEquals("d7")){
            chatWallpaperBackground.setImageResource(R.drawable.d7);
        }else {
            Picasso.get().load(wallpeprb).into(chatWallpaperBackground);
        }

        Calendar Date = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yy");
        CurrentDate = currentDate.format(Date.getTime());

        Calendar Time = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        CurrentTime = currentTime.format(Time.getTime());

        Calendar Date2 = Calendar.getInstance();
        SimpleDateFormat currentDate2 = new SimpleDateFormat("dd-MMM-yyyy");
        CurrentDate2 = currentDate2.format(Date2.getTime());

        Calendar Time2 = Calendar.getInstance();
        SimpleDateFormat currentTime2 = new SimpleDateFormat("hh:mm aa");
        CurrentTime2 = currentTime2.format(Time2.getTime());

        chat_recycler = (RecyclerView)findViewById(R.id.chat_recyclerView);
        linearLayoutManager = new LinearLayoutManager(ChatWithFriends.this);
        linearLayoutManager.setStackFromEnd(true);
        chat_recycler.setHasFixedSize(true);
        chat_recycler.setLayoutManager(linearLayoutManager);
        retrieveMessageAdapter = new RetrieveMessageAdapter(ChatWithFriends.this,list,Image,Value,UserId,chatBackground);
        chat_recycler.setAdapter(retrieveMessageAdapter);

        muteNotificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(UserId).exists()){
                    muetIconImg.setVisibility(View.VISIBLE);
                }else {
                    muetIconImg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        UserDetails.child(UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                String Image = userDetails.getUserProfileImageUrl();
                String Online_Status = userDetails.getOnlineStatus();
                String Online_Time = userDetails.getOnlineTime();
                String Online_Date = userDetails.getOnlineDate();
                if (Online_Status.equals("Online")){
                    OnlineStatus.setText(Online_Status);
                }else if (Online_Date.equals(CurrentDate2)){
                    OnlineStatus.setText("Last Seen "+Online_Time);
                }else {
                    OnlineStatus.setText("Last Seen "+Online_Date);
                }
                Name.setText(userDetails.getUserName());
                if (Image.contentEquals("None")){
                    ProfileImage.setImageResource(R.drawable.profile_image);
                }else {
                    Picasso.get().load(Image).fit().placeholder(R.drawable.profile_image).into(ProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToChatList();
            }
        });

        Thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIcons();
            }
        });

        Attachment.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatWithFriends.this);
                View view = getLayoutInflater().inflate(R.layout.bottomsheet_attachment,null);

                final ImageView pickCamera = (ImageView) view.findViewById(R.id.pick_camera_click_image);
                final ImageView pickImage = (ImageView) view.findViewById(R.id.pick_image);
                final ImageView pickVideo = (ImageView) view.findViewById(R.id.pick_video);
                final ImageView pickAudio = (ImageView) view.findViewById(R.id.pick_audio);
                final ImageView pickFiles = (ImageView) view.findViewById(R.id.pick_file);

                builder.setView(view);
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                alertDialog.show();

                pickCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(ChatWithFriends.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(ChatWithFriends.this,new String[]{
                                    Manifest.permission.CAMERA
                            },1);
                        }else {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            //startActivityForResult(cameraIntent, PICK_CAMERA_IMAGE);
                            cameraResultLauncher.launch(cameraIntent);
                        }
                        alertDialog.dismiss();
                    }
                });

                pickImage.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("IntentReset")
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(ChatWithFriends.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            String[] mimeType = {"image/png","image/jpg","image/jpeg"};
                            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeType);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            galleryResultLauncher.launch(intent);
                            alertDialog.dismiss();
                        }else {
                            requestPermission();
                        }
                    }
                });

                pickFiles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(ChatWithFriends.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Intent chooseFile = new Intent();
                            chooseFile.setAction(Intent.ACTION_GET_CONTENT);
                            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                            chooseFile.setType("applications/*");
                            String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                    "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                    "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                            chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                            chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            fileResultLauncher.launch(Intent.createChooser(chooseFile,"Choose a file"));
                            alertDialog.dismiss();
                        }else {
                            requestPermission();
                        }
                    }
                });

                pickAudio.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("IntentReset")
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(ChatWithFriends.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent();
                            intent.setType("audio/*");
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            audioResultLauncher.launch(intent);
                            alertDialog.dismiss();
                        }else {
                            requestPermission();
                        }
                    }
                });

                pickVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(ChatWithFriends.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent();
                            intent.setType("video/*");
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            videoResultLauncher.launch(intent);
                            alertDialog.dismiss();
                        }else {
                            requestPermission();
                        }
                    }
                });
            }
        });

        //Initialize emoji
        EmojiPopup popup = EmojiPopup.Builder.fromRootView(findViewById(R.id.message_const1)).build(send_message_input);
        Emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!check){
                    popup.toggle();
                    Emoji.setImageResource(R.drawable.keyboard);
                    check = true;
                }else {
                    popup.toggle();
                    Emoji.setImageResource(R.drawable.emoji);
                    check = false;
                }
            }
        });

        SendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Emoji.setImageResource(R.drawable.emoji);
                check = false;
                String SendMessageInput = send_message_input.getText().toString();
                sendMessage(SendMessageInput);
            }
        });

        fetchAllMessages();

        cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
               new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData();
                    cameraImageMap = (Bitmap) data.getExtras().get("data");
                    sendCameraImage();
                }else {
                    Log.e("Error","Camera Error");
                }
            }
        });

        galleryResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
               new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData();
                    selectedImageUri.clear();
                    if (data.getClipData() != null){
                        int count = data.getClipData().getItemCount();
                        if (count <= 10){
                            for (int i = 0 ; i < count; i++){
                                selected_message_image = data.getClipData().getItemAt(i).getUri();
                                selectedImageUri.add(selected_message_image);
                            }
                            selectedImages(selectedImageUri);
                        }else {
                            Toast.makeText(getApplicationContext(), "You can select only 10 images.", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        selected_message_image = data.getData();
                        selectedImageUri.add(selected_message_image);
                        selectedImages(selectedImageUri);
                    }
                }else {
                    Log.e("Error","Gallery Error");
                }
            }
       });

        fileResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
               new ActivityResultCallback<ActivityResult>() {
                   @Override
                   public void onActivityResult(ActivityResult result) {
                       if (result.getResultCode() == Activity.RESULT_OK){
                           Intent data = result.getData();
                           if(data.getClipData() != null){
                               int count = data.getClipData().getItemCount();
                               if (count <= 10){
                                   AlertDialog.Builder builder = new AlertDialog.Builder(ChatWithFriends.this);
                                   builder.setMessage("Are you sure you want to send");
                                   builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           for (int i = 0; i < count; i++){
                                               file_uri = data.getClipData().getItemAt(i).getUri();
                                               //get file name
                                               String uriString = file_uri.toString();
                                               File myFile = new File(uriString);
                                               String path = myFile.getAbsolutePath();
                                               String fileName = null;
                                               if (uriString.startsWith("content://")) {
                                                   Cursor cursor = null;
                                                   try {
                                                       cursor = getApplicationContext().getContentResolver().query(file_uri, null, null, null, null);
                                                       if (cursor != null && cursor.moveToFirst()) {
                                                           fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                                       }
                                                   } finally {
                                                       cursor.close();
                                                   }
                                               } else if (uriString.startsWith("file://")) {
                                                   fileName = myFile.getName();
                                               }
                                               //get file type
                                               String fileExtension;
                                               ContentResolver contentResolver = getContentResolver();
                                               MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                                               fileExtension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(file_uri));

                                               sendFiles(file_uri,fileName,fileExtension);
                                           }
                                       }
                                   }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           dialog.dismiss();
                                       }
                                   });
                                   AlertDialog alertDialog = builder.setCancelable(false).create();
                                   alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                                   alertDialog.show();
                               }else {
                                   Toast.makeText(getApplicationContext(), "You can send only 10 files.", Toast.LENGTH_SHORT).show();
                               }
                           }else {
                               AlertDialog.Builder builder = new AlertDialog.Builder(ChatWithFriends.this);
                               builder.setMessage("Are you sure you want to send");
                               builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       file_uri = data.getData();
                                       //get file name
                                       String uriString = file_uri.toString();
                                       File myFile = new File(uriString);
                                       String path = myFile.getAbsolutePath();
                                       String fileName = null;
                                       if (uriString.startsWith("content://")) {
                                           Cursor cursor = null;
                                           try {
                                               cursor = getApplicationContext().getContentResolver().query(file_uri, null, null, null, null);
                                               if (cursor != null && cursor.moveToFirst()) {
                                                   fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                               }
                                           } finally {
                                               cursor.close();
                                           }
                                       } else if (uriString.startsWith("file://")) {
                                           fileName = myFile.getName();
                                       }
                                       //get file type
                                       String fileExtension;
                                       ContentResolver contentResolver = getContentResolver();
                                       MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                                       fileExtension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(file_uri));

                                       sendFiles(file_uri,fileName,fileExtension);

                                   }
                               }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       dialog.dismiss();
                                   }
                               });
                               AlertDialog alertDialog = builder.setCancelable(false).create();
                               alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                               alertDialog.show();
                           }
                       }else {
                           Log.e("Error","File Error");
                       }
                   }
       });

        audioResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
               new ActivityResultCallback<ActivityResult>() {
                   @Override
                   public void onActivityResult(ActivityResult result) {
                       if (result.getResultCode() == Activity.RESULT_OK){
                           Intent data = result.getData();
                           if(data.getClipData() != null){
                               int count = data.getClipData().getItemCount();
                               if (count <= 10){
                                   AlertDialog.Builder builder = new AlertDialog.Builder(ChatWithFriends.this);
                                   builder.setMessage("Are you sure you want to send");
                                   builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           for (int i = 0; i < count; i++){
                                               file_uri = data.getClipData().getItemAt(i).getUri();
                                               //get file name
                                               String uriString = file_uri.toString();
                                               File myFile = new File(uriString);
                                               String path = myFile.getAbsolutePath();
                                               String displayName = null;
                                               if (uriString.startsWith("content://")) {
                                                   Cursor cursor = null;
                                                   try {
                                                       cursor = getApplicationContext().getContentResolver().query(file_uri, null, null, null, null);
                                                       if (cursor != null && cursor.moveToFirst()) {
                                                           displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                                       }
                                                   } finally {
                                                       cursor.close();
                                                   }
                                               } else if (uriString.startsWith("file://")) {
                                                   displayName = myFile.getName();
                                               }
                                               sendAudio(file_uri,displayName);
                                           }
                                       }
                                   }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           dialog.dismiss();
                                       }
                                   });
                                   AlertDialog alertDialog = builder.setCancelable(false).create();
                                   alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                                   alertDialog.show();
                               }else {
                                   Toast.makeText(getApplicationContext(), "You can send only 10 audio files.", Toast.LENGTH_SHORT).show();
                               }
                           }else {
                               AlertDialog.Builder builder = new AlertDialog.Builder(ChatWithFriends.this);
                               builder.setMessage("Are you sure you want to send");
                               builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       file_uri = data.getData();
                                       //get file name
                                       String uriString = file_uri.toString();
                                       File myFile = new File(uriString);
                                       String path = myFile.getAbsolutePath();
                                       String displayName = null;
                                       if (uriString.startsWith("content://")) {
                                           Cursor cursor = null;
                                           try {
                                               cursor = getApplicationContext().getContentResolver().query(file_uri, null, null, null, null);
                                               if (cursor != null && cursor.moveToFirst()) {
                                                   displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                               }
                                           } finally {
                                               cursor.close();
                                           }
                                       } else if (uriString.startsWith("file://")) {
                                           displayName = myFile.getName();
                                       }

                                       sendAudio(file_uri,displayName);
                                   }
                               }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       dialog.dismiss();
                                   }
                               });
                               AlertDialog alertDialog = builder.setCancelable(false).create();
                               alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                               alertDialog.show();
                           }
                       }else {
                           Log.e("Error","Audio Error");
                       }
                   }
       });

        videoResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
               new ActivityResultCallback<ActivityResult>() {
                   @Override
                   public void onActivityResult(ActivityResult result) {
                       if (result.getResultCode() == Activity.RESULT_OK){
                           Intent data = result.getData();
                           Toast.makeText(ChatWithFriends.this, "Currently working", Toast.LENGTH_SHORT).show();
                       }else {
                           Log.e("Error","Video Error");
                       }
                   }
       });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.mute_notification);

        muteNotificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(UserId).exists()){
                    menuItem.setTitle("Unmute Notification");
                    muetIconImg.setVisibility(View.VISIBLE);
                }else {
                    menuItem.setTitle("Mute Notification");
                    muetIconImg.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.refresh_chat:
                progressBar.setVisibility(View.VISIBLE);
                chat_recycler.setAdapter(null);
                MessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ChatMessages chatMessages = dataSnapshot.getValue(ChatMessages.class);
                                if (chatMessages.getReceiverId().equals(CurrentUserId) && chatMessages.getSenderId().equals(UserId) ||
                                        chatMessages.getReceiverId().equals(UserId) && chatMessages.getSenderId().equals(CurrentUserId)) {
                                    list.add(chatMessages);
                                }
                                retrieveMessageAdapter.notifyDataSetChanged();
                                chat_recycler.setAdapter(retrieveMessageAdapter);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.friend_profile:
                Intent intent = new Intent(ChatWithFriends.this, SendFriendRequest.class);
                intent.putExtra("UserId",UserId);
                intent.putExtra("Value","C");
                intent.putExtra("ChatBackground",chatBackground);
                startActivity(intent);
                finish();
                break;
            case R.id.mute_notification:
                muteNotificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(UserId).exists()){
                            muteNotificationRef.child(UserId).removeValue();
                        }else {
                            HashMap<String, Object> mute = new HashMap<String, Object>();
                            mute.put("status",UserId);
                            muteNotificationRef.child(UserId).updateChildren(mute);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.clear_chat:
                clearChats();
                break;
            case R.id.wallpaper:
                Intent intent1 = new Intent(ChatWithFriends.this, SelectWallpaper.class);
                intent1.putExtra("UserId",UserId);
                intent1.putExtra("Value",Value);
                intent1.putExtra("Image",Image);
                intent1.putExtra("ChatBackground",chatBackground);
                startActivity(intent1);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onStart() {
        super.onStart();
        checkOnlineStatus("Online");

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver,intentFilter);
        send_message_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(null)){
                    SendMessage.setVisibility(View.INVISIBLE);
                    SendMessage.setEnabled(false);
                    Thumb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendIcons();
                        }
                    });
                }else {
                    Thumb.setVisibility(View.INVISIBLE);
                    Thumb.setEnabled(false);
                    SendMessage.setVisibility(View.VISIBLE);
                    SendMessage.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (send_message_input.getText().toString().isEmpty()){
                    Thumb.setVisibility(View.VISIBLE);
                    Thumb.setEnabled(true);
                }
            }
        });

        msgSendListener = MessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChatMessages chatMessages = dataSnapshot.getValue(ChatMessages.class);
                    if (chatMessages.getReceiverId().equals(CurrentUserId) && chatMessages.getSenderId().equals(UserId)){
                        HashMap<String, Object> update = new HashMap<String, Object>();
                        update.put("messageSeenDetails","seen");
                        dataSnapshot.getRef().updateChildren(update);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkOnlineStatus("Offline");
        MessageRef.removeEventListener(msgSendListener);

        MessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ChatMessages chatMessages = dataSnapshot.getValue(ChatMessages.class);
                        if (chatMessages.getReceiverId().equals(CurrentUserId) && chatMessages.getSenderId().equals(UserId) ||
                                chatMessages.getReceiverId().equals(UserId) && chatMessages.getSenderId().equals(CurrentUserId)) {
                            list.add(chatMessages);
                        }
                        retrieveMessageAdapter.notifyDataSetChanged();
                        chat_recycler.smoothScrollToPosition(chat_recycler.getAdapter().getItemCount());
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }else {
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChatWithFriends.this, Chats.class));
        finish();
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ChatWithFriends.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(ChatWithFriends.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
        } else {
            ActivityCompat.requestPermissions(ChatWithFriends.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
        }
    }

    private void fetchAllMessages() {
        MessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ChatMessages chatMessages = dataSnapshot.getValue(ChatMessages.class);
                        if (chatMessages.getReceiverId().equals(CurrentUserId) && chatMessages.getSenderId().equals(UserId) ||
                                chatMessages.getReceiverId().equals(UserId) && chatMessages.getSenderId().equals(CurrentUserId)) {
                            list.add(chatMessages);
                        }
                        retrieveMessageAdapter.notifyDataSetChanged();
                        chat_recycler.smoothScrollToPosition(chat_recycler.getAdapter().getItemCount());
                        progressBar.setVisibility(View.GONE);
                    }
                }else {
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectedImages(List<Uri> selected_message_image) {
        for (int i = 0; i < selected_message_image.size(); i++){
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatWithFriends.this);
            View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

            TextView title;
            title = view.findViewById(R.id.progressBarTitle);

            title.setText("Sending Photo");

            builder.setCancelable(false).setView(view);
            AlertDialog progressDialog = builder.create();
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
            progressDialog.show();


            DatabaseReference MsgRef = FirebaseDatabase.getInstance().getReference("Messages");
            String MessagePushKey = MsgRef.push().getKey();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Messages").child(CurrentUserId).child("Image").child(MessagePushKey);
            storageReference.putFile(selected_message_image.get(i)).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        progressDialog.dismiss();
                        throw Objects.requireNonNull(task.getException());
                    }else {
                        return storageReference.getDownloadUrl();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        MultipleImageUri = downloadUri.toString();
                        CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child(UserId).exists()){
                                    MessageType = "image";
                                    MsgSentDetails = "not_seen";

                                    HashMap<String,Object> addMessage = new HashMap<>();
                                    addMessage.put("messageDetails",MultipleImageUri);
                                    addMessage.put("messageTime",CurrentTime);
                                    addMessage.put("messageDate",CurrentDate);
                                    addMessage.put("messageType",MessageType);
                                    addMessage.put("senderId",CurrentUserId);
                                    addMessage.put("receiverId",UserId);
                                    addMessage.put("messageId",MessagePushKey);
                                    addMessage.put("messageSeenDetails",MsgSentDetails);
                                    addMessage.put("fileName",null);
                                    addMessage.put("fileType",null);
                                    addMessage.put("senderSideMsgDelete","not_delete");
                                    addMessage.put("receiverSideMsgDelete","not_delete");
                                    addMessage.put("forward","no");

                                    MsgRef.child(MessagePushKey).setValue(addMessage);

                                    HashMap<String,Object> position = new HashMap<String, Object>();
                                    position.put("firstPosition","first");
                                    CheckUserIdRef.child(CurrentUserId).child(UserId).updateChildren(position);
                                    CheckUserIdRef.child(UserId).child(CurrentUserId).updateChildren(position);

                                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        String key = dataSnapshot.getKey();
                                                        if (!key.equals(UserId)){
                                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                                            position.put("firstPosition","");
                                                            CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });

                                    CheckUserIdRef.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Query query1 = CheckUserIdRef.child(UserId).orderByChild("firstPosition").equalTo("first");
                                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        String Key = dataSnapshot.getKey();
                                                        if (!Key.equals(CurrentUserId)){
                                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                                            position.put("firstPosition","");
                                                            CheckUserIdRef.child(UserId).child(Key).updateChildren(position);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                                    progressDialog.dismiss();

                                }else {
                                    MessageType = "image";
                                    MsgSentDetails = "not_seen";
                                    Position = "first";

                                    HashMap<String,Object> addMessage = new HashMap<>();
                                    addMessage.put("messageDetails",MultipleImageUri);
                                    addMessage.put("messageTime",CurrentTime);
                                    addMessage.put("messageDate",CurrentDate);
                                    addMessage.put("messageType",MessageType);
                                    addMessage.put("senderId",CurrentUserId);
                                    addMessage.put("receiverId",UserId);
                                    addMessage.put("messageId",MessagePushKey);
                                    addMessage.put("messageSeenDetails",MsgSentDetails);
                                    addMessage.put("fileName",null);
                                    addMessage.put("fileType",null);
                                    addMessage.put("senderSideMsgDelete","not_delete");
                                    addMessage.put("receiverSideMsgDelete","not_delete");
                                    addMessage.put("forward","no");

                                    MsgRef.child(MessagePushKey).setValue(addMessage);

                                    ExistsChatUser senders = new ExistsChatUser(UserId,Position);
                                    CheckUserIdRef.child(CurrentUserId).child(UserId).setValue(senders);
                                    ExistsChatUser receivers = new ExistsChatUser(CurrentUserId,Position);
                                    CheckUserIdRef.child(UserId).child(CurrentUserId).setValue(receivers);

                                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        String key = dataSnapshot.getKey();
                                                        if (!key.equals(UserId)){
                                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                                            position.put("firstPosition","");
                                                            CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });

                                    CheckUserIdRef.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Query query1 = CheckUserIdRef.child(UserId).orderByChild("firstPosition").equalTo("first");
                                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        String Key = dataSnapshot.getKey();
                                                        if (!Key.equals(CurrentUserId)){
                                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                                            position.put("firstPosition","");
                                                            CheckUserIdRef.child(UserId).child(Key).updateChildren(position);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    }else {
                        Toast.makeText(ChatWithFriends.this, "Something is wrong !!!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
            /*up++;
            k++;*/
        }
        //Send Notification
        String msg = "Image";
        UserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.child(CurrentUserId).getValue(com.social.makefriends.model.UserDetails.class);
                UserDetails userDetails2 = snapshot.child(UserId).getValue(com.social.makefriends.model.UserDetails.class);
                String onlineStatus = userDetails2.getOnlineStatus();
                muteNotificationRefD.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(CurrentUserId).exists()){
                            Log.e("Message","Exists");
                        }else {
                            if (onlineStatus.equals("Offline")){
                                sendNotification(UserId,userDetails.getUserName(),msg);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String sendMessageInput) {
        CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(UserId).exists()){
                    MessageType = "text";
                    MsgSentDetails = "not_seen";
                    String MessagePushKey = MessageRef.push().getKey();

                    HashMap<String,Object> addMessage = new HashMap<>();
                    addMessage.put("messageDetails",sendMessageInput);
                    addMessage.put("messageTime",CurrentTime);
                    addMessage.put("messageDate",CurrentDate);
                    addMessage.put("messageType",MessageType);
                    addMessage.put("senderId",CurrentUserId);
                    addMessage.put("receiverId",UserId);
                    addMessage.put("messageId",MessagePushKey);
                    addMessage.put("messageSeenDetails",MsgSentDetails);
                    addMessage.put("fileName",null);
                    addMessage.put("fileType",null);
                    addMessage.put("senderSideMsgDelete","not_delete");
                    addMessage.put("receiverSideMsgDelete","not_delete");
                    addMessage.put("forward","no");

                    MessageRef.child(MessagePushKey).setValue(addMessage);

                    HashMap<String,Object> position = new HashMap<String, Object>();
                    position.put("firstPosition","first");
                    CheckUserIdRef.child(CurrentUserId).child(UserId).updateChildren(position);
                    CheckUserIdRef.child(UserId).child(CurrentUserId).updateChildren(position);

                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String key = dataSnapshot.getKey();
                                        if (!key.equals(UserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    CheckUserIdRef.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(UserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String Key = dataSnapshot.getKey();
                                        if (!Key.equals(CurrentUserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(UserId).child(Key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    MessageType = "text";
                    MsgSentDetails = "not_seen";
                    Position = "first";
                    String MessagePushKey = MessageRef.push().getKey();

                    HashMap<String,Object> addMessage = new HashMap<>();
                    addMessage.put("messageDetails",sendMessageInput);
                    addMessage.put("messageTime",CurrentTime);
                    addMessage.put("messageDate",CurrentDate);
                    addMessage.put("messageType",MessageType);
                    addMessage.put("senderId",CurrentUserId);
                    addMessage.put("receiverId",UserId);
                    addMessage.put("messageId",MessagePushKey);
                    addMessage.put("messageSeenDetails",MsgSentDetails);
                    addMessage.put("fileName",null);
                    addMessage.put("fileType",null);
                    addMessage.put("senderSideMsgDelete","not_delete");
                    addMessage.put("receiverSideMsgDelete","not_delete");
                    addMessage.put("forward","no");

                    MessageRef.child(MessagePushKey).setValue(addMessage);

                    ExistsChatUser senders = new ExistsChatUser(UserId,Position);
                    CheckUserIdRef.child(CurrentUserId).child(UserId).setValue(senders);
                    ExistsChatUser receivers = new ExistsChatUser(CurrentUserId,Position);
                    CheckUserIdRef.child(UserId).child(CurrentUserId).setValue(receivers);

                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String key = dataSnapshot.getKey();
                                        if (!key.equals(UserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    CheckUserIdRef.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(UserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String Key = dataSnapshot.getKey();
                                        if (!Key.equals(CurrentUserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(UserId).child(Key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                SendMessage.setVisibility(View.INVISIBLE);
                SendMessage.setEnabled(false);
                send_message_input.setText(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Send Notification
        String msg = sendMessageInput;
        UserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.child(CurrentUserId).getValue(com.social.makefriends.model.UserDetails.class);
                UserDetails userDetails2 = snapshot.child(UserId).getValue(com.social.makefriends.model.UserDetails.class);
                String onlineStatus = userDetails2.getOnlineStatus();
                muteNotificationRefD.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(CurrentUserId).exists()){
                            Log.e("Message","Exists");
                        }else {
                            if (onlineStatus.equals("Offline")){
                                sendNotification(UserId,userDetails.getUserName(),msg);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendAudio(Uri file_uri, String fileName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatWithFriends.this);
        View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

        TextView title,message;
        title = view.findViewById(R.id.progressBarTitle);
        message = view.findViewById(R.id.progressBarMessage);

        title.setText("Send Audio");

        builder.setCancelable(false).setView(view);
        AlertDialog progressDialog = builder.create();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
        progressDialog.show();

        DatabaseReference MsgRef = FirebaseDatabase.getInstance().getReference("Messages");
        String MessagePushKey = MsgRef.push().getKey();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Messages").child(CurrentUserId).child("Audio").child(MessagePushKey);
        storageReference.putFile(file_uri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                message.setText("Uploaded " + (int) progress + "%");
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    progressDialog.dismiss();
                    throw Objects.requireNonNull(task.getException());
                }else {
                    return storageReference.getDownloadUrl();
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    AudioUri = downloadUri.toString();
                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(UserId).exists()){
                                MessageType = "audio";
                                MsgSentDetails = "not_seen";

                                HashMap<String,Object> addMessage = new HashMap<>();
                                addMessage.put("messageDetails",AudioUri);
                                addMessage.put("messageTime",CurrentTime);
                                addMessage.put("messageDate",CurrentDate);
                                addMessage.put("messageType",MessageType);
                                addMessage.put("senderId",CurrentUserId);
                                addMessage.put("receiverId",UserId);
                                addMessage.put("messageId",MessagePushKey);
                                addMessage.put("messageSeenDetails",MsgSentDetails);
                                addMessage.put("fileName",fileName);
                                addMessage.put("fileType",null);
                                addMessage.put("senderSideMsgDelete","not_delete");
                                addMessage.put("receiverSideMsgDelete","not_delete");
                                addMessage.put("forward","no");

                                MsgRef.child(MessagePushKey).setValue(addMessage);

                                HashMap<String,Object> position = new HashMap<String, Object>();
                                position.put("firstPosition","first");
                                CheckUserIdRef.child(CurrentUserId).child(UserId).updateChildren(position);
                                CheckUserIdRef.child(UserId).child(CurrentUserId).updateChildren(position);

                                CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String key = dataSnapshot.getKey();
                                                    if (!key.equals(UserId)){
                                                        HashMap<String,Object> position = new HashMap<String, Object>();
                                                        position.put("firstPosition","");
                                                        CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });

                                CheckUserIdRef.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Query query1 = CheckUserIdRef.child(UserId).orderByChild("firstPosition").equalTo("first");
                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String Key = dataSnapshot.getKey();
                                                    if (!Key.equals(CurrentUserId)){
                                                        HashMap<String,Object> position = new HashMap<String, Object>();
                                                        position.put("firstPosition","");
                                                        CheckUserIdRef.child(UserId).child(Key).updateChildren(position);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                                progressDialog.dismiss();
                            }else {
                                MessageType = "audio";
                                MsgSentDetails = "not_seen";
                                Position = "first";

                                HashMap<String,Object> addMessage = new HashMap<>();
                                addMessage.put("messageDetails",FileUri);
                                addMessage.put("messageTime",CurrentTime);
                                addMessage.put("messageDate",CurrentDate);
                                addMessage.put("messageType",MessageType);
                                addMessage.put("senderId",CurrentUserId);
                                addMessage.put("receiverId",UserId);
                                addMessage.put("messageId",MessagePushKey);
                                addMessage.put("messageSeenDetails",MsgSentDetails);
                                addMessage.put("fileName",fileName);
                                addMessage.put("fileType",null);
                                addMessage.put("senderSideMsgDelete","not_delete");
                                addMessage.put("receiverSideMsgDelete","not_delete");
                                addMessage.put("forward","no");

                                MsgRef.child(MessagePushKey).setValue(addMessage);

                                ExistsChatUser senders = new ExistsChatUser(UserId,Position);
                                CheckUserIdRef.child(CurrentUserId).child(UserId).setValue(senders);
                                ExistsChatUser receivers = new ExistsChatUser(CurrentUserId,Position);
                                CheckUserIdRef.child(UserId).child(CurrentUserId).setValue(receivers);

                                CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String key = dataSnapshot.getKey();
                                                    if (!key.equals(UserId)){
                                                        HashMap<String,Object> position = new HashMap<String, Object>();
                                                        position.put("firstPosition","");
                                                        CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });

                                CheckUserIdRef.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Query query1 = CheckUserIdRef.child(UserId).orderByChild("firstPosition").equalTo("first");
                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String Key = dataSnapshot.getKey();
                                                    if (!Key.equals(CurrentUserId)){
                                                        HashMap<String,Object> position = new HashMap<String, Object>();
                                                        position.put("firstPosition","");
                                                        CheckUserIdRef.child(UserId).child(Key).updateChildren(position);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                               progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    Toast.makeText(ChatWithFriends.this, "Something is wrong !!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });

        //Send Notification
        String msg = fileName;
        UserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.child(CurrentUserId).getValue(com.social.makefriends.model.UserDetails.class);
                UserDetails userDetails2 = snapshot.child(UserId).getValue(com.social.makefriends.model.UserDetails.class);
                String onlineStatus = userDetails2.getOnlineStatus();
                muteNotificationRefD.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(CurrentUserId).exists()){
                            Log.e("Message","Exists");
                        }else {
                            if (onlineStatus.equals("Offline")){
                                sendNotification(UserId,userDetails.getUserName(),msg);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendFiles(Uri file_uri, String fileName, String fileType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatWithFriends.this);
        View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

        TextView title,message;
        title = view.findViewById(R.id.progressBarTitle);
        message = view.findViewById(R.id.progressBarMessage);

        title.setText("Sending Files");

        builder.setCancelable(false).setView(view);
        AlertDialog progressDialog = builder.create();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
        progressDialog.show();

        DatabaseReference MsgRef = FirebaseDatabase.getInstance().getReference("Messages");
        String MessagePushKey = MsgRef.push().getKey();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Messages").child(CurrentUserId).child("File").child(MessagePushKey);
        storageReference.putFile(file_uri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                message.setText("Uploaded " + (int) progress + "%");
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    progressDialog.dismiss();
                    throw Objects.requireNonNull(task.getException());
                }else {
                    return storageReference.getDownloadUrl();
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    FileUri = downloadUri.toString();
                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(UserId).exists()){
                                MessageType = "file";
                                MsgSentDetails = "not_seen";

                                Log.e(TAG,fileType+" "+fileName);
                                HashMap<String,Object> addMessage = new HashMap<>();
                                addMessage.put("messageDetails",FileUri);
                                addMessage.put("messageTime",CurrentTime);
                                addMessage.put("messageDate",CurrentDate);
                                addMessage.put("messageType",MessageType);
                                addMessage.put("senderId",CurrentUserId);
                                addMessage.put("receiverId",UserId);
                                addMessage.put("messageId",MessagePushKey);
                                addMessage.put("messageSeenDetails",MsgSentDetails);
                                addMessage.put("fileName",fileName);
                                addMessage.put("fileType",fileType);
                                addMessage.put("senderSideMsgDelete","not_delete");
                                addMessage.put("receiverSideMsgDelete","not_delete");
                                addMessage.put("forward","no");

                                MsgRef.child(MessagePushKey).setValue(addMessage);

                                HashMap<String,Object> position = new HashMap<String, Object>();
                                position.put("firstPosition","first");
                                CheckUserIdRef.child(CurrentUserId).child(UserId).updateChildren(position);
                                CheckUserIdRef.child(UserId).child(CurrentUserId).updateChildren(position);

                                CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String key = dataSnapshot.getKey();
                                                    if (!key.equals(UserId)){
                                                        HashMap<String,Object> position = new HashMap<String, Object>();
                                                        position.put("firstPosition","");
                                                        CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });

                                CheckUserIdRef.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Query query1 = CheckUserIdRef.child(UserId).orderByChild("firstPosition").equalTo("first");
                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String Key = dataSnapshot.getKey();
                                                    if (!Key.equals(CurrentUserId)){
                                                        HashMap<String,Object> position = new HashMap<String, Object>();
                                                        position.put("firstPosition","");
                                                        CheckUserIdRef.child(UserId).child(Key).updateChildren(position);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                               progressDialog.dismiss();
                            }else {
                                MessageType = "file";
                                MsgSentDetails = "not_seen";
                                Position = "first";

                                HashMap<String,Object> addMessage = new HashMap<>();
                                addMessage.put("messageDetails",FileUri);
                                addMessage.put("messageTime",CurrentTime);
                                addMessage.put("messageDate",CurrentDate);
                                addMessage.put("messageType",MessageType);
                                addMessage.put("senderId",CurrentUserId);
                                addMessage.put("receiverId",UserId);
                                addMessage.put("messageId",MessagePushKey);
                                addMessage.put("messageSeenDetails",MsgSentDetails);
                                addMessage.put("fileName",fileName);
                                addMessage.put("fileType",fileType);
                                addMessage.put("senderSideMsgDelete","not_delete");
                                addMessage.put("receiverSideMsgDelete","not_delete");
                                addMessage.put("forward","no");

                                MsgRef.child(MessagePushKey).setValue(addMessage);

                                ExistsChatUser senders = new ExistsChatUser(UserId,Position);
                                CheckUserIdRef.child(CurrentUserId).child(UserId).setValue(senders);
                                ExistsChatUser receivers = new ExistsChatUser(CurrentUserId,Position);
                                CheckUserIdRef.child(UserId).child(CurrentUserId).setValue(receivers);

                                CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String key = dataSnapshot.getKey();
                                                    if (!key.equals(UserId)){
                                                        HashMap<String,Object> position = new HashMap<String, Object>();
                                                        position.put("firstPosition","");
                                                        CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });

                                CheckUserIdRef.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Query query1 = CheckUserIdRef.child(UserId).orderByChild("firstPosition").equalTo("first");
                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String Key = dataSnapshot.getKey();
                                                    if (!Key.equals(CurrentUserId)){
                                                        HashMap<String,Object> position = new HashMap<String, Object>();
                                                        position.put("firstPosition","");
                                                        CheckUserIdRef.child(UserId).child(Key).updateChildren(position);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    Toast.makeText(ChatWithFriends.this, "Something is wrong !!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });

        //Send Notification
        String msg = fileName;
        UserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.child(CurrentUserId).getValue(com.social.makefriends.model.UserDetails.class);
                UserDetails userDetails2 = snapshot.child(UserId).getValue(com.social.makefriends.model.UserDetails.class);
                String onlineStatus = userDetails2.getOnlineStatus();
                muteNotificationRefD.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(CurrentUserId).exists()){
                            Log.e("Message","Exists");
                        }else {
                            if (onlineStatus.equals("Offline")){
                                sendNotification(UserId,userDetails.getUserName(),msg);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendCameraImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatWithFriends.this);
        View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

        TextView title,message;
        title = view.findViewById(R.id.progressBarTitle);
        message = view.findViewById(R.id.progressBarMessage);

        title.setText("Sending Image");

        builder.setCancelable(false).setView(view);
        AlertDialog progressDialog = builder.create();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
        progressDialog.show();

        DatabaseReference MsfRef = FirebaseDatabase.getInstance().getReference("Messages");
        String MessagePushKey = MsfRef.push().getKey();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Messages").child(CurrentUserId).child("Image").child(MessagePushKey);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        cameraImageMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] image = stream.toByteArray();

        storageReference.putBytes(image).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                message.setText("Uploaded " + (int) progress + "%");
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    progressDialog.dismiss();
                    throw Objects.requireNonNull(task.getException());
                }else {
                    return storageReference.getDownloadUrl();
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    CameraImageUri = downloadUri.toString();
                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(UserId).exists()){
                                MessageType = "image";
                                MsgSentDetails = "not_seen";

                                HashMap<String,Object> addMessage = new HashMap<>();
                                addMessage.put("messageDetails",CameraImageUri);
                                addMessage.put("messageTime",CurrentTime);
                                addMessage.put("messageDate",CurrentDate);
                                addMessage.put("messageType",MessageType);
                                addMessage.put("senderId",CurrentUserId);
                                addMessage.put("receiverId",UserId);
                                addMessage.put("messageId",MessagePushKey);
                                addMessage.put("messageSeenDetails",MsgSentDetails);
                                addMessage.put("fileName",null);
                                addMessage.put("fileType",null);
                                addMessage.put("senderSideMsgDelete","not_delete");
                                addMessage.put("receiverSideMsgDelete","not_delete");
                                addMessage.put("forward","no");

                                MsfRef.child(MessagePushKey).setValue(addMessage);

                                HashMap<String,Object> position = new HashMap<String, Object>();
                                position.put("firstPosition","first");
                                CheckUserIdRef.child(CurrentUserId).child(UserId).updateChildren(position);
                                CheckUserIdRef.child(UserId).child(CurrentUserId).updateChildren(position);

                                CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String key = dataSnapshot.getKey();
                                                    if (!key.equals(UserId)){
                                                        HashMap<String,Object> position = new HashMap<String, Object>();
                                                        position.put("firstPosition","");
                                                        CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });

                                CheckUserIdRef.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Query query1 = CheckUserIdRef.child(UserId).orderByChild("firstPosition").equalTo("first");
                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String Key = dataSnapshot.getKey();
                                                    if (!Key.equals(CurrentUserId)){
                                                        HashMap<String,Object> position = new HashMap<String, Object>();
                                                        position.put("firstPosition","");
                                                        CheckUserIdRef.child(UserId).child(Key).updateChildren(position);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                                progressDialog.dismiss();

                            }else {
                                MessageType = "image";
                                MsgSentDetails = "not_seen";
                                Position = "first";

                                HashMap<String,Object> addMessage = new HashMap<>();
                                addMessage.put("messageDetails",CameraImageUri);
                                addMessage.put("messageTime",CurrentTime);
                                addMessage.put("messageDate",CurrentDate);
                                addMessage.put("messageType",MessageType);
                                addMessage.put("senderId",CurrentUserId);
                                addMessage.put("receiverId",UserId);
                                addMessage.put("messageId",MessagePushKey);
                                addMessage.put("messageSeenDetails",MsgSentDetails);
                                addMessage.put("fileName",null);
                                addMessage.put("fileType",null);
                                addMessage.put("senderSideMsgDelete","not_delete");
                                addMessage.put("receiverSideMsgDelete","not_delete");
                                addMessage.put("forward","no");

                                MsfRef.child(MessagePushKey).setValue(addMessage);

                                ExistsChatUser senders = new ExistsChatUser(UserId,Position);
                                CheckUserIdRef.child(CurrentUserId).child(UserId).setValue(senders);
                                ExistsChatUser receivers = new ExistsChatUser(CurrentUserId,Position);
                                CheckUserIdRef.child(UserId).child(CurrentUserId).setValue(receivers);

                                CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String key = dataSnapshot.getKey();
                                                    if (!key.equals(UserId)){
                                                        HashMap<String,Object> position = new HashMap<String, Object>();
                                                        position.put("firstPosition","");
                                                        CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });

                                CheckUserIdRef.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Query query1 = CheckUserIdRef.child(UserId).orderByChild("firstPosition").equalTo("first");
                                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String Key = dataSnapshot.getKey();
                                                    if (!Key.equals(CurrentUserId)){
                                                        HashMap<String,Object> position = new HashMap<String, Object>();
                                                        position.put("firstPosition","");
                                                        CheckUserIdRef.child(UserId).child(Key).updateChildren(position);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    Toast.makeText(ChatWithFriends.this, "Something is wrong !!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });

        //Send Notification
        String msg = "Image";
        UserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.child(CurrentUserId).getValue(com.social.makefriends.model.UserDetails.class);
                UserDetails userDetails2 = snapshot.child(UserId).getValue(com.social.makefriends.model.UserDetails.class);
                String onlineStatus = userDetails2.getOnlineStatus();
                muteNotificationRefD.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(CurrentUserId).exists()){
                            Log.e("Message","Exists");
                        }else {
                            if (onlineStatus.equals("Offline")){
                                sendNotification(UserId,userDetails.getUserName(),msg);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendIcons() {
        CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(UserId).exists()){
                    MessageType = "icon";
                    MsgSentDetails = "not_seen";
                    String MessagePushKey = MessageRef.push().getKey();

                    HashMap<String,Object> addMessage = new HashMap<>();
                    addMessage.put("messageDetails","icon");
                    addMessage.put("messageTime",CurrentTime);
                    addMessage.put("messageDate",CurrentDate);
                    addMessage.put("messageType",MessageType);
                    addMessage.put("senderId",CurrentUserId);
                    addMessage.put("receiverId",UserId);
                    addMessage.put("messageId",MessagePushKey);
                    addMessage.put("messageSeenDetails",MsgSentDetails);
                    addMessage.put("fileName",null);
                    addMessage.put("fileType",null);
                    addMessage.put("senderSideMsgDelete","not_delete");
                    addMessage.put("receiverSideMsgDelete","not_delete");
                    addMessage.put("forward","no");

                    MessageRef.child(MessagePushKey).setValue(addMessage);

                    HashMap<String,Object> position = new HashMap<String, Object>();
                    position.put("firstPosition","first");
                    CheckUserIdRef.child(CurrentUserId).child(UserId).updateChildren(position);
                    CheckUserIdRef.child(UserId).child(CurrentUserId).updateChildren(position);

                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String key = dataSnapshot.getKey();
                                        if (!key.equals(UserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    CheckUserIdRef.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(UserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String Key = dataSnapshot.getKey();
                                        if (!Key.equals(CurrentUserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(UserId).child(Key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    MessageType = "icon";
                    MsgSentDetails = "not_seen";
                    Position = "first";
                    String MessagePushKey = MessageRef.push().getKey();

                    HashMap<String,Object> addMessage = new HashMap<>();
                    addMessage.put("messageDetails","icon");
                    addMessage.put("messageTime",CurrentTime);
                    addMessage.put("messageDate",CurrentDate);
                    addMessage.put("messageType",MessageType);
                    addMessage.put("senderId",CurrentUserId);
                    addMessage.put("receiverId",UserId);
                    addMessage.put("messageId",MessagePushKey);
                    addMessage.put("messageSeenDetails",MsgSentDetails);
                    addMessage.put("fileName",null);
                    addMessage.put("fileType",null);
                    addMessage.put("senderSideMsgDelete","not_delete");
                    addMessage.put("receiverSideMsgDelete","not_delete");
                    addMessage.put("forward","no");

                    MessageRef.child(MessagePushKey).setValue(addMessage);

                    ExistsChatUser senders = new ExistsChatUser(UserId,Position);
                    CheckUserIdRef.child(CurrentUserId).child(UserId).setValue(senders);
                    ExistsChatUser receivers = new ExistsChatUser(CurrentUserId,Position);
                    CheckUserIdRef.child(UserId).child(CurrentUserId).setValue(receivers);

                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String key = dataSnapshot.getKey();
                                        if (!key.equals(UserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    CheckUserIdRef.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(UserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String Key = dataSnapshot.getKey();
                                        if (!Key.equals(CurrentUserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(UserId).child(Key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                SendMessage.setVisibility(View.INVISIBLE);
                SendMessage.setEnabled(false);
                send_message_input.setText(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Send Notification
        String msg = "👍";
        UserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.child(CurrentUserId).getValue(com.social.makefriends.model.UserDetails.class);
                UserDetails userDetails2 = snapshot.child(UserId).getValue(com.social.makefriends.model.UserDetails.class);
                String onlineStatus = userDetails2.getOnlineStatus();
                muteNotificationRefD.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(CurrentUserId).exists()){
                            Log.e("Message","Exists");
                        }else {
                            if (onlineStatus.equals("Offline")){
                                sendNotification(UserId,userDetails.getUserName(),msg);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkOnlineStatus(String status){
        HashMap<String,Object> updateStatus = new HashMap<String, Object>();
        updateStatus.put("onlineStatus",status);
        updateStatus.put("onlineDate",CurrentDate2);
        updateStatus.put("onlineTime",CurrentTime2);
        UserDetails.child(CurrentUserId).updateChildren(updateStatus);
    }

    private void backToChatList() {
        startActivity(new Intent(ChatWithFriends.this, Chats.class));
        finish();
    }

    private void clearChats() {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(ChatWithFriends.this);
        View view = getLayoutInflater().inflate(R.layout.progressdialog,null);

        TextView title,message;
        title = view.findViewById(R.id.progressBarTitle);
        message = view.findViewById(R.id.progressBarMessage);

        title.setText("Delete Message");

        builder2.setCancelable(false).setView(view);
        AlertDialog progressDialog = builder2.create();
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatWithFriends.this);
        builder.setTitle("Delete messages");
        builder.setMessage("Are you sure you want to delete all the messages ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.show();
                MessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ChatMessages chatMessages = dataSnapshot.getValue(ChatMessages.class);
                            if (chatMessages.getReceiverId().equals(CurrentUserId) && chatMessages.getSenderId().equals(UserId) ||
                                    chatMessages.getReceiverId().equals(UserId) && chatMessages.getSenderId().equals(CurrentUserId)) {
                                if (chatMessages.getSenderId().equals(CurrentUserId)){
                                    HashMap<String, Object> sender = new HashMap<String, Object>();
                                    sender.put("senderSideMsgDelete","delete");
                                    MessageRef.child(chatMessages.getMessageId()).updateChildren(sender);
                                }
                                if(chatMessages.getReceiverId().equals(CurrentUserId)){
                                    HashMap<String, Object> receiver = new HashMap<String, Object>();
                                    receiver.put("receiverSideMsgDelete","delete");
                                    MessageRef.child(chatMessages.getMessageId()).updateChildren(receiver);
                                }
                                CheckUserIdRef.child(CurrentUserId).child(UserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                        }else {
                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.setCancelable(false).create();
        alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
        alertDialog.show();
    }

    private void sendNotification(String userId, String name, String msg){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query1 = tokens.orderByKey().equalTo(userId);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(CurrentUserId,name+" : "+msg,"New Message",userId,"chatting");
                    Senders senders = new Senders(data,token.getToken());

                    apiService.sendNotification(senders).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200){
                                if (response.body().success != 1){
//                                    Toast.makeText(ChatWithFriends.this, "Failed!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatWithFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}