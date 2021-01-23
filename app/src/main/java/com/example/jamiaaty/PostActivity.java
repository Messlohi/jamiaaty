package com.example.jamiaaty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.jamiaaty.Model.PostMember;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
//import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    ImageView imageView,iv_userProfile;
    ProgressBar progressBar;
    private Uri selectedUri,supportUri;

    private  static final int PICK_FILE = 1;
    private  static final int PICK_SUPPORT = 2;
    UploadTask uploadTask;
    ConstraintLayout mainLayoutPost;
    LinearLayout supportLayout;
    EditText etdesc,postBody,titreSupport,desccriptionSupport;
    Button btnuploadfile;
    ImageButton unShowMediaButton,unshowSupport;
    TextView btnchoosefile,tv_nameUser,btnchosesupport,nameSupport;
    VideoView videoView;
    String url,name;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db1,db2,db3,db4,questionsRef,UserQuesion,userSupport;

    MediaController mediaController;
    String type="text";
    PostMember postmember;
    String typePoste ="Post";
    String [] genre = {"Post","Question"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        mediaController = new MediaController(this);


        unshowSupport = findViewById(R.id.ib_closeSupport_createProfile);
        nameSupport = findViewById(R.id.tv_name_uplodSuport_post);
        desccriptionSupport = findViewById(R.id.et_body_uploadSupport_post);
        mainLayoutPost = findViewById(R.id.cl_mainBody_post);
        supportLayout = findViewById(R.id.ll_uplodSuport_post_item);
        titreSupport = findViewById(R.id.et_titre_uploadSupport_post);
        unShowMediaButton = findViewById(R.id.ib_closeImage_createProfile);
        progressBar = findViewById(R.id.pb_post);
        tv_nameUser = findViewById(R.id.tv_name_Creatpost);
        iv_userProfile = findViewById(R.id.iv_userProfile_Createpost_item);
        imageView = findViewById(R.id.iv_post);
        videoView= findViewById(R.id.vv_post);
        postBody = findViewById(R.id.et_postBody_cretePost);
        btnchoosefile = findViewById(R.id.btn_choosefile_post);
        btnuploadfile = findViewById(R.id.btn_uploadfile_post);
        etdesc = findViewById(R.id.et_desc_post);
        btnchosesupport = findViewById(R.id.btn_chooseSupport_post);

        storageReference = FirebaseStorage.getInstance().getReference("User posts");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All videos").child(currentuid);
        db4 = database.getReference("All TextPosts").child(currentuid);
        db3 = database.getReference("All posts");
        userSupport = database.getReference("All support").child(currentuid);



        btnuploadfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dopost();
            }
        });

        btnchoosefile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainLayoutPost.setVisibility(View.VISIBLE);
                supportLayout.setVisibility(View.GONE);
                titreSupport.setText("");
                desccriptionSupport.setText("");
                chooseImage();
            }
        });

        btnchosesupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedUri = null;
                mainLayoutPost.setVisibility(View.GONE);
                supportLayout.setVisibility(View.VISIBLE);
                unshowSupport.setVisibility(View.VISIBLE);
                chooseSupport();
            }
        });



        unshowSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titreSupport.setText("");
                desccriptionSupport.setText("");
                mainLayoutPost.setVisibility(View.VISIBLE);
                supportLayout.setVisibility(View.GONE);
                unshowSupport.setVisibility(View.GONE);
                selectedUri = null;
                supportUri = null;
                type ="text";
                unshowMediaFucntion();
            }
        });

        unShowMediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!type.equals("support")){
                    selectedUri = null;
                    unshowMediaFucntion();
                }

            }
        });



        titreSupport.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameSupport.setText(titreSupport.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
        private void unshowMediaFucntion(){
            postBody.setText(etdesc.getText().toString());
            etdesc.setVisibility(View.GONE);
            postBody.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            unShowMediaButton.setVisibility(View.GONE);
            type ="text";
        }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        // intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_FILE);
        unShowMediaButton.setVisibility(View.VISIBLE);
    }

    private  void chooseSupport(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        // intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_SUPPORT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE || resultCode == RESULT_OK ||

                data != null || data.getData() != null){

            selectedUri = data.getData();


            if (selectedUri.toString().contains("images")){
//                Picasso.get().load(selectedUri).into(imageView);
                Glide.with(getApplicationContext()).load(selectedUri).into(imageView);
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                postBody.setVisibility(View.GONE);
                etdesc.setText(postBody.getText().toString());
                etdesc.setVisibility(View.VISIBLE);
                type = "iv";
            }else if (selectedUri.toString().contains("video")){
                videoView.setMediaController(mediaController);
                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                postBody.setVisibility(View.GONE);
                etdesc.setText(postBody.getText().toString());
                etdesc.setVisibility(View.VISIBLE);
                videoView.setVideoURI(selectedUri);
                videoView.start();
                type = "vv";
            }

        }else {
            Toast.makeText(this, "Aucun fichier sélectioner", Toast.LENGTH_SHORT).show();
        }
        if(requestCode != PICK_FILE){
            if (requestCode == PICK_SUPPORT || resultCode == RESULT_OK ||

                    data != null || data.getData() != null){

                supportUri = data.getData();
                if(supportUri.toString().contains("pdf") ||supportUri.toString().contains("docs")
                        ||supportUri.toString().contains("xlsx") ||supportUri.toString().contains("pptx") ){

                    type="support";

                }else {
                    Toast.makeText(this, "fichier non supporter", Toast.LENGTH_SHORT).show();
                    supportUri =null;
                }
            }

        }


    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("user").document(currentuid);

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()) {
                            name = task.getResult().getString("name");
                            url = task.getResult().getString("url");
                            tv_nameUser.setText(name);
                            if(!url.equals("")){
//                                Picasso.get().load(url).into(iv_userProfile);
                                Glide.with(getApplicationContext()).load(url).into(iv_userProfile);
                            }

                        } else {
                            Toast.makeText(PostActivity.this, "Information personnel erreur !", Toast.LENGTH_SHORT).show();

                        }

                    }
                });


    }

    private  String getFileExtention(Uri uri){
        if( uri == null) return  "";
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    private void Dopost(){
        btnuploadfile.setEnabled(false);
        postmember = new PostMember();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String currentuid = user.getUid();
        final String desc = etdesc.getText().toString();
        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
        final  String savedate = currentdate.format(cdate.getTime());
        Calendar ctime = Calendar.getInstance();
        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
        final String savetime = currenttime.format(ctime.getTime());

        final String time = savedate +":"+ savetime;

        if(type.equals("support")){
            progressBar.setVisibility(View.VISIBLE);
            if(titreSupport.getText().toString().equals("")){
                Toast.makeText(PostActivity.this, "Sasie un titre", Toast.LENGTH_SHORT).show();
                btnuploadfile.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                return;
            }
            if(type.equals("text") && postBody.getText().toString().trim().equals("")) {
                Toast.makeText(getApplication(),"Remplire le champ !",Toast.LENGTH_SHORT).show();
                return;
            }

            StorageReference ref =  FirebaseStorage.getInstance().getReference("Support Users");
            final StorageReference reference = ref.child(System.currentTimeMillis() + "." + getFileExtention(supportUri));
            UploadTask uploadTask2 = reference.putFile(supportUri);
            Task<Uri> urlTask = uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {

                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    return;
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                   if(task.isSuccessful()){
                       Uri dowloadUri = task.getResult();
                       postmember.setDescription(desccriptionSupport.getText().toString());
                       postmember.setTitre(titreSupport.getText().toString()+"."+getFileExtention(supportUri));
                       postmember.setName(name);
                       postmember.setPostUri(dowloadUri.toString());
                       postmember.setTime(time);
                       postmember.setUid(currentuid);
                       postmember.setUrl(url);
                       postmember.setTitreToLower(titreSupport.getText().toString().toLowerCase());
                       postmember.setDscLower(desccriptionSupport.getText().toString().toLowerCase());
                       postmember.setUserToLower(name.toLowerCase());
                       postmember.setType("support");

                       // for image
                       String postKey = userSupport.push().getKey();
                       userSupport.child(postKey).setValue(postmember);
                       // for both
                       postmember.setKey_post(postKey);
                       db3.child(postKey).setValue(postmember);

                       progressBar.setVisibility(View.INVISIBLE);
                       Toast.makeText(PostActivity.this, "Post uploaded", Toast.LENGTH_SHORT).show();
                       finish();
                   }
                }
            });
        }


        if ( selectedUri != null && (type.equals("iv") || type.equals("vv"))){
            progressBar.setVisibility(View.VISIBLE);
            btnuploadfile.setEnabled(false);
            final StorageReference reference = storageReference.child(System.currentTimeMillis()+ "."+getFileExt(selectedUri));
            uploadTask = reference.putFile(selectedUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        if (type.equals("iv")){
                            postmember.setDescription(desc);
                            postmember.setTitre("");
                            postmember.setTitreToLower("");
                            postmember.setDscLower(desc.toLowerCase());
                            postmember.setUserToLower(name.toLowerCase());
                            postmember.setName(name);
                            postmember.setPostUri(downloadUri.toString());
                            postmember.setTime(time);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setType("iv");

                            // for image
                            String postKey = db1.push().getKey();
                            db1.child(postKey).setValue(postmember);
                            // for both
                            postmember.setKey_post(postKey);
                            db3.child(postKey).setValue(postmember);

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "Post uploaded", Toast.LENGTH_SHORT).show();
                            finish();
                        }else if (type.equals("vv")){

                            postmember.setDescription(desc);
                            postmember.setName(name);
                            postmember.setTitre("");
                            postmember.setDscLower(desc.toLowerCase());
                            postmember.setUserToLower(name.toLowerCase());
                            postmember.setPostUri(downloadUri.toString());
                            postmember.setTime(time);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setType("vv");

                            // for video
                            String postKey = db2.push().getKey();
                            db2.child(postKey).setValue(postmember);

                            // for both
                            postmember.setKey_post(postKey);
                            db3.child(postKey).setValue(postmember);

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                            finish();

                        }else {
                            btnuploadfile.setEnabled(true);
                            progressBar.setVisibility(View.INVISIBLE);
                        }


                    }else {
                        Toast.makeText(PostActivity.this, "Erreur de téléchargement", Toast.LENGTH_SHORT).show();
                        btnuploadfile.setEnabled(true);


                    }

                }
            });

        }else if(type.equals("text")){
            progressBar.setVisibility(View.VISIBLE);
            btnuploadfile.setEnabled(false);
            postmember.setDescription(postBody.getText().toString());
            postmember.setName(name);
            postmember.setTitre("");
            postmember.setDscLower(postBody.getText().toString().toLowerCase());
            postmember.setUserToLower(name.toLowerCase());
            postmember.setTime(time);
            postmember.setPostUri("");
            postmember.setUid(currentuid);
            postmember.setUrl(url);
            postmember.setType("text");

            // for video
            String postKey = db4.push().getKey();
            db4.child(postKey).setValue(postmember);

            // for both
            postmember.setKey_post(postKey);
            db3.child(postKey).setValue(postmember);

            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(PostActivity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
            finish();

        }
        hideKeyboard(PostActivity.this);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}