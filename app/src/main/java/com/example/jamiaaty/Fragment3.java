package com.example.jamiaaty;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jamiaaty.Model.All_UserMemeber;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Fragment3 extends Fragment {

    RecyclerView recyclerView ;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allUserRef,addToContactref;
    String currentUserId = "";
    All_userAdapter adapter;
    List<All_UserMemeber> listeUsers = new ArrayList<>();
    SearchView searchView;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment3,container,false);

        searchView = view.findViewById(R.id.et_search_users);
        recyclerView =view.findViewById(R.id.rv_usercards);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(user != null){
            currentUserId = user.getUid();
        }
        allUserRef = database.getReference("All Users");
        addToContactref = database.getReference("All Users").child(currentUserId).child("FreindList");
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
                adapter = new All_userAdapter(getContext(),listeUsers);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query.toLowerCase());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.trim().equals("")){
                    fetchAllUsers();
                }
                return false;
            }
        });

        return  view;
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
                adapter = new All_userAdapter(getContext(),listeUsers);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private  void searchUsers(String name){
        allUserRef.orderByChild("nameTolower").startAt(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listeUsers.clear();
                for(DataSnapshot item : snapshot.getChildren()){
                    All_UserMemeber memeber = item.getValue(All_UserMemeber.class);
                    if(!currentUserId.equals(memeber.getUid())){
                        listeUsers.add(memeber);
                    }
                }
                adapter = new All_userAdapter(getContext(),listeUsers);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}
