package com.example.jamiaaty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jamiaaty.Model.PostMember;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

String name,website,email,urlProfile,uid,prof;

TextView nameTv,webTv,emailTv,profTV,nbPubTv,nbAbonTv,nbAbonmTv;
ImageView userIv;
RecyclerView recyclerView ;

FirebaseDatabase database = FirebaseDatabase.getInstance();
DatabaseReference postUserRef,userRef;
List<PostMember> listPost  = new ArrayList<>();
PostAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        nbPubTv = findViewById(R.id.tv_nbPub_profile);
        nbAbonTv = findViewById(R.id.tv_nbAbon_profile);
        nbAbonmTv = findViewById(R.id.tv_nbAbonm_profile);
        nameTv = findViewById(R.id.tv_name_profile);
        webTv = findViewById(R.id.tv_website_profile);
        emailTv = findViewById(R.id.tv_email_profle);
        profTV = findViewById(R.id.tv_prof_profile);
        userIv = findViewById(R.id.iv_profile_user);
        recyclerView = findViewById(R.id.rv_post_profile);
//        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));




        Bundle extras = getIntent().getExtras();
        if(extras != null){
            name = extras.getString("name");
            website = extras.getString("web");
            uid = extras.getString("uid");
            urlProfile = extras.getString("url");
            prof = extras.getString("prof");
            email = extras.getString("email");
            postUserRef = database.getReference("All userPost").child(uid);
            userRef = database.getReference("All Users").child(uid);

            nameTv.setText(name);
            webTv.setText(website);
            profTV.setText(prof);
            emailTv.setText(email);
            if(!urlProfile.isEmpty()){
                Glide.with(getApplicationContext()).load(urlProfile).into(userIv);
            }


            postUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue()!=null && snapshot.hasChildren() !=false){
                        nbPubTv.setText((int)snapshot.getChildrenCount()+"");
                        for(DataSnapshot ds :snapshot.getChildren()){
                            PostMember member = ds.getValue(PostMember.class);
                            listPost.add(member);
                        }
                        adapter = new PostAdapter(ProfileActivity.this, listPost);
                        recyclerView.setAdapter(adapter);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        userRef.child("IFollowList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nbAbonmTv.setText((int)snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userRef.child("FollowMeList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nbAbonTv.setText((int)snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}