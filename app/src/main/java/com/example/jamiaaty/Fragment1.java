package com.example.jamiaaty;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jamiaaty.Home.Module_pack.Module;
import com.example.jamiaaty.Home.Module_pack.ModuleCardAdapter;
import com.example.jamiaaty.Home.localdb.localdb;
import com.example.jamiaaty.Model.All_UserMemeber;
import com.example.jamiaaty.Model.PostMember;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import com.squareup.picasso.Picasso;

public class Fragment1 extends Fragment implements  View.OnClickListener{
    ImageView imageView;
    TextView nameEt, profEt, emailEt,webEt;
    ImageButton imageButtonEdit,imageButtonMenu;
    TextView nbPubTv,nbAbonTv,nbAbonmTv,pubTv,abonTv,abonmTv,infoRvTV;
    View view;
    RecyclerView recyclerView,recyclerViewAbon,recyclerViewAbonm;
    FirebaseAuth auth;
    String webResult="";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference postUserRef,reference,Allusers;
    List<PostMember> listPost  = new ArrayList<>();
    List<All_UserMemeber> listFollowMe  = new ArrayList<>();
    List<All_UserMemeber> listIFollow  = new ArrayList<>();
    RecyclerView.Adapter adapter;RecyclerView.Adapter adapter3;
    All_userAdapter adapter2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


          view = inflater.inflate(R.layout.fragment1,container,false);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        infoRvTV =getActivity().findViewById(R.id.tv_showingInrv);
        abonmTv = getActivity().findViewById(R.id.tv_abonm_profile);
        abonTv = getActivity().findViewById(R.id.tv_abon_profile);
        pubTv = getActivity().findViewById(R.id.tv_publication_profile);
        nbPubTv = getActivity().findViewById(R.id.tv_nbPub_profile);
        nbAbonTv = getActivity().findViewById(R.id.tv_nbAbon_profile);
        nbAbonmTv = getActivity().findViewById(R.id.tv_nbAbonm_profile);
        imageView = getActivity().findViewById(R.id.iv_profile_pic);
        nameEt = getActivity().findViewById(R.id.tv_name_profile);
         profEt = getActivity().findViewById(R.id.tv_prof_profile);
        emailEt = getActivity().findViewById(R.id.tv_email_profle);
        webEt = getActivity().findViewById(R.id.tv_website_profile);
        recyclerView = getActivity().findViewById(R.id.rv_post_profile_fragment);
        recyclerViewAbon = getActivity().findViewById(R.id.rv_abon_profile_fragment);
        recyclerViewAbonm = getActivity().findViewById(R.id.rv_abonm_profile_fragment);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        recyclerViewAbon.setHasFixedSize(true);
        recyclerViewAbon.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        recyclerViewAbonm.setHasFixedSize(true);
        recyclerViewAbonm.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            postUserRef = database.getReference("All userPost").child(user.getUid());
            reference = database.getReference("All Users").child(user.getUid());
            Allusers = database.getReference("All Users");
        }

        auth = FirebaseAuth.getInstance();


        imageButtonEdit = getActivity().findViewById(R.id.ib_edit_f1);
        imageButtonMenu = getActivity().findViewById(R.id.ib_menu_f1);


       imageButtonMenu.setOnClickListener(this);
        imageButtonEdit.setOnClickListener(this);
        imageView.setOnClickListener(this);
        webEt.setOnClickListener(this);
        pubTv.setOnClickListener(this);
        abonTv.setOnClickListener(this);
        abonmTv.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.ib_edit_f1:
                imageButtonEdit.setEnabled(false);
                Intent intent = new Intent(getActivity(),UpdateProfile.class);
                startActivity(intent);
                break;
            case R.id.ib_menu_f1 :
                imageButtonMenu.setEnabled(false);
                logout();
                break;
            case R.id.iv_profile_pic :
                imageView.setEnabled(false);
                Intent intent1 = new Intent(getActivity(),ImageActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);

                break;
            case R.id.tv_website_profile :
                try {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webResult));
                    startActivity(browserIntent);
                }catch(Exception e){
                    Toast.makeText(getActivity(),"Ivalid Url",Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.tv_publication_profile :
                postUserRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        infoRvTV.setText("Publication");
                        recyclerViewAbon.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerViewAbonm.setVisibility(View.GONE);
                        listPost.clear();
                        try {
                            if(snapshot.getValue()!=null && snapshot.hasChildren() !=false){
                                for(DataSnapshot ds :snapshot.getChildren()){
                                    PostMember member = ds.getValue(PostMember.class);
                                    listPost.add(member);
                                }
                                adapter = new PostAdapter(getActivity(), listPost);
                                recyclerView.setAdapter(adapter);
                            }
                        }catch (Exception e){}

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case R.id.tv_abon_profile :
                reference.child("FollowMeList").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        infoRvTV.setText("Abonnés");
                        recyclerViewAbon.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        recyclerViewAbonm.setVisibility(View.GONE);
                        String userId = "";
                        try {
                            GenericTypeIndicator<HashMap<String, Boolean>> to = new
                                    GenericTypeIndicator<HashMap<String, Boolean>>() {};
                            HashMap<String, Boolean> model = snapshot.getValue(to);
                            for(Map.Entry<String, Boolean> entry: model.entrySet()) {
                                userId = entry.getKey();
                                break;
                            }
                            if(!userId.isEmpty()){
                                Allusers.child(userId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        listFollowMe.clear();
                                        try {
                                            All_UserMemeber member = snapshot.getValue(All_UserMemeber.class);
                                            listFollowMe.add(member);
                                            adapter3 = new All_userAdapter(getActivity().getApplicationContext(), listFollowMe,false);
                                            recyclerViewAbonm.setAdapter(adapter3);
                                        }catch (Exception e){}
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }

                        }catch (Exception e){}


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;

            case R.id.tv_abonm_profile :

                reference.child("IFollowList").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        infoRvTV.setText("Abonnemments");
                        recyclerViewAbon.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        recyclerViewAbonm.setVisibility(View.VISIBLE);
                        String userId = "";
                        try {
                            GenericTypeIndicator<HashMap<String, Boolean>> to = new
                                    GenericTypeIndicator<HashMap<String, Boolean>>() {};
                            HashMap<String, Boolean> model = snapshot.getValue(to);
                            for(Map.Entry<String, Boolean> entry: model.entrySet()) {
                                userId = entry.getKey();
                                break;
                            }
                            if(!userId.isEmpty()){
                                Allusers.child(userId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        listIFollow.clear();
                                        try {
                                            All_UserMemeber member = snapshot.getValue(All_UserMemeber.class);

                                            listIFollow.add(member);
                                            adapter2 = new All_userAdapter(getActivity().getApplicationContext(), listIFollow,false);
                                            recyclerViewAbonm.setAdapter(adapter2);


                                        }catch (Exception e){}
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }

                        }catch (Exception e){}



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;

        }

    }


    private  void logout(){
        AlertDialog.Builder   builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Déconnection").setMessage("Vous voulez sortir !")
                .setPositiveButton("Se déconnecter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth.signOut();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                }).setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }


    @Override
    public void onStart() {
        super.onStart();
        imageView.setEnabled(true);
        imageButtonMenu.setEnabled(true);
        imageButtonEdit.setEnabled(true);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    All_UserMemeber memeber = snapshot.getValue(All_UserMemeber.class);
                    String nameResult = memeber.getName();
                    String emailResult = memeber.getEmail();
                    String profResult = memeber.getProf();
                     webResult = memeber.getWeb();
                    String urlResult = memeber.getUrl();
                    if(!urlResult.equals("")){
                        try {
                            Glide.with(view.getContext()).load(urlResult).into(imageView);
                        }catch (Exception e){
                            Log.i("Erreur","Erreur Base de Donnée :"+e.getMessage());
                        }
                    }
                    nameEt.setText(nameResult);
                    emailEt.setText(emailResult);
                    webEt.setText(webResult);
                    profEt.setText(profResult);

                }catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("IFollowList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    nbAbonmTv.setText((int)snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("FollowMeList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nbAbonTv.setText((int)snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        postUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPost.clear();
                nbPubTv.setText((int)snapshot.getChildrenCount()+"");
                try {
                    if(snapshot.getValue()!=null && snapshot.hasChildren() !=false){
                        for(DataSnapshot ds :snapshot.getChildren()){
                            PostMember member = ds.getValue(PostMember.class);
                            listPost.add(member);
                        }
                        adapter = new PostAdapter(getActivity(), listPost);
                        recyclerView.setAdapter(adapter);
                    }
                }catch (Exception e){}

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}
