package com.example.jamiaaty;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jamiaaty.Model.All_UserMemeber;
import com.example.jamiaaty.Model.AnswerMember;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
//import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReplyActivity extends AppCompatActivity {


    String uid,question,post_key,privacy;
    TextView nametv,questiontv,tvreply;
    RecyclerView recyclerView;
    ImageView imageViewQue,imageViewUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference Allquetions,voteRef,reference,reference2;
    String currentuid="";
    Boolean voted = false;
    String url, name;
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

        recyclerView.setNestedScrollingEnabled(false);
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
            Allquetions = database.getReference("All Comments").child(post_key);
            voteRef = database.getReference("votes").child(post_key);



            reference = database.getReference("All Users").child(uid);
            reference2 = database.getReference("All Users").child(currentuid);


        }



        tvreply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ReplyActivity.this,R.style.BottomSheetTheme);
                View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottomsheet_comment_post,(ViewGroup) findViewById(R.id.ll_bottomsheet_commentPost));

                EditText comment = sheetView.findViewById(R.id.commentFeild_for_post);
                ImageButton submitMessage = sheetView.findViewById((R.id.ib_send_commentFeild_for_post));
                submitMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitMessage.setEnabled(false);
                        AnswerMember member = new AnswerMember();
                        if(comment.getText().toString().trim().equals("")){
                            return;
                        }
                        if(!comment.getText().toString().trim().equals("")){
                            Calendar cdate = Calendar.getInstance();
                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                            final String savedate = currentDate.format(cdate.getTime());

                            Calendar ctime = Calendar.getInstance();
                            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                            final String savetime = currentTime.format(ctime.getTime());

                            String time = savedate +":"+ savetime;
                            member.setTime(time);
                            member.setName(name);
                            member.setUid(uid);
                            member.setAnswer(comment.getText().toString());
                            member.setUrl(url);


                            Allquetions.push().setValue(member);
                            bottomSheetDialog.dismiss();

                        }else {
                            Toast.makeText(ReplyActivity.this, "Commenter!" , Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();

             }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();

        //question user reference
        if(!currentuid.equals("")){
          reference.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  try {
                      All_UserMemeber memeber = snapshot.getValue(All_UserMemeber.class);
                      String   url = memeber.getUrl();
                      String  name = memeber.getName();
                      Glide.with(getApplicationContext()).load(url).into(imageViewQue);
                      questiontv.setText(question);
                      nametv.setText(name);

                  }catch (Exception e){}
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });

            //refrence for the replying user
            reference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        All_UserMemeber memeber = snapshot.getValue(All_UserMemeber.class);
                         url = memeber.getUrl();
                         name = memeber.getName();
                        Glide.with(getApplicationContext()).load(url).into(imageViewUser);

                    }catch (Exception e){}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        if(!currentuid.equals("")){
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
                            holder.setAnswer(getApplication(),model.getName(),model.getAnswer().trim(),model.getUid(),model.getTime(),model.getUrl());
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