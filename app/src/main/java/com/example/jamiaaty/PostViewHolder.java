package com.example.jamiaaty;

import android.app.Application;
import android.net.Uri;
import android.view.View;
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
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
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
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
//import com.squareup.picasso.Picasso;

public class PostViewHolder extends RecyclerView.ViewHolder {

    ImageView imageViewprofile, iv_post;
    TextView tv_name, tv_desc, tv_likes, tv_comment, tv_time, tv_nameprofile,nameSuppot;
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


    public void setPost(FragmentActivity activity , String name, String url, String postUri, String time, String uid, String type, String description,String titre){


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

                        startVideIb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                exoPlayer.prepare(videoSource);
                                startVideIb.setVisibility(View.GONE);
                            }
                        });
                        playerView.setVisibility(View.VISIBLE);


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
                    likebtn.setImageResource(R.drawable.ic_like);
                    likescount = (int) snapshot.child(postkey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likescount) + "likes");
                } else {
                    likebtn.setImageResource(R.drawable.ic_dislike);
                    likescount = (int) snapshot.child(postkey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likescount) + "likes");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });

    }

}
