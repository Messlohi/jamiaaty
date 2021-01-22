package com.example.jamiaaty;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class Fragment2 extends Fragment implements  View.OnClickListener {

    FloatingActionButton fb;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;
    ImageView imageView;
    String currentUser ="";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference,fvrtref,fvrt_listRef;
    RecyclerView recyclerView;
    Boolean fvrtChekcker = false;
    QuestionMember member;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment2,container,false);
        return  view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageView  =getActivity().findViewById(R.id.iv_f2);
        fb = getActivity().findViewById(R.id.floatingActionButton);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = getActivity().findViewById(R.id.rv_f2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        databaseReference = database.getReference("All Questions");
        member = new QuestionMember();


        if(user != null){
            currentUser = user.getUid();
            reference = db.collection("user").document(currentUser);
            //Chekin in if the question is fetured or not !
            fvrtref = database.getReference("favourites_in_poste");
            //Stocking the actual featured question
            fvrt_listRef = database.getReference("favouriteList_user").child(currentUser);
        }else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        fb.setOnClickListener(this);
        imageView.setOnClickListener(this);

        FirebaseRecyclerOptions<QuestionMember> options = new FirebaseRecyclerOptions.Builder<QuestionMember>()
                .setQuery(databaseReference,QuestionMember.class)
                .build();
        FirebaseRecyclerAdapter<QuestionMember,ViewHolder_Question> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<QuestionMember, ViewHolder_Question>(options){
                    @NonNull
                    @Override
                    public ViewHolder_Question onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quesiton_item_row,parent,false);
                        return new ViewHolder_Question(view);

                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolder_Question holder, int position, @NonNull QuestionMember model) {

                        final String postKey = getRef(position).getKey();

                        holder.setItem(getActivity(),model.getName(),model.getUrl(),model.getUserid(),model.getKey(),model.getQuestion(),model.getPrivacy(),model.getTime());


                        String que = getItem(position).getQuestion();
                        String name = getItem(position).getName();
                        String url = getItem(position).getUrl();
                        String time = getItem(position).getTime();
                        String privacy = getItem(position).getPrivacy();
                        String userid = getItem(position).getUserid();

                        holder.replyToPoste.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(),ReplyActivity.class);
                                intent.putExtra("uid",userid);
                                intent.putExtra("q",que);
                                intent.putExtra("postkey",postKey);
                               // intent.putExtra("key",privacy);
                                startActivity(intent);
                            }
                        });

                        holder.favouriteCheker(postKey);
                        holder.fvrt_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // this "fvrtCheker" to not enter in a loop(of onDataChange)
                                fvrtChekcker = true;
                                fvrtref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(fvrtChekcker.equals(true)){
                                            if(snapshot.child(postKey).hasChild(currentUser)){
                                                fvrtref.child(postKey).child(currentUser).removeValue();
                                                delete(time);
                                                fvrtChekcker = false;
                                            }else {
                                                fvrtref.child(postKey).child(currentUser).setValue(true);
                                                member.setName(name);
                                                member.setTime(time);
                                                member.setUserid(userid);
                                                member.setQuestion(que);
                                                member.setPrivacy(privacy);
                                                member.setUrl(url);

                                                fvrt_listRef.child(postKey).setValue(member);
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
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void delete(String time) {
        Query query = fvrt_listRef.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                    Toast.makeText(getActivity(),  "Deleted",Toast.LENGTH_SHORT);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        if(currentUser != ""){
            reference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            try {
                                if(task.getResult().exists()){
                                    String url = task.getResult().getString("url");
                                    Picasso.get().load(url).into(imageView);
                                }
                            }
                            catch (Exception e){
                                Toast.makeText(getActivity(),"Error Data not found !"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.iv_f2 :
                BottomSheetF2 bottomSheetF2 = new BottomSheetF2();
                bottomSheetF2.show(getFragmentManager(),"bottomSheetF2");
                break;
            case  R.id.floatingActionButton :
                Intent intent2 = new Intent(getActivity(),AskActivity.class );
                startActivity(intent2);
                break;

        }

    }
}
