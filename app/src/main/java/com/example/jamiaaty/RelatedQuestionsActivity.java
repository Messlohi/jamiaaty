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

import com.example.jamiaaty.Model.QuestionMember;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RelatedQuestionsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_questions);

        recyclerView = findViewById(R.id.rv_related);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String currentUser = user.getUid();
            databaseReference = database.getReference("favouriteList_user").child(currentUser);

            FirebaseRecyclerOptions<QuestionMember> options = new FirebaseRecyclerOptions.Builder<QuestionMember>()
                    .setQuery(databaseReference,QuestionMember.class)
                    .build();
            FirebaseRecyclerAdapter<QuestionMember,ViewHolder_Question> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<QuestionMember, ViewHolder_Question>(options){
                        @NonNull
                        @Override
                        public ViewHolder_Question onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rekated_item_row,parent,false);
                            return new ViewHolder_Question(view);

                        }

                        @Override
                        protected void onBindViewHolder(@NonNull ViewHolder_Question holder, int position, @NonNull QuestionMember model) {

                            final String postKey = getRef(position).getKey();

                            holder.setItemRelated( getApplication(),model.getName(),model.getUrl(),model.getUserid(),model.getKey(),model.getQuestion(),model.getPrivacy(),model.getTime());


                            String que = getItem(position).getQuestion();
                           // String name = getItem(position).getName();
                           // String url = getItem(position).getUrl();
                           // String time = getItem(position).getTime();
                           // String privacy = getItem(position).getPrivacy();
                            String userid = getItem(position).getUserid();


                            holder.replyBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(RelatedQuestionsActivity.this,ReplyActivity.class);
                                    intent.putExtra("uid",userid);
                                    intent.putExtra("q",que);
                                    intent.putExtra("postkey",postKey);
                                    // intent.putExtra("key",privacy);
                                    startActivity(intent);
                                }
                            });

                        }
                    };

            recyclerView.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();

        }
    }
}