package com.social.makefriends.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.social.makefriends.R;
import com.social.makefriends.ui.activity.chat.friendrequest.SendFriendRequestDuplicate;
import com.social.makefriends.ui.activity.chat.ChatWithFriends;
import com.social.makefriends.model.UserDetails;

import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    private String channelId;
    private int notificationId;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        channelId = "pushNotification";
        notificationId = new Random().nextInt();

        userRef = FirebaseDatabase.getInstance().getReference("User Details");
        firebaseAuth = FirebaseAuth.getInstance();
        String receiverUserId = remoteMessage.getData().get("receiverNotificationUserId");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null && receiverUserId.equals(firebaseUser.getUid())){
             createNotification(remoteMessage,receiverUserId);
        }

    }

    private void createNotification(RemoteMessage remoteMessage, String receiverUserId) {
        userRef = FirebaseDatabase.getInstance().getReference("User Details");
        String senderUserId = remoteMessage.getData().get("senderNotificationUserId");
        String body = remoteMessage.getData().get("notificationBody");
        String title = remoteMessage.getData().get("notificationTitle");
        String messageType = remoteMessage.getData().get("notificationType");

        if (messageType.equals("chatting")){
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Intent intent = new Intent(MyFirebaseMessaging.this,ChatWithFriends.class);
                    Bundle bundle = new Bundle();
                    UserDetails userDetails = snapshot.child(senderUserId).getValue(UserDetails.class);
                    UserDetails userDetails2 = snapshot.child(firebaseAuth.getUid()).getValue(UserDetails.class);
                    String Image = userDetails.getUserProfileImageUrl();
                    String Wallpaper = userDetails2.getChatBackgroundWall();
                    bundle.putString("UserId",senderUserId);
                    bundle.putString("Value","N");
                    bundle.putString("Image",Image);
                    bundle.putString("ChatBackground",Wallpaper);
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessaging.this,0,intent,0);

                    sendNotification(pendingIntent,title,body);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MyFirebaseMessaging.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else if (messageType.equals("friendRequest")){
            Intent intent = new Intent(MyFirebaseMessaging.this, SendFriendRequestDuplicate.class);
            Bundle bundle = new Bundle();
            bundle.putString("UserId",senderUserId);
            bundle.putString("Value","TLB");
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessaging.this,0,intent,0);

            sendNotification(pendingIntent,title,body);
        }
    }

    private void sendNotification(PendingIntent pendingIntent, String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,channelId);
        builder.setSmallIcon(R.drawable.splash_screen_logo);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence channelName = "notification";
            String channelDescription = "This notification is used for get Push Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId,channelName,importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat compat = NotificationManagerCompat.from(this);
        compat.notify(notificationId,builder.build());
    }
}
