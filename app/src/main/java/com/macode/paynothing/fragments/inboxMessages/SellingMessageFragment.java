package com.macode.paynothing.fragments.inboxMessages;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.macode.paynothing.activities.ChatActivity;
import com.macode.paynothing.R;
import com.macode.paynothing.utilities.InboxChats;
import com.macode.paynothing.utilities.InboxChatsViewHolder;
import com.squareup.picasso.Picasso;

public class SellingMessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private String buyerProfileImage, buyerUsername, buyerStatus, mostRecentMessage, itemKey, itemImage;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference itemReference, userReference, inboxChatReference, messageReference;
    private FirebaseRecyclerOptions<InboxChats> inboxChatsOptions;
    private FirebaseRecyclerAdapter<InboxChats, InboxChatsViewHolder> inboxChatsAdapter;

    public SellingMessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selling_message, container, false);
        recyclerView = view.findViewById(R.id.sellingMessageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        itemReference = FirebaseDatabase.getInstance().getReference().child("Items");
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        inboxChatReference = FirebaseDatabase.getInstance().getReference().child("InboxChats");
        messageReference = FirebaseDatabase.getInstance().getReference().child("Messages");

        loadInboxChats();

        return view;
    }

    private void loadInboxChats() {
        Query query = inboxChatReference.child(firebaseUser.getUid());
        inboxChatsOptions = new FirebaseRecyclerOptions.Builder<InboxChats>().setQuery(query, InboxChats.class).build();
        inboxChatsAdapter = new FirebaseRecyclerAdapter<InboxChats, InboxChatsViewHolder>(inboxChatsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull InboxChatsViewHolder holder, int position, @NonNull InboxChats model) {
                if (firebaseUser.getUid().equals(model.getSellerId()) && !model.getMostRecentMessage().equals("No recent messages")) {
                    if (model.getMostRecentMessage().length() > 25) {
                        String cutMessage = model.getMostRecentMessage().substring(0, 25);
                        holder.mostRecentMessage.setText(String.format("%s...", cutMessage));
                    } else {
                        holder.mostRecentMessage.setText(model.getMostRecentMessage());
                    }
                    userReference.child(model.getBuyerId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                buyerProfileImage = snapshot.child("profileImage").getValue().toString();
                                buyerUsername = snapshot.child("username").getValue().toString();
                                buyerStatus = snapshot.child("status").getValue().toString();
                                if (buyerStatus.equals("Online")) {
                                    holder.sellerBuyerStatusImage.setColorFilter(Color.GREEN);
                                }

                                Picasso.get().load(buyerProfileImage).into(holder.sellerBuyerProfileImage);
                                holder.sellerBuyerUsername.setText(buyerUsername);
                                holder.sellerBuyerStatus.setText(buyerStatus);

                                itemReference.child(model.getItemKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            itemImage = snapshot.child("imageUrl").getValue().toString();

                                            Picasso.get().load(itemImage).into(holder.itemImage);
                                        } else {
                                            Toast.makeText(requireActivity(), "Sorry, could not grab item's image from firebase!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(requireActivity(), "" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(requireActivity(), "Sorry, could not grab buyer's info from firebase!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(requireActivity(), "" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    holder.sellerBuyerCardView.setVisibility(View.GONE);
                }
                holder.sellerBuyerCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(requireActivity(), ChatActivity.class);
                        intent.putExtra("sellersId", model.getBuyerId());
                        intent.putExtra("itemKey", model.getItemKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public InboxChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_inbox_message, parent, false);
                return new InboxChatsViewHolder(view);
            }
        };
        inboxChatsAdapter.startListening();
        recyclerView.setAdapter(inboxChatsAdapter);
        recyclerView.setHasFixedSize(true);
    }
}