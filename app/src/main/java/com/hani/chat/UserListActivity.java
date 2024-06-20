package com.hani.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hani.chat.adapter.UserAdapter;
import com.hani.chat.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {


    private ListView listViewUsers;
    private List<User> userList = new ArrayList<>();
    private UserAdapter adapter;
    private DatabaseReference databaseUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);



        listViewUsers = findViewById(R.id.listViewUsers);
        adapter = new UserAdapter(this, userList);
        listViewUsers.setAdapter(adapter);

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < userList.size()) {
                    User selectedUser = userList.get(position);
                    Intent intent = new Intent(UserListActivity.this, MainActivity.class);
                    intent.putExtra("selectedUserId", selectedUser.getUserId());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UserListActivity.this, "Invalid user selection", Toast.LENGTH_SHORT).show();
                }
            }
        });


        databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    userList.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserListActivity.this, "Failed to load users.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
