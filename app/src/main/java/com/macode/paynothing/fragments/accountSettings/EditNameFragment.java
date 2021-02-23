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


public class EditNameFragment extends Fragment {

    private Toolbar toolbar;
    private TextInputLayout editNameInput;
    private RelativeLayout main, secondary;
    private String nameString, newNameString, newNameLastNameString;
    private Button editNameSaveButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference;

    public EditNameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_name, container, false);

        toolbar = view.findViewById(R.id.editNameToolbar);
        editNameInput = view.findViewById(R.id.editNameInput);
        editNameSaveButton = view.findViewById(R.id.editNameSaveButton);
        main = requireActivity().findViewById(R.id.accountSettingsMainRelativeLayout);
        secondary = requireActivity().findViewById(R.id.accountSettingsSecondaryRelativeLayout);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Enter Name");
        setHasOptionsMenu(true);

        loadUserName();

        editNameSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditedName();
            }
        });

        return view;
    }

    private void saveEditedName() {
        newNameString = editNameInput.getEditText().getText().toString();
        newNameLastNameString = newNameString.substring(newNameString.indexOf(" ") + 1);

        if (!newNameString.contains(" ") || newNameString.isEmpty() || newNameString.length() < 3) {
            showError(editNameInput, "Please provide first and last name!");
        } else {
            userReference.child(firebaseUser.getUid()).child("firstName").setValue(newNameString.substring(0, newNameString.indexOf(" "))).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        userReference.child(firebaseUser.getUid()).child("lastName").setValue(newNameLastNameString).addOnCompleteListener(new OnCompleteListener<Void>() {
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
            });
        }
    }

    private void loadUserName() {
        userReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    nameString = String.format("%s %s", snapshot.child("firstName").getValue().toString(), snapshot.child("lastName").getValue().toString());

                    editNameInput.getEditText().setText(nameString);
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

    private void showError(TextInputLayout input, String string) {
        input.setError(string);
        input.requestFocus();
    }
}