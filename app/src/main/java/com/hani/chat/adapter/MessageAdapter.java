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

import com.google.firebase.auth.FirebaseAuth;
import com.hani.chat.R;
import com.hani.chat.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message>{
    private Context context;
    private List<Message> messages;
    private String currentUserId;

    public MessageAdapter(Context context, List<Message> messages, String currentUserId) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
        }

        Message message = getItem(position);

        TextView textViewMessage = convertView.findViewById(R.id.textViewMessage);
        TextView textViewUser = convertView.findViewById(R.id.textViewUser);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 메시지 보낸 사람이 현재 사용자인 경우
        if (message.getSenderId().equals(currentUserId)) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item_right, parent, false);
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item_left, parent, false);
        }

        textViewMessage = convertView.findViewById(R.id.textViewMessage);
        textViewUser = convertView.findViewById(R.id.textViewUser);

        textViewMessage.setText(message.getText());
        // textViewUser.setText(message.getSenderId()); // senderId 표시는 생략하는 것으로 가정

        return convertView;
    }
}