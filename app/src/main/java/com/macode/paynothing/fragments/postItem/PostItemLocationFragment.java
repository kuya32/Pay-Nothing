package com.macode.paynothing.fragments.postItem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.macode.paynothing.ItemDetailActivity;
import com.macode.paynothing.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostItemLocationFragment extends Fragment {

    private Toolbar postToolbar;
    private SwitchCompat pickUpOnlySwitch;
    private Boolean locationDetermined, pickUpOnly = false;
    private EditText postItemZipCode;
    private String itemImageString, itemTitleString, itemCategoryString, itemConditionString, itemBrandString, itemModelString, itemTypeString, itemDescriptionString, itemLocationString, cityName, stateName, pickUpOnlyString, latString, longString;
    private Button postItemGetLocationButton, postItemSubmitButton, postItemLocationApplyCityAndStateButton;
    private FusedLocationProviderClient client;
    private RelativeLayout mainLocationRelativeLayout, getLocationRelativeLayout;
    private TextView postItemLocationText, postItemLocationEditText, postItemLocationCityAndStateText;
    private CardView postingItemProgressCardView;
    private ProgressBar postingItemProgressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference, itemReference;
    private StorageReference itemImageReference;
    private Bitmap itemImageBitmap;
    private Uri itemImageUri;

    public PostItemLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_item_location, container, false);

        postToolbar = view.findViewById(R.id.postItemLocationToolbar);
        pickUpOnlySwitch = view.findViewById(R.id.pickUpOnlySwitch);
        postItemZipCode = view.findViewById(R.id.postItemZipCode);
        postItemGetLocationButton = view.findViewById(R.id.postItemGetLocationButton);
        postItemSubmitButton = view.findViewById(R.id.postItemLocationSubmitButton);
        postItemLocationApplyCityAndStateButton = view.findViewById(R.id.postItemApplyCityAndStateButton);
        mainLocationRelativeLayout = view.findViewById(R.id.mainLocationRelativeLayout);
        getLocationRelativeLayout = view.findViewById(R.id.getLocationRelativeLayout);
        postItemLocationText = view.findViewById(R.id.postItemLocationText);
        postItemLocationEditText = view.findViewById(R.id.postItemLocationEditText);
        postItemLocationApplyCityAndStateButton = view.findViewById(R.id.postItemApplyCityAndStateButton);
        postingItemProgressCardView = view.findViewById(R.id.postingItemProgressCardView);
        postingItemProgressBar = view.findViewById(R.id.postingItemProgressBar);
        postingItemProgressBar.getIndeterminateDrawable().setColorFilter(Color.rgb(34, 147, 13), android.graphics.PorterDuff.Mode.MULTIPLY);
        client = LocationServices.getFusedLocationProviderClient(requireActivity());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        itemReference = FirebaseDatabase.getInstance().getReference().child("Items");
        itemImageReference = FirebaseStorage.getInstance().getReference().child("ItemImages");

        ((AppCompatActivity) requireActivity()).setSupportActionBar(postToolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Location");
        setHasOptionsMenu(true);

        locationFragmentRetriever();

        checkSwitchPosition();

        postItemGetLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationDetermined = true;
                    getCurrentLocation();
                    postItemZipCode.setText("");
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }
        });

        postItemLocationApplyCityAndStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationRelativeLayout.setVisibility(View.INVISIBLE);
                mainLocationRelativeLayout.setVisibility(View.VISIBLE);
                if (!postItemZipCode.getText().toString().matches("") && !locationDetermined) {
                    Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
                    String zipCode = postItemZipCode.getText().toString();
                    try {
                        List<Address> zipAddresses = geocoder.getFromLocationName(zipCode, 1);
                        if (zipAddresses != null && !zipAddresses.isEmpty()) {
                            Address address = zipAddresses.get(0);
                            List<Address> addresses = geocoder.getFromLocation(address.getLatitude(), address.getLongitude(), 1);
                            Toast.makeText(requireActivity(), "Unable to geocode zip code", Toast.LENGTH_LONG).show();
                            latString = String.valueOf(address.getLatitude());
                            longString = String.valueOf(address.getLongitude());
                            cityName = addresses.get(0).getLocality();
                            stateName = addresses.get(0).getAdminArea();
                            postItemLocationText.setText(String.format("%s, %s", cityName, stateName));
                            postItemLocationText.setError(null);
                        } else {
                            Toast.makeText(requireActivity(), "Unable to geocode zip code", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    postItemLocationText.setText(postItemLocationCityAndStateText.getText().toString());
                }
            }
        });

        postItemLocationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLocationRelativeLayout.setVisibility(View.INVISIBLE);
                getLocationRelativeLayout.setVisibility(View.VISIBLE);
                locationDetermined = false;
            }
        });

        postItemSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(postItemLocationText.getText())) {
                    showError(postItemLocationText, "Edit Location!");
                } else {
                    saveItemDetailsToDatabase();
                    locationFragmentPasser();
                }
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            Toast.makeText(requireActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.post_item_top_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cancel) {
            requireActivity().onBackPressed();
        } else if (item.getItemId() == android.R.id.home) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.postItemFragmentContainer, new PostItemCategoryFragment()).commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
                        postItemLocationCityAndStateText = requireView().findViewById(R.id.postItemLocationCityAndStateText);
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            latString = String.valueOf(location.getLatitude());
                            longString = String.valueOf(location.getLongitude());
                            cityName = addresses.get(0).getLocality();
                            stateName = addresses.get(0).getAdminArea();
                            postItemLocationCityAndStateText.setText(String.format("%s, %s", cityName, stateName));
                            postItemLocationText.setError(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location1.getLatitude(), location1.getLongitude(), 1);
                                    latString = String.valueOf(location1.getLatitude());
                                    longString = String.valueOf(location1.getLongitude());
                                    cityName = addresses.get(0).getLocality();
                                    stateName = addresses.get(0).getAdminArea();
                                    postItemLocationCityAndStateText.setText(String.format("%s, %s", cityName, stateName));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void saveItemDetailsToDatabase() {
        postingItemProgressBar.setVisibility(View.VISIBLE);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        final String stringDate = format.format(date);

        itemImageBitmap = stringToBitMap(itemImageString);
        itemImageUri = getImageUri(requireActivity(), itemImageBitmap);

        itemImageReference.child(firebaseUser.getUid() + " " + stringDate).putFile(itemImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    itemImageReference.child(firebaseUser.getUid() + " " + stringDate).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap hashMap = new HashMap();
                            hashMap.put("dateItemPosted", stringDate);
                            hashMap.put("title", itemTitleString);
                            hashMap.put("imageUrl", uri.toString());
                            hashMap.put("category", itemCategoryString);
                            hashMap.put("condition", itemConditionString);
                            hashMap.put("brand", itemBrandString);
                            hashMap.put("model", itemModelString);
                            hashMap.put("type", itemTypeString);
                            hashMap.put("description", itemDescriptionString);
                            hashMap.put("location", itemLocationString);
                            hashMap.put("pickUpOnly", pickUpOnly);
                            hashMap.put("latitude", latString);
                            hashMap.put("longitude", longString);
                            itemReference.child(firebaseUser.getUid() + " " + stringDate).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    postingItemProgressCardView.setVisibility(View.INVISIBLE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(requireActivity(), "Item Posted", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(requireActivity(), "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void checkSwitchPosition() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("project", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        pickUpOnlySwitch.setChecked(sharedPreferences.getBoolean("switch", false));

        pickUpOnlySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                     pickUpOnly = true;
                } else {
                    pickUpOnly = false;
                }
            }
        });
    }

    private void locationFragmentPasser() {
        itemLocationString = postItemLocationText.getText().toString();
        Intent intent = new Intent(requireActivity(), ItemDetailActivity.class);
        intent.putExtra("title", itemTitleString);
        intent.putExtra("image", itemImageString);
        intent.putExtra("category", itemCategoryString);
        intent.putExtra("condition", itemConditionString);
        intent.putExtra("brand", itemBrandString);
        intent.putExtra("model", itemModelString);
        intent.putExtra("type", itemTypeString);
        intent.putExtra("description", itemDescriptionString);
        intent.putExtra("location", itemLocationString);
        intent.putExtra("pickUp", pickUpOnly);
        intent.putExtra("lat", latString);
        intent.putExtra("long", longString);
        startActivity(intent);
    }

    private void locationFragmentRetriever() {
        Bundle locationRetrieveBundle = this.getArguments();
        itemTitleString = locationRetrieveBundle.getString("title");
        itemImageString = locationRetrieveBundle.getString("image");
        itemCategoryString = locationRetrieveBundle.getString("category");
        itemConditionString = locationRetrieveBundle.getString("condition");
        itemBrandString = locationRetrieveBundle.getString("brand");
        itemModelString = locationRetrieveBundle.getString("model");
        itemTypeString = locationRetrieveBundle.getString("type");
        itemDescriptionString = locationRetrieveBundle.getString("description");
    }

    public Bitmap stringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Pic", null);
        return Uri.parse(path);
    }

    private void showError(TextView view, String text) {
        view.setError(text);
        view.requestFocus();
    }
}