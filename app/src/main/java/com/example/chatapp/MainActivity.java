package com.example.chatapp;





import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;

import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity{

    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    UserAdpter  adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    ImageView imglogout;
    ImageView cumbut,setbut;
    long timestamp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        database=FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        cumbut = findViewById(R.id.camBut);
        setbut = findViewById(R.id.settingBut);

        DatabaseReference reference = database.getReference().child("user");
        DatabaseReference reference2 = database.getReference().child("chats");

        usersArrayList = new ArrayList<>();

        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdpter(MainActivity.this,usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        imglogout = findViewById(R.id.logoutimg);

        imglogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this,R.style.dialoge);
                dialog.setContentView(R.layout.dialog_layout);
                Button no,yes;
                yes = dialog.findViewById(R.id.yesbnt);
                no = dialog.findViewById(R.id.nobnt);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this,login.class);
                        startActivity(intent);
                        finish();
                    }
                });
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        setbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, setting.class);
                startActivity(intent);
            }
        });

        cumbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,10);
            }
        });

        if (auth.getCurrentUser() == null){
            Intent intent = new Intent(MainActivity.this,login.class);
            startActivity(intent);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.POST_NOTIFICATIONS},101);}}
        // Check for recent messages
        fetchUserNamesAndCheckForRecentMessages();

    }
    private void fetchUserNamesAndCheckForRecentMessages() {
        DatabaseReference usersReference = database.getReference().child("user");
        DatabaseReference chatsReference = database.getReference().child("chats");
        long oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000); // One hour ago in milliseconds

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> userIdToNameMap = new HashMap<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    String userName = userSnapshot.child("userName").getValue(String.class);
                    userIdToNameMap.put(userId, userName);
                }

                checkForRecentMessages(chatsReference, userIdToNameMap, oneHourAgo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println("Error fetching user names: " + databaseError.getMessage());
            }
        });
    }


    private void showRecentMessageNotification(String messageContent) {
        String channelID = "CHANNEL_ID_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelID);
        builder.setSmallIcon(R.drawable.notifications);
        builder.setContentTitle("Recent Messages");
        builder.setContentText(messageContent);
        builder.setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", messageContent);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelID, "Channel for recent messages", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            int notificationId = (int) System.currentTimeMillis(); // Unique ID for each notification
            notificationManager.notify(notificationId, builder.build());

        }
    }
    private void checkForRecentMessages(DatabaseReference chatsReference, HashMap<String, String> userIdToNameMap, long oneHourAgo) {
        chatsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashSet<String> recentSenders = new HashSet<>();

                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot messagesSnapshot = chatSnapshot.child("messages");
                    for (DataSnapshot messageSnapshot : messagesSnapshot.getChildren()) {
                        msgModelclass message = messageSnapshot.getValue(msgModelclass.class);
                        if (message != null && message.getTimeStamp() >= oneHourAgo && !Objects.equals(message.getSenderid(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            String senderName = userIdToNameMap.getOrDefault(message.getSenderid(), "Unknown User");
                            recentSenders.add(senderName);
                        }
                    }
                }

                if (!recentSenders.isEmpty()) {
                    String senderNames = String.join(", ", recentSenders) + "'s messages are recent in the last hour.";
                    showRecentMessageNotification(senderNames);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.err.println("Error checking messages: " + databaseError.getMessage());
            }
        });
    }





}