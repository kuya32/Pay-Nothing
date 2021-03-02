package com.macode.paynothing.fragments.postItem;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputLayout;
import com.macode.paynothing.R;

public class PostItemCategoryFragment extends Fragment {

    private Toolbar postToolbar;
    private Spinner categorySpinner, conditionSpinner;
    private TextInputLayout postItemBrand, postItemModel, postItemType, postItemDescription;
    private String postImageString, postTitleString, postCategoryString, postConditionString, postBrandString, postModelString, postTypeString, postDescriptionString;
    private Button postItemCategoryNextButton;

    public PostItemCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_item_category, container, false);

        postToolbar = view.findViewById(R.id.postItemCatToolbar);
        categorySpinner = view.findViewById(R.id.postCategorySpinner);
        conditionSpinner = view.findViewById(R.id.postConditionSpinner);
        postItemBrand = view.findViewById(R.id.postItemBrandInput);
        postItemModel = view.findViewById(R.id.postItemModelInput);
        postItemType = view.findViewById(R.id.postItemTypeInput);
        postItemDescription = view.findViewById(R.id.postItemDescriptionInput);
        postItemCategoryNextButton = view.findViewById(R.id.postItemCatNextButton);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(postToolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Post Details");
        setHasOptionsMenu(true);

        addingCategorySpinner();
        addingConditionSpinner();

        detailFragmentRetriever();

        postItemCategoryNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailFragmentPasser();
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
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.postItemFragmentContainer, new PostItemImageFragment()).commit();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addingCategorySpinner() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.categories));
        categoryAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                postCategoryString = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void addingConditionSpinner() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.condition));
        categoryAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        conditionSpinner.setAdapter(categoryAdapter);

        conditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                postConditionString = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void detailFragmentPasser() {
        postBrandString = postItemBrand.getEditText().getText().toString();
        postModelString = postItemModel.getEditText().getText().toString();
        postTypeString = postItemType.getEditText().getText().toString();
        postDescriptionString = postItemDescription.getEditText().getText().toString();
        Bundle detailBundle = new Bundle();
        detailBundle.putString("title", postTitleString);
        detailBundle.putString("image", postImageString);
        detailBundle.putString("category", postCategoryString);
        detailBundle.putString("condition", postConditionString);
        detailBundle.putString("brand", postBrandString);
        detailBundle.putString("model", postModelString);
        detailBundle.putString("type", postTypeString);
        detailBundle.putString("description", postDescriptionString);
        Fragment locationFragment = new PostItemLocationFragment();
        locationFragment.setArguments(detailBundle);
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.postItemFragmentContainer, locationFragment).commit();
    }

    public void detailFragmentRetriever() {
        Bundle detailRetrieveBundle = this.getArguments();
        if (detailRetrieveBundle != null) {
            postTitleString = detailRetrieveBundle.getString("title");
        }
        if (detailRetrieveBundle != null) {
            postImageString = detailRetrieveBundle.getString("image");
        }
    }


}