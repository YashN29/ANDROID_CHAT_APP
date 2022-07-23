package com.example.bingbong.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.bingbong.Adapters.TopStatus_Adapter;
import com.example.bingbong.Models.Status;
import com.example.bingbong.Models.User_Model;
import com.example.bingbong.Models.UserStatus;
import com.example.bingbong.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class Status_Frag extends Fragment {

    RelativeLayout rvlayout;
    RecyclerView status_rv;
    CircleImageView circlular_image;

    FirebaseDatabase firebaseDatabase;

    ArrayList<UserStatus> userStatuses;
    TopStatus_Adapter status_adapter;

    ProgressDialog progressDialog;

    User_Model userModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status_,container,false);

        rvlayout = v.findViewById(R.id.rvlayout);
        status_rv = v.findViewById(R.id.status_rv);
        circlular_image = v.findViewById(R.id.circlular_image);


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);


        firebaseDatabase = FirebaseDatabase.getInstance();
        userStatuses = new ArrayList<>();

        firebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userModel = snapshot.getValue(User_Model.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        status_adapter = new TopStatus_Adapter(getContext(),userStatuses);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        status_rv.setLayoutManager(linearLayoutManager);
        status_rv.setAdapter(status_adapter);

        firebaseDatabase.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userStatuses.clear();
                    for (DataSnapshot storySnapshot : snapshot.getChildren()){
                        UserStatus status = new UserStatus();
                        status.setName(storySnapshot.child("name").getValue(String.class));
                        status.setProfileimage(storySnapshot.child("profileimage").getValue(String.class));
                        status.setLastupdated(storySnapshot.child("lastupdated").getValue(Long.class));

                        ArrayList<Status> statuses = new ArrayList<>();

                        for (DataSnapshot statusSnapshot : storySnapshot.child("statuses").getChildren()){
                            Status status1 = statusSnapshot.getValue(Status.class);
                            statuses.add(status1);
                        }
                        status.setStatuses(statuses);
                        userStatuses.add(status);
                    }
                    status_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        rvlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 75);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.getData() != null){
                progressDialog.show();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");

                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    UserStatus user_status = new UserStatus();
                                    user_status.setName(userModel.getName());
                                    user_status.setProfileimage(userModel.getProfileImage());
                                    user_status.setLastupdated(date.getTime());

                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("name" , user_status.getName());
                                    obj.put("profileimage" , user_status.getProfileimage());
                                    obj.put("lastupdated" , user_status.getLastupdated());

                                    String imageUrl = uri.toString();
                                    Status status = new Status(imageUrl, user_status.getLastupdated());

                                    firebaseDatabase.getReference()
                                            .child("status")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj);

                                    firebaseDatabase.getReference().child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("statuses")
                                            .push()
                                            .setValue(status);

                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }
    }
}