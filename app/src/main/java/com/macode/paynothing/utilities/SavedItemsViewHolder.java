package com.macode.paynothing.utilities;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.macode.paynothing.R;

public class SavedItemsViewHolder extends RecyclerView.ViewHolder{

    public ImageView savedItemImageView;
    public TextView savedItemTitle, dateSavedItem;
    public CardView savedItemCardView;

    public SavedItemsViewHolder(@NonNull View itemView) {
        super(itemView);

        savedItemImageView = itemView.findViewById(R.id.savedItemImageView);
        savedItemTitle = itemView.findViewById(R.id.savedItemTitle);
        dateSavedItem = itemView.findViewById(R.id.dateSavedItem);
        savedItemCardView = itemView.findViewById(R.id.savedItemCardView);
    }
}
