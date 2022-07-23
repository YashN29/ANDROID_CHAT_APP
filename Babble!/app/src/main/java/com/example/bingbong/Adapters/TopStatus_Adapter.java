package com.example.bingbong.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bingbong.Activities.MainActivity;
import com.example.bingbong.Models.Status;
import com.example.bingbong.Models.UserStatus;
import com.example.bingbong.Models.User_Model;
import com.example.bingbong.R;
import com.example.bingbong.databinding.StatusRowLayoutBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TopStatus_Adapter extends RecyclerView.Adapter<TopStatus_Adapter.TopStatusViewHolder> {

    Context context;
    ArrayList<UserStatus> userStatuses;

    public TopStatus_Adapter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public TopStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_row_layout,parent,false);
        return new TopStatusViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusViewHolder holder, int position) {

        UserStatus userstatus = userStatuses.get(position);

        Status laststatus = userstatus.getStatuses().get(userstatus.getStatuses().size() - 1);

        Glide.with(context).load(laststatus.getImageurl()).into(holder.binding.statusView);

        holder.binding.username.setText(userstatus.getName());

        holder.binding.circularStatusView.setPortionsCount(userstatus.getStatuses().size());

        holder.binding.showStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for (Status status : userstatus.getStatuses()){
                    myStories.add(new MyStory(status.getImageurl()));
                }

                new StoryView.Builder(((MainActivity)context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userstatus.getName()) // Default is Hidden
                        .setSubtitleText("") // Default is Hidden
                        .setTitleLogoUrl(userstatus.getProfileimage()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class TopStatusViewHolder extends RecyclerView.ViewHolder{

        StatusRowLayoutBinding binding;

        public TopStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = StatusRowLayoutBinding.bind(itemView);
        }
    }
}
