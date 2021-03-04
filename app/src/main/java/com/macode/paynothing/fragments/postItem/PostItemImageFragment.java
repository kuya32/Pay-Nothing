package com.macode.paynothing.fragments.postItem;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.macode.paynothing.activities.PostActivity;
import com.macode.paynothing.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PostItemImageFragment extends Fragment {

    private Toolbar postToolBar;
    private int SELECT_PHOTO = 1, RESULT_OK = -1;
    private Uri uri;
    private TextView postItemImageAlert;
    private ImageView postItemImageView;
    private Button takePhotoButton, selectPhotoButton, postItemImageNextButton;
    private TextInputLayout postItemTitle;
    private String postItemImageString, postItemTitleString;

    public PostItemImageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_item_image, container, false);

        postToolBar = view.findViewById(R.id.postItemImageToolbar);
        postItemTitle = view.findViewById(R.id.postItemTitleInput);
        postItemImageAlert = view.findViewById(R.id.postItemImageAlert);
        postItemImageView = view.findViewById(R.id.postItemImage);
        takePhotoButton = view.findViewById(R.id.takePhotoButton);
        selectPhotoButton = view.findViewById(R.id.selectPhotoButton);
        postItemImageNextButton = view.findViewById(R.id.postItemImageNextButton);

        postToolBar.setTitle("Post an Item");
        ((AppCompatActivity) requireActivity()).setSupportActionBar(postToolBar);
        setHasOptionsMenu(true);

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[] {
                            Manifest.permission.CAMERA
                    },
                    100);
        }

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);
            }
        });

        selectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PHOTO);
            }
        });

        postItemImageNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postItemTitleString = postItemTitle.getEditText().getText().toString();
                if (postItemTitleString.isEmpty()) {
                    postItemImageAlert.setVisibility(View.INVISIBLE);
                    showError(postItemTitle, "Title Required!");
                } else if (postItemImageView.getDrawable() == null) {
                    postItemImageAlert.setVisibility(View.VISIBLE);
                    postItemImageAlert.setError("Image Required! ");
                    postItemImageAlert.setHint("Image Required! ");
                } else {
                    postItemImageAlert.setVisibility(View.INVISIBLE);
                    imageFragmentPasser();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.post_item_top_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cancel) {
            requireActivity().onBackPressed();
        } else if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            postItemImageString = bitMapToString(captureImage);
            postItemImageView.setImageBitmap(captureImage);
        } else if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                Context applicationContext = PostActivity.getContextOfApplication();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), uri);
                postItemImageString = bitMapToString(bitmap);
                postItemImageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void imageFragmentPasser() {
        Bundle imageBundle = new Bundle();
        postItemTitleString = postItemTitle.getEditText().getText().toString();
        imageBundle.putString("title", postItemTitleString);
        imageBundle.putString("image", postItemImageString);
        Fragment categoryFragment = new PostItemCategoryFragment();
        categoryFragment.setArguments(imageBundle);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.postItemFragmentContainer, categoryFragment).commit();
    }

    public String bitMapToString(Bitmap bitmap){
        ByteArrayOutputStream bit = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, bit);
        byte[] b = bit.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private void showError(TextInputLayout layout, String text) {
        layout.setError(text);
        layout.requestFocus();
    }
}