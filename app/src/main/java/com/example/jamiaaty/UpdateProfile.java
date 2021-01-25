package com.example.jamiaaty;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.jamiaaty.Model.All_UserMemeber;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;


public class UpdateProfile extends AppCompatActivity {
    EditText etname,etProfession,etEmail,etWeb;
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

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    All_UserMemeber memeber = snapshot.getValue(All_UserMemeber.class);
                    String nameResult = memeber.getName();
                    String emailResult = memeber.getEmail();
                    String profResult = memeber.getProf();
                    String webResult = memeber.getWeb();
                    urlResult = memeber.getUrl();

                    etname.setText(nameResult);
                    etEmail.setText(emailResult);
                    etWeb.setText(webResult);
                    etProfession.setText(profResult);


                }catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private  void  updateProfile(){

        final String name = etname.getText().toString();
        final String prof = etProfession.getText().toString();
        final String web = etWeb.getText().toString();
        final String email =etEmail.getText().toString();
        if(name.isEmpty()){
            Toast.makeText(UpdateProfile.this,"Saisie votre nom !",Toast.LENGTH_LONG).show();
            return;
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name.trim());
        result.put("prof", prof.trim());
        result.put("uid", currentuid);
        result.put("url", urlResult);
        result.put("web", web.trim());
        result.put("email", email.trim());
        result.put("nameTolower",name.toLowerCase().trim());
        reference.updateChildren(result);
        Toast.makeText(UpdateProfile.this,"Profile modifié !",Toast.LENGTH_LONG).show();
        finish();



    }
}