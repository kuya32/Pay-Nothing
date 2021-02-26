package com.macode.paynothing.utilities;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.macode.paynothing.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView sellersProfileImage, usersProfileImage;
    public TextView sellersMessage, usersMessage, sellersMessageTimeAgo, usersMessageTimeAgo;

    public ChatsViewHolder(@NonNull View itemView) {
        super(itemView);

        sellersProfileImage = itemView.findViewById(R.id.sellersProfileImage);
        usersProfileImage = itemView.findViewById(R.id.usersProfileImage);
        sellersMessage = itemView.findViewById(R.id.sellersMessage);
        usersMessage = itemView.findViewById(R.id.usersMessage);
        sellersMessageTimeAgo = itemView.findViewById(R.id.sellersMessageTimeAgo);
        usersMessageTimeAgo = itemView.findViewById(R.id.usersMessageTimeAgo);
    }
}
