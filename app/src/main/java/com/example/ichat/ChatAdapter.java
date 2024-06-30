// ChatAdapter.java
package com.example.ichat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> chatMessageList;

    public ChatAdapter(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessageList.get(position);
        holder.userName.setText(chatMessage.getUsername());
        holder.messageText.setText(chatMessage.getMessage());
        Glide.with(holder.userImage.getContext()).load(chatMessage.getUserPhoto()).into(holder.userImage);
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView userName, messageText;
        ImageView userImage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            messageText = itemView.findViewById(R.id.message_text);
            userImage = itemView.findViewById(R.id.user_image);
        }
    }
}
