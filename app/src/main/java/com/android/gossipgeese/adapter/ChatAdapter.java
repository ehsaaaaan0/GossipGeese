package com.android.gossipgeese.adapter;

import static android.content.Context.CLIPBOARD_SERVICE;



import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.content.ClipboardManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.gossipgeese.ForwardMsg;
import com.android.gossipgeese.R;
import com.android.gossipgeese.ViewImage;
import com.android.gossipgeese.model.MessageModel;
import com.github.pgreze.reactions.PopupGravity;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

public class ChatAdapter extends RecyclerView.Adapter {
    ArrayList<MessageModel> list;
    Context context;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;
    String senderRoom, receiverRoom;

    public ChatAdapter(ArrayList<MessageModel> list, Context context,String senderRoom,String receiverRoom) {
        this.list = list;
        this.context = context;
        this.senderRoom=senderRoom;
        this.receiverRoom=receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_side, parent, false);
            return new SenderViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_side, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                return SENDER_VIEW_TYPE;
        }else {
                return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel model  = list.get(position);
        int reactions[] = new int[]{
                R.drawable.ic_like,
                R.drawable.ic_love,
                R.drawable.ic_laugh,
                R.drawable.ic_wow,
                R.drawable.ic_sad,
                R.drawable.ic_angry,
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .withPopupGravity(PopupGravity.CENTER)
                .withPopupMargin(5)
                .build();
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {


            if (holder.getClass() == SenderViewHolder.class) {

                ((SenderViewHolder) holder).reaction.setImageResource(reactions[pos]);
                ((SenderViewHolder) holder).reaction.setVisibility(View.VISIBLE);
                FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(pos));
            }

            else {
                ((ReceiverViewHolder)holder).reactionRec.setImageResource(reactions[pos]);
                ((ReceiverViewHolder)holder).reactionRec.setVisibility(View.VISIBLE);
                FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(pos));
            }
            return true;
        });


        if (holder.getClass()==SenderViewHolder.class){
            ((SenderViewHolder)holder).senderMsg.setText(model.getMessage());

            String getImge = model.getImage();

            if (Objects.equals(model.getType(), "voice")){
                ((SenderViewHolder)holder).showOther.setVisibility(View.GONE);
                ((SenderViewHolder)holder).voicePlayerView.setVisibility(View.VISIBLE);
                Uri uri = Uri.parse(model.getVoice());
                ((SenderViewHolder)holder).voicePlayerView.setAudio(model.getVoice());
            }else{
                ((SenderViewHolder)holder).showOther.setVisibility(View.VISIBLE);
                ((SenderViewHolder)holder).voicePlayerView.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(getImge)){
                ((SenderViewHolder)holder).image_msg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.VISIBLE);
            }else{
                ((SenderViewHolder)holder).image_msg.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder)holder).image_msg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, ViewImage.class)
                                .putExtra("image",model.getImage()));
                    }
                });
            }
            Picasso.get().load(model.getImage()).placeholder(R.drawable.ic_profile).into(((SenderViewHolder)holder).image_msg);
            FirebaseDatabase.getInstance().getReference().child("users").child(model.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
               Picasso.get().load(snapshot.child("image").getValue(String.class)).placeholder(R.drawable.ic_profile).into(((SenderViewHolder)holder).senderImage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            ((SenderViewHolder)holder).show_options.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    DialogPlus dialogPlus = DialogPlus.newDialog(context)
                            .setContentHolder(new ViewHolder(R.layout.select_option2)).setExpanded(true, 700)
                            .create();
                    View v = dialogPlus.getHolderView();
                    TextView copy, delete,forward;
                    copy = v.findViewById(R.id.copy);
                    delete = v.findViewById(R.id.delete);
                    forward = v.findViewById(R.id.forwardMsg);
                    ImageView rec0,rec1,rec2,rec3,rec4,rec5;
                    rec0 = v.findViewById(R.id.rec0);
                    rec1 = v.findViewById(R.id.rec1);
                    rec2 = v.findViewById(R.id.rec2);
                    rec3 = v.findViewById(R.id.rec3);
                    rec4 = v.findViewById(R.id.rec4);
                    rec5 = v.findViewById(R.id.rec5);

                    rec0.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(0));
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(0));
                            dialogPlus.dismiss();
                        }
                    });
                    rec1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(1));
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(1));
                            dialogPlus.dismiss();
                        }
                    });
                    rec2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(2));
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(2));
                            dialogPlus.dismiss();
                        }
                    });
                    rec3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(3));
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(3));
                            dialogPlus.dismiss();
                        }
                    });
                    rec4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(4));
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(4));
                            dialogPlus.dismiss();
                        }
                    });
                    rec5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(5));
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(5));
                            dialogPlus.dismiss();
                        }
                    });

                    forward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(context, ForwardMsg.class)
                                    .putExtra("msg",model.getMessage()));
                        }
                    });

                    copy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("label", ((SenderViewHolder)holder).senderMsg.getText().toString());
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(context, "Text Copied", Toast.LENGTH_SHORT).show();
                            dialogPlus.dismiss();
                        }
                    });
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(model.getMsgKey()).removeValue();
                            Toast.makeText(context, "Message Deleted", Toast.LENGTH_SHORT).show();
                            list.remove(holder.getBindingAdapterPosition());
                            dialogPlus.dismiss();
                        }
                    });



                    dialogPlus.show();





                    return true;
                }
            });
            if (!Objects.equals(model.getReaction(), "null")) {
                int fe = Integer.parseInt(model.getReaction());
                ((SenderViewHolder) holder).reaction.setImageResource(reactions[fe]);
                ((SenderViewHolder) holder).reaction.setVisibility(View.VISIBLE);
            }else{
                ((SenderViewHolder)holder).reaction.setVisibility(View.GONE);
            }
//            ((SenderViewHolder) holder).showReaction.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    popup.onTouch(v, event);
//                    return false;
//                }
//            });
        }else{
            String getImge = model.getImage();

            if (Objects.equals(model.getType(), "voice")){
                ((ReceiverViewHolder)holder).hide_rec.setVisibility(View.GONE);
                ((ReceiverViewHolder)holder).voicePlayer_rec.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder)holder).voicePlayer_rec.setAudio(model.getVoice());
            }else{
                ((ReceiverViewHolder)holder).hide_rec.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder)holder).voicePlayer_rec.setVisibility(View.GONE);
            }

            ((ReceiverViewHolder)holder).receiverMsg.setText(model.getMessage());
            if (TextUtils.isEmpty(getImge)){
                ((ReceiverViewHolder)holder).rec_image.setVisibility(View.GONE);
                ((ReceiverViewHolder) holder).receiverMsg.setVisibility(View.VISIBLE);
            }else{
                ((ReceiverViewHolder)holder).rec_image.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder) holder).receiverMsg.setVisibility(View.GONE);
                Picasso.get().load(model.getImage()).placeholder(R.drawable.ic_profile).into(((ReceiverViewHolder)holder).rec_image);
                ((ReceiverViewHolder)holder).rec_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(context, ViewImage.class)
                                .putExtra("image",model.getImage()));
                    }
                });
            }
            FirebaseDatabase.getInstance().getReference().child("users").child(model.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Picasso.get().load(snapshot.child("image").getValue(String.class)).placeholder(R.drawable.ic_profile).into(((ReceiverViewHolder)holder).receiverImage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if (!Objects.equals(model.getReaction(), "null")){
                int te = Integer.parseInt(model.getReaction());
                ((ReceiverViewHolder) holder).reactionRec.setImageResource(reactions[te]);
                ((ReceiverViewHolder) holder).reactionRec.setVisibility(View.VISIBLE);
            }else{
                ((ReceiverViewHolder)holder).reactionRec.setVisibility(View.GONE);
            }

            ((ReceiverViewHolder)holder).show_rec_option.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    DialogPlus dialogPlus = DialogPlus.newDialog(context)
                            .setContentHolder(new ViewHolder(R.layout.select_option2)).setExpanded(true, 700)
                            .create();
                    View v = dialogPlus.getHolderView();
                    TextView copy, delete,forward;
                    copy = v.findViewById(R.id.copy);
                    delete = v.findViewById(R.id.delete);
                    forward = v.findViewById(R.id.forwardMsg);
                    ImageView rec0,rec1,rec2,rec3,rec4,rec5;
                    rec0 = v.findViewById(R.id.rec0);
                    rec1 = v.findViewById(R.id.rec1);
                    rec2 = v.findViewById(R.id.rec2);
                    rec3 = v.findViewById(R.id.rec3);
                    rec4 = v.findViewById(R.id.rec4);
                    rec5 = v.findViewById(R.id.rec5);

                    rec0.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(0));
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(0));
                            dialogPlus.dismiss();
                        }
                    });
                    rec1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(1));
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(1));
                            dialogPlus.dismiss();
                        }
                    });
                    rec2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(2));
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(2));
                            dialogPlus.dismiss();
                        }
                    });
                    rec3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(3));
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(3));
                            dialogPlus.dismiss();
                        }
                    });
                    rec4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(4));
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(4));
                            dialogPlus.dismiss();
                        }
                    });
                    rec5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(5));
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(model.getMsgKey()).child("reaction").setValue(String.valueOf(5));
                            dialogPlus.dismiss();
                        }
                    });

                    forward.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(new Intent(context, ForwardMsg.class)
                                    .putExtra("msg",model.getMessage()));
                        }
                    });

                    copy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("label", ((SenderViewHolder)holder).senderMsg.getText().toString());
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(context, "Text Copied", Toast.LENGTH_SHORT).show();
                            dialogPlus.dismiss();
                        }
                    });
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom).child(model.getMsgKey()).removeValue();
                            Toast.makeText(context, "Message Deleted", Toast.LENGTH_SHORT).show();
                            list.remove(holder.getBindingAdapterPosition());
                            dialogPlus.dismiss();
                        }
                    });



                    dialogPlus.show();





                    return true;
                }
            });


//            ((ReceiverViewHolder) holder).show_rec_option.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    popup.onTouch(v, event);
//                    return false;
//                }
//            });


        }





    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SenderViewHolder extends RecyclerView.ViewHolder{
        CircleImageView senderImage;
        TextView senderMsg;
        ImageView reaction,image_msg;
        LinearLayout showOther;
        ConstraintLayout showReaction,show_options;
        VoicePlayerView voicePlayerView;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderImage = itemView.findViewById(R.id.senderImage);
            senderMsg = itemView.findViewById(R.id.senderMsg_text);
            reaction = itemView.findViewById(R.id.reactionImage);
            showReaction = itemView.findViewById(R.id.showReaction);
            show_options = itemView.findViewById(R.id.show_options);
            image_msg = itemView.findViewById(R.id.image_msg);
            showOther = itemView.findViewById(R.id.showOther);
            voicePlayerView = itemView.findViewById(R.id.voicePlayer);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder{
        CircleImageView receiverImage;
        TextView receiverMsg;
        ImageView reactionRec,rec_image;
        ConstraintLayout hide_rec,show_rec_option;
        VoicePlayerView voicePlayer_rec;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverImage = itemView.findViewById(R.id.receiverImage);
            receiverMsg = itemView.findViewById(R.id.receiverMsg);
            reactionRec = itemView.findViewById(R.id.reactionImage_rec);
            rec_image = itemView.findViewById(R.id.rec_image);
            hide_rec = itemView.findViewById(R.id.hide_rec);
            show_rec_option = itemView.findViewById(R.id.show_rec_option);
            voicePlayer_rec = itemView.findViewById(R.id.voicePlayer_rec);
        }
    }
}
