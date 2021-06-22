package com.social.makefriends.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.friendrequest.chatting.ShareMessages;
import com.social.makefriends.friendrequest.chatting.ViewMessageImages;
import com.social.makefriends.model.ChatMessages;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RetrieveMessageAdapter extends RecyclerView.Adapter<RetrieveMessageAdapter.MessageHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {
    private FirebaseAuth firebaseAuth;
    private Activity activity;
    private List<ChatMessages> chatList;
    private ArrayList<String> song_name = new ArrayList<>();
    private ArrayList<String> song_url = new ArrayList<>();
    private ArrayList<JcAudio> jcAudios = new ArrayList<>();
    private ArrayAdapter<String> song_adapter;
    private String Image;
    private String Value,UserId,chatWallpaper;
    private DatabaseReference MessageRef,userRef;
    private static final  int PERMISSION = 999;

    public RetrieveMessageAdapter(Activity activity, List<ChatMessages> chatList, String Image, String Value, String UserId, String chatWallpaper) {
        this.activity = activity;
        this.chatList = chatList;
        this.Image = Image;
        this.Value = Value;
        this.UserId = UserId;
        this.chatWallpaper = chatWallpaper;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_list,parent,false);
        MessageHolder messageHolder = new MessageHolder(view);
        return messageHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {

        firebaseAuth = FirebaseAuth.getInstance();
        ChatMessages chatMessages = chatList.get(position);
        String CurrentUserId = firebaseAuth.getCurrentUser().getUid();

        //Message Details
        String messageDetails = chatMessages.getMessageDetails();
        String messageType = chatMessages.getMessageType();

        MessageRef = FirebaseDatabase.getInstance().getReference("Messages");
        userRef = FirebaseDatabase.getInstance().getReference("User Details").child(CurrentUserId);

        if (Image.equals("None")){
            holder.Profile_Image.setImageResource(R.drawable.profile_image);
            holder.Profile_Image2.setImageResource(R.drawable.profile_image);
        }else {
            Picasso.get().load(Image).fit().placeholder(R.drawable.profile_image).into(holder.Profile_Image);
            Picasso.get().load(Image).fit().placeholder(R.drawable.profile_image).into(holder.Profile_Image2);
        }

        if (messageType.equals("text")) {
            holder.txtRetFor.setVisibility(View.GONE);
            holder.txtSenFor.setVisibility(View.GONE);
            holder.imgRetFor.setVisibility(View.GONE);
            holder.imgSenFor.setVisibility(View.GONE);
            holder.fileRetFor.setVisibility(View.GONE);
            holder.fileSenFor.setVisibility(View.GONE);
            holder.audioRetFor.setVisibility(View.GONE);
            holder.audioSenFor.setVisibility(View.GONE);
            holder.ReceiverMessage.setVisibility(View.GONE);
            holder.Profile_Image.setVisibility(View.GONE);
            holder.Profile_Image2.setVisibility(View.GONE);
            holder.SenderMessage.setVisibility(View.GONE);
            holder.ChatTime.setVisibility(View.GONE);
            holder.ChatTime_2.setVisibility(View.GONE);
            holder.ChatTime_3.setVisibility(View.GONE);
            holder.ChatTime_4.setVisibility(View.GONE);
            holder.MessageSend.setVisibility(View.INVISIBLE);
            holder.MessageSendSuccessful.setVisibility(View.INVISIBLE);
            holder.MessageWaiting.setVisibility(View.INVISIBLE);
            holder.MessageSend_2.setVisibility(View.GONE);
            holder.MessageSendSuccessful_2.setVisibility(View.GONE);
            holder.MessageWaiting_2.setVisibility(View.GONE);
            holder.SenderMessageImage.setVisibility(View.GONE);
            holder.ReceiverMessageImage.setVisibility(View.GONE);
            holder.SenderConst.setVisibility(View.GONE);
            holder.ReceiverConst.setVisibility(View.GONE);
            holder.ReceiverConst2.setVisibility(View.GONE);
            holder.SenderConst2.setVisibility(View.GONE);
            holder.ReceiverConst3.setVisibility(View.GONE);
            holder.SenderConst3.setVisibility(View.GONE);

            if (CurrentUserId.equals(chatMessages.getSenderId())){
                if (chatMessages.getSenderSideMsgDelete().equals("not_delete")){
                    if (chatMessages.getMessageSeenDetails().equals("seen")){
                        holder.MessageSendSuccessful.setVisibility(View.VISIBLE);
                    }else if (chatMessages.getMessageSeenDetails().equals("not_seen")){
                        holder.MessageSend.setVisibility(View.VISIBLE);
                    }else {
                        holder.MessageWaiting.setVisibility(View.VISIBLE);
                    }
                    if (chatMessages.getForward().equals("yes")){
                        holder.txtSenFor.setTypeface(holder.txtSenFor.getTypeface(), Typeface.BOLD);
                        holder.txtSenFor.setVisibility(View.VISIBLE);
                    }
                    holder.ChatTime.setVisibility(View.VISIBLE);
                    holder.SenderMessage.setVisibility(View.VISIBLE);
                    holder.SenderMessage.setText(chatMessages.getMessageDetails()+"                           ");
                    holder.ChatTime.setText(chatMessages.getMessageDate()+" "+ chatMessages.getMessageTime());

                    holder.SenderMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            CharSequence options[] = new CharSequence[]{
                                    "Copy",
                                    "Delete for me",
                                    "Delete for Everyone",
                                    "Forward",
                                    "Cancel"
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Delete Message");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0){
                                        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("label", chatMessages.getMessageDetails());
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(activity, "copied", Toast.LENGTH_SHORT).show();
                                    }else if (which == 1){
                                        HashMap<String,Object> deleteMsg = new HashMap<String, Object>();
                                        deleteMsg.put("senderSideMsgDelete","delete");
                                        MessageRef.child(chatMessages.getMessageId()).updateChildren(deleteMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(activity, "Delete successful", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(activity, "Not deleted", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                    }else if (which == 2){
                                        HashMap<String,Object> deleteMsg = new HashMap<String, Object>();
                                        deleteMsg.put("senderSideMsgDelete","delete");
                                        deleteMsg.put("receiverSideMsgDelete","delete");
                                        MessageRef.child(chatMessages.getMessageId()).updateChildren(deleteMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(activity, "Delete successful", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(activity, "Not deleted", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                    }else if (which == 3){
                                        Intent intent = new Intent(activity, ShareMessages.class);
                                        intent.putExtra("Value",Value);
                                        intent.putExtra("UserId",UserId);
                                        intent.putExtra("Image",Image);
                                        intent.putExtra("ChatBackground",chatWallpaper);
                                        intent.putExtra("MessageType",messageType);
                                        intent.putExtra("MessageDetails",messageDetails);
                                        intent.putExtra("fileType","null");
                                        intent.putExtra("fileName","null");
                                        v.getContext().startActivity(intent);
                                        ((AppCompatActivity)v.getContext()).finish();
                                    }else {
                                        dialog.dismiss();
                                    }
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                            alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                            alertDialog.show();
                            return true;
                        }
                    });
                }else {
                    holder.SenderMessage.setVisibility(View.GONE);
                    holder.ChatTime.setVisibility(View.GONE);
                    holder.MessageSend.setVisibility(View.GONE);
                    holder.MessageSendSuccessful.setVisibility(View.GONE);
                    holder.MessageWaiting.setVisibility(View.GONE);
                }
            }else {
                if (chatMessages.getReceiverSideMsgDelete().equals("not_delete")){
                    holder.ReceiverMessage.setVisibility(View.VISIBLE);
                    holder.Profile_Image.setVisibility(View.VISIBLE);
                    holder.SenderMessage.setVisibility(View.INVISIBLE);
                    holder.ChatTime_2.setVisibility(View.VISIBLE);
                    holder.MessageSendSuccessful.setVisibility(View.INVISIBLE);
                    holder.MessageSend.setVisibility(View.INVISIBLE);
                    holder.MessageWaiting.setVisibility(View.INVISIBLE);
                    holder.ReceiverMessage.setText(chatMessages.getMessageDetails()+"                          ");
                    holder.ChatTime_2.setText(chatMessages.getMessageDate()+" "+ chatMessages.getMessageTime());

                    if (chatMessages.getForward().equals("yes")){
                        holder.txtRetFor.setTypeface(holder.txtRetFor.getTypeface(), Typeface.BOLD);
                        holder.txtRetFor.setVisibility(View.VISIBLE);
                    }

                    holder.ReceiverMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            CharSequence options[] = new CharSequence[]{
                                    "Copy",
                                    "Delete for me",
                                    "Forward",
                                    "Cancel"
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Delete Message");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0){
                                        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText("label", chatMessages.getMessageDetails());
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(activity, "copied", Toast.LENGTH_SHORT).show();
                                    }else if (which == 1){
                                        HashMap<String,Object> deleteMsg = new HashMap<String, Object>();
                                        deleteMsg.put("receiverSideMsgDelete","delete");
                                        MessageRef.child(chatMessages.getMessageId()).updateChildren(deleteMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(activity, "Delete successful", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(activity, "Not deleted", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                    }else if (which == 2){
                                        Intent intent = new Intent(activity, ShareMessages.class);
                                        intent.putExtra("Value",Value);
                                        intent.putExtra("UserId",UserId);
                                        intent.putExtra("Image",Image);
                                        intent.putExtra("ChatBackground",chatWallpaper);
                                        intent.putExtra("MessageType",messageType);
                                        intent.putExtra("MessageDetails",messageDetails);
                                        intent.putExtra("fileType","null");
                                        intent.putExtra("fileName","null");
                                        v.getContext().startActivity(intent);
                                        ((AppCompatActivity)v.getContext()).finish();
                                    }else{
                                        dialog.dismiss();
                                    }
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                            alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                            alertDialog.show();
                            return true;
                        }
                    });
                }else {
                    holder.Profile_Image.setVisibility(View.GONE);
                    holder.ReceiverMessage.setVisibility(View.GONE);
                    holder.ChatTime_2.setVisibility(View.GONE);
                }
            }
        }

        if (messageType.equals("image")) {
            holder.txtRetFor.setVisibility(View.GONE);
            holder.txtSenFor.setVisibility(View.GONE);
            holder.imgRetFor.setVisibility(View.GONE);
            holder.imgSenFor.setVisibility(View.GONE);
            holder.fileRetFor.setVisibility(View.GONE);
            holder.fileSenFor.setVisibility(View.GONE);
            holder.audioRetFor.setVisibility(View.GONE);
            holder.audioSenFor.setVisibility(View.GONE);
            holder.ReceiverMessage.setVisibility(View.GONE);
            holder.Profile_Image.setVisibility(View.GONE);
            holder.Profile_Image2.setVisibility(View.GONE);
            holder.SenderMessage.setVisibility(View.GONE);
            holder.ChatTime.setVisibility(View.GONE);
            holder.ChatTime_2.setVisibility(View.GONE);
            holder.ChatTime_3.setVisibility(View.GONE);
            holder.ChatTime_4.setVisibility(View.GONE);
            holder.MessageSend.setVisibility(View.GONE);
            holder.MessageSendSuccessful.setVisibility(View.GONE);
            holder.MessageWaiting.setVisibility(View.GONE);
            holder.MessageSend_2.setVisibility(View.INVISIBLE);
            holder.MessageSendSuccessful_2.setVisibility(View.INVISIBLE);
            holder.MessageWaiting_2.setVisibility(View.INVISIBLE);
            holder.SenderMessageImage.setVisibility(View.GONE);
            holder.ReceiverMessageImage.setVisibility(View.GONE);
            holder.SenderConst.setVisibility(View.GONE);
            holder.ReceiverConst.setVisibility(View.GONE);
            holder.ReceiverConst2.setVisibility(View.GONE);
            holder.SenderConst2.setVisibility(View.GONE);
            holder.ReceiverConst3.setVisibility(View.GONE);
            holder.SenderConst3.setVisibility(View.GONE);

            if (CurrentUserId.equals(chatMessages.getSenderId())){
                if (chatMessages.getSenderSideMsgDelete().equals("not_delete")){
                    if (chatMessages.getMessageSeenDetails().equals("seen")){
                        holder.MessageSendSuccessful_2.setVisibility(View.VISIBLE);
                    }else if (chatMessages.getMessageSeenDetails().equals("not_seen")){
                        holder.MessageSend_2.setVisibility(View.VISIBLE);
                    }else {
                        holder.MessageWaiting_2.setVisibility(View.VISIBLE);
                    }
                    holder.ChatTime_4.setVisibility(View.VISIBLE);
                    holder.ChatTime_4.setText(chatMessages.getMessageDate()+" "+ chatMessages.getMessageTime());
                    holder.SenderMessageImage.setVisibility(View.VISIBLE);

                    CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(activity);
                    circularProgressDrawable.setStrokeWidth(10);
                    circularProgressDrawable.setCenterRadius(42);
                    circularProgressDrawable.setColorSchemeColors(R.color.light_blue_900);
                    circularProgressDrawable.start();

                    Picasso.get().load(chatMessages.getMessageDetails()).placeholder(circularProgressDrawable).into(holder.SenderMessageImage);

                    if (chatMessages.getForward().equals("yes")){
                        holder.imgSenFor.setTypeface(holder.txtSenFor.getTypeface(), Typeface.BOLD);
                        holder.imgSenFor.setVisibility(View.VISIBLE);
                    }

                    holder.SenderMessageImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), ViewMessageImages.class);
                            intent.putExtra("Value",Value);
                            intent.putExtra("UserId",UserId);
                            intent.putExtra("Image",Image);
                            intent.putExtra("Message",chatMessages.getMessageDetails());
                            intent.putExtra("MsgId",chatMessages.getMessageId());
                            intent.putExtra("ChatBackground",chatWallpaper);
                            v.getContext().startActivity(intent);
                            ((AppCompatActivity)v.getContext()).finish();
                        }
                    });

                    holder.SenderMessageImage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            CharSequence options[] = new CharSequence[]{
                                    "View Image",
                                    "Delete for me",
                                    "Delete for Everyone",
                                    "Forward",
                                    "Cancel"
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Delete Message");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0){
                                        Intent intent = new Intent(v.getContext(), ViewMessageImages.class);
                                        intent.putExtra("Value",Value);
                                        intent.putExtra("UserId",UserId);
                                        intent.putExtra("Image",Image);
                                        intent.putExtra("Message",chatMessages.getMessageDetails());
                                        intent.putExtra("MsgId",chatMessages.getMessageId());
                                        intent.putExtra("ChatBackground",chatWallpaper);
                                        v.getContext().startActivity(intent);
                                        ((AppCompatActivity)v.getContext()).finish();
                                    }else if (which == 1){
                                        HashMap<String,Object> deleteMsg = new HashMap<String, Object>();
                                        deleteMsg.put("senderSideMsgDelete","delete");
                                        MessageRef.child(chatMessages.getMessageId()).updateChildren(deleteMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(activity, "Delete successful", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(activity, "Not deleted", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                    }else if (which == 2){
                                        HashMap<String,Object> deleteMsg = new HashMap<String, Object>();
                                        deleteMsg.put("senderSideMsgDelete","delete");
                                        deleteMsg.put("receiverSideMsgDelete","delete");
                                        MessageRef.child(chatMessages.getMessageId()).updateChildren(deleteMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(activity, "Delete successful", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(activity, "Not deleted", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                    }else if (which == 3){
                                        Intent intent = new Intent(activity, ShareMessages.class);
                                        intent.putExtra("Value",Value);
                                        intent.putExtra("UserId",UserId);
                                        intent.putExtra("Image",Image);
                                        intent.putExtra("ChatBackground",chatWallpaper);
                                        intent.putExtra("MessageType",messageType);
                                        intent.putExtra("MessageDetails",messageDetails);
                                        intent.putExtra("fileType","null");
                                        intent.putExtra("fileName","null");
                                        v.getContext().startActivity(intent);
                                        ((AppCompatActivity)v.getContext()).finish();
                                    }else {
                                        dialog.dismiss();
                                    }
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                            alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                            alertDialog.show();
                            return true;
                        }
                    });
                }else {
                    holder.SenderMessageImage.setVisibility(View.GONE);
                    holder.ChatTime_4.setVisibility(View.GONE);
                    holder.MessageSend_2.setVisibility(View.GONE);
                    holder.MessageSendSuccessful_2.setVisibility(View.GONE);
                    holder.MessageWaiting_2.setVisibility(View.GONE);
                }
            }else {
                if (chatMessages.getReceiverSideMsgDelete().equals("not_delete")){
                    holder.MessageSendSuccessful_2.setVisibility(View.INVISIBLE);
                    holder.MessageSend_2.setVisibility(View.INVISIBLE);
                    holder.MessageWaiting_2.setVisibility(View.INVISIBLE);
                    holder.ChatTime_3.setVisibility(View.VISIBLE);
                    holder.ChatTime_3.setText(chatMessages.getMessageDate()+" "+ chatMessages.getMessageTime());
                    holder.Profile_Image.setVisibility(View.VISIBLE);
                    holder.ReceiverMessageImage.setVisibility(View.VISIBLE);

                    CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(activity);
                    circularProgressDrawable.setStrokeWidth(10);
                    circularProgressDrawable.setCenterRadius(42);
                    circularProgressDrawable.setColorSchemeColors(R.color.silver);
                    circularProgressDrawable.start();

                    Picasso.get().load(chatMessages.getMessageDetails()).placeholder(circularProgressDrawable).into(holder.ReceiverMessageImage);

                    if (chatMessages.getForward().equals("yes")){
                        holder.imgRetFor.setTypeface(holder.txtSenFor.getTypeface(), Typeface.BOLD);
                        holder.imgRetFor.setVisibility(View.GONE);
                    }

                    holder.ReceiverMessageImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), ViewMessageImages.class);
                            intent.putExtra("Value",Value);
                            intent.putExtra("UserId",UserId);
                            intent.putExtra("Image",Image);
                            intent.putExtra("Message",chatMessages.getMessageDetails());
                            intent.putExtra("MsgId",chatMessages.getMessageId());
                            intent.putExtra("ChatBackground",chatWallpaper);
                            v.getContext().startActivity(intent);
                            ((AppCompatActivity)v.getContext()).finish();
                        }
                    });

                    holder.ReceiverMessageImage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            CharSequence options[] = new CharSequence[]{
                                    "View Image",
                                    "Delete for me",
                                    "Forward",
                                    "Cancel"
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Delete Message");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0){
                                        Intent intent = new Intent(v.getContext(), ViewMessageImages.class);
                                        intent.putExtra("Value",Value);
                                        intent.putExtra("UserId",UserId);
                                        intent.putExtra("Image",Image);
                                        intent.putExtra("Message",chatMessages.getMessageDetails());
                                        intent.putExtra("MsgId",chatMessages.getMessageId());
                                        intent.putExtra("ChatBackground",chatWallpaper);
                                        v.getContext().startActivity(intent);
                                        ((AppCompatActivity)v.getContext()).finish();
                                    }else if (which == 1){
                                        HashMap<String,Object> deleteMsg = new HashMap<String, Object>();
                                        deleteMsg.put("receiverSideMsgDelete","delete");
                                        MessageRef.child(chatMessages.getMessageId()).updateChildren(deleteMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(activity, "Delete successful", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(activity, "Not deleted", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                    }else if (which == 2){
                                        Intent intent = new Intent(activity, ShareMessages.class);
                                        intent.putExtra("Value",Value);
                                        intent.putExtra("UserId",UserId);
                                        intent.putExtra("Image",Image);
                                        intent.putExtra("ChatBackground",chatWallpaper);
                                        intent.putExtra("MessageType",messageType);
                                        intent.putExtra("MessageDetails",messageDetails);
                                        intent.putExtra("fileType","null");
                                        intent.putExtra("fileName","null");
                                        v.getContext().startActivity(intent);
                                        ((AppCompatActivity)v.getContext()).finish();
                                    }else {
                                        dialog.dismiss();
                                    }
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                            alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                            alertDialog.show();
                            return true;
                        }
                    });

                }else {
                    holder.Profile_Image.setVisibility(View.GONE);
                    holder.ReceiverMessageImage.setVisibility(View.GONE);
                    holder.ChatTime_3.setVisibility(View.GONE);
                    holder.MessageSend_2.setVisibility(View.GONE);
                    holder.MessageSendSuccessful_2.setVisibility(View.GONE);
                    holder.MessageWaiting_2.setVisibility(View.GONE);
                }
            }
        }

        if (messageType.equals("file")) {
            holder.txtRetFor.setVisibility(View.GONE);
            holder.txtSenFor.setVisibility(View.GONE);
            holder.imgRetFor.setVisibility(View.GONE);
            holder.imgSenFor.setVisibility(View.GONE);
            holder.fileRetFor.setVisibility(View.GONE);
            holder.fileSenFor.setVisibility(View.GONE);
            holder.audioRetFor.setVisibility(View.GONE);
            holder.audioSenFor.setVisibility(View.GONE);
            holder.ReceiverMessage.setVisibility(View.GONE);
            holder.Profile_Image.setVisibility(View.GONE);
            holder.Profile_Image2.setVisibility(View.GONE);
            holder.SenderMessage.setVisibility(View.GONE);
            holder.ChatTime.setVisibility(View.GONE);
            holder.ChatTime_2.setVisibility(View.GONE);
            holder.ChatTime_3.setVisibility(View.GONE);
            holder.ChatTime_4.setVisibility(View.GONE);
            holder.MessageSend.setVisibility(View.GONE);
            holder.MessageSendSuccessful.setVisibility(View.GONE);
            holder.MessageWaiting.setVisibility(View.GONE);
            holder.MessageSend_2.setVisibility(View.GONE);
            holder.MessageSendSuccessful_2.setVisibility(View.GONE);
            holder.MessageWaiting_2.setVisibility(View.GONE);
            holder.MessageSendSuccessful_3.setVisibility(View.INVISIBLE);
            holder.MessageSend_3.setVisibility(View.INVISIBLE);
            holder.MessageWaiting_3.setVisibility(View.INVISIBLE);
            holder.SenderMessageImage.setVisibility(View.GONE);
            holder.ReceiverMessageImage.setVisibility(View.GONE);
            holder.SenderConst.setVisibility(View.GONE);
            holder.ReceiverConst.setVisibility(View.GONE);
            holder.ReceiverConst2.setVisibility(View.GONE);
            holder.SenderConst2.setVisibility(View.GONE);
            holder.ReceiverConst3.setVisibility(View.GONE);
            holder.SenderConst3.setVisibility(View.GONE);

            if (CurrentUserId.equals(chatMessages.getSenderId())){
                if (chatMessages.getSenderSideMsgDelete().equals("not_delete")){
                    holder.SenderConst.setVisibility(View.VISIBLE);
                    holder.ChatTime_6.setVisibility(View.VISIBLE);
                    holder.ChatTime_6.setText(chatMessages.getMessageDate()+" "+ chatMessages.getMessageTime());
                    holder.SenderFileName.setText(chatMessages.getFileName());

                    if (chatMessages.getMessageSeenDetails().equals("seen")){
                        holder.MessageSendSuccessful_3.setVisibility(View.VISIBLE);
                    }else if (chatMessages.getMessageSeenDetails().equals("not_seen")){
                        holder.MessageSend_3.setVisibility(View.VISIBLE);
                    }else {
                        holder.MessageWaiting_3.setVisibility(View.VISIBLE);
                    }

                    if (chatMessages.getFileType().equals("application/pdf")){
                        holder.SenderFileType.setText(".pdf");
                        holder.SenderFileImage.setImageResource(R.drawable.pdf);
                    }else if (chatMessages.getFileType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
                        holder.SenderFileType.setText(".docx");
                        holder.SenderFileImage.setImageResource(R.drawable.doc_docx);
                    }else if (chatMessages.getFileType().equals("application/msword")){
                        holder.SenderFileType.setText(".doc");
                        holder.SenderFileImage.setImageResource(R.drawable.doc_docx);
                    }else if (chatMessages.getFileType().equals("application/vnd.ms-powerpoint")){
                        holder.SenderFileType.setText(".ppt");
                        holder.SenderFileImage.setImageResource(R.drawable.ppt);
                    }else if (chatMessages.getFileType().equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")){
                        holder.SenderFileType.setText(".pptx");
                        holder.SenderFileImage.setImageResource(R.drawable.pptx);
                    }else if (chatMessages.getFileType().equals("application/zip")){
                        holder.SenderFileType.setText(".zip");
                        holder.SenderFileImage.setImageResource(R.drawable.zip);
                    }else if (chatMessages.getFileType().equals("text/html")){
                        holder.SenderFileType.setText(".html");
                        holder.SenderFileImage.setImageResource(R.drawable.html);
                    }else if (chatMessages.getFileType().equals("text/plain")){
                        holder.SenderFileType.setText(".txt");
                        holder.SenderFileImage.setImageResource(R.drawable.text);
                    }else if (chatMessages.getFileType().equals("application/vnd.ms-excel")){
                        holder.SenderFileType.setText(".xls");
                        holder.SenderFileImage.setImageResource(R.drawable.excel);
                    }else {
                        holder.SenderFileType.setText(".xlsx");
                        holder.SenderFileImage.setImageResource(R.drawable.excel);
                    }

                    if (chatMessages.getForward().equals("yes")){
                        holder.fileSenFor.setTypeface(holder.txtSenFor.getTypeface(), Typeface.BOLD);
                        holder.fileSenFor.setVisibility(View.VISIBLE);
                    }

                    holder.SenderConst.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            CharSequence options[] = new CharSequence[]{
                                    "View File",
                                    "Delete for me",
                                    "Delete for Everyone",
                                    "Forward",
                                    "Cancel"
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Delete Message");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0){
                                        Toast.makeText(activity, "View file", Toast.LENGTH_SHORT).show();
                                    }else if (which == 1){
                                        HashMap<String,Object> deleteMsg = new HashMap<String, Object>();
                                        deleteMsg.put("senderSideMsgDelete","delete");
                                        MessageRef.child(chatMessages.getMessageId()).updateChildren(deleteMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(activity, "Delete successful", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(activity, "Not delete", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                    }else if (which == 2){
                                        HashMap<String,Object> deleteMsg = new HashMap<String, Object>();
                                        deleteMsg.put("senderSideMsgDelete","delete");
                                        deleteMsg.put("receiverSideMsgDelete","delete");
                                        MessageRef.child(chatMessages.getMessageId()).updateChildren(deleteMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(activity, "Delete successful", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(activity, "Not delete", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                    }else if (which == 3){
                                        Intent intent = new Intent(activity, ShareMessages.class);
                                        intent.putExtra("Value",Value);
                                        intent.putExtra("UserId",UserId);
                                        intent.putExtra("Image",Image);
                                        intent.putExtra("ChatBackground",chatWallpaper);
                                        intent.putExtra("MessageType",messageType);
                                        intent.putExtra("MessageDetails",messageDetails);
                                        intent.putExtra("fileType",chatMessages.getFileType());
                                        intent.putExtra("fileName",chatMessages.getFileName());
                                        v.getContext().startActivity(intent);
                                        ((AppCompatActivity)v.getContext()).finish();
                                    }else {
                                        dialog.dismiss();
                                    }
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                            alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                            alertDialog.show();
                            return true;
                        }
                    });

                    holder.sender_file_download.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ContextCompat.checkSelfPermission(v.getContext(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                if (chatMessages.getFileType().equals("application/pdf")) {
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);

                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".pdf");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".pdf");
                                    }
                                    DownloadManager downloadManager=(DownloadManager)v.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(Arrays.toString(mimeTypes));
                                    //request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);

                                    Toast.makeText(activity, "pdf", Toast.LENGTH_SHORT).show();
                                }else if (chatMessages.getFileType().equals("application/zip")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".zip");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".zip");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else if (chatMessages.getFileType().equals("text/plain")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".txt");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".txt");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else if (chatMessages.getFileType().equals("text/html")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }

                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".html");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".html");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else if (chatMessages.getFileType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".docx");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".docx");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else if (chatMessages.getFileType().equals("application/msword")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".doc");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".doc");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else if (chatMessages.getFileType().equals("application/vnd.ms-powerpoint")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".ppt");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".ppt");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else if (chatMessages.getFileType().equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".pptx");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".pptx");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else if (chatMessages.getFileType().equals("application/vnd.ms-excel")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".xls");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".xls");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else {
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File", title + ".xlsx");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".xlsx");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }
                            } else {
                                requestStoragePermission(v);
                            }
                        }
                    });
                }else {
                    holder.SenderConst.setVisibility(View.GONE);
                }
            }else {
                if (chatMessages.getReceiverSideMsgDelete().equals("not_delete")){
                    if (chatMessages.getFileType().equals("application/pdf")){
                        holder.ReceiverFileType.setText(".pdf");
                        holder.ReceiverFileImage.setImageResource(R.drawable.pdf);
                    }else if (chatMessages.getFileType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
                        holder.ReceiverFileType.setText(".docx");
                        holder.ReceiverFileImage.setImageResource(R.drawable.doc_docx);
                    }else if (chatMessages.getFileType().equals("application/msword")){
                        holder.ReceiverFileType.setText(".doc");
                        holder.ReceiverFileImage.setImageResource(R.drawable.doc_docx);
                    }else if (chatMessages.getFileType().equals("application/vnd.ms-powerpoint")){
                        holder.ReceiverFileType.setText(".ppt");
                        holder.ReceiverFileImage.setImageResource(R.drawable.pptx);
                    }else if (chatMessages.getFileType().equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")){
                        holder.ReceiverFileType.setText(".pptx");
                        holder.ReceiverFileImage.setImageResource(R.drawable.pptx);
                    }else if (chatMessages.getFileType().equals("application/zip")){
                        holder.ReceiverFileType.setText(".zip");
                        holder.ReceiverFileImage.setImageResource(R.drawable.zip);
                    }else if (chatMessages.getFileType().equals("text/html")){
                        holder.ReceiverFileType.setText(".html");
                        holder.ReceiverFileImage.setImageResource(R.drawable.html);
                    }else if (chatMessages.getFileType().equals("text/plain")){
                        holder.ReceiverFileType.setText(".txt");
                        holder.ReceiverFileImage.setImageResource(R.drawable.text);
                    }else if (chatMessages.getFileType().equals("application/vnd.ms-excel")){
                        holder.ReceiverFileType.setText(".xls");
                        holder.ReceiverFileImage.setImageResource(R.drawable.excel);
                    }else {
                        holder.ReceiverFileType.setText(".xlsx");
                        holder.ReceiverFileImage.setImageResource(R.drawable.excel);
                    }
                    holder.ReceiverConst.setVisibility(View.VISIBLE);
                    holder.ChatTime_5.setText(chatMessages.getMessageDate()+" "+ chatMessages.getMessageTime());
                    holder.Profile_Image.setVisibility(View.VISIBLE);
                    holder.ReceiverFileName.setText(chatMessages.getFileName());

                    if (chatMessages.getForward().equals("yes")){
                        holder.fileRetFor.setTypeface(holder.txtSenFor.getTypeface(), Typeface.BOLD);
                        holder.fileRetFor.setVisibility(View.VISIBLE);
                    }

                    holder.receiver_file_download.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ContextCompat.checkSelfPermission(v.getContext(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                if (chatMessages.getFileType().equals("application/pdf")) {
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".pdf");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".pdf");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else if (chatMessages.getFileType().equals("application/zip")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".zip");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".zip");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else if (chatMessages.getFileType().equals("text/plain")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".txt");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".txt");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else if (chatMessages.getFileType().equals("text/html")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".html");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".html");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else if (chatMessages.getFileType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".docx");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".docx");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else if (chatMessages.getFileType().equals("application/msword")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".doc");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".doc");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else if (chatMessages.getFileType().equals("application/vnd.ms-powerpoint")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".ppt");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".ppt");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else if (chatMessages.getFileType().equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".pptx");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".pptx");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else if (chatMessages.getFileType().equals("application/vnd.ms-excel")){
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".xls");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".xls");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }else {
                                    String msg = chatMessages.getMessageDetails();
                                    String title = chatMessages.getFileName();
                                    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                    request.setTitle(title);
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    }
                                    try {
                                        request.setDestinationInExternalPublicDir("/Make Friends/Message/File",title+".xlsx");
                                    }catch (Exception e){
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".xlsx");
                                    }
                                    DownloadManager downloadManager=(DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                                    String[] mimeTypes = {"application/pdf","application/zip","text/plain","text/html", //.pdf & .zip & .txt & .html
                                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document","application/msword", // .docx & .doc
                                            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", //.ppt & .pptx
                                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx
                                    request.setMimeType(String.valueOf(mimeTypes));
//                                    request.allowScanningByMediaScanner();
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                    downloadManager.enqueue(request);
                                }
                            }else {
                                requestStoragePermission(v);
                            }
                        }
                    });

                    holder.ReceiverConst.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            CharSequence options[] = new CharSequence[]{
                                    "View File",
                                    "Delete for me",
                                    "Forward",
                                    "Cancel"
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Delete Message");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0){
                                        Toast.makeText(activity, "view File", Toast.LENGTH_SHORT).show();
                                    }else if (which == 1){
                                        HashMap<String,Object> deleteMsg = new HashMap<String, Object>();
                                        deleteMsg.put("receiverSideMsgDelete","delete");
                                        MessageRef.child(chatMessages.getMessageId()).updateChildren(deleteMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(activity, "Delete successful", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(activity, "Not delete", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                    }else if (which == 2){
                                        Intent intent = new Intent(activity, ShareMessages.class);
                                        intent.putExtra("Value",Value);
                                        intent.putExtra("UserId",UserId);
                                        intent.putExtra("Image",Image);
                                        intent.putExtra("ChatBackground",chatWallpaper);
                                        intent.putExtra("MessageType",messageType);
                                        intent.putExtra("MessageDetails",messageDetails);
                                        intent.putExtra("fileType",chatMessages.getFileType());
                                        intent.putExtra("fileName",chatMessages.getFileName());
                                        v.getContext().startActivity(intent);
                                        ((AppCompatActivity)v.getContext()).finish();
                                    }else {
                                        dialog.dismiss();
                                    }
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                            alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                            alertDialog.show();
                            return true;
                        }
                    });
                }else {
                    holder.ReceiverConst.setVisibility(View.GONE);
                    holder.Profile_Image.setVisibility(View.GONE);
                }
            }
        }

        if (messageType.equals("audio")) {
            holder.txtRetFor.setVisibility(View.GONE);
            holder.txtSenFor.setVisibility(View.GONE);
            holder.imgRetFor.setVisibility(View.GONE);
            holder.imgSenFor.setVisibility(View.GONE);
            holder.fileRetFor.setVisibility(View.GONE);
            holder.fileSenFor.setVisibility(View.GONE);
            holder.audioRetFor.setVisibility(View.GONE);
            holder.audioSenFor.setVisibility(View.GONE);
            holder.ReceiverMessage.setVisibility(View.GONE);
            holder.Profile_Image.setVisibility(View.GONE);
            holder.Profile_Image2.setVisibility(View.GONE);
            holder.SenderMessage.setVisibility(View.GONE);
            holder.ChatTime.setVisibility(View.GONE);
            holder.ChatTime_2.setVisibility(View.GONE);
            holder.ChatTime_3.setVisibility(View.GONE);
            holder.ChatTime_4.setVisibility(View.GONE);
            holder.MessageSend.setVisibility(View.GONE);
            holder.MessageSendSuccessful.setVisibility(View.GONE);
            holder.MessageWaiting.setVisibility(View.GONE);
            holder.MessageSend_2.setVisibility(View.GONE);
            holder.MessageSendSuccessful_2.setVisibility(View.GONE);
            holder.MessageWaiting_2.setVisibility(View.GONE);
            holder.MessageSendSuccessful_3.setVisibility(View.GONE);
            holder.MessageSend_3.setVisibility(View.GONE);
            holder.MessageWaiting_3.setVisibility(View.GONE);
            holder.MessageSendSuccessful_4.setVisibility(View.INVISIBLE);
            holder.MessageSend_4.setVisibility(View.INVISIBLE);
            holder.MessageWaiting_4.setVisibility(View.INVISIBLE);
            holder.SenderMessageImage.setVisibility(View.GONE);
            holder.ReceiverMessageImage.setVisibility(View.GONE);
            holder.SenderConst.setVisibility(View.GONE);
            holder.ReceiverConst.setVisibility(View.GONE);
            holder.ReceiverConst2.setVisibility(View.GONE);
            holder.SenderConst2.setVisibility(View.GONE);
            holder.ReceiverConst3.setVisibility(View.GONE);
            holder.SenderConst3.setVisibility(View.GONE);

            if (CurrentUserId.equals(chatMessages.getSenderId())){
                if (chatMessages.getSenderSideMsgDelete().equals("not_delete")){
                    if (chatMessages.getMessageSeenDetails().equals("seen")){
                        holder.MessageSendSuccessful_4.setVisibility(View.VISIBLE);
                    }else if (chatMessages.getMessageSeenDetails().equals("not_seen")){
                        holder.MessageSend_4.setVisibility(View.VISIBLE);
                    }else {
                        holder.MessageWaiting_4.setVisibility(View.VISIBLE);
                    }
                    holder.SenderConst2.setVisibility(View.VISIBLE);
                    holder.ChatTime_8.setText(chatMessages.getMessageDate()+" "+ chatMessages.getMessageTime());
                    holder.SenderAudioName.setText(chatMessages.getFileName());

                    if (chatMessages.getForward().equals("yes")){
                        holder.audioSenFor.setTypeface(holder.txtSenFor.getTypeface(), Typeface.BOLD);
                        holder.audioSenFor.setVisibility(View.VISIBLE);
                    }

                    MessageRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            song_name.clear();
                            song_url.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ChatMessages chatMessages = dataSnapshot.getValue(ChatMessages.class);
                                if (chatMessages.getReceiverId().equals(CurrentUserId) && chatMessages.getSenderId().equals(UserId) ||
                                        chatMessages.getReceiverId().equals(UserId) && chatMessages.getSenderId().equals(CurrentUserId)) {
                                    if (chatMessages.getMessageType().equals("audio")){
                                        song_name.add(chatMessages.getFileName());
                                        song_url.add(chatMessages.getMessageDetails());
                                        jcAudios.add(JcAudio.createFromURL(chatMessages.getFileName(),chatMessages.getMessageDetails()));
                                    }
                                }
                            }
                            song_adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1,song_name){
                                @NonNull
                                @Override
                                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    View view = super.getView(position,convertView,parent);
                                    TextView textView = view.findViewById(android.R.id.text1);
                                    textView.setSingleLine(true);
                                    textView.setMaxLines(1);
                                    return view;
                                }
                            };
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    holder.SenderConst2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialog dialog = new Dialog(v.getContext());
                            dialog.setContentView(R.layout.audio_bottom_sheet);
                            dialog.setCanceledOnTouchOutside(false);

                            JcPlayerView jcplayerView = dialog.findViewById(R.id.jcplayer);
                            ListView listView = dialog.findViewById(R.id.audio_list);

                            jcplayerView.initPlaylist(jcAudios,null);
                            listView.setAdapter(song_adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    jcplayerView.playAudio(jcAudios.get(position));
                                }
                            });
                            jcplayerView.createNotification();
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                            dialog.show();
                        }
                    });

                    holder.SenderDownloadAudio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ContextCompat.checkSelfPermission(activity,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                String msg = chatMessages.getMessageDetails();
                                String title = chatMessages.getFileName();
                                DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                request.setTitle(title);
                                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                    request.allowScanningByMediaScanner();
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                }
                                try{
                                    request.setDestinationInExternalPublicDir("/Make Friends/Message/Audio",title+".mp3");
                                }catch (Exception e){
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".mp3");
                                }
                                DownloadManager downloadManager=(DownloadManager)v.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                                request.setMimeType("audio/*");
//                                request.allowScanningByMediaScanner();
                                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                downloadManager.enqueue(request);
                            }else {
                                requestStoragePermission(v);
                            }
                        }
                    });

                    holder.SenderConst2.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            CharSequence options[] = new CharSequence[]{
                                    "View",
                                    "Delete for me",
                                    "Delete for Everyone",
                                    "Forward",
                                    "Cancel"
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Delete Message");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0){
                                        Dialog dialogs = new Dialog(v.getContext());
                                        dialogs.setContentView(R.layout.audio_bottom_sheet);
                                        dialogs.setCanceledOnTouchOutside(false);

                                        JcPlayerView jcplayerView = dialogs.findViewById(R.id.jcplayer);
                                        ListView listView = dialogs.findViewById(R.id.audio_list);

                                        jcplayerView.initPlaylist(jcAudios,null);
                                        listView.setAdapter(song_adapter);
                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                jcplayerView.playAudio(jcAudios.get(position));
                                            }
                                        });
                                        jcplayerView.createNotification();
                                        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialogs.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                                        dialogs.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                                        dialogs.show();
                                        dialog.dismiss();
                                    }else if (which == 1){
                                        HashMap<String,Object> deleteMsg = new HashMap<String, Object>();
                                        deleteMsg.put("senderSideMsgDelete","delete");
                                        MessageRef.child(chatMessages.getMessageId()).updateChildren(deleteMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(activity, "Delete successful", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(activity, "Not delete", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                    }else if (which == 2){
                                        HashMap<String,Object> deleteMsg = new HashMap<String, Object>();
                                        deleteMsg.put("senderSideMsgDelete","delete");
                                        deleteMsg.put("receiverSideMsgDelete","delete");
                                        MessageRef.child(chatMessages.getMessageId()).updateChildren(deleteMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(activity, "Delete successful", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(activity, "Not delete", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                    }else if (which == 3){
                                        Intent intent = new Intent(activity, ShareMessages.class);
                                        intent.putExtra("Value",Value);
                                        intent.putExtra("UserId",UserId);
                                        intent.putExtra("Image",Image);
                                        intent.putExtra("ChatBackground",chatWallpaper);
                                        intent.putExtra("MessageType",messageType);
                                        intent.putExtra("MessageDetails",messageDetails);
                                        intent.putExtra("fileType","");
                                        intent.putExtra("fileName",chatMessages.getFileName());
                                        v.getContext().startActivity(intent);
                                        ((AppCompatActivity)v.getContext()).finish();
                                    }else {
                                        dialog.dismiss();
                                    }
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                            alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                            alertDialog.show();
                            return true;
                        }
                    });
                }else {
                    holder.SenderConst2.setVisibility(View.GONE);
                }
            }else {
                if (chatMessages.getReceiverSideMsgDelete().equals("not_delete")){
                    holder.ReceiverConst2.setVisibility(View.VISIBLE);
                    holder.ChatTime_7.setText(chatMessages.getMessageDate()+" "+ chatMessages.getMessageTime());
                    holder.Profile_Image.setVisibility(View.VISIBLE);
                    holder.ReceiverAudioName.setText(chatMessages.getFileName());

                    if (chatMessages.getForward().equals("yes")){
                        holder.audioRetFor.setTypeface(holder.txtSenFor.getTypeface(), Typeface.BOLD);
                        holder.audioRetFor.setVisibility(View.VISIBLE);
                    }

                    MessageRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            song_name.clear();
                            song_url.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ChatMessages chatMessages = dataSnapshot.getValue(ChatMessages.class);
                                if (chatMessages.getReceiverId().equals(CurrentUserId) && chatMessages.getSenderId().equals(UserId) ||
                                        chatMessages.getReceiverId().equals(UserId) && chatMessages.getSenderId().equals(CurrentUserId)) {
                                    if (chatMessages.getMessageType().equals("audio")){
                                        song_name.add(chatMessages.getFileName());
                                        song_url.add(chatMessages.getMessageDetails());
                                        jcAudios.add(JcAudio.createFromURL(chatMessages.getFileName(),chatMessages.getMessageDetails()));
                                    }
                                }
                            }
                            song_adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1,song_name){
                                @NonNull
                                @Override
                                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                    View view = super.getView(position,convertView,parent);
                                    TextView textView = view.findViewById(android.R.id.text1);
                                    textView.setSingleLine(true);
                                    textView.setMaxLines(1);
                                    return view;
                                }
                            };
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    holder.ReceiverConst2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Dialog dialog = new Dialog(v.getContext());
                            dialog.setContentView(R.layout.audio_bottom_sheet);
                            dialog.setCanceledOnTouchOutside(false);

                            JcPlayerView jcplayerView = dialog.findViewById(R.id.jcplayer);
                            ListView listView = dialog.findViewById(R.id.audio_list);
                            jcplayerView.initPlaylist(jcAudios,null);
                            listView.setAdapter(song_adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    jcplayerView.playAudio(jcAudios.get(position));
                                }
                            });
                            jcplayerView.createNotification();
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                            dialog.show();
                        }
                    });

                    holder.ReceiverDownloadAudio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ContextCompat.checkSelfPermission(activity,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                String msg = chatMessages.getMessageDetails();
                                String title = chatMessages.getFileName();
                                DownloadManager.Request request=new DownloadManager.Request(Uri.parse(msg));
                                request.setTitle(title);
                                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
//                                    request.allowScanningByMediaScanner();
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                }
                                try {
                                    request.setDestinationInExternalPublicDir("/Make Friends/Message/Audio",title+".mp3");
                                }catch (Exception e){
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+".mp3");
                                }

                                DownloadManager downloadManager=(DownloadManager)v.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                                request.setMimeType("audio/*");
//                                request.allowScanningByMediaScanner();
                                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                                downloadManager.enqueue(request);
                            }else {
                                requestStoragePermission(v);
                            }
                        }
                    });

                    holder.ReceiverConst2.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            CharSequence options[] = new CharSequence[]{
                                    "View",
                                    "Delete for me",
                                    "Forward",
                                    "Cancel"
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Delete Message");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0){
                                        Dialog dialogs = new Dialog(v.getContext());
                                        dialogs.setContentView(R.layout.audio_bottom_sheet);
                                        dialogs.setCanceledOnTouchOutside(false);

                                        JcPlayerView jcplayerView = dialogs.findViewById(R.id.jcplayer);
                                        ListView listView = dialogs.findViewById(R.id.audio_list);
                                        jcplayerView.initPlaylist(jcAudios,null);
                                        listView.setAdapter(song_adapter);
                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                jcplayerView.playAudio(jcAudios.get(position));
                                            }
                                        });
                                        jcplayerView.createNotification();
                                        dialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialogs.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                                        dialogs.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                                        dialogs.show();
                                        dialog.dismiss();
                                    }else if (which == 1){
                                        HashMap<String,Object> deleteMsg = new HashMap<String, Object>();
                                        deleteMsg.put("receiverSideMsgDelete","delete");
                                        MessageRef.child(chatMessages.getMessageId()).updateChildren(deleteMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(activity, "Delete successful", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(activity, "Not delete", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                    }else if (which == 2){
                                        Intent intent = new Intent(activity, ShareMessages.class);
                                        intent.putExtra("Value",Value);
                                        intent.putExtra("UserId",UserId);
                                        intent.putExtra("Image",Image);
                                        intent.putExtra("ChatBackground",chatWallpaper);
                                        intent.putExtra("MessageType",messageType);
                                        intent.putExtra("MessageDetails",messageDetails);
                                        intent.putExtra("fileType","");
                                        intent.putExtra("fileName",chatMessages.getFileName());
                                        v.getContext().startActivity(intent);
                                        ((AppCompatActivity)v.getContext()).finish();
                                    }else {
                                        dialog.dismiss();
                                    }
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                            alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                            alertDialog.show();
                            return true;
                        }
                    });
                }else {
                    holder.ReceiverConst2.setVisibility(View.GONE);
                    holder.Profile_Image.setVisibility(View.GONE);
                }
            }
        }

        if (messageType.equals("icon")) {
            holder.txtRetFor.setVisibility(View.GONE);
            holder.txtSenFor.setVisibility(View.GONE);
            holder.imgRetFor.setVisibility(View.GONE);
            holder.imgSenFor.setVisibility(View.GONE);
            holder.fileRetFor.setVisibility(View.GONE);
            holder.fileSenFor.setVisibility(View.GONE);
            holder.audioRetFor.setVisibility(View.GONE);
            holder.audioSenFor.setVisibility(View.GONE);
            holder.ReceiverMessage.setVisibility(View.GONE);
            holder.Profile_Image.setVisibility(View.GONE);
            holder.Profile_Image2.setVisibility(View.GONE);
            holder.SenderMessage.setVisibility(View.GONE);
            holder.ChatTime.setVisibility(View.GONE);
            holder.ChatTime_2.setVisibility(View.GONE);
            holder.ChatTime_3.setVisibility(View.GONE);
            holder.ChatTime_4.setVisibility(View.GONE);
            holder.MessageSend.setVisibility(View.GONE);
            holder.MessageSendSuccessful.setVisibility(View.GONE);
            holder.MessageWaiting.setVisibility(View.GONE);
            holder.MessageSend_2.setVisibility(View.GONE);
            holder.MessageSendSuccessful_2.setVisibility(View.GONE);
            holder.MessageWaiting_2.setVisibility(View.GONE);
            holder.MessageSendSuccessful_3.setVisibility(View.GONE);
            holder.MessageSend_3.setVisibility(View.GONE);
            holder.MessageWaiting_3.setVisibility(View.GONE);
            holder.MessageSendSuccessful_4.setVisibility(View.INVISIBLE);
            holder.MessageSend_4.setVisibility(View.INVISIBLE);
            holder.MessageWaiting_4.setVisibility(View.INVISIBLE);
            holder.SenderMessageImage.setVisibility(View.GONE);
            holder.ReceiverMessageImage.setVisibility(View.GONE);
            holder.SenderConst.setVisibility(View.GONE);
            holder.ReceiverConst.setVisibility(View.GONE);
            holder.ReceiverConst2.setVisibility(View.GONE);
            holder.SenderConst2.setVisibility(View.GONE);
            holder.ReceiverConst3.setVisibility(View.GONE);
            holder.SenderConst3.setVisibility(View.GONE);

            if (chatMessages.getSenderId().equals(CurrentUserId)){
                if (chatMessages.getSenderSideMsgDelete().equals("not_delete")){
                    holder.SenderConst3.setVisibility(View.VISIBLE);

                    holder.SenderConst3.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            CharSequence options[] = new CharSequence[]{
                                    "Delete for me",
                                    "Delete for Everyone",
                                    "Cancel"
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Delete Message");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0){
                                        HashMap<String,Object> deleteMsg = new HashMap<String, Object>();
                                        deleteMsg.put("senderSideMsgDelete","delete");
                                        MessageRef.child(chatMessages.getMessageId()).updateChildren(deleteMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(activity, "Delete successful", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(activity, "Not deleted", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                    }else if (which == 1){
                                        HashMap<String,Object> deleteMsg = new HashMap<String, Object>();
                                        deleteMsg.put("senderSideMsgDelete","delete");
                                        deleteMsg.put("receiverSideMsgDelete","delete");
                                        MessageRef.child(chatMessages.getMessageId()).updateChildren(deleteMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(activity, "Delete successful", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(activity, "Not deleted", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                    }else {
                                        dialog.dismiss();
                                    }
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                            alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                            alertDialog.show();
                            return true;
                        }
                    });
                }else {
                    holder.SenderConst3.setVisibility(View.GONE);
                }
            }else {
                if(chatMessages.getReceiverSideMsgDelete().equals("not_delete")){
                    holder.Profile_Image2.setVisibility(View.VISIBLE);
                    holder.ReceiverConst3.setVisibility(View.VISIBLE);
                    holder.ReceiverConst3.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            CharSequence options[] = new CharSequence[]{
                                    "Delete for me",
                                    "Cancel"
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Delete Message");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0){
                                        HashMap<String,Object> deleteMsg = new HashMap<String, Object>();
                                        deleteMsg.put("receiverSideMsgDelete","delete");
                                        MessageRef.child(chatMessages.getMessageId()).updateChildren(deleteMsg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(activity, "Delete successful", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(activity, "Not deleted", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                    }else{
                                        dialog.dismiss();
                                    }
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                            alertDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Toast;
                            alertDialog.show();
                            return true;
                        }
                    });
                }else {
                    holder.Profile_Image2.setVisibility(View.GONE);
                    holder.Profile_Image.setVisibility(View.GONE);
                    holder.ReceiverConst3.setVisibility(View.GONE);
                }
            }

        }
    }

    private void requestStoragePermission(View v) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) v.getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) v.getContext(),
                                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION);
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
            ActivityCompat.requestPermissions((Activity) v.getContext(),
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(activity, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(activity, "Enable Permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    class MessageHolder extends RecyclerView.ViewHolder{
        private CircleImageView Profile_Image,Profile_Image2;
        private TextView ChatTime,ChatTime_2,ChatTime_3,ChatTime_4,ChatTime_5,ChatTime_6,ChatTime_7,ChatTime_8,ReceiverFileName,
                         SenderFileName,SenderFileType,ReceiverFileType,ReceiverAudioName,SenderAudioName;
        private ImageView MessageSend,MessageWaiting,MessageSendSuccessful,MessageSend_2,MessageWaiting_2,MessageSendSuccessful_2,MessageSend_3,MessageWaiting_3
                         ,MessageSendSuccessful_3,SenderMessageImage,ReceiverMessageImage,sender_file_download,receiver_file_download,ReceiverFileImage,SenderFileImage
                         ,ReceiverDownloadAudio,SenderDownloadAudio,MessageSend_4,MessageWaiting_4,MessageSendSuccessful_4,SenderThumb,ReceiverThumb;
        private ConstraintLayout ReceiverConst,SenderConst,ReceiverConst2,SenderConst2,ReceiverConst3,SenderConst3;
        private EmojiTextView SenderMessage,ReceiverMessage;
        private TextView txtRetFor,txtSenFor,imgRetFor,imgSenFor,fileRetFor,fileSenFor,audioRetFor,audioSenFor;
        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            txtRetFor = itemView.findViewById(R.id.receiver_forward_msg);
            txtSenFor = itemView.findViewById(R.id.sender_forward_msg);
            imgRetFor = itemView.findViewById(R.id.receiver_forward_img);
            imgSenFor = itemView.findViewById(R.id.sender_forward_img);
            fileRetFor = itemView.findViewById(R.id.receiver_forward_file);
            fileSenFor = itemView.findViewById(R.id.sender_forward_file);
            audioRetFor = itemView.findViewById(R.id.receiver_forward_audio);
            audioSenFor = itemView.findViewById(R.id.sender_forward_audio);

            ChatTime = itemView.findViewById(R.id.sender_chat_time);
            ChatTime_2 = itemView.findViewById(R.id.receiver_chat_time);
            ChatTime_3 = itemView.findViewById(R.id.receiver_chat_time2);
            ChatTime_4 = itemView.findViewById(R.id.sender_chat_time2);
            ChatTime_5 = itemView.findViewById(R.id.receiver_chat_time3);
            ChatTime_6 = itemView.findViewById(R.id.sender_chat_time3);
            ChatTime_7 = itemView.findViewById(R.id.receiver_chat_time4);
            ChatTime_8 = itemView.findViewById(R.id.sender_chat_time4);
            ReceiverFileName = itemView.findViewById(R.id.receiver_file_name);
            SenderFileName = itemView.findViewById(R.id.sender_file_name);
            SenderFileType = itemView.findViewById(R.id.sender_fileType);
            ReceiverFileType = itemView.findViewById(R.id.receiver_fileType);
            ReceiverAudioName = itemView.findViewById(R.id.receiver_audio_name);
            SenderAudioName = itemView.findViewById(R.id.sender_audio_name);

            ReceiverDownloadAudio = itemView.findViewById(R.id.receiver_download_audio);
            SenderDownloadAudio = itemView.findViewById(R.id.sender_download_audio);

            ReceiverFileImage = itemView.findViewById(R.id.receiver_file_image);
            SenderFileImage = itemView.findViewById(R.id.sender_file_image);
            Profile_Image = itemView.findViewById(R.id.receiver_profile_image);
            Profile_Image2 = itemView.findViewById(R.id.receiver_profile_image2);
            SenderMessage = itemView.findViewById(R.id.sender_message);
            ReceiverMessage = itemView.findViewById(R.id.receiver_message);
            MessageSend = itemView.findViewById(R.id.message_send);
            MessageWaiting = itemView.findViewById(R.id.waiting);
            MessageSendSuccessful = itemView.findViewById(R.id.message_send_successful);
            MessageSend_2 = itemView.findViewById(R.id.message_send2);
            MessageWaiting_2 = itemView.findViewById(R.id.waiting2);
            MessageSendSuccessful_2 = itemView.findViewById(R.id.message_send_successful2);
            MessageSend_3 = itemView.findViewById(R.id.message_send3);
            MessageWaiting_3 = itemView.findViewById(R.id.waiting3);
            MessageSendSuccessful_3 = itemView.findViewById(R.id.message_send_successful3);
            MessageSend_4 = itemView.findViewById(R.id.message_send4);
            MessageWaiting_4 = itemView.findViewById(R.id.waiting4);
            MessageSendSuccessful_4 = itemView.findViewById(R.id.message_send_successful4);
            SenderMessageImage = itemView.findViewById(R.id.sender_image_message);
            ReceiverMessageImage = itemView.findViewById(R.id.receiver_image_message);
            sender_file_download = itemView.findViewById(R.id.download_file2);
            receiver_file_download = itemView.findViewById(R.id.download_file);
            SenderThumb = itemView.findViewById(R.id.sender_thumb);
            ReceiverThumb = itemView.findViewById(R.id.receiver_thumb);

            SenderConst = itemView.findViewById(R.id.sender_const_file);
            ReceiverConst = itemView.findViewById(R.id.receiver_const_file);

            SenderConst2 = itemView.findViewById(R.id.sender_const_audio);
            ReceiverConst2 = itemView.findViewById(R.id.receiver_const_audio);

            SenderConst3 = itemView.findViewById(R.id.sender_const_thumb);
            ReceiverConst3 = itemView.findViewById(R.id.receiver_const_thumb);
        }
    }
}