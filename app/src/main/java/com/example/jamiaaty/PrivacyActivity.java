package com.example.jamiaaty;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class PrivacyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    String [] status = {"Chose any one","Public","Private"};
        TextView status_tv,privacy_to_profile;
        Spinner spinner ;
        Button button;

        FirebaseFirestore db =FirebaseFirestore.getInstance();
        DocumentReference reference;
        String currentUser="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);


        spinner = findViewById(R.id.spinner);
        button = findViewById(R.id.btn_privacy);
        status_tv = findViewById(R.id.tv_status);
        privacy_to_profile = findViewById(R.id.privacy_to_profile);

        privacy_to_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
             currentUser = user.getUid();
            reference = db.collection("user").document(currentUser);
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,status);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    savePrivacy();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },1500);
                }
            });

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(),"Please Select a value ",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser != ""){
            reference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            try {
                                if(task.getResult().exists()){
                                    String privacy_result = task.getResult().getString("privacy");
                                    status_tv.setText(privacy_result);
                                }
                            }
                            catch (Exception e){
                                Toast.makeText(getApplicationContext(),"Error user not found !"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });

        }

    }

    private  void savePrivacy(){

        final String value = spinner.getSelectedItem().toString();

        if(value == "Chose any one"){
            Toast.makeText(getApplicationContext(),"Please Select a value",Toast.LENGTH_SHORT).show();
        }else {
            if(currentUser !=""){

                db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(reference);

                        transaction.update(reference, "privacy",value );

                        // Success
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Privacy Updaeted", Toast.LENGTH_SHORT).show();

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }


    }
}