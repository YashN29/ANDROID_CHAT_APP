package com.example.bingbong.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bingbong.Activities.ChatScreen;
import com.example.bingbong.Models.User_Model;
import com.example.bingbong.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat_Adapter extends RecyclerView.Adapter<Chat_Adapter.MyClass> {

    Context context;
    List<User_Model> userModels;

    public Chat_Adapter() {
    }

    public Chat_Adapter(Context context, List<User_Model> userModels) {
        this.context = context;
        this.userModels = userModels;
    }

    @NonNull
    @Override
    public Chat_Adapter.MyClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.chats_row_layout,parent,false);
        return new MyClass(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Chat_Adapter.MyClass holder, @SuppressLint("RecyclerView") int position) {

        User_Model user = userModels.get(position);

        String senderid = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderid + user.getUid();

        FirebaseDatabase.getInstance().getReference()
                        .child("Chats")
                                .child(senderRoom)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    String lastmsg = snapshot.child("lastmsg").getValue(String.class);
                                                    Long time = snapshot.child("lastmsgTime").getValue(Long.class);
                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

                                                    holder.recent_msg.setText(lastmsg);
                                                    holder.tv_timeshow.setText(dateFormat.format(new Date(time)));
                                                }else {
                                                    holder.recent_msg.setText("Tap to chat");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

        holder.tv_show_name.setText(user.getName());

        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.profile)
                .into(holder.circular_img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ChatScreen.class);
                i.putExtra("uid",user.getUid());
                i.putExtra("name",user.getName());
                i.putExtra("image",user.getProfileImage());
                i.putExtra("token", user.getToken());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public class MyClass extends RecyclerView.ViewHolder {

        CircleImageView circular_img;
        TextView tv_show_name,recent_msg,tv_timeshow;

        public MyClass(@NonNull View itemView) {
            super(itemView);

            circular_img = itemView.findViewById(R.id.circular_img);
            tv_show_name = itemView.findViewById(R.id.tv_show_name);
            recent_msg = itemView.findViewById(R.id.recent_msg);
            tv_timeshow = itemView.findViewById(R.id.tv_timeshow);
        }
    }
}
