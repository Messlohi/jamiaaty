package com.example.jamiaaty.Home.Module_pack;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jamiaaty.R;
import com.example.jamiaaty.Home.localdb.localdb;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class bookmarks_Modules extends AppCompatActivity {
    public static com.example.jamiaaty.Home.Module_pack.ModuleCardAdapter adapter;
    public static ArrayList<com.example.jamiaaty.Home.Module_pack.Module> modules;
    public static ArrayList<com.example.jamiaaty.Home.Module_pack.Module> modulesBookmarked;
    private localdb bookmarkedModulesdb;
    DatabaseReference myRef;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_bookmarks__modules);
        bookmarkedModulesdb=new localdb(this);
//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("Modules");
       // modules=new ArrayList<>();
        modulesBookmarked=bookmarkedModulesdb.getAllModules();
        RecyclerView recyclerView = findViewById(R.id.bookmarkcoursRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        adapter = new com.example.jamiaaty.Home.Module_pack.ModuleCardAdapter(getApplicationContext(),modulesBookmarked );
        recyclerView.setAdapter(adapter);

    }
//    public void getModuleByKey(String moduleKey){
//        try {
//            Query queryRef = myRef.orderByChild("key").equalTo(moduleKey);
//            queryRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) { //Log.i("moduleeee","size:  "+ dataSnapshot.child("0").child("name").getValue());
////                    modules.clear();
//                    for (DataSnapshot elem: dataSnapshot.getChildren()) {
//                        Module module=elem.getValue(Module.class);
//                        modules.add(module);
//                        Log.i("boookmarks","\nsize:  "+ module.name+"\n "+module.description+"\n "+module.key+"\n "+module.imgLink);
//                    }
//                }
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    Log.i("debuuug", "Failed to read value.", error.toException());
//                }
//            });
//        }catch (Exception e){
//            Toast.makeText(this, "error "+e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }

    public void backbtn(View view) {
        finish();
    }
}