package com.macode.paynothing.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import com.macode.paynothing.ChatActivity;
import com.macode.paynothing.R;
import com.macode.paynothing.activities.OtherItemDetailActivity;
import com.macode.paynothing.utilities.InboxChats;
import com.macode.paynothing.utilities.InboxChatsViewHolder;
import com.squareup.picasso.Picasso;

import java.util.zip.Inflater;

public class SellingMessageFragment extends Fragment {

    private RecyclerView recyclerView;
    private String sellersId, sellerProfileImage, sellerUsername, sellerStatus, mostRecentMessage, itemKey, itemImage;
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
        Query query = inboxChatReference.orderByChild("dateOfMostRecentMessage");
        inboxChatsOptions = new FirebaseRecyclerOptions.Builder<InboxChats>().setQuery(query, InboxChats.class).build();
        inboxChatsAdapter = new FirebaseRecyclerAdapter<InboxChats, InboxChatsViewHolder>(inboxChatsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull InboxChatsViewHolder holder, int position, @NonNull InboxChats model) {
                final String inboxChatKey = getRef(position).getKey();
                sellersId = model.getSellerId();
                itemKey = model.getItemKey();
                System.out.println(model.getSellerId() + " HEllo");
                System.out.println(itemKey);
                holder.sellerBuyerCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println(getRef(position));
                    }
                });
//                if (firebaseUser.getUid().equals(model.getSellerId())) {
//                    userReference.child(sellersId).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.exists()) {
//                                sellerProfileImage = snapshot.child("profileImage").getValue().toString();
//                                sellerUsername = snapshot.child("username").getValue().toString();
//                                sellerStatus = snapshot.child("status").getValue().toString();
//
//                                Picasso.get().load(sellerProfileImage).into(holder.sellerBuyerProfileImage);
//                                holder.sellerBuyerUsername.setText(sellerUsername);
//                                if (sellerStatus.equals("Online")) {
//                                    holder.sellerBuyerStatusImage.setColorFilter(Color.GREEN);
//                                }
//                                holder.sellerBuyerStatus.setText(sellerStatus);
//                                itemReference.child(itemKey).addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        if (snapshot.exists()) {
//                                            itemImage = snapshot.child("imageUrl").getValue().toString();
//
//                                            Picasso.get().load(itemImage).into(holder.itemImage);
//
//                                        } else {
//                                            Toast.makeText(requireActivity(), "Sorry, could not get item data from firebase!", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//                                        Toast.makeText(requireActivity(), "" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            } else {
//                                Toast.makeText(requireActivity(), "Sorry, could not get seller's data from firebase!", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Toast.makeText(requireActivity(), "" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    holder.sellerBuyerCardView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(requireActivity(), ChatActivity.class);
//                            intent.putExtra("sellersId", sellersId);
//                            intent.putExtra("itemKey", itemKey);
//                            startActivity(intent);
//                        }
//                    });
//                } else {
//                    holder.sellerBuyerCardView.setVisibility(View.GONE);
//                }
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