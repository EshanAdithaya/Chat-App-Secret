package com.example.ichat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatsRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = FirebaseFirestore.getInstance();

        ImageView settingsIcon = findViewById(R.id.settings_icon);
        settingsIcon.setOnClickListener(v -> {
            showSettingsDialog();
        });

        chatsRecyclerView = findViewById(R.id.chats_recycler_view);
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList);
        chatsRecyclerView.setAdapter(chatAdapter);

      

        findViewById(R.id.find_user_button).setOnClickListener(v -> showFindUserDialog());

        loadChats();
    }

    private void loadChats() {
        // Load previous chats from Firestore
        db.collection("chats")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        chatList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ChatMessage chat = document.toObject(ChatMessage.class);
                            chatList.add(chat);
                        }
                        chatAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ChatActivity.this, "Failed to load chats.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Settings");

        // Add logout option
        builder.setItems(new CharSequence[]{"Logout"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle logout action
                logoutUser();
            }
        });

        builder.show();
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(ChatActivity.this, LoginActivity.class));
        finish();
    }

    private void showFindUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Find User by Email");

        final EditText input = new EditText(this);
        input.setHint("Enter email");
        builder.setView(input);

        builder.setPositiveButton("Find", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (!email.isEmpty()) {
                findUserByEmail(email);
            } else {
                Toast.makeText(ChatActivity.this, "Please enter an email.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void findUserByEmail(String email) {
        // Find user by email in Firestore
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            // Start chat with the found user
                            startChatWithUser(user);
                        }
                    } else {
                        Toast.makeText(ChatActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startChatWithUser(User user) {
        // Implement chat initiation logic
        Toast.makeText(this, "Starting chat with " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
    }
}
