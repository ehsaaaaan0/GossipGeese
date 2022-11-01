package com.android.gossipgeese.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.gossipgeese.R;
import com.android.gossipgeese.StartMessaging;
import com.android.gossipgeese.database.DbHealper;
import com.android.gossipgeese.model.NewMessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ForwardMsgAdapter extends RecyclerView.Adapter<ForwardMsgAdapter.myViewHolder> {

    ArrayList<NewMessageModel> list;
    Context context;
    String msg;

    public ForwardMsgAdapter(ArrayList<NewMessageModel> list, Context context,String msg) {
        this.list = list;
        this.context = context;
        this.msg = msg;
    }

    @NonNull
    @Override
    public ForwardMsgAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_rv,parent,false);
        return new ForwardMsgAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForwardMsgAdapter.myViewHolder holder, int position) {
        NewMessageModel model = list.get(position);
        DbHealper db = new DbHealper(context);
        holder.name.setText(model.getName());
        holder.time.setText(model.getTime());
        holder.lastMessage.setText(model.getLastMsg());
        Picasso.get().load(model.getImage()).into(holder.image);

        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(FirebaseAuth.getInstance().getUid() + model.getId())
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                holder.lastMessage.setText(snapshot1.child("message").getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             context.startActivity(new Intent(context, StartMessaging.class)
                        .putExtra("receiver",model.getId())
                        .putExtra("image",model.getImage())
                        .putExtra("msg",msg));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        CircleImageView image;
        TextView name,lastMessage,time;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.userImage);
            name = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.userLast);
            time = itemView.findViewById(R.id.userTime);
        }
    }
}
