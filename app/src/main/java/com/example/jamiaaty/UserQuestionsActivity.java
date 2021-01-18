package com.example.jamiaaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserQuestionsActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userQuestions,AllQuestions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_questions);

        recyclerView = findViewById(R.id.rv_users_Allquestions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String currentUser = user.getUid();

            userQuestions = database.getReference("User Questions").child(currentUser);
            AllQuestions = database.getReference("All Questions");

            FirebaseRecyclerOptions<QuestionMember> options = new FirebaseRecyclerOptions.Builder<QuestionMember>()
                    .setQuery(userQuestions,QuestionMember.class)
                    .build();
            FirebaseRecyclerAdapter<QuestionMember,ViewHolder_Question> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<QuestionMember, ViewHolder_Question>(options){
                        @NonNull
                        @Override
                        public ViewHolder_Question onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userquestions_item_row,parent,false);
                            return new ViewHolder_Question(view);

                        }

                        @Override
                        protected void onBindViewHolder(@NonNull ViewHolder_Question holder, int position, @NonNull QuestionMember model) {

                            final String postKey = getRef(position).getKey();

                            holder.setItemShowQuesions( getApplication(),model.getName(),model.getUrl(),model.getUserid(),model.getKey(),model.getQuestion(),model.getPrivacy(),model.getTime());


                            String time = getItem(position).getTime();
                            holder.delteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    delete(time);
                                }
                            });




                        }
                    };

            recyclerView.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();

        }
    }

    private void delete(String time) {
        Query query = userQuestions.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                    Toast.makeText(UserQuestionsActivity.this,  "Post Deleted",Toast.LENGTH_SHORT);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query1 = AllQuestions.orderByChild("time").equalTo(time);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                    Toast.makeText(UserQuestionsActivity.this,  "Post Deleted",Toast.LENGTH_SHORT);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}