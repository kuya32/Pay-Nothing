package com.macode.paynothing.utilities;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.macode.paynothing.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class InboxChatsViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView sellerBuyerProfileImage;
    public TextView sellerBuyerUsername, sellerBuyerStatus, mostRecentMessage;
    public ImageView sellerBuyerStatusImage, itemImage;
    public CardView sellerBuyerCardView;

    public InboxChatsViewHolder(@NonNull View itemView) {
        super(itemView);

        sellerBuyerProfileImage = itemView.findViewById(R.id.sellerBuyerImageView);
        sellerBuyerUsername = itemView.findViewById(R.id.sellerBuyerUsername);
        sellerBuyerStatus = itemView.findViewById(R.id.sellerBuyerStatus);
        mostRecentMessage = itemView.findViewById(R.id.sellerBuyerMostRecentMessage);
        sellerBuyerStatusImage = itemView.findViewById(R.id.sellerBuyerStatusImage);
        itemImage = itemView.findViewById(R.id.sellerBuyerItemImage);
        sellerBuyerCardView = itemView.findViewById(R.id.sellerBuyerCardView);
    }
}
