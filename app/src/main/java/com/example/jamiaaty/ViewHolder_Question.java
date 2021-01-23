package com.example.jamiaaty;

import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;

public class ViewHolder_Question extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView time_result, name_result, quesiton_result,delteButton,replyToPoste,replyBtn;
    ImageButton fvrt_btn;
    DatabaseReference favouriteref;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    public ViewHolder_Question(@NonNull View itemView) {
        super(itemView);
    }

    public void setItem(FragmentActivity activity , String name,String url ,String userid, String key , String question,String privacy,
                   String time){
        imageView = itemView.findViewById(R.id.iv_que_item);
        time_result = itemView.findViewById(R.id.time_que_item_tv);
        name_result = itemView.findViewById(R.id.name_que_item_tv);
        quesiton_result = itemView.findViewById(R.id.que_item_tv);
        replyToPoste = itemView.findViewById(R.id.reply_item_que);


        if(!url.equals("")){
//            Picasso.get().load(url).into(imageView);
            Glide.with(activity).load(url).into(imageView);
        }
        time_result.setText(time);
        name_result.setText(name);
        quesiton_result.setText(question);

    }

    public  void favouriteCheker(String postKey){
             fvrt_btn = itemView.findViewById(R.id.fvrt_f2_item);
            favouriteref = database.getReference("favourites_in_poste");
        FirebaseUser user   = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String uid = user.getUid();
            favouriteref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(postKey).hasChild(uid)){
                        fvrt_btn.setImageResource(R.drawable.ic_baseline_turned_in_24);
                    }else {
                        fvrt_btn.setImageResource(R.drawable.ic_baseline_turned_in_not_24);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    public void setItemRelated(Application activity , String name, String url , String userid, String key , String question, String privacy,
                               String time){
        TextView timeRrelated = itemView.findViewById(R.id.related_time_que_item_tv);
        ImageView imageViewRelated = itemView.findViewById(R.id.related_iv_que_item);
        TextView nameRelated = itemView.findViewById(R.id.related_name_que_item_tv);
        TextView questionRelated = itemView.findViewById(R.id.related_que_item_tv);
        replyBtn = itemView.findViewById(R.id.related_reply_item_que);

        if(!url.equals("")){
//            Picasso.get().load(url).into(imageViewRelated);
            Glide.with(activity).load(url).into(imageViewRelated);

        }
        timeRrelated.setText(time);
        nameRelated.setText(name);
        questionRelated.setText(question);

    }
    public void setItemShowQuesions(Application activity , String name, String url , String userid, String key , String question, String privacy,
                               String time){
        TextView timeRrelated = itemView.findViewById(R.id.userQuestions_time_que_item_tv);
        ImageView imageViewRelated = itemView.findViewById(R.id.userQuestions_iv_que_item);
        TextView nameRelated = itemView.findViewById(R.id.userQuestions_name_que_item_tv);
        TextView questionRelated = itemView.findViewById(R.id.userQuestions_que_item_tv);
        delteButton = itemView.findViewById(R.id.userQuestions_item_que);

        if(!url.equals("")){
//            Picasso.get().load(url).into(imageViewRelated);
            Glide.with(activity).load(url).into(imageViewRelated);
        }
        timeRrelated.setText(time);
        nameRelated.setText(name);
        questionRelated.setText(question);

    }










}
