package com.social.makefriends.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.ui.activity.chat.friendrequest.SendFriendRequestDuplicate;
import com.social.makefriends.model.Friends;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyFriendAdapter extends FirebaseRecyclerAdapter<Friends, MyFriendAdapter.Holder> {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Context context;

    public MyFriendAdapter(@NonNull FirebaseRecyclerOptions<Friends> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull Holder holder, int position, @NonNull Friends model) {
        holder.Cross3.setVisibility(View.INVISIBLE);
        holder.Cross3.setEnabled(false);
        holder.Online3.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        String Uid = model.getFriendUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User Details").child(Uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String Image = snapshot.child("userProfileImageUrl").getValue().toString();
                    String Name = snapshot.child("userName").getValue().toString();
                    String UserName = snapshot.child("usersName").getValue().toString();

                    if (Image.equals("None")){
                        holder.Profile_Image.setImageResource(R.drawable.profile_image);
                    }else {
                        Picasso.get().load(Image).fit().placeholder(R.drawable.profile_image).into(holder.Profile_Image);
                    }
                    holder.name.setText(Name);
                    holder.User_Name.setText(UserName);
                }else {
                    Log.e("Error","not exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.constraint_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(context, SendFriendRequestDuplicate.class);
                intent2.putExtra("UserId",Uid);
                intent2.putExtra("Value","TLB");
                v.getContext().startActivity(intent2);
                ((AppCompatActivity)context).finish();
            }
        });
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_friends_list,parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    class Holder extends RecyclerView.ViewHolder{
        CircleImageView Profile_Image;
        TextView name,User_Name;
        ConstraintLayout constraint_Layout;
        ImageView Cross3,Online3;
        public Holder(@NonNull View itemView) {
            super(itemView);
            Profile_Image = (CircleImageView) itemView.findViewById(R.id.user_search_profile_image);
            name = (TextView) itemView.findViewById(R.id.user_search_name);
            User_Name = (TextView) itemView.findViewById(R.id.user_search_username);
            constraint_Layout = (ConstraintLayout) itemView.findViewById(R.id.search_friend_const);
            Cross3 = (ImageView) itemView.findViewById(R.id.cross);
            Online3 = (ImageView) itemView.findViewById(R.id.online_status);
        }
    }
}
