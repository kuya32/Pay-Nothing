package com.macode.paynothing.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macode.paynothing.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar itemDetailToolBar;
    private CircleImageView itemDetailSellerImage;
    private ImageView itemDetailImage;
    private String itemKey, itemTitle, itemImage, itemCategory,  itemCondition, itemBrand, itemModel, itemType, itemDescription, itemLocation, itemPickUpOnly, itemLat, itemLong, itemSellerImageUrl, itemSellerUsername, loyaltyString;
    private TextView itemDetailTitle, itemDetailLocation, itemDetailCategory, itemDetailCondition, itemDetailPickUpOnly, itemDetailSellerUsername, itemDetailBrand, itemDetailModel, itemDetailType, itemDetailDescription, loyalty;
    private Boolean itemPickUp;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference, itemReference;
    private Button editItemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        itemDetailToolBar = findViewById(R.id.itemDetailToolbar);
        itemDetailImage = findViewById(R.id.itemDetailImageView);
        itemDetailTitle = findViewById(R.id.itemDetailTitle);
        itemDetailLocation = findViewById(R.id.itemDetailLocation);
        itemDetailCategory = findViewById(R.id.itemDetailCategory);
        itemDetailCondition = findViewById(R.id.itemDetailCondition);
        itemDetailPickUpOnly = findViewById(R.id.itemDetailPickUpOnly);
        itemDetailSellerImage = findViewById(R.id.itemDetailSellerImage);
        itemDetailSellerUsername = findViewById(R.id.itemDetailSellerName);
        loyalty = findViewById(R.id.loyalty);
        itemDetailBrand = findViewById(R.id.itemDetailBrand);
        itemDetailModel = findViewById(R.id.itemDetailModel);
        itemDetailType = findViewById(R.id.itemDetailType);
        itemDetailDescription = findViewById(R.id.itemDetailDescription);
        editItemButton = findViewById(R.id.itemDetailEditButton);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        itemReference = FirebaseDatabase.getInstance().getReference().child("Items");

        setSupportActionBar(itemDetailToolBar);
        getSupportActionBar().setTitle("Item Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.itemDetailGoogleMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Null Map Fragment", Toast.LENGTH_LONG).show();
        }

        retrieveExtraData();

        editItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendItemInfoToEdit();
            }
        });
    }

    private void sendItemInfoToEdit() {
        Intent intent = new Intent(ItemDetailActivity.this, EditItemActivity.class);
        intent.putExtra("itemKey", itemKey);
        startActivity(intent);
    }

    public void retrieveExtraData() {
        Intent intent = getIntent();
        itemKey = intent.getStringExtra("itemKey");
        retrieveItemDataFromFirebase();
        retrieveSellerData();
    }

    private void retrieveSellerData() {
        userReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    itemSellerImageUrl = snapshot.child("profileImage").getValue().toString();
                    itemSellerUsername = snapshot.child("username").getValue().toString();
                    loyaltyString = snapshot.child("dateUserCreated").getValue().toString();
                    String subLoyaltyString = changeNumberDateToWordedDate(loyaltyString);

                    Picasso.get().load(itemSellerImageUrl).into(itemDetailSellerImage);
                    itemDetailSellerUsername.setText(itemSellerUsername);
                    loyalty.setText(String.format("Member since %s", subLoyaltyString));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ItemDetailActivity.this, "Sorry, something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveItemDataFromFirebase() {
        itemReference.child(itemKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    itemTitle = snapshot.child("title").getValue().toString();
                    itemImage = snapshot.child("imageUrl").getValue().toString();
                    itemLocation = snapshot.child("location").getValue().toString();
                    itemCategory = snapshot.child("category").getValue().toString();
                    itemCondition = snapshot.child("condition").getValue().toString();
                    itemPickUpOnly = snapshot.child("pickUpOnly").getValue().toString();
                    itemPickUpOnly = (itemPickUp = true) ? "Pick Up Only" : "Drop Off";
                    itemBrand = snapshot.child("brand").getValue().toString();
                    itemModel = snapshot.child("model").getValue().toString();
                    itemType = snapshot.child("type").getValue().toString();
                    itemDescription = snapshot.child("description").getValue().toString();
                    itemLat = snapshot.child("latitude").getValue().toString();
                    itemLong = snapshot.child("longitude").getValue().toString();

                    Picasso.get().load(itemImage).into(itemDetailImage);
                    itemDetailTitle.setText(itemTitle);
                    itemDetailLocation.setText(itemLocation);
                    itemDetailCategory.setText(itemCategory);
                    itemDetailCondition.setText(String.format("Condition: %s", itemCondition));
                    itemDetailPickUpOnly.setText(String.format("%s", itemPickUpOnly));
                    itemDetailBrand.setText(String.format("Brand: %s", itemBrand));
                    itemDetailModel.setText(String.format("Model: %s", itemModel));
                    itemDetailType.setText(String.format("Type: %s", itemType));
                    itemDetailDescription.setText(String.format("More info: %s", itemDescription));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ItemDetailActivity.this, "Sorry, could not retrieve data from firebase!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        itemReference.child(itemKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    itemLat = snapshot.child("latitude").getValue().toString();
                    itemLong = snapshot.child("longitude").getValue().toString();

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(itemLat), Double.parseDouble(itemLong)), 12));
                    Circle circle = googleMap.addCircle(new CircleOptions()
                            .center(new LatLng(Double.parseDouble(itemLat), Double.parseDouble(itemLong)))
                            .radius(1500)
                            .strokeColor(Color.parseColor("#8097FAFB"))
                            .fillColor(Color.parseColor("#8097FAFB")));
                    circle.setVisible(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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