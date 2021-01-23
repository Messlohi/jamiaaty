package com.example.jamiaaty;


import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;


public class UpdateProfile extends AppCompatActivity {
    EditText etname,etBio,etProfession,etEmail,etWeb;
    Button button;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference,Allposts;
    DocumentReference documentReference ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String  currentuid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
         currentuid= user.getUid();
         reference = database.getReference("All Users").child(currentuid);
         Allposts = database.getReference("All posts");
        documentReference = db.collection("user").document(currentuid);

        etBio = findViewById(R.id.et_bio_up);
        etEmail = findViewById(R.id.et_email_up);
        etname = findViewById(R.id.et_name_up);
        etProfession = findViewById(R.id.et_Profession_up);
        etWeb = findViewById(R.id.et_website_up);
        button = findViewById(R.id.btn_up);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }
    String urlResult ="";
    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {
                            String nameResult = task.getResult().getString("name");
                            String emailResult = task.getResult().getString("email");
                            String bioResult = task.getResult().getString("bio");
                            String profResult = task.getResult().getString("prof");
                            String uidResult = task.getResult().getString("uid");
                            String webResult = task.getResult().getString("web");
                            String privacyResult = task.getResult().getString("privacy");
                            urlResult = task.getResult().getString("url");

                            etname.setText(nameResult);
                            etBio.setText(bioResult);
                            etEmail.setText(emailResult);
                            etWeb.setText(webResult);
                            etProfession.setText(profResult);
                        }
                    }
                });


    }

    private  void  updateProfile(){

        final String name = etname.getText().toString();
        final String bio = etBio.getText().toString();
        final String prof = etProfession.getText().toString();
        final String web = etWeb.getText().toString();
        final String email =etEmail.getText().toString();


        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name.trim());
        result.put("prof", prof.trim());
        result.put("uid", currentuid);
        result.put("url", urlResult);
        result.put("nameTolower",name.toLowerCase().trim());
        reference.updateChildren(result);
        final DocumentReference sDoc = db.collection("user").document(currentuid);
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sDoc);

                transaction.update(sDoc, "name",name.trim() );
                transaction.update(sDoc,"prof",prof.trim());
                transaction.update(sDoc,"email",email.trim());
                transaction.update(sDoc,"web",web.trim());
                transaction.update(sDoc,"bio",bio.trim());

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateProfile.this, "updated", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },1000);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfile.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                });


    }
}