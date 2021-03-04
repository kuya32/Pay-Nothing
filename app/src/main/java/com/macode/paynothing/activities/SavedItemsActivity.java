package com.macode.paynothing.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.macode.paynothing.R;
import com.macode.paynothing.utilities.SavedItems;
import com.macode.paynothing.utilities.SavedItemsViewHolder;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SavedItemsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference savedItemReference, itemReference;
    private FirebaseRecyclerAdapter<SavedItems, SavedItemsViewHolder> savedItemsAdapter;
    private FirebaseRecyclerOptions<SavedItems> savedItemsOptions;
    private RecyclerView savedItemRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_items);

        toolbar = findViewById(R.id.savedItemsToolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        savedItemReference = FirebaseDatabase.getInstance().getReference().child("SavedItems");
        itemReference = FirebaseDatabase.getInstance().getReference().child("Items");
        savedItemRecyclerView = findViewById(R.id.savedItemsRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SavedItemsActivity.this);
        savedItemRecyclerView.setLayoutManager(layoutManager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Saved Items");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadSavedItems("");
    }

    private void loadSavedItems(String string) {
        Query query = savedItemReference.child(firebaseUser.getUid()).orderByChild("title").startAt(string).endAt(string + "\uf8ff");
        savedItemsOptions = new FirebaseRecyclerOptions.Builder<SavedItems>().setQuery(query, SavedItems.class).build();
        savedItemsAdapter = new FirebaseRecyclerAdapter<SavedItems, SavedItemsViewHolder>(savedItemsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull SavedItemsViewHolder holder, int position, @NonNull SavedItems model) {
                final String itemKey = getRef(position).getKey();
                Picasso.get().load(model.getImageUrl()).into(holder.savedItemImageView);
                holder.savedItemTitle.setText(model.getTitle());
                String timeAgo = calculateTimeAgo(model.getDateItemSaved());
                holder.dateSavedItem.setText(String.format("Item saved %s", timeAgo));

                itemReference.child(itemKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.child("sold").getValue().equals(true)) {
                                holder.savedItemSoldRelativeLayout.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(SavedItemsActivity.this, "Sorry, could not retrieve item data from firebase!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SavedItemsActivity.this, "" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                holder.savedItemCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SavedItemsActivity.this, OtherItemDetailActivity.class);
                        intent.putExtra("itemKey", itemKey);
                        startActivity(intent);
                    }
                });

                holder.deleteSavedItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        savedItemReference.child(firebaseUser.getUid()).child(itemKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SavedItemsActivity.this, "Item has been unsaved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SavedItemsActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public SavedItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_saved_item, parent, false);
                return new SavedItemsViewHolder(view);
            }
        };
        savedItemsAdapter.startListening();
        savedItemRecyclerView.setAdapter(savedItemsAdapter);
    }

    private String calculateTimeAgo(String datePost) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        try {
            long time = simpleDateFormat.parse(datePost).getTime();
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
}