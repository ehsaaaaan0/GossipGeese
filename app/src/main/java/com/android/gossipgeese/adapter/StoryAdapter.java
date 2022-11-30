package com.android.gossipgeese.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.gossipgeese.MainActivity;
import com.android.gossipgeese.R;
import com.android.gossipgeese.StartStory;
import com.android.gossipgeese.ViewStories;
import com.android.gossipgeese.model.StoryModel;
import com.android.gossipgeese.model.UserStories;
import com.devlomi.circularstatusview.CircularStatusView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.myViewHolder> {
    ArrayList<StoryModel> list;
    Context context;

    public StoryAdapter(ArrayList<StoryModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_stories_rv, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        StoryModel model = list.get(position);
        UserStories stories = model.getStories().get(model.getStories().size() - 1);

        Picasso.get().load(stories.getImage()).placeholder(R.drawable.ic_gossipgeese).into(holder.imageView);
        holder.storyView.setPortionsCount(model.getStories().size());


        FirebaseDatabase.getInstance().getReference().child("users").child(model.getStoryBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String n = snapshot.child("name").getValue(String.class);
                String i = snapshot.child("image").getValue(String.class);
                holder.name.setText(snapshot.child("name").getValue(String.class));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        ArrayList<omari.hamza.storyview.model.MyStory> myStories = new ArrayList<>();
//                        for (UserStories stories1 : model.getStories()){
//                            myStories.add(new MyStory(stories1.getImage()));
//                        }
//                        new StoryView.Builder(((AppCompatActivity)context).getSupportFragmentManager())
//                                .setStoriesList(myStories) // Required
//                                .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
//                                .setTitleText(holder.name.getText().toString()) // Default is Hidden
//                                .setSubtitleText("") // Default is Hidden
//                                .setTitleLogoUrl(snapshot.child("image").getValue(String.class)) // Default is Hidden
//                                .setStoryClickListeners(new StoryClickListeners() {
//                                    @Override
//                                    public void onDescriptionClickListener(int position) {
//                                    }
//
//                                    @Override
//                                    public void onTitleIconClickListener(int position) {
//                                    }
//                                })
//                                .build()
//                                .show();
                        context.startActivity(new Intent(context, StartStory.class)
                                .putExtra("id", model.getStoryBy())
                                .putExtra("key", model.getStoryKey())
                                .putExtra("name",n)
                                .putExtra("image",i));
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView name;
        CircularStatusView storyView;

        //        View storyView, storyView2, storyView3, storyView4, storyView5,storyView6;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.story_show_user);
            name = itemView.findViewById(R.id.user_name_story);
            storyView = itemView.findViewById(R.id.circular_status_view);
//            storyView = itemView.findViewById(R.id.storyView);
//            storyView2 = itemView.findViewById(R.id.storyView2);
//            storyView3 = itemView.findViewById(R.id.storyView3);
//            storyView4 = itemView.findViewById(R.id.storyView4);
//            storyView5 = itemView.findViewById(R.id.storyView5);
//            storyView6 = itemView.findViewById(R.id.storyView6);
        }
    }
}
