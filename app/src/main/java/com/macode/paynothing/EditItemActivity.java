package com.macode.paynothing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.macode.paynothing.fragments.EditMultipleItemPropertiesFragment;
import com.macode.paynothing.fragments.accountSettings.AuthenticateUserFragment;
import com.macode.paynothing.fragments.postItem.PostItemCategoryFragment;
import com.squareup.picasso.Picasso;

public class EditItemActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String itemKey, itemImage, itemTitle, itemCategory, itemCondition, itemBrand, itemModel, itemType, itemDescription, itemLocation, itemPickUpOnly, newUpdatedTitle, newUpdatedBrand, newUpdatedModel, newUpdatedType, itemPropertyKey;
    private Boolean itemPickUp;
    private TextView category, condition, description, location, pickUpOnly, updateTitle, editCategory, editCondition, updateBrand, updateModel, updateType, editDescription, editLocation, editPickUpOnly;
    private ImageView image;
    private EditText title, brand, model, type;
    public RelativeLayout main, secondary;
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
        editDescription = findViewById(R.id.editItemDescriptionButton);
        location = findViewById(R.id.editItemLocation);
        editLocation = findViewById(R.id.editItemLocationButton);
        pickUpOnly = findViewById(R.id.editItemPickUpOnly);
        editPickUpOnly = findViewById(R.id.editItemPickUpOnlyButton);
        main = findViewById(R.id.editItemMainRelativeLayout);
        secondary = findViewById(R.id.editItemSecondaryRelativeLayout);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        itemReference = FirebaseDatabase.getInstance().getReference().child("Items");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadItem();

        updateTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUpdatedTitle = title.getText().toString();
                itemReference.child(itemKey).child("title").setValue(newUpdatedTitle).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditItemActivity.this, "Item title updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditItemActivity.this, "Sorry, could not updated item's title", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.INVISIBLE);
                secondary.setVisibility(View.VISIBLE);
                Bundle categoryBundle = new Bundle();
                Fragment itemPropertyFragment = new EditMultipleItemPropertiesFragment();
                itemPropertyKey = "category";
                categoryBundle.putString("itemPropertyKey", itemPropertyKey);
                categoryBundle.putString("itemKey", itemKey);
                categoryBundle.putString("itemTitle", itemTitle);
                itemPropertyFragment.setArguments(categoryBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.editItemFragmentContainer, itemPropertyFragment).commit();
            }
        });

        editCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.INVISIBLE);
                secondary.setVisibility(View.VISIBLE);
                Bundle conditionBundle = new Bundle();
                Fragment itemPropertyFragment = new EditMultipleItemPropertiesFragment();
                itemPropertyKey = "condition";
                conditionBundle.putString("itemPropertyKey", itemPropertyKey);
                conditionBundle.putString("itemKey", itemKey);
                conditionBundle.putString("itemTitle", itemTitle);
                itemPropertyFragment.setArguments(conditionBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.editItemFragmentContainer, itemPropertyFragment).commit();
            }
        });

        updateBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUpdatedBrand = brand.getText().toString();
                itemReference.child(itemKey).child("brand").setValue(newUpdatedBrand).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditItemActivity.this, "Item brand updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditItemActivity.this, "Sorry, could not updated item's title", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        updateModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUpdatedModel = model.getText().toString();
                itemReference.child(itemKey).child("model").setValue(newUpdatedModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditItemActivity.this, "Item model updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditItemActivity.this, "Sorry, could not updated item's title", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        updateType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUpdatedType = type.getText().toString();
                itemReference.child(itemKey).child("type").setValue(newUpdatedType).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditItemActivity.this, "Item type updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditItemActivity.this, "Sorry, could not updated item's title", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        editDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.INVISIBLE);
                secondary.setVisibility(View.VISIBLE);
                Bundle descriptionBundle = new Bundle();
                Fragment itemPropertyFragment = new EditMultipleItemPropertiesFragment();
                itemPropertyKey = "description";
                descriptionBundle.putString("itemPropertyKey", itemPropertyKey);
                descriptionBundle.putString("itemKey", itemKey);
                descriptionBundle.putString("itemTitle", itemTitle);
                itemPropertyFragment.setArguments(descriptionBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.editItemFragmentContainer, itemPropertyFragment).commit();
            }
        });

        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.INVISIBLE);
                secondary.setVisibility(View.VISIBLE);
                Bundle locationBundle = new Bundle();
                Fragment itemPropertyFragment = new EditMultipleItemPropertiesFragment();
                itemPropertyKey = "location";
                locationBundle.putString("itemPropertyKey", itemPropertyKey);
                locationBundle.putString("itemKey", itemKey);
                locationBundle.putString("itemTitle", itemTitle);
                itemPropertyFragment.setArguments(locationBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.editItemFragmentContainer, itemPropertyFragment).commit();
            }
        });

        editPickUpOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.INVISIBLE);
                secondary.setVisibility(View.VISIBLE);
                Bundle pickUpOnlyBundle = new Bundle();
                Fragment itemPropertyFragment = new EditMultipleItemPropertiesFragment();
                itemPropertyKey = "pickUpOnly";
                pickUpOnlyBundle.putString("itemPropertyKey", itemPropertyKey);
                pickUpOnlyBundle.putString("itemKey", itemKey);
                pickUpOnlyBundle.putString("itemTitle", itemTitle);
                itemPropertyFragment.setArguments(pickUpOnlyBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.editItemFragmentContainer, itemPropertyFragment).commit();
            }
        });
    }

    public void loadItem() {
        Intent intent = getIntent();
        itemKey = intent.getStringExtra("itemKey");
        retrieveItemDataFromFirebase();
    }

    private void retrieveItemDataFromFirebase() {
        itemReference.child(itemKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    itemTitle = snapshot.child("title").getValue().toString();
                    getSupportActionBar().setTitle(String.format("Editing \"%s\" Item", itemTitle));
                    itemImage = snapshot.child("imageUrl").getValue().toString();
                    itemLocation = snapshot.child("location").getValue().toString();
                    itemCategory = snapshot.child("category").getValue().toString();
                    itemCondition = snapshot.child("condition").getValue().toString();
                    itemPickUp = (Boolean) snapshot.child("pickUpOnly").getValue();
                    itemPickUpOnly = (itemPickUp) ? "Pick Up Only" : "Drop Off";
                    itemBrand = snapshot.child("brand").getValue().toString();
                    itemModel = snapshot.child("model").getValue().toString();
                    itemType = snapshot.child("type").getValue().toString();
                    itemDescription = snapshot.child("description").getValue().toString();
                    if (itemDescription.length() > 30) {
                        itemDescription = itemDescription.substring(0, 30) + "...";
                    }

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}