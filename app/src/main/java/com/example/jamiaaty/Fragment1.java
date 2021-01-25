package com.example.jamiaaty;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
//import com.squareup.picasso.Picasso;

public class Fragment1 extends Fragment implements  View.OnClickListener{
    ImageView imageView;
    TextView nameEt, profEt, emailEt,webEt;
    ImageButton imageButtonEdit,imageButtonMenu;
    View view;
    RecyclerView recyclerView;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference postUserRef,reference;
    List<PostMember> listPost  = new ArrayList<>();
    PostAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


          view = inflater.inflate(R.layout.fragment1,container,false);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageView = getActivity().findViewById(R.id.iv_profile_pic);
        nameEt = getActivity().findViewById(R.id.tv_name_profile);
         profEt = getActivity().findViewById(R.id.tv_prof_profile);
        emailEt = getActivity().findViewById(R.id.tv_email_profle);
        webEt = getActivity().findViewById(R.id.tv_website_profile);
        recyclerView = getActivity().findViewById(R.id.rv_post_profile_fragment);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            postUserRef = database.getReference("All userPost").child(user.getUid());
            reference = database.getReference("All Users").child(user.getUid());
        }


        imageButtonEdit = getActivity().findViewById(R.id.ib_edit_f1);
        imageButtonMenu = getActivity().findViewById(R.id.ib_menu_f1);


       imageButtonMenu.setOnClickListener(this);
        imageButtonEdit.setOnClickListener(this);
        imageView.setOnClickListener(this);
        webEt.setOnClickListener(this);


        postUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null && snapshot.hasChildren() !=false){
                    for(DataSnapshot ds :snapshot.getChildren()){
                        PostMember member = ds.getValue(PostMember.class);
                        listPost.add(member);
                    }
                    adapter = new PostAdapter(getActivity(), listPost);
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.ib_edit_f1:
                Intent intent = new Intent(getActivity(),UpdateProfile.class);
                startActivity(intent);
                break;
            case R.id.ib_menu_f1 :
                BottomSheetMen bottomSheetMen = new BottomSheetMen();
                bottomSheetMen.show(getFragmentManager(),"bottomsheet");
                break;
            case R.id.profile_pic :
                Intent intent1 = new Intent(getActivity(),ImageActivity.class);
                startActivity(intent1);
                break;
            case R.id.et_website_cp :
                try {
                    String url = webEt.getText().toString();
                    Intent intent2 = new Intent(Intent.ACTION_VIEW);
                    intent2.setData(Uri.parse(url));
                    startActivity(intent2);

                }catch(Exception e){
                    Toast.makeText(getActivity(),"Ivalid Url",Toast.LENGTH_SHORT).show();
                }

                break;

        }

    }

    @Override
    public void onStart() {
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
                    String urlResult = memeber.getUrl();
                    if(!urlResult.equals("")){
                        try {
                            Glide.with(view.getContext()).load(urlResult).into(imageView);
                        }catch (Exception e){
                            Log.i("Errooor","error while getting img frim db :"+e.getMessage());
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
    }
}
