package com.android.gossipgeese.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.gossipgeese.R;
import com.android.gossipgeese.model.CallLogModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.myViewHolder> {

    ArrayList<CallLogModel>list;
    Context context;

    public CallLogAdapter(ArrayList<CallLogModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_rv, parent,false);
       return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        CallLogModel model = list.get(position);
        holder.type.setText(model.getKey());
        FirebaseDatabase.getInstance().getReference().child("users").child(model.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("image").getValue(String.class);
                Picasso.get().load(image).placeholder(R.drawable.ic_gossipgeese).into(holder.image);
                holder.name.setText(snapshot.child("name").getValue(String.class));
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

    class myViewHolder extends RecyclerView.ViewHolder{
        CircleImageView image;
        AppCompatTextView name,type;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.userImage);
            name = itemView.findViewById(R.id.name);
            type = itemView.findViewById(R.id.type);
        }
    }
}
