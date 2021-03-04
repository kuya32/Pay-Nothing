package com.macode.paynothing.notifications;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference tokenReference;
    private String refreshToken;
    private Token token;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        refreshToken = FirebaseInstanceId.getInstance().getToken();
        if (firebaseUser != null) {
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        tokenReference = FirebaseDatabase.getInstance().getReference().child("Tokens");
        token = new Token(refreshToken);
        tokenReference.child(firebaseUser.getUid()).setValue(token);
    }
}
