package com.macode.paynothing.fragments.accountSettings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.macode.paynothing.R;

public class EditPhoneNumberFragment extends Fragment {

    private Toolbar toolbar;
    private TextInputLayout editPhoneNumberInput;
    private TextInputEditText editPhoneNumberText;
    private String newPhoneNumberString;
    private RelativeLayout main, secondary;
    private Button editPhoneNumberSaveButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference userReference;

    public EditPhoneNumberFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_phone_number, container, false);

        toolbar = view.findViewById(R.id.editPhoneNumberToolbar);
        editPhoneNumberInput = view.findViewById(R.id.editPhoneNumberInput);
        editPhoneNumberText = view.findViewById(R.id.editPhoneNumberText);
        main = requireActivity().findViewById(R.id.accountSettingsMainRelativeLayout);
        secondary = requireActivity().findViewById(R.id.accountSettingsSecondaryRelativeLayout);
        editPhoneNumberSaveButton = view.findViewById(R.id.editPhoneNumberSaveButton);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Change Phone Number");
        setHasOptionsMenu(true);

        editPhoneNumberText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        editPhoneNumberSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditedPhoneNumber();
            }
        });

        return view;
    }

    private void saveEditedPhoneNumber() {
        newPhoneNumberString = editPhoneNumberInput.getEditText().getText().toString();

        if (newPhoneNumberString.isEmpty() || newPhoneNumberString.length() < 12) {
            showError(editPhoneNumberInput, "Please provide a valid phone number!");
        } else {
            userReference.child(firebaseUser.getUid()).child("phoneNumber").setValue(newPhoneNumberString).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void showError(TextInputLayout input, String string) {
        input.setError(string);
        input.requestFocus();
    }
}