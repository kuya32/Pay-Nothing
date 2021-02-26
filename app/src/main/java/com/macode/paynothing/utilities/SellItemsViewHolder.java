package com.macode.paynothing.utilities;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.macode.paynothing.R;

public class SellItemsViewHolder extends RecyclerView.ViewHolder{

    public ImageView itemImageView;
    public TextView itemTitle, editItem, shareItem, deleteItem;
    public Button markAsSoldButton, soldButton;
    public CardView sellingItemCardView;

    public SellItemsViewHolder(@NonNull View itemView) {
        super(itemView);

        itemImageView = itemView.findViewById(R.id.sellingItemImageView);
        itemTitle = itemView.findViewById(R.id.sellingItemTitle);
        editItem = itemView.findViewById(R.id.sellingEditItem);
        shareItem = itemView.findViewById(R.id.sellingShareItem);
        deleteItem = itemView.findViewById(R.id.sellingDeleteItem);
        markAsSoldButton = itemView.findViewById(R.id.markedAsSold);
        soldButton = itemView.findViewById(R.id.sold);
        sellingItemCardView = itemView.findViewById(R.id.sellingItemCardView);
    }
}
