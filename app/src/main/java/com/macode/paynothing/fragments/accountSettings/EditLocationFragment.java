package com.macode.paynothing.fragments.accountSettings;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.macode.paynothing.R;
import com.macode.paynothing.fragments.postItem.PostItemCategoryFragment;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EditLocationFragment extends Fragment {

    private Toolbar toolbar;
    private boolean locationDetermined = false;
    private EditText getLocationZipCode;
    private String locationString, newLocationString, latString, longString, cityName, stateName;
    private Button getLocationButton, applyLocationButton;
    private FusedLocationProviderClient client;
    private RelativeLayout main, secondary;
    private TextView editLocationCityAndStateText;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference;

    public EditLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_location, container, false);

        toolbar = view.findViewById(R.id.editLocationToolbar);
        getLocationZipCode = view.findViewById(R.id.editLocationZipCode);
        getLocationButton = view.findViewById(R.id.editGetLocationButton);
        applyLocationButton = view.findViewById(R.id.editLocationApplyCityAndStateButton);
        client = LocationServices.getFusedLocationProviderClient(requireActivity());
        main = requireActivity().findViewById(R.id.accountSettingsMainRelativeLayout);
        secondary = requireActivity().findViewById(R.id.accountSettingsSecondaryRelativeLayout);
        editLocationCityAndStateText = view.findViewById(R.id.editLocationCityAndStateText);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Set Location");
        setHasOptionsMenu(true);

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationDetermined = true;
                    getCurrentLocation();
                    getLocationZipCode.setText("");
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }
        });

        applyLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getLocationZipCode.getText().toString().matches("") && !locationDetermined) {
                    Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
                    String zipCode = getLocationZipCode.getText().toString();
                    try {
                        List<Address> zipAddresses = geocoder.getFromLocationName(zipCode, 1);
                        if (zipAddresses != null && !zipAddresses.isEmpty()) {
                            Address address = zipAddresses.get(0);
                            List<Address> addresses = geocoder.getFromLocation(address.getLatitude(), address.getLongitude(), 1);
                            Toast.makeText(requireActivity(), "Geocode zip code successful", Toast.LENGTH_LONG).show();
                            latString = String.valueOf(address.getLatitude());
                            longString = String.valueOf(address.getLongitude());
                            cityName = addresses.get(0).getLocality();
                            stateName = addresses.get(0).getAdminArea();
                            saveEditedLocation(String.format("%s, %s", cityName, stateName));
                        } else {
                            Toast.makeText(requireActivity(), "Unable to geocode zip code", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    saveEditedLocation(editLocationCityAndStateText.getText().toString());
                }
            }
        });

        return view;
    }

    private void saveEditedLocation(String cityAndState) {
        if (cityAndState.isEmpty() || !cityAndState.contains(" ") || !cityAndState.contains(",")) {
            Toast.makeText(requireActivity(), "Invalid location!", Toast.LENGTH_LONG).show();
        } else {
            userReference.child(firebaseUser.getUid()).child("location").setValue(cityAndState).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        requireActivity().finish();
                        startActivity(requireActivity().getIntent());
                    }
                }
            });
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
            main.setVisibility(View.VISIBLE);
            secondary.setVisibility(View.GONE);
        }
        return super.onOptionsItemSelected(item);
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
                        editLocationCityAndStateText = requireView().findViewById(R.id.editLocationCityAndStateText);
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            latString = String.valueOf(location.getLatitude());
                            longString = String.valueOf(location.getLongitude());
                            cityName = addresses.get(0).getLocality();
                            stateName = addresses.get(0).getAdminArea();
                            editLocationCityAndStateText.setText(String.format("%s, %s", cityName, stateName));
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
                                    editLocationCityAndStateText.setText(String.format("%s, %s", cityName, stateName));
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

    private void showError(TextView view, String text) {
        view.setError(text);
        view.requestFocus();
    }
}