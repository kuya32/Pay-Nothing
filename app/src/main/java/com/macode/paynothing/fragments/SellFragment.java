package com.macode.paynothing.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.macode.paynothing.EditItemActivity;
import com.macode.paynothing.MainActivity;
import com.macode.paynothing.PostActivity;
import com.macode.paynothing.R;
import com.macode.paynothing.activities.OtherItemDetailActivity;
import com.macode.paynothing.utilities.Items;
import com.macode.paynothing.utilities.SellItemsViewHolder;
import com.squareup.picasso.Picasso;

public class SellFragment extends Fragment {

    private Toolbar toolbar;
    private ImageView postItemImage;
    private TextView postItem;
    private RelativeLayout warningRelativeLayout;
    private CardView markedAsSoldCardView, deleteItemCardView;
    private Button markedAsSoldCancelButton, markedAsSoldButton, deleteItemCancelButton, deleteItemButton;
    private String refactoredItemKey, currentItemKey;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference itemReference;
    private FirebaseRecyclerAdapter<Items, SellItemsViewHolder> sellItemsAdapter;
    private FirebaseRecyclerOptions<Items> sellItemsOptions;
    private RecyclerView sellItemsRecyclerView;

    public SellFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell, container, false);

        toolbar = view.findViewById(R.id.sellingToolbar);
        postItemImage = view.findViewById(R.id.sellingPostItemImage);
        postItem = view.findViewById(R.id.sellingTitleText);
        warningRelativeLayout = view.findViewById(R.id.warningMessageRelativeLayout);
        markedAsSoldCardView = view.findViewById(R.id.markedAsSoldWarningCardView);
        deleteItemCardView = view.findViewById(R.id.deleteItemWarningCardView);
        markedAsSoldCancelButton = view.findViewById(R.id.markedAsSoldCancelButton);
        markedAsSoldButton = view.findViewById(R.id.markedAsSoldMarkSoldButton);
        deleteItemCancelButton = view.findViewById(R.id.deleteItemCancelButton);
        deleteItemButton = view.findViewById(R.id.deleteItemButton);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        itemReference = FirebaseDatabase.getInstance().getReference().child("Items");
        sellItemsRecyclerView = view.findViewById(R.id.sellingRecyclerView);
        sellItemsRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Selling");

        postItemImage.getDrawable().setColorFilter(Color.rgb(245, 192, 20), PorterDuff.Mode.MULTIPLY);

        postItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), PostActivity.class));
            }
        });

        postItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), PostActivity.class));
            }
        });

        markedAsSoldCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markedAsSoldCardView.setVisibility(View.GONE);
                warningRelativeLayout.setVisibility(View.GONE);
            }
        });

        markedAsSoldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemReference.child(currentItemKey).child("sold").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        markedAsSoldCardView.setVisibility(View.GONE);
                        warningRelativeLayout.setVisibility(View.GONE);
                    }
                });
            }
        });

        deleteItemCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItemCardView.setVisibility(View.GONE);
                warningRelativeLayout.setVisibility(View.GONE);
            }
        });

        deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemReference.child(currentItemKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        deleteItemCardView.setVisibility(View.GONE);
                        warningRelativeLayout.setVisibility(View.GONE);
                    }
                });
            }
        });

        loadSellingItems();

        return view;
    }

    private void loadSellingItems() {
        Query query = itemReference.orderByChild("dateItemPosted");
        sellItemsOptions = new FirebaseRecyclerOptions.Builder<Items>().setQuery(query, Items.class).build();
        sellItemsAdapter = new FirebaseRecyclerAdapter<Items, SellItemsViewHolder>(sellItemsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull SellItemsViewHolder holder, int position, @NonNull Items model) {
                final String itemKey = getRef(position).getKey();
                refactoredItemKey = itemKey.substring(0, itemKey.indexOf(" "));
                if (firebaseUser.getUid().equals(refactoredItemKey)) {
                    Picasso.get().load(model.getImageUrl()).into(holder.itemImageView);
                    holder.itemTitle.setText(model.getTitle());

                    if (model.getSold().equals(true)) {
                        holder.markAsSoldButton.setVisibility(View.GONE);
                        holder.soldButton.setVisibility(View.VISIBLE);
                    }

                    holder.markAsSoldButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            warningRelativeLayout.setVisibility(View.VISIBLE);
                            markedAsSoldCardView.setVisibility(View.VISIBLE);
                            currentItemKey = itemKey;
                        }
                    });

                    holder.editItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(requireActivity(), EditItemActivity.class);
                            intent.putExtra("itemKey", itemKey);
                            startActivity(intent);
                        }
                    });

                    holder.shareItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    holder.deleteItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            warningRelativeLayout.setVisibility(View.VISIBLE);
                            deleteItemCardView.setVisibility(View.VISIBLE);
                            currentItemKey = itemKey;
                        }
                    });
                } else {
                    holder.sellingItemCardView.setVisibility(View.GONE);
                }
            }

            @NonNull
            @Override
            public SellItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_selling_item, parent, false);
                return new SellItemsViewHolder(view);
            }
        };
        sellItemsAdapter.startListening();
        sellItemsRecyclerView.setAdapter(sellItemsAdapter);
        sellItemsRecyclerView.setHasFixedSize(true);
    }
}