package com.example.jamiaaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AnswerPost extends AppCompatActivity {

    String uid,question,postKey;
    EditText editText;
    Button submitAnswer;
    AnswerMember member;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference AllQuestions;
    String name ,url ,time ,currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_post);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            currentUser = user.getUid();
        }
        member = new AnswerMember();
        editText = findViewById(R.id.answer_et);
        submitAnswer = findViewById(R.id.btn_answer_submit);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            uid = extras.getString("uid");
            question = extras.getString("q");
            postKey = extras.getString("postkey");
        }else {
            Toast.makeText(getApplicationContext(), "Error",Toast.LENGTH_SHORT).show();
        }
        AllQuestions = database.getReference("All Questions").child(postKey).child("Answer");

        submitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnswer();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser != "") {
            FirebaseFirestore  db = FirebaseFirestore.getInstance();
            DocumentReference reference ;
            reference = db.collection("user").document(currentUser);
                    reference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            try {
                                if (task.getResult().exists()) {
                                     url = task.getResult().getString("url");
                                     name = task.getResult().getString("name");

                                }
                            } catch (Exception e) {
                                Toast.makeText(AnswerPost.this, "Error Data not found !" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void saveAnswer() {
        String answer = editText.getText().toString();
        if(!answer.trim().equals("")){
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
            member.setAnswer(answer);
            member.setUrl(url);


            String id = AllQuestions.push().getKey();
            AllQuestions.child(id).setValue(member);
            submitAnswer.setEnabled(false);
            Toast.makeText(AnswerPost.this, "Submitted !" , Toast.LENGTH_SHORT).show();
            finish();

        }else {
            Toast.makeText(AnswerPost.this, "Write an answer First !" , Toast.LENGTH_SHORT).show();
        }

    }
}