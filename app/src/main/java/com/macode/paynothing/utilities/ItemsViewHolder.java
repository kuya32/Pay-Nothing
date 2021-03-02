package com.macode.paynothing.utilities;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.macode.paynothing.R;

public class ItemsViewHolder extends RecyclerView.ViewHolder{

    public ImageView itemImageView;

    public ItemsViewHolder(@NonNull View itemView) {
        super(itemView);

        itemImageView = itemView.findViewById(R.id.feedItemImageView);
    }
}
