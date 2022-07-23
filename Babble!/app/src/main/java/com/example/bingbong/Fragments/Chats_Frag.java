package com.example.bingbong.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.bingbong.Adapters.Chat_Adapter;
import com.example.bingbong.R;
import com.example.bingbong.Models.User_Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Chats_Frag extends Fragment {

    ShimmerRecyclerView recyclerview_chats;
    Chat_Adapter chat_adapter;
    List list = new ArrayList();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chats_,container,false);

        firebaseDatabase = FirebaseDatabase.getInstance();

        chat_adapter = new Chat_Adapter(getContext(),list);

        recyclerview_chats = v.findViewById(R.id.recyclerview_chats);
        recyclerview_chats.setHasFixedSize(true);
        recyclerview_chats.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview_chats.setAdapter(chat_adapter);
        recyclerview_chats.showShimmerAdapter();

        getallData();

        return v;
    }

    private void getallData(){

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){

                    User_Model user = snapshot1.getValue(User_Model.class);
                    if (!user.getUid().equals(FirebaseAuth.getInstance().getUid())){
                        list.add(user);
                    }
                }
                recyclerview_chats.hideShimmerAdapter();
                chat_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}