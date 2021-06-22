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
import com.social.makefriends.manage.userpost.ViewPost;
import com.social.makefriends.model.AllPost;
import com.social.makefriends.model.UserPost;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendProfilePostImageAdapter extends RecyclerView.Adapter<FriendProfilePostImageAdapter.ViewHolder> {

    private Activity context;
    private String Value,chatWallpaper;
    private DatabaseReference userRef;
    private FirebaseAuth firebaseAuth;
    private List<UserPost> userPosts;

    public FriendProfilePostImageAdapter(List<UserPost> userPosts, Activity context, String Value, String chatWallpaper) {
        this.userPosts = userPosts;
        this.context = context;
        this.Value = Value;
        this.chatWallpaper = chatWallpaper;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserPost model = userPosts.get(position);
        String postId = model.getPostId();
        String UserId = model.getUserId();

        firebaseAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("User Details").child(firebaseAuth.getUid());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post").child(UserId).child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UserPost userPost = snapshot.getValue(UserPost.class);
                    String image = userPost.getPostImage();

                    CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
                    circularProgressDrawable.setStrokeWidth(8);
                    circularProgressDrawable.setCenterRadius(35);
                    circularProgressDrawable.setColorSchemeColors(R.color.purple_500);
                    circularProgressDrawable.start();


                    Picasso.get().load(image).placeholder(circularProgressDrawable).into(holder.PostImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
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

                    holder.PostImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), ViewPost.class);
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
                }else {
                    Log.e("Error", "Upload Post");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
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
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView PostImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            PostImage = (ImageView)itemView.findViewById(R.id.post_image);
        }
    }
}
