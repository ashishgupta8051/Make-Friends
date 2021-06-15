package com.social.makefriends.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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
import com.social.makefriends.manage.userpost.PostDislikedUsers;
import com.social.makefriends.manage.userpost.PostLikedUsers;
import com.social.makefriends.model.AllPost;
import com.social.makefriends.manage.userpost.UserPostComment;
import com.social.makefriends.manage.userpost.UpdatePost;
import com.social.makefriends.model.FavPost;
import com.social.makefriends.model.UserDetails;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePostAdapter extends RecyclerView.Adapter<HomePostAdapter.MyViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {
    private Bitmap bitmap;
    private FileOutputStream fileOutputStream;
    private String HomePostValue = "H";
    private List<AllPost> allPostList;
    private static final  int PERMISSION = 999;
    private Activity context;
    private DatabaseReference userRef,savePostRef,allPostRef,likeRef,disLikeRef,commentRef;

    public HomePostAdapter(List<AllPost> allPostList, Activity context) {
        this.allPostList = allPostList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AllPost model = allPostList.get(position);
        FirebaseAuth firebaseAuth;
        firebaseAuth = FirebaseAuth.getInstance();
        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String userId = model.getCurrentUserId();
        String postId = model.getKey();
        String Name = model.getUserName();
        String usersName = model.getUsersName();
        String Profile_Pic = model.getUserProfilePic();
        String PostImage = model.getPostImage();

        userRef = FirebaseDatabase.getInstance().getReference("User Details").child(currentUserId);
        likeRef = FirebaseDatabase.getInstance().getReference("Likes");
        disLikeRef = FirebaseDatabase.getInstance().getReference("Dislikes");
        savePostRef = FirebaseDatabase.getInstance().getReference("Favourite Post").child(currentUserId);
        allPostRef = FirebaseDatabase.getInstance().getReference("All Post");
        commentRef = FirebaseDatabase.getInstance().getReference("Comment");

        if (Profile_Pic.equals("None")){
            holder.ProfilePic.setImageResource(R.drawable.profile_image);
        }else {
            Picasso.get().load(Profile_Pic).fit().placeholder(R.drawable.profile_image).into(holder.ProfilePic);
        }

        //Post Image
        Picasso.get().load(PostImage).placeholder(R.drawable.progress).into(holder.PostImage);

        holder.UserName.setText(model.getUserName());
        holder.Date.setText(model.getCurrentDate());
        holder.Time.setText(model.getCurrentTime());
        holder.Caption.setText(model.getCaption());

        commentRef.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long count = snapshot.getChildrenCount();
                    holder.Comment.setText(String.valueOf(count));
                }else {
                    holder.Comment.setText(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        final int[] likeCount = new int[1];
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).hasChild(currentUserId)){
                    likeCount[0] = (int)snapshot.child(postId).getChildrenCount();
                    holder.PostLike.setImageResource(R.drawable.colored_like);
                    if (likeCount[0] == 0){
                        holder.TotalLike.setText(null);
                    }else {
                        holder.TotalLike.setText(Integer.toString(likeCount[0]));
                    }
                }else {
                    likeCount[0] = (int)snapshot.child(postId).getChildrenCount();
                    holder.PostLike.setImageResource(R.drawable.uncolored_like);
                    if (likeCount[0] == 0){
                        holder.TotalLike.setText(null);
                    }else {
                        holder.TotalLike.setText(Integer.toString(likeCount[0]));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", String.valueOf(error));
            }
        });

        final int[] likeCount2 = new int[1];
        disLikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).hasChild(currentUserId)){
                    likeCount2[0] = (int)snapshot.child(postId).getChildrenCount();
                    holder.PostDislike.setImageResource(R.drawable.colored_dislike);
                    if (likeCount2[0] == 0){
                        holder.TotalDislike.setText(null);
                    }else {
                        holder.TotalDislike.setText(Integer.toString(likeCount2[0]));
                    }
                }else {
                    likeCount2[0] = (int)snapshot.child(postId).getChildrenCount();
                    holder.PostDislike.setImageResource(R.drawable.uncolored_dislike);
                    if (likeCount2[0] == 0){
                        holder.TotalDislike.setText(null);
                    }else {
                        holder.TotalDislike.setText(Integer.toString(likeCount2[0]));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", String.valueOf(error));
            }
        });

        final boolean[] LickChecker = {false};
        holder.PostLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LickChecker[0] = true;
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (LickChecker[0] == true) {
                            if (snapshot.child(postId).hasChild(currentUserId)) {
                                likeRef.child(postId).child(currentUserId).removeValue();
                            } else {
                                likeRef.child(postId).child(currentUserId).setValue(currentUserId);
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

        final boolean[] LickChecker2 = {false};
        holder.PostDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LickChecker2[0] = true;
                disLikeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (LickChecker2[0] == true) {
                            if (snapshot.child(postId).hasChild(currentUserId)) {
                                disLikeRef.child(postId).child(currentUserId).removeValue();
                                //holder.PostLike.setImageResource(R.drawable.uncolored_like);
                            } else {
                                disLikeRef.child(postId).child(currentUserId).setValue(currentUserId);
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

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                String chatWallpaper = userDetails.getChatBackgroundWall();
                holder.ProfilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentUserId.equals(userId)){
                            Intent intent2 = new Intent(v.getContext(),UserProfile.class);
                            intent2.putExtra("UserFriendsValue","A");
                            v.getContext().startActivity(intent2);
                        }else {
                            Intent intent = new Intent(v.getContext(), SendFriendRequest.class);
                            intent.putExtra("UserId",userId);
                            intent.putExtra("Value","H");
                            intent.putExtra("ChatBackground",chatWallpaper);
                            v.getContext().startActivity(intent);
                        }
                        ((AppCompatActivity)v.getContext()).finish();

                    }
                });

                holder.PostComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), UserPostComment.class);
                        intent.putExtra("PostId",postId);
                        intent.putExtra("UserName",Name);
                        intent.putExtra("UserDp",Profile_Pic);
                        intent.putExtra("UsersName",usersName);
                        intent.putExtra("CurrentUserId",userId);
                        intent.putExtra("Value",HomePostValue);
                        intent.putExtra("value",HomePostValue);
                        intent.putExtra("ChatBackground",chatWallpaper);
                        v.getContext().startActivity(intent);
                        ((AppCompatActivity)v.getContext()).finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        savePostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(postId)){
                    holder.PostImageSave.setImageResource(R.drawable.fav_fill);
                }else {
                    holder.PostImageSave.setImageResource(R.drawable.fav);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        final boolean[] postChecker = {false};
        holder.PostImageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postChecker[0] = true;
                savePostRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (postChecker[0] == true){
                            if (snapshot.hasChild(model.getKey())){
                                savePostRef.child(model.getKey()).removeValue();
                            }else {
                                FavPost favPost = new FavPost(model.getKey());
                                savePostRef.child(model.getKey()).setValue(favPost);
                            }
                            postChecker[0] = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.MoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(),holder.MoreOption);
                popupMenu.inflate(R.menu.update_post_menu);
                if (!currentUserId.equals(userId)){
                    popupMenu.getMenu().findItem(R.id.action_update_post).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.action_delete_post).setVisible(false);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_download_post:
                                if (ContextCompat.checkSelfPermission(context,
                                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    savePostImage(context,model);
                                }else {
                                    requestPermission(context);
                                }
                                break;
                            case R.id.action_share_post:
                                try {
                                    File file = new File(view.getContext().getExternalCacheDir(),"sample.png");
                                    fileOutputStream = new FileOutputStream(file);
                                    bitmap = ((BitmapDrawable)holder.PostImage.getDrawable()).getBitmap();
                                    bitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
                                    fileOutputStream.flush();
                                    fileOutputStream.close();
                                    file.setReadable(true,false);
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                                    intent.setType("image/png");
                                    view.getContext().startActivity(Intent.createChooser(intent,"Share via"));
                                } catch (Exception e){
                                    Toast.makeText(view.getContext(),"Something is wrong please try again", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case R.id.action_update_post:
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                        String Image = userDetails.getChatBackgroundWall();
                                        Intent intent = new Intent(view.getContext(),UpdatePost.class);
                                        intent.putExtra("PostId",postId);
                                        intent.putExtra("UserName",Name);
                                        intent.putExtra("UserDp",Profile_Pic);
                                        intent.putExtra("UsersName",usersName);
                                        intent.putExtra("CurrentUserId",userId);
                                        intent.putExtra("Value",HomePostValue);
                                        intent.putExtra("value",HomePostValue);
                                        intent.putExtra("ChatBackground",Image);
                                        view.getContext().startActivity(intent);
                                        ((AppCompatActivity)view.getContext()).finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case R.id.action_delete_post:
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setTitle("Delete Post");
                                builder.setMessage("Are you sure you want to delete your post?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseReference Post = FirebaseDatabase.getInstance().getReference("Post").child(currentUserId).child(postId);
                                        DatabaseReference AllPost = FirebaseDatabase.getInstance().getReference("All Post").child(postId);
                                        DatabaseReference Comment = FirebaseDatabase.getInstance().getReference("Comment").child(postId);
                                        DatabaseReference Like = FirebaseDatabase.getInstance().getReference("Likes").child(postId);
                                        DatabaseReference Dislike = FirebaseDatabase.getInstance().getReference("Dislikes").child(postId);
                                        Query query = Comment.orderByChild("commentValue").equalTo("1");
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Post").child(currentUserId).child(postId);
                                        storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Post.removeValue();
                                                    AllPost.removeValue();
                                                    Like.removeValue();
                                                    Dislike.removeValue();
                                                    Toast.makeText(view.getContext(), "Post Delete Successfully", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }else {
                                                    Toast.makeText(view.getContext(), "Something is wrong Post not deleted !!!", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(view.getContext(), error.getCode(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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

        holder.TotalLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PostLikedUsers.class);
                intent.putExtra("Value",HomePostValue);
                intent.putExtra("PostId",model.getKey());
                v.getContext().startActivity(intent);
                ((AppCompatActivity)v.getContext()).finish();
            }
        });

        holder.TotalDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PostDislikedUsers.class);
                intent.putExtra("Value",HomePostValue);
                intent.putExtra("PostId",model.getKey());
                v.getContext().startActivity(intent);
                ((AppCompatActivity)v.getContext()).finish();
            }
        });
    }

    private void requestPermission(Activity v) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);
        }
    }

    private void savePostImage(Activity v, AllPost model) {
        String title = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        String Message = model.getPostImage();
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(Message));
        request.setTitle(title);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        String folder = "/Make Friends/Post Image";
        try {
            request.setDestinationInExternalPublicDir(folder,title+".png");
        }catch (Exception e){
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".png");
        }

        DownloadManager downloadManager=(DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        request.setMimeType("image/*");
//        request.allowScanningByMediaScanner();
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        downloadManager.enqueue(request);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_view_post_list,parent,false);
        return new HomePostAdapter.MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return allPostList.size();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(context, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Enable Permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ProfilePic;
        ImageView PostImage,MoreOption,PostLike,PostDislike,PostComment,PostImageSave;
        TextView UserName,Date,Time,Caption,TotalLike,TotalDislike,Comment;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ProfilePic = (CircleImageView)itemView.findViewById(R.id.user_profile_image);
            PostImage = (ImageView)itemView.findViewById(R.id.post_image);
            MoreOption = (ImageView)itemView.findViewById(R.id.more_option);
            PostLike = (ImageView)itemView.findViewById(R.id.post_like);
            PostDislike = (ImageView)itemView.findViewById(R.id.post_dislike);
            PostComment = (ImageView)itemView.findViewById(R.id.post_comment);
            PostImageSave = (ImageView)itemView.findViewById(R.id.post_image_Save);

            UserName = (TextView)itemView.findViewById(R.id.user_name);
            Date = (TextView)itemView.findViewById(R.id.post_date);
            Time = (TextView)itemView.findViewById(R.id.post_time);
            Caption = (TextView)itemView.findViewById(R.id.post_caption);
            TotalLike = (TextView)itemView.findViewById(R.id.post_like_number);
            TotalDislike = (TextView)itemView.findViewById(R.id.post_dislike_number);
            Comment = (TextView)itemView.findViewById(R.id.post_comment_number);
        }
    }
}
