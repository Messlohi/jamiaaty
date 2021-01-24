package com.example.jamiaaty;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.jamiaaty.Model.All_UserMemeber;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;

import java.util.List;

public class All_userAdapter extends RecyclerView.Adapter<All_userAdapter.All_userViewHolder> {

    Context context= null;
    List<All_UserMemeber> listUser;
    Application application=null;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allUserRef,IFollowRef,FollowMeList,chatRef;
    String currentUserId = "";
    Boolean followCheker = false;
    Boolean FollowMeCheker = false;
    Boolean isInChatList =false ;





    public All_userAdapter(Context context, List<All_UserMemeber> listUser,Boolean isInChatList) {
        this.context = context;
        this.listUser = listUser;
        this.isInChatList = isInChatList;
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
        holder.checkFollow(model.getUid());
        holder.setUser(model.getName(),model.getProf(),model.getUid(),model.getUrl());

        String chatKey ="";
        holder.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        FollowMeList = database.getReference("All Users").child(model.getUid()).child("FollowMeList");
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
        holder.messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra("rName",model.getName());
                intent.putExtra("rUrl",model.getUrl());
                intent.putExtra("rId",model.getUid());
                intent.putExtra("chatKey",getChatKey(currentUserId,model.getUid()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);


            }
        });
    }

    public static String getChatKey(String uid1,String uid2){
        int size = uid1.length()>=uid2.length()?uid1.length():uid2.length();
        for(int i=0 ;i<size;i++){
            if(uid1.charAt(i) == uid2.charAt(i)) continue;
            if(uid1.charAt(i)>uid2.charAt(i)) return uid1+uid2;
            if(uid2.charAt(i)>uid1.charAt(i)) return uid2+uid1;
        }
        return uid1.length()>=uid2.length()?uid1+uid2:uid2+uid1;
    }
    public static Boolean getChatKeyMajorKey(String uid1,String uid2){
        int size = uid1.length()>=uid2.length()?uid1.length():uid2.length();
        for(int i=0 ;i<size;i++){
            if(uid1.charAt(i) == uid2.charAt(i)) continue;
            if(uid1.charAt(i)>uid2.charAt(i)) return true;
            if(uid2.charAt(i)>uid1.charAt(i)) return false;
        }
        return uid1.length()>=uid2.length()?true:false;
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
       ImageButton messageButton;


        public All_userViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name_usercard);
            tv_prof = itemView.findViewById(R.id.tv_prof_usercard);
            imageViewProfile = itemView.findViewById(R.id.iv_imageProfile_usercard);
            addUser = itemView.findViewById(R.id.addFreind_userCard);
            requestString = itemView.findViewById(R.id.tv_relation_usercard);
            messageButton = itemView.findViewById(R.id.messageTo_userCard);
        }


        public  void  setUser(String name, String prof, String uid, String url){
            if(!url.equals("")){
//                Picasso.get().load(url).into(imageViewProfile);
                Glide.with(context).load(url).into(imageViewProfile);
            }
            tv_prof.setText(prof.trim());
            tv_name.setText(name.trim());

            if(isInChatList){
                requestString.setVisibility(View.GONE);
                addUser.setVisibility(View.GONE);
            }else{
                requestString.setVisibility(View.VISIBLE);
                addUser.setVisibility(View.VISIBLE);
            }

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
