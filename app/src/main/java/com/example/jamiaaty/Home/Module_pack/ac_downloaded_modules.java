package com.example.jamiaaty.Home.Module_pack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.jamiaaty.R;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Environment;
import android.view.View;
import java.io.File;
import java.util.ArrayList;

public class ac_downloaded_modules extends AppCompatActivity {
    public static com.example.jamiaaty.Home.Module_pack.ModuleCardAdapter adapter;
    public static ArrayList<com.example.jamiaaty.Home.Module_pack.Module> modules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_downloaded_modules);
        modules=new ArrayList<>();
        File dir = new File(Environment.getExternalStorageDirectory().getPath()+"/Download/jami3aty");
        if(!dir.exists()) return;
        File[] dirs = dir.listFiles();
        for (File inFile : dirs) { //get modules folders
            com.example.jamiaaty.Home.Module_pack.Module m=new com.example.jamiaaty.Home.Module_pack.Module(Environment.getExternalStorageDirectory().getPath()+"/Download/jami3aty/"+inFile.getName(),
                    inFile.getName(),"","Cours, TD, Examens...");
//            Log.i("test",m.key);
//            Toast.makeText(this, " patj module  "+m.key, Toast.LENGTH_LONG).show();
            modules.add(m);
        }
        RecyclerView recyclerView = findViewById(R.id.DownloadsRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new com.example.jamiaaty.Home.Module_pack.ModuleCardAdapter(this,modules,true );
        recyclerView.setAdapter(adapter);
    }
    public void backbtn(View view) {
        finish();
    }
}