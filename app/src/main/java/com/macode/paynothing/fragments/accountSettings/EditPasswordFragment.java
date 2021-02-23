package com.macode.paynothing.fragments.accountSettings;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.macode.paynothing.R;

public class EditPasswordFragment extends Fragment {

    private Toolbar toolbar;
    private TextInputLayout editCurrentPasswordInput, editNewPasswordInput, confirmEditNewPasswordInput;
    private RelativeLayout main, secondary;
    private String currentPasswordString, newPasswordString, confirmedNewPasswordString;
    private Button editPasswordSaveButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference;
    private AuthCredential authCredential;

    public EditPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_password, container, false);

        toolbar = view.findViewById(R.id.editPasswordToolbar);
        editCurrentPasswordInput = view.findViewById(R.id.editCurrentPasswordInput);
        editNewPasswordInput = view.findViewById(R.id.editNewPasswordInput);
        editPasswordSaveButton = view.findViewById(R.id.editPasswordSaveButton);
        confirmEditNewPasswordInput = view.findViewById(R.id.confirmEditNewPasswordInput);
        main = requireActivity().findViewById(R.id.accountSettingsMainRelativeLayout);
        secondary = requireActivity().findViewById(R.id.accountSettingsSecondaryRelativeLayout);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Change Password");
        setHasOptionsMenu(true);

        editPasswordSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditedPassword();
            }
        });

        return view;
    }

    private void saveEditedPassword() {
        currentPasswordString = editCurrentPasswordInput.getEditText().getText().toString();
        newPasswordString = editNewPasswordInput.getEditText().getText().toString();
        confirmedNewPasswordString = confirmEditNewPasswordInput.getEditText().getText().toString();

        if (currentPasswordString.isEmpty()) {
            showError(editCurrentPasswordInput, "Current password required!");
        } else if (currentPasswordString.equals(newPasswordString)) {
            showError(editNewPasswordInput, "New password should be different from the current password!");
        } else if (newPasswordString.isEmpty() || newPasswordString.length() < 6) {
            showError(editNewPasswordInput, "Password must be greater than 6 characters!");
        } else if (!confirmedNewPasswordString.equals(newPasswordString)) {
            showError(confirmEditNewPasswordInput, "New password does not match!");
        } else {
            authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), currentPasswordString);
            firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        firebaseUser.updatePassword(newPasswordString).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(requireActivity(), "Password updated!", Toast.LENGTH_SHORT).show();
                                    requireActivity().finish();
                                    startActivity(requireActivity().getIntent());
                                } else {
                                    Toast.makeText(requireActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        showError(editCurrentPasswordInput, "Provided wrong current password!");
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

    private void showError(TextInputLayout input, String string) {
        input.setError(string);
        input.requestFocus();
    }
}