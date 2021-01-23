package com.example.jamiaaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jamiaaty.Model.QuestionMember;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AskActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference AllQuestions,UserQuesion;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    ImageView imageView;
    String currentUser ="";
    QuestionMember questionMember;
    String name , url,privacy,uid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        editText = findViewById(R.id.ask_et_question);
        button = findViewById(R.id.btn_submit_question);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            currentUser = user.getUid();
            documentReference = db.collection("user").document(currentUser);

            AllQuestions = database.getReference("All Questions");
            //To delete the Question and know the user
            UserQuesion = database.getReference("User Questions").child(currentUser);

            questionMember = new QuestionMember();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String qustion = editText.getText().toString();

                    Calendar cdate = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                    final String savedate = currentDate.format(cdate.getTime());

                    Calendar ctime = Calendar.getInstance();
                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                    final String savetime = currentTime.format(ctime.getTime());

                    String time = savedate +":"+ savetime;
                    if(!qustion.equals("")){
                        questionMember.setQuestion(qustion);
                        questionMember.setName(name);
                        questionMember.setUrl(url);
                        questionMember.setPrivacy(privacy);
                        questionMember.setUserid(uid);
                        questionMember.setTime(time);

                        String id = UserQuesion.push().getKey();
                        UserQuesion.child(id).setValue(questionMember);

                        String child = AllQuestions.push().getKey();
                        questionMember.setKey(id);
                        AllQuestions.child(child).setValue(questionMember);

                        Toast.makeText(getApplicationContext(),"Submitted",Toast.LENGTH_SHORT).show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },1500);

                    }else {
                        Toast.makeText(getApplicationContext(),"Please ask a question !",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            name= task.getResult().getString("name");
                            uid = task.getResult().getString("uid");
                            privacy = task.getResult().getString("privacy");
                            url = task.getResult().getString("url");



                        }else {
                            Toast.makeText(getApplicationContext(),"Error fetching Data !",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}