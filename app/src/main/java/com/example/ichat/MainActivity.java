// MainActivity.java
package com.example.ichat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private EditText etMessage;
    private Button btnSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // User is not logged in, redirect to LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            // User is logged in, load ChatActivity
            startActivity(new Intent(MainActivity.this, ChatActivity.class));
            finish();
        }
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recycler_view);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        fetchMessages();
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(messageText)) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                String username = user.getDisplayName();
                String userPhoto = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "";

                ChatMessage chatMessage = new ChatMessage(userId, username, userPhoto, messageText);

                db.collection("messages").add(chatMessage)
                        .addOnSuccessListener(documentReference -> etMessage.setText(""))
                        .addOnFailureListener(e -> {
                            // Handle failure
                        });
            }
        }
    }

    private void fetchMessages() {
        db.collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        if (snapshots != null) {
                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                DocumentSnapshot document = dc.getDocument();
                                ChatMessage message = document.toObject(ChatMessage.class);
                                switch (dc.getType()) {
                                    case ADDED:
                                        messageList.add(message);
                                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                                        recyclerView.scrollToPosition(messageList.size() - 1);
                                        break;
                                    case MODIFIED:
                                        // Handle message modification if needed
                                        break;
                                    case REMOVED:
                                        // Handle message removal if needed
                                        break;
                                }
                            }
                        }
                    }
                });
    }
}
