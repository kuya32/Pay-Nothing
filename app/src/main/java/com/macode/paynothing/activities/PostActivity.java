package com.macode.paynothing.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;

import com.macode.paynothing.R;
import com.macode.paynothing.fragments.postItem.PostItemImageFragment;

public class PostActivity extends AppCompatActivity {

    public static Context contextOfApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Fragment fragment = new PostItemImageFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.postItemFragmentContainer, fragment).commit();
        contextOfApplication = getApplicationContext();
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }
}