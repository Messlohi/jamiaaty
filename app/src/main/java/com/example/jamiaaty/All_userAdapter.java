package com.example.jamiaaty;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class All_userAdapter extends RecyclerView.Adapter<All_userAdapter.All_userViewHolder> {

    Context context;
    List<All_UserMemeber> listUser;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allUserRef,IFollowRef,FollowMeList,chatRef;
    String currentUserId = "";
    Boolean followCheker = false;
    Boolean FollowMeCheker = false;




    public All_userAdapter(Context context, List<All_UserMemeber> listUser) {
        this.context = context;
        this.listUser = listUser;
        if(user != null){
            currentUserId = user.getUid();
        }
        IFollowRef = database.getReference("All Users").child(currentUserId).child("IFollowList");
    }

    @NonNull
    @Override
    public All_userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.usercard_layout, parent, false);

        /*
        // match_parent won't work for RecyclerView (sigh)
        final ViewGroup.LayoutParams lp = v.getLayoutParams();
        lp.width = parent.getWidth();
        lp.height = parent.getHeight();
        v.setLayoutParams(lp);

         */

        return new All_userViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull All_userViewHolder holder, int position) {

        All_UserMemeber model = listUser.get(position);
        holder.setUser(model.getName(),model.getProf(),model.getUid(),model.getUrl());

        String chatKey ="";
        holder.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra("rName",model.getName());
                intent.putExtra("rUrl",model.getUrl());
                intent.putExtra("rId",model.getUid());
                intent.putExtra("chatKey",getChatKey(currentUserId,model.getUid()));
                context.startActivity(intent);
            }
        });
        FollowMeList = database.getReference("All Users").child(model.getUid()).child("FollowMeList");
        holder.checkFollow(model.getUid());
        holder.addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handelFollow(model.getUid());
            }
        });
        holder.requestString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handelFollow(model.getUid());

            }
        });
    }

    private static String getChatKey(String uid1,String uid2){
        int size = uid1.length()>=uid2.length()?uid1.length():uid2.length();
        for(int i=0 ;i<size;i++){
            if(uid1.charAt(i) == uid2.charAt(i)) continue;
            if(uid1.charAt(i)>uid2.charAt(i)) return uid1+uid2;
            if(uid2.charAt(i)>uid1.charAt(i)) return uid2+uid1;
        }
        return uid1.length()>=uid2.length()?uid1+uid2:uid2+uid1;
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    private  void handelFollow(String uid){
        followCheker =true;
        IFollowRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(followCheker.equals(true)){
                    if(snapshot.hasChild(uid)){
                        IFollowRef.child(uid).removeValue();
                        followCheker = false;

                    }else {
                        IFollowRef.child(uid).setValue(true);
                        followCheker = false;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FollowMeCheker = true;
        FollowMeList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(FollowMeCheker.equals(true)){
                    if(snapshot.hasChild(currentUserId)){
                        FollowMeList.child(currentUserId).removeValue();
                        FollowMeCheker = false;

                    }else {
                        FollowMeList.child(currentUserId).setValue(true);
                        FollowMeCheker = false;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }




    public class All_userViewHolder  extends RecyclerView.ViewHolder {

        ImageView imageViewProfile;
        TextView tv_name,tv_prof;
        ImageButton addUser;
        TextView requestString;


        public All_userViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name_usercard);
            tv_prof = itemView.findViewById(R.id.tv_prof_usercard);
            imageViewProfile = itemView.findViewById(R.id.iv_imageProfile_usercard);
            addUser = itemView.findViewById(R.id.addFreind_userCard);
            requestString = itemView.findViewById(R.id.tv_relation_usercard);
        }


        public  void  setUser(String name, String prof, String uid, String url){
            if(url !=""){
                Picasso.get().load(url).into(imageViewProfile);
            }
            tv_prof.setText(prof);
            tv_name.setText(name);
        }

        public  void checkFollow(String uid){
            IFollowRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(uid)){
                        requestString.setText("Suivi");
                        addUser.setImageResource(R.drawable.ic_baseline_close_red);
                    }else{
                        requestString.setText("Suivre");
                        addUser.setImageResource(R.drawable.ic_baseline_add_24);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}
