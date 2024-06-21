package com.hani.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hani.chat.R;
import com.hani.chat.model.Message;
import com.hani.chat.model.User;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context context;
    private List<Message> messagesList;
    private String currentUserId;

    public MessageAdapter(Context context, List<Message> messagesList, String currentUserId) {
        this.context = context;
        this.messagesList = messagesList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messagesList.get(position);

        if (message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(context).inflate(R.layout.message_item_right, parent, false);
            return new SentMessageHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.message_item_left, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messagesList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView senderNameTextView;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.textViewMessage);
            senderNameTextView = itemView.findViewById(R.id.senderNameTextView);
        }

        void bind(Message message) {
            messageTextView.setText(message.getText());
            senderNameTextView.setText("Me"); // 자신의 메시지이므로 "Me"로 설정
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView senderNameTextView;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.textViewMessage);
            senderNameTextView = itemView.findViewById(R.id.senderNameTextView);
        }

        void bind(Message message) {
            messageTextView.setText(message.getText());

            // 상대방의 이름 가져오기
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(message.getSenderId());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        senderNameTextView.setText(user.getUserName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Error handling
                }
            });
        }
    }
}