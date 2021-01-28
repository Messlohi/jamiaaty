package com.example.jamiaaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jamiaaty.Model.PostMember;
import com.example.jamiaaty.Model.QuestionMember;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RelatedPostsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference,likeRef,fvrt_listRef,fvrtref,db1,db2,db4,reference;
    Boolean likecheker = false;
    String currentUser ="";
    Boolean fvrtChekcker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_posts);

        recyclerView = findViewById(R.id.rv_related_postes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseRecyclerAdapter<PostMember,PostViewHolder> firebaseRecyclerAdapter;
        if(user != null) {
            currentUser = user.getUid();
            databaseReference = database.getReference("favouriteList_user").child(currentUser);
            likeRef = database.getReference("post likes");
            fvrt_listRef = database.getReference("favouriteList_user").child(currentUser);
            fvrtref = database.getReference("favourites_in_poste");
            db1 = database.getReference("All images").child(currentUser);
            db2 = database.getReference("All videos").child(currentUser);
            db4 = database.getReference("All TextPosts").child(currentUser);
            reference = database.getReference("All posts");



            FirebaseRecyclerOptions<PostMember> options = new FirebaseRecyclerOptions.Builder<PostMember>()
                    .setQuery(databaseReference, PostMember.class)
                    .build();
            firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<PostMember, PostViewHolder>(options) {
                        @NonNull
                        @Override
                        public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
                            return new PostViewHolder(view);
                        }

                        @Override
                        protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostMember model) {

                            final String postKey = getRef(position).getKey();

                            holder.setPost(RelatedPostsActivity.this, model.getName(), model.getUrl(), model.getPostUri(), model.getTime(), model.getUid(), model.getType(), model.getDescription(), model.getTitre());

                            String description = getItem(position).getDescription();
                            String type = getItem(position).getType();
                            String name = getItem(position).getName();
                            String url = getItem(position).getUrl();
                            String postUri = getItem(position).getPostUri();
                            String time = getItem(position).getTime();
                            String userid = getItem(position).getUid();


                            holder.dowlnloadSupport.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        DownloadManager mgr = (DownloadManager) getApplication().getSystemService(Context.DOWNLOAD_SERVICE);
                                        Uri downloadUri = Uri.parse(postUri);
                                        DownloadManager.Request request = new DownloadManager.Request(
                                                downloadUri);
                                        request.setAllowedNetworkTypes(
                                                DownloadManager.Request.NETWORK_WIFI
                                                        | DownloadManager.Request.NETWORK_MOBILE)
                                                .setAllowedOverRoaming(true).setTitle(model.getTitre())
                                                .setDescription("Téléchargement de Support")
                                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, model.getTitre());
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        mgr.enqueue(request);
                                        Toast.makeText(getApplication(), "Téléchargé avec succés ", Toast.LENGTH_SHORT).show();

                                    } catch (Exception e) {
                                        Toast.makeText(getApplication(), "Donner la pérmission de stockage ", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                            holder.commentbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplication(), ReplyActivity.class);
                                    intent.putExtra("uid", userid);
                                    intent.putExtra("q", description);
                                    intent.putExtra("postkey", postKey);
                                    // intent.putExtra("key",privacy);
                                    startActivity(intent);
                                }
                            });
                            holder.menuoptions.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog(name, url, time, userid, type, postUri, postKey, description);
                                }
                            });

                            holder.likeschecker(postKey);

                            holder.likebtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // this "likecheker" to not enter in a loop(of onDataChange)
                                    likecheker = true;
                                    likeRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (likecheker.equals(true)) {
                                                if (snapshot.child(postKey).hasChild(currentUser)) {
                                                    likeRef.child(postKey).child(currentUser).removeValue();
                                                } else {
                                                    likeRef.child(postKey).child(currentUser).setValue(true);
                                                }
                                                likecheker = false;

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }
                            });

                            holder.favouriteCheker(postKey);
                            holder.favorie.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // this "fvrtCheker" to not enter in a loop(of onDataChange)
                                    fvrtChekcker = true;
                                    fvrtref.addValueEventListener(new ValueEventListener() {
                                        PostMember postMember = new PostMember();

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (fvrtChekcker.equals(true)) {
                                                if (snapshot.child(postKey).hasChild(currentUser)) {
                                                    fvrtref.child(postKey).child(currentUser).removeValue();
                                                    delete(time);
                                                    fvrtChekcker = false;
                                                } else {
                                                    fvrtref.child(postKey).child(currentUser).setValue(true);

                                                    postMember.setTitre("");
                                                    postMember.setUrl(url);
                                                    postMember.setPostUri(postUri);
                                                    postMember.setName(name);
                                                    postMember.setType(type);
                                                    postMember.setDescription(description);
                                                    postMember.setTime(time);
                                                    postMember.setUid(userid);

                                                    fvrt_listRef.child(postKey).setValue(postMember);
                                                    fvrtChekcker = false;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });


                        }



                    };

            recyclerView.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();
        }

    }

    private void showDialog(String name, String url, String time, String userid,String type,String postUri,String postKey,String description) {
        LayoutInflater inflater = LayoutInflater.from(RelatedPostsActivity.this);
        View view = inflater.inflate(R.layout.post_options,null);
        TextView download = view.findViewById(R.id.dowload_tv_post);
        TextView delete = view.findViewById(R.id.delete_tv_post);
        TextView copyUrl = view.findViewById(R.id.copyUrl_tv_post);
        TextView share = view.findViewById(R.id.share_tv_post);

        AlertDialog alertDialog = new AlertDialog.Builder(RelatedPostsActivity.this)
                .setView(view)
                .create();
        alertDialog.show();


        if(currentUser.equals(userid)){
            delete.setVisibility(View.VISIBLE);
        }else {
            delete.setVisibility(View.GONE);
        }
        if(type.equals("text") || type.equals("support")){
            download.setVisibility(View.GONE);
        }else {
            download.setVisibility(View.VISIBLE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("iv")){
                    Query query = db1;
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(postKey)){
                                db1.child(postKey).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(type.equals("vv")){
                    Query query1 = db2;
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(postKey)){
                                db2.child(postKey).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                if(type.equals("text")){
                    Query query4 = db4;
                    query4.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(postKey)){
                                db4.child(postKey).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                Query query2 = reference;
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(postKey)){
                            reference.child(postKey).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Query query3 = likeRef;
                query3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(postKey)){
                            likeRef.child(postKey).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if(postUri.equals("")){
                    StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(postUri);
                    ref.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(),"Post Supprimé",Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                alertDialog.dismiss();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("iv")){

                    try {
                        DownloadManager mgr = (DownloadManager) getApplication().getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri downloadUri = Uri.parse(postUri);
                        DownloadManager.Request request = new DownloadManager.Request(
                                downloadUri);
                        request.setAllowedNetworkTypes(
                                DownloadManager.Request.NETWORK_WIFI
                                        | DownloadManager.Request.NETWORK_MOBILE)
                                .setAllowedOverRoaming(true).setTitle(name+"_"+System.currentTimeMillis()+".jpg")
                                .setDescription("Téléchargement d'image")
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name+"_"+System.currentTimeMillis()+".jpg");
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        mgr.enqueue(request);
                        Toast.makeText(getApplicationContext(),  "Téléchargé avec succés ", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();

                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),  "Donner la pérmission de stockage ", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }

                }else if(type.equals("vv")){

                    try {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(postUri));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        request.setTitle("Download");
                        request.setDescription("Downloading Video..");
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name+"_"+System.currentTimeMillis()+".mp4");
                        DownloadManager manager = (DownloadManager)getApplication().getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);

                        Toast.makeText(getApplicationContext(),"Téléchargé avec succés", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Donner la pérmission de stockage", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();

                    }

                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sharetext ="";
                if(type.equals("text")){
                    sharetext = description +"\n"+ "\n" +"Post de:"+name+"\n"+"Photo de profile :"+url;
                }else  {
                    sharetext = "Contenu de post (Media):"+postUri +"\n" +"Post de:"+name+"\n"+"\n"+"Photo de profile :"+url;
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,sharetext);
                startActivity(intent.createChooser(intent,"Share Via"));
                alertDialog.dismiss();

            }
        });


        copyUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cp = (ClipboardManager)getApplication().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("String", postUri);
                cp.setPrimaryClip(clip);
                clip.getDescription();
                Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

            }
        });

    }

    private void delete(String time) {
        Query query = fvrt_listRef.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

