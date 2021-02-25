package com.macode.paynothing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macode.paynothing.fragments.postItem.PostItemCategoryFragment;
import com.squareup.picasso.Picasso;

public class EditItemActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String itemKey, itemImage, itemTitle, itemCategory, itemCondition, itemBrand, itemModel, itemType, itemDescription, itemLocation, itemPickUpOnly;
    private Boolean itemPickUp;
    private TextView category, condition, location, pickUpOnly, updateTitle, editCategory, editCondition, updateBrand, updateModel, updateType, updateDescription, editLocation, editPickUpOnly;
    private ImageView image;
    private EditText title, brand, model, type, description;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference itemReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        toolbar = findViewById(R.id.editItemToolbar);
        image = findViewById(R.id.editItemImageView);
        title = findViewById(R.id.editItemTitle);
        updateTitle = findViewById(R.id.editItemTitleButton);
        category = findViewById(R.id.editItemCategory);
        editCategory = findViewById(R.id.editItemCategoryButton);
        condition = findViewById(R.id.editItemCondition);
        editCondition = findViewById(R.id.editItemConditionButton);
        brand = findViewById(R.id.editItemBrand);
        updateBrand = findViewById(R.id.editItemBrandButton);
        model = findViewById(R.id.editItemModel);
        updateModel = findViewById(R.id.editItemModelButton);
        type = findViewById(R.id.editItemType);
        updateType = findViewById(R.id.editItemTypeButton);
        description = findViewById(R.id.editItemDescription);
        updateDescription = findViewById(R.id.editItemDescriptionButton);
        location = findViewById(R.id.editItemLocation);
        editLocation = findViewById(R.id.editItemLocationButton);
        pickUpOnly = findViewById(R.id.editItemPickUpOnly);
        editPickUpOnly = findViewById(R.id.editItemPickUpOnlyButton);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        itemReference = FirebaseDatabase.getInstance().getReference().child("Items");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadItem();
    }

    public void loadItem() {
        Intent intent = getIntent();
        itemKey = intent.getStringExtra("itemKey");
        if (itemKey != null) {
            retrieveItemDataFromFirebase();
        } else {
            itemTitle = intent.getStringExtra("title");
            itemImage = intent.getStringExtra("image");
            System.out.println(itemImage + " Hello");
            itemCategory = intent.getStringExtra("category");
            itemCondition = intent.getStringExtra("condition");
            itemBrand = intent.getStringExtra("brand");
            itemModel = intent.getStringExtra("model");
            itemType = intent.getStringExtra("type");
            itemDescription = intent.getStringExtra("description");
            itemLocation = intent.getStringExtra("location");
            itemPickUp = intent.getBooleanExtra("pickUpOnly", false);

            if (itemImage.contains("firebasestorage")) {
                Picasso.get().load(itemImage).into(image);
            } else {
                image.setImageBitmap(stringToBitMap(itemImage));
            }
            title.setText(itemTitle);
            location.setText(itemLocation);
            category.setText(itemCategory);
            condition.setText(itemCondition);
            itemPickUpOnly = (itemPickUp = true) ? "Pick Up Only" : "Drop Off";
            pickUpOnly.setText(itemPickUpOnly);
            brand.setText(itemBrand);
            model.setText(itemModel);
            type.setText(itemType);
            description.setText(itemDescription);
        }
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
                    itemPickUp = (Boolean) snapshot.child("pickUpOnly").getValue();
                    itemPickUpOnly = (itemPickUp = true) ? "Pick Up Only" : "Drop Off";
                    itemBrand = snapshot.child("brand").getValue().toString();
                    itemModel = snapshot.child("model").getValue().toString();
                    itemType = snapshot.child("type").getValue().toString();
                    itemDescription = snapshot.child("description").getValue().toString();


                    Picasso.get().load(itemImage).into(image);
                    title.setText(itemTitle);
                    location.setText(itemLocation);
                    category.setText(itemCategory);
                    condition.setText(itemCondition);
                    pickUpOnly.setText(itemPickUpOnly);
                    brand.setText(itemBrand);
                    model.setText(itemModel);
                    type.setText(itemType);
                    description.setText(itemDescription);
                } else {
                    Toast.makeText(EditItemActivity.this, "Could not retrieve item information from Firebase!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditItemActivity.this, "Sorry, could not retrieve data from firebase!", Toast.LENGTH_SHORT).show();
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}