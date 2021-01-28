package com.example.jamiaaty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jamiaaty.Model.All_UserMemeber;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
//import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {

    ImageView imageView ;
    TextView textView ;
    Button delete,edit;
    DocumentReference reference;
    String url ="";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String cuurentUserId ="";
    private  static final int PICK_IMAGE = 1;
    Uri imageUri = null;
    All_UserMemeber  memeber=null;
    Button btnAppliquer;
    UploadTask uploadTask;
    StorageReference storageReference;
    ProgressBar progressBar;


    @Override
    protected void onStart() {
        super.onStart();

        database.getReference("All Users").child(cuurentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    memeber =   snapshot.getValue(All_UserMemeber.class);
                    url = memeber.getUrl();
                    if(!url.isEmpty()){
                        Glide.with(getApplication()).load(url).into(imageView);
                        delete.setVisibility(View.VISIBLE);
                    }
                    if(url.isEmpty()) delete.setVisibility(View.INVISIBLE);
                    textView.setText(memeber.getName());

                }catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        edit = findViewById(R.id.btn_edit_iv);
        delete = findViewById(R.id.btn_del_iv);
        imageView = findViewById(R.id.iv_expand);
        textView = findViewById(R.id.tv_name_image);
        btnAppliquer = findViewById(R.id.appliquerBtn_change_updateProfile);
        progressBar = findViewById(R.id.progressBar);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Profile images");


        if(user != null){
             cuurentUserId = user.getUid();
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent,PICK_IMAGE);

                }
            });

            btnAppliquer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(imageUri != null) {
                        progressBar.setVisibility(View.VISIBLE);
                        final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExtention(imageUri));
                        uploadTask = reference.putFile(imageUri);
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {

                                    throw task.getException();
                                }
                                return reference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri dowloadUri = task.getResult();
                                    memeber.setUrl(dowloadUri.toString());
                                    database.getReference("All Users").child(cuurentUserId).setValue(memeber);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    finish();
                                }else {
                                    Toast.makeText(ImageActivity.this,"réessayer ultiérement",Toast.LENGTH_SHORT).show();
                                    finish();

                                }
                            }
                        });
                    }

                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        memeber.setUrl("");
                        database.getReference("All Users").child(cuurentUserId).setValue(memeber);
                        if(!url.isEmpty()){
                            StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                            ref.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ImageActivity.this,"Image supprimer",Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                        }

                    }catch (Exception e){}

                }
            });
        }

    }
    private  String getFileExtention(Uri uri){
        if( uri == null) return  "";
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode == PICK_IMAGE || requestCode == RESULT_OK || data !=null || data.getData() != null) {
                delete.setVisibility(View.VISIBLE);
//                Toast.makeText(ImageActivity.this,"pick is done",Toast.LENGTH_LONG).show();
                imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
               // Glide.with(getApplicationContext()).load(imageUri).into(imageView);

            }
        }catch (Exception e){

            Toast.makeText(getApplicationContext(),"Error" +e.getMessage(),Toast.LENGTH_LONG).show();
            delete.setVisibility(View.INVISIBLE);

        }
    }


}