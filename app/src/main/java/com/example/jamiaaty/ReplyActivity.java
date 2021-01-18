package com.example.jamiaaty;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import android.os.Bundle;

public class ReplyActivity extends AppCompatActivity {


    String uid,question,post_key,privacy;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference ,reference2;
    TextView nametv,questiontv,tvreply;
    RecyclerView recyclerView;
    ImageView imageViewQue,imageViewUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference Allquetions,voteRef;
    String currentuid="";
    Boolean voted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);


        nametv = findViewById(R.id.name_reply_tv);
        questiontv = findViewById(R.id.que_reply_tv);
        imageViewQue = findViewById(R.id.iv_que_user);
        imageViewUser = findViewById(R.id.iv_reply_user);
        tvreply = findViewById(R.id.answer_tv);

        recyclerView = findViewById(R.id.rv_ans);
        recyclerView.setLayoutManager(new LinearLayoutManager(ReplyActivity.this, LinearLayoutManager.VERTICAL,false));

        Bundle extra = getIntent().getExtras();
        if (extra != null){
            uid = extra.getString("uid");
            post_key = extra.getString("postkey");
            question = extra.getString("q");
            // privacy = extra.getString("key");
        }else {
            Toast.makeText(this, "opps some thing went wrong !", Toast.LENGTH_SHORT).show();
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            currentuid = user.getUid();
            Allquetions = database.getReference("All Questions").child(post_key).child("Answer");
            voteRef = database.getReference("votes").child(post_key);

            reference = db.collection("user").document(uid);
            reference2 = db.collection("user").document(currentuid);



        }




        tvreply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReplyActivity.this,AnswerPost.class);
                intent.putExtra("uid",uid);
                intent.putExtra("q",question);
                intent.putExtra("postkey",post_key);
                // intent.putExtra("key",privacy);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //question user reference
        if(currentuid != ""){
            reference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            try {
                                if(task.getResult().exists()){
                                    String url = task.getResult().getString("url");
                                    String name = task.getResult().getString("name");
                                    Picasso.get().load(url).into(imageViewQue);
                                    questiontv.setText(question);
                                    nametv.setText(name);


                                }
                            }
                            catch (Exception e){
                                Toast.makeText(ReplyActivity.this,"Error Data not found !"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        //refrence for the replying user
        reference2.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        try {
                            if(task.getResult().exists()){
                                String url = task.getResult().getString("url");
                                Picasso.get().load(url).into(imageViewUser);
                            }
                        }
                        catch (Exception e){
                            Toast.makeText(ReplyActivity.this,"Error Data not found !"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });



        if(currentuid != ""){
            FirebaseRecyclerOptions<AnswerMember> options = new FirebaseRecyclerOptions.Builder<AnswerMember>()
                    .setQuery(Allquetions,AnswerMember.class)
                    .build();
            FirebaseRecyclerAdapter<AnswerMember,AnswerViewHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<AnswerMember, AnswerViewHolder>(options) {
                        @NonNull
                        @Override
                        public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_post_layout,parent,false);
                            return new AnswerViewHolder(view);

                        }

                        @Override
                        protected void onBindViewHolder(@NonNull AnswerViewHolder holder, int position, @NonNull AnswerMember model) {
                            holder.setAnswer(getApplication(),model.getAnswer(),model.getName(),model.getUid(),model.getTime(),model.getUrl());
                            final String replyKey = getRef(position).getKey();
                            holder.UpvoteChecker(post_key,replyKey);
                            holder.upvoteTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    voted = true;
                                    holder.UpvoteChecker(post_key,replyKey);
                                    voteRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(voted.equals(true)){
                                                if(snapshot.child(replyKey).hasChild(currentuid)){
                                                    voteRef.child(replyKey).child(currentuid).removeValue();
                                                }else {
                                                    voteRef.child(replyKey).child(currentuid).setValue(true);
                                                }
                                                voted = false;
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });

                        }


                    };


            recyclerView.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();
        }

    }
}