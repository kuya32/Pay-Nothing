package com.macode.paynothing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private String email, password, confirmPassword;
    private TextInputLayout inputEmail, inputPassword, inputConfirmPassword;
    private Button signUpButton;
    private TextView alreadyHaveAnAccount;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        inputEmail = findViewById(R.id.signUpInputEmail);
        inputPassword = findViewById(R.id.signUpInputPassword);
        inputConfirmPassword = findViewById(R.id.signUpConfirmPasswordInput);
        signUpButton = findViewById(R.id.signUpButton);
        alreadyHaveAnAccount = findViewById(R.id.alreadyHaveAnAccountText);
        firebaseAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpNewUser();
            }
        });

        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void signUpNewUser() {
        email = inputEmail.getEditText().getText().toString();
        password = inputPassword.getEditText().getText().toString();
        confirmPassword = inputConfirmPassword.getEditText().getText().toString();

        if (email.isEmpty()) {
            showError(inputEmail, "Email is not valid!");
        } else if (password.isEmpty() || password.length() < 6) {
            showError(inputPassword, "Password must be greater than 6 characters!");
        } else if (!confirmPassword.equals(password)) {
            showError(inputConfirmPassword, "Password did not match!");
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        firebaseAuth.getCurrentUser().sendEmailVerification();
                        Toast.makeText(SignUpActivity.this, "SignUp was successful! Check your email!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void showError(TextInputLayout layout, String text) {
        layout.setError(text);
        layout.requestFocus();
    }
}