package com.social.makefriends.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.manage.userpost.ViewPost;
import com.social.makefriends.model.AllPost;
import com.social.makefriends.model.UserDetails;
import com.social.makefriends.model.UserPost;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendProfilePostImageAdapterDuplicate extends RecyclerView.Adapter<FriendProfilePostImageAdapterDuplicate.ViewHolderTwo> {

    private FirebaseAuth firebaseAuth;
    private  DatabaseReference userRef;
    private Activity activity;
    private List<UserPost> userPosts;

    public FriendProfilePostImageAdapterDuplicate(List<UserPost> userPosts, Activity activity) {
        this.userPosts = userPosts;
        this.activity = activity;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTwo holder, int position) {
        UserPost model = userPosts.get(position);
        firebaseAuth = FirebaseAuth.getInstance();
        String postId = model.getPostId();
        String UserId = model.getUserId();

        userRef = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post").child(UserId).child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String image = snapshot.child("postImage").getValue().toString();

                    Glide.with(activity).load(image).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@SuppressLint("CheckResult") @Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource)  {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @SuppressLint("CheckResult")
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(holder.PostImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", String.valueOf(error));
            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("All Post").child(postId);
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    AllPost allPost = snapshot.getValue(AllPost.class);
                    String UserName = allPost.getUserName();
                    String ProfilePic = allPost.getUserProfilePic();
                    String UserId = allPost.getCurrentUserId();
                    String UsersName = allPost.getUsersName();

                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                            String Image = userDetails.getChatBackgroundWall();

                            holder.PostImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(v.getContext(), ViewPost.class);
                                    intent.putExtra("PostId",postId);
                                    intent.putExtra("UserName",UserName);
                                    intent.putExtra("ProfilePic",ProfilePic);
                                    intent.putExtra("CurrentUserId",UserId);
                                    intent.putExtra("UsersName",UsersName);
                                    intent.putExtra("value","TLB");
                                    intent.putExtra("ChatBackground",Image);
                                    v.getContext().startActivity(intent);
                                    ((AppCompatActivity)v.getContext()).finish();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Log.e("Error", "Upload Post");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", String.valueOf(error));
            }
        });
    }

    @Override
    public int getItemCount() {
        return userPosts.size();
    }

    @NonNull
    @Override
    public ViewHolderTwo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_view_post_list,parent,false);
        ViewHolderTwo viewHolderTwo = new ViewHolderTwo(view);
        return viewHolderTwo;
    }

    class ViewHolderTwo extends RecyclerView.ViewHolder{
        ImageView PostImage;
        ProgressBar progressBar;
        public ViewHolderTwo(@NonNull View itemView) {
            super(itemView);
            PostImage = (ImageView)itemView.findViewById(R.id.post_image);
            progressBar = itemView.findViewById(R.id.profileViewPostProgress);
        }
    }
}
