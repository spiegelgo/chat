package com.hani.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hani.chat.adapter.MessageAdapter;
import com.hani.chat.model.Message;


import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private EditText editTextMessage;
    private Button buttonSend;
    private ListView listViewMessages;

    private DatabaseReference databaseMessages;
    private ArrayList<Message> messagesList;
    private MessageAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    String selectedUserId;
    String userId;
    String receiverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        listViewMessages = findViewById(R.id.listViewMessages);

        // Firebase 인증 및 데이터베이스 초기화
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // 사용자가 로그인되어 있지 않은 경우, 로그인 화면으로 이동
            Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // ChatActivity를 종료하고 LoginActivity로 이동
            return; // 추가 코드 실행 방지
        }
        userId = currentUser.getUid();
        selectedUserId = getIntent().getStringExtra("selectedUserId");
        receiverId = selectedUserId;

        databaseMessages = FirebaseDatabase.getInstance().getReference("messages");

        // 메시지 리스트 초기화 및 어댑터 설정
        messagesList = new ArrayList<>();
        adapter = new MessageAdapter(this, messagesList, userId);
        listViewMessages.setAdapter(adapter);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // onDataChange 메서드에서 메시지 필터링 및 화면에 표시할 방향 설정
        databaseMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Message message = postSnapshot.getValue(Message.class);
                    if (message != null && message.getSenderId() != null && message.getReceiverId() != null) {
                        if ((message.getSenderId().equals(userId) && message.getReceiverId().equals(receiverId)) ||
                                (message.getSenderId().equals(receiverId) && message.getReceiverId().equals(userId))) {
                            messagesList.add(message);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                // 스크롤을 맨 아래로 이동하려면 필요한 코드를 여기에 추가할 수 있습니다.
                listViewMessages.smoothScrollToPosition(messagesList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(message)) {
            // Firebase Database에 메시지를 보내는 코드
            String messageId = databaseMessages.push().getKey();
            Message newMessage = new Message(message, userId, receiverId);
            if (messageId != null) {
                databaseMessages.child(messageId).setValue(newMessage);
                editTextMessage.setText("");
            }
        } else {
            Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show();
        }
    }
}