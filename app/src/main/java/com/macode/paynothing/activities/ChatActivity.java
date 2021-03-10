package com.macode.paynothing.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.macode.paynothing.R;
import com.macode.paynothing.notifications.APIService;
import com.macode.paynothing.notifications.Client;
import com.macode.paynothing.notifications.Data;
import com.macode.paynothing.notifications.MyResponse;
import com.macode.paynothing.notifications.Sender;
import com.macode.paynothing.notifications.Token;
import com.macode.paynothing.utilities.Chats;
import com.macode.paynothing.utilities.ChatsViewHolder;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView sellersProfileImage;
    private TextView sellersUsername, sellersLocation, sellersStatus;
    private Boolean seen = false, notify = false;
    private EditText messageInput;
    private ImageView itemImage, addImageButton, sendMessageButton, sellersStatusImage;
    private RecyclerView chatRecyclerView;
    private String sellersId, itemKey, itemTitle, sellersUsernameString, sellersProfileImageString, sellersLocationString, sellersStatusString, chatMessageString, userProfileImageString, usernameString, stringDate, timeAgo, itemImageString, senderUsername;
    private DatabaseReference userReference, itemReference, messageReference, inboxChatReference, tokenReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseRecyclerOptions<Chats> chatOptions;
    private FirebaseRecyclerAdapter<Chats, ChatsViewHolder> chatAdapter;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.chatToolbar);
        sellersProfileImage = findViewById(R.id.chatSellersImageView);
        sellersUsername = findViewById(R.id.chatSellersUsername);
        sellersLocation = findViewById(R.id.chatSellersLocation);
        sellersStatus = findViewById(R.id.chatStatus);
        messageInput = findViewById(R.id.chatMessageInput);
        itemImage = findViewById(R.id.chatItemImage);
        addImageButton = findViewById(R.id.chatAddImageButton);
        sendMessageButton = findViewById(R.id.chatSendMessageButton);
        sellersStatusImage = findViewById(R.id.chatStatusImage);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        itemReference = FirebaseDatabase.getInstance().getReference().child("Items");
        messageReference = FirebaseDatabase.getInstance().getReference().child("Messages");
        inboxChatReference = FirebaseDatabase.getInstance().getReference().child("InboxChats");
        tokenReference = FirebaseDatabase.getInstance().getReference().child("Tokens");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sellersId = getIntent().getStringExtra("sellersId");
        itemKey = getIntent().getStringExtra("itemKey");

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        loadUserData();
        loadSellerData();
        loadItemData();
        loadMessages();

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void updateToken(String token) {
        Token token1 = new Token(token);
        tokenReference.child(firebaseUser.getUid()).setValue(token1);
    }


    private void loadUserData() {
        userReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userProfileImageString = snapshot.child("profileImage").getValue().toString();
                    usernameString = snapshot.child("username").getValue().toString();
                } else {
                    Toast.makeText(ChatActivity.this, "Sorry, we could not your account info from firebase!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSellerData() {
        userReference.child(sellersId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    sellersProfileImageString = snapshot.child("profileImage").getValue().toString();
                    sellersUsernameString = snapshot.child("username").getValue().toString();
                    getSupportActionBar().setTitle(String.format("%s's Item Chat", sellersUsernameString));
                    sellersLocationString = snapshot.child("location").getValue().toString();
                    sellersStatusString = snapshot.child("status").getValue().toString();

                    Picasso.get().load(sellersProfileImageString).into(sellersProfileImage);
                    sellersUsername.setText(sellersUsernameString);
                    sellersLocation.setText(sellersLocationString);
                    sellersStatus.setText(sellersStatusString);
                    if (sellersStatusString.equals("Online")) {
                        sellersStatusImage.setColorFilter(Color.GREEN);
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Sorry, we could not seller's account info from firebase!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadItemData() {
        itemReference.child(itemKey).child("imageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    itemImageString = snapshot.getValue().toString();

                    Picasso.get().load(itemImageString).into(itemImage);
                } else {
                    Toast.makeText(ChatActivity.this, "Could not load item image from firebase!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessages() {
        chatOptions = new FirebaseRecyclerOptions.Builder<Chats>().setQuery(messageReference.child(firebaseUser.getUid()).child(sellersId).child(itemKey), Chats.class).build();
        chatAdapter = new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(chatOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ChatsViewHolder holder, int position, @NonNull Chats model) {
                if (model.getUserId().equals(firebaseUser.getUid())) {
                    holder.sellersMessage.setVisibility(View.GONE);
                    holder.sellersProfileImage.setVisibility(View.GONE);
                    holder.sellersMessageTimeAgo.setVisibility(View.GONE);
                    holder.usersMessage.setVisibility(View.VISIBLE);
                    holder.usersProfileImage.setVisibility(View.VISIBLE);
                    holder.usersMessageTimeAgo.setVisibility(View.VISIBLE);

                    Picasso.get().load(userProfileImageString).into(holder.usersProfileImage);
                    holder.usersMessage.setText(model.getMessage());
                    timeAgo = calculateTimeAgo(model.getDateMessageSent());
                    if (timeAgo.equals("0 minutes ago")) {
                        timeAgo = "seconds ago";
                    }
                    holder.usersMessageTimeAgo.setText(timeAgo);
                } else {
                    holder.sellersMessage.setVisibility(View.VISIBLE);
                    holder.sellersProfileImage.setVisibility(View.VISIBLE);
                    holder.sellersMessageTimeAgo.setVisibility(View.VISIBLE);
                    holder.usersMessage.setVisibility(View.GONE);
                    holder.usersProfileImage.setVisibility(View.GONE);
                    holder.usersMessageTimeAgo.setVisibility(View.GONE);

                    Picasso.get().load(sellersProfileImageString).into(holder.sellersProfileImage);
                    holder.sellersMessage.setText(model.getMessage());
                    timeAgo = calculateTimeAgo(model.getDateMessageSent());
                    if (timeAgo.equals("0 minutes ago")) {
                        timeAgo = "seconds ago";
                    }
                    holder.sellersMessageTimeAgo.setText(timeAgo);

                }
            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_message, parent, false);
                return new ChatsViewHolder(view);
            }
        };
        chatAdapter.startListening();
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setHasFixedSize(true);
    }

    private void sendMessage() {
        notify = true;
        chatMessageString = messageInput.getText().toString();
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.getDefault());
        stringDate = format.format(date);
        if (chatMessageString.isEmpty()) {
            showError(messageInput, "Please write a message!");
        } else {
            final HashMap hashMap = new HashMap();
            hashMap.put("message", chatMessageString);
            hashMap.put("dateMessageSent", stringDate);
            hashMap.put("seen", seen);
            hashMap.put("userId", firebaseUser.getUid());
            messageReference.child(firebaseUser.getUid()).child(sellersId).child(itemKey).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        messageReference.child(sellersId).child(firebaseUser.getUid()).child(itemKey).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    HashMap inboxChatUsersHashMap = new HashMap();
                                    inboxChatUsersHashMap.put("dateOfMostRecentMessage", stringDate);
                                    inboxChatUsersHashMap.put("mostRecentMessage", String.format("%s: %s", usernameString, chatMessageString));
                                    inboxChatReference.child(firebaseUser.getUid()).child(itemKey).updateChildren(inboxChatUsersHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                HashMap inboxChatSellersHashMap = new HashMap();
                                                inboxChatSellersHashMap.put("dateOfMostRecentMessage", stringDate);
                                                inboxChatSellersHashMap.put("mostRecentMessage", String.format("%s: %s", usernameString, chatMessageString));
                                                inboxChatReference.child(sellersId).child(itemKey).updateChildren(inboxChatSellersHashMap).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        if (task.isSuccessful()) {
                                                            userReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()) {
                                                                        usernameString = snapshot.child("username").getValue().toString();
                                                                        if (notify) {
                                                                            sendMessageNotification(sellersId, usernameString, chatMessageString);
                                                                        }
                                                                        notify = false;
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                            messageInput.setText(null);
                                                            Toast.makeText(ChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(ChatActivity.this, "Date of most recent message could not be updated!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(ChatActivity.this, "Date of most recent message could not be updated!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(ChatActivity.this, "Message could not be sent!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(ChatActivity.this, "Message could not be sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendMessageNotification(String sellersId, String usernameString, String chatMessageString) {
        Query query = tokenReference.orderByKey().equalTo(sellersId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Token token = snapshot1.getValue(Token.class);
                        Data data = new Data(firebaseUser.getUid(), String.format("%s: %s", usernameString, chatMessageString), "New Message", sellersId, R.drawable.pay_nothing_logo);

                        Sender sender = new Sender(data, token.getToken());

                        apiService.sendNotification(sender)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200) {
                                            if (response.body().success != 1) {
                                                Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void messageNotification(String chatMessageString) {
//        userReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    usernameString = snapshot.child("username").getValue().toString();
//                    NotificationCompat.Builder builder = new NotificationCompat.Builder(ChatActivity.this, "message")
//                            .setSmallIcon(R.drawable.pay_nothing_logo)
//                            .setContentTitle("New message")
//                            .setContentText(String.format("%s: %s", usernameString, chatMessageString))
//                            .setStyle(new NotificationCompat.BigTextStyle())
//                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                            .setAutoCancel(true);
//
//                    Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("sellersId", sellersId);
//                    intent.putExtra("itemKey", itemKey);
//
//                    PendingIntent pendingIntent = PendingIntent.getActivity(ChatActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    builder.setContentIntent(pendingIntent);
//
//                    notificationManager.notify(0, builder.build());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private String calculateTimeAgo(String dateSent) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.getDefault());
        try {
            long time = simpleDateFormat.parse(dateSent).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago + "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showError(EditText layout, String text) {
        layout.setError(text);
        layout.requestFocus();
    }
}