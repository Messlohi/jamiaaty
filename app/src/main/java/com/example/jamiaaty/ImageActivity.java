package com.example.jamiaaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {

    ImageView imageView ;
    TextView textView ;
    Button delete,edit;
    DocumentReference reference;
    String url ="";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        edit = findViewById(R.id.btn_edit_iv);
        delete = findViewById(R.id.btn_del_iv);
        imageView = findViewById(R.id.iv_expand);
        textView = findViewById(R.id.tv_name_image);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            String cuurentUserId = user.getUid();
            reference = db.collection("user").document(cuurentUserId);
            reference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            try {
                                if(task.getResult().exists()){
                                    String name =task.getResult().getString("name");
                                    url = task.getResult().getString("url");
                                    textView.setText(name);
                                    Picasso.get().load(url).into(imageView);
                                }else {
                                    Toast.makeText(ImageActivity.this, "No profile Image !",Toast.LENGTH_SHORT).show();
                                }

                            }catch(Exception e){
                                Toast.makeText(ImageActivity.this, "Error in Handlig Profila image !",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });



        }

    }
}