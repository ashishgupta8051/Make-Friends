package com.social.makefriends.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.ui.activity.chat.ChatWithFriends;
import com.social.makefriends.model.ChatMessages;
import com.social.makefriends.model.ExistsChatUser;
import com.social.makefriends.model.ViewModel;
import com.social.makefriends.model.UserDetails;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendChatListAdapter extends RecyclerView.Adapter<FriendChatListAdapter.ChatHolder> {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDetailsRef,MessageRef,RemoveExistUser;
    private String CurrentDate,CurrentTime;
    private Boolean isEnable = false;
    private Boolean isSelectedAll = false;
    private List<ExistsChatUser> list;
    private List<String> existsChatUsers;
    private ArrayList<String> selectList = new ArrayList<>();
    private ViewModel viewModel;
    private Activity activity;

    public FriendChatListAdapter(List<ExistsChatUser> list, Activity activity, List<String> existsChatUsers) {
        this.list = list;
        this.activity = activity;
        this.existsChatUsers = existsChatUsers;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list,parent,false);
       ChatHolder chatHolder = new ChatHolder(view);
       //Initialize MainViewModel
       viewModel = ViewModelProviders.of((FragmentActivity)parent.getContext()).get(ViewModel.class);
       return chatHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        ExistsChatUser model = list.get(position);

        holder.OnlineStatusImage.setVisibility(View.INVISIBLE);
        holder.Check.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        String CurrentUserId = firebaseAuth.getCurrentUser().getUid();

        userDetailsRef = FirebaseDatabase.getInstance().getReference("User Details");
        RemoveExistUser = FirebaseDatabase.getInstance().getReference("Exists Chat User").child(CurrentUserId);
        MessageRef = FirebaseDatabase.getInstance().getReference("Messages");

        Calendar Date = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yy");
        CurrentDate = currentDate.format(Date.getTime());

        Calendar Time = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        CurrentTime = currentTime.format(Time.getTime());

        userDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.child(model.getExistsUserId()).getValue(UserDetails.class);
                UserDetails userDetails2 = snapshot.child(CurrentUserId).getValue(UserDetails.class);
                String Online_Status = userDetails.getOnlineStatus();
                String ProfileImage = userDetails.getUserProfileImageUrl();
                if (Online_Status.equals("Online")){
                    holder.OnlineStatusImage.setVisibility(View.VISIBLE);
                }else {
                    holder.OnlineStatusImage.setVisibility(View.INVISIBLE);
                }

                holder.Name.setText(userDetails.getUserName());
                if (userDetails.getUserProfileImageUrl().equals("None")){
                    holder.ProfileImage.setImageResource(R.drawable.profile_image);
                }else {
                    Picasso.get().load(userDetails.getUserProfileImageUrl()).fit().placeholder(R.drawable.profile_image).into(holder.ProfileImage);
                }

                String backgroundImage = userDetails2.getChatBackgroundWall();

                holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isEnable){
                            ClickItem(holder);
                        }else {
                            Intent intent = new Intent(activity, ChatWithFriends.class);
                            intent.putExtra("UserId",model.getExistsUserId());
                            intent.putExtra("Value","C");
                            intent.putExtra("Image",ProfileImage);
                            intent.putExtra("ChatBackground",backgroundImage);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChatMessages chatMessages = dataSnapshot.getValue(ChatMessages.class);
                    if (chatMessages.getReceiverId().equals(CurrentUserId) && chatMessages.getSenderId().equals(model.getExistsUserId()) ||
                            chatMessages.getReceiverId().equals(model.getExistsUserId()) && chatMessages.getSenderId().equals(CurrentUserId)){
                        //Last Message Details
                        if (chatMessages.getSenderId().equals(CurrentUserId) && chatMessages.getSenderSideMsgDelete().equals("not_delete") ||
                                chatMessages.getReceiverId().equals(CurrentUserId) && chatMessages.getReceiverSideMsgDelete().equals("not_delete")){
                            if (chatMessages.getMessageType().equals("text")){
                                if (chatMessages.getMessageSeenDetails().equals("not_seen") && chatMessages.getReceiverId().equals(CurrentUserId)){
                                    holder.LastMessage.setTypeface(holder.LastMessage.getTypeface(), Typeface.BOLD);
                                }
                                holder.LastMessage.setText(chatMessages.getMessageDetails());
                            }else if (chatMessages.getMessageType().equals("image")){
                                if (chatMessages.getMessageSeenDetails().equals("not_seen") && chatMessages.getReceiverId().equals(CurrentUserId)){
                                    holder.LastMessage.setTypeface(holder.LastMessage.getTypeface(), Typeface.BOLD);
                                }
                                holder.LastMessage.setText("Image");
                            }else if (chatMessages.getMessageType().equals("file")){
                                if (chatMessages.getMessageSeenDetails().equals("not_seen") && chatMessages.getReceiverId().equals(CurrentUserId)){
                                    holder.LastMessage.setTypeface(holder.LastMessage.getTypeface(), Typeface.BOLD);
                                }
                                holder.LastMessage.setText("File: "+chatMessages.getFileName());
                            }else if (chatMessages.getMessageType().equals("audio")){
                                if (chatMessages.getMessageSeenDetails().equals("not_seen") && chatMessages.getReceiverId().equals(CurrentUserId)){
                                    holder.LastMessage.setTypeface(holder.LastMessage.getTypeface(), Typeface.BOLD);
                                }
                                holder.LastMessage.setText("Audio: "+chatMessages.getFileName());
                            }else if (chatMessages.getMessageType().equals("icon")){
                                if (chatMessages.getMessageSeenDetails().equals("not_seen") && chatMessages.getReceiverId().equals(CurrentUserId)){
                                    holder.LastMessage.setTypeface(holder.LastMessage.getTypeface(), Typeface.BOLD);
                                }
                                holder.LastMessage.setText("Icon: 👍");
                            }
                            //For Message Time
                            if (chatMessages.getMessageDate().equals(CurrentDate)){
                                holder.Time.setText(chatMessages.getMessageTime());
                            }else {
                                holder.Time.setText(chatMessages.getMessageDate());
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

           holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isEnable){
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            MenuInflater menuInflater = mode.getMenuInflater();
                            menuInflater.inflate(R.menu.delete_item,menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            isEnable = true;
                            ClickItem(holder);

                            viewModel.getTxt().observe((LifecycleOwner) v.getContext(), new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    mode.setTitle(String.format("%s Selected",s));
                                }
                            });
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.delete:
                                    for (String s : selectList){
                                        RemoveExistUser.child(s).removeValue();
                                    }
                                    mode.finish();
                                    break;
                                case R.id.selectAll:
                                    if (selectList.size() == existsChatUsers.size()){
                                        isSelectedAll = false;
                                        selectList.clear();
                                    }else {
                                        isSelectedAll = true;
                                        selectList.clear();
                                        selectList.addAll(existsChatUsers);
                                    }
                                    viewModel.setTxt(String.valueOf(selectList.size()));
                                    notifyDataSetChanged();
                                    break;
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            isEnable = false;
                            isSelectedAll = false;
                            selectList.clear();
                            notifyDataSetChanged();

                        }
                    };
                    activity.startActionMode(callback);
                }else {
                    ClickItem(holder);
                }
                return true;
            }
        });

        if (isSelectedAll){
            holder.Check.setVisibility(View.VISIBLE);
        }else {
            holder.Check.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private void ClickItem(ChatHolder holder) {
        String s = existsChatUsers.get(holder.getAbsoluteAdapterPosition());
        if (holder.Check.getVisibility() == View.GONE){
            holder.Check.setVisibility(View.VISIBLE);
            selectList.add(s);
        }else {
            holder.Check.setVisibility(View.GONE);
            selectList.remove(s);
        }
        viewModel.setTxt(String.valueOf(selectList.size()));
    }

    class ChatHolder extends RecyclerView.ViewHolder{
        CircleImageView ProfileImage;
        ImageView OnlineStatusImage;
        CircleImageView Check;
        TextView Name,Time;
        EmojiTextView LastMessage;
        ConstraintLayout constraintLayout;
        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            ProfileImage = itemView.findViewById(R.id.chat_user_profile_image);
            Check = itemView.findViewById(R.id.check);

            Name = itemView.findViewById(R.id.chat_name);
            LastMessage = itemView.findViewById(R.id.last_message);
            Time = itemView.findViewById(R.id.chat_time);
            OnlineStatusImage = itemView.findViewById(R.id.online_status3);

            constraintLayout = itemView.findViewById(R.id.const_chat_list);
        }
    }
}
