package com.macode.paynothing.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.macode.paynothing.R;
import com.macode.paynothing.activities.OtherItemDetailActivity;
import com.macode.paynothing.utilities.Items;
import com.macode.paynothing.utilities.ItemsViewHolder;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private Toolbar toolbar;
    private String refactoredItemKey;
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

        toolbar = view.findViewById(R.id.homeToolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Home");
        setHasOptionsMenu(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        itemReference = FirebaseDatabase.getInstance().getReference().child("Items");
        itemFeedRecyclerView = view.findViewById(R.id.itemFeedRecyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireActivity(), 3);
        itemFeedRecyclerView.setLayoutManager(layoutManager);

        loadItems("");

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.top_main_menu, menu);

        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        };
        menu.findItem(R.id.search).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search Pay Nothing...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadItems(newText);
                return false;
            }
        });
    }

    private void loadItems(String string) {
        Query query = itemReference.orderByChild("title").startAt(string).endAt(string + "\uf8ff");
        itemsOptions = new FirebaseRecyclerOptions.Builder<Items>().setQuery(query, Items.class).build();
        itemsAdapter = new FirebaseRecyclerAdapter<Items, ItemsViewHolder>(itemsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ItemsViewHolder holder, int position, @NonNull Items model) {
                final String itemKey = getRef(position).getKey();
                refactoredItemKey = itemKey.substring(0, itemKey.indexOf(" "));
                if (!firebaseUser.getUid().equals(refactoredItemKey) && !model.getSold().equals(true)) {
                    Picasso.get().load(model.getImageUrl()).into(holder.itemImageView);

                    holder.itemImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(requireActivity(), OtherItemDetailActivity.class);
                            intent.putExtra("itemKey", itemKey);
                            startActivity(intent);
                        }
                    });
                } else {
                    holder.itemImageView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
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