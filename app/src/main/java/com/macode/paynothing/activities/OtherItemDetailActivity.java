package com.macode.paynothing.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
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
import com.macode.paynothing.EditItemActivity;
import com.macode.paynothing.ItemDetailActivity;
import com.macode.paynothing.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherItemDetailActivity extends AppCompatActivity implements OnMapReadyCallback{

    private Toolbar itemDetailToolBar;
    private CircleImageView itemDetailSellerImage;
    private ImageView itemDetailImage;
    private String itemKey, otherUserId, itemTitle, itemImage, itemCategory,  itemCondition, itemBrand, itemModel, itemType, itemDescription, itemLocation, itemPickUpOnly, itemLat, itemLong, itemSellerImageUrl, itemSellerUsername, loyaltyString;
    private TextView itemDetailTitle, itemDetailLocation, itemDetailCategory, itemDetailCondition, itemDetailPickUpOnly, itemDetailSellerUsername, itemDetailBrand, itemDetailModel, itemDetailType, itemDetailDescription, loyalty;
    private Boolean itemPickUp;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference, itemReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_item_detail);

        itemDetailToolBar = findViewById(R.id.otherItemDetailToolbar);
        itemDetailImage = findViewById(R.id.otherItemDetailImageView);
        itemDetailTitle = findViewById(R.id.otherItemDetailTitle);
        itemDetailLocation = findViewById(R.id.otherItemDetailLocation);
        itemDetailCategory = findViewById(R.id.otherItemDetailCategory);
        itemDetailCondition = findViewById(R.id.otherItemDetailCondition);
        itemDetailPickUpOnly = findViewById(R.id.otherItemDetailPickUpOnly);
        itemDetailSellerImage = findViewById(R.id.otherItemDetailSellerImage);
        itemDetailSellerUsername = findViewById(R.id.otherItemDetailSellerUsername);
        loyalty = findViewById(R.id.otherLoyalty);
        itemDetailBrand = findViewById(R.id.otherItemDetailBrand);
        itemDetailModel = findViewById(R.id.otherItemDetailModel);
        itemDetailType = findViewById(R.id.otherItemDetailType);
        itemDetailDescription = findViewById(R.id.otherItemDetailDescription);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        itemReference = FirebaseDatabase.getInstance().getReference().child("Items");

        retrieveExtraData();
        otherUserId = itemKey.substring(0, itemKey.indexOf(" "));

        setSupportActionBar(itemDetailToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        retrieveItemData();
        retrieveSellerData();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.otherItemDetailGoogleMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Null Map Fragment", Toast.LENGTH_LONG).show();
        }

    }

    public void retrieveExtraData() {
        Intent intent = getIntent();
        itemKey = intent.getStringExtra("itemKey");
    }

    private void retrieveItemData() {
        itemReference.child(itemKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    itemTitle = snapshot.child("title").getValue().toString();
                    itemImage = snapshot.child("imageUrl").getValue().toString();
                    itemLocation = snapshot.child("location").getValue().toString();
                    itemCategory = snapshot.child("category").getValue().toString();
                    itemCondition = snapshot.child("condition").getValue().toString();
//                    itemPickUpOnly = snapshot.child("").getValue().toString();
//                    itemPickUpOnly = (itemPickUp = true) ? "Pick Up Only" : "Drop Off";
                    itemBrand = snapshot.child("brand").getValue().toString();
                    itemModel = snapshot.child("model").getValue().toString();
                    itemType = snapshot.child("type").getValue().toString();
                    itemDescription = snapshot.child("description").getValue().toString();
                    itemLat = snapshot.child("lat").getValue().toString();
                    itemLong = snapshot.child("long").getValue().toString();

                    Picasso.get().load(itemImage).into(itemDetailImage);
                    itemDetailTitle.setText(itemTitle);
                    itemDetailLocation.setText(itemLocation);
                    itemDetailCategory.setText(itemCategory);
                    itemDetailCondition.setText(String.format("Condition: %s", itemCondition));
//                    itemDetailPickUpOnly.setText(String.format("%s", itemPickUpOnly));
                    itemDetailBrand.setText(String.format("Brand: %s", itemBrand));
                    itemDetailModel.setText(String.format("Model: %s", itemModel));
                    itemDetailType.setText(String.format("Type: %s", itemType));
                    itemDetailDescription.setText(String.format("More info: %s", itemDescription));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OtherItemDetailActivity.this, "Sorry, something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveSellerData() {
        userReference.child(otherUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    itemSellerImageUrl = snapshot.child("profileImage").getValue().toString();
                    itemSellerUsername = snapshot.child("username").getValue().toString();
                    loyaltyString = snapshot.child("dateUserCreated").getValue().toString();
                    String subLoyaltyString = loyaltyString.substring((loyaltyString.indexOf("-") + 1), loyaltyString.indexOf(" "));

                    Picasso.get().load(itemSellerImageUrl).into(itemDetailSellerImage);
                    itemDetailSellerUsername.setText(itemSellerUsername);
                    loyalty.setText(String.format("Member since %s", subLoyaltyString));
                    getSupportActionBar().setTitle(String.format("%s\'s Item Details", itemSellerUsername));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OtherItemDetailActivity.this, "Sorry, something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        System.out.println(itemKey + "Hello");
        itemReference.child(itemKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    itemLat = snapshot.child("lat").getValue().toString();
                    itemLong = snapshot.child("long").getValue().toString();

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
}