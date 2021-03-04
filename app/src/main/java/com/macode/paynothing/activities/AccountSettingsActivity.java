package com.macode.paynothing.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macode.paynothing.R;
import com.macode.paynothing.fragments.accountSettings.AuthenticateUserFragment;
import com.macode.paynothing.fragments.accountSettings.EditEmailFragment;
import com.macode.paynothing.fragments.accountSettings.EditLocationFragment;
import com.macode.paynothing.fragments.accountSettings.EditNameFragment;
import com.macode.paynothing.fragments.accountSettings.EditPasswordFragment;
import com.macode.paynothing.fragments.accountSettings.EditPhoneNumberFragment;
import com.macode.paynothing.fragments.accountSettings.EditUsernameFragment;
import com.macode.paynothing.fragments.postItem.PostItemCategoryFragment;
import com.macode.paynothing.fragments.postItem.PostItemLocationFragment;

import org.w3c.dom.Text;

public class AccountSettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView name, editName, username, editUsername, email, editEmail, password, editPassword, phoneNumber, editPhoneNumber, location, editLocation;
    private String nameString, usernameString, emailString, passwordString, phoneNumberString, locationString, providedUserPassword, fragmentId;
    public RelativeLayout main, secondary;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        toolbar = findViewById(R.id.accountSettingsToolbar);
        name = findViewById(R.id.accountSettingsName);
        editName = findViewById(R.id.accountSettingsEditName);
        username = findViewById(R.id.accountSettingsUsername);
        editUsername = findViewById(R.id.accountSettingsEditUsername);
        email = findViewById(R.id.accountSettingsEmail);
        editEmail = findViewById(R.id.accountSettingsEditEmail);
        password = findViewById(R.id.accountSettingsPassword);
        editPassword = findViewById(R.id.accountSettingsEditPassword);
        phoneNumber = findViewById(R.id.accountSettingsPhoneNumber);
        editPhoneNumber = findViewById(R.id.accountSettingsEditPhoneNumber);
        location = findViewById(R.id.accountSettingsLocation);
        editLocation = findViewById(R.id.accountSettingsEditLocation);
        main = findViewById(R.id.accountSettingsMainRelativeLayout);
        secondary = findViewById(R.id.accountSettingsSecondaryRelativeLayout);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadUserAccountSettings();

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.INVISIBLE);
                secondary.setVisibility(View.VISIBLE);
                Fragment editNameFragment = new EditNameFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.accountSettingsFragmentContainer, editNameFragment).commit();
            }
        });

        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.INVISIBLE);
                secondary.setVisibility(View.VISIBLE);
                Fragment editUsernameFragment = new EditUsernameFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.accountSettingsFragmentContainer, editUsernameFragment).commit();
            }
        });

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.INVISIBLE);
                secondary.setVisibility(View.VISIBLE);

                Bundle detailBundle = new Bundle();
                Fragment authenticateUserFragment = new AuthenticateUserFragment();
                fragmentId = "email";
                detailBundle.putString("fragmentId", fragmentId);
                authenticateUserFragment.setArguments(detailBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.accountSettingsFragmentContainer, authenticateUserFragment).commit();
            }
        });

        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.INVISIBLE);
                secondary.setVisibility(View.VISIBLE);

                Bundle detailBundle = new Bundle();
                Fragment authenticateUserFragment = new AuthenticateUserFragment();
                fragmentId = "password";
                detailBundle.putString("fragmentId", fragmentId);
                authenticateUserFragment.setArguments(detailBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.accountSettingsFragmentContainer, authenticateUserFragment).commit();
            }
        });

        editPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.INVISIBLE);
                secondary.setVisibility(View.VISIBLE);
                Fragment editPhoneNumberFragment = new EditPhoneNumberFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.accountSettingsFragmentContainer, editPhoneNumberFragment).commit();
            }
        });

        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.INVISIBLE);
                secondary.setVisibility(View.VISIBLE);
                Fragment editLocationFragment = new EditLocationFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.accountSettingsFragmentContainer, editLocationFragment).commit();
            }
        });
    }

    private void changePassword() {

    }

    private void loadUserAccountSettings() {
        userReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    nameString = String.format("%s %s", snapshot.child("firstName").getValue().toString(), snapshot.child("lastName").getValue().toString());
                    usernameString = snapshot.child("username").getValue().toString();
                    phoneNumberString = snapshot.child("phoneNumber").getValue().toString();
                    locationString = snapshot.child("location").getValue().toString();

                    name.setText(nameString);
                    username.setText(usernameString);
                    phoneNumber.setText(phoneNumberString);
                    location.setText(locationString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        emailString = firebaseUser.getEmail();
        email.setText(emailString);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}