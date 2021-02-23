package com.macode.paynothing.fragments.accountSettings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macode.paynothing.AccountSettingsActivity;
import com.macode.paynothing.R;

public class EditUsernameFragment extends Fragment {

    private Toolbar toolbar;
    private TextInputLayout editUsernameInput;
    private RelativeLayout main, secondary;
    private String usernameString, newUsernameString;
    private Button editUsernameSaveButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference;

    public EditUsernameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_username, container, false);
        toolbar = view.findViewById(R.id.editUsernameToolbar);
        editUsernameInput = view.findViewById(R.id.editUsernameInput);
        editUsernameSaveButton = view.findViewById(R.id.editUsernameSaveButton);
        main = requireActivity().findViewById(R.id.accountSettingsMainRelativeLayout);
        secondary = requireActivity().findViewById(R.id.accountSettingsSecondaryRelativeLayout);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Enter Username");
        setHasOptionsMenu(true);

        loadUserUsername();

        editUsernameSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditedUsername();
            }
        });

        return view;
    }

    private void saveEditedUsername() {
        newUsernameString = editUsernameInput.getEditText().getText().toString();
        userReference.child(firebaseUser.getUid()).child("username").setValue(newUsernameString).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    requireActivity().finish();
                    startActivity(requireActivity().getIntent());
                }
            }
        });
    }

    private void loadUserUsername() {
        userReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    usernameString = snapshot.child("username").getValue().toString();

                    editUsernameInput.getEditText().setText(usernameString);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
}