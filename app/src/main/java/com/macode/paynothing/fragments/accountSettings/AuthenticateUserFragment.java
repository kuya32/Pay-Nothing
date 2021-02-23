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

public class AuthenticateUserFragment extends Fragment {

    private Toolbar toolbar;
    private TextInputLayout emailInput, passwordInput;
    private RelativeLayout main, secondary;
    private String emailString, passwordString, fragmentId;
    private Button authorizeButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference;
    private AuthCredential authCredential;

    public AuthenticateUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authenticate_user, container, false);

        toolbar = view.findViewById(R.id.authenticationToolbar);
        emailInput = view.findViewById(R.id.authenticationEmailInput);
        passwordInput = view.findViewById(R.id.authenticationPasswordInput);
        main = requireActivity().findViewById(R.id.mainLocationRelativeLayout);
        secondary = requireActivity().findViewById(R.id.accountSettingsSecondaryRelativeLayout);
        authorizeButton = view.findViewById(R.id.authorizeButton);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("User Account Authentication");
        setHasOptionsMenu(true);

        authorizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser();
            }
        });

        return view;
    }

    private void authenticateUser() {
        Bundle authenticationRetrieveBundle = this.getArguments();
        fragmentId = authenticationRetrieveBundle.getString("fragmentId");

        emailString = emailInput.getEditText().getText().toString();
        passwordString = passwordInput.getEditText().getText().toString();
        if (emailString.isEmpty()) {
            showError(emailInput, "Email is not valid!");
        } else if (passwordString.isEmpty() || passwordString.length() < 6) {
            showError(passwordInput, "Password must be greater than 6 characters!");
        } else {
            authCredential = EmailAuthProvider.getCredential(emailString, passwordString);
            firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful() && fragmentId.equals("email")) {
                        Fragment editEmailFragment = new EditEmailFragment();
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.accountSettingsFragmentContainer, editEmailFragment).commit();
                    } else if (task.isSuccessful() && fragmentId.equals("password")) {
                        Fragment editPasswordFragment = new EditPasswordFragment();
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.accountSettingsFragmentContainer, editPasswordFragment).commit();
                    } else {
                        showError(emailInput, "Email or password is incorrect!");
                        showError(passwordInput, "Email or password is incorrect!");
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