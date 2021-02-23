package com.macode.paynothing.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;
import com.macode.paynothing.EditItemActivity;
import com.macode.paynothing.ItemDetailActivity;
import com.macode.paynothing.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherItemDetailActivity extends AppCompatActivity implements OnMapReadyCallback{

    private Toolbar itemDetailToolBar;
    private CircleImageView itemDetailSellerImage;
    private ImageView itemDetailImage;
    private String itemKey, otherUserId, itemTitle, itemImage, itemCategory,  itemCondition, itemBrand, itemModel, itemType, itemDescription, itemLocation, itemPickUpOnly, itemLat, itemLong, itemSellerImageUrl, itemSellerUsername, loyaltyString;
    private TextView itemDetailTitle, itemDetailLocation, itemDetailCategory, itemDetailCondition, itemDetailPickUpOnly, itemDetailSellerUsername, itemDetailBrand, itemDetailModel, itemDetailType, itemDetailDescription, loyalty;
    private Boolean itemPickUp, isSaved = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference, itemReference, savedItemReference;
    private MenuItem savedItem;
    private Menu otherItemDetailTopMenu;

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
        savedItemReference = FirebaseDatabase.getInstance().getReference().child("SavedItems");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.other_item_detail_top_menu, menu);
        this.otherItemDetailTopMenu = menu;
        checkIfItemIsSaved();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ((item.getItemId() == R.id.saveItem && isSaved)) {
            removedSavedItem();
            isSaved = false;
        } else if (item.getItemId() == R.id.saveItem && !isSaved) {
            saveItem(itemKey);
            isSaved = true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isSaved) {
            savedItem = menu.findItem(R.id.saveItem)
                    .setIcon(R.drawable.ic_unsaved);
        } else {
            savedItem = menu.findItem(R.id.saveItem)
                    .setIcon(R.drawable.ic_saved);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void checkIfItemIsSaved() {
        savedItemReference.child(firebaseUser.getUid()).child(itemKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("title").exists()) {
                    isSaved = true;
                    savedItem = otherItemDetailTopMenu.findItem(R.id.saveItem);
                    savedItem.setIcon(R.drawable.ic_saved);
                } else {
                    isSaved = false;
                    savedItem = otherItemDetailTopMenu.findItem(R.id.saveItem);
                    savedItem.setIcon(R.drawable.ic_unsaved);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveItem(String itemKey) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        final String stringDate = format.format(date);
        HashMap hashMap = new HashMap();
        hashMap.put("title", itemTitle);
        hashMap.put("imageUrl", itemImage);
        hashMap.put("dateItemSaved", stringDate);
        savedItemReference.child(firebaseUser.getUid()).child(itemKey).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                isSaved = true;
                Toast.makeText(OtherItemDetailActivity.this, "Item Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removedSavedItem() {
        savedItemReference.child(firebaseUser.getUid()).child(itemKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    isSaved = false;
                    Toast.makeText(OtherItemDetailActivity.this, "Item has been unsaved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OtherItemDetailActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
}