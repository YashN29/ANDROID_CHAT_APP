package com.example.bingbong.Adapters;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.bingbong.Models.Chat;
import com.example.bingbong.Models.User_Model;
import com.example.bingbong.R;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Adapter_Message extends RecyclerView.Adapter<Adapter_Message.Myclass> {

    Context context;
    static final int ITEM_SENT = 0;
    static final int ITEM_RECIEVE = 1;

    String senderRoom;
    String recieverRoom;

    ArrayList<Chat> chatList;
    FirebaseUser firebaseUser;


    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;


    public Adapter_Message(Context context, ArrayList<Chat> chatList, String senderRoom, String recieverRoom) {
        this.context = context;
        this.chatList = chatList;
        this.senderRoom = senderRoom;
        this.recieverRoom = recieverRoom;

    }

    @NonNull
    @Override
    public Adapter_Message.Myclass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_SENT)
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.iteam_sent,parent,false);
            return new Myclass(v);
        }
        else
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.iteam_recieve,parent,false);
            return new Myclass(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Message.Myclass holder, int position) {

        Chat chat = chatList.get(position);

        Log.d("mydata","message shows"+chat.getMessages());
        holder.message_show.setText(chat.getMessages());

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("hh:mm a");
        date = dateFormat.format(calendar.getTime());
        holder.msg_time.setText(date);

        if (chat.getMessages().equals("photo")){
            holder.image_sent.setVisibility(View.VISIBLE);
            holder.message_show.setVisibility(View.GONE);
            Glide.with(context)
                    .load(chat.getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.image_sent);
        }

//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//                builder.setTitle("Delete Message?");
//                builder.setPositiveButton("DELETE FOR ME", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        chat.setMessages("This message is deleted.");
//                        FirebaseDatabase.getInstance().getReference()
//                                .child("Chats")
//                                .child(senderRoom)
//                                .child("messages")
//                                .child(chat.getMessageId()).removeValue();
//                    }
//                });
//
//                builder.setNegativeButton("DELETE FOR EVERYONE", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//
//                builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//                builder.show();
//               return false;
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class Myclass extends RecyclerView.ViewHolder {

        TextView message_show,msg_time;
        ImageView reaction,image_sent;

        public Myclass(@NonNull View itemView) {
            super(itemView);

            message_show = itemView.findViewById(R.id.message_show);
            reaction = itemView.findViewById(R.id.reaction);
            msg_time = itemView.findViewById(R.id.msg_time);
            image_sent = itemView.findViewById(R.id.image_sent);
        }
    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chatList.get(position).getSender().equals(firebaseUser.getUid()))
        {
            return ITEM_SENT;
        }
        else
        {
            return ITEM_RECIEVE;
        }
    }
}
