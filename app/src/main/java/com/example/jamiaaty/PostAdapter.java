package com.example.jamiaaty;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jamiaaty.Model.All_UserMemeber;
import com.example.jamiaaty.Model.PostMember;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
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

import java.util.List;
import java.util.zip.Inflater;

public class PostAdapter extends  RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    Context context ;
    List<PostMember> listPost;
    Boolean fvrtChekcker = false;
    Boolean likecheker = false;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference fvrt_listRef,fvrtref,likeRef,allUserPost,reference,commentRef;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUser ="";
    RecyclerView.Adapter adapter;


    public PostAdapter(Context context, List<PostMember> listPost) {
        this.context = context;
        this.listPost = listPost;
        likeRef = database.getReference("post likes");
        fvrtref = database.getReference("favourites_in_poste");
        reference = database.getReference("All posts");
        if(user != null){
            currentUser = user.getUid();
        }
        this.adapter = this;
        //Stocking the actual featured question
    }




    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.post_layout, parent, false);

        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
            PostMember member = listPost.get(position);

        String description = listPost.get(position).getDescription();
        String type = listPost.get(position).getType();
        String name =listPost.get(position).getName();
        String url = listPost.get(position).getUrl();
        String postUri = listPost.get(position).getPostUri();
        String time = listPost.get(position).getTime();
        String userid = listPost.get(position).getUid();
        String postKey = listPost.get(position).getKey_post();


        commentRef = database.getReference("All Comments").child(member.getKey_post());

        fvrt_listRef = database.getReference("favouriteList_user").child(member.getUid());
        allUserPost = database.getReference("All userPost").child(member.getUid());

        holder.setPost(context,member.getName(),member.getUrl(),member.getPostUri(),member.getTime(),member.getUid(),member.getType(),member.getDescription(),member.getTitre());




        holder.dowlnloadSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri downloadUri = Uri.parse(member.getPostUri());
                    DownloadManager.Request request = new DownloadManager.Request(
                            downloadUri);
                    request.setAllowedNetworkTypes(
                            DownloadManager.Request.NETWORK_WIFI
                                    | DownloadManager.Request.NETWORK_MOBILE)
                            .setAllowedOverRoaming(true).setTitle(member.getTitre())
                            .setDescription("Téléchargement de Support")
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, member.getTitre());
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    mgr.enqueue(request);
                    Toast.makeText(context, "Téléchargé avec succés ", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(context, "Donner la pérmission de stockage ", Toast.LENGTH_SHORT).show();

                }
            }
        });




        holder.commentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,ReplyActivity.class);
                intent.putExtra("uid",userid);
                intent.putExtra("q",description);
                intent.putExtra("postkey",member.getKey_post());
                // intent.putExtra("key",privacy);
                context.startActivity(intent);
            }
        });

        holder.menuoptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(name, url, time, userid, type, postUri, postKey,description,position);
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


    @Override
    public int getItemCount() {
        return listPost.size();
    }


    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

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

    private void showDialog(String name, String url, String time, String userid,String type,String postUri,String postKey,String description,int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_options,null);
        TextView download = view.findViewById(R.id.dowload_tv_post);
        TextView delete = view.findViewById(R.id.delete_tv_post);
        TextView copyUrl = view.findViewById(R.id.copyUrl_tv_post);
        TextView share = view.findViewById(R.id.share_tv_post);

        AlertDialog alertDialog = new AlertDialog.Builder(context)
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
                    listPost.remove(position);
                    adapter.notifyItemRemoved(position);
                    Query query = allUserPost;
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(postKey)){
                                allUserPost.child(postKey).removeValue();
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
                if(!postUri.equals("")){
                    StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(postUri);
                    ref.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context,"Post Supprimer",Toast.LENGTH_SHORT).show();
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
                        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
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
                        Toast.makeText(context,  "Téléchargé avec succés ", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();

                    }catch (Exception e){
                        Toast.makeText(context,  "Donner la pérmission de stockage ", Toast.LENGTH_SHORT).show();
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
                        DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);

                        Toast.makeText(context,"Téléchargé avec succés", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }catch (Exception e){
                        Toast.makeText(context,"Donner la pérmission de stockage", Toast.LENGTH_SHORT).show();
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
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent.createChooser(intent,"Share Via"));
                alertDialog.dismiss();

            }
        });

        copyUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cp = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("String", postUri);
                cp.setPrimaryClip(clip);
                clip.getDescription();
                Toast.makeText(context,"",Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

            }
        });

    }



    public class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewprofile, iv_post;
        TextView tv_name, tv_desc, tv_likes, tv_comment, tv_time, tv_nameprofile,nameSuppot,nbcomment;
        ImageButton likebtn, menuoptions, commentbtn,dowlnloadSupport,favorie,startVideIb;
        DatabaseReference likesref,Allusers,favouriteref;
        ConstraintLayout mainBody ;
        CardView supportLayout;
        PlayerView playerView;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid ="";

        int likescount;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null){
                uid = user.getUid();
            }
        }



        public void setPost(FragmentActivity activity , String name, String url, String postUri, String time, String uid, String type, String description, String titre){


            SimpleExoPlayer exoPlayer;
            startVideIb = itemView.findViewById(R.id.start_btn_video_post);
            favorie = itemView.findViewById(R.id.fvrt_post_item);
            dowlnloadSupport = itemView.findViewById(R.id.ib_dowload_uplodSuport_post);
            mainBody = itemView.findViewById(R.id.cl_main_ShowPostBody);
            nameSuppot = itemView.findViewById(R.id.tv_name_showPost_post);
            supportLayout = itemView.findViewById(R.id.cv_support_showPost);
            imageViewprofile = itemView.findViewById(R.id.iv_userProfile_post_item);
            iv_post = itemView.findViewById(R.id.iv_post_item);
            //    tv_comment = itemView.findViewById(R.id.commentbutton_posts);
            tv_desc = itemView.findViewById(R.id.tv_desc_post);
            commentbtn = itemView.findViewById(R.id.commentbutton_post);
            likebtn = itemView.findViewById(R.id.likebutton_post);
            tv_likes = itemView.findViewById(R.id.tv_lkes_post);
            menuoptions = itemView.findViewById(R.id.morebutton_post);
            tv_time = itemView.findViewById(R.id.tv_time_post);
            tv_nameprofile = itemView.findViewById(R.id.tv_name_post);
            playerView = itemView.findViewById(R.id.exoplayer_item_post);
            supportLayout.setVisibility(View.GONE);



            //If the user delete the profile the name will appear is that
            tv_nameprofile.setText(name);
            try {
                Glide.with(activity).load(url).into(imageViewprofile);
            }catch (Exception e){}
            tv_desc.setText(description);
            tv_time.setText(time);
            //Otherwise
            Allusers = database.getReference("All Users").child(uid);
            Allusers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        All_UserMemeber memeber = snapshot.getValue(All_UserMemeber.class);
                        if(memeber != null){
                            tv_nameprofile.setText(memeber.getName());
                            if(!memeber.getUrl().equals("")){
//                        Picasso.get().load(memeber.getUrl()).into(imageViewprofile);
                                Glide.with(activity).load(memeber.getUrl()).into(imageViewprofile);
                            }
                        }

                    }catch (Exception e){}

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            if(type.equals("iv")){
                //For image Post
                Glide.with(activity.getApplicationContext()).load(postUri).into(iv_post);
                tv_desc.setText(description);
                tv_time.setText(time);
                iv_post.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.GONE);
                supportLayout.setVisibility(View.GONE);
            }else if(type.equals("vv")) {
                //For Video Post
                playerView.setVisibility(View.VISIBLE);
                iv_post.setVisibility(View.GONE);
            }else if(type.equals("text")){
                playerView.setVisibility(View.GONE);
                iv_post.setVisibility(View.GONE);
                supportLayout.setVisibility(View.GONE);
            }else if(type.equals("support")){
                playerView.setVisibility(View.GONE);
                iv_post.setVisibility(View.GONE);
                supportLayout.setVisibility(View.VISIBLE);
                if(titre.trim().equals("")){
                    nameSuppot.setText("support");
                }else{
                    nameSuppot.setText(titre);
                }
            }
            if(type.equals("vv")){
                startVideIb.setVisibility(View.VISIBLE);
              /*
                try {
                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(activity).build();
                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                    exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(activity);
                    Uri video = Uri.parse(postUri);
                    DefaultHttpDataSourceFactory df = new DefaultHttpDataSourceFactory("video");
                    ExtractorsFactory ef = new DefaultExtractorsFactory();
                    MediaSource mediaSource = new ExtractorMediaSource(video,df,ef,null,null);
                    playerView.setPlayer(exoPlayer);
                    exoPlayer.prepare(mediaSource);
                    exoPlayer.setPlayWhenReady(false);

                }catch(Exception e){
                    Toast.makeText(activity, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
*/

                try {
                    // Create a default TrackSelector
                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                    TrackSelection.Factory videoTrackSelectionFactory =
                            new AdaptiveTrackSelection.Factory(bandwidthMeter);
                    TrackSelector trackSelector =
                            new DefaultTrackSelector(videoTrackSelectionFactory);

                    //Initialize the player
                    exoPlayer = ExoPlayerFactory.newSimpleInstance(activity.getApplicationContext(), trackSelector);
                    playerView.setPlayer(exoPlayer);

                    //Initialize simpleExoPlayerView

                    // Produces DataSource instances through which media data is loaded.
                    DataSource.Factory dataSourceFactory =
                            new DefaultDataSourceFactory(activity.getApplicationContext(), Util.getUserAgent(activity.getApplicationContext(), "CloudinaryExoplayer"));

                    // Produces Extractor instances for parsing the media data.
                    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

                    // This is the MediaSource representing the media to be played.
                    Uri videoUri = Uri.parse(postUri);
                    MediaSource videoSource = new ExtractorMediaSource(videoUri,
                            dataSourceFactory, extractorsFactory, null, null);

                    // Prepare the player with the source.

                    playerView.setVisibility(View.VISIBLE);

                    startVideIb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exoPlayer.prepare(videoSource);
                            startVideIb.setVisibility(View.GONE);

                        }
                    });








                }catch (Exception e){}
            }

        }
        public void setPost(Context activity , String name, String url, String postUri, String time, String uid, String type, String description, String titre){


            SimpleExoPlayer exoPlayer;
            startVideIb = itemView.findViewById(R.id.start_btn_video_post);
            favorie = itemView.findViewById(R.id.fvrt_post_item);
            dowlnloadSupport = itemView.findViewById(R.id.ib_dowload_uplodSuport_post);
            mainBody = itemView.findViewById(R.id.cl_main_ShowPostBody);
            nameSuppot = itemView.findViewById(R.id.tv_name_showPost_post);
            supportLayout = itemView.findViewById(R.id.cv_support_showPost);
            imageViewprofile = itemView.findViewById(R.id.iv_userProfile_post_item);
            iv_post = itemView.findViewById(R.id.iv_post_item);
            //    tv_comment = itemView.findViewById(R.id.commentbutton_posts);
            tv_desc = itemView.findViewById(R.id.tv_desc_post);
            commentbtn = itemView.findViewById(R.id.commentbutton_post);
            likebtn = itemView.findViewById(R.id.likebutton_post);
            tv_likes = itemView.findViewById(R.id.tv_lkes_post);
            menuoptions = itemView.findViewById(R.id.morebutton_post);
            tv_time = itemView.findViewById(R.id.tv_time_post);
            tv_nameprofile = itemView.findViewById(R.id.tv_name_post);
            playerView = itemView.findViewById(R.id.exoplayer_item_post);
            supportLayout.setVisibility(View.GONE);
            nbcomment = itemView.findViewById(R.id.tv_comment_post);



            //nombre de  Commentaire
            commentRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        nbcomment.setText((int)snapshot.getChildrenCount()+"");
                    }catch (Exception e){}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //If the user delete the profile the name will appear is that
            tv_nameprofile.setText(name);
            try {
                Glide.with(activity).load(url).into(imageViewprofile);
            }catch (Exception e){}
            tv_desc.setText(description);
            tv_time.setText(time);
            //Otherwise
            Allusers = database.getReference("All Users").child(uid);
            Allusers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        All_UserMemeber memeber = snapshot.getValue(All_UserMemeber.class);
                        if(memeber != null){
                            tv_nameprofile.setText(memeber.getName());
                            if(!memeber.getUrl().equals("")){
//                        Picasso.get().load(memeber.getUrl()).into(imageViewprofile);
                                Glide.with(activity).load(memeber.getUrl()).into(imageViewprofile);
                            }
                        }

                    }catch (Exception e){}

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            if(type.equals("iv")){
                //For image Post
                Glide.with(activity.getApplicationContext()).load(postUri).into(iv_post);
                tv_desc.setText(description);
                tv_time.setText(time);
                iv_post.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.GONE);
                supportLayout.setVisibility(View.GONE);
            }else if(type.equals("vv")) {
                //For Video Post
                playerView.setVisibility(View.VISIBLE);
                iv_post.setVisibility(View.GONE);
            }else if(type.equals("text")){
                playerView.setVisibility(View.GONE);
                iv_post.setVisibility(View.GONE);
                supportLayout.setVisibility(View.GONE);
            }else if(type.equals("support")){
                playerView.setVisibility(View.GONE);
                iv_post.setVisibility(View.GONE);
                supportLayout.setVisibility(View.VISIBLE);
                if(titre.trim().equals("")){
                    nameSuppot.setText("support");
                }else{
                    nameSuppot.setText(titre);
                }
            }
            if(type.equals("vv")){
                startVideIb.setVisibility(View.VISIBLE);
              /*
                try {
                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(activity).build();
                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                    exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(activity);
                    Uri video = Uri.parse(postUri);
                    DefaultHttpDataSourceFactory df = new DefaultHttpDataSourceFactory("video");
                    ExtractorsFactory ef = new DefaultExtractorsFactory();
                    MediaSource mediaSource = new ExtractorMediaSource(video,df,ef,null,null);
                    playerView.setPlayer(exoPlayer);
                    exoPlayer.prepare(mediaSource);
                    exoPlayer.setPlayWhenReady(false);

                }catch(Exception e){
                    Toast.makeText(activity, "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
*/

                try {
                    // Create a default TrackSelector
                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                    TrackSelection.Factory videoTrackSelectionFactory =
                            new AdaptiveTrackSelection.Factory(bandwidthMeter);
                    TrackSelector trackSelector =
                            new DefaultTrackSelector(videoTrackSelectionFactory);

                    //Initialize the player
                    exoPlayer = ExoPlayerFactory.newSimpleInstance(activity.getApplicationContext(), trackSelector);
                    playerView.setPlayer(exoPlayer);

                    //Initialize simpleExoPlayerView

                    // Produces DataSource instances through which media data is loaded.
                    DataSource.Factory dataSourceFactory =
                            new DefaultDataSourceFactory(activity.getApplicationContext(), Util.getUserAgent(activity.getApplicationContext(), "CloudinaryExoplayer"));

                    // Produces Extractor instances for parsing the media data.
                    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

                    // This is the MediaSource representing the media to be played.
                    Uri videoUri = Uri.parse(postUri);
                    MediaSource videoSource = new ExtractorMediaSource(videoUri,
                            dataSourceFactory, extractorsFactory, null, null);

                    // Prepare the player with the source.

                    playerView.setVisibility(View.VISIBLE);

                    startVideIb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            exoPlayer.prepare(videoSource);
                            startVideIb.setVisibility(View.GONE);

                        }
                    });








                }catch (Exception e){}
            }

        }

        public  void favouriteCheker(String postKey){
            favouriteref = database.getReference("favourites_in_poste");
            FirebaseUser user   = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null){
                String uid = user.getUid();
                favouriteref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child(postKey).hasChild(uid)){
                            favorie.setImageResource(R.drawable.ic_baseline_turned_in_24);
                        }else {
                            favorie.setImageResource(R.drawable.ic_baseline_turned_in_not_24);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }


        public void likeschecker(final String postkey) {
            likebtn = itemView.findViewById(R.id.likebutton_post);
            likesref = database.getReference("post likes");


            likesref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.child(postkey).hasChild(uid)) {
                        likebtn.setImageResource(R.drawable.ic_like_post);
                        likescount = (int) snapshot.child(postkey).getChildrenCount();
                        tv_likes.setText(Integer.toString(likescount).trim());
                    } else {
                        likebtn.setImageResource(R.drawable.ic_dislike);
                        likescount = (int) snapshot.child(postkey).getChildrenCount();
                        tv_likes.setText( String.valueOf( likescount).trim() );
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }

            });

        }

    }
}
