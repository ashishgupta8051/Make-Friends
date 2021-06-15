package com.social.makefriends.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.model.Comments;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder>{
    private Activity activity;
    private List<Comments> commentsList;
    private String value,wallpaper,CurrentDate;
    private DatabaseReference commentLikeRef,replyCommentRef;
    private boolean checker = false;
    private boolean replyChecker = false;

    public CommentAdapter(Activity activity, List<Comments> commentsList, String value, String wallpaper) {
        this.activity = activity;
        this.commentsList = commentsList;
        this.value = value;
        this.wallpaper = wallpaper;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        Comments model = commentsList.get(position);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String CurrentUserId = firebaseAuth.getCurrentUser().getUid();

        commentLikeRef = FirebaseDatabase.getInstance().getReference("Like Comment");
        replyCommentRef = FirebaseDatabase.getInstance().getReference("Reply Comment").child(model.getCommentId());

        Calendar Date = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        CurrentDate = currentDate.format(Date.getTime());

        if (CurrentDate.equals(model.getCurrentDate())){
            holder.commentTime.setText(model.getCurrentTime());
        }else {
            holder.commentTime.setText(model.getCurrentDate()+" at "+model.getCurrentTime());
        }

        holder.Name.setText(model.getUserName());
        holder.Comment.setText("Comment : "+model.getPostComment());

        if (model.getProfilePic().equals("None")){
            holder.ProfilePic.setImageResource(R.drawable.profile_image);
        }else {
            Picasso.get().load(model.getProfilePic()).fit().placeholder(R.drawable.profile_image).into(holder.ProfilePic);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (CurrentUserId.equals(model.getPostUserId())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Delete Comment");
                    builder.setMessage("Are you sure you want to delete comment?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference removeCommentRef = FirebaseDatabase.getInstance().getReference("Comment")
                                    .child(model.getPostId()).child(model.getCommentId());
                            removeCommentRef.removeValue();
                            DatabaseReference removeReplyCommentRef = FirebaseDatabase.getInstance().getReference("Reply Comment").
                                    child(model.getCommentId());
                            removeReplyCommentRef.removeValue();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.setCancelable(false).create();
                    alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                    alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                    alertDialog.show();
                }else if (CurrentUserId.equals(model.getCurrentUserId())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Delete Comment");
                    builder.setMessage("Are you sure you want to delete comment?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference removeCommentRef = FirebaseDatabase.getInstance().getReference("Comment")
                                    .child(model.getPostId()).child(model.getCommentId());
                            removeCommentRef.removeValue();

                            DatabaseReference removeReplyCommentRef = FirebaseDatabase.getInstance().getReference("Reply Comment").
                                    child(model.getCommentId());
                            removeReplyCommentRef.removeValue();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.setCancelable(false).create();
                    alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                    alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                    alertDialog.show();
                }else {
                    Toast.makeText(activity, "can't delete another comments", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        holder.ProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CurrentUserId.equals(model.getCurrentUserId())){
                    Log.d("Msg","Your id");
                }else {
                   /* Intent intent = new Intent(activity, SendFriendRequest.class);
                    intent.putExtra("UserId",model.getCurrentUserId());
                    intent.putExtra("Value",value);
                    intent.putExtra("ChatBackground",wallpaper);
                    activity.startActivity(intent);
                    activity.finish();*/
                }
            }
        });

        commentLikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(model.getCommentId()).hasChild(CurrentUserId)){
                    holder.likeCommentImage.setImageResource(R.drawable.fav_fill);
                    long count = snapshot.child(model.getCommentId()).getChildrenCount();
                    if (count == 0){
                        holder.likeCount.setText("0 likes");
                    }else {
                        holder.likeCount.setText(String.valueOf(count)+" likes");
                    }
                }else {
                    holder.likeCommentImage.setImageResource(R.drawable.fav);
                    long count = snapshot.child(model.getCommentId()).getChildrenCount();
                    if (count == 0){
                        holder.likeCount.setText("0 likes");
                    }else {
                        holder.likeCount.setText(String.valueOf(count)+" likes");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        final boolean[] click = {false};
        holder.likeCommentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click[0] = true;
                commentLikeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (click[0] == true){
                            if (snapshot.child(model.getCommentId()).hasChild(CurrentUserId)){
                                commentLikeRef.child(model.getCommentId()).child(CurrentUserId).removeValue();
                            }else {
                                commentLikeRef.child(model.getCommentId()).child(CurrentUserId).setValue(true);
                            }
                            click[0] = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_comment_list,parent,false);
        return new CommentAdapter.CommentHolder(view);
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder{
        CircleImageView ProfilePic;
        TextView commentTime,Name,likeCount;
        EmojiTextView Comment;
        ImageView likeCommentImage;
        public CommentHolder(@NonNull View itemView) {
            super(itemView);
            ProfilePic = (CircleImageView)itemView.findViewById(R.id.user_profile_image3);
            commentTime = (TextView)itemView.findViewById(R.id.comment_date);
            Comment = (EmojiTextView) itemView.findViewById(R.id.post_comment3);
            Name = (TextView)itemView.findViewById(R.id.user_name3);
            likeCommentImage = (ImageView)itemView.findViewById(R.id.like_comment);

            likeCount = (TextView)itemView.findViewById(R.id.count_like);
        }
    }

}
