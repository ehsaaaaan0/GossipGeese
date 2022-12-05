package com.android.gossipgeese.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.gossipgeese.R;
import com.android.gossipgeese.StartMessaging;
import com.android.gossipgeese.database.DbHealper;
import com.android.gossipgeese.model.NewMessageModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewMessageAdapter extends RecyclerView.Adapter<NewMessageAdapter.myViewHolder> {

    ArrayList<NewMessageModel>list;
    Context context;
    int re=0,ae;

    public NewMessageAdapter(ArrayList<NewMessageModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_rv,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        NewMessageModel model = list.get(position);
        DbHealper db = new DbHealper(context);
        holder.name.setText(model.getName());
        holder.lastMessage.setText(model.getLastMsg());
        Glide.with(context).load(model.getImage()).placeholder(R.drawable.ic_gossipgeese).into(holder.image);
        String time =model.getTime();
        String sub1 = time.substring(0,5);
        holder.time.setText(sub1);

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





        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                DialogPlus dialogPlus = DialogPlus.newDialog(context)
                        .setContentHolder(new ViewHolder(R.layout.select_option)).setExpanded(true, 1300)
                        .create();
                View v = dialogPlus.getHolderView();
                TextView addArchive, addRecent;

                addArchive = v.findViewById(R.id.addArchive);
                addRecent = v.findViewById(R.id.addRecent);
                ae=0;
                re=0;
                if (Objects.equals(model.getRecent(), "true")){
                    addRecent.setText("Remove From Recent");
                    re=1;
                }else{
                    addRecent.setText("Add To Recent");
                    re=2;
                }
                if (Objects.equals(model.getArchive(), "false")){
                    addArchive.setText("Add To Archive");
                    ae=1;
                }else{
                    addArchive.setText("Remove From Archive");
                    ae=2;
                }
                addArchive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ae==1){
                            db.updateData(model.getId(),"false","true");
                            dialogPlus.dismiss();
                        }else{
                            db.updateData(model.getId(),"true","false");
                            dialogPlus.dismiss();
                            list.remove(holder.getAbsoluteAdapterPosition());
                        }
                    }
                });
                addRecent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (re==1){
                            db.updateData(model.getId(),"false",model.getArchive());
                            dialogPlus.dismiss();
                            list.remove(holder.getAbsoluteAdapterPosition());
                        }else{
                            db.updateData(model.getId(),"true",model.getArchive());
                            dialogPlus.dismiss();
                        }
                    }
                });


                dialogPlus.show();





                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = db.realAllData();
                while (cursor.moveToNext()){
                    if (cursor.getCount()>0) {
                        if (Objects.equals(cursor.getString(0), model.getId())) {
                            db.updateData(model.getId(), "true", "false");
                        }
                    }else{
                        db.insert(model.getId(),model.getName(),model.getImage(),model.
                                getLastMsg(),model.getTime(),"true","false",model.getToken());
                    }
                }context.startActivity(new Intent(context, StartMessaging.class)
                        .putExtra("receiver",model.getId())
                        .putExtra("image",model.getImage())
                        .putExtra("token",model.getToken())
                        .putExtra("name",model.getName()));
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
