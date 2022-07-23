package com.example.bingbong.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.bingbong.R;
import com.example.bingbong.Models.User_Model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class User_Profile extends AppCompatActivity {

    CircleImageView pf_img;
    MaterialButton btn_set_profile,set_profileImg;
    TextInputLayout name_box;
    ProgressDialog pd;

    Uri selectedimage;
    static final int IMAGE_REQ = 1;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        pd = new ProgressDialog(User_Profile.this);
        pd.setMessage("Updating Profile");
        pd.setCancelable(false);

        set_profileImg = findViewById(R.id.set_profileImg);
        pf_img = findViewById(R.id.pf_img);
        btn_set_profile = findViewById(R.id.btn_set_profile);
        name_box = findViewById(R.id.Name);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        set_profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,IMAGE_REQ);
            }
        });

        btn_set_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( name_box.getEditText().getText().toString().isEmpty()){
                    name_box.setError("Please type a name!");
                    return;
                }

                pd.show();

                if (selectedimage != null){
                    StorageReference storageReference = firebaseStorage.getReference().child("Profiles").child(firebaseAuth.getUid());
                    storageReference.putFile(selectedimage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageurl = uri.toString();

                                        String uid = firebaseAuth.getUid();
                                        String phonenumber = firebaseAuth.getCurrentUser().getPhoneNumber();
                                        String name = name_box.getEditText().getText().toString();


                                        User_Model userModel = new User_Model(uid , name , phonenumber , imageurl);

                                        firebaseDatabase.getReference()
                                                .child("Users")
                                                .child(uid)
                                                .setValue(userModel)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        pd.dismiss();
                                                        Intent y = new Intent(User_Profile.this, MainActivity.class);
                                                        startActivity(y);
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }
                else {
                    String uid = firebaseAuth.getUid();
                    String phonenumber = firebaseAuth.getCurrentUser().getPhoneNumber();
                    String name = name_box.getEditText().getText().toString();


                    User_Model userModel = new User_Model(uid , name , phonenumber ," default");

                    firebaseDatabase.getReference()
                            .child("Users")
                            .child(uid)
                            .setValue(userModel)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    pd.dismiss();
                                    Intent y = new Intent(User_Profile.this,MainActivity.class);
                                    startActivity(y);
                                    finish();
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null){
            if (data.getData() != null){
                pf_img.setImageURI(data.getData());
                selectedimage = data.getData();
            }
        }
    }
}