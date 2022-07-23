package com.example.bingbong.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.bingbong.Adapters.Adapter_Message;
import com.example.bingbong.Models.Chat;
import com.example.bingbong.R;
import com.example.bingbong.Models.User_Model;
import com.example.bingbong.databinding.ActivityChatScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatScreen extends AppCompatActivity {



    FirebaseUser firebaseUser;
    Uri pfimage;

    DatabaseReference databaseReference;
    Toolbar toolb;

    String messageperson,senderuid;
    String senderRoom , recieverRoom;

    Adapter_Message adapter;
    ArrayList<Chat> chatArrayList;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage storage;
    ActivityChatScreenBinding binding;

    ProgressDialog pd ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        setSupportActionBar(binding.toolb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.setCancelable(false);

        chatArrayList = new ArrayList<>();
        adapter = new Adapter_Message(this,chatArrayList,senderRoom,recieverRoom);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        binding.messageRv.setLayoutManager(linearLayoutManager);
        binding.messageRv.setAdapter(adapter);
        binding.messageRv.setHasFixedSize(true);

        messageperson = getIntent().getStringExtra("uid");
        String token = getIntent().getStringExtra("token");
        String name = getIntent().getStringExtra("name");

//        Toast.makeText(this, token, Toast.LENGTH_SHORT).show();

        senderuid = FirebaseAuth.getInstance().getUid();

        firebaseDatabase.getReference().child("presence").child(messageperson).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String status = snapshot.getValue(String.class);
                    if (!status.isEmpty()){
                        if (status.equals("Offline")){
                            binding.userActive.setVisibility(View.GONE);
                        }
                        else {
                            binding.userActive.setText(status);
                            binding.userActive.setVisibility(View.VISIBLE);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        senderRoom = senderuid + messageperson;
        recieverRoom = messageperson + senderuid;


        firebaseDatabase.getReference().child("Chats")
                        .child(senderRoom)
                                .child("mesaages")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                chatArrayList.clear();
                                                for (DataSnapshot snapshot1: snapshot.getChildren())
                                                {
                                                    Chat chat = snapshot1.getValue(Chat.class);
                                                    chat.setMessageId(snapshot1.getKey());
                                                    chatArrayList.add(chat);
                                                }

                                                adapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

        binding.btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!binding.messagebox.getText().toString().equals(""))
                {
                    String msgtxt = binding.messagebox.getText().toString();
                    Date date = new Date();
                    Chat chat = new Chat(msgtxt , senderuid , date.getTime());
                    binding.messagebox.setText("");

                    String randomkey = firebaseDatabase.getReference().push().getKey();

                    HashMap<String,Object> lastmsgObj = new HashMap<>();
                    lastmsgObj.put("lastmsg",chat.getMessages());
                    lastmsgObj.put("lastmsgTime",date.getTime());

                    firebaseDatabase.getReference().child("Chats").child(senderRoom).updateChildren(lastmsgObj);
                    firebaseDatabase.getReference().child("Chats").child(recieverRoom).updateChildren(lastmsgObj);


                    firebaseDatabase.getReference().child("Chats")
                            .child(senderRoom)
                            .child("mesaages")
                            .child(randomkey)
                            .setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    firebaseDatabase.getReference().child("Chats")
                                            .child(recieverRoom)
                                            .child("mesaages")
                                            .child(randomkey)
                                            .setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    sendNotification(name, chat.getMessages(), token);
                                                }
                                            });
                                }
                            });
                }
                else
                {
                    Toast.makeText(ChatScreen.this, "Please write a message!", Toast.LENGTH_SHORT).show();
                }
                }

        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(messageperson);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User_Model userModel = snapshot.getValue(User_Model.class);
                binding.userNameShow.setText(userModel.getName());

                try {
                    if (userModel.getProfileImage().contentEquals("imageurl"))
                    {
                        binding.profileImg.setImageResource(R.drawable.profile);
                    }
                    else
                    {
                        Glide.with(ChatScreen.this).load(userModel.getProfileImage()).into(binding.profileImg);
                    }
                }
                catch (Exception e){
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 25);
            }
        });

        final Handler handler = new Handler();
        binding.messagebox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                firebaseDatabase.getReference().child("presence").child(senderuid).setValue("typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping, 1500);
            }

            Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    firebaseDatabase.getReference().child("presence").child(senderuid).setValue("Online");
                }
            };
        });

    }

    void sendNotification(String name , String message, String token){
        try {
            RequestQueue queue = Volley.newRequestQueue(this);

            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title", name);
            data.put("body", message);
            JSONObject notificationdata = new JSONObject();
            notificationdata.put("notification", data);
            notificationdata.put("to", token);

            JsonObjectRequest request = new JsonObjectRequest(url, notificationdata,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
//                            Toast.makeText(ChatScreen.this, "success", Toast.LENGTH_SHORT).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<>();
                    String key = "Key=AAAAqv4rUS0:APA91bEngxwGcHlgc-ZR5dy8ra18rn_fjO_InmXoD0O8wudUsVM4-zmT5URBzXwOp0YqoUzLYpFjiW0nFGIMVmGhPmJOvpI9qlDt_CwMNeBElLH2fOY1QrEldnsC1WJujDc5WV_mQ2O_";
                    map.put("Content-Type", "application/json");
                    map.put("Authorization", key);

                    return map;
                }
            };

            queue.add(request);

        }catch (Exception e){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentid = FirebaseAuth.getInstance().getUid();
        firebaseDatabase.getReference().child("presence").child(currentid).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentid = FirebaseAuth.getInstance().getUid();
        firebaseDatabase.getReference().child("presence").child(currentid).setValue("Offline");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.chatmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 25) {
            if (data != null) {
                if (data.getData() != null){
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("Chats").child(calendar.getTimeInMillis() + "");
                    pd.show();
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            pd.dismiss();
                            if (task.isSuccessful())
                            {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filepath = uri.toString();

                                        String msgtxt = binding.messagebox.getText().toString();
                                        Date date = new Date();
                                        Chat chat = new Chat(msgtxt , senderuid , date.getTime());
                                        chat.setMessages("photo");
                                        chat.setImageUrl(filepath);
                                        binding.messagebox.setText("");

                                        String randomkey = firebaseDatabase.getReference().push().getKey();

                                        HashMap<String,Object> lastmsgObj = new HashMap<>();
                                        lastmsgObj.put("lastmsg",chat.getMessages());
                                        lastmsgObj.put("lastmsgTime",date.getTime());

                                        firebaseDatabase.getReference().child("Chats").child(senderRoom).updateChildren(lastmsgObj);
                                        firebaseDatabase.getReference().child("Chats").child(recieverRoom).updateChildren(lastmsgObj);

                                        firebaseDatabase.getReference().child("Chats")
                                                .child(senderRoom)
                                                .child("mesaages")
                                                .child(randomkey)
                                                .setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        firebaseDatabase.getReference().child("Chats")
                                                                .child(recieverRoom)
                                                                .child("mesaages")
                                                                .child(randomkey)
                                                                .setValue(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {

                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }
}