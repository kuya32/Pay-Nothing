package com.macode.paynothing.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.macode.paynothing.R;
import com.macode.paynothing.utilities.Items;
import com.macode.paynothing.utilities.ItemsViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView profileImage;
    private TextView userName, userDateJoined, userLocation;
    private String nameString, firstNameString, lastNameString, imageString, dateJoinedString, refactoredDateJoinedString, locationString, refactoredItemKey;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference, itemReference;
    private FirebaseRecyclerAdapter<Items, ItemsViewHolder> userItemsAdapter;
    private FirebaseRecyclerOptions<Items> userItemsOptions;
    private RecyclerView userItemsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

        toolbar = findViewById(R.id.publicProfileToolbar);
        profileImage = findViewById(R.id.publicProfileImage);
        userName = findViewById(R.id.publicProfileName);
        userDateJoined = findViewById(R.id.publicProfileDateJoined);
        userLocation = findViewById(R.id.publicProfileLocation);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        itemReference = FirebaseDatabase.getInstance().getReference().child("Items");
        userItemsRecyclerView = findViewById(R.id.publicProfileUsersItemsRecyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        userItemsRecyclerView.setLayoutManager(layoutManager);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadUserInformation();
        loadUserItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ((item.getItemId() == R.id.share)) {
            ApplicationInfo api = getApplicationContext().getApplicationInfo();
            String apkPath = api.sourceDir;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(apkPath)));
            startActivity(Intent.createChooser(intent, "ShareVia"));
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserInformation() {
        userReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    imageString = snapshot.child("profileImage").getValue().toString();
                    firstNameString = snapshot.child("firstName").getValue().toString();
                    lastNameString = snapshot.child("lastName").getValue().toString();
                    nameString = String.format("%s %s", firstNameString, lastNameString);
                    dateJoinedString = snapshot.child("dateUserCreated").getValue().toString();
                    refactoredDateJoinedString = changeNumberDateToWordedDate(dateJoinedString);
                    locationString = snapshot.child("location").getValue().toString();

                    Picasso.get().load(imageString).into(profileImage);
                    userName.setText(nameString);
                    userDateJoined.setText(String.format("Joined %s", refactoredDateJoinedString));
                    userLocation.setText(locationString);

                } else {
                    Toast.makeText(PublicProfileActivity.this, "Sorry, could not retrieve user information from firebase!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadUserItems() {
        Query query = itemReference.orderByChild("dateItemPosted");
        userItemsOptions = new FirebaseRecyclerOptions.Builder<Items>().setQuery(query, Items.class).build();
        userItemsAdapter = new FirebaseRecyclerAdapter<Items, ItemsViewHolder>(userItemsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ItemsViewHolder holder, int position, @NonNull Items model) {
                final String itemKey = getRef(position).getKey();
                refactoredItemKey = itemKey.substring(0, itemKey.indexOf(" "));
                if (firebaseUser.getUid().equals(refactoredItemKey)) {
                    Picasso.get().load(model.getImageUrl()).into(holder.itemImageView);
                    holder.itemImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(PublicProfileActivity.this, ItemDetailActivity.class);
                            intent.putExtra("itemKey", itemKey);
                            startActivity(intent);
                        }
                    });
                } else {
                    holder.itemImageView.setVisibility(View.GONE);
                }
            }

            @NonNull
            @Override
            public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_item, parent, false);
                return new ItemsViewHolder(view);
            }
        };
        userItemsAdapter.startListening();
        userItemsRecyclerView.setAdapter(userItemsAdapter);
        userItemsRecyclerView.setHasFixedSize(true);
    }

    private String changeNumberDateToWordedDate(String numberedDate) {
        String refactorNumberedDate;
        String month = "";
        String wordedMonth = "";
        String year = "";

        HashMap<String, String> months = new HashMap<>();
        months.put("1", "January");
        months.put("2", "February");
        months.put("3", "March");
        months.put("4", "April");
        months.put("5", "May");
        months.put("6", "June");
        months.put("7", "July");
        months.put("8", "August");
        months.put("9", "September");
        months.put("10", "October");
        months.put("11", "November");
        months.put("12", "December");

        refactorNumberedDate = numberedDate.substring(numberedDate.indexOf("-") + 1, numberedDate.indexOf(" "));
        month = refactorNumberedDate.substring(0, refactorNumberedDate.indexOf("-"));
        wordedMonth = months.get(month);
        year = refactorNumberedDate.substring(refactorNumberedDate.indexOf("-") + 1);

        return String.format("%s, %s", wordedMonth, year);
    }
}