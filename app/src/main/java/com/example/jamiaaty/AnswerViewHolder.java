package com.example.jamiaaty;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class AnswerViewHolder extends RecyclerView.ViewHolder {

    ImageView imageProfile;
    TextView nameTv, timeTv, ansTv,upvoteTv,votesNoTv;
    Application application;
    int votesCountes;
    DatabaseReference reference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    public AnswerViewHolder(@NonNull View itemView) {
        super(itemView);
    }


    public void setAnswer(Application application,String name, String answer, String uid, String time, String url){
        this.application = application;
        imageProfile = itemView.findViewById(R.id.imageView_ans);
        nameTv  = itemView.findViewById(R.id.tv_name_ans);
        timeTv = itemView.findViewById(R.id.tv_time_ans);
        ansTv = itemView.findViewById(R.id.tv_ans);

        if(url != ""){
            Picasso.get().load(url).into(imageProfile);
        }
        nameTv.setText(name);
        timeTv.setText(time);
        ansTv.setText(answer);

    }

    public void UpvoteChecker(String postKey,String answerKey) {
        reference = database.getReference("votes").child(postKey);
        upvoteTv = itemView.findViewById(R.id.tv_vote_ans);
        votesNoTv = itemView.findViewById(R.id.tv_vote_no);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String currentUser = user.getUid();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(answerKey).hasChild(currentUser)){
                        upvoteTv.setText("VOTES");
                        votesCountes = (int)snapshot.child(answerKey).getChildrenCount();
                        votesNoTv.setText(votesCountes+"-VOTES");
                    }else {
                        upvoteTv.setText("UPVOTE");
                        votesCountes = (int)snapshot.child(answerKey).getChildrenCount();
                        votesNoTv.setText(votesCountes+"-VOTES");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

}
