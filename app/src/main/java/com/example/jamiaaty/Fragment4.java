package com.example.jamiaaty;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class Fragment4 extends Fragment {

    Button btn_createPost;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference,likeRef,db1,db2;
    Boolean likecheker = false;
    String currentUser ="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment4,container,false);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            currentUser =user.getUid();
        }
        btn_createPost = getActivity().findViewById(R.id.createpost_f4);

        reference = database.getReference("All posts");
        likeRef = database.getReference("post likes");
        recyclerView = getActivity().findViewById(R.id.rv_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        db1 = database.getReference("All images").child(currentUser);
        db2 = database.getReference("All videos").child(currentUser);



        btn_createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PostActivity.class);
                startActivity(intent);
            }
        });

        FirebaseRecyclerOptions<PostMember> options = new FirebaseRecyclerOptions.Builder<PostMember>()
                .setQuery(reference,PostMember.class)
                .build();
        FirebaseRecyclerAdapter<PostMember, PostViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<PostMember, PostViewHolder>(options){
                    @NonNull
                    @Override
                    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout,parent,false);
                        return new PostViewHolder(view);

                    }

                    @Override
                    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostMember model) {

                        final String postKey = getRef(position).getKey();
                        holder.setPost(getActivity(),model.getName(),model.getUrl(),model.getPostUri(),model.getTime(),model.getUid(),model.getType(),model.getDescription());


                        //String description = getItem(position).getDescription();
                        String type = getItem(position).getType();
                        String name = getItem(position).getName();
                        String url = getItem(position).getUrl();
                        String postUri = getItem(position).getPostUri();
                        String time = getItem(position).getTime();
                        String userid = getItem(position).getUid();



                        holder.menuoptions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog(name,url,time,userid,type,postUri,postKey);
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
                                        if(likecheker.equals(true)){
                                            if(snapshot.child(postKey).hasChild(currentUser)){
                                                likeRef.child(postKey).child(currentUser).removeValue();
                                            }else {
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
                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void showDialog(String name, String url, String time, String userid,String type,String postUri,String postKey) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.post_options,null);
        TextView download = view.findViewById(R.id.dowload_tv_post);
        TextView delete = view.findViewById(R.id.delete_tv_post);
        TextView copyUrl = view.findViewById(R.id.copyUrl_tv_post);
        TextView share = view.findViewById(R.id.share_tv_post);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
                alertDialog.show();


        if(currentUser.equals(userid)){
            delete.setVisibility(View.VISIBLE);
        }else {
            delete.setVisibility(View.GONE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                alertDialog.dismiss();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("iv")){

                 try {
                     DownloadManager mgr = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
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
                     Toast.makeText(getActivity(),  "Téléchargé avec succés ", Toast.LENGTH_SHORT).show();
                     alertDialog.dismiss();

                 }catch (Exception e){
                     Toast.makeText(getActivity(),  "Donner la pérmission de stockage ", Toast.LENGTH_SHORT).show();
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
                        DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);

                        Toast.makeText(getActivity(),"Téléchargé avec succés", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }catch (Exception e){
                        Toast.makeText(getActivity(),"Donner la pérmission de stockage", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();

                    }

                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sharetext = name +"\n"+ "\n" +url;
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
                ClipboardManager cp = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("String", postUri);
                cp.setPrimaryClip(clip);
                clip.getDescription();
                Toast.makeText(getActivity(),"",Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

            }
        });





    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
