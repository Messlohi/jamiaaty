package com.example.jamiaaty;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class messageChatAdapter extends RecyclerView.Adapter<messageChatAdapter.ViewHolder>  {

    Context context;
    List<chatMessageModel> listMessages ;
    List<Boolean> isSenderList;


    public messageChatAdapter(Context context, List<chatMessageModel> listMessages,List<Boolean> isSenderList) {
        this.context = context;
        this.listMessages = listMessages;
        this.isSenderList = isSenderList;
    }

    @NonNull
    @Override
    public messageChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.message_chat_layout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull messageChatAdapter.ViewHolder holder, int position) {

        chatMessageModel member = listMessages.get(position);
        holder.setMessage(member.message,member.time,isSenderList.get(position));

    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout containerChatMessages;
        CardView senderCard,receiverCard;
        TextView messageSender,timeSender,messageReceiver,timeReceiver;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            containerChatMessages = itemView.findViewById(R.id.ll_container_message_chat);
            senderCard = itemView.findViewById(R.id.message_sender_cv);
            receiverCard = itemView.findViewById(R.id.message_reciver_cv);
            messageSender = itemView.findViewById(R.id.tv_messageContent_sender);
            messageReceiver = itemView.findViewById(R.id.tv_messageContent_receiver);
            timeSender = itemView.findViewById(R.id.tv_timeMessage_sender);
            timeReceiver = itemView.findViewById(R.id.tv_timeMessage_receiver);

        }

        public void setMessage(String message,String time,boolean isSender){
            if(isSender){
                containerChatMessages.setGravity(Gravity.RIGHT);
                senderCard.setVisibility(View.VISIBLE);
                receiverCard.setVisibility(View.GONE);
                messageSender.setText(message);
                timeSender.setText(time);
            }else {
                containerChatMessages.setGravity(Gravity.LEFT);
                senderCard.setVisibility(View.GONE);
                receiverCard.setVisibility(View.VISIBLE);
                messageReceiver.setText(message);
                timeReceiver.setText(time);
            }
        }
    }
}
