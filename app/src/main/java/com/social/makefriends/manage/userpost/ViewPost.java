package com.social.makefriends.manage.userpost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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
import com.social.makefriends.R;
import com.social.makefriends.activity.UserProfile;
import com.social.makefriends.friendrequest.SendFriendRequest;
import com.social.makefriends.friendrequest.SendFriendRequestDuplicate;
import com.social.makefriends.model.AllPost;
import com.social.makefriends.model.FavPost;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPost extends AppCompatActivity {
    private String PostId,User_Name,UserProfilePic,UserId,UsersName,Value,CurrentTime,CurrentDate,wallpaper;
    private TextView Date,Time,Caption,UserName,TotalLike,TotalDislike,Comment;
    private CircleImageView UserProfileImage;
    private ImageView PostImage,PostLike,PostDislike,PostComment,PostImageSave,MoreOption;
    private FirebaseAuth firebaseAuth;
    private static final  int PERMISSION = 999;
    private String ViewPostValue = "V";
    private FileOutputStream fileOutputStream;
    private DatabaseReference postRef,savePostRef,commentRef;
    Bitmap bitmap;
    private long countPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        getSupportActionBar().setTitle("View Post");

        firebaseAuth = FirebaseAuth.getInstance();
        String CurrentUserId = firebaseAuth.getCurrentUser().getUid();

        Calendar Date2 = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        CurrentDate = currentDate.format(Date2.getTime());

        Calendar Time2 = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        CurrentTime = currentTime.format(Time2.getTime());

        PostId = getIntent().getExtras().get("PostId").toString();
        User_Name = getIntent().getExtras().get("UserName").toString();
        UserProfilePic = getIntent().getExtras().get("ProfilePic").toString();
        UserId = getIntent().getExtras().get("CurrentUserId").toString();
        UsersName = getIntent().getExtras().get("UsersName").toString();
        Value = getIntent().getExtras().get("value").toString();
        wallpaper = getIntent().getExtras().get("ChatBackground").toString();

        UserProfileImage = (CircleImageView)findViewById(R.id.user_profile_image2);
        PostImage = (ImageView)findViewById(R.id.post_image2);
        MoreOption = (ImageView)findViewById(R.id.more_option2);
        PostLike = (ImageView)findViewById(R.id.post_like2);
        PostDislike = (ImageView)findViewById(R.id.post_dislike2);
        PostComment = (ImageView)findViewById(R.id.post_comment2);
        PostImageSave = (ImageView)findViewById(R.id.post_image_save2);

        UserName = (TextView)findViewById(R.id.user_name2);
        Date = (TextView)findViewById(R.id.post_date2);
        Time = (TextView)findViewById(R.id.post_time2);
        Caption = (TextView)findViewById(R.id.post_caption2);
        TotalLike = (TextView)findViewById(R.id.post_like_number2);
        TotalDislike = (TextView)findViewById(R.id.post_dislike_number2);
        Comment = (TextView)findViewById(R.id.post_comment_number2);

        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");
        final int[] likeCount = new int[1];
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(PostId).hasChild(CurrentUserId)){
                    likeCount[0] = (int)snapshot.child(PostId).getChildrenCount();
                    PostLike.setImageResource(R.drawable.colored_like);
                    if (likeCount[0] == 0){
                        TotalLike.setText(null);
                    }else {
                        TotalLike.setText(Integer.toString(likeCount[0]));
                    }
                }else {
                    likeCount[0] = (int)snapshot.child(PostId).getChildrenCount();
                    PostLike.setImageResource(R.drawable.uncolored_like);
                    if (likeCount[0] == 0){
                        TotalLike.setText(null);
                    }else {
                        TotalLike.setText(Integer.toString(likeCount[0]));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", String.valueOf(error));
            }
        });

        DatabaseReference DislikeRef = FirebaseDatabase.getInstance().getReference("Dislikes");
        final int[] likeCount2 = new int[1];
        DislikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(PostId).hasChild(CurrentUserId)){
                    likeCount2[0] = (int)snapshot.child(PostId).getChildrenCount();
                    PostDislike.setImageResource(R.drawable.colored_dislike);
                    if (likeCount2[0] == 0){
                        TotalDislike.setText(null);
                    }else {
                        TotalDislike.setText(Integer.toString(likeCount2[0]));
                    }
                }else {
                    likeCount2[0] = (int)snapshot.child(PostId).getChildrenCount();
                    PostDislike.setImageResource(R.drawable.uncolored_dislike);
                    if (likeCount2[0] == 0){
                        TotalDislike.setText(null);
                    }else {
                        TotalDislike.setText(Integer.toString(likeCount2[0]));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", String.valueOf(error));
            }
        });

        DatabaseReference Like = FirebaseDatabase.getInstance().getReference("Likes");
        final boolean[] LickChecker = {false};
        PostLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LickChecker[0] = true;
                Like.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (LickChecker[0] == true) {
                            if (snapshot.child(PostId).hasChild(CurrentUserId)) {
                                Like.child(PostId).child(CurrentUserId).removeValue();
                                //holder.PostLike.setImageResource(R.drawable.uncolored_like);
                            } else {
                                Like.child(PostId).child(CurrentUserId).setValue(CurrentUserId);
                                //holder.PostLike.setImageResource(R.drawable.colored_like);
                            }
                            LickChecker[0] = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Error",error.toString());
                    }
                });
            }
        });

        DatabaseReference DisLike = FirebaseDatabase.getInstance().getReference("Dislikes");
        final boolean[] LickChecker2 = {false};
        PostDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LickChecker2[0] = true;
                DisLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (LickChecker2[0] == true) {
                            if (snapshot.child(PostId).hasChild(CurrentUserId)) {
                                DisLike.child(PostId).child(CurrentUserId).removeValue();
                                //holder.PostLike.setImageResource(R.drawable.uncolored_like);
                            } else {
                                DisLike.child(PostId).child(CurrentUserId).setValue(CurrentUserId);
                                //holder.PostLike.setImageResource(R.drawable.colored_like);
                            }
                            LickChecker2[0] = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Error",error.toString());
                    }
                });
            }
        });

        postRef = FirebaseDatabase.getInstance().getReference("All Post").child(PostId);
        postRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    AllPost allPost = snapshot.getValue(AllPost.class);
                    String Post_Image = allPost.getPostImage();
                    String ProfileImage = allPost.getUserProfilePic();
                    Picasso.get().load(Post_Image).placeholder(R.drawable.progress).into(PostImage);

                    if (ProfileImage.equals("None")){
                        UserProfileImage.setImageResource(R.drawable.profile_image);
                    }else {
                        Picasso.get().load(ProfileImage).fit().placeholder(R.drawable.profile_image).into(UserProfileImage);
                    }

                    UserName.setText(allPost.getUserName());
                    Date.setText(allPost.getCurrentDate());
                    Time.setText(allPost.getCurrentTime());
                    Caption.setText(allPost.getCaption());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewPost.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        commentRef = FirebaseDatabase.getInstance().getReference("Comment");
        commentRef.child(PostId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long count = snapshot.getChildrenCount();
                    Comment.setText(String.valueOf(count));
                }else {
                    Comment.setText(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewPost.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        PostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UserPostComment.class);
                intent.putExtra("PostId",PostId);
                intent.putExtra("UserName",User_Name);
                intent.putExtra("UserDp",UserProfilePic);
                intent.putExtra("UsersName",UsersName);
                intent.putExtra("CurrentUserId",UserId);
                intent.putExtra("Value",ViewPostValue);
                intent.putExtra("value",Value);
                intent.putExtra("ChatBackground",wallpaper);
                startActivity(intent);
                finish();
            }
        });

        MoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ViewPost.this,MoreOption);
                popupMenu.inflate(R.menu.update_post_menu);
                if (!CurrentUserId.equals(UserId)){
                    popupMenu.getMenu().findItem(R.id.action_update_post).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.action_delete_post).setVisible(false);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_download_post:
                                postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        AllPost allPost = snapshot.getValue(AllPost.class);
                                        String Post_Image = allPost.getPostImage();
//                                        bitmap = ((BitmapDrawable)PostImage.getDrawable()).getBitmap();
                                        if (ContextCompat.checkSelfPermission(ViewPost.this,
                                                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                            savePostImage(Post_Image);
                                        }else {
                                            requestPermission();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ViewPost.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case R.id.action_share_post:
                                try {
                                    File file = new File(getExternalCacheDir(),"sample.png");
                                    fileOutputStream = new FileOutputStream(file);
                                    bitmap = ((BitmapDrawable)PostImage.getDrawable()).getBitmap();
                                    bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
                                    fileOutputStream.flush();
                                    fileOutputStream.close();
                                    file.setReadable(true,false);
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                                    intent.setType("image/png");
                                    startActivity(Intent.createChooser(intent,"Share via"));
                                } catch (Exception e){
                                    Toast.makeText(ViewPost.this, "Something is wrong please try again", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case R.id.action_update_post:
                                Intent intent = new Intent(ViewPost.this,UpdatePost.class);
                                intent.putExtra("PostId",PostId);
                                intent.putExtra("UserName",User_Name);
                                intent.putExtra("UserDp",UserProfilePic);
                                intent.putExtra("UsersName",UsersName);
                                intent.putExtra("CurrentUserId",UserId);
                                intent.putExtra("Value",ViewPostValue);
                                intent.putExtra("value",Value);
                                intent.putExtra("ChatBackground",wallpaper);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.action_delete_post:
                                AlertDialog.Builder builder = new AlertDialog.Builder(ViewPost.this);
                                builder.setTitle("Delete Post");
                                builder.setMessage("Are you sure you want to delete your post?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseReference Post = FirebaseDatabase.getInstance().getReference("Post").child(CurrentUserId).child(PostId);
                                        DatabaseReference AllPost = FirebaseDatabase.getInstance().getReference("All Post").child(PostId);
                                        DatabaseReference Comment = FirebaseDatabase.getInstance().getReference("Comment").child(PostId);
                                        DatabaseReference Like = FirebaseDatabase.getInstance().getReference("Likes").child(PostId);
                                        DatabaseReference Dislike = FirebaseDatabase.getInstance().getReference("Dislikes").child(PostId);
                                        Query query = Comment.orderByChild("commentValue").equalTo("1");
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Post").child(CurrentUserId).child(PostId);
                                        storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Post.removeValue();AllPost.removeValue();Like.removeValue();Dislike.removeValue();
                                                    Toast.makeText(ViewPost.this, "Post Delete Successfully", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(),UserProfile.class);
                                                    intent.putExtra("UserFriendsValue","A");
                                                    startActivity(intent);
                                                    finish();
                                                }else {
                                                    Toast.makeText(ViewPost.this, "Something is wrong Post not deleted !!!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        String CommentId = dataSnapshot.getKey();
                                                        Comment.child(CommentId).removeValue();
                                                    }
                                                }else {
                                                    Toast.makeText(ViewPost.this, "", Toast.LENGTH_SHORT);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(ViewPost.this, error.getCode(), Toast.LENGTH_SHORT).show();
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
                                alertDialog.show();
                                alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                                alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                                alertDialog.show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        savePostRef = FirebaseDatabase.getInstance().getReference("Favourite Post").child(CurrentUserId);
        savePostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(PostId)){
                    PostImageSave.setImageResource(R.drawable.fav_fill);
                }else {
                    PostImageSave.setImageResource(R.drawable.fav);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewPost.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        final boolean[] postChecker = {false};
        PostImageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postChecker[0] = true;
                savePostRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (postChecker[0] == true){
                            if (snapshot.hasChild(PostId)){
                                savePostRef.child(PostId).removeValue();
                            }else {
                                FavPost favPost = new FavPost(PostId);
                                savePostRef.child(PostId).setValue(favPost);
                            }
                            postChecker[0] = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ViewPost.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        TotalLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PostLikedUsers.class);
                intent.putExtra("Value",Value);
                intent.putExtra("PostId",PostId);
                intent.putExtra("UserName",User_Name);
                intent.putExtra("ProfilePic",UserProfilePic);
                intent.putExtra("UsersName",UsersName);
                intent.putExtra("CurrentUserId",UserId);
                intent.putExtra("ChatBackground",wallpaper);
                startActivity(intent);
                finish();
            }
        });

        TotalDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PostDislikedUsers.class);
                intent.putExtra("Value",Value);
                intent.putExtra("PostId",PostId);
                intent.putExtra("UserName",User_Name);
                intent.putExtra("ProfilePic",UserProfilePic);
                intent.putExtra("UsersName",UsersName);
                intent.putExtra("CurrentUserId",UserId);
                intent.putExtra("ChatBackground",wallpaper);
                startActivity(intent);
                finish();
            }
        });
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ViewPost.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewPost.this);
            builder.setTitle("Permission needed");
            builder.setMessage("This permission is needed because of this and that");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(ViewPost.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
                }
            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        } else {
            ActivityCompat.requestPermissions(ViewPost.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
        }
    }

    private void savePostImage(String post_Image) {
        String title = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(post_Image));
        request.setTitle(title);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        try {
            request.setDestinationInExternalPublicDir("/Make Friends/Message/Image",title+".png");
        }catch (Exception e){
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+" .png");
        }
        DownloadManager downloadManager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        request.setMimeType("image/*");
//        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        downloadManager.enqueue(request);

        /*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path+"/Make Friends/Post Image");
        dir.mkdirs();
        String imageName = timeStamp + ".PNG";
        File file = new File(dir,imageName);
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();;
            outputStream.close();
            Toast.makeText(this, "Download Successful", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }*/
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
    public void onBackPressed() {
        if (Value.contentEquals("S") || Value.equals("A") || Value.endsWith("UP")){
            Intent intent2 = new Intent(getApplicationContext(),UserProfile.class);
            intent2.putExtra("UserFriendsValue",Value);
            startActivity(intent2);
        }else if (Value.contentEquals("TLB")){
            Intent intent = new Intent(getApplicationContext(), SendFriendRequestDuplicate.class);
            intent.putExtra("UserId",UserId);
            intent.putExtra("Value",Value);
            startActivity(intent);
        }else if (Value.contentEquals("SE")){
            Intent intent2 = new Intent(getApplicationContext(),FavouritePost.class);
            intent2.putExtra("Value",Value);
            startActivity(intent2);
        }else {
            Intent intent = new Intent(getApplicationContext(), SendFriendRequest.class);
            intent.putExtra("UserId",UserId);
            intent.putExtra("Value",Value);
            intent.putExtra("ChatBackground",wallpaper);
            startActivity(intent);
        }
        finish();
    }
}