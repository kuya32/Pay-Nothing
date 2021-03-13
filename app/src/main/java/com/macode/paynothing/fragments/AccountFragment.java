package com.macode.paynothing.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.ValueEventListener;
import com.macode.paynothing.activities.AccountSettingsActivity;
import com.macode.paynothing.activities.LoginActivity;
import com.macode.paynothing.activities.PublicProfileActivity;
import com.macode.paynothing.R;
import com.macode.paynothing.activities.SavedItemsActivity;
import com.macode.paynothing.activities.AboutMeActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    private Toolbar accountToolbar;
    private CircleImageView profileImage;
    private TextView accountUserName, accountUserLocation, accountSavedItems, accountSettings, accountPublicProfile, accountHelpCenter;
    private String profileImageUrl, firstName, lastName, location;
    private Button logoutButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        accountToolbar = view.findViewById(R.id.accountToolbar);
        profileImage = view.findViewById(R.id.accountUserProfileImage);
        accountUserName = view.findViewById(R.id.accountUserName);
        accountUserLocation = view.findViewById(R.id.accountUserLocation);
        accountSavedItems = view.findViewById(R.id.accountSavedItemsText);
        accountSettings = view.findViewById(R.id.accountSettingsText);
        accountPublicProfile = view.findViewById(R.id.accountPublicProfileText);
        accountHelpCenter = view.findViewById(R.id.accountHelpText);
        logoutButton = view.findViewById(R.id.accountLogoutButton);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        ((AppCompatActivity) requireActivity()).setSupportActionBar(accountToolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Account");
        setHasOptionsMenu(true);

        retrieveUserAccountInfo();

        accountSavedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), SavedItemsActivity.class);
                startActivity(intent);
            }
        });

        accountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), AccountSettingsActivity.class);
                startActivity(intent);
            }
        });

        accountPublicProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), PublicProfileActivity.class);
                startActivity(intent);
            }
        });

        accountHelpCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), AboutMeActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userReference.child(firebaseUser.getUid()).child("status").setValue("Offline").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth.signOut();
                            Intent intent = new Intent(requireActivity(), LoginActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        } else {
                            Toast.makeText(requireActivity(), "Sorry, could not logout for some reason!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;
    }

    private void retrieveUserAccountInfo() {
        userReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileImageUrl = snapshot.child("profileImage").getValue().toString();
                    firstName = snapshot.child("firstName").getValue().toString();
                    lastName = snapshot.child("lastName").getValue().toString();
                    location = snapshot.child("location").getValue().toString();

                    Picasso.get().load(profileImageUrl).into(profileImage);
                    accountUserName.setText(String.format("%s %s", firstName, lastName));
                    accountUserLocation.setText(location);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}