package com.macode.paynothing.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.macode.paynothing.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EditMultipleItemPropertiesFragment extends Fragment {

    private Toolbar mainToolbar, locationToolbar;
    private RelativeLayout main, secondary, mainLayout, locationLayout;
    private CardView categoryCardView, conditionCardView, descriptionCardView, pickUpOnlyCardView;
    private String itemKey, itemPropertyKey, itemTitle, categoryString, conditionString, descriptionString, locationString, latString, longString, cityName, stateName;
    private TextInputLayout descriptionInput;
    private EditText locationZipCode;
    private TextView cityAndState;
    private Boolean pickUpOnly, locationDetermined;
    private Spinner categorySpinner, conditionSpinner;
    private Button categorySaveButton, conditionSaveButton, descriptionSaveButton, getLocationButton, locationSaveButton, pickUpOnlyYesButton, pickUpOnlyNoButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference itemReference;
    private FusedLocationProviderClient client;

    public EditMultipleItemPropertiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_multiple_item_properties, container, false);

        mainToolbar = view.findViewById(R.id.editingMultipleItemPropertiesToolbar);
        locationToolbar = view.findViewById(R.id.editingLocationToolbar);
        main = requireActivity().findViewById(R.id.editItemMainRelativeLayout);
        secondary = requireActivity().findViewById(R.id.editItemSecondaryRelativeLayout);
        mainLayout = view.findViewById(R.id.editingItemMainRelativeLayout);
        locationLayout = view.findViewById(R.id.editingItemGetLocationRelativeLayout);
        categoryCardView = view.findViewById(R.id.editingCategoryCardView);
        conditionCardView = view.findViewById(R.id.editingConditionCardView);
        descriptionCardView = view.findViewById(R.id.editingDescriptionCardView);
        pickUpOnlyCardView = view.findViewById(R.id.editingPickUpOnlyCardView);
        descriptionInput = view.findViewById(R.id.editingDescriptionInput);
        locationZipCode = view.findViewById(R.id.editingItemZipCode);
        cityAndState = view.findViewById(R.id.editingLocationCityAndState);
        categorySpinner = view.findViewById(R.id.editingCategorySpinner);
        conditionSpinner = view.findViewById(R.id.editingConditionSpinner);
        categorySaveButton = view.findViewById(R.id.editingCategorySaveButton);
        conditionSaveButton = view.findViewById(R.id.editingConditionSaveButton);
        descriptionSaveButton = view.findViewById(R.id.editingDescriptionSaveButton);
        getLocationButton = view.findViewById(R.id.editingItemGetLocationButton);
        locationSaveButton = view.findViewById(R.id.editingLocationSaveCityAndStateButton);
        pickUpOnlyYesButton = view.findViewById(R.id.editingPickUpOnlyYesButton);
        pickUpOnlyNoButton = view.findViewById(R.id.editingPickUpOnlyNoButton);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        itemReference = FirebaseDatabase.getInstance().getReference().child("Items");
        client = LocationServices.getFusedLocationProviderClient(requireActivity());

        addingCategorySpinner();
        addingConditionSpinner();

        retrieveItemPropertyDecider();

        categorySaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemReference.child(itemKey).child("category").setValue(categoryString).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mainLayout.setVisibility(View.GONE);
                            secondary.setVisibility(View.GONE);
                            main.setVisibility(View.VISIBLE);
                            Toast.makeText(requireActivity(), "Item category updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireActivity(), "Sorry, could not update item category to firebase!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        conditionSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemReference.child(itemKey).child("condition").setValue(conditionString).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mainLayout.setVisibility(View.GONE);
                            secondary.setVisibility(View.GONE);
                            main.setVisibility(View.VISIBLE);
                            Toast.makeText(requireActivity(), "Item condition updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireActivity(), "Sorry, could not update item condition to firebase!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        descriptionSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemReference.child(itemKey).child("description").setValue(descriptionString).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mainLayout.setVisibility(View.GONE);
                            secondary.setVisibility(View.GONE);
                            main.setVisibility(View.VISIBLE);
                            Toast.makeText(requireActivity(), "Item description updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireActivity(), "Sorry, could not update item description to firebase!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationDetermined = true;
                    getCurrentLocation();
                    locationZipCode.setText("");
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }
        });

        locationSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCityAndStateToFirebase();
            }
        });

        pickUpOnlyYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickUpOnly = true;
                itemReference.child(itemKey).child("pickUpOnly").setValue(pickUpOnly).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mainLayout.setVisibility(View.GONE);
                            secondary.setVisibility(View.GONE);
                            main.setVisibility(View.VISIBLE);
                            Toast.makeText(requireActivity(), "Item pickUpOnly updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireActivity(), "Sorry, could not update item pickUpOnly to firebase!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        pickUpOnlyNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickUpOnly = false;
                itemReference.child(itemKey).child("pickUpOnly").setValue(pickUpOnly).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mainLayout.setVisibility(View.GONE);
                            secondary.setVisibility(View.GONE);
                            main.setVisibility(View.VISIBLE);
                            Toast.makeText(requireActivity(), "Item pickUpOnly updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireActivity(), "Sorry, could not update item pickUpOnly to firebase!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.post_item_top_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cancel && itemPropertyKey.equals("location")) {
            locationLayout.setVisibility(View.GONE);
            secondary.setVisibility(View.GONE);
            main.setVisibility(View.VISIBLE);
        } else if (item.getItemId() == R.id.cancel) {
            mainLayout.setVisibility(View.GONE);
            secondary.setVisibility(View.GONE);
            main.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveCityAndStateToFirebase() {
        if (!locationZipCode.getText().toString().matches("") && !locationDetermined) {
            Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
            String zipCode = locationZipCode.getText().toString();
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
                    cityAndState.setText(String.format("%s, %s", cityName, stateName));
                    cityAndState.setError(null);
                } else {
                    Toast.makeText(requireActivity(), "Unable to geocode zip code", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        HashMap hashMap = new HashMap();
        hashMap.put("latitude", latString);
        hashMap.put("longitude", longString);
        hashMap.put("location", cityAndState.getText().toString());
        itemReference.child(itemKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    locationLayout.setVisibility(View.GONE);
                    secondary.setVisibility(View.GONE);
                    main.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(requireActivity(), "Could not update location element for item!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                        cityAndState = requireView().findViewById(R.id.editingLocationCityAndState);
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            latString = String.valueOf(location.getLatitude());
                            longString = String.valueOf(location.getLongitude());
                            cityName = addresses.get(0).getLocality();
                            stateName = addresses.get(0).getAdminArea();
                            cityAndState.setText(String.format("%s, %s", cityName, stateName));
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
                                    cityAndState.setText(String.format("%s, %s", cityName, stateName));
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

    private void retrieveItemPropertyDecider() {
        Bundle itemPropertyRetrievalBundle = this.getArguments();
        itemKey = itemPropertyRetrievalBundle.getString("itemKey");
        itemPropertyKey = itemPropertyRetrievalBundle.getString("itemPropertyKey");
        itemTitle = itemPropertyRetrievalBundle.getString("itemTitle");

        switch (itemPropertyKey) {
            case "category":
                mainLayout.setVisibility(View.VISIBLE);
                categoryCardView.setVisibility(View.VISIBLE);
                ((AppCompatActivity) requireActivity()).setSupportActionBar(mainToolbar);
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(String.format("Editing %s's %s", itemTitle, itemPropertyKey));
                setHasOptionsMenu(true);
                break;
            case "condition":
                mainLayout.setVisibility(View.VISIBLE);
                conditionCardView.setVisibility(View.VISIBLE);
                ((AppCompatActivity) requireActivity()).setSupportActionBar(mainToolbar);
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(String.format("Editing %s's %s", itemTitle, itemPropertyKey));
                setHasOptionsMenu(true);
                break;
            case "description":
                mainLayout.setVisibility(View.VISIBLE);
                descriptionCardView.setVisibility(View.VISIBLE);
                ((AppCompatActivity) requireActivity()).setSupportActionBar(mainToolbar);
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(String.format("Editing %s's %s", itemTitle, itemPropertyKey));
                setHasOptionsMenu(true);
                break;
            case "location":
                locationLayout.setVisibility(View.VISIBLE);
                locationDetermined = false;
                ((AppCompatActivity) requireActivity()).setSupportActionBar(locationToolbar);
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(String.format("Editing %s's %s", itemTitle, itemPropertyKey));
                setHasOptionsMenu(true);
                break;
            case "pickUpOnly":
                mainLayout.setVisibility(View.VISIBLE);
                pickUpOnlyCardView.setVisibility(View.VISIBLE);
                ((AppCompatActivity) requireActivity()).setSupportActionBar(mainToolbar);
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(String.format("Editing %s's Pick Up Only", itemTitle));
                setHasOptionsMenu(true);
                break;
            default:
                Toast.makeText(requireActivity(), "This is impossibruuuuu!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void addingCategorySpinner() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, getResources().getStringArray(R.array.categories));
        categoryAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryString = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void addingConditionSpinner() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, getResources().getStringArray(R.array.condition));
        categoryAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        conditionSpinner.setAdapter(categoryAdapter);

        conditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                conditionString = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}