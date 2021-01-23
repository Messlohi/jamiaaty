package com.example.jamiaaty.Home.Module_pack;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jamiaaty.R;

import java.io.File;
import java.util.ArrayList;


public class frag_downloads extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    View root;
    public static com.example.jamiaaty.Home.Module_pack.ModuleCardAdapter adapter;
    public static ArrayList<com.example.jamiaaty.Home.Module_pack.Module> modules;

    public frag_downloads() {
        // Required empty public constructor
    }
    public static frag_downloads newInstance(String param1, String param2) {
        frag_downloads fragment = new frag_downloads();
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
        root= inflater.inflate(R.layout.frag_downloads, container, false);
        modules=new ArrayList<>();
        File dir = new File(Environment.getExternalStorageDirectory().getPath()+"/Download/jami3aty");
        if(dir.exists()) {
            File[] dirs = dir.listFiles();
            for (File inFile : dirs) { //get modules folders
                com.example.jamiaaty.Home.Module_pack.Module m = new com.example.jamiaaty.Home.Module_pack.Module(Environment.getExternalStorageDirectory().getPath() + "/Download/jami3aty/" + inFile.getName(),
                        inFile.getName(), "", "Cours, TD, Examens...");
//            Log.i("test",m.key);
//            Toast.makeText(this, " patj module  "+m.key, Toast.LENGTH_LONG).show();
                modules.add(m);
            }
            RecyclerView recyclerView = root.findViewById(R.id.DownloadsRV);
            recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(), DividerItemDecoration.VERTICAL));
            adapter = new com.example.jamiaaty.Home.Module_pack.ModuleCardAdapter(root.getContext(), modules, true);
            recyclerView.setAdapter(adapter);
        }
        return root;
    }
    public void backbtn(View view) {
        getActivity().finish();
    }
}