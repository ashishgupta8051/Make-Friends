package com.social.friends.notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.social.friends.R;
import com.social.friends.friendrequest.SendFriendRequestDuplicate;
import com.social.friends.friendrequest.chatting.ChatWithFriends;
import com.social.friends.model.UserDetails;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

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

        int j = Integer.parseInt(senderUserId.replaceAll("[\\D]",""));
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

                    PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessaging.this,j,intent,PendingIntent.FLAG_ONE_SHOT);
                    sendNotification(pendingIntent);
                }

                private void sendNotification(PendingIntent pendingIntent) {
                    Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MyFirebaseMessaging.this,"")
                            .setSmallIcon(R.drawable.splash2)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setAutoCancel(true)
                            .setSound(defaultSound)
                            .setContentIntent(pendingIntent);
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    int i = 0;
                    if (j > 0){
                        i = j;
                    }
                    notificationManager.notify(i,builder.build());
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

            PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessaging.this,j,intent,PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MyFirebaseMessaging.this,"")
                    .setSmallIcon(R.drawable.splash2)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSound)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            int i = 0;
            if (j > 0){
                i = j;
            }
            notificationManager.notify(i,builder.build());

        }


    }
}
