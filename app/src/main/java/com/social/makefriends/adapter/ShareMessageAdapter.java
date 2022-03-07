package com.social.makefriends.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.model.ExistsChatUser;
import com.social.makefriends.model.Friends;
import com.social.makefriends.model.UserDetails;
import com.social.makefriends.network.Client;
import com.social.makefriends.notification.Data;
import com.social.makefriends.notification.MyResponse;
import com.social.makefriends.notification.Senders;
import com.social.makefriends.notification.Token;
import com.social.makefriends.utils.APIService;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareMessageAdapter extends RecyclerView.Adapter<ShareMessageAdapter.ShareMessageHolder>{
    private List<Friends> friendsList;
    private Activity activity;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference friendDetailRef,MessageRef,CheckUserIdRef,UserDetails,muteNotificationRefD;
    private String messageDetails,messageType,fileName,fileType,CurrentUserId,MsgSentDetails,Position;
    private APIService apiService;

    public ShareMessageAdapter(List<Friends> friendsList, Activity activity, String messageDetails, String messageType, String fileName, String fileType) {
        this.friendsList = friendsList;
        this.activity = activity;
        this.messageDetails = messageDetails;
        this.messageType = messageType;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    @NonNull
    @Override
    public ShareMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_message_list,parent,false);
        ShareMessageHolder shareMessageHolder = new ShareMessageHolder(view);
        return shareMessageHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShareMessageHolder holder, int position) {
        Friends friends = friendsList.get(position);
        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserId = firebaseAuth.getCurrentUser().getUid();
        String name = friends.getFriend_name();
        String userName = friends.getFriend_userName();

        apiService = Client.getClient().create(APIService.class);

        holder.nameTxt.setText(name);
        holder.userNameTxt.setText(userName);
        friendDetailRef = FirebaseDatabase.getInstance().getReference("User Details");
        friendDetailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(friends.getFriendUid()).exists()){
                    UserDetails userDetails = snapshot.child(friends.getFriendUid()).getValue(UserDetails.class);
                    if (userDetails.getUserProfileImageUrl().equals("None")){
                        holder.profileImg.setImageResource(R.drawable.profile_image);
                    }else {
                        Picasso.get().load(userDetails.getUserProfileImageUrl()).fit().placeholder(R.drawable.profile_image).into(holder.profileImg);
                    }
                }else {
                    Log.e("Error","This user is not your friend.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        MessageRef = FirebaseDatabase.getInstance().getReference("Messages");
        CheckUserIdRef = FirebaseDatabase.getInstance().getReference("Exists Chat User");
        UserDetails = FirebaseDatabase.getInstance().getReference("User Details");
        muteNotificationRefD = FirebaseDatabase.getInstance().getReference("Notification").child(friends.getFriendUid());

        holder.sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageType.equals("text")){
                    sendMessage(messageDetails,friends);
                    holder.sendMsgBtn.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.silver));
                    holder.sendMsgBtn.setEnabled(false);
                }else if (messageType.equals("image")){
                    sendImage(messageDetails,friends);
                    holder.sendMsgBtn.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.silver));
                    holder.sendMsgBtn.setEnabled(false);
                }else if (messageType.equals("file")){
                    sendFile(messageDetails,friends,fileName,fileType);
                    holder.sendMsgBtn.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.silver));
                    holder.sendMsgBtn.setEnabled(false);
                }else if (messageType.equals("audio")){
                    sendAudio(messageDetails,fileName,friends);
                    holder.sendMsgBtn.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.silver));
                    holder.sendMsgBtn.setEnabled(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    private void sendAudio(String file_uri, String fileName, Friends friends) {
        DatabaseReference MsgRef = FirebaseDatabase.getInstance().getReference("Messages");
        String MessagePushKey = MsgRef.push().getKey();

        Calendar Date = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yy");
        String CurrentDate = date.format(Date.getTime());

        Calendar Time = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("hh:mm aa");
        String CurrentTime = time.format(Time.getTime());

        CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(friends.getFriendUid()).exists()){
                    MsgSentDetails = "not_seen";

                    HashMap<String,Object> addMessage = new HashMap<>();
                    addMessage.put("messageDetails",file_uri);
                    addMessage.put("messageTime",CurrentTime);
                    addMessage.put("messageDate",CurrentDate);
                    addMessage.put("messageType",messageType);
                    addMessage.put("senderId",CurrentUserId);
                    addMessage.put("receiverId",friends.getFriendUid());
                    addMessage.put("messageId",MessagePushKey);
                    addMessage.put("messageSeenDetails",MsgSentDetails);
                    addMessage.put("fileName",fileName);
                    addMessage.put("fileType",null);
                    addMessage.put("senderSideMsgDelete","not_delete");
                    addMessage.put("receiverSideMsgDelete","not_delete");
                    addMessage.put("forward","yes");
                    MsgRef.child(MessagePushKey).setValue(addMessage);

                    HashMap<String,Object> position = new HashMap<String, Object>();
                    position.put("firstPosition","first");
                    CheckUserIdRef.child(CurrentUserId).child(friends.getFriendUid()).updateChildren(position);
                    CheckUserIdRef.child(friends.getFriendUid()).child(CurrentUserId).updateChildren(position);

                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String key = dataSnapshot.getKey();
                                        if (!key.equals(friends.getFriendUid())){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    CheckUserIdRef.child(friends.getFriendUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(friends.getFriendUid()).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String Key = dataSnapshot.getKey();
                                        if (!Key.equals(CurrentUserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(friends.getFriendUid()).child(Key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    MsgSentDetails = "not_seen";
                    Position = "first";

                    HashMap<String,Object> addMessage = new HashMap<>();
                    addMessage.put("messageDetails",file_uri);
                    addMessage.put("messageTime",CurrentTime);
                    addMessage.put("messageDate",CurrentDate);
                    addMessage.put("messageType",messageType);
                    addMessage.put("senderId",CurrentUserId);
                    addMessage.put("receiverId",friends.getFriendUid());
                    addMessage.put("messageId",MessagePushKey);
                    addMessage.put("messageSeenDetails",MsgSentDetails);
                    addMessage.put("fileName",fileName);
                    addMessage.put("fileType",null);
                    addMessage.put("senderSideMsgDelete","not_delete");
                    addMessage.put("receiverSideMsgDelete","not_delete");
                    addMessage.put("forward","yes");

                    MsgRef.child(MessagePushKey).setValue(addMessage);

                    ExistsChatUser senders = new ExistsChatUser(friends.getFriendUid(),Position);
                    CheckUserIdRef.child(CurrentUserId).child(friends.getFriendUid()).setValue(senders);
                    ExistsChatUser receivers = new ExistsChatUser(CurrentUserId,Position);
                    CheckUserIdRef.child(friends.getFriendUid()).child(CurrentUserId).setValue(receivers);

                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String key = dataSnapshot.getKey();
                                        if (!key.equals(friends.getFriendUid())){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    CheckUserIdRef.child(friends.getFriendUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(friends.getFriendUid()).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String Key = dataSnapshot.getKey();
                                        if (!Key.equals(CurrentUserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(friends.getFriendUid()).child(Key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Send Notification
        String msg = fileName;
        UserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.child(CurrentUserId).getValue(com.social.makefriends.model.UserDetails.class);
                UserDetails userDetails2 = snapshot.child(friends.getFriendUid()).getValue(com.social.makefriends.model.UserDetails.class);
                String onlineStatus = userDetails2.getOnlineStatus();
                muteNotificationRefD.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(CurrentUserId).exists()){
                            Log.e("Message","Exists");
                        }else {
                            if (onlineStatus.equals("Offline")){
                                sendNotification(friends.getFriendUid(),userDetails.getUserName(),msg);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendFile(String messageDetails, Friends friends, String fileName, String fileType) {
        DatabaseReference MsgRef = FirebaseDatabase.getInstance().getReference("Messages");
        String MessagePushKey = MsgRef.push().getKey();

        Calendar Date = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yy");
        String CurrentDate = date.format(Date.getTime());

        Calendar Time = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("hh:mm aa");
        String CurrentTime = time.format(Time.getTime());

        CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(friends.getFriendUid()).exists()){
                    MsgSentDetails = "not_seen";

                    HashMap<String,Object> addMessage = new HashMap<>();
                    addMessage.put("messageDetails",messageDetails);
                    addMessage.put("messageTime",CurrentTime);
                    addMessage.put("messageDate",CurrentDate);
                    addMessage.put("messageType",messageType);
                    addMessage.put("senderId",CurrentUserId);
                    addMessage.put("receiverId",friends.getFriendUid());
                    addMessage.put("messageId",MessagePushKey);
                    addMessage.put("messageSeenDetails",MsgSentDetails);
                    addMessage.put("fileName",fileName);
                    addMessage.put("fileType",fileType);
                    addMessage.put("senderSideMsgDelete","not_delete");
                    addMessage.put("receiverSideMsgDelete","not_delete");
                    addMessage.put("forward","yes");

                    MsgRef.child(MessagePushKey).setValue(addMessage);

                    HashMap<String,Object> position = new HashMap<String, Object>();
                    position.put("firstPosition","first");
                    CheckUserIdRef.child(CurrentUserId).child(friends.getFriendUid()).updateChildren(position);
                    CheckUserIdRef.child(friends.getFriendUid()).child(CurrentUserId).updateChildren(position);

                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String key = dataSnapshot.getKey();
                                        if (!key.equals(friends.getFriendUid())){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    CheckUserIdRef.child(friends.getFriendUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(friends.getFriendUid()).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String Key = dataSnapshot.getKey();
                                        if (!Key.equals(CurrentUserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(friends.getFriendUid()).child(Key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    MsgSentDetails = "not_seen";
                    Position = "first";

                    HashMap<String,Object> addMessage = new HashMap<>();
                    addMessage.put("messageDetails",messageDetails);
                    addMessage.put("messageTime",CurrentTime);
                    addMessage.put("messageDate",CurrentDate);
                    addMessage.put("messageType",messageType);
                    addMessage.put("senderId",CurrentUserId);
                    addMessage.put("receiverId",friends.getFriendUid());
                    addMessage.put("messageId",MessagePushKey);
                    addMessage.put("messageSeenDetails",MsgSentDetails);
                    addMessage.put("fileName",fileName);
                    addMessage.put("fileType",fileType);
                    addMessage.put("senderSideMsgDelete","not_delete");
                    addMessage.put("receiverSideMsgDelete","not_delete");
                    addMessage.put("forward","yes");

                    MsgRef.child(MessagePushKey).setValue(addMessage);

                    ExistsChatUser senders = new ExistsChatUser(friends.getFriendUid(),Position);
                    CheckUserIdRef.child(CurrentUserId).child(friends.getFriendUid()).setValue(senders);
                    ExistsChatUser receivers = new ExistsChatUser(CurrentUserId,Position);
                    CheckUserIdRef.child(friends.getFriendUid()).child(CurrentUserId).setValue(receivers);

                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String key = dataSnapshot.getKey();
                                        if (!key.equals(friends.getFriendUid())){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    CheckUserIdRef.child(friends.getFriendUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(friends.getFriendUid()).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String Key = dataSnapshot.getKey();
                                        if (!Key.equals(CurrentUserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(friends.getFriendUid()).child(Key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        //Send Notification
        String msg = fileName;
        UserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.child(CurrentUserId).getValue(com.social.makefriends.model.UserDetails.class);
                UserDetails userDetails2 = snapshot.child(friends.getFriendUid()).getValue(com.social.makefriends.model.UserDetails.class);
                String onlineStatus = userDetails2.getOnlineStatus();
                muteNotificationRefD.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(CurrentUserId).exists()){
                            Log.e("Message","Exists");
                        }else {
                            if (onlineStatus.equals("Offline")){
                                sendNotification(friends.getFriendUid(),userDetails.getUserName(),msg);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendImage(String messageDetails, Friends friends) {
        DatabaseReference MsfRef = FirebaseDatabase.getInstance().getReference("Messages");
        String MessagePushKey = MsfRef.push().getKey();

        Calendar Date = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yy");
        String CurrentDate = date.format(Date.getTime());

        Calendar Time = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("hh:mm aa");
        String CurrentTime = time.format(Time.getTime());

        CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(friends.getFriendUid()).exists()){
                    MsgSentDetails = "not_seen";

                    HashMap<String,Object> addMessage = new HashMap<>();
                    addMessage.put("messageDetails",messageDetails);
                    addMessage.put("messageTime",CurrentTime);
                    addMessage.put("messageDate",CurrentDate);
                    addMessage.put("messageType",messageType);
                    addMessage.put("senderId",CurrentUserId);
                    addMessage.put("receiverId",friends.getFriendUid());
                    addMessage.put("messageId",MessagePushKey);
                    addMessage.put("messageSeenDetails",MsgSentDetails);
                    addMessage.put("fileName",null);
                    addMessage.put("fileType",null);
                    addMessage.put("senderSideMsgDelete","not_delete");
                    addMessage.put("receiverSideMsgDelete","not_delete");
                    addMessage.put("forward","yes");

                    MsfRef.child(MessagePushKey).setValue(addMessage);

                    HashMap<String,Object> position = new HashMap<String, Object>();
                    position.put("firstPosition","first");
                    CheckUserIdRef.child(CurrentUserId).child(friends.getFriendUid()).updateChildren(position);
                    CheckUserIdRef.child(friends.getFriendUid()).child(CurrentUserId).updateChildren(position);

                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String key = dataSnapshot.getKey();
                                        if (!key.equals(friends.getFriendUid())){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    CheckUserIdRef.child(friends.getFriendUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(friends.getFriendUid()).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String Key = dataSnapshot.getKey();
                                        if (!Key.equals(CurrentUserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(friends.getFriendUid()).child(Key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    MsgSentDetails = "not_seen";
                    Position = "first";

                    HashMap<String,Object> addMessage = new HashMap<>();
                    addMessage.put("messageDetails",messageDetails);
                    addMessage.put("messageTime",CurrentTime);
                    addMessage.put("messageDate",CurrentDate);
                    addMessage.put("messageType",messageType);
                    addMessage.put("senderId",CurrentUserId);
                    addMessage.put("receiverId",friends.getFriendUid());
                    addMessage.put("messageId",MessagePushKey);
                    addMessage.put("messageSeenDetails",MsgSentDetails);
                    addMessage.put("fileName",null);
                    addMessage.put("fileType",null);
                    addMessage.put("senderSideMsgDelete","not_delete");
                    addMessage.put("receiverSideMsgDelete","not_delete");
                    addMessage.put("forward","yes");

                    MsfRef.child(MessagePushKey).setValue(addMessage);

                    ExistsChatUser senders = new ExistsChatUser(friends.getFriendUid(),Position);
                    CheckUserIdRef.child(CurrentUserId).child(friends.getFriendUid()).setValue(senders);
                    ExistsChatUser receivers = new ExistsChatUser(CurrentUserId,Position);
                    CheckUserIdRef.child(friends.getFriendUid()).child(CurrentUserId).setValue(receivers);

                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String key = dataSnapshot.getKey();
                                        if (!key.equals(friends.getFriendUid())){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    CheckUserIdRef.child(friends.getFriendUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(friends.getFriendUid()).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String Key = dataSnapshot.getKey();
                                        if (!Key.equals(CurrentUserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(friends.getFriendUid()).child(Key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Send Notification
        String msg = "Image";
        UserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.child(CurrentUserId).getValue(com.social.makefriends.model.UserDetails.class);
                UserDetails userDetails2 = snapshot.child(friends.getFriendUid()).getValue(com.social.makefriends.model.UserDetails.class);
                String onlineStatus = userDetails2.getOnlineStatus();
                muteNotificationRefD.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(CurrentUserId).exists()){
                            Log.e("Message","Exists");
                        }else {
                            if (onlineStatus.equals("Offline")){
                                sendNotification(friends.getFriendUid(),userDetails.getUserName(),msg);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String sendMessageInput, Friends friends) {
        Calendar Date = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yy");
        String CurrentDate = date.format(Date.getTime());

        Calendar Time = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("hh:mm aa");
        String CurrentTime = time.format(Time.getTime());

        CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(friends.getFriendUid()).exists()){
                    MsgSentDetails = "not_seen";
                    String MessagePushKey = MessageRef.push().getKey();

                    HashMap<String,Object> addMessage = new HashMap<>();
                    addMessage.put("messageDetails",sendMessageInput);
                    addMessage.put("messageTime",CurrentTime);
                    addMessage.put("messageDate",CurrentDate);
                    addMessage.put("messageType",messageType);
                    addMessage.put("senderId",CurrentUserId);
                    addMessage.put("receiverId",friends.getFriendUid());
                    addMessage.put("messageId",MessagePushKey);
                    addMessage.put("messageSeenDetails",MsgSentDetails);
                    addMessage.put("fileName",null);
                    addMessage.put("fileType",null);
                    addMessage.put("senderSideMsgDelete","not_delete");
                    addMessage.put("receiverSideMsgDelete","not_delete");
                    addMessage.put("forward","yes");

                    MessageRef.child(MessagePushKey).setValue(addMessage);

                    HashMap<String,Object> position = new HashMap<String, Object>();
                    position.put("firstPosition","first");
                    CheckUserIdRef.child(CurrentUserId).child(friends.getFriendUid()).updateChildren(position);
                    CheckUserIdRef.child(friends.getFriendUid()).child(CurrentUserId).updateChildren(position);

                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String key = dataSnapshot.getKey();
                                        if (!key.equals(friends.getFriendUid())){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    CheckUserIdRef.child(friends.getFriendUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(friends.getFriendUid()).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String Key = dataSnapshot.getKey();
                                        if (!Key.equals(CurrentUserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(friends.getFriendUid()).child(Key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    MsgSentDetails = "not_seen";
                    Position = "first";
                    String MessagePushKey = MessageRef.push().getKey();

                    HashMap<String,Object> addMessage = new HashMap<>();
                    addMessage.put("messageDetails",sendMessageInput);
                    addMessage.put("messageTime",CurrentTime);
                    addMessage.put("messageDate",CurrentDate);
                    addMessage.put("messageType",messageType);
                    addMessage.put("senderId",CurrentUserId);
                    addMessage.put("receiverId",friends.getFriendUid());
                    addMessage.put("messageId",MessagePushKey);
                    addMessage.put("messageSeenDetails",MsgSentDetails);
                    addMessage.put("fileName",null);
                    addMessage.put("fileType",null);
                    addMessage.put("senderSideMsgDelete","not_delete");
                    addMessage.put("receiverSideMsgDelete","not_delete");
                    addMessage.put("forward","yes");

                    MessageRef.child(MessagePushKey).setValue(addMessage);

                    ExistsChatUser senders = new ExistsChatUser(friends.getFriendUid(),Position);
                    CheckUserIdRef.child(CurrentUserId).child(friends.getFriendUid()).setValue(senders);
                    ExistsChatUser receivers = new ExistsChatUser(CurrentUserId,Position);
                    CheckUserIdRef.child(friends.getFriendUid()).child(CurrentUserId).setValue(receivers);

                    CheckUserIdRef.child(CurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(CurrentUserId).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String key = dataSnapshot.getKey();
                                        if (!key.equals(friends.getFriendUid())){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(CurrentUserId).child(key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    CheckUserIdRef.child(friends.getFriendUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Query query1 = CheckUserIdRef.child(friends.getFriendUid()).orderByChild("firstPosition").equalTo("first");
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        String Key = dataSnapshot.getKey();
                                        if (!Key.equals(CurrentUserId)){
                                            HashMap<String,Object> position = new HashMap<String, Object>();
                                            position.put("firstPosition","");
                                            CheckUserIdRef.child(friends.getFriendUid()).child(Key).updateChildren(position);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Send Notification
        String msg = sendMessageInput;
        UserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.child(CurrentUserId).getValue(com.social.makefriends.model.UserDetails.class);
                UserDetails userDetails2 = snapshot.child(friends.getFriendUid()).getValue(com.social.makefriends.model.UserDetails.class);
                String onlineStatus = userDetails2.getOnlineStatus();
                muteNotificationRefD.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(CurrentUserId).exists()){
                            Log.e("Message","Exists");
                        }else {
                            if (onlineStatus.equals("Offline")){
                                sendNotification(friends.getFriendUid(),userDetails.getUserName(),msg);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
//                                    Toast.makeText(activity, "Failed!!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    class ShareMessageHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImg;
        TextView nameTxt,userNameTxt;
        Button sendMsgBtn;
        public ShareMessageHolder(@NonNull View itemView) {
            super(itemView);

            profileImg = itemView.findViewById(R.id.share_user_profile_image);
            nameTxt = itemView.findViewById(R.id.share_user_name);
            userNameTxt = itemView.findViewById(R.id.share_user__username);
            sendMsgBtn = itemView.findViewById(R.id.send_share_message);
        }
    }

}
