package com.macode.paynothing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar itemDetailToolBar;
    private CircleImageView itemDetailSellerImage;
    private ImageView itemDetailImage;
    private String itemTitle, itemImage, itemCategory,  itemCondition, itemBrand, itemModel, itemType, itemDescription, itemLocation, itemPickUpOnly, itemLat, itemLong, itemSellerImageUrl, itemSellerUsername, loyaltyString;
    private TextView itemDetailTitle, itemDetailLocation, itemDetailCategory, itemDetailCondition, itemDetailPickUpOnly, itemDetailSellerUsername, itemDetailBrand, itemDetailModel, itemDetailType, itemDetailDescription, loyalty;
    private Boolean itemPickUp;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference;
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

        itemPickUpOnly = (itemPickUp) ? "Pick Up Only" : "Drop Off";

        itemDetailImage.setImageBitmap(stringToBitMap(itemImage));
        itemDetailTitle.setText(itemTitle);
        itemDetailLocation.setText(itemLocation);
        itemDetailCategory.setText(itemCategory);
        itemDetailCondition.setText(String.format("Condition: %s", itemCondition));
        itemDetailPickUpOnly.setText(String.format("%s", itemPickUpOnly));
        retrieveSellerData();
        itemDetailBrand.setText(String.format("Brand: %s", itemBrand));
        itemDetailModel.setText(String.format("Model: %s", itemModel));
        itemDetailType.setText(String.format("Type: %s", itemType));
        itemDetailDescription.setText(String.format("More info: %s", itemDescription));

        editItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailActivity.this, EditItemActivity.class);
                startActivity(intent);
            }
        });
    }

    public void retrieveExtraData() {
        Intent intent = getIntent();
        itemTitle = intent.getStringExtra("title");
        itemImage = intent.getStringExtra("image");
        itemCategory = intent.getStringExtra("category");
        itemCondition = intent.getStringExtra("condition");
        itemBrand = intent.getStringExtra("brand");
        itemModel = intent.getStringExtra("model");
        itemType = intent.getStringExtra("type");
        itemDescription = intent.getStringExtra("description");
        itemLocation = intent.getStringExtra("location");
        itemLat = intent.getStringExtra("lat");
        itemLong = intent.getStringExtra("long");
        itemPickUp = intent.getBooleanExtra("pickUp", false);
    }

    private void retrieveSellerData() {
        userReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ItemDetailActivity.this, "Sorry, something went wrong!", Toast.LENGTH_SHORT).show();
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
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(itemLat), Double.parseDouble(itemLong)), 12));
        Circle circle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(Double.parseDouble(itemLat), Double.parseDouble(itemLong)))
                .radius(1500)
                .strokeColor(Color.parseColor("#8097FAFB"))
                .fillColor(Color.parseColor("#8097FAFB")));
        circle.setVisible(true);
    }
}