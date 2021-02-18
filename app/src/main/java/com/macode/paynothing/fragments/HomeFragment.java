package com.macode.paynothing.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.macode.paynothing.MainActivity;
import com.macode.paynothing.R;
import com.macode.paynothing.activities.OtherItemDetailActivity;
import com.macode.paynothing.utilities.Items;
import com.macode.paynothing.utilities.ItemsViewHolder;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference itemReference;
    private FirebaseRecyclerAdapter<Items, ItemsViewHolder> itemsAdapter;
    private FirebaseRecyclerOptions<Items> itemsOptions;
    private RecyclerView itemFeedRecyclerView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        itemReference = FirebaseDatabase.getInstance().getReference().child("Items");
        itemFeedRecyclerView = view.findViewById(R.id.itemFeedRecyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireActivity(), 3);
        itemFeedRecyclerView.setLayoutManager(layoutManager);

        loadItems();

        return view;
    }

    private void loadItems() {
        Query query = itemReference.orderByChild("postItemDate");
        itemsOptions = new FirebaseRecyclerOptions.Builder<Items>().setQuery(query, Items.class).build();
        itemsAdapter = new FirebaseRecyclerAdapter<Items, ItemsViewHolder>(itemsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ItemsViewHolder holder, int position, @NonNull Items model) {
                final String itemKey = getRef(position).getKey();
                Picasso.get().load(model.getImageUrl()).into(holder.itemImageView);
                holder.itemImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(requireActivity(), OtherItemDetailActivity.class);
                        intent.putExtra("itemKey", itemKey);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_item, parent, false);
                return new ItemsViewHolder(view);
            }
        };
        itemsAdapter.startListening();
        itemFeedRecyclerView.setAdapter(itemsAdapter);
        itemFeedRecyclerView.setHasFixedSize(true);
    }
}