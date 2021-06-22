package com.social.makefriends.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.model.AllPost;
import com.social.makefriends.model.UserDetails;
import com.social.makefriends.model.UserPost;
import com.social.makefriends.manage.userpost.ViewPost;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.ViewHolder> {

    private String Value;
    private DatabaseReference userRef;
    private FirebaseAuth firebaseAuth;
    private Activity activity;
    private List<UserPost> userPosts;

    public ProfilePostAdapter(String Value, Activity activity, List<UserPost> userPosts) {
        this.Value = Value;
        this.activity = activity;
        this.userPosts = userPosts;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserPost model = userPosts.get(position);
        String postId = model.getPostId();
        String PostImage = model.getPostImage();

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(activity);
        circularProgressDrawable.setStrokeWidth(8);
        circularProgressDrawable.setCenterRadius(35);
        circularProgressDrawable.setColorSchemeColors(R.color.purple_500);
        circularProgressDrawable.start();

        Picasso.get().load(PostImage).placeholder(circularProgressDrawable).into(holder.PostImage);

        firebaseAuth = FirebaseAuth.getInstance();

        userRef =  FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());
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

                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                            String chatWallpaper = userDetails.getChatBackgroundWall();
                            holder.PostImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(v.getContext(),ViewPost.class);
                                    intent.putExtra("PostId",postId);
                                    intent.putExtra("UserName",UserName);
                                    intent.putExtra("ProfilePic",ProfilePic);
                                    intent.putExtra("CurrentUserId",UserId);
                                    intent.putExtra("UsersName",UsersName);
                                    intent.putExtra("value",Value);
                                    intent.putExtra("ChatBackground",chatWallpaper);
                                    v.getContext().startActivity(intent);
                                    ((AppCompatActivity)v.getContext()).finish();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText( activity,error.getMessage(), Toast.LENGTH_SHORT).show();
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_view_post_list,parent,false);
        return new ProfilePostAdapter.ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView PostImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            PostImage = (ImageView)itemView.findViewById(R.id.post_image);
        }
    }
}
