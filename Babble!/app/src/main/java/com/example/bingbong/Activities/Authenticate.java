package com.example.bingbong.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bingbong.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Authenticate extends AppCompatActivity {

    TextInputLayout ed_phonenumber;
    MaterialButton btn_send_otp;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(Authenticate.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        ed_phonenumber = findViewById(R.id.ed_phonenumber);
        ed_phonenumber.requestFocus();
        btn_send_otp = findViewById(R.id.btn_send_otp);
        progressBar = findViewById(R.id.progressbar);

        btn_send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                btn_send_otp.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(Authenticate.this,OTPActivity.class);
                intent.putExtra("phonenumber",ed_phonenumber.getEditText().getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }
}