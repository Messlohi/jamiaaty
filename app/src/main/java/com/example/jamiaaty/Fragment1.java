package com.example.jamiaaty;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.squareup.picasso.Picasso;

public class Fragment1 extends Fragment implements  View.OnClickListener{
    ImageView imageView;
    TextView nameEt, profEt, bioEt, emailEt,webEt;
    ImageButton imageButtonEdit,imageButtonMenu;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment1,container,false);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageView = getActivity().findViewById(R.id.iv_f1);
        nameEt = getActivity().findViewById(R.id.tv_name_f1);
        profEt = getActivity().findViewById(R.id.tv_prof_f1);
        bioEt = getActivity().findViewById(R.id.tv_bio_f1);
        emailEt = getActivity().findViewById(R.id.tv_email_f1);
        webEt = getActivity().findViewById(R.id.tv_web_f1);


        imageButtonEdit = getActivity().findViewById(R.id.ib_edit_f1);
        imageButtonMenu = getActivity().findViewById(R.id.ib_menu_f1);


       imageButtonMenu.setOnClickListener(this);
        imageButtonEdit.setOnClickListener(this);
        imageView.setOnClickListener(this);
        webEt.setOnClickListener(this);
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
            case R.id.iv_f1 :
                Intent intent1 = new Intent(getActivity(),ImageActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_web_f1 :
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
        String cuurentUser="";
        DocumentReference reference ;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user !=null){
             cuurentUser = user.getUid();
            reference = firestore.collection("user").document(cuurentUser);
            reference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                String nameResult = task.getResult().getString("name");
                                String emailResult = task.getResult().getString("email");
                                String bioResult = task.getResult().getString("bio");
                                String profResult = task.getResult().getString("prof");
                                String uidResult = task.getResult().getString("uid");
                                String webResult = task.getResult().getString("web");
                                String privacyResult = task.getResult().getString("privacy");
                                String urlResult = task.getResult().getString("url");
                                if(!urlResult.equals("")){
//                                    Picasso.get().load(urlResult).into(imageView);
                                    Glide.with(getActivity()).load(urlResult).into(imageView);
                                }
                                nameEt.setText(nameResult);
                                bioEt.setText(bioResult);
                                emailEt.setText(emailResult);
                                webEt.setText(webResult);
                                profEt.setText(profResult);


                            } else {
                                Intent intent = new Intent(getActivity(), CreateProfile.class);
                                startActivity(intent);
                            }
                        }
                    });
        }
    }
}
