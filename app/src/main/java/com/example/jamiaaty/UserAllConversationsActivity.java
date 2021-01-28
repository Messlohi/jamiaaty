package com.example.jamiaaty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.jamiaaty.Model.All_UserMemeber;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UserAllConversationsActivity extends AppCompatActivity  {


    RecyclerView recyclerView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allUserRef,chatRef;
    String currentUserId = "";
    All_userAdapter adapter;
    List<All_UserMemeber> listeUsers = new ArrayList<>();

   // SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_all_conversations2);

        //searchView = findViewById(R.id.et_search_users);
        recyclerView = findViewById(R.id.rv_allConversations);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if(user != null){
            currentUserId = user.getUid();
        }
        allUserRef = database.getReference("All Users");
        chatRef = database.getReference("chat");
        adapter = new All_userAdapter(getApplication(),listeUsers,true);
        recyclerView.setAdapter(adapter);
        allUserRef.child(currentUserId).child("chatKeys").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listeUsers.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    allUserRef.child(ds.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                All_UserMemeber memeber = snapshot.getValue(All_UserMemeber.class);
                                    listeUsers.add(memeber);
                                    listeUsers = new ArrayList<>(new HashSet<>(listeUsers));
                                    adapter.notifyDataSetChanged();

                                allUserRef.child(currentUserId).child("chatKeys").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if((int)snapshot.getChildrenCount()<listeUsers.size()){
                                            listeUsers.remove(listeUsers.size()-1);
                                        }
                                        adapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }catch (Exception e){

                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    //adapter = new All_userAdapter(getApplication(),listeUsers,true);
                    //recyclerView.setAdapter(adapter);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void fetchAllUsers(){
        allUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listeUsers.clear();
                for(DataSnapshot item : snapshot.getChildren()){
                    All_UserMemeber memeber = item.getValue(All_UserMemeber.class);
                    if(!currentUserId.equals(memeber.getUid())){
                        listeUsers.add(memeber);
                    }
                }
                adapter = new All_userAdapter(getApplication().getApplicationContext(),listeUsers,true);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private  void searchUsers(String name){
        allUserRef.orderByChild("nameTolower").startAt(name).endAt(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listeUsers.clear();
                for(DataSnapshot item : snapshot.getChildren()){
                    All_UserMemeber memeber = item.getValue(All_UserMemeber.class);
                    if(!currentUserId.equals(memeber.getUid())){
                        listeUsers.add(memeber);
                    }
                }
                adapter = new All_userAdapter(getApplication().getApplicationContext(),listeUsers,true);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}