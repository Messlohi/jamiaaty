package com.example.jamiaaty.Home.module_fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jamiaaty.R;
import com.example.jamiaaty.Home.Support_pack.Support;
import com.example.jamiaaty.Home.Support_pack.SupportCardAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;


public class frag_exam extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static SupportCardAdapter adapter;
    public static ArrayList<Support> supports;
    public Boolean isInDownloads=false;
    View root=null;
    String moduleKey;
    public frag_exam() {
        // Required empty public constructor
    }

    public static frag_exam newInstance(String param1, String param2) {
        frag_exam fragment = new frag_exam();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root=inflater.inflate(R.layout.frag_cours, container, false);
        supports=new ArrayList<>();

        moduleKey= getActivity().getIntent().getExtras().getString("Cours_key");
        if(moduleKey.contains("/")){//it s a file path in storage
            isInDownloads=true;
            fetchFilesFromStorage();
        }else {//it s a file key in db
            isInDownloads=false;
            fetchFilesFromDb();
            Log.i("debuuug", "fetchFilesFromDb key:"+moduleKey);
        }

        RecyclerView recyclerView = root.findViewById(R.id.supportRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new SupportCardAdapter(root.getContext(),supports, getActivity().getIntent().getExtras().getString("Cours_name"), isInDownloads,"Examen");
        recyclerView.setAdapter(adapter);
        return root;
    }
    public void fetchFilesFromDb(){
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Modules");
            Query queryRef = myRef.orderByChild("key").equalTo(moduleKey);
            queryRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) { //Log.i("moduleeee","size:  "+ dataSnapshot.child("0").child("name").getValue());
                    supports.clear();
                    for (DataSnapshot elem : dataSnapshot.getChildren()) {
                        Iterator<DataSnapshot> items = elem.child("Examen").getChildren().iterator();
                        while (items.hasNext()) {
                            DataSnapshot item = items.next();
                            Support support=item.getValue(Support.class);
                            supports.add(support);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    Log.i("debuuug", "Failed to read value.", error.toException());
                }
            });
        }catch (Exception e){
            Toast.makeText(root.getContext(), "error "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void fetchFilesFromStorage(){
        try {
            File path = new File(moduleKey+"/Examen");
            if (path.exists()){
                File[] files = path.listFiles();
                for (File file : files) {
                    if (file.isFile()){
                        // Log.i("test",  "  ---->fileName:        -" + file.getName() + "  --  " + file.getTotalSpace() + "\n ");
                        double fileSize=(double) file.length()/1000000;
                        Support s=new Support(file.getName(),file.getName(),moduleKey+"/Examen/"+file.getName(),String.format("%.2f",fileSize));
                        supports.add(s);
                        //Log.i("test",s.Name+" "+s.FileLink+" "+String.format("%.2f",fileSize));
                    }
                }
            }
        }catch (Exception e){
            Toast.makeText(root.getContext(), "error while fitching supports"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}