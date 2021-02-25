package com.macode.paynothing.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.macode.paynothing.EditItemActivity;
import com.macode.paynothing.R;

public class EditMultipleItemPropertiesFragment extends Fragment {

    private RelativeLayout main, secondary, mainLayout, locationLayout;
    private CardView categoryCardView, conditionCardView, descriptionCardView, pickUpOnlyCardView;
    private String itemKey, itemPropertyKey, categoryString, conditionString, descriptionString, locationString, latString, longString;
    private TextInputLayout descriptionInput;
    private EditText locationZipCode;
    private TextView cityAndState;
    private Boolean pickUpOnly;
    private Spinner categorySpinner, conditionSpinner;
    private Button categorySaveButton, conditionSaveButton, descriptionSaveButton, getLocationButton, locationSaveButton, pickUpOnlyYesButton, pickUpOnlyNoButton;

    public EditMultipleItemPropertiesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_multiple_item_properties, container, false);
        main = requireActivity().findViewById(R.id.editItemMainRelativeLayout);
        secondary = requireActivity().findViewById(R.id.editItemSecondaryRelativeLayout);
        mainLayout = view.findViewById(R.id.editingItemMainRelativeLayout);
        locationLayout = view.findViewById(R.id.editingItemGetLocationRelativeLayout);
        categoryCardView = view.findViewById(R.id.editingCategoryCardView);
        conditionCardView = view.findViewById(R.id.editingConditionCardView);
        descriptionCardView = view.findViewById(R.id.editingDescriptionCardView);
        pickUpOnlyCardView = view.findViewById(R.id.editingPickUpOnlyCardView);
        descriptionInput = view.findViewById(R.id.editingDescriptionInput);
        locationZipCode = view.findViewById(R.id.editingItemZipCode);
        cityAndState = view.findViewById(R.id.editingLocationCityAndState);
        categorySpinner = view.findViewById(R.id.editingCategorySpinner);
        conditionSpinner = view.findViewById(R.id.editingConditionSpinner);
        categorySaveButton = view.findViewById(R.id.editingCategorySaveButton);
        conditionSaveButton = view.findViewById(R.id.editingConditionSaveButton);
        descriptionSaveButton = view.findViewById(R.id.editingDescriptionSaveButton);
        getLocationButton = view.findViewById(R.id.editingItemGetLocationButton);
        locationSaveButton = view.findViewById(R.id.editingLocationSaveCityAndStateButton);
        pickUpOnlyYesButton = view.findViewById(R.id.editingPickUpOnlyYesButton);
        pickUpOnlyNoButton = view.findViewById(R.id.editingPickUpOnlyNoButton);

        addingCategorySpinner();
        addingConditionSpinner();

        retrieveItemPropertyDecider();

        return view;
    }

    private void retrieveItemPropertyDecider() {
        Bundle itemPropertyRetrievalBundle = this.getArguments();
        itemKey = itemPropertyRetrievalBundle.getString("itemKey");
        itemPropertyKey = itemPropertyRetrievalBundle.getString("itemPropertyKey");

        switch (itemPropertyKey) {
            case "category":
                mainLayout.setVisibility(View.VISIBLE);
                categoryCardView.setVisibility(View.VISIBLE);
                break;
            case "condition":
                mainLayout.setVisibility(View.VISIBLE);
                conditionCardView.setVisibility(View.VISIBLE);
                break;
            case "description":
                mainLayout.setVisibility(View.VISIBLE);
                descriptionCardView.setVisibility(View.VISIBLE);
                break;
            case "location":
                locationLayout.setVisibility(View.VISIBLE);
                break;
            case "pickUpOnly":
                mainLayout.setVisibility(View.VISIBLE);
                pickUpOnlyCardView.setVisibility(View.VISIBLE);
                break;
            default:
                Toast.makeText(requireActivity(), "This is impossibruuuuu!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void addingCategorySpinner() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.categories));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryString = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void addingConditionSpinner() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.condition));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        conditionSpinner.setAdapter(categoryAdapter);

        conditionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                conditionString = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}