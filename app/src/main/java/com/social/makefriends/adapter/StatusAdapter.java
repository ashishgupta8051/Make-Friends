package com.social.makefriends.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.devlomi.circularstatusview.CircularStatusView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.activity.Home;

import com.social.makefriends.model.Status;
import com.social.makefriends.model.UserDetails;
import com.social.makefriends.model.UserStatus;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


import de.hdodenhof.circleimageview.CircleImageView;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.OnStoryChangedCallback;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.Holder>{
    private Context context;
    private ArrayList<UserStatus> userStatusArrayList;
    private DatabaseReference databaseReference;
    private String profileIMage;
    private Status status;

    public StatusAdapter(Context context, ArrayList<UserStatus> userStatusArrayList) {
        this.context = context;
        this.userStatusArrayList = userStatusArrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_list,parent,false);
        return new StatusAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        UserStatus userStatus = userStatusArrayList.get(position);

        databaseReference = FirebaseDatabase.getInstance().getReference("User Details");
        if (userStatus.getProfileImage().equals("None")){
            profileIMage = "https://firebasestorage.googleapis.com/v0/b/make-friends-7338f.appspot.com/o/Default%20Profile%20Image%2Fprofile_image.png?alt=media&token=5a8a1e76-a503-4241-af43-ae48e39dc528";
        }else {
            profileIMage = userStatus.getProfileImage();
        }

        for (int i = 0; i < userStatus.getStatusList().size(); i++){
            status = userStatus.getStatusList().get(i);
            Picasso.get().load(status.getStatusUrl()).noFade().into(holder.circleImageView);
        }
        holder.circularStatusView.setPortionsCount(userStatus.getStatusList().size());

        holder.circularStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for(Status status: userStatus.getStatusList()){
                    myStories.add(new MyStory(
                            status.getStatusUrl()
                    ));
                }

                new StoryView.Builder(((Home)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStatus.getName())
                        .setSubtitleText(userStatus.getUserName())// Default is Hidden// Default is Hidden
                        .setTitleLogoUrl(profileIMage)// Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {

                            }

                            @Override
                            public void onTitleIconClickListener(int position) {

                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return userStatusArrayList.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        CircularStatusView circularStatusView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.statusImage);
            circularStatusView = itemView.findViewById(R.id.circular_status_view);
        }
    }

}
