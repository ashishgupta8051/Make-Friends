package com.social.makefriends.friendrequest.chatting.background;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.makefriends.R;
import com.social.makefriends.friendrequest.chatting.ChatWithFriends;
import com.social.makefriends.model.UserDetails;
import com.social.makefriends.utils.SharedPrefManager;

import java.util.HashMap;

public class ViewWallpaper extends AppCompatActivity {
    private String userId,image,value,wallpaper,transfer,chatWallpaper;
    private String currentUserId;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userDetailsRef;
    private ImageView imageView;
    private Button button;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wallpaper);

        userId = getIntent().getExtras().get("UserId").toString();
        value = getIntent().getExtras().get("Value").toString();
        image = getIntent().getExtras().get("Image").toString();
        wallpaper = getIntent().getExtras().get("Wallpaper").toString();
        chatWallpaper = getIntent().getExtras().get("ChatBackground").toString();
        transfer = getIntent().getExtras().get("transfer").toString();

        imageView = findViewById(R.id.setWallpaperImg);
        button = findViewById(R.id.setWallpaperBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        userDetailsRef = FirebaseDatabase.getInstance().getReference("User Details").child(currentUserId);

        sharedPrefManager = new SharedPrefManager(ViewWallpaper.this);

        if (transfer.contentEquals("def")){
            imageView.setImageResource(R.drawable.d);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String d = "d";
                    HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                    updateWallpaper.put("chatBackgroundWall",d);
                    userDetailsRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                        String backgroundWall = userDetails.getChatBackgroundWall();
                                        sharedPrefManager.saveWallpaper("d");
                                        Toast.makeText(ViewWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ViewWallpaper.this, ChatWithFriends.class);
                                        intent.putExtra("Value",value);
                                        intent.putExtra("Image",image);
                                        intent.putExtra("UserId",userId);
                                        intent.putExtra("ChatBackground",backgroundWall);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(ViewWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                Toast.makeText(ViewWallpaper.this, "Wallpaper did not updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }else if (transfer.contentEquals("lig")){
            if (wallpaper.contentEquals("l1")){
                imageView.setImageResource(R.drawable.l1);
                String s1 = "l1";
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                        updateWallpaper.put("chatBackgroundWall",s1);
                        userDetailsRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                            String backgroundWall = userDetails.getChatBackgroundWall();
                                            sharedPrefManager.saveWallpaper("l1");
                                            Toast.makeText(ViewWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewWallpaper.this, ChatWithFriends.class);
                                            intent.putExtra("Value",value);
                                            intent.putExtra("Image",image);
                                            intent.putExtra("UserId",userId);
                                            intent.putExtra("ChatBackground",backgroundWall);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ViewWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    Toast.makeText(ViewWallpaper.this, "Wallpaper did not updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }else if (wallpaper.contentEquals("l2")){
                imageView.setImageResource(R.drawable.l2);
                String s2 = "l2";
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                        updateWallpaper.put("chatBackgroundWall",s2);
                        userDetailsRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                            String backgroundWall = userDetails.getChatBackgroundWall();
                                            sharedPrefManager.saveWallpaper("l2");
                                            Toast.makeText(ViewWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewWallpaper.this, ChatWithFriends.class);
                                            intent.putExtra("Value",value);
                                            intent.putExtra("Image",image);
                                            intent.putExtra("UserId",userId);
                                            intent.putExtra("ChatBackground",backgroundWall);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ViewWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    Toast.makeText(ViewWallpaper.this, "Wallpaper did not updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }else if (wallpaper.contentEquals("l3")){
                imageView.setImageResource(R.drawable.l3);
                String s3 = "l3";
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                        updateWallpaper.put("chatBackgroundWall",s3);
                        userDetailsRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                            String backgroundWall = userDetails.getChatBackgroundWall();
                                            sharedPrefManager.saveWallpaper("l3");
                                            Toast.makeText(ViewWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewWallpaper.this, ChatWithFriends.class);
                                            intent.putExtra("Value",value);
                                            intent.putExtra("Image",image);
                                            intent.putExtra("UserId",userId);
                                            intent.putExtra("ChatBackground",backgroundWall);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ViewWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    Toast.makeText(ViewWallpaper.this, "Wallpaper did not updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }else if (wallpaper.contentEquals("l4")){
                imageView.setImageResource(R.drawable.l4);
                String s4 = "l4";
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                        updateWallpaper.put("chatBackgroundWall",s4);
                        userDetailsRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                            String backgroundWall = userDetails.getChatBackgroundWall();
                                            sharedPrefManager.saveWallpaper("l4");
                                            Toast.makeText(ViewWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewWallpaper.this, ChatWithFriends.class);
                                            intent.putExtra("Value",value);
                                            intent.putExtra("Image",image);
                                            intent.putExtra("UserId",userId);
                                            intent.putExtra("ChatBackground",backgroundWall);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ViewWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    Toast.makeText(ViewWallpaper.this, "Wallpaper did not updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }else if (wallpaper.contentEquals("l5")){
                imageView.setImageResource(R.drawable.l5);
                String s5 = "l5";
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                        updateWallpaper.put("chatBackgroundWall",s5);
                        userDetailsRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                            String backgroundWall = userDetails.getChatBackgroundWall();
                                            sharedPrefManager.saveWallpaper("l5");
                                            Toast.makeText(ViewWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewWallpaper.this, ChatWithFriends.class);
                                            intent.putExtra("Value",value);
                                            intent.putExtra("Image",image);
                                            intent.putExtra("UserId",userId);
                                            intent.putExtra("ChatBackground",backgroundWall);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ViewWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    Toast.makeText(ViewWallpaper.this, "Wallpaper did not updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }else if (wallpaper.contentEquals("l6")){
                imageView.setImageResource(R.drawable.l6);
                String s6 = "l6";
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                        updateWallpaper.put("chatBackgroundWall",s6);
                        userDetailsRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                            String backgroundWall = userDetails.getChatBackgroundWall();
                                            sharedPrefManager.saveWallpaper("l6");
                                            Toast.makeText(ViewWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewWallpaper.this, ChatWithFriends.class);
                                            intent.putExtra("Value",value);
                                            intent.putExtra("Image",image);
                                            intent.putExtra("UserId",userId);
                                            intent.putExtra("ChatBackground",backgroundWall);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ViewWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    Toast.makeText(ViewWallpaper.this, "Wallpaper did not updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }else {
                imageView.setImageResource(R.drawable.l7);
                String s7 = "l7";
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                        updateWallpaper.put("chatBackgroundWall",s7);
                        userDetailsRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                            String backgroundWall = userDetails.getChatBackgroundWall();
                                            sharedPrefManager.saveWallpaper("l7");
                                            Toast.makeText(ViewWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewWallpaper.this, ChatWithFriends.class);
                                            intent.putExtra("Value",value);
                                            intent.putExtra("Image",image);
                                            intent.putExtra("UserId",userId);
                                            intent.putExtra("ChatBackground",backgroundWall);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ViewWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    Toast.makeText(ViewWallpaper.this, "Wallpaper did not updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }else {
            if (wallpaper.contentEquals("d1")){
                imageView.setImageResource(R.drawable.d1);
                String d1 = "d1";
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                        updateWallpaper.put("chatBackgroundWall",d1);
                        userDetailsRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                            String backgroundWall = userDetails.getChatBackgroundWall();
                                            sharedPrefManager.saveWallpaper("d1");
                                            Toast.makeText(ViewWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewWallpaper.this, ChatWithFriends.class);
                                            intent.putExtra("Value",value);
                                            intent.putExtra("Image",image);
                                            intent.putExtra("UserId",userId);
                                            intent.putExtra("ChatBackground",backgroundWall);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ViewWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    Toast.makeText(ViewWallpaper.this, "Wallpaper did not updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }else if (wallpaper.contentEquals("d2")){
                imageView.setImageResource(R.drawable.d2);
                String d2 = "d2";
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                        updateWallpaper.put("chatBackgroundWall",d2);
                        userDetailsRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                            String backgroundWall = userDetails.getChatBackgroundWall();
                                            sharedPrefManager.saveWallpaper("d2");
                                            Toast.makeText(ViewWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewWallpaper.this, ChatWithFriends.class);
                                            intent.putExtra("Value",value);
                                            intent.putExtra("Image",image);
                                            intent.putExtra("UserId",userId);
                                            intent.putExtra("ChatBackground",backgroundWall);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ViewWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    Toast.makeText(ViewWallpaper.this, "Wallpaper did not updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }else if (wallpaper.contentEquals("d3")){
                imageView.setImageResource(R.drawable.d3);
                String d3 = "d3";
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                        updateWallpaper.put("chatBackgroundWall",d3);
                        userDetailsRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                            String backgroundWall = userDetails.getChatBackgroundWall();
                                            sharedPrefManager.saveWallpaper("d3");
                                            Toast.makeText(ViewWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewWallpaper.this, ChatWithFriends.class);
                                            intent.putExtra("Value",value);
                                            intent.putExtra("Image",image);
                                            intent.putExtra("UserId",userId);
                                            intent.putExtra("ChatBackground",backgroundWall);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ViewWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    Toast.makeText(ViewWallpaper.this, "Wallpaper did not updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }else if (wallpaper.contentEquals("d4")){
                imageView.setImageResource(R.drawable.d4);
                String d4 = "d4";
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                        updateWallpaper.put("chatBackgroundWall",d4);
                        userDetailsRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                            String backgroundWall = userDetails.getChatBackgroundWall();
                                            sharedPrefManager.saveWallpaper("d4");
                                            Toast.makeText(ViewWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewWallpaper.this, ChatWithFriends.class);
                                            intent.putExtra("Value",value);
                                            intent.putExtra("Image",image);
                                            intent.putExtra("UserId",userId);
                                            intent.putExtra("ChatBackground",backgroundWall);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ViewWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    Toast.makeText(ViewWallpaper.this, "Wallpaper did not updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }else if (wallpaper.contentEquals("d5")){
                imageView.setImageResource(R.drawable.d5);
                String d5 = "d5";
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                        updateWallpaper.put("chatBackgroundWall",d5);
                        userDetailsRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                            String backgroundWall = userDetails.getChatBackgroundWall();
                                            sharedPrefManager.saveWallpaper("d5");
                                            Toast.makeText(ViewWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewWallpaper.this, ChatWithFriends.class);
                                            intent.putExtra("Value",value);
                                            intent.putExtra("Image",image);
                                            intent.putExtra("UserId",userId);
                                            intent.putExtra("ChatBackground",backgroundWall);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ViewWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    Toast.makeText(ViewWallpaper.this, "Wallpaper did not updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }else if (wallpaper.contentEquals("d6")){
                imageView.setImageResource(R.drawable.d6);
                String d6 = "d6";
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                        updateWallpaper.put("chatBackgroundWall",d6);
                        userDetailsRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                            String backgroundWall = userDetails.getChatBackgroundWall();
                                            sharedPrefManager.saveWallpaper("d6");
                                            Toast.makeText(ViewWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewWallpaper.this, ChatWithFriends.class);
                                            intent.putExtra("Value",value);
                                            intent.putExtra("Image",image);
                                            intent.putExtra("UserId",userId);
                                            intent.putExtra("ChatBackground",backgroundWall);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ViewWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    Toast.makeText(ViewWallpaper.this, "Wallpaper did not updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }else {
                imageView.setImageResource(R.drawable.d7);
                String d7 = "d7";
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String,Object> updateWallpaper = new HashMap<String, Object>();
                        updateWallpaper.put("chatBackgroundWall",d7);
                        userDetailsRef.updateChildren(updateWallpaper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                                            String backgroundWall = userDetails.getChatBackgroundWall();
                                            sharedPrefManager.saveWallpaper("d7");
                                            Toast.makeText(ViewWallpaper.this, "Wallpaper set", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewWallpaper.this, ChatWithFriends.class);
                                            intent.putExtra("Value",value);
                                            intent.putExtra("Image",image);
                                            intent.putExtra("UserId",userId);
                                            intent.putExtra("ChatBackground",backgroundWall);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ViewWallpaper.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    Toast.makeText(ViewWallpaper.this, "Wallpaper did not updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (transfer.contentEquals("def")){
            Intent intent = new Intent(ViewWallpaper.this,SelectWallpaper.class);
            intent.putExtra("Value",value);
            intent.putExtra("Image",image);
            intent.putExtra("UserId",userId);
            intent.putExtra("ChatBackground",chatWallpaper);
            startActivity(intent);
            finish();
        }else if (transfer.contentEquals("lig")){
            Intent intent = new Intent(ViewWallpaper.this,LightWallpaper.class);
            intent.putExtra("Value",value);
            intent.putExtra("Image",image);
            intent.putExtra("UserId",userId);
            intent.putExtra("ChatBackground",chatWallpaper);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(ViewWallpaper.this,DarkWallpaper.class);
            intent.putExtra("Value",value);
            intent.putExtra("Image",image);
            intent.putExtra("UserId",userId);
            intent.putExtra("ChatBackground",chatWallpaper);
            startActivity(intent);
            finish();
        }
    }
}