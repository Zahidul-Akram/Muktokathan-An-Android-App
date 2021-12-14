package com.example.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.Adapter.MessagesAdapter;
import com.example.chatapp.ModelClass.Messages;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    String reciverimg,reciveruid,recivername,senderuid;
    TextView recivername1,recivername2;
    CircleImageView profileimage;
    FirebaseDatabase database;
    FirebaseAuth auth;
    public static String simage,rimage;
    CardView send_btn;
    EditText editmsg;
    String senderroom,reciverroom;
    RecyclerView msgadapter;
    ArrayList<Messages> messagesArrayList;
    MessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        recivername=getIntent().getStringExtra("name");
        reciverimg=getIntent().getStringExtra("reciverimg");
        reciveruid=getIntent().getStringExtra("uid");
        messagesArrayList=new ArrayList<>();
        profileimage=findViewById(R.id.profile_image);
        send_btn=findViewById(R.id.send_btn);
        editmsg=findViewById(R.id.editmsg);
        msgadapter=findViewById(R.id.msgadapter);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        msgadapter.setLayoutManager(linearLayoutManager);
        adapter=new MessagesAdapter(ChatActivity.this,messagesArrayList);
        msgadapter.setAdapter(adapter);
        senderuid=auth.getUid();
        senderroom=senderuid+reciveruid;
        reciverroom=reciveruid+senderuid;

        Picasso.get().load(reciverimg).into(profileimage);

        recivername1=findViewById(R.id.recivername1);
        recivername2=findViewById(R.id.recivername2);
        recivername1.setText(""+recivername);
        recivername2.setText(""+recivername);

        DatabaseReference reference= database.getReference().child("user").child(auth.getUid());
        DatabaseReference chatreference= database.getReference().child("chats").child(senderroom).child("messages");

        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Messages messages=dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                simage=snapshot.child("imageUri").getValue().toString();
                rimage=reciverimg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=editmsg.getText().toString();
                if(message.isEmpty())
                {
                    Toast.makeText(ChatActivity.this,"Enter Valid Message",Toast.LENGTH_SHORT).show();
                    return;

                }
               editmsg.setText("");
                Date date=new Date();
                Messages messages=new Messages(message,senderuid, date.getTime());
                database=FirebaseDatabase.getInstance();
                database.getReference().child("chats")
                        .child(senderroom)
                        .child("messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("chats")
                                .child(reciverroom)
                                .child("messages")
                                .push()
                                .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                    }
                });

            }
        });


    }
}