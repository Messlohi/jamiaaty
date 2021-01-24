package com.example.jamiaaty;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BottomSheetMen extends BottomSheetDialogFragment implements  View.OnClickListener{


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;
    CardView cv_privacy, cv_logout, cv_delelte;
    FirebaseAuth auth;
    DatabaseReference df;
    String currentUser="";
    String url ="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_menu,null);

        cv_delelte = view.findViewById(R.id.cv_delete);
        cv_logout = view.findViewById(R.id.cv_logout);
        cv_privacy = view.findViewById(R.id.cv_privacy);

        cv_delelte.setOnClickListener(this);
        cv_logout.setOnClickListener(this);
        cv_privacy.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        df = FirebaseDatabase.getInstance().getReference("All Users");
        if(user!= null) {
            currentUser = user.getUid();
            reference = db.collection("user").document(currentUser);
            reference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            try {
                                if(task.getResult().exists()){
                                    url = task.getResult().getString("url");
                                }
                            }catch (Exception e){
                                Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
        return  view ;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case  R.id.cv_logout :
                logout();
                break;

            case  R.id.cv_delete :
                deleteProfile();
                break;
            case  R.id.cv_privacy :
                startActivity(new Intent(getActivity(),PrivacyActivity.class));
                break;

        }

    }

    private  void logout(){
        AlertDialog.Builder   builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Logut").setMessage("Are you sure to logout !")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth.signOut();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }

    private  void deleteProfile(){
        AlertDialog.Builder   builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Profile").setMessage("Are you sure delete the profile !")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(currentUser != null)
                                reference.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Delete the user from the run time db
                                            Query query = df.orderByChild("uid").equalTo(currentUser);
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                        dataSnapshot.getRef().removeValue();
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            //Delete the Profile image from storage
                                            if(!url.isEmpty()){
                                                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                                                ref.delete()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(getContext(),"Profile Succefuly delted",Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }

                                        }
                                    });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }
}
