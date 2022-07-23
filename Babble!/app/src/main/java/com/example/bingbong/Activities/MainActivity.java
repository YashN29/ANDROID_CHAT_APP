package com.example.bingbong.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.bingbong.Fragments.Calls_Frag;
import com.example.bingbong.Fragments.Chats_Frag;
import com.example.bingbong.R;
import com.example.bingbong.Fragments.Status_Frag;
import com.example.bingbong.Adapters.ViewPager_Adapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    TabLayout tablayout;
    ViewPager viewpager;
    FloatingActionButton fab;
    Toolbar toolbaar;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseMessaging.getInstance()
                .getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String token) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("token", token);
                        firebaseDatabase.getReference()
                                .child("Users")
                                .child(FirebaseAuth.getInstance().getUid())
                                .updateChildren(map);
                    }
                });

        firebaseAuth = FirebaseAuth.getInstance();


        toolbaar =findViewById(R.id.toolbaar);
        setSupportActionBar(toolbaar);

        tablayout = findViewById(R.id.tablayout);
        viewpager = findViewById(R.id.viewpager);

        ViewPager_Adapter viewPagerAdapter = new ViewPager_Adapter(getSupportFragmentManager());

        viewPagerAdapter.addFragments(new Chats_Frag(),"Chats");
        viewPagerAdapter.addFragments(new Status_Frag(),"Status");
        viewPagerAdapter.addFragments(new Calls_Frag(),"Calls");

        viewpager.setAdapter(viewPagerAdapter);
        tablayout.setupWithViewPager(viewpager);
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

        getMenuInflater().inflate(R.menu.mymenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this,Authenticate.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}